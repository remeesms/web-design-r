/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package org.zkoss.poi.ss.format;

import org.zkoss.poi.hssf.util.HSSFColor;
import org.zkoss.poi.ss.usermodel.ZssContext;
import org.zkoss.util.Locales;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.zkoss.poi.ss.format.CellFormatter.logger;
import static org.zkoss.poi.ss.format.CellFormatter.quote;

/**
 * Objects of this class represent a single part of a cell format expression.
 * Each cell can have up to four of these for positive, zero, negative, and text
 * values.
 * <p/>
 * Each format part can contain a color, a condition, and will always contain a
 * format specification.  For example <tt>"[Red][>=10]#"</tt> has a color
 * (<tt>[Red]</tt>), a condition (<tt>>=10</tt>) and a format specification
 * (<tt>#</tt>).
 * <p/>
 * This class also contains patterns for matching the subparts of format
 * specification.  These are used internally, but are made public in case other
 * code has use for them.
 *
 * @author Ken Arnold, Industrious Media LLC
 */
public class CellFormatPart {
	private final Locale locale;
    private final Color color;
    private CellFormatCondition condition;
    private final CellFormatter format;
    private final CellFormatType type;

    private static final Map<String, Color> NAMED_COLORS;

    static {
        NAMED_COLORS = new TreeMap<String, Color>(
                String.CASE_INSENSITIVE_ORDER);

        Map<Integer,HSSFColor> colors = HSSFColor.getIndexHash();
        for (HSSFColor color : colors.values()) {
            Class<? extends HSSFColor> type = color.getClass();
            String name = type.getSimpleName();
            if (name.equals(name.toUpperCase())) {
                short[] rgb = color.getTriplet();
                Color c = new Color(rgb[0], rgb[1], rgb[2]);
                NAMED_COLORS.put(name, c);
                if (name.indexOf('_') > 0)
                    NAMED_COLORS.put(name.replace('_', ' '), c);
                if (name.indexOf("_PERCENT") > 0)
                    NAMED_COLORS.put(name.replace("_PERCENT", "%").replace('_',
                            ' '), c);
            }
        }
    }

    /** Pattern for the color part of a cell format part. */
    public static final Pattern COLOR_PAT;
    /** Pattern for the condition part of a cell format part. */
    public static final Pattern CONDITION_PAT;
    /** Pattern for the format specification part of a cell format part. */
    public static final Pattern SPECIFICATION_PAT;
    /** Pattern for an entire cell single part. */
    public static final Pattern FORMAT_PAT;
    /** Pattern for an i18n part of a cell format. */
    public static final Pattern LOCALE_PAT; //20100615, Henri Chen

    /** Within {@link #FORMAT_PAT}, the group number for the matched locale code. */
    public static final int LOCALE_GROUP;
    
    /** Within {@link #FORMAT_PAT}, the group number for the matched color. */
    public static final int COLOR_GROUP;
    /**
     * Within {@link #FORMAT_PAT}, the group number for the operator in the
     * condition.
     */
    public static final int CONDITION_OPERATOR_GROUP;
    /**
     * Within {@link #FORMAT_PAT}, the group number for the value in the
     * condition.
     */
    public static final int CONDITION_VALUE_GROUP;
    /**
     * Within {@link #FORMAT_PAT}, the group number for the format
     * specification.
     */
    public static final int SPECIFICATION_GROUP;

    static {
    	// A locale specification
    	String locale = "\\[\\$\\-([0-9A-F]{2,8})\\]";
        // A condition specification
        String condition = "([<>=]=?|!=|<>)    # The operator\n" +
                "  \\s*([0-9]+(?:\\.[0-9]*)?)\\s*  # The constant to test against\n";

        String color =
                "\\[(black|blue|cyan|green|magenta|red|white|yellow|color [0-9]+)\\]";

        // A number specification
        // Note: careful that in something like ##, that the trailing comma is not caught up in the integer part

        // A part of a specification
        String part = "\\\\.                 # Quoted single character\n" +
                "|\"([^\\\\\"]|\\\\.)*\"         # Quoted string of characters (handles escaped quotes like \\\") \n" +
                "|_.                             # Space as wide as a given character\n" +
                "|\\*.                           # Repeating fill character\n" +
                "|@                              # Text: cell text\n" +
                "|([0?\\#](?:[0?\\#,]*))         # Number: digit + other digits and commas\n" +
                "|e[-+]                          # Number: Scientific: Exponent\n" +
                "|m{1,5}                         # Date: month or minute spec\n" +
                "|d{1,4}                         # Date: day/date spec\n" +
                "|y{2,4}                         # Date: year spec\n" +
                "|h{1,2}                         # Date: hour spec\n" +
                "|s{1,2}                         # Date: second spec\n" +
                "|am?/pm?                        # Date: am/pm spec\n" +
                "|\\[h{1,2}\\]                   # Elapsed time: hour spec\n" +
                "|\\[m{1,2}\\]                   # Elapsed time: minute spec\n" +
                "|\\[s{1,2}\\]                   # Elapsed time: second spec\n" +
                "|[^;]                           # A character\n" + "";

        String format = "(?:" + locale + ")?     # locale code\n" + 
        		"(?:" + color + ")?              # Text color\n" +
                "(?:\\[" + condition + "\\])?    # Condition\n" +
                "((?:" + part + ")+)             # Format spec\n";

        int flags = Pattern.COMMENTS | Pattern.CASE_INSENSITIVE;
        LOCALE_PAT = Pattern.compile(locale, flags); //20100615, Henri Chen
        COLOR_PAT = Pattern.compile(color, flags);
        CONDITION_PAT = Pattern.compile(condition, flags);
        SPECIFICATION_PAT = Pattern.compile(part, flags);
        FORMAT_PAT = Pattern.compile(format, flags);

        // Calculate the group numbers of important groups.  (They shift around
        // when the pattern is changed; this way we figure out the numbers by
        // experimentation.)

        LOCALE_GROUP = findGroup(FORMAT_PAT, "[$-409]@", "409"); //20100615, Henri Chen
        COLOR_GROUP = findGroup(FORMAT_PAT, "[Blue]@", "Blue");
        CONDITION_OPERATOR_GROUP = findGroup(FORMAT_PAT, "[>=1]@", ">=");
        CONDITION_VALUE_GROUP = findGroup(FORMAT_PAT, "[>=1]@", "1");
        SPECIFICATION_GROUP = findGroup(FORMAT_PAT, "[Blue][>1]\\a ?", "\\a ?");
    }

    interface PartHandler {
        String handlePart(Matcher m, String part, CellFormatType type,
                StringBuffer desc);
    }

    /**
     * Create an object to represent a format part.
     *
     * @param desc The string to parse.
     */
    public CellFormatPart(String desc) {
        Matcher m = FORMAT_PAT.matcher(desc);
        if (!m.matches()) {
            throw new IllegalArgumentException("Unrecognized format: " + quote(
                    desc));
        }
        Locale locale0 = getLocale(m); //20100616, Henri Chen
        locale = locale0;
        color = getColor(m);
        condition = getCondition(m);
        type = getCellFormatType(m);
        format = getFormatter(m);
    }

    /**
     * Returns <tt>true</tt> if this format part applies to the given value. If
     * the value is a number and this is part has a condition, returns
     * <tt>true</tt> only if the number passes the condition.  Otherwise, this
     * always return <tt>true</tt>.
     *
     * @param valueObject The value to evaluate.
     *
     * @return <tt>true</tt> if this format part applies to the given value.
     */
    public boolean applies(Object valueObject) {
        if (condition == null || !(valueObject instanceof Number)) {
            if (valueObject == null)
                throw new NullPointerException("valueObject");
            return true;
        } else {
            Number num = (Number) valueObject;
            return condition.pass(num.doubleValue());
        }
    }

    /**
     * Returns the number of the first group that is the same as the marker
     * string.  The search starts with group 1.
     *
     * @param pat    The pattern to use.
     * @param str    The string to match against the pattern.
     * @param marker The marker value to find the group of.
     *
     * @return The matching group number.
     *
     * @throws IllegalArgumentException No group matches the marker.
     */
    private static int findGroup(Pattern pat, String str, String marker) {
        Matcher m = pat.matcher(str);
        if (!m.find())
            throw new IllegalArgumentException(
                    "Pattern \"" + pat.pattern() + "\" doesn't match \"" + str +
                            "\"");
        for (int i = 1; i <= m.groupCount(); i++) {
            String grp = m.group(i);
            if (grp != null && grp.equals(marker))
                return i;
        }
        throw new IllegalArgumentException(
                "\"" + marker + "\" not found in \"" + pat.pattern() + "\"");
    }

    //20100616, Henri Chen
    /**
     * Returns the locale specification from the matcher, or <tt>null</tt> if
     * there is none.
     * @param m The matcher for the format part.
     * @return The locale specification of <tt>null</tt>.
     */
    private static Locale getLocale(Matcher m) {
    	String ldesc = m.group(LOCALE_GROUP);
   		logger.finer("format locale: "+ldesc);
    	Locale l = getLocale(ldesc);
    	if (l == null)
    		logger.warning("Unknown locale: " + quote(ldesc));
    	return l;
    }
    
    private static Locale getLocale(String ldesc) {
    	//ZSS-68
    	//TODO based on ldesc to return the locale; e.g. [$-409] => en_US
    	//currently, we always return current locale no matter the format
    	//return Locale.US;
        return ZssContext.getCurrent().getLocale();
    }
    /**
     * Returns the color specification from the matcher, or <tt>null</tt> if
     * there is none.
     *
     * @param m The matcher for the format part.
     *
     * @return The color specification or <tt>null</tt>.
     */
    private static Color getColor(Matcher m) {
        String cdesc = m.group(COLOR_GROUP);
        if (cdesc == null || cdesc.length() == 0)
            return null;
        Color c = NAMED_COLORS.get(cdesc);
        if (c == null)
            logger.warning("Unknown color: " + quote(cdesc));
        return c;
    }

    /**
     * Returns the condition specification from the matcher, or <tt>null</tt> if
     * there is none.
     *
     * @param m The matcher for the format part.
     *
     * @return The condition specification or <tt>null</tt>.
     */
    private CellFormatCondition getCondition(Matcher m) {
        String mdesc = m.group(CONDITION_OPERATOR_GROUP);
        if (mdesc == null || mdesc.length() == 0)
            return null;
        return CellFormatCondition.getInstance(m.group(
                CONDITION_OPERATOR_GROUP), m.group(CONDITION_VALUE_GROUP));
    }

    /**
     * Returns the CellFormatType object implied by the format specification for
     * the format part.
     *
     * @param matcher The matcher for the format part.
     *
     * @return The CellFormatType.
     */
    private CellFormatType getCellFormatType(Matcher matcher) {
        String fdesc = matcher.group(SPECIFICATION_GROUP);
        return formatType(fdesc);
    }

    /**
     * Returns the formatter object implied by the format specification for the
     * format part.
     *
     * @param matcher The matcher for the format part.
     *
     * @return The formatter.
     */
    private CellFormatter getFormatter(Matcher matcher) {
        String fdesc = matcher.group(SPECIFICATION_GROUP);
//        type = formatType(fdesc);
        return type.formatter(fdesc, locale); //ZSS-68
//        return type.formatter(fdesc);
    }
 
    /**
     * Returns the type of format.
     *
     * @param fdesc The format specification
     *
     * @return The type of format.
     */
    private CellFormatType formatType(String fdesc) {
        fdesc = fdesc.trim();
        if (fdesc.equals("") || fdesc.equalsIgnoreCase("General"))
            return CellFormatType.GENERAL;

        Matcher m = SPECIFICATION_PAT.matcher(fdesc);
        boolean couldBeDate = false;
        boolean seenZero = false;
        boolean couldBeElasped = false;
        while (m.find()) {
            String repl = m.group(0);
            if (repl.length() > 0) {
                switch (repl.charAt(0)) {
                case '@':
                    return CellFormatType.TEXT;
                case 'd':
                case 'D':
                case 'y':
                case 'Y':
                    return CellFormatType.DATE;
                case 'h':
                case 'H':
                case 'm':
                case 'M':
                case 's':
                case 'S':
                    // These can be part of date, or elapsed
                    couldBeDate = true;
                    break;
                case '0':
                    // This can be part of date, elapsed, or number
                    seenZero = true;
                    break;
                case '[':
                    return CellFormatType.ELAPSED;
                case '#':
                case '?':
                    return CellFormatType.NUMBER;
                }
            }
        }

        // Nothing definitive was found, so we figure out it deductively
        if (couldBeDate)
            return CellFormatType.DATE;
        if (seenZero)
            return CellFormatType.NUMBER;
        return CellFormatType.TEXT;
    }

    /**
     * Returns a version of the original string that has any special characters
     * quoted (or escaped) as appropriate for the cell format type.  The format
     * type object is queried to see what is special.
     *
     * @param repl The original string.
     * @param type The format type representation object.
     *
     * @return A version of the string with any special characters replaced.
     *
     * @see CellFormatType#isSpecial(char)
     */
    static String quoteSpecial(String repl, CellFormatType type) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repl.length(); i++) {
            char ch = repl.charAt(i);
            if (ch == '\'' && type.isSpecial('\'')) {
                sb.append('\u0000');
                continue;
            }

            boolean special = type.isSpecial(ch);
            if (special)
                sb.append("'");
            sb.append(ch);
            if (special)
                sb.append("'");
        }
        return sb.toString();
    }

    /**
     * Apply this format part to the given value.  This returns a {@link
     * CellFormatResult} object with the results.
     *
     * @param value The value to apply this format part to.
     *
     * @return A {@link CellFormatResult} object containing the results of
     *         applying the format to the value.
     */
    public CellFormatResult apply(Object value) {
        boolean applies = applies(value);
        String text;
        Color textColor;
        if (applies) {
            text = format.format(value);
            textColor = color;
        } else {
            text = format.simpleFormat(value);
            textColor = null;
        }
        return new CellFormatResult(applies, text, textColor);
    }

    /**
     * Apply this format part to the given value, applying the result to the
     * given label.
     *
     * @param label The label
     * @param value The value to apply this format part to.
     *
     * @return <tt>true</tt> if the
     */
    public CellFormatResult apply(JLabel label, Object value) {
        CellFormatResult result = apply(value);
        label.setText(result.text);
        if (result.textColor != null) {
            label.setForeground(result.textColor);
        }
        return result;
    }

    /**
     * Returns the CellFormatType object implied by the format specification for
     * the format part.
     *
     * @return The CellFormatType.
     */
    public CellFormatType getCellFormatType() { //20120402, henrichen@zkoss.org: make public
        return type;
    }

    /**
     * Returns <tt>true</tt> if this format part has a condition.
     *
     * @return <tt>true</tt> if this format part has a condition.
     */
    boolean hasCondition() {
        return condition != null;
    }

    public static StringBuffer parseFormat(String fdesc, CellFormatType type,
            PartHandler partHandler) {

        // Quoting is very awkward.  In the Java classes, quoting is done
        // between ' chars, with '' meaning a single ' char. The problem is that
        // in Excel, it is legal to have two adjacent escaped strings.  For
        // example, consider the Excel format "\a\b#".  The naive (and easy)
        // translation into Java DecimalFormat is "'a''b'#".  For the number 17,
        // in Excel you would get "ab17", but in Java it would be "a'b17" -- the
        // '' is in the middle of the quoted string in Java.  So the trick we
        // use is this: When we encounter a ' char in the Excel format, we
        // output a \u0000 char into the string.  Now we know that any '' in the
        // output is the result of two adjacent escaped strings.  So after the
        // main loop, we have to do two passes: One to eliminate any ''
        // sequences, to make "'a''b'" become "'ab'", and another to replace any
        // \u0000 with '' to mean a quote char.  Oy.
        //
        // For formats that don't use "'" we don't do any of this
        Matcher m = SPECIFICATION_PAT.matcher(fdesc);
        StringBuffer fmt = new StringBuffer();
        while (m.find()) {
            String part = group(m, 0);
            if (part.length() > 0) {
                String repl = partHandler.handlePart(m, part, type, fmt);
                if (repl == null) {
                    switch (part.charAt(0)) {
                    case '\"':
                        repl = quoteSpecial(part.substring(1,
                                part.length() - 1), type);
                        break;
                    case '\\':
                        repl = quoteSpecial(part.substring(1), type);
                        break;
                    case '_':
                        repl = " ";
                        break;
                    case '*': //!! We don't do this for real, we just put in 3 of them
                        repl = ""; //expandChar(part); //20100924, henrichen@zkoss.org: DON'T expand, just clear it out!
                        break;
                    default:
                        repl = part;
                        break;
                    }
                }
                m.appendReplacement(fmt, Matcher.quoteReplacement(repl));
            }
        }
        m.appendTail(fmt);

        if (type.isSpecial('\'')) {
            // Now the next pass for quoted characters: Remove '' chars, making "'a''b'" into "'ab'"
            int pos = 0;
            while ((pos = fmt.indexOf("''", pos)) >= 0) {
                fmt.delete(pos, pos + 2);
            }

            // Now the final pass for quoted chars: Replace any \u0000 with ''
            pos = 0;
            while ((pos = fmt.indexOf("\u0000", pos)) >= 0) {
                fmt.replace(pos, pos + 1, "''");
            }
        }

        return fmt;
    }

    /**
     * Expands a character. This is only partly done, because we don't have the
     * correct info.  In Excel, this would be expanded to fill the rest of the
     * cell, but we don't know, in general, what the "rest of the cell" is.
     *
     * @param part The character to be repeated is the second character in this
     *             string.
     *
     * @return The character repeated three times.
     */
    static String expandChar(String part) {
        String repl;
        char ch = part.charAt(1);
        repl = "" + ch + ch + ch;
        return repl;
    }

    /**
     * Returns the string from the group, or <tt>""</tt> if the group is
     * <tt>null</tt>.
     *
     * @param m The matcher.
     * @param g The group number.
     *
     * @return The group or <tt>""</tt>.
     */
    public static String group(Matcher m, int g) {
        String str = m.group(g);
        return (str == null ? "" : str);
    }
}