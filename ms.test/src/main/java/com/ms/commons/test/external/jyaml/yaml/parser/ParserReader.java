/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.yaml.parser;

import java.io.IOException;
import java.io.Reader;

/**
 * A character Reader with some additional functionality.<br>
 * <ul>
 * <li>nested mark-unmark-reset
 * <li>current() returns next character that will be read, without consuming it
 * <li>string() returns a string since the last mark
 * <li>previous() returns the character before the last one readed
 * </ul>
 * <p>
 * This implementation uses a circular buffer with a default size of 64k. To create a ParserReader from a Reader do:
 * </p>
 * <p>
 * ParserReader parserReader = ParserReader(reader);
 * </p>
 * <p>
 * The constructor accepts any reader and returns a new reader with the functions defined here. As of now, this class is
 * not an extension of Reader.
 * </p>
 * <p>
 * Care has to be taken when using mark(), unmark() and reset(), since they can be nested. Each mark() call has to be
 * matched by exactly one unmark() or reset(). This code is now legal:
 * <p>
 * 
 * <pre>
 * ParserReader r = new ParserReader(reader);
 * 
 * mark(); // position 1
 * mark(); // position 2
 * reset(); // return to position 2
 * if (condition) reset(); // return to position 1
 * else unmark();
 * </pre>
 * 
 * @license: Open-source compatible TBD (Apache or zlib or Public Domain)
 * @author zxc Apr 14, 2013 12:39:12 AM
 */
public final class ParserReader {

    Reader    reader;
    int       c;
    char[]    buffer;         /* used for mark(), unmark() and reset() operations */
    int       index, fileIndex, level;
    int       eofIndex;               /* where in buffer[] is the eof */
    int[]     mark;
    final int BUFLEN = 120000;        /* this constant determines how much lookahead we can have */

    public ParserReader(Reader reader) {
        this.reader = reader;
        buffer = new char[BUFLEN]; // maximum mark-reset range. Circular buffer !
        buffer[buffer.length - 1] = 0; // correct response from previous() after start.
        index = 0;
        fileIndex = 0;
        level = 0;
        eofIndex = -1;
        mark = new int[1000];
    }

    /** return a string begining at the last mark() untill the current position */

    public String string() {
        int begin = mark[level - 1];
        int end = index;

        if (begin > end) return new String(buffer, begin, BUFLEN - begin) + new String(buffer, 0, end);
        else return new String(buffer, begin, end - begin);
    }

    /**
     * read and return one character from the stream.
     * <ul>
     * <li>If index != fileIndex, read from buffer
     * <li>else check if eof has been readed. If so return eof
     * <li>else read a new character from the stream
     * </ul>
     */

    public int read() throws IOException {
        if (index == eofIndex) {
            index++;
            return -1;
        } else if (index != (fileIndex % BUFLEN)) // assuming index < fileIndex
        c = (int) buffer[index];
        else { // assuming index == fileIndex
            if (eofIndex != -1) return -1;

            c = reader.read();
            fileIndex++;
            if (c == -1) eofIndex = index;
            buffer[index] = (char) c;
        }
        index++;
        if (index >= BUFLEN) index = 0;
        return c;
    }

    /** return one character from the stream without 'consuming' it */

    public int current() throws IOException {
        read();
        unread();
        return c;
    }

    /** return the previous character */

    public int previous() {
        if (index == 0) return (int) buffer[BUFLEN - 2];
        else if (index == 1) return (int) buffer[BUFLEN - 1];
        else return (int) buffer[index - 2];
    }

    /** remember the current position for a future reset() */

    public void mark() {
        mark[level] = index;
        level++;
    }

    public void unmark() {
        level--;
        if (level < 0) throw new IndexOutOfBoundsException("no more mark()'s to unmark()");
    }

    /** return to the position of a previous mark(). */

    public void reset() {
        unmark();
        index = mark[level];
    }

    /** unread one character. */

    public void unread() {
        index--;
        // if (index == mark[level-1])
        // throw new IndexOutOfBoundsException("too much unreads");
        if (index < 0) index = BUFLEN - 1;
    }
}
