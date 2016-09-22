package clientInfo;

import java.util.Collection;

import dbUtils.MaxDBTable;
import dbUtils.MaxObject;

public class Status extends MaxObject {

	private String statusName = ""; public static final String statusNameField = "statusName";
	private Integer statusColor = 0; public static final String statusColorField = "color";
	public Status() {
		updateDBMap();
	}

	
	@Override
	public String toString() {
		return statusName;
	}


	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
		dbMap.put(statusNameField, this.statusName);
	}
	
	@Override
	public void setupDBDatatypes() {
		if (dbDatatypes.isEmpty()) {
			
			dbDatatypes.put(statusNameField, String.class);
			dbDatatypes.put(statusColorField, Integer.class);			
			
		}
		
	}

	public void createTableForClass(MaxDBTable table) {
		table.addDatatype(statusNameField, MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING);
		table.addDatatype(statusColorField, MaxDBTable.DATA_MYSQL_TYPE_INT);
		table.setPrimaryKeyName(statusNameField);
		table.createTable();
	}
	@Override
	public void loadInternalFromMap() {
		statusName = (String) dbMap.get(statusNameField);
		statusColor = (Integer) dbMap.get(statusColor);
	}
	@Override
	public void updateDBMap() {
		dbMap.put(statusNameField, statusName);
		dbMap.put(statusColorField, statusColor);
	}


	@Override
	public String getPrimaryKey() {
		// TODO Auto-generated method stub
		return this.getStatusName();
	}


	

}
