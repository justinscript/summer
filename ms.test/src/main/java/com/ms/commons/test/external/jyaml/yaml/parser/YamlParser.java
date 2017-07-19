/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.yaml.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

import com.ms.commons.test.external.jyaml.org.ho.yaml.Utilities;
import com.ms.commons.test.external.jyaml.org.ho.yaml.tests.TestYamlParserEvent;

/**
 * An experimental Yaml parser.
 * <ul>
 * <li>No compiler-compiler: hand-written code.
 * <li>All functions return boolean if the item exists, false if not. If false, they don't modify anything.
 * <li>Relevant functions send an event thru the ParserEvent object.
 * <li>properties are inespecific (analyze at the next higher level): no branch and leaf properties.
 * <li>Uses a special ParserReader, not a java.io.PushBackReader.
 * </ul>
 * 
 * @license: Open-source compatible TBD (Apache or zlib or Public Domain)
 * @author zxc Apr 14, 2013 12:38:08 AM
 */
@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
public final class YamlParser {

    /* Definition of the YAML events */

    public final static int LIST_OPEN       = '[';
    public final static int LIST_CLOSE      = ']';
    public final static int MAP_OPEN        = '{';
    public final static int MAP_CLOSE       = '}';
    public final static int LIST_NO_OPEN    = 'n';
    public final static int MAP_NO_OPEN     = 'N';
    public final static int DOCUMENT_HEADER = 'H';
    public final static int MAP_SEPARATOR   = ':';
    public final static int LIST_ENTRY      = '-';

    protected ParserReader  r;
    protected int           line            = 1;

    private ParserEvent     event;
    private HashMap         props;
    private char            pendingEvent;

    public YamlParser(Reader r, ParserEvent event) {
        this.r = new ParserReader(r);
        this.event = event;
        props = new HashMap();
    }

    protected String readerString() {
        return r.string();
    }

    private void clearEvents() {
        props.clear();
    }

    private void sendEvents() {
        String s;

        if (pendingEvent == '[') event.event(LIST_OPEN);

        if (pendingEvent == '{') event.event(MAP_OPEN);

        pendingEvent = 0;

        if ((s = (String) props.get("anchor")) != null) event.property("anchor", s);

        if ((s = (String) props.get("transfer")) != null) event.property("transfer", s);

        if ((s = (String) props.get("alias")) != null) event.content("alias", s);

        if (props.keySet().contains("string")) event.content("string", (String) props.get("string"));

        if (props.keySet().contains("value")) event.content("value", (String) props.get("value"));

        props.clear();
    }

    /** how many spaces from index till first non space */

    public int indent() throws IOException, SyntaxException {
        mark();

        int i = 0;
        int ch;
        while (YamlCharacter.is(ch = r.read(), YamlCharacter.INDENT))
            i++;
        if (ch == '\t') throw new SyntaxException("Tabs may not be used for indentation.", line);
        reset();
        return i;
    }

    /** string of characters of type 'type' */

    public boolean array(int type) throws IOException {
        mark();

        int i = 0;

        while (YamlCharacter.is(r.read(), type))
            i++;

        if (i != 0) {
            r.unread();
            unmark();
            return true;
        }

        reset();
        return false;
    }

    /** space = char_space+ */

    public boolean space() throws IOException {
        return array(YamlCharacter.SPACE);
    }

    /**
     * line = char_line+
     * <p>
     * Spaces not included !
     * </p>
     */

    public boolean line() throws IOException {
        return array(YamlCharacter.LINE);
    }

    /**
     * linesp = char_linesp+
     * <p>
     * Spaces included !
     * </p>
     */

    public boolean linesp() throws IOException {
        return array(YamlCharacter.LINESP);
    }

    /** word = char_word+ */

    public boolean word() throws IOException {
        return array(YamlCharacter.WORD);
    }

    /** number = char_digit+ */

    public boolean number() throws IOException {
        return array(YamlCharacter.DIGIT);
    }

    /** indent(n) ::= n*SP */

    public boolean indent(int n) throws IOException {
        mark();

        while (YamlCharacter.is(r.read(), YamlCharacter.INDENT) && n > 0)
            n--;

        if (n == 0) {
            r.unread();
            unmark();
            return true;
        }

        reset();
        return false;
    }

    /** newline ::= ( CR LF) | CR | LF / NEL / PS / LS (| EOF) */

    public boolean newline() throws IOException {
        line++;
        mark();

        int c = r.read();
        int c2 = r.read();

        if (c == -1 || (c == 13 && c2 == 10)) {
            unmark();
            return true;
        }

        if (YamlCharacter.is(c, YamlCharacter.LINEBREAK)) {
            r.unread();
            unmark();
            return true;
        }

        reset();
        line--;
        return false;
    }

    /* end ::= space? newline icomment(-1)* */

    public boolean end() throws IOException, SyntaxException {
        mark();

        space();

        if (!newline()) {
            reset();
            return false;
        }

        while (comment(-1, false))
            ;

        unmark();
        return true;
    }

    /**
     * simple string
     * <p>
     * This function reads also trailing spaces. Trim later
     * </p>
     */

    public boolean string_simple() throws IOException {
        char ch;
        int c;

        int i = 0;
        r.mark();
        boolean dash_first = false;
        while (true) {
            c = r.read();
            if (c == -1) break;

            ch = (char) c;
            if (i == 0 && '-' == ch) {
                dash_first = true;
                continue;
            }
            if (i == 0
                && (YamlCharacter.isSpaceChar(ch) || YamlCharacter.isIndicatorNonSpace(ch) || YamlCharacter.isIndicatorSpace(ch))) break;
            if (dash_first && (YamlCharacter.isSpaceChar(ch) || YamlCharacter.isLineBreakChar(ch))) {
                unmark();
                return false;
            }
            if (!YamlCharacter.isLineSpChar(ch) || (YamlCharacter.isIndicatorSimple(ch) && r.previous() != '\\')) break;
            i++;
        }

        r.unread();
        r.unmark();
        if (i != 0) return true;

        return false;
    }

    public boolean loose_string_simple() throws IOException {
        char ch = 0;
        int c;

        int i = 0;

        while (true) {
            c = r.read();
            if (c == -1) break;

            ch = (char) c;
            // if (i == 0 && (YamlCharacter.isSpaceChar(ch) || YamlCharacter.isIndicatorNonSpace(ch) ||
            // YamlCharacter.isIndicatorSpace(ch) ) )
            // break;

            if (!YamlCharacter.isLineSpChar(ch) || (YamlCharacter.isLooseIndicatorSimple(ch) && r.previous() != '\\')) break;
            i++;
        }

        r.unread();
        if (i == 0) {
            if (YamlCharacter.isLineBreakChar(ch)) return true;
            else return false;
        }
        return true;
    }

    /** single quoted string (ends with ''' not preceded by esc) */

    boolean string_q1() throws IOException, SyntaxException {
        if (r.current() != '\'') return false;

        r.read();
        int c = 0;
        int i = 0;

        while (YamlCharacter.is(c = r.read(), YamlCharacter.LINESP)) {
            if (c == '\'' && r.previous() != '\\') break;
            i++;
        }

        if (c != '\'') throw new SyntaxException("Unterminated string", line);

        // if (i != 0)
        return true;

        // return false;
    }

    /** double quoted string (ends with '"' not preceded by esc) */

    boolean string_q2() throws IOException, SyntaxException {

        if (r.current() != '"') return false;

        r.read();
        int c = 0;
        int i = 0;

        while (YamlCharacter.is(c = r.read(), YamlCharacter.LINESP)) {
            if (c == '"' && r.previous() != '\\') break;
            i++;
        }

        if (c != '"') throw new SyntaxException("Unterminated string", line);

        // if (i != 0)
        return true;

        // return false;
    }

    /**
     * string ::= single_quoted | double_quoted | simple
     * <p>
     * All strings are trimmed
     * </p>
     */
    public boolean loose_string() throws IOException, SyntaxException {
        mark();
        boolean q2 = false;
        boolean q1 = false;
        if ((q1 = string_q1()) || (q2 = string_q2()) || loose_string_simple()) {
            String str = r.string().trim();
            if (q2) str = fix_q2(str);
            else if (q1) str = fix_q1(str);
            if (q1 || q2) props.put("string", str);
            else if ("".equals(str)) props.put("value", null);
            else props.put("value", str);
            unmark();
            return true;
        }

        reset();
        return false;
    }

    public boolean string() throws IOException, SyntaxException {
        mark();
        boolean q2 = false;
        boolean q1 = false;
        if ((q1 = string_q1()) || (q2 = string_q2()) || string_simple()) {
            String str = r.string().trim();
            if (q2) str = fix_q2(str);
            else if (q1) str = fix_q1(str);
            if (q1 || q2) props.put("string", str);
            else props.put("value", str);
            unmark();
            return true;
        }

        reset();
        return false;
    }

    String fix_q2(String str) {
        if (str.length() > 2) return Utilities.unescape(str.substring(1, str.length() - 1));
        else return "";
    }

    String fix_q1(String str) {
        if (str.length() > 2) return str.substring(1, str.length() - 1);
        else return "";
    }

    /** alias ::= '*' word */

    public boolean alias() throws IOException {
        mark();

        if (r.read() != '*') {
            r.unread();
            unmark();
            return false;
        }

        if (!word()) {
            reset();
            return false;
        }

        unmark();
        props.put("alias", r.string());
        return true;
    }

    /** anchor ::= '&' word */

    public boolean anchor() throws IOException {
        mark();

        if (r.read() != '&') {
            r.unread();
            unmark();
            return false;
        }

        if (!word()) {
            reset();
            return false;
        }

        unmark();
        props.put("anchor", r.string());
        return true;
    }

    /**
     * throwable comment productions. icomment(n) ::= indent(&lt;n) ('#' linesp )? newline<br>
     * xcomment(n) ::= indent(&lt;n) '#' linesp newline<br>
     * <br>
     * icomment(n) ::= comment(n,false)<br>
     * xcomment(n) ::= comment(n,true)
     */

    public boolean comment(int n, boolean explicit) throws IOException, SyntaxException {
        mark();

        if (n != -1 && indent() >= n) {
            reset();
            return false;
        }

        space();

        int c;
        if ((c = r.read()) == '#') linesp();
        else {
            if (c == -1) {
                unmark();
                return false;
            }

            if (explicit) {
                reset();
                return false;
            } else r.unread();
        }

        boolean b = newline();

        if (b == false) {
            reset();
            return false;
        }

        unmark();
        return true;
    }

    /** header ::= '---' (space directive)* */

    public boolean header() throws IOException {
        mark();

        int c = r.read();
        int c2 = r.read();
        int c3 = r.read();

        if (c != '-' || c2 != '-' || c3 != '-') {
            reset();
            return false;
        }

        while (space() && directive())
            ;

        unmark();
        event.event(DOCUMENT_HEADER);
        return true;
    }

    /** directive ::= '#' word ':' line */

    public boolean directive() throws IOException {
        mark();

        if (r.read() != '#') {
            r.unread();
            unmark();
            return false;
        }

        if (!word()) {
            reset();
            return false;
        }

        if (r.read() != ':') {
            reset();
            return false;
        }

        if (!line()) {
            reset();
            return false;
        }

        event.content("directive", r.string());
        unmark();
        return true;
    }

    /** transfer ::= '!' line */

    public boolean transfer() throws IOException {
        mark();

        if (r.read() != '!') {
            r.unread();
            unmark();
            return false;
        }

        if (!line()) {
            reset();
            return false;
        }
        props.put("transfer", r.string());
        unmark();

        return true;
    }

    /** properties ::= ( transfer ( space anchor )? ) | ( anchor ( space transfer )? */

    public boolean properties() throws IOException {
        mark();

        if (transfer()) {
            space();
            anchor();
            unmark();
            return true;
        }

        if (anchor()) {
            space();
            transfer();
            unmark();
            return true;
        }

        reset();
        return false;
    }

    /** key ::= ( '?' value_nested(&gt;n) indent(n) ) | ( value_inline space? ) */

    public boolean key(int n) throws IOException, SyntaxException {
        if (r.current() == '?') {
            r.read();
            if (!value_nested(n + 1)) throw new SyntaxException("'?' key indicator without a nested value", line);
            if (!indent(n)) throw new SyntaxException("Incorrect indentations after nested key", line);
            return true;
        }

        if (!value_inline()) return false;

        space();
        return true;
    }

    /* value ::= (value_inline end) | value_block(n) | value_nested(n) */

    public boolean value(int n) throws IOException, SyntaxException {

        // System.out.p.frameworkn("value(n)");

        if (value_nested(n) || value_block(n)) return true;

        if (!loose_value_inline()) return false;
        // System.out.p.frameworkn("value(n) is inline");
        if (!end()) throw new SyntaxException("Unterminated inline value", line);
        return true;
    }

    public boolean loose_value(int n) throws IOException, SyntaxException {

        // System.out.p.frameworkn("value(n)");

        if (value_nested(n) || value_block(n)) return true;

        if (!loose_value_inline()) return false;
        // System.out.p.frameworkn("value(n) is inline");
        if (!end()) throw new SyntaxException("Unterminated inline value", line);
        return true;
    }

    public boolean value_na(int n) throws IOException, SyntaxException {
        if (value_nested(n) || value_block(n)) return true;

        if (!value_inline_na()) return false;

        if (!end()) throw new SyntaxException("Unterminated inline value", line);

        return true;
    }

    public boolean value_inline() throws IOException, SyntaxException {
        mark();

        if (properties()) space();

        if (alias() || string()) {
            sendEvents();
            unmark();
            return true;
        }

        if (list() || map()) {
            unmark();
            return true;
        }

        clearEvents();
        reset();
        return false;
    }

    public boolean loose_value_inline() throws IOException, SyntaxException {
        mark();

        if (properties()) space();

        if (alias() || loose_string()) {
            sendEvents();
            unmark();
            return true;
        }

        if (list() || map()) {
            unmark();
            return true;
        }

        clearEvents();
        reset();
        return false;
    }

    public boolean value_inline_na() throws IOException, SyntaxException {
        mark();

        if (properties()) space();

        if (string()) {
            sendEvents();
            unmark();
            return true;
        }

        if (list() || map()) {
            unmark();
            return true;
        }

        clearEvents();
        reset();
        return false;
    }

    public boolean value_nested(int n) throws IOException, SyntaxException {
        mark();
        // System.out.p.frameworkn("----------------------- 0");
        if (properties()) space();
        // System.out.p.frameworkn("----------------------- 1");
        if (!end()) {
            clearEvents();
            reset();
            return false;
        }
        // System.out.p.frameworkn("----------------------- 2");
        sendEvents();

        while (comment(n, false))
            ;
        // System.out.p.frameworkn("----------------------- 3");
        if (nlist(n) || nmap(n)) {
            unmark();
            return true;
        }

        reset();
        return false;
    }

    public boolean value_block(int n) throws IOException, SyntaxException {
        mark();

        if (properties()) space();

        if (!block(n)) {
            clearEvents();
            reset();
            return false;
        }

        sendEvents();

        while (comment(n, false))
            ;

        unmark();
        return true;
    }

    /** nmap(n) ::= (indent(n) nmap_entry(n))+ */

    public boolean nmap(int n) throws IOException, SyntaxException {
        mark();
        // System.out.p.frameworkn("----------------------- 10");
        int in = indent();

        if (n == -1) n = in;
        else if (in > n) n = in;

        pendingEvent = '{';

        int i = 0;
        while (true) {
            if (!indent(n)) break;
            if (!nmap_entry(n)) break;
            i++;
        }
        // System.out.p.frameworkn("----------------------- 11");
        if (i > 0) {
            event.event(MAP_CLOSE);
            unmark();
            return true;
        }
        // System.out.p.frameworkn("----------------------- 12");
        pendingEvent = 0;
        reset();
        return false;
    }

    /** nmap_entry(n) ::= key(n) ':' value(&gt;n) */

    public boolean nmap_entry(int n) throws IOException, SyntaxException {
        if (!key(n)) return false;
        if (r.current() != ':') return false;
        r.read();

        event.event(MAP_SEPARATOR);
        space(); /* enforce this space? */

        if (!loose_value(n + 1)) throw new SyntaxException("no value after ':'", line);

        return true;
    }

    /** nlist(n) ::= ( indent(n) nlist_entry(n) )+ */

    public boolean nlist(int n) throws IOException, SyntaxException {
        mark();

        int in = indent();

        if (n == -1) n = in;
        else if (in > n) n = in;

        pendingEvent = '[';

        int i = 0;
        while (true) {
            if (!indent(n)) break;
            if (!nlist_entry(n)) break;
            i++;
        }

        if (i > 0) {
            event.event(LIST_CLOSE);
            unmark();
            return true;
        }

        pendingEvent = 0;
        reset();
        return false;
    }

    boolean start_list() throws IOException {
        r.mark();
        if (r.read() == '-') {
            if (YamlCharacter.isLineBreakChar((char) r.current()) || space()) {
                r.unmark();
                return true;
            }
        }
        r.reset();
        return false;

    }

    /** nlist_entry(n) ::= '-' ( value(&gt;n) | nmap_inlist(&gt;n) ) */

    public boolean nlist_entry(int n) throws IOException, SyntaxException {
        if (!start_list()) return false;

        // System.out.p.frameworkn("nlist_entry");
        space();
        // if (!space())
        // throw new SyntaxException("No space after nested list entry",line);

        if (nmap_inlist(n + 1) || value(n + 1)) {
            return true;
        }

        throw new SyntaxException("bad nlist", line);
    }

    /** nmap_inlist(n) ::= space string space? ':' space value(&gt;n) nmap(&gt;n)? XXX */

    public boolean nmap_inlist(int n) throws IOException, SyntaxException {
        mark();

        // System.out.p.frameworkn("nmap_inlist()-1");
        if (!string()) {
            reset();
            return false;
        }

        // System.out.p.frameworkn("nmap_inlist()-2");
        space();
        // System.out.p.frameworkn("nmap_inlist()-3");
        if (r.read() != ':') {
            reset();
            return false;
        }
        if (pendingEvent == '[') {
            event.event(LIST_OPEN);
            pendingEvent = 0;
        }
        event.event(MAP_OPEN);
        sendEvents();
        event.event(MAP_SEPARATOR);
        // System.out.p.frameworkn("nmap_inlist()-4");
        if (!space()) {
            reset();
            return false;
        }
        // System.out.p.frameworkn("nmap_inlist()-5");
        if (!value(n + 1)) throw new SyntaxException("No value after ':' in map_in_list", line);
        // System.out.p.frameworkn("nmap_inlist()-6");

        n = n + 1;
        int in = indent();

        if (n == -1) n = in;
        else if (in > n) n = in;

        int i = 0;
        while (true) {
            if (!indent(n)) break;
            if (!nmap_entry(n)) break;
            i++;
        }
        event.event(MAP_CLOSE);
        unmark();
        return true;

    }

    /** block(n) ::= '|' space? number? space? newline block_line(n)* */

    public boolean block(int n) throws IOException, SyntaxException {
        int c = r.current();
        if (c != '|' && c != ']' && c != '>') return false;

        r.read();
        if (r.current() == '\\') r.read();

        space();
        if (number()) space();

        if (!newline()) throw new SyntaxException("No newline after block definition", line);

        StringBuffer sb = new StringBuffer();
        int block_indent = block_line(n, -1, sb, (char) c);
        while (-1 != block_line(n, block_indent, sb, (char) c))
            ;
        String blockString = sb.toString();
        if (blockString.length() > 0 && YamlCharacter.isLineBreakChar(blockString.charAt(blockString.length() - 1))) blockString = blockString.substring(0,
                                                                                                                                                         blockString.length() - 1);
        event.content("string", blockString);

        return true;
    }

    /* block_line(n) ::= indent(n) linesp? newline */

    public int block_line(int n, int block_indent, StringBuffer sb, char ch) throws IOException, SyntaxException {
        int in = 0;
        if (block_indent == -1) {
            in = indent();
            if (in < n) return -1;
            n = in;
            indent(n);
        } else {
            in = block_indent;
            if (!indent(block_indent)) return -1;
        }

        if (r.current() == -1) return -1;

        mark();

        linesp();
        sb.append(r.string());

        unmark();

        if (ch == '|') sb.append('\n');
        else sb.append(' ');

        newline();
        return in;
    }

    /** list ::= '[' (list_entry ',')* list_entry? ']' */

    public boolean list() throws IOException, SyntaxException {
        if (r.current() != '[') return false;
        r.read();
        sendEvents();
        event.event(LIST_OPEN);

        while (list_entry()) {
            int c = r.current();
            if (c == ']') {
                r.read();
                event.event(LIST_CLOSE);
                return true;
            }
            if (c != ',') throw new SyntaxException("inline list error: expecting ','", line);
            r.read();
        }
        int c = r.current();
        if (c == ']') {
            r.read();
            event.event(LIST_CLOSE);
            return true;
        } else throw new SyntaxException("inline list error", line);
    }

    /** list_entry ::= space? value_inline space? */

    public boolean list_entry() throws IOException, SyntaxException {
        space();

        if (!loose_value_inline()) return false;

        space();
        return true;
    }

    /** map ::= '{' (map_entry ',')* map_entry? '}' */

    public boolean map() throws IOException, SyntaxException {
        if (r.current() != '{') return false;
        r.read();
        sendEvents();
        event.event(MAP_OPEN);

        while (map_entry()) {
            int c = r.current();
            if (c == '}') {
                r.read();
                event.event(MAP_CLOSE);
                return true;
            }
            if (c != ',') throw new SyntaxException("inline map error: expecting ','", line);
            r.read();
        }
        int c = r.current();
        if (c == '}') {
            r.read();
            event.event(MAP_CLOSE);
            return true;
        }
        throw new SyntaxException("inline map error", line);
    }

    /** map_entry ::= space? value_inline space? ':' space value_inline space? */

    public boolean map_entry() throws IOException, SyntaxException {
        space();

        if (!value_inline()) return false;

        space();

        if (r.current() != ':') return false;

        r.read();

        event.event(MAP_SEPARATOR);

        if (!space()) throw new SyntaxException("No space after ':'", line);

        if (!loose_value_inline()) throw new SyntaxException("No value after ':'", line);

        space();
        return true;
    }

    /** document_first ::= nlist(-1) | nmap(-1) */

    public boolean document_first() throws IOException, SyntaxException {
        boolean b = nlist(-1) || nmap(-1);
        mark();

        if (!header() && r.read() != YamlCharacter.EOF && r.read() != YamlCharacter.EOF) // Hacky hack here, want to
                                                                                         // call read() twice
                                                                                         // which changes the reader's
                                                                                         // state
        throw new SyntaxException("End of document expected.");
        unmark();
        // if (!value_na(-1)) throw new SyntaxException("first document is not a nested list or map",line);
        if (!b) throw new SyntaxException("first document is not a nested list or map", line);
        return true;
    }

    /** document_next ::= header node_non_alias(-1) */

    public boolean document_next() throws IOException, SyntaxException {
        if (!header()) return false;
        // throw new SyntaxException("Expected beginning of document",line);
        if (!value_na(-1)) return false;
        return true;
    }

    /** parse ::= icomment(-1)* document_first? document_next* */

    public void parse() throws IOException, SyntaxException {
        try {
            while (comment(-1, false))
                ;

            if (!header()) document_first();
            else value_na(-1);

            while (document_next())
                ;
        } catch (SyntaxException e) {
            event.error(e, e.line);
        }
    }

    /* ----------------------------------------------------- */

    private void mark() {
        r.mark();
    }

    private void reset() {
        r.reset();
    }

    private void unmark() {
        r.unmark();
    }

    /**
     * @return Returns the event.
     */
    public ParserEvent getEvent() {
        return event;
    }

    /**
     * @param event The event to set.
     */
    public void setEvent(ParserEvent event) {
        this.event = event;
    }

    public int getLineNumber() {
        return line;
    }

    public static void parse(File file) throws FileNotFoundException {
        parse(new FileReader(file));
    }

    public static void parse(String yamlText) {
        parse(new StringReader(yamlText));
    }

    public static void parse(Reader reader) {
        try {
            ParserEvent handler = new TestYamlParserEvent();
            YamlParser y = new YamlParser(reader, handler);
            y.parse();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
