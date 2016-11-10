/*
 * Copyright (C) Joshua Benton 2016. All Rights Reserved.
 */

package dbUtils;

/**
 * This is an <b>experimental</b> class that will hold everything
 * required for a MaxObject field (aka piece of data stored in a Max Object)
 * and contains methods to generate the required function calls of a MaxObject
 * 
 * This makes it so you can use this class instead of manually calling the specific function calls required.
 * @author Joshua Benton
 *
 */
public class MaxField<T> {
	
	String fieldName;
	String fieldDBType;
	T fieldValue;
	T defaultFieldValue;
	
	
	
	/**
	 * Sets up the field. This is intended to be the only constructor
	 * as it will require all values to be entered.
	 * @param fieldName - the name of the field (used in a MaxObject's DbMap)
	 * @param fieldDBType - The database type
	 * @param fieldValue - The value of the field (subject to change over time)
	 */
	public MaxField(String fieldName, String fieldDBType, T fieldValue, T defaultFieldValue) {
		super();
		this.fieldName = fieldName;
		this.fieldDBType = fieldDBType;
		this.fieldValue = fieldValue;
		this.defaultFieldValue = defaultFieldValue;
		
	}

	/*
	 * Special Methods
	 */


	public Class<T> getExtendedClass() {
		if (fieldValue != null) {
		
		return (Class<T>) fieldValue.getClass();
		} else {
			return null;
		}
	}
	
	/**
	 * Loads a value from a Max Object's internal map
	 * 
	 * A safe load method due to Java Language limitations.
	 * This method should only be called when loading from an internal map
	 * in a MaxObject
	 * @param maxObject - the max object to pull the value from
	 */
	public void safeLoadValue(MaxObject maxObject) {
		T loadedValue = maxObject.safeLoadFromInternalMap(this.fieldName, this.defaultFieldValue);
		this.setFieldValue(loadedValue);
	}
	
	
	
	/*
	 * Getters / Setters
	 */

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldDBType() {
		return fieldDBType;
	}

	public void setFieldDBType(String fieldDBType) {
		this.fieldDBType = fieldDBType;
	}

	public T getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(T fieldValue) {
		this.fieldValue = fieldValue;
	}

	public T getDefaultFieldValue() {
		return defaultFieldValue;
	}

	public void setDefaultFieldValue(T defaultFieldValue) {
		this.defaultFieldValue = defaultFieldValue;
	}



	
	

}
