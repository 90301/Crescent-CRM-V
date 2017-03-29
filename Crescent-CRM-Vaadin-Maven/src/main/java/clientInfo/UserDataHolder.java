/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package clientInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import dbUtils.MaxDBTable;
import dbUtils.MaxField;
import dbUtils.MaxObject;
import debugging.Debugging;
import debugging.profiling.ProfilingTimer;
import inventory.InventoryCategory;
import inventory.InventoryItem;

//TODO: make this extend max object, and have a table of available databases
public class UserDataHolder extends MaxObject {

	//public static final String databasePrefixField = "DatabasePrefix";
	
	
	//private String user;
	//private String databasePrefix;
	
	MaxField<String> databasePrefix = new MaxField<String>("DatabasePrefix",MaxDBTable.DATA_MYSQL_TYPE_HUGE_KEY_STRING,"","",this);
	
	// will not be after the transition
	private ConcurrentHashMap<String, Client> userClientMap = new ConcurrentHashMap<String, Client>();
	private ConcurrentHashMap<String, Location> userLocationMap = new ConcurrentHashMap<String, Location>();
	private ConcurrentHashMap<String, Status> userStatusMap = new ConcurrentHashMap<String, Status>();
	private ConcurrentHashMap<String, Group> userGroupMap = new ConcurrentHashMap<String, Group>();
	private ConcurrentHashMap<String, InventoryItem> userInventoryMap = new ConcurrentHashMap<String, InventoryItem>();
	private ConcurrentHashMap<String, InventoryCategory> userInventoryCategoryMap = new ConcurrentHashMap<String, InventoryCategory>();
	private ConcurrentHashMap<String, TemplateField> userTemplateFieldMap = new ConcurrentHashMap<String, TemplateField>();
	
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
	MaxDBTable userInventoryCategoryTable;
	public String INVENTORY_CATEGORY_TABLE_TITLE = "inventoryCategoryTable";
	MaxDBTable userTemplateFieldTable;
	public String TEMPLATE_FIELD_TABLE_TITLE = "templateFieldTable";

	ConcurrentMap<Class<? extends MaxObject>, ConcurrentHashMap<String, ? extends MaxObject>> localMapLookup  = new ConcurrentHashMap<Class<? extends MaxObject>, ConcurrentHashMap<String, ? extends MaxObject>>();
	ConcurrentMap<Class<? extends MaxObject>, MaxDBTable> tableLookup  = new ConcurrentHashMap<Class<? extends MaxObject>, MaxDBTable>();

	Boolean databaseSetup = false;
	
	public Client templateClient;
	
	{
		addMaxField(databasePrefix);
		this.setKeyField(databasePrefix);
	}
	
	/**
	 * Holds data which is user speciific. Will implement data structures and be
	 * a sub section of the dataholder class.
	 */
	public UserDataHolder() {
		// TODO Auto-generated constructor stub
	}

	public void initalizeDatabases() {
		ProfilingTimer initTime = new ProfilingTimer("Initalize UserDataHolder");
		// setup the tables
		if (!databaseSetup) {
		setupTable(userTemplateFieldMap,userTemplateFieldTable,TEMPLATE_FIELD_TABLE_TITLE,TemplateField.class);
		setupTable(userGroupMap,userGroupTable,GROUP_TABLE_TITLE,Group.class);
		setupTable(userStatusMap,userStatusTable,STATUS_TABLE_TITLE,Status.class);
		setupTable(userLocationMap,userLocationTable,LOCATION_TABLE_TITLE,Location.class);
		setupTable(userInventoryCategoryMap,userInventoryCategoryTable,INVENTORY_CATEGORY_TABLE_TITLE,InventoryCategory.class);
		setupTable(userInventoryMap,userInventoryTable,INVENTORY_TABLE_TITLE,InventoryItem.class);
		setupTable(userClientMap,userClientTable,CLIENT_TABLE_TITLE,Client.class); // must be added last
		
		
		setupTemplate();
		
		databaseSetup = true;
		}
		
		initTime.stopTimer();

	}
	
	@SuppressWarnings("unchecked")
	public MaxDBTable setupTable(ConcurrentHashMap<String,? extends MaxObject> userMap, MaxDBTable table, String tableTitle, Class c) {
		//DEBUG THIS
		Debugging.output("Setting up table: " + table + " " + tableTitle, Debugging.USER_EDITOR_OUTPUT, Debugging.USER_EDITOR_OUTPUT_ENABLED);
		
		
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

			//c.setId(DataHolder.TEMPLATE_STRING);
			c.setName(DataHolder.TEMPLATE_STRING);
			c.setGroup(group);
			c.setLocation(loc);
			c.setStatus(status);
			c.setNotes("");
			
			Debugging.output("Template class created: " + c,Debugging.DATABASE_OUTPUT);
			store(c, Client.class);
			this.templateClient = c;
		} else {
			Debugging.output("TEMPLATE: " + DataHolder.TEMPLATE_STRING + " already found" + c,Debugging.DATABASE_OUTPUT);
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
	 * Attempts to delete Object from the database and the local map
	 * @param obj
	 * @param ref
	 */
	public <T extends MaxObject> void delete(T obj, Class<T> ref) {
		MaxDBTable table = tableLookup.get(ref);
		ConcurrentHashMap<String, T> map = (ConcurrentHashMap<String, T>) localMapLookup.get(ref);
		
		table.deleteRow(obj);
		map.remove(obj.getPrimaryKey());
		
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
	public <T extends MaxObject> T retrieve(String itemName, Class<T> ref) {
		T item;
		@SuppressWarnings("unchecked")
		Map<String,T> lMap = (Map<String, T>) localMapLookup.get(ref);
		item = lMap.get(itemName);
		if (item!=null)
		item.setUserDataHolder(this);
		
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
				obj.setUserDataHolder(this);
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
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends MaxObject> ConcurrentHashMap<String, T> getMap (Class<T> ref) {
		return (ConcurrentHashMap<String, T>) localMapLookup.get(ref);
		
	}
	
	@SuppressWarnings("unchecked")
	public <T extends MaxObject> Collection<T> getMaxObjects (Class<T> ref) {
		return (Collection<T>) localMapLookup.get(ref).values();
		
	}
	
	public <T extends MaxObject> Collection<T> removeTemplate(Collection<T> list) {
		
		Collection<T> outputList = new ArrayList<T>();
		//NOTE, this is only being done this way because 
		MaxObject templateObject = null;
		
		
		
		for (T obj : list) {
			if (obj.getPrimaryKey().equals(DataHolder.TEMPLATE_STRING)) {
				templateObject = obj;
				//break;
			} else {
				outputList.add(obj);
			}
		}
		
		//outputList.remove(templateObject);
		
		return outputList;
	}
	


	/*
	 * Getters and Setters
	 */
	

	public String getDatabasePrefix() {
		return databasePrefix.getFieldValue();
	}

	public void setDatabasePrefix(String databasePrefix) {
		this.databasePrefix.setFieldValue(databasePrefix);
	}

	public Client getClient(String id) {
		return userClientMap.get(id);
	}

	public Collection<Client> getAllClients() {
		return userClientMap.values();
	}
	/**
	 * Removes the template client
	 * @return
	 */
	public Collection<Client> getAllRealClients() {
		return removeTemplate(getAllClients());
	}
	/**
	 * Gets the primary key of all REAL clients
	 * @return
	 */
	public Collection<String> getAllStringClients() {
		ArrayList<String> allStringClients = new ArrayList<String>();
		for (Client c : getAllRealClients()) {
			allStringClients.add(c.getPrimaryKey());
		}
		return allStringClients;
	}
	

	/*
	 * LOCATION
	 */

	public Location getLocation(String id) {
		return retrieve(id,Location.class);
	}

	public Collection<Location> getAllLocations() {
		return removeTemplate(userLocationMap.values());
	}

	/*
	 * STATUS
	 */

	public Status getStatus(String id) {
		return retrieve(id,Status.class);
	}

	public Collection<Status> getAllStatus() {
				
		return removeTemplate(userStatusMap.values());
		
	}

	/*
	 * GROUPs
	 */

	public Group getGroup(String id) {
		return retrieve(id,Group.class);
	}

	public Collection<Group> getAllGroups() {
		return removeTemplate(userGroupMap.values());
	}
	public ConcurrentHashMap<String, Location> getLocationMap() {
		return userLocationMap;
	}

	public ConcurrentHashMap<String, Status> getStatusMap() {
		return userStatusMap;
	}

	public ConcurrentHashMap<String, Group> getGroupMap() {
		return userGroupMap;
	}
	
	public ConcurrentHashMap<String, Client> getClientMap() {
		return userClientMap;
	}

	@Override
	public String getPrimaryKey() {
		return this.getDatabasePrefix();
	}

	
	/*
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

		table.addDatatype(this.getClass(),databasePrefixField, MaxDBTable.DATA_MYSQL_TYPE_HUGE_KEY_STRING);
		table.setPrimaryKeyName(databasePrefixField);
		table.createTable();
		
	}

	@Override
	public void setupDBDatatypes() {
		if (dbDatatypes == null) {
			dbDatatypes = new HashMap<String, Class<?>>();
		}
		if (USE_BETTER_DB_DATATYPES) {
			if (!betterDbDatatypes.containsKey(this.getClass())) {
				betterDbDatatypes.put(this.getClass(), new HashMap<>());
			}
			
			betterDbDatatypes.get(this.getClass()).put(databasePrefixField, String.class);
		} else {
			dbDatatypes.put(databasePrefixField, String.class);
		}
		
	}
	*/
}
