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

package org.itracker.web.scheduler;

import java.util.HashMap;


/**
 * This class contains utilities for dealing with the Scheduler and Scheduler tasks.
 */
public class SchedulerUtilities {
    
    public static final String TASK_CLASS_REMINDER = "org.itracker.web.scheduler.tasks.ReminderNotification";
    public static final String TASK_KEY_REMINDER = "itracker.web.admin.scheduler.task.reminder";
    
    private static HashMap<String,String> definedTasks;
    
    static {
        definedTasks = new HashMap<String,String>();
        definedTasks.put(TASK_CLASS_REMINDER, TASK_KEY_REMINDER);
    }
    
    public SchedulerUtilities() {
    }
    
    /**
     * This returns a HashMap of defined task classes and their associated resource bundle key.
     * @return a HashMap with the classes and keys of predefined tasks
     */
    public static HashMap<String,String> getDefinedTasks() {
        return definedTasks;
    }
    
    
    /**
     * Returns the key associated with a class, or null if that class has not been defined.
     * @param className the name of the class including the package to look up
     * @return a sting containing the reosuce bundle key, or null if the class is not defined
     */
    public static String getClassKey(String className) {
        if(definedTasks.containsKey(className)) {
            return (String) definedTasks.get(className);
        }
        return null;
    }
}
