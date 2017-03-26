/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package dbUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

//import com.google.gwt.thirdparty.javascript.jscomp.FunctionInformationMap.Entry;

import clientInfo.Client;
import clientInfo.UserDataHolder;
import debugging.Debugging;
import debugging.profiling.RapidProfilingTimer;

/**
 * A serialization for objects SUBCLASSES MUST IMPLEMENT A NO ARGUMENT
 * CONSTRUCTOR
 * 
 * @author inhaler
 *
 */
public abstract class MaxObject {
	/*
	 * All variables and data structures
	 */
	public Map<String, Object> dbMap = new HashMap<String, Object>();
	public static Map<String, Class<?>> dbDatatypes = null;
	
	public static final Boolean USE_BETTER_DB_DATATYPES = true;
	
	public static HashMap<Class<? extends MaxObject>,HashMap<String,Class<?>>> betterDbDatatypes = new HashMap<>();
	
	
	/**
	 * A list that contains all the MaxFields to be passed into functions.
	 */
	public ArrayList<MaxField<?>> autoGenList = new ArrayList<MaxField<?>>();
	public Map<String,MaxField<?>> autoGenMap = new HashMap<String,MaxField<?>>();
	
	public MaxField<?> keyField;
	
	ArrayList<String> preparedListOrder = new ArrayList<String>();
	
	/*
	 * End variables and data structures
	 */
	
	/**
	 * Creates a sql statement to insert a MaxObject into a database.
	 * @return sql insertion string.
	 */
	public String getInsertValues() {
		// TODO: update this to use a custom SQL object class
		// with a special variables and a special function to get the key and
		// value
		// SQL representation
		//TODO: escape  commas in notes to prevent issues
		this.updateDBMap();
		String insertValues = " ";
		String keys = "(";
		String values = "(";
		Boolean firstLoop = true;
		Debugging.output("Generating insert values for: " + this + " " + dbMap.size(),Debugging.DATABASE_OUTPUT);
		for (String key : dbMap.keySet()) {
			Object value = dbMap.get(key);
			// special case for first loop (doesn't have a comma)
			if (!firstLoop) {
				keys += ", ";
				values += ", ";
			} else {
				firstLoop = false;
			}
			keys += key;

			if (value instanceof Date) {
				//special case for dates
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String modValue = sdf.format(value);
				Debugging.output("date: " + modValue,Debugging.DATABASE_OUTPUT);
				values += "'" + modValue + "'";
			} else if (value instanceof Boolean) {
				//MYSQL requires no quotes for a true/false value
					values +=  ""+value;
				
			} else {
				values += "'" + value + "'";
			}

		}
		keys += ")";
		values += ")";
		insertValues += keys + " VALUES " + values;
		return insertValues;
	}
	

	/**
	 * PREPARED statement version of insertion
	 * @return
	 */
	public String getPreparedValues() {
		// TODO: update this to use a custom SQL object class
		// with a special variables and a special function to get the key and
		// value
		// SQL representation
		//TODO: escape  commas in notes to prevent issues
		preparedListOrder.clear();
		
		this.updateDBMap();
		String insertValues = " ";
		String keys = "(";
		String values = "(";
		
		Boolean firstLoop = true;
		Debugging.output("Generating insert values for: " + this + " " + dbMap.size(),Debugging.DATABASE_OUTPUT);
		for (String key : dbMap.keySet()) {
			Object value = dbMap.get(key);
			// special case for first loop (doesn't have a comma)
			if (!firstLoop) {
				keys += ", ";
				values += ", ";
			} else {
				firstLoop = false;
			}
			keys += key;
			//adds a question mark for each value
			//To be used for prepared statements
			values += "?";
			preparedListOrder.add(key);

		}
		keys += ")";
		values += ")";
		insertValues += keys + " VALUES " + values;
		
		return insertValues;
	}
	
	public PreparedStatement setPreparedValues(PreparedStatement updateStatement) {
		// TODO: update this to use a custom SQL object class
		// with a special variables and a special function to get the key and
		// value
		// SQL representation
		//TODO: escape  commas in notes to prevent issues
		//this.updateDBMap();
		Debugging.output("Generating insert values for: " + this + " " + dbMap.size(),Debugging.DATABASE_OUTPUT);
		
		//Loops through the DB map in the order the statement was prepared.
		int currentValue = 1;
		for (String key : preparedListOrder) {
			Object value = dbMap.get(key);

			try {
			if (value instanceof Date) {
				//special case for dates
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String modValue = sdf.format(value);
				Debugging.output("date: " + modValue,Debugging.DATABASE_OUTPUT);
				//values += "'" + modValue + "'";
				//Experimental Date Storage
				//updateStatement.setDate(currentValue, (Date) value);
				updateStatement.setString(currentValue, modValue);
				
			} else if (value instanceof Boolean) {
				//MYSQL requires no quotes for a true/false value
					//values +=  ""+value;
				updateStatement.setBoolean(currentValue, (boolean) value);
			} else if (value instanceof Integer) {
				updateStatement.setInt(currentValue, (Integer) value);
			
			} else {
				//values += "'" + value + "'";
				updateStatement.setString(currentValue, (String) value);
			}
			} catch (Exception e) {
				
			}
			
			currentValue ++;
		}

		return updateStatement;
	}

	/**
	 * Attempts to load a value from the internal map, 
	 * if not found, use a specified default value
	 * 
	 * Useful when adding a new field to a class
	 * @param fieldName
	 * @param defaultValue
	 * @return (the field)
	 */
	@SuppressWarnings("unchecked")
	public <V> V safeLoadFromInternalMap(String fieldName, V defaultValue) {
		V item;
		if (dbMap.get(fieldName)!=null) {
			
			
			item = (V) dbMap.get(fieldName);
			Debugging.output(
					"Found Field: " + fieldName + " = " + item
					, Debugging.MAX_OBJECT_OUTPUT
					, Debugging.MAX_OBJECT_OUTPUT_ENABLED);
			
		} else {
			item = defaultValue;
			Debugging.output(
					"Failed to find Field: " + fieldName + " default value: " + item
					, Debugging.MAX_OBJECT_OUTPUT
					, Debugging.MAX_OBJECT_OUTPUT_ENABLED);
		}
		return item;
	}
	
	public void loadFromDB(ResultSet rs) {
		
		
		if (dbMap.keySet().size()==0){
			//load the db keys if not already loaded
			//this.setupDBDatatypes();
			this.updateDBMap();
		}
		
		
		Debugging.output("MaxObject.loadFromDB() expecting: " + dbMap.keySet().size() + " keys.",Debugging.DATABASE_OUTPUT);
		//for (String key : dbMap.keySet()) {
		
		Collection<String> keysToUse = null;
		if (USE_BETTER_DB_DATATYPES) {
			keysToUse = betterDbDatatypes.get(this.getClass()).keySet();
		} else {
			keysToUse = dbDatatypes.keySet();
		}
		
		for (String key : keysToUse) {
			Debugging.output("Key: " + key,Debugging.DATABASE_OUTPUT);
			try {
				Object value = rs.getObject(key);
				Debugging.output("Attempting to Load value: " + key + " , " + value,Debugging.DATABASE_OUTPUT);
				dbMap.put(key, value);
				Debugging.output("Loaded value: " + key + " , " + value + " into: " + dbMap,Debugging.DATABASE_OUTPUT);

			} catch (SQLException e) {
				
				Debugging.output("Error code: " + e.getErrorCode() +" SQL EXCEPTION!",Debugging.DATABASE_OUTPUT_ERROR);
				Debugging.output("Error Message: " + e.getMessage(),Debugging.DATABASE_OUTPUT_ERROR);
				if (e.getMessage().contains("Column") && e.getMessage().contains("not found.")) {
					//Special debugging
					//This should be fixed later
					Debugging.output("key not found: " + key + " in: " + this,Debugging.DATABASE_OUTPUT_ERROR);
					
				} else {
					e.printStackTrace();
				}
			}
		}
		loadInternalFromMap();

	}


	public abstract String getPrimaryKey();


	

	public void loadInternalFromMap() {
		this.autoGenLoadInternalFromMap(this.autoGenList);
	}

	public void updateDBMap() {
		this.autoGenUpdateDBMap(this.autoGenList);
	}

	
	/**
	 * MaxField default version
	 * creates the MaxDBTable that holds the data for this
	 * class and instances of the class. modifies an existing table
	 * to the new schema.
	 * 
	 * @param table- the table to set up.
	 */
	public void createTableForClass(MaxDBTable table) {
		this.autoGenCreateTableForClass(this.autoGenList, this.keyField, table);
	}
		public void setupDBDatatypes() {
		this.autoGenSetupDBTypes(this.autoGenList);
	}
	/**
	 * Gets all the dbMap Fields
	 * Please not these are NOT the MaxFields
	 * @return- the dbMap Keyset.
	 */
	public Collection<String> getFields() {
		return dbMap.keySet();
	}

	public UserDataHolder userDataHolder;
	/**
	 * sets the user data holder
	 * @param udh
	 */
	public void setUserDataHolder(UserDataHolder udh) {
		this.userDataHolder = udh;
		//loop through looking for conversions
		
		updateConversions();
		
		
	}



	/**
	 * @deprecated
	 * Loads data into dbMap from a csv file map.
	 * 
	 * @param entity
	 *            <field, object> format. ex: name,john smith
	 */
	public void loadFromCSV(Map<String, String> entity) {
		
		//TODO: comment and debug this method
		setupDBDatatypes();
		//setup the variables and their types. this must be done for every subclass (IE client/status)
		Debugging.output("MaxObject.loadFromCSV()",Debugging.DATABASE_OUTPUT);
		try {
			//go through every field, and get the respective name
			// [Name:Jessie] -> get all fields (Name,id,...ect)
			for (String key : entity.keySet()) {
				Debugging.output("Loading: " + key + " from csv with value: " + entity.get(key),Debugging.DATABASE_OUTPUT);

				//reference class, used to create an object
				Class<?> ref = dbDatatypes.get(key);

				Debugging.output("loaded ref: " + ref,Debugging.DATABASE_OUTPUT);
				Object obj;
				
				if (ref==null) {
					//null checking
					Debugging.output("Null value encountered.",Debugging.DATABASE_OUTPUT);
				}
				
				
				if (ref.equals(Integer.class)) {
					obj = 0;
				} else {

					obj = ref.newInstance();
				}
				//convert the string to the respective data type.
				try {
				if (obj.getClass() == String.class) {
					obj = entity.get(key);
				} else if (obj.getClass() == Integer.class) {
					obj = Integer.parseInt(entity.get(key));
				} else if (obj.getClass() == java.util.Date.class) {
					obj = Client.SIMPLE_DATE_FORMAT.parse(entity.get(key));
				}else {
					Debugging.output("Class: " + obj.getClass() + " WAS NOT PROGRAMED!",Debugging.DATABASE_OUTPUT);
				}

				dbMap.put(key, obj);
				} catch (Exception e) {
					//Data does not match expected input
					System.err.println("Bad data encountered in file, discarding line with item: " + entity.get(key));
				}

			}

			loadInternalFromMap();

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	
	/*
	 * ------------------------------------------------------------------------------------------------------------------
	 * Experimental CODE
	 * relating to MaxFields
	 */
	

	
	/**
	 * Adds a max field to the relevant data structures
	 * @param mf the Max Field to add.
	 */
	public void addMaxField(MaxField<?> mf) {
		autoGenList.add(mf);
		autoGenMap.put(mf.getFieldName(), mf);
	}
	
	public MaxField<?> getField(String fieldName) {
		
		return autoGenMap.get(fieldName);
	}
	
	public <T> void setFieldValue(String fieldName,T value) {
		autoGenMap.get(fieldName).setFieldValueUnsafe(value);
	}
	
	public void setKeyField(MaxField<?> key) {
		this.keyField=key;
	}
	
	public Collection<MaxField<?>> getAutoGenList() {
		return autoGenList;
	}
	
	/**
	 * Automatically sets up db datatypes based on a collection
	 * of maxFields
	 * @param maxFields
	 */
	public void autoGenSetupDBTypes(Collection<MaxField<?>> maxFields) {
		if (dbDatatypes ==null) {
			dbDatatypes = new HashMap<String, Class<?>>();
		}

		//This automates setting up dbDatatypes 
		
		for (MaxField<?> m : maxFields) {
			
				
				if (USE_BETTER_DB_DATATYPES) {
					if (!betterDbDatatypes.containsKey(this.getClass())) {
					betterDbDatatypes.put(this.getClass(), new HashMap<>());
					}
					betterDbDatatypes.get(this.getClass()).put(m.getFieldName(), m.getExtendedClass());
				} else {
					dbDatatypes.put(m.getFieldName(), m.getExtendedClass());
				}
		}
	}
	
	/**
	 * This will automatically update the DB Map based on values provided
	 * within the collection of MaxFields
	 * @param maxFields that provide actual data
	 */
	public void autoGenUpdateDBMap(Collection<MaxField<?>> maxFields) {
		
		for (MaxField<?> m : maxFields) {
			if (m.conversion==null) {
				Debugging.output("no conversion for: " + m, Debugging.CONVERSION_DEBUG2);
			dbMap.put(m.getFieldName(), m.getFieldValue());
			} else {
				Debugging.output("Conversion found for: " + m, Debugging.CONVERSION_DEBUG2);
				//load the converted value
				dbMap.put(m.getFieldName(), m.getConvertedFieldValue());
			}
		}
	}
	
	/**
	 * Loads the information from the internal map into the MaxFields
	 * 
	 * @param maxFields - collection to load from
	 * @return
	 */
	public Collection<MaxField<?>> autoGenLoadInternalFromMap(Collection<MaxField<?>> maxFields) {
		RapidProfilingTimer rpt = new RapidProfilingTimer("auto gen load internal");
		rpt.logTime();
		for (MaxField<?> m : maxFields) {
			if (m.getConversion()==null) {
				m.safeLoadValue(this);
			} else {
				//if a conversion is available, use it
				m.safeConversionLoad(this);
			}
			rpt.logTime();
			
		}
		
		return maxFields;
	}
	
	/**
	 * Generates the table for the class from the new collection of fields
	 * @param maxFields
	 * @param primaryKey - maxField to be used as the primary key
	 * @param table - the table to generate
	 * @return the table
	 */
	public MaxDBTable autoGenCreateTableForClass(Collection<MaxField<?>> maxFields,MaxField<?> primaryKey, MaxDBTable table) {
		
		for (MaxField<?> m : maxFields) {
			table.addDatatype(this.getClass(),m.getFieldName(), m.getFieldDBType());
		}
		table.setPrimaryKeyName(primaryKey.getFieldName());
		table.createTable();
		return table;
	}
	
	/**
	 * Updates the conversions to use the user data holder
	 */
	private void updateConversions() {
		for (MaxField<?> m : this.autoGenList) {
			if (m.conversion!=null) {
				m.conversion.setUserDataHolder(this.userDataHolder);
			}
		}
	}

	/**
	 * Gets a delete string for a Prepared Statement Query
	 * This method only works with classes upgraded to
	 * use MaxFields
	 * @return the prepared statement string
	 */
	public String getDeleteString() {
		if (keyField!=null){
			String deleteString = keyField.getFieldName()+ "= ? ";
			
			return deleteString;
		} else {
			return null;
		}
	}
	
	/**
	 * Sets the prepared statement paramters to delete
	 * the entitiy with this primary key.
	 * @param deletestatement
	 * @return
	 */
	public PreparedStatement setPreparedDeleteValues(PreparedStatement deletestatement) {
		
		if (keyField==null) {
			return null;
		}
		try {
			//TODO move this code to max field? 
			deletestatement.setString(1, (String) keyField.getFieldValue());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return deletestatement;
	}

}
