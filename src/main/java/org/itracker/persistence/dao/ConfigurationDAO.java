package org.itracker.persistence.dao;

import org.itracker.model.Configuration;

import java.util.List;

/**
 *
 */
public interface ConfigurationDAO extends BaseDAO<Configuration> {

    public Configuration findByPrimaryKey(Integer id);

    public List<Configuration> findByType(int type);

    public List<Configuration> findByTypeAndValue(int type, String value);

}