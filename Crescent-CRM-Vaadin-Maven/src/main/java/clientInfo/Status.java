/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package clientInfo;

import java.util.Collection;

import com.vaadin.v7.shared.ui.colorpicker.Color;

import dbUtils.MaxDBTable;
import dbUtils.MaxField;
import dbUtils.MaxObject;

public class Status extends MaxObject implements Comparable<Status> {


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
	}
	
	public Integer getColor() {
		return statusColor.getFieldValue();
	}
	
	public Color getJavaColor() {
		Color c = new Color(statusColor.getFieldValue());
		return c;
	}
	public void setColor(Integer color) {
		this.statusColor.setFieldValue(color);
	}


	@Override
	public String getPrimaryKey() {
		return this.getStatusName();
	}


	@Override
	public int compareTo(Status o) {
		return this.getStatusName().compareTo(o.getStatusName());
	}


	

}
