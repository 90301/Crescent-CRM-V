/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package dbUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import debugging.Debugging;

//import org.yaml.snakeyaml.introspector.PropertyUtils;

/**
 * Holds information related to a database
 * Creates a database and holds the connector types
 *  V0.11
 * @author inhaler
 *
 */
public class MaxDB extends MaxDBTopLevel {
	
	public static final String CREATE_DB_SQL = "CREATE DATABASE ";
	
	
	protected String dbName;
	
	protected String dbConnectionString;
	
	protected Connection dbConnection;
	/**
	 * Default Constructor
	 * Sets DB type to mySql database
	 */
	public MaxDB() {
		this.databaseType = DB_TYPE_MYSQL;
	}
	
	@Override
	public String toString() {
		return "MaxDB [dbName=" + dbName + ", dbConnectionString=" + dbConnectionString + ", dbConnection="
				+ dbConnection + "]";
	}

	public MaxDB(int databaseType) {
		this.databaseType = databaseType;
	}
	
	public MaxDB(String dbName, int dbType) {
		this.databaseType = dbType;
		this.dbName = dbName;
	}
	public void topLevelTransfer(MaxDBTopLevel topLevel) {
		this.databaseType = topLevel.databaseType;
		this.host = topLevel.host;
		this.pass = topLevel.pass;
		this.port = topLevel.port;
	}
	/**
	 * Creates the database if not already created.
	 * This shouldn't wipe a database
	 */
	public void createDB() {
		this.connectTopLevel();
		try {
			Statement createDBStatement = topLevelConnection.createStatement();
			createDBStatement.execute(CREATE_DB_SQL + dbName);
		    
		} catch (SQLException e) {
			
			if (!e.getMessage().contains("exist")) {
			e.printStackTrace();
			} else {
				Debugging.output("database: " + dbName + " Already exists!",Debugging.DATABASE_OUTPUT);
			}
		}
	}
	/**
	 * checks if the connection is still alive, if not it calls
	 * connectDB();
	 */
	public void checkOrEstablishConnection() {
		try {
			if (dbConnection.isValid(0)) {
				Debugging.output("Database connection already established.",Debugging.DATABASE_OUTPUT);
				return;
			} else {
				Debugging.output("Connection not valid, reconnecting.",Debugging.DATABASE_OUTPUT);
				connectDB();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public void genDBConnectionString() {
		genTopLvlConnectionString();
		dbConnectionString = topConnectionString+ dbName;
	}
		
	
	public Boolean connectDB() {
		Boolean sucsess = false;
		genDBConnectionString();
		Debugging.output("Connecting to: " + this,Debugging.DATABASE_OUTPUT);
		try {
			dbConnection = DriverManager.getConnection(dbConnectionString, user, pass);
			 sucsess = true;
			 Debugging.output("connected to: " +this +" sucsessfully!",Debugging.DATABASE_OUTPUT);
		} catch (SQLException e) {
			//failed to connect
			System.err.println("failed to connect to: " +this);
			e.printStackTrace();
		}
		return sucsess;
	}
	/**
	 * Attempts to close the database
	 * @return if no exception was thrown.
	 */
	public Boolean closeDB() {
		Boolean sucsess = false;
		try {
			dbConnection.close();
			sucsess = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sucsess;
	}
	
}
