package org.itracker.persistence.dao;

import java.util.List;
import org.itracker.model.Version;

/**
 * 
 */
public interface VersionDAO extends BaseDAO<Version> {

    public Version findByPrimaryKey(Integer targetVersionId) ;

    public List<Version> findByProjectId(Integer projectId) ;

}
