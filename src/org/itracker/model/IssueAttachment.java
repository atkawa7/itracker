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

import java.util.Comparator;

/**
 * This is a POJO Business Domain Object modelling an attachment to an Issue. 
 * 
 * <p>Hibernate Bean. </p>
 * 
 * @author ready
 */
public class IssueAttachment extends AbstractBean {

    private String originalFileName;
    private String type;
    private String fileName;
    
    /** 
     * PENDING: this should probably not be saved in the DB nor be loaded 
     * in memory for good resource management. 
     */
    private byte[] fileData;
    private String description;
    private long size;
    private Issue issue;
    private User user;

    /**
     * Default constructor required by Hibernate. 
     */
    public IssueAttachment() {
    }

    public IssueAttachment(String origFileName, String type, String description, long size) {
        this.setOriginalFileName(origFileName);
        this.setType(type);
        this.setDescription(description);
        this.setSize(size);
    }

    public IssueAttachment(String origFileName, String type, String description, 
            long size, Issue issue, User user) {
        this(origFileName, type, description, size);
        this.setIssue(issue);
        this.setUser(user);
    }
    
    public  String getOriginalFileName() {
        return originalFileName;
    }
    public  void setOriginalFileName(String value) {
        originalFileName = value;
    }

    public  String getType() {
        return type;
    }
    public  void setType(String value) {
        type = value;
    }

    public  String getFileName() {
        return(fileName);
    }
    public  void setFileName(String value) {
        fileName = value;
    }

    public String getFileExtension() {
        int lastIndex = getOriginalFileName().lastIndexOf('.');
        if(lastIndex > 0) {
           return getOriginalFileName().substring(lastIndex);
        }

        return "";
    }
    
    public  byte[] getFileData() {
        return(fileData);
    }
    public  void setFileData(byte[] value) {
        fileData = value;
    }

    public  String getDescription() {
        return description;
    }
    public  void setDescription(String value) {
        description = value;
    }

    public  long getSize() {
        return(size);
    }
    
    public  void setSize(long value) {
        size = value;
    }

    public  Issue getIssue() {
        return(issue);
    }
    
    public  void setIssue(Issue value) {
        issue = value;
    }

    public  User getUser() {
        return(user);
    }
    public  void setUser(User value) {
        user = value;
    }

    public static abstract class IssueAttachmentComparator implements Comparator<IssueAttachment> {
        protected boolean isAscending = true;

        public IssueAttachmentComparator() {
        }

        public IssueAttachmentComparator(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        protected abstract int doComparison(IssueAttachment ma, IssueAttachment mb);

        public final int compare(IssueAttachment ma, IssueAttachment mb) {
            int result = doComparison(ma, mb);
            
            if (! isAscending) {
                result = result * -1;
            }
            return result;
        }
    }

    public static class CompareByDate extends IssueAttachmentComparator {
        public CompareByDate(){
          super();
        }

        public CompareByDate(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(IssueAttachment ma, IssueAttachment mb) {
            if(ma.getCreateDate() == null && mb.getCreateDate() == null) {
                return 0;
            } else if(ma.getCreateDate() == null) {
                return 1;
            } else if(mb.getCreateDate() == null) {
                return -1;
            }

            if(ma.getCreateDate().equals(mb.getCreateDate())) {
                return 0;
            } else if(ma.getCreateDate().before(mb.getCreateDate())) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public static class CompareByIssueId extends IssueAttachmentComparator {
        public CompareByIssueId(){
          super();
        }

        public CompareByIssueId(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(IssueAttachment ma, IssueAttachment mb) {
            if(ma.getIssue() == null && mb.getIssue() == null) {
                return 0;
            } else if(ma.getIssue() == null) {
                return 1;
            } else if(mb.getIssue() == null) {
                return -1;
            }

            if(ma.getIssue().equals(mb.getIssue())) {
                return ma.getId().compareTo(mb.getId());
            } else {
                return ma.getIssue().compareTo(mb.getIssue());
            }
        }
    }

    public static class CompareBySize extends IssueAttachmentComparator {
        public CompareBySize(){
          super();
        }

        public CompareBySize(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(IssueAttachment ma, IssueAttachment mb) {
            if(ma.getIssue() == null && mb.getIssue() == null) {
                return 0;
            } else if(ma.getIssue() == null) {
                return 1;
            } else if(mb.getIssue() == null) {
                return -1;
            }

            if(ma.getSize() == mb.getSize()) {
                return ma.getIssue().compareTo(mb.getIssue());
            } else if(ma.getSize() > mb.getSize()) {
                return 1;
            } else {
                return -1;
            }
        }
    }
    
}
