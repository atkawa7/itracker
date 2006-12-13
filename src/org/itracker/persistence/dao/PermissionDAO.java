package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.model.Permission;

/**
 * 
 */
public interface PermissionDAO extends BaseDAO<Permission> {

    public List<Permission> findByProjectIdAndPermission(Integer projectId, int permissionType);

    public List<Permission> findByUserId(Integer id);
}
