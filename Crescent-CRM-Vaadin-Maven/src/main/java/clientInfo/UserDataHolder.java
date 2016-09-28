package clientInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import dbUtils.MaxDBTable;
import dbUtils.MaxObject;

//TODO: make this extend max object, and have a table of available databases
public class UserDataHolder {

	//private String user;
	private String databasePrefix;
	// will not be after the transition
	private ConcurrentHashMap<String, Client> userClientMap = new ConcurrentHashMap<String, Client>();
	private ConcurrentHashMap<String, Location> userLocationMap = new ConcurrentHashMap<String, Location>();
	private ConcurrentHashMap<String, Status> userStatusMap = new ConcurrentHashMap<String, Status>();
	private ConcurrentHashMap<String, Group> userGroupMap = new ConcurrentHashMap<String, Group>();

	MaxDBTable userStatusTable;
	public String STATUS_TABLE_TITLE = "statusTable";
	MaxDBTable userLocationTable;
	public String LOCATION_TABLE_TITLE = "locationsTable";
	MaxDBTable userGroupTable;
	public String GROUP_TABLE_TITLE = "groupsTable";
	MaxDBTable userClientTable;
	public String CLIENT_TABLE_TITLE = "clientsTable";

	ConcurrentMap<Class<? extends MaxObject>, ConcurrentHashMap<String, ? extends MaxObject>> localMapLookup;
	ConcurrentMap<Class<? extends MaxObject>, MaxDBTable> tableLookup;

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
		userGroupTable = DataHolder.setupDatabaseTable(userGroupTable, databasePrefix + GROUP_TABLE_TITLE,
				DataHolder.mysqlDatabase, Group.class);
		userStatusTable = DataHolder.setupDatabaseTable(userStatusTable, databasePrefix + STATUS_TABLE_TITLE,
				DataHolder.mysqlDatabase, Status.class);
		userLocationTable = DataHolder.setupDatabaseTable(userLocationTable, databasePrefix + LOCATION_TABLE_TITLE,
				DataHolder.mysqlDatabase, Location.class);
		userClientTable = DataHolder.setupDatabaseTable(userClientTable, databasePrefix + CLIENT_TABLE_TITLE,
				DataHolder.mysqlDatabase, Client.class);

		// load the data
		loadMaxObjects(userStatusMap, userStatusTable, Status.class);
		loadMaxObjects(userGroupMap, userGroupTable, Group.class);
		loadMaxObjects(userLocationMap, userLocationTable, Location.class);
		loadMaxObjects(userClientMap, userClientTable, Client.class);

		/*
		 * User Database lookup tables
		 */
		localMapLookup = new ConcurrentHashMap<Class<? extends MaxObject>, ConcurrentHashMap<String, ? extends MaxObject>>();
		localMapLookup.put(Client.class, userClientMap);
		localMapLookup.put(Location.class, userLocationMap);
		localMapLookup.put(Status.class, userStatusMap);
		localMapLookup.put(Group.class, userGroupMap);

		tableLookup = new ConcurrentHashMap<Class<? extends MaxObject>, MaxDBTable>();
		tableLookup.put(Client.class, userClientTable);
		tableLookup.put(Location.class, userLocationTable);
		tableLookup.put(Status.class, userStatusTable);
		tableLookup.put(Group.class, userGroupTable);

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
			DataHolder.store(c, Client.class);
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
}
