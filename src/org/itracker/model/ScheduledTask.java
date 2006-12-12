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

package org.itracker.model;

import java.util.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.itracker.services.util.Convert;

/**
 * This is a POJO Business Domain Object. Hibernate Bean.
 * @author ready
 *
 */
public class ScheduledTask extends AbstractBean {

    private String hours;
    @SuppressWarnings("unused")
	private String minutes;
    @SuppressWarnings("unused")
	private String daysOfMonth;
    private String months;
    @SuppressWarnings("unused")
	private String weekdays;
    private String className;

    private int[] hrs = new int[] { -1 };
    private int[] mins   = new int[] { -1 };
    private int[] mdays  = new int[] { -1 };
    private int[] mon = new int[] { -1 };
    private int[] wdays  = new int[] { -1 };
    private String[] args = new String[0];
    
    private Date lastRun = null;
    
    public String getArgsAsString() {
        if(args == null || args.length == 0) {
            return "";
        }
        
        StringBuffer buf = new StringBuffer();
        for(int i = 0; i < args.length; i++) {
            buf.append((i == 0 ? "" : " "));
            buf.append(args[i]);
        }
        return buf.toString();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int[] getHours() {
        return hrs;
    }
    
    public void setHours(int[] value) {
        hrs = value;
    }
    
    public void setHours(String value) {
        hrs = parseString(value);
    }
    
    public int[] getMinutes() {
        return mins;
    }
    
    public void setMinutes(int[] value) {
        mins = value;
    }
    
    public void setMinutes(String value) {
        mins = parseString(value);
    }
    
    public int[] getDaysOfMonth() {
        return mdays;
    }
    
    public void setDaysOfMonth(int[] value) {
        mdays = value;
    }
    
    public void setDaysOfMonth(String value) {
        mdays = parseString(value);
    }
    
    public int[] getMonths() {
        return mon;
    }
    
    public void setMonths(int[] value) {
        mon = value;
    }
    
    public void setMonths(String value) {
        mon = parseString(value);
    }
    
    public int[] getWeekdays() {
        return wdays;
    }
    
    public void setWeekdays(int[] value) {
        wdays = value;
    }
    
    public void setWeekdays(String value) {
        wdays = parseString(value);
    }
    
    public Date getLastRun() {
        return lastRun;
    }
    
    public void setLastRun(Date value) {
        lastRun = value;
    }
    
//    public String[] getArgs() {
//        return args;
//    }
//    
//    public void setArgs(String[] value) {
//        args = value;
//    }
    
//    public void setArgs(String value) {
//        args = Convert.StringToArray(value);
//    }
//    
//    public String getArgsAsString() {
//        if(args == null || args.length == 0) {
//            return "";
//        }
//        
//        StringBuffer buf = new StringBuffer();
//        for(int i = 0; i < args.length; i++) {
//            buf.append((i == 0 ? "" : " "));
//            buf.append(args[i]);
//        }
//        return buf.toString();
//    }
    
    /**
     * Takes a string in normal cron format, and turns it into
     * a int array for easier processing.
     * @param value string to parse
     * @return an int array representing the parsed string
     */
    public int[] parseString(String value) {
        int[] valuesArray = new int[] { -1 };
        
        if(value != null && ! "".equals(value)) {
            List<Object> values = new ArrayList<Object>();
            StringTokenizer token = new StringTokenizer(value, ",");
            while(token.hasMoreElements()) {
                String element = (String) token.nextElement();
                try {
                    Integer intElement = new Integer(element.trim());
                    values.add(intElement);
                } catch(NumberFormatException nfe) {
                }
            }
            valuesArray = new int[values.size()];
            for(int i = 0; i < values.size(); i++) {
                valuesArray[i] = ((Integer) values.get(i)).intValue();
            }
        }
        return valuesArray;
    }
    
    /**
     * Takes an int array and collapses it back to a string for use in
     * storage.
     * @param values an int array to join
     * @return a string representing the int array
     */
    public String joinString(int[] values) {
        StringBuffer buf = new StringBuffer();
        if(values != null) {
            for(int i = 0; i < values.length; i++) {
                buf.append((i == 0 ? "" : ","));
                buf.append(Integer.toString(values[i]));
            }
        }
        return buf.toString();
    }
    
    public boolean isAll(int[] array) {
        if(array != null) {
            for(int i = 0; i < array.length; i++) {
                if(array[i] == -1) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Object clone() {
        Object clone = null;
        ScheduledTask clonedTask = null;
        try {
            clone = super.clone();
        } catch(CloneNotSupportedException e) {
        }
        
        clonedTask = (ScheduledTask) clone;
        
        if(clonedTask != null) {
            if(id != null) {
                clonedTask.id = new Integer(id.intValue());
            }
            
            if(hours != null) {
                clonedTask.hrs = new int[hrs.length];
                for(int i = 0; i < hrs.length; i++) {
                    clonedTask.hrs[i] = hrs[i];
                }
            }
            if(mins != null) {
                clonedTask.mins = new int[mins.length];
                for(int i = 0; i < mins.length; i++) {
                    clonedTask.mins[i] = mins[i];
                }
            }
            if(mdays != null) {
                clonedTask.mdays = new int[mdays.length];
                for(int i = 0; i < mdays.length; i++) {
                    clonedTask.mdays[i] = mdays[i];
                }
            }
            if(months != null) {
                clonedTask.mon = new int[mon.length];
                for(int i = 0; i < mon.length; i++) {
                    clonedTask.mon[i] = mon[i];
                }
            }
            if(wdays != null) {
                clonedTask.wdays = new int[wdays.length];
                for(int i = 0; i < wdays.length; i++) {
                    clonedTask.wdays[i] = wdays[i];
                }
            }
            
            if(className != null) {
                clonedTask.className = new String(className);
            }
            if(args != null) {
                clonedTask.args = new String[args.length];
                for(int i = 0; i < args.length; i++) {
                    clonedTask.args[i] = args[i];
                }
            }
            
            if(lastRun != null) {
                clonedTask.lastRun = (Date) lastRun.clone();
            }
        }
        return clonedTask;
    }
    
    public String toString() {
        return "Id: " + getId() + "  Class: " + getClassName();
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
    
    public void setArgs(String value) {
        args = Convert.StringToArray(value);
    }
        
}