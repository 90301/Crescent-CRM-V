/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package clientInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.data.util.ObjectProperty;

import dbUtils.InhalerUtils;
import dbUtils.MaxDBTable;
import dbUtils.MaxField;
import dbUtils.MaxObject;
import dbUtils.Conversions.ClientFieldMapToString;
import dbUtils.Conversions.LinkedHashMapToString;
import dbUtils.Conversions.UdhMaxObjectToString;
import debugging.Debugging;

public class Client extends MaxObject implements Item {

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
	
	MaxField<LinkedHashMap<String, String>> noteHistory = new MaxField<LinkedHashMap<String, String>>("noteHistory",
			MaxDBTable.DATA_MYSQL_TYPE_STRING, new LinkedHashMap<String, String>(), new LinkedHashMap<String, String>(),
			this);
	
	//Conversions
	
	UdhMaxObjectToString<Location> locationConversion = new UdhMaxObjectToString<Location>();
	UdhMaxObjectToString<Status> statusConversion = new UdhMaxObjectToString<Status>();
	UdhMaxObjectToString<Group> groupConversion = new UdhMaxObjectToString<Group>();
	ClientFieldMapToString customFieldConversion = new ClientFieldMapToString();
	LinkedHashMapToString noteHistoryConversion = new LinkedHashMapToString();

	private String currentNoteHistoryKey;
	
	public static final String LOCATION_GRID_NAME = "Location";
	public static final String STATUS_GRID_NAME = "Status";
	public static final String GROUP_GRID_NAME = "Group";
	public static final String LAST_UPDATED_GRID_NAME = "Last Updated";
	
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
		
		//note history
		noteHistoryConversion.setStoreRef(String.class);
		noteHistoryConversion.setDefaultStoreValue("");
		noteHistoryConversion.setUserDataHolder(userDataHolder);
		
		location.setConversion(locationConversion);
		status.setConversion(statusConversion);
		group.setConversion(groupConversion);
		clientFields.setConversion(customFieldConversion);
		noteHistory.setConversion(noteHistoryConversion);
		
		//Hide certain fields for the grid
		
		notes.setShowField(false);
		contactNow.setShowField(false);
		clientFields.setShowField(false);
		profilePicture.setShowField(false);
		noteHistory.setShowField(false);
		
		location.setGridName(LOCATION_GRID_NAME);
		status.setGridName(STATUS_GRID_NAME);
		group.setGridName(GROUP_GRID_NAME);
		lastUpdated.setGridName(LAST_UPDATED_GRID_NAME);
		
		
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
		addMaxField(noteHistory);
		
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

		Debugging.output("Last updated: " + lastUpdated,Debugging.OLD_OUTPUT);
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
		updateNoteHistory();
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
	
	public LinkedHashMap<String,String> getNoteHistory() {
		return noteHistory.getFieldValue();
	}
	
	public void setNoteHistory(LinkedHashMap<String,String> noteHistory) {
		this.noteHistory.setFieldValue(noteHistory);
	}
	
	
	
	/*
	 * Note History
	 */
	
	public void updateNoteHistory() {
		//check last note history, if it's not the same as the current notes add
		//another entry to the history
		//TODO
		Debugging.output("Updating note history for: " + this.getName(), Debugging.NOTE_HISTORY);
		
		LinkedHashMap<String, String> his = this.getNoteHistory();
		String currentNote = this.getNotes();
		
		for (String oldNoteKey : his.keySet()) {
			String oldNote = his.get(oldNoteKey);
			
			Debugging.output("Checking for same text: " + currentNote + " | " +  oldNoteKey + " : " + oldNote, Debugging.NOTE_HISTORY);
			
			if (currentNote.equals(oldNote)) {
				this.currentNoteHistoryKey = oldNoteKey;
				
				Debugging.output("Found matching note: " + oldNoteKey, Debugging.NOTE_HISTORY);
				
				return;
			}
		}
		
		Debugging.output("Note Change detected: " + currentNote, Debugging.NOTE_HISTORY);
		//the current note is not in the history. add it, and set the pointer
		String key = addNoteHistory(currentNote);
		this.currentNoteHistoryKey = key;
		
	}
	public static final SimpleDateFormat historyDateFormat = new SimpleDateFormat("MMM-dd-yyyy");
	private String addNoteHistory(String note) {
		String key = "V" +getNoteHistory().size() + "_" + historyDateFormat.format(new Date());
		
		Debugging.output("Adding note history: " + key + " | " + note, Debugging.NOTE_HISTORY);
		
		LinkedHashMap<String,String> nHis = getNoteHistory();
		nHis.put(key, note);
		
		setNoteHistory(nHis);
		return key;
	}
	/*
	 * VAADIN ITEM Allows addition to special data-structures
	 */

	@Override
	public Property getItemProperty(Object id) {
		HashMap<String, MaxField<?>> fields = new HashMap<String, MaxField<?>>();
		for (MaxField<?> mf : this.autoGenList) {
			if (mf.getShowField())
				fields.put(mf.getGridName(), mf);
		}

		MaxField<?> f = fields.get(id);
		Debugging.output("Item property from field: " + f,Debugging.CLIENT_GRID_DEBUG);

		@SuppressWarnings({ "unchecked", "rawtypes" })
		ObjectProperty prop = new ObjectProperty(f.getGridName(), f.getExtendedClass());
		// prop.setValue(f.getFieldValue());

		return prop;
	}

	@Override
	public Collection<?> getItemPropertyIds() {
		Collection<String> ids = new ArrayList<String>();
		for (MaxField<?> mf : this.autoGenList) {
			if (mf.getShowField())
				ids.add(mf.getGridName());
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
				indexedContainer.addContainerProperty(field.getGridName(), field.getExtendedClass(),
				field.getDefaultFieldValue());
			
				Debugging.output(" Populating indexContatiner with: " + field + 
						" FieldName: " + field.getGridName() + 
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
				Property p = item.getItemProperty(mf.getGridName());
				p.setValue(mf.getFieldValue());
			}
		}
		return item;
	}
}
