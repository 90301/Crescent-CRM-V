/*
 * Copyright (C) Joshua Benton 2016. All Rights Reserved.
 */

package dbUtils;

import inventory.InventoryItem;

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
	
	private static final boolean FULL_DEBUG_TOSTRING = false;
	String fieldName;
	String fieldDBType;
	T fieldValue;
	T defaultFieldValue;
	MaxObject refClass;
	Boolean showField = true;
	
	
	/**
	 * Sets up the field. This is intended to be the only constructor
	 * as it will require all values to be entered.
	 * @param fieldName - the name of the field (used in a MaxObject's DbMap)
	 * @param fieldDBType - The database type
	 * @param fieldValue - The value of the field (subject to change over time)
	 * @param refClass 
	 */
	public MaxField(String fieldName, String fieldDBType, T fieldValue, T defaultFieldValue, MaxObject refClass) {
		super();
		this.fieldName = fieldName;
		this.fieldDBType = fieldDBType;
		this.fieldValue = fieldValue;
		this.defaultFieldValue = defaultFieldValue;
		this.refClass = refClass;
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
		
		
		//NOTE: this may cause a bug if the internal map is updated
		this.setFieldValue(loadedValue);
		
	}
	
	@Override
	public String toString() {
		if (FULL_DEBUG_TOSTRING) {
		return "MaxField [fieldName=" + fieldName + ", fieldDBType=" + fieldDBType + ", fieldValue=" + fieldValue
				+ ", defaultFieldValue=" + defaultFieldValue + ", refClass=" + refClass + "]";
		} else {
			return fieldValue.toString();
		}
		
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

	/**
	 * sets the new value of the field
	 * TODO make the database calls more efficient.
	 * @param fieldDBType
	 */
	public void setFieldDBType(String fieldDBType) {
		this.fieldDBType = fieldDBType;
		refClass.updateDBMap();
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

	public Boolean getShowField() {
		return showField;
	}

	public void setShowField(Boolean showField) {
		this.showField = showField;
	}

	/**
	 * The more dangerous way of setting the field value
	 * @param value
	 */
	public void setFieldValueUnsafe(Object value) {
		this.setFieldValue((T) value); 
		
	}



	
	

}
