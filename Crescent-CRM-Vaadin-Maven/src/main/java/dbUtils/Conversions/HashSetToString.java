package dbUtils.Conversions;

import java.util.HashSet;

import clientInfo.Location;
import debugging.Debugging;

public class HashSetToString extends MaxConversion<HashSet<Location>, String> {

	public static final String DELIMITER = "|";
	@Override
	public String convertToStore(HashSet<Location> input) {
		String csv = "";
		Boolean firstRun = true;
		for (Location l : input) {
			if (!firstRun) {
				csv+=DELIMITER;
			} else {
				firstRun = false;
			}
			csv += l.getLocationName();
		}
		return csv;
	}

	
	@Override
	/**
	 * REQUIRES USER DATA HOLDER
	 * converts the csv data from the database into locations
	 */
	public HashSet<Location> convertToUse(String input) {
		
		Debugging.output("Attempting to convert: " + input, Debugging.CONVERSION_DEBUG, Debugging.CONVERSION_DEBUG_ENABLED);
		
		HashSet<Location> locations = new HashSet<Location>();
		//split the string on the delimited
		String[] splitString = input.split(DELIMITER);
		
		for (String s : splitString) {
			Location l = userDataHolder.getLocation(s);
			if (l!=null) {
				locations.add(l);
			}
		}
		
		return locations;
		
	}

}
