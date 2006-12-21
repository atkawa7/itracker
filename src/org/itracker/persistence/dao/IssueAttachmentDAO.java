package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.model.IssueAttachment;

/**
 * 
 */
public interface IssueAttachmentDAO extends BaseDAO<IssueAttachment> {

    /**
     * 
     * 
     * @param attachmentId system ID
     * @return attachment instance or <tt>null</tt> if none exists
     */
    IssueAttachment findByPrimaryKey(Integer attachmentId) ;

    /**
     * Finds an attachment by the file name on the filesystem.
     * 
     * @param fileName 
     * @return attachment instance or <tt>null</tt> if none exists
     */
    IssueAttachment findByFileName(String fileName);
    
    /**
     * Finds all Issue attachments. 
     * 
     * @return list of all issue attachments, in unspecified order
     */
    List<IssueAttachment> findAll();
    
    /**
     * Finds all attachments for an Issue. 
     * 
     * @param issueId system ID
     * @return list of all issue attachments, in unspecified order
     */
    List<IssueAttachment> findByIssue(Integer issueId);
    
}
