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

package org.itracker.model.deprecatedmodels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.itracker.services.util.Convert;

/**
 * This represents a currently scheduled task to be run by the scheduler.
 */
class ScheduledTaskModel extends GenericModel implements Cloneable {
    
    public int[] hours = new int[] { -1 };
    public int[] mins   = new int[] { -1 };
    public int[] mdays  = new int[] { -1 };
    public int[] months = new int[] { -1 };
    public int[] wdays  = new int[] { -1 };
    public Date lastRun = null;
    
    public String className = null;
    public String[] args = new String[0];
    
    public ScheduledTaskModel() {
    }
    
    public int[] getHours() {
        return hours;
    }
    
    public void setHours(int[] value) {
        hours = value;
    }
    
    public void setHours(String value) {
        hours = parseString(value);
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
        return months;
    }
    
    public void setMonths(int[] value) {
        months = value;
    }
    
    public void setMonths(String value) {
        months = parseString(value);
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
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String value) {
        className = value;
    }
    
    public String[] getArgs() {
        return args;
    }
    
    public void setArgs(String[] value) {
        args = value;
    }
    
    public void setArgs(String value) {
        args = Convert.StringToArray(value);
    }
    
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
        ScheduledTaskModel clonedTask = null;
        try {
            clone = super.clone();
        } catch(CloneNotSupportedException e) {
        }
        
        clonedTask = (ScheduledTaskModel) clone;
        
        if(clonedTask != null) {
            if(id != null) {
                clonedTask.id = new Integer(id.intValue());
            }
            
            if(hours != null) {
                clonedTask.hours = new int[hours.length];
                for(int i = 0; i < hours.length; i++) {
                    clonedTask.hours[i] = hours[i];
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
                clonedTask.months = new int[months.length];
                for(int i = 0; i < months.length; i++) {
                    clonedTask.months[i] = months[i];
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
}
