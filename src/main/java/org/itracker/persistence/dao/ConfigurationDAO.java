package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.model.Configuration;

/**
 * 
 */
public interface ConfigurationDAO extends BaseDAO<Configuration> {

    public Configuration findByPrimaryKey(Integer id);

    public List<Configuration> findByType(int type);

    public List<Configuration> findByTypeAndValue(int type, String value);

}