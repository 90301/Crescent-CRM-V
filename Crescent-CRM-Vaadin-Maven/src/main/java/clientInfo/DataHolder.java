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
import users.User;

/**
 * Handles all the data-storage.
 * 
 * @author inhaler
 *
 */
@SuppressWarnings("unused")
public class DataHolder {

	//will not be static after the transition
	/*
	private static ConcurrentHashMap<String, Client> localClientMap = new ConcurrentHashMap<String, Client>();
	private static ConcurrentHashMap<String, Location> localLocationMap = new ConcurrentHashMap<String, Location>();
	private static ConcurrentHashMap<String, Status> localStatusMap = new ConcurrentHashMap<String, Status>();
	private static ConcurrentHashMap<String, Group> localGroupMap = new ConcurrentHashMap<String, Group>();
	*/
	//Will be static after the transition
	private static ConcurrentHashMap<String, User> localUserMap = new ConcurrentHashMap<String, User>();
	private static ConcurrentHashMap<String, UserDataHolder> userDataHolderMap = new ConcurrentHashMap<String,UserDataHolder>();
	
	
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
	static MaxDBTable userTable,userDataHolderTable;
	public static String USER_TABLE_TITLE = "usersTable";
	public static String USER_DATA_HOLDER_TABLE_TITLE = "userDataHolderTable";
	
	
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
		System.out.println("MYSQL DATABASE: " + mysqlDatabase);

		/*
		 * Set up the tables
		 */

		userTable = setupDatabaseTable(userTable, USER_TABLE_TITLE, mysqlDatabase, User.class);
		userDataHolderTable = setupDatabaseTable(userDataHolderTable, USER_DATA_HOLDER_TABLE_TITLE, mysqlDatabase, UserDataHolder.class);
		

		loadMaxObjects(localUserMap, userTable, User.class);
		loadMaxObjects(userDataHolderMap, userDataHolderTable, UserDataHolder.class);
		
		// BACKUP all data to a CSV file
		backupAllCsv();

		/*
		 * SET UP GENERIC MAPS
		 */
		localMapLookup = new ConcurrentHashMap<Class<? extends MaxObject>, ConcurrentHashMap<String, ? extends MaxObject>>();

		localMapLookup.put(User.class, localUserMap);
		localMapLookup.put(UserDataHolder.class, userDataHolderMap);

		tableLookup = new ConcurrentHashMap<Class<? extends MaxObject>, MaxDBTable>();

		tableLookup.put(User.class, userTable);
		tableLookup.put(UserDataHolder.class, userDataHolderTable);
		// OUTPUT GENERIC Maps

		System.out.println("CREATED LOCAL LOOKUPS" + "\n" + localMapLookup + "\n" + tableLookup);

		
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
		if (userLoggingIn != null) {
			boolean loginSucsess =  userLoggingIn.checkPassword(pass);
			if (loginSucsess) {
				System.out.println("Logged into: " + userLoggingIn);
				return SUCCESS_CODE;
			} else {
				return WRONG_PASS_CODE;
			}
			
		} else {
			return NO_USER_CODE;
		}
	}
	

	// BACKUPS

	/**
	 * @deprecated
	 */
	public static void backupAllCsv() {
		/*
		ArrayList<String> fileNames = new ArrayList<String>();
		fileNames.add("status.csv");
		fileNames.add("location.csv");
		fileNames.add("group.csv");
		fileNames.add("client.csv");

		Integer i = 0;
		BackupManager.backupToCSV(fileNames.get(i), localStatusMap.values());
		i++;
		BackupManager.backupToCSV(fileNames.get(i), localLocationMap.values());
		i++;
		BackupManager.backupToCSV(fileNames.get(i), localGroupMap.values());
		i++;
		BackupManager.backupToCSV(fileNames.get(i), localClientMap.values());
		i++;
		// zip files
		DateFormat zipDateFormat = new SimpleDateFormat("yyyy_MMMMM_dd_HH_mm");
		Date date = new Date();
		zipDateFormat.format(date);

		String zipFileName = "Backup_" + zipDateFormat.format(date);
		BackupManager.zipFiles(fileNames, zipFileName);
		*/
	}

	/**
	 * @deprecated
	 */
	private static void restoreAllCsv() {
		/*
		BackupManager.restore("status", Status.class, localStatusMap);
		BackupManager.restore("location", Location.class, localLocationMap);
		BackupManager.restore("group", Group.class, localGroupMap);
		BackupManager.restore("client", Client.class, localClientMap);
		*/
	}

	public static void restoreFromBackup(String fileName) {
		BackupManager.unzipFiles(fileName);
		// backup first

		// restore
		restoreAllCsv();
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

		System.out.println("Loading objects from: " + table + " To: " + localMap + " of Class: " + ref);
		if (table == null) {
			return;// This is a bug
		}
		ResultSet allObjects = table.getAllRows();
		System.out.println("Result set: " + allObjects);
		if (allObjects == null) {
			System.out.println("NO  objects found");
			return;// no results found
		}
		try {
			while (allObjects.next()) {
				MaxObject obj;
				obj = ref.newInstance();
				obj.loadFromDB(allObjects);
				System.out.println("Loaded: " + obj + " from database.");
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
			// TODO Auto-generated catch block
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

		System.out.println("Type T: ");
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
		// get data-structure based on ref
		@SuppressWarnings("unchecked")
		ConcurrentHashMap<String, T> map = (ConcurrentHashMap<String, T>) localMapLookup.get(ref);
		map.put(obj.getPrimaryKey(), (T) obj);

		MaxDBTable table = tableLookup.get(ref);

		table.insertInTable(obj);
	}


	public static User getUser(String userName) {
		// TODO Auto-generated method stub
		return localUserMap.get(userName);
	}
	
	public static ConcurrentHashMap<String, User> getUserMap() {
		// TODO Auto-generated method stub
		return localUserMap;
	}
	
	public static ConcurrentHashMap<String, UserDataHolder> getUserDataHolderMap() {
		// TODO Auto-generated method stub
		return userDataHolderMap;
	}
	
	
	public static UserDataHolder getUserDataHolder(User loggedInUser) {
		// TODO Auto-generated method stub
		UserDataHolder userDataHolder = userDataHolderMap.get(loggedInUser.getDatabaseSelected());
		if (userDataHolder==null) {
			//create the user data holder if it doesn't exist
			userDataHolder = new UserDataHolder();
			userDataHolder.setDatabasePrefix(loggedInUser.getDatabaseSelected());
			store(userDataHolder, UserDataHolder.class);
		}
		return userDataHolder;
	}

}
