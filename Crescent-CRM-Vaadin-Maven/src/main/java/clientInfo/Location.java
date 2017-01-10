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
import debugging.Debugging;

public class Location extends MaxObject {

	/*
	 * private String locationName; public static final String locationNameField
	 * = "locationName";
	 * 
	 * private HashSet<Location> closeLocations = new HashSet<Location>();
	 * public static final String closeLocationsField = "closeLocations";
	 * private static final String DELIMITER = "|";
	 */
	private static final HashSet<String> dHashSet = new HashSet<String>();

	MaxField<String> locationName = new MaxField<String>("locationName", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "",
			this);

	
	MaxConversion<HashSet<String>, String> closeLocationsConversion = new HashSetToString();

	MaxField<HashSet<String>> closeLocations = new MaxField<HashSet<String>>("closeLocations",
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

	
	public HashSet<String> getCloseLocations() {
		return closeLocations.getFieldValue();
	}
	
	HashSet<Location> realCloseLocations = new HashSet<Location>();
	
	/**
	 * Converts the hash set of close location strings and gets the locations object for use
	 * in the program.
	 * @return a hash set of all close locations
	 */
	public HashSet<Location> getRealCloseLocations() {
		
		realCloseLocations.clear();
		
		for (String s : getCloseLocations()) {
			Debugging.output("String for Real Close Locations: " + s, Debugging.CONVERSION_DEBUG2);
			Location l = userDataHolder.getLocation(s);
			if (l!=null) {
				realCloseLocations.add(l);
			}
		}
		
		return realCloseLocations;
	}

	public void setCloseLocations(HashSet<String> closeLocations) {
		this.closeLocations.setFieldValue(closeLocations);
	}
	
	public void setRealCloseLocations(HashSet<Location> closeLocations2) {
		HashSet<String> locations = new HashSet<String>();
		for (Location l : closeLocations2) {
			
			locations.add(l.getPrimaryKey());
			Debugging.output("primary key: " + l.getPrimaryKey(), Debugging.CONVERSION_DEBUG2);
		}
		
		this.setCloseLocations(locations);
		
	}
	
	@Override
	public String getPrimaryKey() {
		// TODO Auto-generated method stub
		return this.getLocationName();
	}

	
}
