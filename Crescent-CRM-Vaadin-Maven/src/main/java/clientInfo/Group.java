/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package clientInfo;

import java.util.HashMap;
import java.util.Map;

import dbUtils.MaxDBTable;
import dbUtils.MaxField;
import dbUtils.MaxObject;

public class Group extends MaxObject implements Comparable<Group> {

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

	@Override
	public int compareTo(Group o) {
		return this.getGroupName().compareTo(o.getGroupName());
	}




}
