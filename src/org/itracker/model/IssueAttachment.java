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
import java.util.Date;

/**
 * A file attachment to an Issue.
 *
 * @author ready
 */
public class IssueAttachment extends AbstractEntity
        implements Comparable<IssueAttachment> {
    
    /** Compares 2 attachments by file size. */
    public static final Comparator<IssueAttachment> SIZE_COMPARATOR =
            new SizeComparator();
    
    /** The issue to which the file is attached. */
    private Issue issue;
    
    /** The file name used to upload the attachment. */
    private String originalFileName;
    
    /**
     * Globally unique file name constructed from the concatenation
     * of the issue id and original file name.
     *
     * PENDING: remove this computed field.
     */
    private String fileName;
    
    /** MIME type. */
    private String type;
    
    /** Byte size. */
    private long size;
    
    /** Attachment description or comment. */
    private String description;
    
    /**
     * PENDING: this should probably not be saved in the DB nor be loaded
     * in memory for good resource management.
     */
    private byte[] fileData;
    
    /** The use who created the attachment. */
    private User user;
    
    
    /**
     * Default constructor (required by Hibernate).
     *
     * <p>PENDING: should be <code>private</code> so that it can only be used
     * by Hibernate, to ensure that the fields which form an instance's
     * identity are always initialized/never <tt>null</tt>. </p>
     */
    public IssueAttachment() {
    }
    
    public IssueAttachment(Issue issue, String originalFileName) {
        super(new Date());
        setIssue(issue);
        setOriginalFileName(originalFileName);
    }
    
    /**
     * Convenience constructor.
     */
    public IssueAttachment(Issue issue, String origFileName, String type, String description, long size) {
        this(issue, origFileName);
        this.setType(type);
        this.setDescription(description);
        this.setSize(size);
    }
    
    /**
     * Convenience constructor.
     */
    public IssueAttachment(Issue issue, String origFileName, String type, String description,
            long size, User user) {
        this(issue, origFileName, type, description, size);
        this.setUser(user);
    }
    
    public  Issue getIssue() {
        return(issue);
    }
    
    public  void setIssue(Issue issue) {
        if (issue == null) {
            throw new IllegalArgumentException("null issue");
        }
        this.issue = issue;
    }
    
    public  String getOriginalFileName() {
        return originalFileName;
    }
    public  void setOriginalFileName(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("null fileName");
        }
        this.originalFileName = fileName;
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
        final int lastIndex = this.originalFileName.lastIndexOf('.');
        
        if (lastIndex > 0) {
            return this.originalFileName.substring(lastIndex);
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
        return size;
    }
    
    public  void setSize(long size) {
        this.size = size;
    }
    
    public  User getUser() {
        return user;
    }
    
    public  void setUser(User user) {
        this.user = user;
    }
    
    public int compareTo(IssueAttachment other) {
        final int issueComparison = this.issue.compareTo(other.issue);
        
        if (issueComparison == 0) {
            return this.originalFileName.compareTo(other.originalFileName);
        }
        return issueComparison;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof IssueAttachment) {
            final IssueAttachment other = (IssueAttachment)obj;
            
            return this.issue.equals(other.issue)
            && this.originalFileName.equals(other.originalFileName);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.issue.hashCode() + this.originalFileName.hashCode();
    }
    
    @Override
    public String toString() {
        return "IssueAttachment [id=" + this.id
                + ", issue=" + this.issue
                + ", originalfileName=" + this.originalFileName;
    }
    
    
    /**
     * Compares 2 attachments by file size.
     */
    public static class SizeComparator implements Comparator<IssueAttachment> {
        
        public int compare(IssueAttachment a, IssueAttachment b) {
            return (int)(a.size - b.size);
        }
        
    }
    
}
