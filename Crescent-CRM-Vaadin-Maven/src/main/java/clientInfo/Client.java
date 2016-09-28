package clientInfo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dbUtils.MaxDBTable;
import dbUtils.MaxObject;

public class Client extends MaxObject {
	
	private Location location; public static final String locationField = "locationName";
	private Status status; public static final String statusField = "statusName";
	private Group group; public static final String groupField = "groupName";
	private String name; public static final String nameField = "name";
	private String notes; public static final String notesField = "notes";
	//The ID for the map that will hold all this information (or the id for the item in the database)
	private String id; public static final String idField = "id";
	private Boolean mutex = false;//Do not update internal db if true
	//Dates
	private java.util.Date lastUpdated = new Date(); public static final String lastUpdatedField = "lastUpdated";
	
	//private UserDataHolder userDataHolder;
	/*
	 * TYPE CHECKING MUST BE DONE
	 * To insure values are not null before creating the client
	 * 
	 */
	public Client() {
		this.id = genId();
		updateDBMap();
	}
	public static int clientAddCount = 0;
	public static String genId() {
		clientAddCount++;

		String genID = System.currentTimeMillis() + " X " + clientAddCount;
		System.out.println("Generated ID: " + genID );
		return genID;
	}
	
	@Override
	public String toString() {
		return "Client [location=" + location + ", status=" + status + ", group=" + group + ", name=" + name
				+ ", notes=" + notes + ", id=" + id + ", mutex=" + mutex + "]";
	}

	/**
	 * UPDATE ON ADDING FIELDS
	 */
	@Override
	public void loadInternalFromMap() {
		mutex = true;
		this.setLocation((String) dbMap.get(locationField));
		this.setStatus((String) dbMap.get(statusField));
		this.setGroup((String) dbMap.get(groupField));
		this.name = (String) dbMap.get(nameField);
		this.notes = (String) dbMap.get(notesField);
		this.id = (String) dbMap.get(idField);
		
		//null check everything
		if (dbMap.get(lastUpdatedField)!=null)
		this.lastUpdated = (java.util.Date) dbMap.get(lastUpdatedField);
		mutex = false;
	}
	
	/**
	 * UPDATE ON ADDING FIELDS
	 */
	@Override
	public void updateDBMap() {
		if (!mutex) {
			this.dbMap.put(locationField, this.getLocationName());
			this.dbMap.put(statusField, this.getStatusName());
			this.dbMap.put(groupField, this.getGroupName());
			this.dbMap.put(nameField, this.name);
			this.dbMap.put(notesField, this.notes);
			this.dbMap.put(idField,this.id);
			this.dbMap.put(lastUpdatedField, this.lastUpdated);
		}
		
	}
	
	/**
	 * Sets up the map of field names to datatypes
	 * MUST be updated when adding new fields
	 * 
	 * UPDATE ON ADDING FIELDS
	 */
	@Override
	public void setupDBDatatypes() {
		if (dbDatatypes == null) {
			dbDatatypes = new HashMap<String, Class<?>>();
		}
		
			System.out.println("generated db type for: " + this.getClass());
			dbDatatypes.put(locationField, String.class);
			dbDatatypes.put(statusField, String.class);			
			dbDatatypes.put(groupField, String.class);
			
			dbDatatypes.put(nameField, String.class);
			dbDatatypes.put(notesField, String.class);
			dbDatatypes.put(idField, String.class);
			dbDatatypes.put(lastUpdatedField, java.util.Date.class);
			
		
	}
	
	
	public void createTableForClass(MaxDBTable table) {
		table.addDatatype(nameField, MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING);
		table.addDatatype(idField, MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING);
		table.addDatatype(notesField, MaxDBTable.DATA_MYSQL_TYPE_STRING);
		table.addDatatype(groupField, MaxDBTable.DATA_MYSQL_TYPE_STRING);
		table.addDatatype(statusField, MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING);
		table.addDatatype(locationField, MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING);
		table.addDatatype(lastUpdatedField, MaxDBTable.DATA_MYSQL_TYPE_DATE_TIME);
		table.setPrimaryKeyName(nameField);
		table.createTable();
	}
	
	
	
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
		updateDBMap();
	}
	
	public void setLocation(String locationName) {
		this.location = userDataHolder.getLocation(locationName);
		updateDBMap();
	}
	
	public void setLastUpdatedToNow() {
		//this.lastUpdated = Math.toIntExact(System.currentTimeMillis()/DATE_MULTIPLIER);
		this.lastUpdated = new Date();
		updateDBMap();
		System.out.println("Last updated: " + lastUpdated);
	}
	/*
	public long getUsableLastUpdated() {
		return this.lastUpdated * DATE_MULTIPLIER;
	}
	*/
	//public static final Long DATE_MULTIPLIER = 100000L;
	
	public String getLocationName() {
		String locName = null; 
		if (this.location != null) {
		locName = this.location.getLocationName();
		}
		
		return locName;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
		updateDBMap();
	}
	
	public void setStatus(String statusName) {
		this.status = userDataHolder.getStatus(statusName);
		updateDBMap();
	}
	
	public String getStatusName() {
		String statName = null; 
		if (this.status != null) {
			statName = status.getStatusName();
		}
		return statName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		updateDBMap();
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
		updateDBMap();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		updateDBMap();
	}





	public Group getGroup() {
		return group;
	}


	public void setGroup(Group group) {
		this.group = group;
		updateDBMap();
	}
	
	public void setGroup(String groupName) {
		this.group = userDataHolder.getGroup(groupName);
		updateDBMap();
	}
	
	public String getGroupName() {
		String groupName = null;
		if (this.group!=null) {
			groupName = this.group.getPrimaryKey();
		}
		return groupName; 
	}

	@Override
	public String getPrimaryKey() {
		// TODO Auto-generated method stub
		return this.getName();
	}

	public java.util.Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(java.util.Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public static final java.text.SimpleDateFormat SIMPLE_DATE_FORMAT = 
		     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public String getStringLastUpdated() {

			String strLastUpdated = SIMPLE_DATE_FORMAT.format(lastUpdated);
			return strLastUpdated;
	}

	/*
	public UserDataHolder getUserDataHolder() {
		return userDataHolder;
	}

	public void setUserDataHolder(UserDataHolder userDataHolder) {
		this.userDataHolder = userDataHolder;
	}
	*/

}
