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

package org.itracker.services.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.itracker.model.Component;
import org.itracker.model.CustomFieldValue;
import org.itracker.model.NameValuePair;
import org.itracker.model.User;
import org.itracker.model.Version;


public class Convert {
    /**
      * Converts an array of CustomFieldValueModels to NameValuePairs
      * @param options the array of CustomFieldValueModels to convert
      * @return the new NameValuePair array
      */
    public static List<NameValuePair> customFieldOptionsToNameValuePairs(List<CustomFieldValue> options) {
        List<NameValuePair> returnValues = new ArrayList<NameValuePair>();

        if(options != null) {
            returnValues = new ArrayList<NameValuePair>();
            for(int i = 0; i < options.size(); i++) {
                returnValues.add(new NameValuePair(options.get(i).getCustomField().getName(), options.get(i).getValue()));
            }
        }

        return returnValues;
    }

    /**
      * Converts an array of UserModels to NameValuePairs
      * @param options the array of UserModels to convert
      * @return the new NameValuePair array
      */
    public static List<NameValuePair> usersToNameValuePairs(List<User> users) {
        List<NameValuePair> returnValues = new ArrayList<NameValuePair>();

        if(users != null) {
            returnValues = new ArrayList<NameValuePair>();
            for(int i = 0; i < users.size(); i++) {
                returnValues.add(new NameValuePair(users.get(i).getFirstName() + " " + users.get(i).getLastName(), users.get(i).getId().toString()));
            }
        }

        return returnValues;
    }

    /**
      * Converts an array of ComponentModels to NameValuePairs
      * @param options the array of ComponentModels to convert
      * @return the new NameValuePair array
      */
    public static List<NameValuePair> componentsToNameValuePairs(List<Component> components) {
        NameValuePair[] returnValues = new NameValuePair[0];

        if(components != null) {
            returnValues = new NameValuePair[components.size()];
            for(int i = 0; i < components.size(); i++) {
                returnValues[i] = new NameValuePair(components.get(i).getName(), components.get(i).getId().toString());
            }
        }

        return Arrays.asList(returnValues);
    }

    /**
      * Converts an array of VersionModels to NameValuePairs
      * @param options the array of VersionModels to convert
      * @return the new NameValuePair array
      */
    public static List<NameValuePair> versionsToNameValuePairs(List<Version> versions) {
        NameValuePair[] returnValues = new NameValuePair[0];

        if(versions != null) {
            returnValues = new NameValuePair[versions.size()];
            for(int i = 0; i < versions.size(); i++) {
                returnValues[i] = new NameValuePair(versions.get(i).getNumber(), versions.get(i).getId().toString());
            }
        }

        return Arrays.asList(returnValues);
    }


  	public static String[] StringToArray(String input) {
        if(input == null || "".equals(input)) {
            return new String[0];
        }

        List<String> tokenList = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(input, " ");
        while(tokenizer.hasMoreElements()) {
        	String token = tokenizer.nextToken();
            boolean quotedToken = false;
        		if(token.startsWith("\"")) {
                quotedToken = true;
          			token = token.substring(1);
          			if(token.endsWith("\"") && ! token.endsWith("\\\"")) {
          			    quotedToken = false;
            				token = token.substring(0, token.length() - 1);
          			}
        		}

        		if(quotedToken) {
                boolean getNext = true;
                while(getNext) {
            	  		try {
              		  		token += tokenizer.nextToken("\"");
                        if(! token.endsWith("\\\"")){
                            getNext = false;
                        }
              			} catch(NoSuchElementException e){
                        break;
                    }
                }
        		}

            tokenList.add(token);
        }

        String[] stringArray = new String[tokenList.size()];
        stringArray = (String[])tokenList.toArray();

        return stringArray;
  	}
}
