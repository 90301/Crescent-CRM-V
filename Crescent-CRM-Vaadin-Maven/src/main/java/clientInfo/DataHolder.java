/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package clientInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import dbUtils.*;
import debugging.Debugging;
import debugging.profiling.ProfilingTimer;
import users.User;

/**
 * Handles all the data-storage.
 * 
 * @author inhaler
 *
 */
@SuppressWarnings("unused")
public class DataHolder {

	//Will be static after the transition
	private static ConcurrentHashMap<String, User> localUserMap = new ConcurrentHashMap<String, User>();
	private static ConcurrentHashMap<String, UserDataHolder> userDataHolderMap = new ConcurrentHashMap<String,UserDataHolder>();
	
	private static ConcurrentHashMap<String, ScheduleEvent> localScheduleEventMap = new ConcurrentHashMap<String, ScheduleEvent>();
	
	
	static final boolean writeCredentials = false;
	static final String credentialsFile = "credentials.dat";
	static final boolean readCredentials = false;

	private static final String DB_LOCATION = "localhost";
	private static final int DB_PORT = 3306;
	private static final String DB_USER = "ccrmUser";
	private static final String DB_PASS = "ccrmPass";
	private static final String DB_DATABASE_NAME = "ccrm";
	static String dbUser = "test";
	static String dbPass = "test";

	static MaxDB mysqlDatabase;

	//Persistant (user table)
	static MaxDBTable userTable,userDataHolderTable, scheduleEventTable;
	public static String USER_TABLE_TITLE = "usersTable";
	public static String USER_DATA_HOLDER_TABLE_TITLE = "userDataHolderTable";
	public static String SCHEDULE_EVENT_TABLE_TITLE = "scheduleEventTable";
	
	static ConcurrentMap<Class<? extends MaxObject>, ConcurrentHashMap<String, ? extends MaxObject>> localMapLookup;
	static ConcurrentMap<Class<? extends MaxObject>, MaxDBTable> tableLookup;

	public static final String TEMPLATE_STRING = "[[TEMPLATE]]";
	public static final boolean MULTI_USER_MODE = true;
	public static Client templateClient;
	
	
	
	/**
	 * Set up all the static databases.
	 * This will change later.
	 */
	public static void initalizeDatabases() {

		/*
		 * Set up the database(s)
		 */
		mysqlDatabase = new MaxDB(DB_DATABASE_NAME, MaxDB.DB_TYPE_MYSQL);
		mysqlDatabase.setConnectionInfo(DB_LOCATION, DB_PORT, DB_USER, DB_PASS);
		mysqlDatabase.createDB();
		mysqlDatabase.connectDB();
		Debugging.output("MYSQL DATABASE: " + mysqlDatabase,Debugging.DATABASE_OUTPUT);

		/*
		 * Set up the tables
		 */

		userTable = setupDatabaseTable(userTable, USER_TABLE_TITLE, mysqlDatabase, User.class);
		userDataHolderTable = setupDatabaseTable(userDataHolderTable, USER_DATA_HOLDER_TABLE_TITLE, mysqlDatabase, UserDataHolder.class);
		scheduleEventTable = setupDatabaseTable(scheduleEventTable, SCHEDULE_EVENT_TABLE_TITLE, mysqlDatabase, ScheduleEvent.class);
		

		loadMaxObjects(localUserMap, userTable, User.class);
		loadMaxObjects(userDataHolderMap, userDataHolderTable, UserDataHolder.class);
		loadMaxObjects(localScheduleEventMap , scheduleEventTable, ScheduleEvent.class);

		/*
		 * SET UP GENERIC MAPS
		 */
		localMapLookup = new ConcurrentHashMap<Class<? extends MaxObject>, ConcurrentHashMap<String, ? extends MaxObject>>();

		localMapLookup.put(User.class, localUserMap);
		localMapLookup.put(UserDataHolder.class, userDataHolderMap);
		localMapLookup.put(ScheduleEvent.class, localScheduleEventMap);

		tableLookup = new ConcurrentHashMap<Class<? extends MaxObject>, MaxDBTable>();

		tableLookup.put(User.class, userTable);
		tableLookup.put(UserDataHolder.class, userDataHolderTable);
		tableLookup.put(ScheduleEvent.class, scheduleEventTable);
		// OUTPUT GENERIC Maps

		Debugging.output("CREATED LOCAL LOOKUPS" + "\n" + localMapLookup + "\n" + tableLookup,Debugging.DATABASE_OUTPUT);

		
		// try closing all the database connections
		//closeAllDatabaseConnections();
		
		// Change this for multi user
		//setupTemplate();
		
		//If there are no users, create a user
		
		if (localUserMap.isEmpty()) {
			User starterUser = new User();
			starterUser.setUserName("ccrmUser");
			starterUser.setPassword("ccrmPass");
			starterUser.setDatabaseSelected("");//default database
			starterUser.addDatabaseAccsessable("");
			starterUser.addDatabaseAccsessable("ccrmUser");
			starterUser.setAdmin(true);
			store(starterUser,User.class);
			
		}

	}
	
	
	//login failure codes:
	public static final String NO_USER_CODE = "No such user in the system!";
	public static final String WRONG_PASS_CODE = "Wrong Username or Password";
	public static final String SUCCESS_CODE = "SUCCESSFUL LOGIN!";
	/*
	 * Logging in
	 */
	public static String attemptLogin(String userName, String pass) {
		User userLoggingIn = localUserMap.get(userName);
		if (userLoggingIn != null && !userName.equals("")) {
			boolean loginSucsess =  userLoggingIn.checkPassword(pass);
			if (loginSucsess) {
				Debugging.output("Logged into: " + userLoggingIn,Debugging.DATABASE_OUTPUT);
				return SUCCESS_CODE;
			} else {
				return WRONG_PASS_CODE;
			}
			
		} else {
			return NO_USER_CODE;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends MaxObject> ConcurrentHashMap<String, T> getMap (Class<T> ref) {
		return (ConcurrentHashMap<String, T>) localMapLookup.get(ref);
	}

	/**
	 * Loads values from a remote class
	 * 
	 * @param localMap
	 *            the local map to put the objects in.
	 * @param table
	 *            the database table to load value from
	 * @param ref
	 *            <your class>.Class
	 */
	@SuppressWarnings("unchecked")
	public static <T extends MaxObject> void loadMaxObjects(Map<String, T> localMap, MaxDBTable table, Class<T> ref) {

		Debugging.output("Loading objects from: " + table + " To: " + localMap + " of Class: " + ref,Debugging.DATABASE_OUTPUT);
		if (table == null) {
			return;// This is a bug
		}
		ResultSet allObjects = table.getAllRows();
		Debugging.output("Result set: " + allObjects,Debugging.DATABASE_OUTPUT);
		if (allObjects == null) {
			Debugging.output("NO  objects found",Debugging.DATABASE_OUTPUT);
			return;// no results found
		}
		try {
			while (allObjects.next()) {
				MaxObject obj;
				obj = ref.newInstance();
				obj.loadFromDB(allObjects);
				Debugging.output("Loaded: " + obj + " from database.",Debugging.DATABASE_OUTPUT);
				if (obj.getPrimaryKey() != null) {
					localMap.put(obj.getPrimaryKey(), (T) obj);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
			allObjects.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param table
	 * @param title
	 * @param database
	 * @return
	 */
	public static <T extends MaxObject> MaxDBTable setupDatabaseTable(MaxDBTable table, String title, MaxDB database,
			Class<T> ref) {
		table = new MaxDBTable(title);// create table

		table.setParentDB(database);// use this database

		Debugging.output("Type T: ",Debugging.DATABASE_OUTPUT);
		try {
			MaxObject obj;
			obj = ref.newInstance();
			obj.createTableForClass(table);// create a table (if not already
											// created)
			obj.setupDBDatatypes();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		table.connectDB();// connect to the database

		return table;
	}

	/**
	 * Stores an object into the correct map, This method uses a reference to
	 * the object type (class type) to create the entry and get type information.
	 * @param obj - the object to store
	 * @param ref - the class reference of obj
	 */
	public static <T extends MaxObject> void store(T obj, Class<T> ref) {
		
		ProfilingTimer storeTime = new ProfilingTimer("DataHolder Store Time");
		// get data-structure based on ref
		@SuppressWarnings("unchecked")
		ConcurrentHashMap<String, T> map = (ConcurrentHashMap<String, T>) localMapLookup.get(ref);
		
		Debugging.output("Storing in map: " + map,Debugging.DATABASE_OUTPUT);
		map.put(obj.getPrimaryKey(), (T) obj);

		MaxDBTable table = tableLookup.get(ref);

		table.insertInTable(obj);
		
		storeTime.stopTimer();
	}


	public static User getUser(String userName) {
		return localUserMap.get(userName);
	}
	
	public static ConcurrentHashMap<String, User> getUserMap() {
		return localUserMap;
	}
	
	public static ConcurrentHashMap<String, UserDataHolder> getUserDataHolderMap() {
		return userDataHolderMap;
	}
	
	/**
	 * Looks up the userDataHolder based on a user.
	 * Creates the holder if it doesn't already exist.
	 * @param loggedInUser
	 * @return
	 */
	public static UserDataHolder getUserDataHolder(User loggedInUser) {
		// TODO Auto-generated method stub
		UserDataHolder userDataHolder = getUserDataHolder(loggedInUser.getDatabaseSelected());
		return userDataHolder;
	}

	/**
	 * Looks up the userDataHolder based on the userDataHolder Name.
	 * Creates the holder if it doesn't already exist.
	 * @param dataHolderName
	 * @return
	 */
	public static UserDataHolder getUserDataHolder(String dataHolderName) {

		UserDataHolder userDataHolder = userDataHolderMap.get(dataHolderName);
		if (userDataHolder==null) {
			//create the user data holder if it doesn't exist
			userDataHolder = new UserDataHolder();
			userDataHolder.setDatabasePrefix(dataHolderName);
			store(userDataHolder, UserDataHolder.class);
		}
		return userDataHolder;
	}

	/**
	 * Tests to see if the class is a static class
	 * @param ref - the class to test (EX User.Class)
	 * @return if it was found (true / false)
	 */
	public static boolean containsClass(Class ref) {
		Debugging.output("Table: " + tableLookup + " Contains key: " + ref,Debugging.DATABASE_OUTPUT);
		boolean result = tableLookup.containsKey(ref);
		Debugging.output("Table: " + tableLookup + " Contains key: " + ref + " = " + result,Debugging.DATABASE_OUTPUT);
		
		return result;
	}


	/**
	 * Retrieves an item based on a class ref.
	 * May be volitile if you give it a class not found in
	 * localMapLookup
	 * 
	 * @param itemName - the item name (primary key)
	 * @param ref - The class (EX User.class)
	 * @return the object if it exists, or null
	 */
	public static <T extends MaxObject> MaxObject retrieve(String itemName, Class<T> ref) {
		MaxObject item;
		@SuppressWarnings("unchecked")
		Map<String,T> lMap = (Map<String, T>) localMapLookup.get(ref);
		item = lMap.get(itemName);
		
		return item;
	}

}
