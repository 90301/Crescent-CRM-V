package clientInfo;

import java.util.Date;

import com.vaadin.ui.components.calendar.event.BasicEvent;

import dbUtils.MaxDBTable;
import dbUtils.MaxObject;
import debugging.Debugging;

public class ScheduleEvent extends MaxObject {

	
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
	
	
	
	
	
	public void genKey()
	{
		key = this.user + this.eventName + this.start.toString();
		Debugging.output("key: " + key, Debugging.SCHEDULE_EVENT_OUTPUT, Debugging.SCHEDULE_EVENT_OUTPUT_ENABLED);
	}
	
	public BasicEvent genBasicEvent()
	{
		BasicEvent event = new BasicEvent(eventName, eventDescription, start, end);
		return event;
	}
	
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
	public String getPrimaryKey() {
		
		return key;
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
	
	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
		updateDBMap();
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
		updateDBMap();
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
		updateDBMap();
	}

	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
		updateDBMap();
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
		updateDBMap();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
		updateDBMap();
	}

	public String getRepeat() {
		return repeat;
	}

	public void setRepeat(String repeat) {
		this.repeat = repeat;
		updateDBMap();
	}

}
