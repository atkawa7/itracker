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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.apache.log4j.Logger;
import org.itracker.model.ScheduledTask;

public class Scheduler extends Thread {
    
    private final Logger logger;
    private GregorianCalendar currDate;
    private static GregorianCalendar lastDate;
    private static GregorianCalendar startDate;
    private static List<SchedulerThread> threads;
    private static List<ScheduledTask> tasks;
    private static boolean stop = false;
    
    public Scheduler() {
        this.logger = Logger.getLogger(getClass());
        logger.debug("Scheduler starting.");
        lastDate = new GregorianCalendar();
        currDate = new GregorianCalendar();
        threads = new ArrayList<SchedulerThread>();
        tasks = new ArrayList<ScheduledTask>();
    }
    
    /**
     * Main processing method of the scheduler.  Every minute it spans a new SchedulerThread
     * which looks for tasks that need to be run.
     */
    public void run() {
        while(true) {
            if(stop) {
                break;
            }
            
            currDate = new GregorianCalendar();
            if(currDate.get(Calendar.MINUTE) == lastDate.get(Calendar.MINUTE)) {
                // Not time yet..try again in 20 seconds
                try {
                    sleep(20000);
                    for(int i = 0; i < threads.size(); i++) {
                        SchedulerThread minThread = (SchedulerThread) threads.get(i);
                        if(! minThread.isAlive()) {
                            threads.remove(i);
                        }
                    }
                } catch(Exception e) {
                    logger.error("Error checking on scheduler threads.  " + e.getMessage());
                }
            } else {
                lastDate.setTime(currDate.getTime());
                if((currDate.get(Calendar.MINUTE) % 10) == 0) {
                    logger.debug("Scheduler currently running with " + threads.size() + " active threads.");
                }
                
                SchedulerThread sThread = null;
                try {
                    List<ScheduledTask> tasksArray = new ArrayList<ScheduledTask>();
                    tasksArray = tasks;
                    sThread = new SchedulerThread(this, tasksArray);
                    sThread.setPriority(7);
                    sThread.start();
                    threads.add(sThread);
                } catch(Exception e) {
                    logger.error("Error while starting new scheduler thread.  " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        currDate = null;
    }
    
    /**
     * Returns the current number of SchedulerThreads that are running.  Usually there
     * is one scheduler thread produced each minute.  A large number of these could
     * indecate problems processing tasks.
     */
    public static int getNumThreads() {
        if(threads == null) {
            threads = new ArrayList<SchedulerThread>();
        }
        return(threads.size());
    }
    
    /**
     * Returns the current number of tasks being checked by the scheduler.
     */
    public static int getNumTasks() {
        if(tasks == null) {
            tasks = new ArrayList<ScheduledTask>();
        }
        return(tasks.size());
    }
    
    /**
     * Returns the time the scheduler last looked for tasks to process
     */
    public static Date getLastRun() {
        if(lastDate != null) {
            return lastDate.getTime();
        }
        return null;
    }
    
    /**
     * Returns a currently defined scheduler task.
     * @param taskId the id of the task to return
     * @return a ScheduledTask of the requested task
     */
    public static ScheduledTask getTask(int taskId) {
        if(tasks != null) {
            for(int i = 0; i < tasks.size(); i++) {
                ScheduledTask task = (ScheduledTask) tasks.get(i);
                if(task.getId() != null && taskId == task.getId().intValue()) {
                    return task;
                }
            }
        }
        return null;
    }
    
    /**
     * Returns all currently defined scheduler tasks.
     * @return an array of ScheduledTaskModels
     */
    public static ScheduledTask[] getTasks() {
        if(tasks == null) {
            return new ScheduledTask[0];
        }
        
        ScheduledTask[] tasksArray = new ScheduledTask[tasks.size()];
        for(int i = 0; i < tasks.size(); i++) {
            tasksArray[i] = (ScheduledTask) tasks.get(i);
        }
        return tasksArray;
    }
    
    /**
     * Adds a new task into the list.
     * @param task the task to add
     */
    public static void addTask(ScheduledTask task) {
        if(task != null) {
            tasks.add(task);
        }
    }
    
    /**
     * Removes a task for the list of tasks to perform.
     * @param taskId the id of the task to remove
     */
    public static void removeTask(int taskId) {
        if(tasks != null) {
            for(int i = 0; i < tasks.size(); i++) {
                ScheduledTask task = (ScheduledTask) tasks.get(i);
                if(task.getId() != null && taskId == task.getId().intValue()) {
                    tasks.remove(i);
                    return;
                }
            }
        }
    }
    
    /**
     * This will stop the current scheduler threads, but NOT the worker threads.
     * This means you might be able to stop new jobs from starting but not currently
     * running jobs.  In general this should never be called.
     */
    public static void stopAll() {
        for(int i = 0; i < threads.size(); i++) {
            SchedulerThread thread = (SchedulerThread) threads.get(i);
            if(thread.isAlive()) {
                thread.setStop(true);
            }
            threads.remove(i);
        }
    }
    
    /**
     * Outputs a task description.
     * @param taskId the id of the task to print
     */
    public static String printTask(int taskId) {
        ScheduledTask task = getTask(taskId);
        if(task != null) {
            return task.toString();
        }
        return "Unknown task.";
    }
    
    /**
     * Outputs the full date of the last time the task was run.
     * @param taskId the id of the task to print
     */
    public static String printTaskLastRun(int taskId) {
        ScheduledTask task = getTask(taskId);
        if(task != null && task.getLastRun() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            return sdf.format(task.getLastRun());
        }
        return "Unknown task.";
    }
    
    /**
     * Sets the date a task was last run to the current date/time.
     * @param taskId the id of the task to set
     */
    public static void setTaskLastRun(int taskId) {
        ScheduledTask task = getTask(taskId);
        if(task != null) {
            task.setLastRun(new Date());
        }
    }
    
    /**
     * Sets a flag to tell the scheduler whether to continue to process tasks.
     */
    public static void setStop(boolean value) {
        stop = value;
    }
    
    public static GregorianCalendar getStartDate() {
        return startDate;
    }
    
    public static void setStartDate(GregorianCalendar startDate) {
        Scheduler.startDate = startDate;
    }
}

/**
 * This class represents a currently running task in the scheduler.
 */
class WorkerThread extends Thread {
    
    private final Logger logger;
    private ScheduledTask task = null;
    
    public WorkerThread(ScheduledTask task) {
        this.logger = Logger.getLogger(getClass());
        this.task = task;
    }
    
    public void run() {
        try {
            if(task == null || task.getClassName() == null) {
                throw new Exception("Scheduled task, class, or method is null.");
            }
            
            Class taskClass = Class.forName(task.getClassName());
            SchedulableTask workTask = (SchedulableTask) taskClass.newInstance();
            workTask.performTask(task.getArgs());
        } catch (Exception e) {
            logger.error("Unable to run scheduled task " + task.toString() + ".  " + e.getMessage());
        }
    }
}


/**
 * This class is used by the SchedulerEngine to process tasks at specific times.
 * It is spawned every minute and sent a copy of the current tasks list.  This
 * thread then determines if any of the tasks need to be run.  This prevents a single
 * run from stopping the scheduler altogether and preventing future tasks from being
 * spawned.
 */
class SchedulerThread extends Thread {
    
    private final Logger logger;
    private Scheduler engine = null;
    private GregorianCalendar startDate = null;
    private List<ScheduledTask> tasks = null;
    private boolean stop = false;
    
    public SchedulerThread() {
        this.logger = Logger.getLogger(getClass());
        tasks = new ArrayList<ScheduledTask>();
        startDate = new GregorianCalendar();
    }
    
    public SchedulerThread(Scheduler engine, List<ScheduledTask> tasks) {
        this();
        
        int hour = startDate.get(Calendar.HOUR_OF_DAY);
        int min = startDate.get(Calendar.MINUTE);
        int mday = startDate.get(Calendar.DAY_OF_MONTH);
        int month = startDate.get(Calendar.MONTH) + 1;
        int wday = startDate.get(Calendar.DAY_OF_WEEK);
        
        if(engine != null && tasks != null) {
            this.engine = engine;
            for(int i = 0; i < tasks.size(); i++) {
                if(tasks != null && tasks.get(i).getId() != null && tasks.get(i).getId().intValue() > 0) {
                    ScheduledTask task = (ScheduledTask) tasks.get(i).clone();
                    // Now check to see if this is our task.
                    if(processTask(task, hour, min, mday, month, wday)) {
                        this.tasks.add(task);
                    }
                }
            }
        }
    }
    
    public void run() {
        for(int i = 0; i < tasks.size(); i++) {
            if(stop) {
                break;
            }
            ScheduledTask task = (ScheduledTask) tasks.get(i);
            try {
                if(task != null && task.getId() != null) {
                    WorkerThread worker = new WorkerThread(task);
                    worker.setPriority(5);
                    worker.start();
                    Scheduler.setTaskLastRun(task.getId().intValue());
                }
            } catch(Exception e) {
                logger.error("Unable to run scheduled task " + task.toString());
            }
        }
    }
    
    public void setStop(boolean value) {
        stop = value;
    }
    
    private boolean processTask(ScheduledTask task, int hour, int min, int mday, int month, int wday) {
        if(hasInt(task.getHours(), hour) && hasInt(task.getMinutes(), min) && hasInt(task.getDaysOfMonth(), mday) &&
                hasInt(task.getMonths(), month) && hasInt(task.getWeekdays(), wday)) {
            return true;
        }
        
        return false;
    }
    
    private boolean hasInt(int[] array, int value) {
        if(array != null) {
            for(int i = 0; i < array.length; i++) {
                if(array[i] == -1 || value == array[i]) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Scheduler getEngine() {
        return engine;
    }
    
    public void setEngine(Scheduler engine) {
        this.engine = engine;
    }
}
