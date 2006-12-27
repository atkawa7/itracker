package org.itracker.persistence.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.itracker.model.PermissionType;
import org.itracker.model.User;

/**
 * User Data Access Object interface.
 * 
 * @author ?
 * @author Johnny Macchione
 */
public interface UserDAO extends BaseDAO<User> {
    
    /**
     * Finds the users with the given primary key. 
     * 
     * PENDING: should this method throw a NoSuchEntityException  
     * instead of returning null if the userId doesn't exist ? 
     * 
     * @param userId ID of the user to retrieve
     * @return user with the given ID or <tt>null</tt> if none exits
     */
    User findByPrimaryKey(Integer userId);
    
    /**
     * Finds a user by login. 
     * 
     * @param login login name of the user to find
     * @return user with the given login or <tt>null</tt> if login is unknown
     */
    User findByLogin(String login);

    /**
     * Finds all users. 
     * 
     * @return list of all users, in unspecified order
     */
    List<User> findAll();

    /**
     * Finds all active users. 
     * 
     * @return list of users with a status &gt; 0, in unspecified order
     */
    List<User> findActive();

    /**
     * Finds users with the given status. 
     * 
     * @param status status code 
     * @return list of users with the given status, in unspecified order
     */
    List<User> findByStatus(int status);
    
    /**
     * Finds all super users.
     * 
     * @return list of super users, in unspecified order
     */
    List<User> findSuperUsers();

    /**
     * Finds users with the given registration type.
     *
     * @param registrationType 
     * @return list of users with the given registration type, in unspecified order
     */
    List<User> findByRegistrationType(int registrationType);
    
    /**
     * Finds of the given user the set of permission types for all projects 
     * on which the user has permissions. 
     * 
     * @param user 
     * @param sourceRequest 
     * @return set of permission types mapped by project id
     */
    Map<Integer, Set<PermissionType>> getUsersMapOfProjectsAndPermissionTypes(User user, int sourceRequest);
    
}
