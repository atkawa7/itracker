/*
 * This software was designed and created by Jason Carroll.
 * Copyright (c) 2002, 2003, 2004 Jason Carroll.
 * The author can be reached at jcarroll@cowsultants.com
 * ITracker website: http://www.cowsultants.com
 * ITracker forums: http://www.cowsultants.com/phpBB/index.php
 *
 * This program is free software; you can redistribute it and/or modify
 * it only under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.itracker.core.resources;

import org.apache.log4j.Logger;
import org.apache.oro.text.regex.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * This class provides support for message replacement when there is a need for more than
 * 10 arguements.  Currently the only additional formatting it accepts are number patterns.
 */
public class MessageFormat {

    private static final Logger logger = Logger.getLogger(MessageFormat.class);
    private static final Perl5Matcher matcher = new Perl5Matcher();
    private static final PatternCompiler compiler = new Perl5Compiler();
    private static Pattern pattern;

    static {
        try {
            pattern = compiler.compile("{(\\d+),?([\\w]*),?(.*?)}", Perl5Compiler.CASE_INSENSITIVE_MASK | Perl5Compiler.SINGLELINE_MASK);
        } catch (MalformedPatternException mpe) {
            logger.error("Invalid pattern specified.  No formatting will be performed.", mpe);
            pattern = null;
        }
    }

    public static String format(String message, Object[] options) {
        return format(message, options, ITrackerResources.getLocale());
    }

    public static String format(String message, Object[] options, Locale locale) {
        String output = message;

        if (pattern != null) {
            int currentOffset = 0;
            StringBuffer buffer = new StringBuffer();
            PatternMatcherInput input = new PatternMatcherInput(message);

            while (matcher.contains(input, pattern)) {
                MatchResult result = null;
                try {
                    result = matcher.getMatch();
                    int numGroups = result.groups();
                    int optionNumber = Integer.parseInt(result.group(1));

                    buffer.append(message.substring(currentOffset, result.beginOffset(0)));
                    currentOffset = result.endOffset(0);

                    if (options != null && optionNumber < options.length && options[optionNumber] != null) {
                        if (numGroups > 2 && "number".equalsIgnoreCase(result.group(2))) {
                            // Format the option value as a number
                            try {
                                NumberFormat formatter = NumberFormat.getInstance(locale);
                                if (numGroups > 3) {
                                    formatter = new DecimalFormat(result.group(3));
                                }
                                buffer.append(formatter.format(Double.parseDouble(options[optionNumber].toString())));
                            } catch (Exception e) {
                                logger.debug("Unable to format " + options[optionNumber] + " as number.", e);
                                buffer.append(options[optionNumber].toString());
                            }
                        } else {
                            buffer.append(options[optionNumber].toString());
                        }
                    }
                } catch (Exception e) {
                    logger.error("Unable to perform option replacement for option " + (result == null ? "NULL" : result.group(1)));
                    logger.debug("Invalid option has current offest of " + currentOffset + " in message '" + message + "'", e);
                }
            }

            if (buffer.length() > 0) {
                buffer.append(message.substring(currentOffset));
                output = buffer.toString();
            }
        }

        return output;
    }

}
