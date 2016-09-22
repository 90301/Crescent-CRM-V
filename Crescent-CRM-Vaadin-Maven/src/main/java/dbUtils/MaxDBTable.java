package dbUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This is a class that handle a specific table If this is the only table in the
 * program the entire database connection needs to be set up here
 * 
 * @author bentonjc
 *
 */
public class MaxDBTable extends MaxDB {

	protected MaxDB parentDB;
	// Stores information as a key (variable name) value (variable type)
	// TODO: make this ordered
	HashMap<String, String> dataTypes = new HashMap<String, String>();
	protected String tableName;
	protected String tableString;
	protected String primaryKeyName;
	// public Connection tableConnection;

	public static final String BASE_CREATE_TABLE = "CREATE TABLE ";

	public static final String DATA_MYSQL_TYPE_INT = "int";
	public static final String DATA_MYSQL_TYPE_DOUBLE = "real";
	public static final String DATA_MYSQL_TYPE_FLOAT = "float";
	public static final String DATA_MYSQL_TYPE_BOOLEAN = "bool";
	public static final String DATA_MYSQL_TYPE_DATE_TIME = "datetime";
	public static final String DATA_MYSQL_TYPE_STRING = "text";
	public static final String DATA_MYSQL_TYPE_KEY_STRING = "varchar(64)";
	public static final String DATA_MYSQL_TYPE_HUGE_KEY_STRING = "varchar(255)";
	private static final String PRIMARY_KEY = " NOT NULL PRIMARY KEY";
	private static final String BASE_TABLE_UPGRADE = "ALTER TABLE ";
	
	// TODO: create a class to handle database datatypes.

	public MaxDBTable() {
		// TODO Auto-generated constructor stub
	}

	public void addDatatype(String name, String datatype) {

		this.dataTypes.put(name, datatype);
	}

	/**
	 * A safer (but slower) way of adding a datatype.
	 * 
	 * @param name
	 * @param datatype
	 */
	public void addDatatypeSafe(String name, String datatype) {
		addDatatype(name, datatype);
	}

	public MaxDBTable(String tableName) {
		this.tableName = tableName;
	}

	public void setParentDB(MaxDB parentDB) {
		this.parentDB = parentDB;
		populateFromParentDB();
	}

	// populate information with parent db information
	public void populateFromParentDB() {
		try {
			this.databaseType = parentDB.databaseType;
			this.dbName = parentDB.dbName;
			this.host = parentDB.host;
			this.port = parentDB.port;
			this.user = parentDB.user;
			this.pass = parentDB.pass;
			this.genTopLvlConnectionString();
			this.genDBConnectionString();
			if (parentDB.dbConnection == null) {
				parentDB.connectDB();
			}
			this.dbConnection = parentDB.dbConnection;

			this.topLevelConnection = parentDB.topLevelConnection;// May be null
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * attempts to create a table
	 * 
	 * @return if a NEW table was created
	 */
	public Boolean createTable() {

		checkOrEstablishConnection();

		Boolean sucsess = false;
		String tableCreateString = BASE_CREATE_TABLE + tableName + " (";
		List<String> tableUpgradeStrings = new ArrayList<String>();
		
		Boolean firstRun = true;
		for (String name : dataTypes.keySet()) {
			String datatype = dataTypes.get(name);
			if (!firstRun) {
				tableCreateString += ", ";
			} else {
				firstRun = false;
			}
			tableCreateString += name + " " + datatype;
			if (name.equals(primaryKeyName)) {
				tableCreateString += PRIMARY_KEY;
			}
			//TABLE UPGRADE
			tableUpgradeStrings.add(BASE_TABLE_UPGRADE + tableName + " ADD " + name + " " + datatype + ";");
			
			
		}
		tableCreateString += ");";

		System.out.println("Running SQL Command: ");
		System.out.println(tableCreateString);

		try {
			Statement createTableStatement = dbConnection.createStatement();
			createTableStatement.execute(tableCreateString);
			sucsess = true;
		} catch (SQLException e) {
			if (e.getMessage().contains("exists")) {

			} else {
				e.printStackTrace();
			}
		}
		//Database Upgrade
		
		//loop through attempting to add any field not found. try catch must be inside loop
		for(String upgradeQuery : tableUpgradeStrings) {
			try {
				
				System.out.println("Executing SQL Query: " + upgradeQuery);
				Statement createTableStatement = dbConnection.createStatement();
				createTableStatement.execute(upgradeQuery);
				sucsess = true;
			} catch (SQLException e) {
				if (e.getMessage().toLowerCase().contains("exists") || e.getMessage().toLowerCase().contains("duplicate")) {

				} else {
					e.printStackTrace();
				}
			}
		}
		

		return sucsess;
	}

	/**
	 * inserts or replaces an object in the database table
	 * 
	 * @param obj
	 *            the object that goes in the table
	 * @return was the operation sucsessful
	 */
	public Boolean insertInTable(MaxObject obj) {

		checkOrEstablishConnection();

		System.out.println("Adding: " + obj + " TO: " + this);

		Boolean sucsess = false;

		String insertString = "REPLACE INTO " + tableName;
		insertString += obj.getInsertValues();
		insertString += ";";

		System.out.println(insertString);
		Statement insertStatement = null;
		try {
			insertStatement = dbConnection.createStatement();

			insertStatement.execute(insertString);

			sucsess = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				insertStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return sucsess;

	}
	
	public Boolean addTableColumn(String tableColmnName,String dataType) {
		Boolean sucsess = false;
		
		checkOrEstablishConnection();
		
		
		
		
		return sucsess;
	}

	/**
	 * gets all rows for the table
	 * 
	 * @return a result set of a SELECT * on that table.
	 */
	public ResultSet getAllRows() {
		ResultSet rs = null;
		
		System.out.println("Getting all rows for : " + this);
		// TODO: check if connection is alive, if not create connection.
		checkOrEstablishConnection();
		Statement queryStatement = null;
		try {
			queryStatement = dbConnection.createStatement();
			String queryString = "SELECT * FROM " + tableName + ";";
			System.out.println(queryString);

			rs = queryStatement.executeQuery(queryString);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		/*
		finally {
			try {
				queryStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/

		return rs;

	}

	public MaxDB getParentDB() {
		return parentDB;
	}

	public HashMap<String, String> getDataTypes() {
		return dataTypes;
	}

	public void setDataTypes(HashMap<String, String> dataTypes) {
		this.dataTypes = dataTypes;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getPrimaryKeyName() {
		return primaryKeyName;
	}

	public void setPrimaryKeyName(String primaryKeyName) {
		this.primaryKeyName = primaryKeyName;
	}

}
