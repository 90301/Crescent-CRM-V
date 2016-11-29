/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package clientInfo;

import java.util.HashMap;
import java.util.Map;

import dbUtils.MaxDBTable;
import dbUtils.MaxField;
import dbUtils.MaxObject;

public class Group extends MaxObject {

	/*
	String groupName;
	public static final String groupNameField = "groupName";
	Integer color = 0;
	public static final String colorField = "color";
	
	*/
	//Max Field Version
	MaxField<String> groupName = new MaxField<String>("groupName",MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING,"","",this);
	MaxField<Integer> color = new MaxField<Integer>("color",MaxDBTable.DATA_MYSQL_TYPE_INT,0,0,this);
	
	{
		this.setKeyField(groupName);
		
		this.addMaxField(groupName);
		this.addMaxField(color);
	}
	
	public Group() {
		updateDBMap();
	}

	@Override
	public String toString() {
		return getPrimaryKey();
	}

	/*
	@Override
	public void updateDBMap() {
		dbMap.put(groupNameField, groupName);
		dbMap.put(colorField, color);
	}

	@Override
	public void setupDBDatatypes() {
		if (dbDatatypes ==null) {
			dbDatatypes = new HashMap<String, Class<?>>();
		}

			dbDatatypes.put(groupNameField, String.class);
			dbDatatypes.put(colorField, Integer.class);


	}
	
	@Override
	public void createTableForClass(MaxDBTable table) {
		table.addDatatype(groupNameField, MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING);
		table.addDatatype(colorField, MaxDBTable.DATA_MYSQL_TYPE_INT);
		table.setPrimaryKeyName(groupNameField);
		table.createTable();

	}

	@Override
	public void loadInternalFromMap() {
		this.groupName = (String) dbMap.get(groupNameField);
		this.color = (Integer) dbMap.get(colorField);
	}
	*/
	@Override
	public String getPrimaryKey() {
		return this.getGroupName();
	}


	public String getGroupName() {
		return groupName.getFieldValue();
	}

	public void setGroupName(String groupName) {
		this.groupName.setFieldValue(groupName);
		//updateDBMap();
	}

	public Integer getColor() {
		return color.getFieldValue();
	}

	public void setColor(Integer color) {
		this.color.setFieldValue(color);
	}




}
