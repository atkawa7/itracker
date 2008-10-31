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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.itracker.model.Language;
import org.itracker.services.exceptions.ITrackerDirtyResourceException;

public class ITrackerResourceBundle extends ResourceBundle {
    
    private HashMap<String,Object> data = new HashMap<String,Object>();
    private Object[][] dataArray = null;
    private Locale locale;

    public ITrackerResourceBundle() {
    }

    public ITrackerResourceBundle(Locale locale, Object[][] data) {
        setLocale(locale);
        setContents(data);
    }

    public ITrackerResourceBundle(Locale locale, List<Language> items) {
        setLocale(locale);
        setContents(items);
    }

    public Object[][] getContents() {
        // Only load the array if it is requested for some reason.
        if(dataArray == null) {
            int i = 0;
            Object[][] newData = new Object[2][data.size()];
            for(Iterator<String> iter = data.keySet().iterator(); iter.hasNext(); i++) {
                newData[0][i] = iter.next();
                newData[1][i] = data.get(newData[0][i]);
            }
            this.dataArray = newData;
        }
        
        return dataArray.clone();
    }

    public void setContents(List<Language> content) {
        if(content != null ) {
            synchronized (data) {
                data.clear();
                for(int i = 0; i < content.size(); i++) {
                    data.put(content.get(i).getResourceKey(), content.get(i).getResourceValue());
                }
            }
        }
    }

    public void setContents(Object[][] content) {
        if(content != null && content.length == 2 && content[0].length == content[1].length) {
            synchronized (data) {
                data.clear();
                for(int i = 0; i < content[0].length; i++) {
                    data.put((String)content[0][i], content[1][i]);
                }
            }
        }
    }

    public Locale getLocale() {
        return (this.locale == null ? super.getLocale() : this.locale);
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public boolean isDirty(String key) {
        Object value = handleGetObject(key);
        if(value != null && value instanceof DirtyKey) {
            return true;
        }
        return false;
    }
    
    //public void updateValue(String key, Object value) {
    //     synchronized (data) {
    //         data.put(key, value);
    //     }
    //    }
    
    public void updateValue(String key, String value) {
        synchronized (data) {
            data.put(key, value);
        }
    }

    public void updateValue(Language model) {
        if(model != null) {
            synchronized (data) {
                data.put(model.getResourceKey(), model.getResourceValue());
            }
        }
    }

    // public void removeValue(String key, boolean markDirty) {
    // if(key != null) {
    //          synchronized (data) {
    //              if(markDirty) {
    //                  data.put(key, new DirtyKey());
    //             } else {
    //                 data.remove(key);
    //             }
    //         }
    //     }
    // }
    
    public void removeValue(String key, boolean markDirty) {
        if(key != null) {
            synchronized (data) {
                if(markDirty) {
                    data.put(key, new DirtyKey(){});
                } else {
                    data.remove(key);
                }
            }
        }
    }

    /**
      * Implementation of ResourceBundle.handleGetObject.  Returns
      * the request key from the internal data map.
      */
    public final Object handleGetObject(String key) {
        Object value = data.get(key);
        if(value instanceof DirtyKey) {
            throw new ITrackerDirtyResourceException("The requested key has been marked dirty.", "ITrackerResourceBundle_" + locale.toString(), key);
        }
        return value;
    }

    /**
      * Implementation of ResourceBundle.getKeys.  Since it returns an enumeration,
      * It creates a new Hashtable, and returns that collections enumerator.
      */
    public Enumeration<String> getKeys() {
        Hashtable<String, Object> table = new Hashtable<String, Object>(data);
        return table.keys();
    }

    public static interface DirtyKey {
    }
}
