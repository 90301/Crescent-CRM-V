package dbUtils;

import java.sql.*;
import java.util.Properties;

import com.mysql.*;
import com.mysql.jdbc.Driver;
public class MaxDBTopLevel {
	//DATABASE TYPES
	
	public static final String BASE_CONNECTION = "jdbc";
	public static final int DB_TYPE_MYSQL = 0;
	public static final int DB_TYPE_POSTGRESQL = 1;
	public static final String[] CONNECTION_STRINGS = {
			"mysql"
			,"postgresql"};
	
	protected String host;
	protected int port;
	protected String user = "root";
	protected String pass = "";
	protected int databaseType;
	
	//Dynamically generated, stored for ease of use.
	protected String topConnectionString;
	protected Connection topLevelConnection;
	
	private static final boolean USE_PORT = false;
	
	
	public MaxDBTopLevel() {
		
	}
	
	public MaxDBTopLevel(int databaseType) {
		this.databaseType = databaseType;
	}
	
	/**
	 * Sets the variables to establish a database connection.
	 * this must be done before connecting.
	 * @param host
	 * @param port
	 * @param user
	 * @param pass
	 */
	public void setConnectionInfo(String host, int port, String user, String pass) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.pass = pass;
	}
	
	/**
	 *  generates a connection string.
	 *  automatically called before attempting a connection to a database.
	 */
	public void genTopLvlConnectionString() {
		if (USE_PORT) {
		topConnectionString = BASE_CONNECTION + ":" + CONNECTION_STRINGS[databaseType] + "://" + host + ":" + port  + "/";
		} else {
			topConnectionString = BASE_CONNECTION + ":" + CONNECTION_STRINGS[databaseType] + "://" + host + "/";
		}
	}
	
	/**
	 * Attempts to connect to the top level database.
	 * @return if the connection succeeded. 
	 */
	public Boolean connectTopLevel() {
		Boolean sucsess = false;
		genTopLvlConnectionString();
		try {
			Driver driver = new com.mysql.jdbc.Driver();
			Properties props = new Properties();

			driver.connect("localhost", props);
			DriverManager.registerDriver(driver);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			topLevelConnection = DriverManager.getConnection(topConnectionString, user, pass);
			 sucsess = true;
		} catch (SQLException e) {
			//failed to connect
			e.printStackTrace();
		}
		return sucsess;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public int getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(int databaseType) {
		this.databaseType = databaseType;
	}
	
	
}
