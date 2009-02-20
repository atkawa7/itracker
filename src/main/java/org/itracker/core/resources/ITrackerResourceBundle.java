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
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.itracker.model.Language;
import org.itracker.services.exceptions.ITrackerDirtyResourceException;

public class ITrackerResourceBundle extends ResourceBundle {
    
    private HashMap<String,Object> data = new HashMap<String,Object>();
    /**
     * TODO should dataArray be re-factored out?
     */
    private Object[][] dataArray = null;
    

    static ResourceBundle loadBundle() {
    	return new ITrackerResourceBundle();
    }
    static ResourceBundle loadBundle(Locale locale) {
    	return new ITrackerResourceBundle(locale);
    }
    static ResourceBundle loadBundle(Locale locale, Object[][] data)  {
    	return new ITrackerResourceBundle(locale, data);
    }

    static ResourceBundle loadBundle(Locale locale, List<Language> items) {
    	return new ITrackerResourceBundle(locale, items);
    }
    private ITrackerResourceBundle() {
    	super.setParent(ResourceBundle.getBundle(ITrackerResources.RESOURCE_BUNDLE_NAME, new Locale(ITrackerResources.getDefaultLocale())));
    }
    /**
     * @param locale
     */
    private ITrackerResourceBundle(Locale locale) {
    	if (null == locale) {
    		locale = new Locale(ITrackerResources.getDefaultLocale());
    	}
    	setParent(ResourceBundle.getBundle(ITrackerResources.RESOURCE_BUNDLE_NAME, locale));
    }
    /**
     * @param locale
     * @param data
     * @deprecated used still for testing
     */
    public ITrackerResourceBundle(Locale locale, Object[][] data) {
    	this(locale);
        setContents(data);
    }

    /**
     * @param locale
     * @param items
     */
    private ITrackerResourceBundle(Locale locale, List<Language> items) {
    	this(locale);
        setContents(items);
    }


    /**
     * 
     * @return should be private or removed
     * @deprecated
     */
    public Object[][] getContents() {
        // Only load the array if it is requested for some reason.
        if(dataArray == null) {
            int i = 0;
            Object[][] newData = new Object[2][data.size()];
            Enumeration<String> keys = getKeys();
            while (keys.hasMoreElements()) {
                newData[0][i] = keys.nextElement();
                newData[1][i] = data.get(newData[0][i]);
			}
            
            
            this.dataArray = newData;
        }
        
        return dataArray.clone();
    }

    /**
     * @deprecated should be private
     * @param content
     */
    public void setContents(List<Language> content) {
        if(content != null ) {
            synchronized (data) {
                data.clear();
                this.dataArray = null;
                for(int i = 0; i < content.size(); i++) {
                    data.put(content.get(i).getResourceKey(), content.get(i).getResourceValue());
                }
            }
        }
    }

    /**
     * @deprecated
     * @param content should be private
     */
    private void setContents(Object[][] content) {
        if(content != null && content.length == 2 && content[0].length == content[1].length) {
            synchronized (data) {
                data.clear();
                this.dataArray = null;
                for(int i = 0; i < content[0].length; i++) {
                    data.put((String)content[0][i], content[1][i]);
                }
            }
        }
    }


    @Override
    public Locale getLocale() {
    	Locale l =  super.getLocale();
    	if (null == l && null != parent) {
    		return parent.getLocale();
    	}
    	return l;
    }

    public boolean isDirty(String key) {
    	try {
    		handleGetObject(key);
    	} catch (ITrackerDirtyResourceException exception) {
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
            this.dataArray = null;
        }
    }

    public void updateValue(Language model) {
        if(model != null) {
            synchronized (data) {
                data.put(model.getResourceKey(), model.getResourceValue());
                this.dataArray = null;
            }
        }
    }
    
    public void removeValue(String key, boolean markDirty) {
        if (key != null) {
            synchronized (data) {
                if(markDirty) {
                    data.put(key, new DirtyKey(){});
                } else {
                    data.remove(key);
                }
                this.dataArray = null;
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
            throw new ITrackerDirtyResourceException("The requested key has been marked dirty.", 
            		"ITrackerResourceBundle_" + getLocale(),
            		key);
        }
        return value;
    }

    /**
     * Implementation of ResourceBundle.getKeys.  Since it returns an enumeration,
     * It creates a new Hashtable, and returns that collections enumerator.
     */
   public Enumeration<String> getKeys() {
       Hashtable<String, Object> table = new Hashtable<String, Object>(data.size());
       if (null != parent) {
       	Enumeration<String> keys = parent.getKeys();
       	String key;
       	while (keys.hasMoreElements()) {
       		key = keys.nextElement();
				table.put(key, parent.getString(key));
				
			}
       }
       table.putAll(data);
       return table.keys();
   }

    public static interface DirtyKey {
    }
}
