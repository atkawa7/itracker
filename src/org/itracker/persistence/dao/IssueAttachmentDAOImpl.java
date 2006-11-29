package org.itracker.persistence.dao;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Expression;
import org.itracker.model.IssueAttachment;

/**
 * Persistence Hibernate POJO
 * 
 * @author mbae, ready
 */
public class IssueAttachmentDAOImpl extends BaseHibernateDAOImpl<IssueAttachment> 
        implements IssueAttachmentDAO {
	
    public IssueAttachment findByPrimaryKey(Integer attachmentId) {
        try {
            return (IssueAttachment) (getSession().load(IssueAttachment.class, attachmentId));
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }
    
    public IssueAttachment findByFileName(String fileName) {
        try {
            return ((IssueAttachment) getSession()
                .createCriteria(IssueAttachment.class)
                .add(Expression.eq("fileName", fileName)).uniqueResult());
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<IssueAttachment> findAll() {
        try {
            return  getSession().createCriteria(IssueAttachment.class).list();
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }
}
