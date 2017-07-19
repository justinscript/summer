/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.util;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A flexible date/time parser.
 * <p>
 * Will attempt to interpret whatever text it is given as a date and/or time.
 * <p>
 * Implemented as a DateFormat subclass, so can also be used to format dates as strings.
 * 
 * @author zxc Apr 13, 2013 11:40:19 PM
 */
public class DateTimeParser extends DateFormat {

    private static final long   serialVersionUID    = 9052628666944112454L;
    public static final String  DATEFORMAT_ISO8601  = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'";
    public static final String  DATEFORMAT_YAML     = "yyyy-MM-dd hh:mm:ss";
    private static final String DATEFORMAT_TOSTRING = "EEE MMM dd hh:mm:ss z yyyy";
    private static final int    FORMAT_NONE         = -1;

    protected SimpleDateFormat  outputFormat;
    protected ArrayList<Parser> parsers             = new ArrayList<Parser>();

    /**
     * Interface for parsers
     */
    protected interface Parser {

        public Date parse(String s) throws ParseException;
    }

    /**
     * Basic, flexible, parser implementation
     * <p>
     * A wrapper around DateFormat and SimpleDateFormat classes.
     */
    protected class SimpleParser implements Parser {

        private DateFormat fmt;

        public SimpleParser(String format) {
            fmt = new SimpleDateFormat(format);
        }

        public SimpleParser(DateFormat fmt) {
            this.fmt = fmt;
        }

        public SimpleParser(int dateType, int timeType) {
            if (timeType < 0) {
                this.fmt = DateFormat.getDateInstance(dateType);
            } else if (dateType < 0) {
                this.fmt = DateFormat.getTimeInstance(timeType);
            } else {
                this.fmt = DateFormat.getDateTimeInstance(dateType, timeType);
            }
        }

        public Date parse(String s) throws ParseException {
            return fmt.parse(s);
        }
    }

    public DateTimeParser() {
        super();
        outputFormat = new SimpleDateFormat(DATEFORMAT_ISO8601);

        setupParsers();
    }

    public DateTimeParser(String format) {
        super();
        outputFormat = new SimpleDateFormat(format);

        setupParsers();
    }

    protected void setupParsers() {
        parsers.clear();

        // First, try the format defined when creating the parser
        parsers.add(new SimpleParser(outputFormat));

        // Try the date as a single number, which is the milliseconds since January 1, 1970, 00:00:00 GMT
        parsers.add(new Parser() {

            public Date parse(String s) throws ParseException {
                try {
                    long val = Long.parseLong(s);
                    return new Date(val);
                } catch (NumberFormatException e) {
                    throw new ParseException("Error parsing value", -1);
                }
            }
        });

        parsers.add(new SimpleParser("yyyy-MM-dd"));
        parsers.add(new SimpleParser(DATEFORMAT_TOSTRING));

        // Locale date & time
        parsers.add(new SimpleParser(DateFormat.FULL, DateFormat.FULL));
        parsers.add(new SimpleParser(DateFormat.LONG, DateFormat.LONG));
        parsers.add(new SimpleParser(DateFormat.MEDIUM, DateFormat.MEDIUM));
        parsers.add(new SimpleParser(DateFormat.SHORT, DateFormat.SHORT));

        // Date only
        parsers.add(new SimpleParser(DateFormat.FULL, FORMAT_NONE));
        parsers.add(new SimpleParser(DateFormat.LONG, FORMAT_NONE));
        parsers.add(new SimpleParser(DateFormat.MEDIUM, FORMAT_NONE));
        parsers.add(new SimpleParser(DateFormat.SHORT, FORMAT_NONE));

        // Time only
        parsers.add(new SimpleParser(FORMAT_NONE, DateFormat.FULL));
        parsers.add(new SimpleParser(FORMAT_NONE, DateFormat.LONG));
        parsers.add(new SimpleParser(FORMAT_NONE, DateFormat.MEDIUM));
        parsers.add(new SimpleParser(FORMAT_NONE, DateFormat.SHORT));
    }

    public void addParser(Parser parser) {
        parsers.add(parser);
    }

    @Override
    public Date parse(String text, ParsePosition pos) {
        String s = text.substring(pos.getIndex());
        Date date = null;

        for (Parser parser : parsers) {
            try {
                date = parser.parse(s);
                break;
            } catch (ParseException e) {
                // Ignore parse exceptions.
                // We are going to be trying lots of options, so many of
                // them are going to fail.
            }
        }

        if (date == null) {
            pos.setIndex(pos.getIndex());
            pos.setErrorIndex(pos.getIndex());
        } else {
            pos.setIndex(s.length());
        }

        return date;
    }

    @Override
    public StringBuffer format(Date date, StringBuffer buf, FieldPosition pos) {
        return outputFormat.format(date, buf, pos);
    }
}
