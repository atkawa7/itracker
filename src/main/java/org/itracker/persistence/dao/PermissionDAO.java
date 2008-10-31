package org.itracker.persistence.dao;

import java.util.List;

import org.itracker.model.Permission;

/**
 * 
 */
public interface PermissionDAO extends BaseDAO<Permission> {

    /**
     * Finds all Permissions granted to a user. 
     * 
     * @param userId 
     * @return list of permissions granted to the given user, in unspecified order
     */
    List<Permission> findByUserId(Integer userId);
    
    /**
     * Finds all Permissions of a given type granted on a project. 
     * 
     * @param projectId only permissions on this project will be returned
     * @param permissionType type of permissions to return 
     * @return list of permissions, in unspecified order
     */
    List<Permission> findByProjectIdAndPermission(Integer projectId, 
            int permissionType);

}
