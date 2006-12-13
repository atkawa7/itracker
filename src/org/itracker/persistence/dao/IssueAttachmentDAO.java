package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.model.IssueAttachment;

/**
 * 
 */
public interface IssueAttachmentDAO extends BaseDAO<IssueAttachment> {

    public IssueAttachment findByPrimaryKey(Integer attachmentId) ;

    public IssueAttachment findByFileName(String fileName);

    public List<IssueAttachment> findAll();
}
