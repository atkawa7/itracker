package org.itracker.persistence.dao;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.itracker.model.Report;
 
/**
 * 
 */
public class ReportDAOImpl extends BaseHibernateDAOImpl<Report> 
        implements ReportDAO {

    public Report findByPrimaryKey(Integer id) {
        Report report;
        
        try {
            report = (Report)getSession().get(Report.class, id);
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return report;
    }

    @SuppressWarnings("unchecked")
    public List<Report> findAll() {
        final List<Report> reports;
        
        try {
            Query query = getSession().getNamedQuery("ReportsAllQuery");
            reports = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return reports;
    }

}
