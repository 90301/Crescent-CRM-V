/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package clientInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dbUtils.InhalerUtils;
import dbUtils.MaxDBTable;
import dbUtils.MaxObject;
import debugging.Debugging;

public class Client extends MaxObject {

	private Location location;
	public static final String locationField = "locationName";
	private Status status;
	public static final String statusField = "statusName";
	private Group group;
	public static final String groupField = "groupName";
	private String name;
	public static final String nameField = "name";
	private String notes;
	public static final String notesField = "notes";
	private Boolean contactNow = false;
	public static final String contactNowField = "contactNow";
	// The ID for the map that will hold all this information (or the id for the
	// item in the database)
	private String id;
	public static final String idField = "id";
	private Boolean mutex = false;// Do not update internal db if true
	// Dates
	private java.util.Date lastUpdated = new Date();
	public static final String lastUpdatedField = "lastUpdated";
	// CUSTOM FIELDS

	private Map<String, ClientField> clientFields = new HashMap<String, ClientField>();
	public static final String clientFieldsField = "clientFields";

	// private UserDataHolder userDataHolder;
	/*
	 * TYPE CHECKING MUST BE DONE To insure values are not null before creating
	 * the client
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
		System.out.println("Generated ID: " + genID);
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
		// null check everything
		/*
		 * if (dbMap.get(lastUpdatedField)!=null) this.lastUpdated =
		 * (java.util.Date) dbMap.get(lastUpdatedField);
		 */
		lastUpdated = safeLoadFromInternalMap(lastUpdatedField, new Date());
		contactNow = safeLoadFromInternalMap(contactNowField, false);

		String clientXML = safeLoadFromInternalMap(clientFieldsField, "");
		loadCustomFields(clientXML);

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
			this.dbMap.put(idField, this.id);
			this.dbMap.put(lastUpdatedField, this.lastUpdated);
			this.dbMap.put(contactNowField, this.contactNow);

			this.dbMap.put(clientFieldsField, this.genFieldXml());
		}

	}

	/**
	 * Sets up the map of field names to datatypes MUST be updated when adding
	 * new fields
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
		dbDatatypes.put(contactNowField, Boolean.class);

		dbDatatypes.put(clientFieldsField, String.class);

	}

	public void createTableForClass(MaxDBTable table) {
		table.addDatatype(nameField, MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING);
		table.addDatatype(idField, MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING);
		table.addDatatype(notesField, MaxDBTable.DATA_MYSQL_TYPE_STRING);
		table.addDatatype(groupField, MaxDBTable.DATA_MYSQL_TYPE_STRING);
		table.addDatatype(statusField, MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING);
		table.addDatatype(locationField, MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING);
		table.addDatatype(lastUpdatedField, MaxDBTable.DATA_MYSQL_TYPE_DATE_TIME);
		table.addDatatype(contactNowField, MaxDBTable.DATA_MYSQL_TYPE_BOOLEAN);

		table.addDatatype(clientFieldsField, MaxDBTable.DATA_MYSQL_TYPE_STRING);

		table.setPrimaryKeyName(nameField);
		table.createTable();
	}

	// ----[ Custom Fields ]
	// -------------------------------------------------------------------

	/**
	 * Generates xml to serialize the custom fields
	 * 
	 * @return
	 */
	public String genFieldXml() {
		String xml = "";

		// Convert to a map of strings
		HashMap<String, String> tempMap = new HashMap<String, String>();

		for (ClientField cf : clientFields.values()) {
			tempMap.put(cf.getFieldName(), cf.getStringFieldValue());
		}

		xml = InhalerUtils.mapToXML(tempMap);

		return xml;
	}

	/**
	 * Loads custom fields from the xml data.
	 * 
	 * @param xml
	 */
	public void loadCustomFields(String xml) {

		HashMap<String, String> tempMap = new HashMap<String, String>();

		tempMap = InhalerUtils.xmlToMap(xml);
		// TODO finish this method
		for (String key : tempMap.keySet()) {

			String value = tempMap.get(key);
			// create a new client field
			ClientField cf = new ClientField();
			cf.setUserDataHolder(userDataHolder);

			// Check to see if a template field of the same name exists

			TemplateField tf = userDataHolder.getMap(TemplateField.class).get(key);

			if (tf != null) {
				cf.setCurrentDataType(tf.getDataType());
				cf.setFieldName(key);
				cf.setFieldValue(value);

				clientFields.put(key, cf);
			}

		}

	}

	public void setupCustomFieldsFromTemplate() {
		for (String key : userDataHolder.getMap(TemplateField.class).keySet()) {

			if (clientFields.containsKey(key)) {
				ClientField cf = this.clientFields.get(key);

				cf.setUserDataHolder(userDataHolder);

				cf.typeCheck();

			} else {
				// add the field if it doesn't exist

				ClientField cf = new ClientField();
				cf.setUserDataHolder(userDataHolder);

				// Check to see if a template field of the same name exists

				TemplateField tf = userDataHolder.getMap(TemplateField.class).get(key);

				if (tf != null) {
					cf.setCurrentDataType(tf.getDataType());
					cf.setFieldName(key);
					cf.setFieldValue(tf.getDefaultValue());

					clientFields.put(key, cf);
				}

			}
		}
	}

	public void setCustomFieldValue(String fieldName, Object fieldValue) {

		Debugging.output("Setting custom field: " + fieldName + " TO: " + fieldValue, Debugging.CLIENT_FIELD_DEBUGGING,
				Debugging.CLIENT_FIELD_DEBUGGING_ENABLED);

		// if the field isn't found, setup the fields from a template

		// this assumes the field would be included in the template
		if (!clientFields.containsKey(fieldName)) {
			setupCustomFieldsFromTemplate();
			Debugging.output("Field Not found in client: " + fieldName + " setup custom fields starting", Debugging.CLIENT_FIELD_DEBUGGING,
					Debugging.CLIENT_FIELD_DEBUGGING_ENABLED);

		}
		if (!userDataHolder.getMap(TemplateField.class).containsKey(fieldName)) {
			// ERROR CONDITION!
			// field not found in the template fields
			Debugging.output("ERROR: Field not found in template " + fieldName, Debugging.CLIENT_FIELD_DEBUGGING,
					Debugging.CLIENT_FIELD_DEBUGGING_ENABLED);

			return;
		}

		clientFields.get(fieldName).setFieldValue(fieldValue);
		
		Debugging.output("New custom Field: " + fieldName + " Value: " + clientFields.get(fieldName).getFieldValue(), Debugging.CLIENT_FIELD_DEBUGGING,
				Debugging.CLIENT_FIELD_DEBUGGING_ENABLED);
		
		this.updateDBMap();
	}

	public Object getCustomFieldValue(String fieldName) {
		// if the field isn't found, setup the fields from a template

		// this assumes the field would be included in the template
		if (!clientFields.containsKey(fieldName)) {
			setupCustomFieldsFromTemplate();

		}
		if (!userDataHolder.getMap(TemplateField.class).containsKey(fieldName)) {
			// ERROR CONDITION!
			// field not found in the template fields

			return null;
		}
		return clientFields.get(fieldName).getFieldValue();

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
		// this.lastUpdated =
		// Math.toIntExact(System.currentTimeMillis()/DATE_MULTIPLIER);
		this.lastUpdated = new Date();
		updateDBMap();
		System.out.println("Last updated: " + lastUpdated);
	}

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

	public void setContactNow(Boolean contactNow) {
		this.contactNow = contactNow;
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
		if (this.group != null) {
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

	public static final java.text.SimpleDateFormat SIMPLE_DATE_FORMAT = new java.text.SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public String getStringLastUpdated() {

		String strLastUpdated = SIMPLE_DATE_FORMAT.format(lastUpdated);
		return strLastUpdated;
	}

	public Boolean getContactNow() {
		return contactNow;
	}

}
