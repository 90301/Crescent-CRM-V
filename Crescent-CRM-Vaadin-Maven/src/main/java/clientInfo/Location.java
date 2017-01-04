/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package clientInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import dbUtils.MaxDBTable;
import dbUtils.MaxField;
import dbUtils.MaxObject;
import dbUtils.Conversions.HashSetToString;
import dbUtils.Conversions.MaxConversion;

public class Location extends MaxObject {

	/*
	 * private String locationName; public static final String locationNameField
	 * = "locationName";
	 * 
	 * private HashSet<Location> closeLocations = new HashSet<Location>();
	 * public static final String closeLocationsField = "closeLocations";
	 * private static final String DELIMITER = "|";
	 */
	private static final HashSet<Location> dHashSet = new HashSet<Location>();

	MaxField<String> locationName = new MaxField<String>("locationName", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "",
			this);

	
	MaxConversion<HashSet<Location>, String> closeLocationsConversion = new HashSetToString();

	MaxField<HashSet<Location>> closeLocations = new MaxField<HashSet<Location>>("closeLocations",
			MaxDBTable.DATA_MYSQL_TYPE_STRING, dHashSet, dHashSet, this);
			
	{
		
		closeLocationsConversion.setUserDataHolder(userDataHolder);
		closeLocationsConversion.setStoreRef(String.class);
		closeLocationsConversion.setDefaultStoreValue("");
		
		closeLocations.setConversion(closeLocationsConversion);
		
		
		this.setKeyField(locationName);

		this.addMaxField(locationName);
		this.addMaxField(closeLocations);
	}

	public Location() {
		updateDBMap();
	}

	@Override
	public String toString() {
		return this.getPrimaryKey();
	}

	public void setLocationName(String locationName) {
		this.locationName.setFieldValue(locationName);
	}

	public String getLocationName() {
		return locationName.getFieldValue();
	}

	
	public HashSet<Location> getCloseLocations() {
		return closeLocations.getFieldValue();
	}

	public void setCloseLocations(HashSet<Location> closeLocations) {
		this.closeLocations.setFieldValue(closeLocations);
	}
	
	
	@Override
	public String getPrimaryKey() {
		// TODO Auto-generated method stub
		return this.getLocationName();
	}
}
