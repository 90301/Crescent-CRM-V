package dbUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//import com.google.gwt.thirdparty.javascript.jscomp.FunctionInformationMap.Entry;

import clientInfo.Client;
import clientInfo.UserDataHolder;

/**
 * A serialization for objects SUBCLASSES MUST IMPLEMENT A NO ARGUMENT
 * CONSTRUCTOR
 * 
 * @author inhaler
 *
 */
public abstract class MaxObject {
	
	
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
		System.out.println("Generating insert values for: " + this + " " + dbMap.size());
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
				System.out.println("date: " + modValue);
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

	public void loadFromDB(ResultSet rs) {
		
		
		if (dbMap.keySet().size()==0){
			//load the db keys if not already loaded
			//this.setupDBDatatypes();
			this.updateDBMap();
		}
		
		
		System.out.println("MaxObject.loadFromDB() expecting: " + dbMap.keySet().size() + " keys.");
		//for (String key : dbMap.keySet()) {
		for (String key : dbDatatypes.keySet()) {
			System.out.println("Key: " + key);
			try {
				Object value = rs.getObject(key);
				System.out.println("Attempting to Load value: " + key + " , " + value);
				dbMap.put(key, value);
				System.out.println("Loaded value: " + key + " , " + value + " into: " + dbMap);

			} catch (SQLException e) {
				
				System.err.println("Error code: " + e.getErrorCode() +" SQL EXCEPTION!");
				System.err.println("Error Message: " + e.getMessage());
				if (e.getMessage().contains("Column") && e.getMessage().contains("not found.")) {
					//Special debugging
					//This should be fixed later
					System.err.println("key not found: " + key + " in: " + this);
					
				} else {
					e.printStackTrace();
				}
			}
		}
		loadInternalFromMap();

	}

	public abstract void loadInternalFromMap();

	public abstract void updateDBMap();

	public abstract String getPrimaryKey();

	public Map<String, Object> dbMap = new HashMap<String, Object>();
	// TODO: translation table for databases
	// TODO: add dbDatatypes support to all max objects
	public static Map<String, Class<?>> dbDatatypes = null;

	/**
	 * MUST BE OVERRIDEN TO CREATE A TABLE FOR THE SUBCLASS
	 * 
	 * @param table
	 */
	public abstract void createTableForClass(MaxDBTable table);

	public Collection<String> getFields() {
		return dbMap.keySet();
	}

	public abstract void setupDBDatatypes();

	public UserDataHolder userDataHolder;
	/**
	 * sets the user data holder
	 * @param udh
	 */
	public void setUserDataHolder(UserDataHolder udh) {
		this.userDataHolder = udh;
	}
	/**
	 * Loads data into dbMap from a csv file map.
	 * 
	 * @param entity
	 *            <field, object> format. ex: name,john smith
	 */
	public void loadFromCSV(Map<String, String> entity) {
		
		//TODO: comment and debug this method
		setupDBDatatypes();
		//setup the variables and their types. this must be done for every subclass (IE client/status)
		System.out.println("MaxObject.loadFromCSV()");
		try {
			//go through every field, and get the respective name
			// [Name:Jessie] -> get all fields (Name,id,...ect)
			for (String key : entity.keySet()) {
				System.out.println("Loading: " + key + " from csv with value: " + entity.get(key));

				//reference class, used to create an object
				Class<?> ref = dbDatatypes.get(key);

				System.out.println("loaded ref: " + ref);
				Object obj;
				
				if (ref==null) {
					//null checking
					System.out.println("Null value encountered.");
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
					System.out.println("Class: " + obj.getClass() + " WAS NOT PROGRAMED!");
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

}
