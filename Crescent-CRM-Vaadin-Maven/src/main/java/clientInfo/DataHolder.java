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
public class DataHolder {

	//will not be static after the transition
	private static ConcurrentHashMap<String, Client> localClientMap = new ConcurrentHashMap<String, Client>();
	private static ConcurrentHashMap<String, Location> localLocationMap = new ConcurrentHashMap<String, Location>();
	private static ConcurrentHashMap<String, Status> localStatusMap = new ConcurrentHashMap<String, Status>();
	private static ConcurrentHashMap<String, Group> localGroupMap = new ConcurrentHashMap<String, Group>();
	
	//Will be static after the transition
	private static ConcurrentHashMap<String, User> localUserpMap = new ConcurrentHashMap<String, User>();
	
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
	static MaxDBTable statusTable;
	public static String STATUS_TABLE_TITLE = "statusTable";
	static MaxDBTable locationTable;
	public static String LOCATION_TABLE_TITLE = "locationsTable";
	static MaxDBTable groupTable;
	public static String GROUP_TABLE_TITLE = "groupsTable";
	static MaxDBTable clientTable;
	public static String CLIENT_TABLE_TITLE = "clientsTable";

	static ConcurrentMap<Class<? extends MaxObject>, ConcurrentHashMap<String, ? extends MaxObject>> localMapLookup;
	static ConcurrentMap<Class<? extends MaxObject>, MaxDBTable> tableLookup;

	public static final String TEMPLATE_STRING = "[[TEMPLATE]]";
	public static Client templateClient;
	
	
	
	
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
		groupTable = setupDatabaseTable(groupTable, GROUP_TABLE_TITLE, mysqlDatabase, Group.class);
		statusTable = setupDatabaseTable(statusTable, STATUS_TABLE_TITLE, mysqlDatabase, Status.class);
		locationTable = setupDatabaseTable(locationTable, LOCATION_TABLE_TITLE, mysqlDatabase, Location.class);
		clientTable = setupDatabaseTable(clientTable, CLIENT_TABLE_TITLE, mysqlDatabase, Client.class);

		loadMaxObjects(localStatusMap, statusTable, Status.class);
		loadMaxObjects(localGroupMap, groupTable, Group.class);
		loadMaxObjects(localLocationMap, locationTable, Location.class);
		loadMaxObjects(localClientMap, clientTable, Client.class);

		// BACKUP all data to a CSV file
		backupAllCsv();

		/*
		 * SET UP GENERIC MAPS
		 */
		localMapLookup = new ConcurrentHashMap<Class<? extends MaxObject>, ConcurrentHashMap<String, ? extends MaxObject>>();
		localMapLookup.put(Client.class, localClientMap);
		localMapLookup.put(Location.class, localLocationMap);
		localMapLookup.put(Status.class, localStatusMap);
		localMapLookup.put(Group.class, localGroupMap);

		tableLookup = new ConcurrentHashMap<Class<? extends MaxObject>, MaxDBTable>();
		tableLookup.put(Client.class, clientTable);
		tableLookup.put(Location.class, locationTable);
		tableLookup.put(Status.class, statusTable);
		tableLookup.put(Group.class, groupTable);
		// OUTPUT GENERIC Maps

		System.out.println("CREATED LOCAL LOOKUPS" + "\n" + localMapLookup + "\n" + tableLookup);

		
		// try closing all the database connections
		//closeAllDatabaseConnections();
		
		setupTemplate();

	}

	
	/***
	 *    TTTTTTT    EEEEEEE    MM    MM    PPPPPP     LL           AAA      TTTTTTT    EEEEEEE     SSSSS  
	 *      TTT      EE         MMM  MMM    PP   PP    LL          AAAAA       TTT      EE         SS      
	 *      TTT      EEEEE      MM MM MM    PPPPPP     LL         AA   AA      TTT      EEEEE       SSSSS  
	 *      TTT      EE         MM    MM    PP         LL         AAAAAAA      TTT      EE              SS 
	 *      TTT      EEEEEEE    MM    MM    PP         LLLLLLL    AA   AA      TTT      EEEEEEE     SSSSS  
	 *                                                                                                     
	 */
	
	public static void setupTemplate() {
		
		Client c;
		
		if ((c=getClient(TEMPLATE_STRING)) == null) {
			Location loc;
			Status status;
			Group group;
			c = new Client();
			//create a client with "template name"
			//does the "template" location exist?
			if ((loc=getLocation(TEMPLATE_STRING))== null) {
				loc = new Location();
				loc.setLocationName(TEMPLATE_STRING);
				store(loc, Location.class);
			}
			if ((status=getStatus(TEMPLATE_STRING))==null) {
				status = new Status();
				status.setStatusName(TEMPLATE_STRING);
				store(status,Status.class);
			}
			
			if ((group=getGroup(TEMPLATE_STRING))==null) {
				group = new Group();
				group.setGroupName(TEMPLATE_STRING);
				store(group, Group.class);
			}
			
			c.setId(TEMPLATE_STRING);
			c.setName(TEMPLATE_STRING);
			c.setGroup(group);
			c.setLocation(loc);
			c.setStatus(status);
			c.setNotes("");
			store(c,Client.class);
			templateClient = c;
		} else {
			System.out.println("TEMPLATE: " + TEMPLATE_STRING + " already found" + c);
			templateClient = c;
		}
	}
	
	
	
	
	private static void closeAllDatabaseConnections() {
		statusTable.closeDB();
		clientTable.closeDB();
		groupTable.closeDB();
		locationTable.closeDB();
		mysqlDatabase.closeDB();

	}

	// BACKUPS

	public static void backupAllCsv() {
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

	}

	/**
	 * Expects files to be named a certain thing for now
	 */
	private static void restoreAllCsv() {

		BackupManager.restore("status", Status.class, localStatusMap);
		BackupManager.restore("location", Location.class, localLocationMap);
		BackupManager.restore("group", Group.class, localGroupMap);
		BackupManager.restore("client", Client.class, localClientMap);

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

	/*
	 * CLIENTS
	 */


	public static Client getClient(String id) {
		return localClientMap.get(id);
	}

	public static Collection<Client> getAllClients() {
		return localClientMap.values();
	}

	/*
	 * LOCATION
	 */


	public static Location getLocation(String id) {
		return localLocationMap.get(id);
	}

	public static Collection<Location> getAllLocations() {
		return localLocationMap.values();
	}

	/*
	 * STATUS
	 */


	public static Status getStatus(String id) {
		return localStatusMap.get(id);
	}

	public static Collection<Status> getAllStatus() {
		return localStatusMap.values();
	}

	/*
	 * GROUPs
	 */


	public static Group getGroup(String id) {
		return localGroupMap.get(id);
	}

	public static Collection<Group> getAllGroups() {
		return localGroupMap.values();
	}

	public static ConcurrentHashMap<String, Location> getLocationMap() {
		// TODO Auto-generated method stub
		return localLocationMap;
	}

	public static ConcurrentHashMap<String, Status> getStatusMap() {
		// TODO Auto-generated method stub
		return localStatusMap;
	}

	public static ConcurrentHashMap<String, Group> getGroupMap() {
		// TODO Auto-generated method stub
		return localGroupMap;
	}

}
