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
		closeLocations.setConversion(closeLocationsConversion);
		
		
		this.setKeyField(locationName);

		this.addMaxField(locationName);
		//this.addMaxField(closeLocations);
	}

	public Location() {
		updateDBMap();
	}

	@Override
	public String toString() {
		return this.getPrimaryKey();
	}
	/*
	 * @Override public void setupDBDatatypes() {
	 * 
	 * if (dbDatatypes ==null) { dbDatatypes = new HashMap<String, Class<?>>();
	 * }
	 * 
	 * dbDatatypes.put(locationNameField, String.class);
	 * dbDatatypes.put(closeLocationsField, String.class);
	 * 
	 * }
	 * 
	 * public void updateDBMap() { dbMap.put(locationNameField, locationName);
	 * //TODO: serialize close locations dbMap.put(closeLocationsField,
	 * closeLocationsCSV()); }
	 * 
	 * public String closeLocationsCSV() { String csv = ""; Boolean firstRun =
	 * true; for (Location l : closeLocations) { if (!firstRun) {
	 * csv+=DELIMITER; } else { firstRun = false; } csv += l.getLocationName();
	 * } return csv; }
	 * 
	 * 
	 * 
	 * public void createTableForClass(MaxDBTable table) {
	 * table.addDatatype(locationNameField,
	 * MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING);
	 * table.addDatatype(closeLocationsField,
	 * MaxDBTable.DATA_MYSQL_TYPE_STRING);
	 * table.setPrimaryKeyName(locationNameField); table.createTable(); }
	 * 
	 * @Override public void loadInternalFromMap() {
	 * System.out.println("Read location: " + locationName); this.locationName =
	 * (String) dbMap.get(locationNameField);
	 * System.out.println("Read location: " + this.locationName); // TODO: parse
	 * information from csv close locations }
	 */

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
