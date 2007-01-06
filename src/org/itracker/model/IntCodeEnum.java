package org.itracker.model;

/**
 * An interface to be implemented by java.lang.Enum classes that need 
 * to associate a unique and constant integer code to their enum constants. 
 * 
 * <p>The main use case is to allow to persist an enum constant to 
 * an integer rather than a string value, which is supported directly 
 * by the java.lang.Enum class through the enum.name() and enum.valueOf(String)
 * methods. <br>
 * The enum.ordinal() position could be used, but isn't the best approach for 
 * this use case because we don't have any constrol on it : 
 * it is zero-based and changes if the position of the enum constant changes. 
 * <br>
 * Using the enum name isn't satisfactory either because we would have 
 * to update the database if we ever need to rename an enum constant. </p> 
 * 
 * <p>This class allows to migrate to Java 5 enums retaining full backwards 
 * compatiblity with iTracker 2, in which all enumerations were simply defined 
 * as <code>static final int</code> fields. </p>
 * 
 * <p>This interface allows to handle all such enums consistently  
 * and to use a single Hibernate custom type to persist them all. </p>
 * 
 * @author johnny
 */
public interface IntCodeEnum<E extends Enum<E>> {
    
    /**
     * Returns the integer value representing this enum constant. 
     * 
     * @return unique constant as defined in iTracker 2
     */
    int getCode();
    
    /**
     * Returns a java.lang.Enum constant matching the given integer value.
     * 
     * @param code unique enum constant as defined in iTracker 2
     * @return java.lang.Enum constant instance for the given code
     * @throws IllegalArgumentException no matching enum constant for 
     *         the given <code>code</code>  
     */
    E fromCode(int code);
    
}
