package clientInfo;

import java.util.HashMap;
import java.util.Map;

import dbUtils.MaxDBTable;
import dbUtils.MaxObject;

public class Group extends MaxObject {

	String groupName;
	public static final String groupNameField = "groupName";
	Integer color = 0;
	public static final String colorField = "color";

	public Group() {
		updateDBMap();
	}

	@Override
	public String toString() {
		return groupName;
	}

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
	public void loadInternalFromMap() {
		this.groupName = (String) dbMap.get(groupNameField);
		this.color = (Integer) dbMap.get(colorField);
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
		updateDBMap();
	}

	public Integer getColor() {
		return color;
	}

	public void setColor(Integer color) {
		this.color = color;
		updateDBMap();
	}

	@Override
	public String getPrimaryKey() {
		return this.getGroupName();
	}

	@Override
	public void createTableForClass(MaxDBTable table) {
		table.addDatatype(groupNameField, MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING);
		table.addDatatype(colorField, MaxDBTable.DATA_MYSQL_TYPE_INT);
		table.setPrimaryKeyName(groupNameField);
		table.createTable();

	}

}
