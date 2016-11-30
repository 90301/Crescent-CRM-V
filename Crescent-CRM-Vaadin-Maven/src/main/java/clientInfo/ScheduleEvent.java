package clientInfo;

import java.util.Date;
import java.util.UUID;

import com.vaadin.ui.components.calendar.event.BasicEvent;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEvent.EventChangeNotifier;

import dbUtils.InhalerUtils;
import dbUtils.MaxDBTable;
import dbUtils.MaxField;
import dbUtils.MaxObject;
import debugging.Debugging;

public class ScheduleEvent extends MaxObject implements CalendarEvent, EventChangeNotifier {

	/*
	private Date start = new Date();
	public static final String START_FIELD = "startDate";
	
	private Date end = new Date();
	public static final String END_FIELD = "endDate";
	
	private String eventName;
	public static final String EVENTNAME_FIELD = "eventName";
	
	private String eventDescription;
	public static final String EVENTDESCRIPTION_FIELD = "eventDescription";
	
	private String user;
	public static final String USER_FIELD = "eventUser";
	
	private String key;
	public static final String KEY_FIELD = "eventKey";
	
	private String repeat = "";
	public static final String REPEAT_FIELD = "eventRepeat";
	*/
	MaxField<Date> start = new MaxField<Date>("startDate",MaxDBTable.DATA_MYSQL_TYPE_DATE_TIME,new Date(),new Date(),this);
	MaxField<Date> end = new MaxField<Date>("endDate",MaxDBTable.DATA_MYSQL_TYPE_DATE_TIME,new Date(),new Date(),this);
	
	MaxField<String> eventName = new MaxField<String>("eventName",MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING,"","",this);
	MaxField<String> eventDescription = new MaxField<String>("eventDescription",MaxDBTable.DATA_MYSQL_TYPE_STRING,"","",this);
	MaxField<String> user = new MaxField<String>("eventUser",MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING,"","",this);
	MaxField<String> key = new MaxField<String>("eventKey",MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING,"","",this);
	MaxField<String> repeat = new MaxField<String>("eventRepeat",MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING,"","",this);
	
	{
		this.setKeyField(key);
		
		this.addMaxField(start);
		this.addMaxField(end);
		this.addMaxField(eventName);
		this.addMaxField(eventDescription);
		this.addMaxField(user);
		this.addMaxField(key);
		this.addMaxField(repeat);
	}
	
	/**
	 * Generates a random key and sets the key to that random key
	 */
	public void genKey()
	{
		this.setKey(InhalerUtils.genRandomKey());
		//key = this.user + this.eventName + this.start.toString();
		Debugging.output("key: " + key, Debugging.SCHEDULE_EVENT_OUTPUT, Debugging.SCHEDULE_EVENT_OUTPUT_ENABLED);
	}
	
	/**
	 * Creates a vaadin basic event from the data in
	 * this object. Please note this is likely to change
	 * @return a basic event
	 */
	public BasicEvent genBasicEvent()
	{
		BasicEvent event = new BasicEvent(getEventName(), getEventDescription(), getStart(), getEnd());
		
		return event;
	}
	
	@Override
	public String getPrimaryKey() {
		
		return getKey();
	}
	/*
	@Override
	public void loadInternalFromMap() {
		key = (String) dbMap.get(KEY_FIELD);
		start = (Date) dbMap.get(START_FIELD);
		end = (Date) dbMap.get(END_FIELD);
		eventName = (String) dbMap.get(EVENTNAME_FIELD);
		eventDescription = (String) dbMap.get(EVENTDESCRIPTION_FIELD);
		user = (String) dbMap.get(USER_FIELD);
		repeat = (String) dbMap.get(REPEAT_FIELD);
	}

	@Override
	public void updateDBMap() {
		dbMap.put(KEY_FIELD, key);
		dbMap.put(START_FIELD, start);
		dbMap.put(END_FIELD, end);
		dbMap.put(EVENTNAME_FIELD, eventName);
		dbMap.put(EVENTDESCRIPTION_FIELD, eventDescription);
		dbMap.put(USER_FIELD, user);
		dbMap.put(REPEAT_FIELD, repeat);
	}

	

	@Override
	public void createTableForClass(MaxDBTable table) {
		table.addDatatype(KEY_FIELD, MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING);
		table.addDatatype(START_FIELD, MaxDBTable.DATA_MYSQL_TYPE_DATE_TIME);
		table.addDatatype(END_FIELD, MaxDBTable.DATA_MYSQL_TYPE_DATE_TIME);
		table.addDatatype(EVENTNAME_FIELD, MaxDBTable.DATA_MYSQL_TYPE_STRING);
		table.addDatatype(EVENTDESCRIPTION_FIELD, MaxDBTable.DATA_MYSQL_TYPE_STRING);
		table.addDatatype(USER_FIELD, MaxDBTable.DATA_MYSQL_TYPE_STRING);
		table.addDatatype(REPEAT_FIELD, MaxDBTable.DATA_MYSQL_TYPE_STRING);
		
		table.setPrimaryKeyName(KEY_FIELD);
		table.createTable();
	}

	@Override
	public void setupDBDatatypes() {
		dbDatatypes.put(KEY_FIELD, String.class);
		dbDatatypes.put(START_FIELD, Date.class);
		dbDatatypes.put(END_FIELD, Date.class);
		dbDatatypes.put(EVENTNAME_FIELD, String.class);
		dbDatatypes.put(EVENTDESCRIPTION_FIELD, String.class);
		dbDatatypes.put(USER_FIELD, String.class);
		dbDatatypes.put(REPEAT_FIELD, String.class);
	}
	*/
	public Date getStart() {
		return start.getFieldValue();
	}

	public void setStart(Date start) {
		this.start.setFieldValue(start);
		updateDBMap();
	}

	public Date getEnd() {
		return end.getFieldValue();
	}

	public void setEnd(Date end) {
		this.end.setFieldValue(end);
		updateDBMap();
	}

	public String getEventName() {
		return eventName.getFieldValue();
	}

	public void setEventName(String eventName) {
		this.eventName.setFieldValue(eventName);
		updateDBMap();
	}

	public String getEventDescription() {
		return eventDescription.getFieldValue();
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription.setFieldValue(eventDescription);
		updateDBMap();
	}

	public String getUser() {
		return user.getFieldValue();
	}

	public void setUser(String user) {
		this.user.setFieldValue(user);
		updateDBMap();
	}

	public String getKey() {
		return key.getFieldValue();
	}

	public void setKey(String key) {
		this.key.setFieldValue(key);
		updateDBMap();
	}

	public String getRepeat() {
		return repeat.getFieldValue();
	}

	public void setRepeat(String repeat) {
		this.repeat.setFieldValue(repeat);
		updateDBMap();
	}

	
	/*
	 * TONY, let's walk through implementing these methods:
	 * -Josh
	 * https://vaadin.com/docs/-/part/framework/components/components-calendar.html
	 * 
	 */
	@Override
	public void addEventChangeListener(EventChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeEventChangeListener(EventChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCaption() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStyleName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAllDay() {
		// TODO Auto-generated method stub
		return false;
	}

}
