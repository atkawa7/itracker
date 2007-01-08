package org.itracker.persistence.dao;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;

/**
 * Custom Hibernate UserType to persist a Java 5 enum constant as an INTEGER
 * using its ordinal position. 
 * 
 * <p>Beware that the enum.ordinal() returns a is zero based that changes 
 * if the position of the enum members change! </p>
 * 
 * @author johnny
 */
public final class EnumOrdinalUserType extends AbstractEnumUserType {
    
    private static final int[] SQL_TYPES = { Types.SMALLINT };
    
    /** Enum members, in the order they where declared. */
    private Enum[] enumValues;
    
    /** 
     * Default constructor, required by Hibernate. 
     */
    public EnumOrdinalUserType() {
    }

    public void setParameterValues(Properties parameters) {
        super.setParameterValues(parameters);
        this.enumValues = this.enumClass.getEnumConstants();
    }

    public Object nullSafeGet(ResultSet rs, String[] names,
            Object owner) throws HibernateException, SQLException {
        final int ordinal = rs.getInt(names[0]);
        
        return rs.wasNull() ? null : this.enumValues[ordinal];
    }

    public void nullSafeSet(PreparedStatement stmt, Object value,
            int index) throws HibernateException, SQLException {
        if (value == null) {
            stmt.setNull(index, Types.INTEGER);
        } else {
            stmt.setInt(index, ((Enum) value).ordinal());
        }
    }

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public String objectToSQLString(Object value) {
        return Integer.toString(((Enum) value).ordinal());
    }
    
    public String toXMLString(Object value) {
        return objectToSQLString(value);
    }
    
    public Object fromXMLString(String xmlValue) {
        final int ordinal;
        
        try {
            ordinal = Integer.parseInt(xmlValue);
        } catch (NumberFormatException ex) {
            throw new HibernateException(ex);
        }
        return this.enumValues[ordinal];
    }
    
}
