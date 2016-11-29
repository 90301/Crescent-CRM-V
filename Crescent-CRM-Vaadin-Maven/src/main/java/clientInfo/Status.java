/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package clientInfo;

import java.util.Collection;

import dbUtils.MaxDBTable;
import dbUtils.MaxField;
import dbUtils.MaxObject;

public class Status extends MaxObject {

	/*
	 * OLD CODE
	private String statusName = ""; public static final String statusNameField = "statusName";
	private Integer statusColor = 0; public static final String statusColorField = "color";
	*/
	MaxField<String> statusName = new MaxField<String>("statusName", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "", this);
	MaxField<Integer> statusColor = new MaxField<Integer>("color", MaxDBTable.DATA_MYSQL_TYPE_INT, 0, 0, this);
	
	{
		this.setKeyField(statusName);
		
		this.addMaxField(statusName);
		this.addMaxField(statusColor);
	}
	public Status() {
		updateDBMap();
	}

	
	@Override
	public String toString() {
		return statusName.getFieldValue();
	}


	public String getStatusName() {
		return statusName.getFieldValue();
	}
	public void setStatusName(String statusName) {
		
		this.statusName.setFieldValue(statusName);
		/*
		 this.statusName = statusName;
		
		dbMap.put(statusNameField, this.statusName);
		*/
	}
	/*
	@Override
	public void setupDBDatatypes() {
		//if (dbDatatypes.isEmpty()) {
			
			dbDatatypes.put(statusNameField, String.class);
			dbDatatypes.put(statusColorField, Integer.class);			
			
		//}
		
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
	*/

	@Override
	public String getPrimaryKey() {
		// TODO Auto-generated method stub
		return this.getStatusName();
	}


	

}
