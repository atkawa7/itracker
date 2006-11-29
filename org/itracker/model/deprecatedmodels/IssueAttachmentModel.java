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

import java.util.Comparator;

class IssueAttachmentModel extends GenericModel implements Comparator<IssueAttachmentModel> {
    private String type;
    private String fileName;
    private String originalFileName;
    private String description;
    private long size;
    private String firstName;
    private String lastName;
    private Integer issueId;
    private Integer userId;

    private UserModel user;

    public IssueAttachmentModel() {
    }

    public IssueAttachmentModel(String origFileName, String type, String description, long size) {
        this.setOriginalFileName(origFileName);
        this.setType(type);
        this.setDescription(description);
        this.setSize(size);
    }

    public IssueAttachmentModel(String origFileName, String type, String description, long size, Integer issueId, Integer userId) {
        this(origFileName, type, description, size);
        this.setIssueId(issueId);
        this.setUserId(userId);
    }

    public String getOriginalFileName() {
        return (originalFileName == null ? "" : originalFileName);
    }

    public void setOriginalFileName(String value) {
        originalFileName = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String value) {
        type = value;
    }

    public String getFileName() {
        return (fileName == null ? "attachment" + getId() : fileName);
    }

    public void setFileName(String value) {
        fileName = value;
    }

    public String getDescription() {
        return (description == null ? "" : description);
    }

    public void setDescription(String value) {
        description = value;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long value) {
        size = value;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel value) {
        user = value;
    }

    public Integer getUserId() {
        return (user == null ? userId : user.getId());
    }

    public void setUserId(Integer value) {
        userId = value;
    }

    public String getUserFirstName() {
        return (user == null ? "" : user.getFirstName());
    }

    public String getUserLastName() {
        return (user == null ? "" : user.getLastName());
    }

    public Integer getIssueId() {
        return issueId;
    }

    public void setIssueId(Integer value) {
        issueId = value;
    }

    public String getFileExtension() {
        int lastIndex = getOriginalFileName().lastIndexOf('.');
        if(lastIndex > 0) {
           return getOriginalFileName().substring(lastIndex);
        }

        return "";
    }

    public String toString() {
        return "Attachment [" + getId() + "] Description: " + getDescription() + " FileName: " + getFileName() +
               " OrigFileName: " + getOriginalFileName() + " Size: " + getSize() + " Type: " + getType();
    }

    public int compare(IssueAttachmentModel a, IssueAttachmentModel b) {
        return new IssueAttachmentModel.CompareByDate().compare(a, b);
    }

    public boolean equals(Object obj) {
        if(! (obj instanceof IssueAttachmentModel)) {
            return false;
        }

        try {
            IssueAttachmentModel mo = (IssueAttachmentModel) obj;
            if(IssueAttachmentModel.this.getCreateDate().equals(mo.getCreateDate()) &&
               IssueAttachmentModel.this.getFileName().equals(mo.getFileName()) &&
               IssueAttachmentModel.this.getDescription().equals(mo.getDescription())) {
                return true;
            }
        } catch(ClassCastException cce) {
        }

        return false;
    }

    public int hashCode() {
        return ((IssueAttachmentModel.this.getCreateDate() == null ? 1 : IssueAttachmentModel.this.getCreateDate().hashCode()) ^
                IssueAttachmentModel.this.getFileName().hashCode() ^
                (IssueAttachmentModel.this.getDescription() == null ? 1 : IssueAttachmentModel.this.getDescription().hashCode()));
    }

    public static abstract class IssueAttachmentModelComparator implements Comparator<IssueAttachmentModel> {
        protected boolean isAscending = true;

        public IssueAttachmentModelComparator() {
        }

        public IssueAttachmentModelComparator(boolean isAscending) {
            setAscending(isAscending);
        }

        public void setAscending(boolean value) {
            this.isAscending = value;
        }

        protected abstract int doComparison(IssueAttachmentModel ma, IssueAttachmentModel mb);

        public final int compare(IssueAttachmentModel a, IssueAttachmentModel b) {
            if(! (a instanceof IssueAttachmentModel) || ! (b instanceof IssueAttachmentModel)) {
                throw new ClassCastException();
            }

            IssueAttachmentModel ma = (IssueAttachmentModel) a;
            IssueAttachmentModel mb = (IssueAttachmentModel) b;

            int result = doComparison(ma, mb);
            if(! isAscending) {
                result = result * -1;
            }
            return result;
        }
    }

    public static class CompareByDate extends IssueAttachmentModelComparator {
        public CompareByDate(){
          super();
        }

        public CompareByDate(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(IssueAttachmentModel ma, IssueAttachmentModel mb) {
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

    public static class CompareByIssueId extends IssueAttachmentModelComparator {
        public CompareByIssueId(){
          super();
        }

        public CompareByIssueId(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(IssueAttachmentModel ma, IssueAttachmentModel mb) {
            if(ma.getIssueId() == null && mb.getIssueId() == null) {
                return 0;
            } else if(ma.getIssueId() == null) {
                return 1;
            } else if(mb.getIssueId() == null) {
                return -1;
            }

            if(ma.getIssueId().equals(mb.getIssueId())) {
                return ma.getId().compareTo(mb.getId());
            } else {
                return ma.getIssueId().compareTo(mb.getIssueId());
            }
        }
    }

    public static class CompareBySize extends IssueAttachmentModelComparator {
        public CompareBySize(){
          super();
        }

        public CompareBySize(boolean isAscending) {
          super(isAscending);
        }

        protected int doComparison(IssueAttachmentModel ma, IssueAttachmentModel mb) {
            if(ma.getIssueId() == null && mb.getIssueId() == null) {
                return 0;
            } else if(ma.getIssueId() == null) {
                return 1;
            } else if(mb.getIssueId() == null) {
                return -1;
            }

            if(ma.getSize() == mb.getSize()) {
                return ma.getIssueId().compareTo(mb.getIssueId());
            } else if(ma.getSize() > mb.getSize()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}