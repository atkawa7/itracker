package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.model.Report;

/**
 * 
 */
public interface ReportDAO extends BaseDAO<Report> {
    
    public Report findByPrimaryKey(Integer id);

    public List<Report> findAll();
}
