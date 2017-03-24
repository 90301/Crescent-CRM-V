/*
 * Copyright (C) Joshua Benton 2016. All Rights Reserved.
 */

package dbUtils;

import dbUtils.Conversions.MaxConversion;
import debugging.Debugging;
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
	
	String gridName;
	
	MaxConversion<?,?> conversion;
	
	
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
		
		//default value for grid name is just field name
		this.gridName = this.fieldName;
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
	
	public Class<?> getConversionExtendedClass() {
		if (fieldValue != null) {
			
			return conversion.getStoreClass();
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

	/**
	 * Load the value from the database, then convert it to
	 * the expected datatype
	 * @param maxObject
	 */
	@SuppressWarnings("unchecked")
	public <STORE> void safeConversionLoad(MaxObject maxObject) {
		STORE databaseLoadedValue = (STORE) maxObject.safeLoadFromInternalMap(this.fieldName, this.conversion.getDefaultStoreValue());
		
		Debugging.output("database Loaded Value: " + databaseLoadedValue.toString(), Debugging.CONVERSION_DEBUG2);
		
		T convertedValue = (T) ((MaxConversion<T,STORE>) this.conversion).convertToUse(databaseLoadedValue);
		
		this.setFieldValue(convertedValue);
	}
	
	@Override
	public String toString() {
		if (FULL_DEBUG_TOSTRING) {
		return "MaxField [fieldName=" + fieldName + ", fieldDBType=" + fieldDBType + ", fieldValue=" + fieldValue
				+ ", defaultFieldValue=" + defaultFieldValue + ", refClass=" + refClass + "]";
		} else {
			if (fieldValue==null) {
				return "NULL VALUE";
			} else {
			return fieldValue.toString();
			}
		}
		
	}
	

	public <STORE> Object getConvertedFieldValue() {
		
		
		Debugging.output(this.getFieldName() + " Conversion Class: " + this.conversion.getClass(), Debugging.CONVERSION_DEBUG2);
		
		
		//@SuppressWarnings("unchecked")
		Object convertedObject = ((MaxConversion<T,STORE>) this.conversion).convertToStore(this.getFieldValue());
		
		Debugging.output("Converted value: " + convertedObject, Debugging.CONVERSION_DEBUG2);
		return convertedObject;
	}


	
	
	/*
	 * Getters / Setters
	 */
	
	
	public MaxConversion<?, ?> getConversion() {
		return conversion;
	}

	public void setConversion(MaxConversion<?, ?> conversion) {
		this.conversion = conversion;
	}

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

	public String getGridName() {
		return gridName;
	}

	public void setGridName(String gridName) {
		this.gridName = gridName;
	}

	/**
	 * The more dangerous way of setting the field value
	 * @param value
	 */
	public void setFieldValueUnsafe(Object value) {
		this.setFieldValue((T) value); 
		
	}





	
	

}
