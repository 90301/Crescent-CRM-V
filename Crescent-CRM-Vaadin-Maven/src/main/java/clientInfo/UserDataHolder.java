/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package clientInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import dbUtils.MaxDBTable;
import dbUtils.MaxObject;
import inventory.InventoryItem;

//TODO: make this extend max object, and have a table of available databases
public class UserDataHolder extends MaxObject {

	public static final String databasePrefixField = "DatabasePrefix";
	//private String user;
	private String databasePrefix;
	// will not be after the transition
	private ConcurrentHashMap<String, Client> userClientMap = new ConcurrentHashMap<String, Client>();
	private ConcurrentHashMap<String, Location> userLocationMap = new ConcurrentHashMap<String, Location>();
	private ConcurrentHashMap<String, Status> userStatusMap = new ConcurrentHashMap<String, Status>();
	private ConcurrentHashMap<String, Group> userGroupMap = new ConcurrentHashMap<String, Group>();
	private ConcurrentHashMap<String, InventoryItem> userInventoryMap = new ConcurrentHashMap<String, InventoryItem>();

	MaxDBTable userStatusTable;
	public String STATUS_TABLE_TITLE = "statusTable";
	MaxDBTable userLocationTable;
	public String LOCATION_TABLE_TITLE = "locationsTable";
	MaxDBTable userGroupTable;
	public String GROUP_TABLE_TITLE = "groupsTable";
	MaxDBTable userClientTable;
	public String CLIENT_TABLE_TITLE = "clientsTable";
	MaxDBTable userInventoryTable;
	public String INVENTORY_TABLE_TITLE = "inventoryTable";

	ConcurrentMap<Class<? extends MaxObject>, ConcurrentHashMap<String, ? extends MaxObject>> localMapLookup  = new ConcurrentHashMap<Class<? extends MaxObject>, ConcurrentHashMap<String, ? extends MaxObject>>();
	ConcurrentMap<Class<? extends MaxObject>, MaxDBTable> tableLookup  = new ConcurrentHashMap<Class<? extends MaxObject>, MaxDBTable>();

	public Client templateClient;

	/**
	 * Holds data which is user speciific. Will implement data structures and be
	 * a sub section of the dataholder class.
	 */
	public UserDataHolder() {
		// TODO Auto-generated constructor stub
	}

	public void initalizeDatabases() {
		// setup the tables
		
		/*
		userGroupTable = DataHolder.setupDatabaseTable(userGroupTable, databasePrefix + GROUP_TABLE_TITLE,
				DataHolder.mysqlDatabase, Group.class);
		userStatusTable = DataHolder.setupDatabaseTable(userStatusTable, databasePrefix + STATUS_TABLE_TITLE,
				DataHolder.mysqlDatabase, Status.class);
		userLocationTable = DataHolder.setupDatabaseTable(userLocationTable, databasePrefix + LOCATION_TABLE_TITLE,
				DataHolder.mysqlDatabase, Location.class);
		userClientTable = DataHolder.setupDatabaseTable(userClientTable, databasePrefix + CLIENT_TABLE_TITLE,
				DataHolder.mysqlDatabase, Client.class);
		userClientTable = DataHolder.setupDatabaseTable(userInventoryTable, databasePrefix + INVENTORY_TABLE_TITLE,
				DataHolder.mysqlDatabase, Client.class);


		// load the data
		loadMaxObjects(userStatusMap, userStatusTable, Status.class);
		loadMaxObjects(userGroupMap, userGroupTable, Group.class);
		loadMaxObjects(userLocationMap, userLocationTable, Location.class);
		loadMaxObjects(userClientMap, userClientTable, Client.class);
		loadMaxObjects(userInventoryMap, userInventoryTable, InventoryItem.class);

		*/
		
		/*
		 * User Database lookup tables
		 */
		/*
		localMapLookup = new ConcurrentHashMap<Class<? extends MaxObject>, ConcurrentHashMap<String, ? extends MaxObject>>();
		localMapLookup.put(Client.class, userClientMap);
		localMapLookup.put(Location.class, userLocationMap);
		localMapLookup.put(Status.class, userStatusMap);
		localMapLookup.put(Group.class, userGroupMap);
		localMapLookup.put(InventoryItem.class, userInventoryMap);

		tableLookup = new ConcurrentHashMap<Class<? extends MaxObject>, MaxDBTable>();
		tableLookup.put(Client.class, userClientTable);
		tableLookup.put(Location.class, userLocationTable);
		tableLookup.put(Status.class, userStatusTable);
		tableLookup.put(Group.class, userGroupTable);
		tableLookup.put(InventoryItem.class, userInventoryTable);
		*/
		
		setupTable(userGroupMap,userGroupTable,GROUP_TABLE_TITLE,Group.class);
		setupTable(userStatusMap,userStatusTable,STATUS_TABLE_TITLE,Status.class);
		setupTable(userLocationMap,userLocationTable,LOCATION_TABLE_TITLE,Location.class);
		//setupTable(userInventoryMap,userInventoryTable,INVENTORY_TABLE_TITLE,InventoryItem.class);
		setupTable(userClientMap,userClientTable,CLIENT_TABLE_TITLE,Client.class); // must be added last
		
		setupTemplate();

	}
	
	@SuppressWarnings("unchecked")
	public MaxDBTable setupTable(ConcurrentHashMap<String,? extends MaxObject> userMap, MaxDBTable table, String tableTitle, Class c) {
		
		table = DataHolder.setupDatabaseTable(table, databasePrefix + tableTitle,
				DataHolder.mysqlDatabase, c);
		
		loadMaxObjects(userMap, table, c);
		
		tableLookup.put(c, table);
		localMapLookup.put(c, userMap);
		
		return table;
	}

	public void setupTemplate() {

		Client c;

		if ((c = getClient(DataHolder.TEMPLATE_STRING)) == null) {
			Location loc;
			Status status;
			Group group;
			c = new Client();
			// create a client with "template name"
			// does the "template" location exist?
			if ((loc = getLocation(DataHolder.TEMPLATE_STRING)) == null) {
				loc = new Location();
				loc.setLocationName(DataHolder.TEMPLATE_STRING);
				store(loc, Location.class);
			}
			if ((status = getStatus(DataHolder.TEMPLATE_STRING)) == null) {
				status = new Status();
				status.setStatusName(DataHolder.TEMPLATE_STRING);
				store(status, Status.class);
			}

			if ((group = getGroup(DataHolder.TEMPLATE_STRING)) == null) {
				group = new Group();
				group.setGroupName(DataHolder.TEMPLATE_STRING);
				store(group, Group.class);
			}

			c.setId(DataHolder.TEMPLATE_STRING);
			c.setName(DataHolder.TEMPLATE_STRING);
			c.setGroup(group);
			c.setLocation(loc);
			c.setStatus(status);
			c.setNotes("");
			
			System.out.println("Template class created: " + c);
			store(c, Client.class);
			this.templateClient = c;
		} else {
			System.out.println("TEMPLATE: " + DataHolder.TEMPLATE_STRING + " already found" + c);
			this.templateClient = c;
		}
	}

	public <T extends MaxObject> void store(T obj, Class<T> ref) {
		// get data-structure based on ref
		@SuppressWarnings("unchecked")
		ConcurrentHashMap<String, T> map = (ConcurrentHashMap<String, T>) localMapLookup.get(ref);
		map.put(obj.getPrimaryKey(), (T) obj);

		MaxDBTable table = tableLookup.get(ref);

		table.insertInTable(obj);
	}
	

	/**
	 * Retrieves an item based on a class ref.
	 * May be volatile if you give it a class not found in
	 * localMapLookup
	 * 
	 * @param itemName - the item name (primary key)
	 * @param ref - The class (EX User.class)
	 * @return the object if it exists, or null
	 */
	public <T extends MaxObject> MaxObject retrieve(String itemName, Class<T> ref) {
		// TODO Auto-generated method stub
		MaxObject item;
		@SuppressWarnings("unchecked")
		Map<String,T> lMap = (Map<String, T>) localMapLookup.get(ref);
		item = lMap.get(itemName);
		
		return item;
	}
	
	/**
	 * Version of loadMaxObjects that sets the user data holder
	 * @param localMap
	 * @param table
	 * @param ref
	 */
	@SuppressWarnings("unchecked")
	public <T extends MaxObject> void loadMaxObjects(Map<String, T> localMap, MaxDBTable table, Class<T> ref) {

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
				obj.setUserDataHolder(this);
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

	/*
	 * Getters and Setters
	 */
	

	public String getDatabasePrefix() {
		return databasePrefix;
	}

	public void setDatabasePrefix(String databasePrefix) {
		this.databasePrefix = databasePrefix;
		updateDBMap();
	}

	public Client getClient(String id) {
		return userClientMap.get(id);
	}

	public Collection<Client> getAllClients() {
		return userClientMap.values();
	}

	/*
	 * LOCATION
	 */

	public Location getLocation(String id) {
		return userLocationMap.get(id);
	}

	public Collection<Location> getAllLocations() {
		return userLocationMap.values();
	}

	/*
	 * STATUS
	 */

	public Status getStatus(String id) {
		return userStatusMap.get(id);
	}

	public Collection<Status> getAllStatus() {
		return userStatusMap.values();
	}

	/*
	 * GROUPs
	 */

	public Group getGroup(String id) {
		return userGroupMap.get(id);
	}

	public Collection<Group> getAllGroups() {
		return userGroupMap.values();
	}
	public ConcurrentHashMap<String, Location> getLocationMap() {
		// TODO Auto-generated method stub
		return userLocationMap;
	}

	public ConcurrentHashMap<String, Status> getStatusMap() {
		// TODO Auto-generated method stub
		return userStatusMap;
	}

	public ConcurrentHashMap<String, Group> getGroupMap() {
		// TODO Auto-generated method stub
		return userGroupMap;
	}
	
	public ConcurrentHashMap<String, Client> getClientMap() {
		// TODO Auto-generated method stub
		return userClientMap;
	}

	@Override
	public void loadInternalFromMap() {
		this.databasePrefix = (String) dbMap.get(databasePrefixField);
		
	}

	@Override
	public void updateDBMap() {
		
		if (this.databasePrefix == null) {
			this.databasePrefix = "";
		}
		dbMap.put(databasePrefixField, this.databasePrefix);
		
	}

	@Override
	public String getPrimaryKey() {
		// TODO Auto-generated method stub
		return this.databasePrefix;
	}

	@Override
	public void createTableForClass(MaxDBTable table) {

		table.addDatatype(databasePrefixField, MaxDBTable.DATA_MYSQL_TYPE_HUGE_KEY_STRING);
		table.setPrimaryKeyName(databasePrefixField);
		table.createTable();
		
	}

	@Override
	public void setupDBDatatypes() {
		if (dbDatatypes == null) {
			dbDatatypes = new HashMap<String, Class<?>>();
		}
		dbDatatypes.put(databasePrefixField, String.class);
		
	}
}
