/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package clientInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.ObjectProperty;

import dbUtils.InhalerUtils;
import dbUtils.MaxDBTable;
import dbUtils.MaxField;
import dbUtils.MaxObject;
import dbUtils.Conversions.ClientFieldMapToString;
import dbUtils.Conversions.UdhMaxObjectToString;
import debugging.Debugging;

public class Client extends MaxObject implements Item {

	/*
	 * private Location location; public static final String locationField =
	 * "locationName"; private Status status; public static final String
	 * statusField = "statusName"; private Group group; public static final
	 * String groupField = "groupName"; private String name; public static final
	 * String nameField = "name"; private String notes; public static final
	 * String notesField = "notes"; private Boolean contactNow = false; public
	 * static final String contactNowField = "contactNow"; // Dates private
	 * java.util.Date lastUpdated = new Date(); public static final String
	 * lastUpdatedField = "lastUpdated";
	 * 
	 * 
	 * // CUSTOM FIELDS
	 * 
	 * private Map<String, ClientField> clientFields = new HashMap<String,
	 * ClientField>(); public static final String clientFieldsField =
	 * "clientFields";
	 */
	// The ID for the map that will hold all this information (or the id for the
	// item in the database)
	// private String id;
	// public static final String idField = "id";

	private Boolean mutex = false;// Do not update internal db if true

	MaxField<String> name = new MaxField<String>("name", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "", this);

	MaxField<Location> location = new MaxField<Location>("locationName", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING,
			new Location(), new Location(), this);
	MaxField<Status> status = new MaxField<Status>("statusName", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, new Status(),
			new Status(), this);
	MaxField<Group> group = new MaxField<Group>("groupName", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, new Group(),
			new Group(), this);

	MaxField<String> notes = new MaxField<String>("notes", MaxDBTable.DATA_MYSQL_TYPE_STRING, "", "", this);
	
	MaxField<Boolean> contactNow = new MaxField<Boolean>("contactNow", MaxDBTable.DATA_MYSQL_TYPE_BOOLEAN, false, false,
			this);
	MaxField<Date> lastUpdated = new MaxField<Date>("lastUpdated", MaxDBTable.DATA_MYSQL_TYPE_DATE_TIME, new Date(), new Date(), this);

	MaxField<Map<String, ClientField>> clientFields = new MaxField<Map<String, ClientField>>("clientFields",
			MaxDBTable.DATA_MYSQL_TYPE_STRING, new HashMap<String, ClientField>(), new HashMap<String, ClientField>(),
			this);
	
	MaxField<String> profilePicture = new MaxField<String>("profilePicture", MaxDBTable.DATA_MYSQL_TYPE_STRING, "", "", this);
	
	//Conversions
	
	UdhMaxObjectToString<Location> locationConversion = new UdhMaxObjectToString<Location>();
	UdhMaxObjectToString<Status> statusConversion = new UdhMaxObjectToString<Status>();
	UdhMaxObjectToString<Group> groupConversion = new UdhMaxObjectToString<Group>();
	ClientFieldMapToString customFieldConversion = new ClientFieldMapToString();
	
	{
		//set up conversions
		
		
		locationConversion.setUserDataHolder(userDataHolder);//THIS MAY NOT WORK
		locationConversion.setStoreRef(String.class);
		locationConversion.setRef(Location.class);
		locationConversion.setDefaultStoreValue("");
		
		statusConversion.setUserDataHolder(userDataHolder);
		statusConversion.setStoreRef(String.class);
		statusConversion.setRef(Status.class);
		statusConversion.setDefaultStoreValue("");
		
		groupConversion.setUserDataHolder(userDataHolder);
		groupConversion.setStoreRef(String.class);
		groupConversion.setRef(Group.class);
		groupConversion.setDefaultStoreValue("");
		
		
		//Custom Fields
		customFieldConversion.setStoreRef(String.class);
		customFieldConversion.setUserDataHolder(userDataHolder);
		customFieldConversion.setDefaultStoreValue("");
		
		location.setConversion(locationConversion);
		status.setConversion(statusConversion);
		group.setConversion(groupConversion);
		clientFields.setConversion(customFieldConversion);
		
		//Hide certain fields for the grid
		
		notes.setShowField(false);
		contactNow.setShowField(false);
		clientFields.setShowField(false);
		profilePicture.setShowField(false);
		
		this.setKeyField(name);
		addMaxField(name);
		addMaxField(location);
		addMaxField(status);
		addMaxField(group);
		addMaxField(notes);
		addMaxField(contactNow);
		addMaxField(lastUpdated);
		addMaxField(clientFields);
		addMaxField(profilePicture);
		
	}

	// private UserDataHolder userDataHolder;
	/*
	 * TYPE CHECKING MUST BE DONE To insure values are not null before creating
	 * the client
	 * 
	 */
	public Client() {
		//this.id = genId();
		updateDBMap();
	}
	
	public static int clientAddCount = 0;

	@Override
	public String toString() {
		return "Client [location=" + location + ", status=" + status + ", group=" + group + ", name=" + name
				+ ", notes=" + notes + ", mutex=" + mutex + "]";
	}


	/*
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
		lastUpdated = safeLoadFromInternalMap(lastUpdatedField, new Date());
		contactNow = safeLoadFromInternalMap(contactNowField, false);

		String clientXML = safeLoadFromInternalMap(clientFieldsField, "");
		loadCustomFields(clientXML);

		mutex = false;
	}


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
	*/

	// ----[ Custom Fields ]
	// -------------------------------------------------------------------

	/**
	 * Generates xml to serialize the custom fields
	 * 
	 * @return
	 */
	/*
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
	*/

	/**
	 * Loads custom fields from the xml data.
	 * 
	 * @param xml
	 */
	/*
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
	*/

	public void setupCustomFieldsFromTemplate() {
		
		if (userDataHolder == null) {
			System.err.println("Failed to set user data holder for client");
		}
		
		
		for (String key : userDataHolder.getMap(TemplateField.class).keySet()) {

			if (getClientFields().containsKey(key)) {
				ClientField cf = this.getClientFields().get(key);

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

					//Need a special method for adding to a client field?
					getClientFields().put(key, cf);
				}

			}
		}
		
		this.updateDBMap();
	}

	public void setCustomFieldValue(String fieldName, Object fieldValue) {

		Debugging.output("Setting custom field: " + fieldName + " TO: " + fieldValue, Debugging.CLIENT_FIELD_DEBUGGING,
				Debugging.CLIENT_FIELD_DEBUGGING_ENABLED);

		// if the field isn't found, setup the fields from a template

		// this assumes the field would be included in the template
		if (!getClientFields().containsKey(fieldName)) {
			setupCustomFieldsFromTemplate();
			Debugging.output("Field Not found in client: " + fieldName + " setup custom fields starting",
					Debugging.CLIENT_FIELD_DEBUGGING, Debugging.CLIENT_FIELD_DEBUGGING_ENABLED);

		}
		if (!userDataHolder.getMap(TemplateField.class).containsKey(fieldName)) {
			// ERROR CONDITION!
			// field not found in the template fields
			Debugging.output("ERROR: Field not found in template " + fieldName, Debugging.CLIENT_FIELD_DEBUGGING,
					Debugging.CLIENT_FIELD_DEBUGGING_ENABLED);

			return;
		}

		getClientFields().get(fieldName).setFieldValue(fieldValue);

		Debugging.output("New custom Field: " + fieldName + " Value: " + getClientFields().get(fieldName).getFieldValue(),
				Debugging.CLIENT_FIELD_DEBUGGING, Debugging.CLIENT_FIELD_DEBUGGING_ENABLED);

		this.updateDBMap();
	}

	public Object getCustomFieldValue(String fieldName) {
		// if the field isn't found, setup the fields from a template

		// this assumes the field would be included in the template
		if (!getClientFields().containsKey(fieldName)) {
			setupCustomFieldsFromTemplate();

		}
		if (!userDataHolder.getMap(TemplateField.class).containsKey(fieldName)) {
			// ERROR CONDITION!
			// field not found in the template fields

			return null;
		}
		return getClientFields().get(fieldName).getFieldValue();

	}

	private Map<String, ClientField> getClientFields() {
		// TODO Auto-generated method stub
		return clientFields.getFieldValue();
	}


	public Location getLocation() {
		return location.getFieldValue();
	}

	public void setLocation(Location location) {
		this.location.setFieldValue(location);
	}

	public void setLocation(String locationName) {
		this.location.setFieldValue(userDataHolder.getLocation(locationName));
	}

	public void setLastUpdatedToNow() {
		// this.lastUpdated =
		// Math.toIntExact(System.currentTimeMillis()/DATE_MULTIPLIER);
		this.lastUpdated.setFieldValue(new Date());

		System.out.println("Last updated: " + lastUpdated);
	}

	public String getLocationName() {
		String locName = null;
		if (this.location.getFieldValue() != null) {
			locName = this.location.getFieldValue().getLocationName();
		}

		return locName;
	}

	public Status getStatus() {
		return status.getFieldValue();
	}

	public void setStatus(Status status) {
		this.status.setFieldValue(status);
	}

	public void setStatus(String statusName) {
		this.status.setFieldValue(userDataHolder.getStatus(statusName));
	}

	public String getStatusName() {
		String statName = null;
		if (this.status.getFieldValue() != null) {
			statName = status.getFieldValue().getPrimaryKey();
		}
		return statName;
	}

	public String getName() {
		return name.getFieldValue();
	}

	public void setName(String name) {
		this.name.setFieldValue(name);
	}

	public String getNotes() {
		return notes.getFieldValue();
	}

	public void setNotes(String notes) {
		this.notes.setFieldValue(notes);
	}
	
	public void setContactNow(Boolean contactNow) {
		this.contactNow.setFieldValue(contactNow);
	}

	public Group getGroup() {
		return group.getFieldValue();
	}

	public void setGroup(Group group) {
		this.group.setFieldValue(group);
	}

	public void setGroup(String groupName) {
		
		Group g = userDataHolder.getGroup(groupName);
		
		this.group.setFieldValue(g);
	}

	public String getGroupName() {
		String groupName = null;
		if (this.group.getFieldValue() != null) {
			groupName = this.group.getFieldValue().getPrimaryKey();
		}
		return groupName;
	}
	
	public String getProfilePicture() {
		return profilePicture.getFieldValue();
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture.setFieldValue(profilePicture);
	}

	@Override
	public String getPrimaryKey() {
		// TODO Auto-generated method stub
		return this.getName();
	}

	public java.util.Date getLastUpdated() {
		return lastUpdated.getFieldValue();
	}

	public void setLastUpdated(java.util.Date lastUpdated) {
		this.lastUpdated.setFieldValue(lastUpdated);
	}

	public static final java.text.SimpleDateFormat SIMPLE_DATE_FORMAT = new java.text.SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public String getStringLastUpdated() {

		String strLastUpdated = SIMPLE_DATE_FORMAT.format(lastUpdated);
		return strLastUpdated;
	}

	public Boolean getContactNow() {
		return contactNow.getFieldValue();
	}

	/*
	 * VAADIN ITEM Allows addition to special data-structures
	 */

	@Override
	public Property getItemProperty(Object id) {
		HashMap<String, MaxField<?>> fields = new HashMap<String, MaxField<?>>();
		for (MaxField<?> mf : this.autoGenList) {
			if (mf.getShowField())
				fields.put(mf.getFieldName(), mf);
		}

		MaxField<?> f = fields.get(id);
		Debugging.output("Item property from field: " + f,Debugging.CLIENT_GRID_DEBUG);

		@SuppressWarnings({ "unchecked", "rawtypes" })
		ObjectProperty prop = new ObjectProperty(f.getFieldValue(), f.getExtendedClass());
		// prop.setValue(f.getFieldValue());

		return prop;
	}

	@Override
	public Collection<?> getItemPropertyIds() {
		Collection<String> ids = new ArrayList<String>();
		for (MaxField<?> mf : this.autoGenList) {
			if (mf.getShowField())
				ids.add(mf.getFieldName());
		}

		return ids;
	}

	@Override
	public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	public IndexedContainer populateContainer(IndexedContainer indexedContainer) {
		for (MaxField<?> field : this.getAutoGenList()) {
			if (field.getShowField())
				indexedContainer.addContainerProperty(field.getFieldName(), field.getExtendedClass(),
				field.getDefaultFieldValue());
			
				Debugging.output(" Populating indexContatiner with: " + field + 
						" FieldName: " + field.getFieldName() + 
						" Extended Class" + field.getExtendedClass()
						,Debugging.CLIENT_GRID_DEBUG);
			}
		
		Debugging.output(Debugging.LINE ,Debugging.CLIENT_GRID_DEBUG);
		
		return indexedContainer;
	}
	
	/**
	 * Generates an item that can be added to a grid.
	 * 
	 * @param item
	 *            the item to generate data into
	 * @return the item with data generated
	 */
	public Item genItem(Item item) {

		for (MaxField<?> mf : this.getAutoGenList()) {
			if (mf.getShowField()) {
				Property p = item.getItemProperty(mf.getFieldName());
				p.setValue(mf.getFieldValue());
			}
		}
		return item;
	}
}
