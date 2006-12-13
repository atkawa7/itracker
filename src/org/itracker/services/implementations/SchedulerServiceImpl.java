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

package org.itracker.services.implementations;

import java.util.Date;
import java.util.List;

import org.itracker.model.ScheduledTask;
import org.itracker.persistence.dao.ScheduledTaskDAO;
import org.itracker.services.SchedulerService;

public class SchedulerServiceImpl implements SchedulerService {

    private final ScheduledTaskDAO scheduledTaskDAO;

    public SchedulerServiceImpl(ScheduledTaskDAO scheduledTaskDAO) {
        this.scheduledTaskDAO = scheduledTaskDAO;
    }

    public  ScheduledTask getTask(Integer id) {
            ScheduledTask task = scheduledTaskDAO.findByPrimaryKey(id);
            return task;
    }

    public List<ScheduledTask> getAllTasks() {
        List<ScheduledTask> tasks = scheduledTaskDAO.findAll();
            
        return tasks;
    }

    /**
     * Creates a scheduled task
     *
     * @param model The <code>ScheduledTask</code> carrying the data
     * @return The <code>ScheduledTask</code> after saving
     */
    public ScheduledTask createTask(ScheduledTask task) {
        task.setCreateDate(new Date());
        this.scheduledTaskDAO.saveOrUpdate( task );
        return task;
    }


    /**
     * updates a scheduled task
     *
     * @param model The <code>ScheduledTask</code> carrying the data
     * @return The <code>ScheduledTask</code> after updating
     */    
    public ScheduledTask updateTask(ScheduledTask task) {
        this.scheduledTaskDAO.saveOrUpdate(task);
        return task;
    }

    /**
     * removes a scheduled task
     *
     * @param taskId The id of the task
     * @return true if removed
     */
    public boolean removeTask(Integer taskId) {
        ScheduledTask task = this.scheduledTaskDAO.findByPrimaryKey(taskId);
        this.scheduledTaskDAO.delete(task);
        return false;
    }

}
  