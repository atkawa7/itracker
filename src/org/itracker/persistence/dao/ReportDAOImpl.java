package org.itracker.persistence.dao;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.itracker.model.Report;
 
/**
 * 
 */
public class ReportDAOImpl extends BaseHibernateDAOImpl<Report> implements ReportDAO {

    public Report findByPrimaryKey(Integer id) {
        try {
            return (Report)getSession().get(Report.class, id);
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Report> findAll() {
        final List<Report> reports;
        
        Criteria criteria = getSession().createCriteria(Report.class);
        try {
            reports = criteria.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return reports;
    }

}
