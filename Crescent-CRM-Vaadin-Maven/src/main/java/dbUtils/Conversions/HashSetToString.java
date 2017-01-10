package dbUtils.Conversions;

import java.util.HashSet;
import java.util.regex.Pattern;

import clientInfo.Location;
import debugging.Debugging;

public class HashSetToString extends MaxConversion<HashSet<String>, String> {

	public static final String DELIMITER = "|";
	@Override
	public String convertToStore(HashSet<String> input) {
		
		Debugging.output("Attempting to convert(to store): " + input, Debugging.CONVERSION_DEBUG2);
		
		String csv = "";
		Boolean firstRun = true;
		for (String l : input) {
			if (!firstRun) {
				csv+=DELIMITER;
			} else {
				firstRun = false;
			}
			csv += l;
			Debugging.output("primary key: " + l, Debugging.CONVERSION_DEBUG2);
		}
		return csv;
	}

	
	@Override
	/**
	 * REQUIRES USER DATA HOLDER
	 * converts the csv data from the database into locations
	 */
	public HashSet<String> convertToUse(String input) {
		
		Debugging.output("Attempting to convert (to use): " + input, Debugging.CONVERSION_DEBUG2);
		
		HashSet<String> locations = new HashSet<String>();
		//split the string on the delimited
		String[] splitString = input.split(Pattern.quote(DELIMITER));
		
		for (String s : splitString) {
			Debugging.output("Found String: " + s, Debugging.CONVERSION_DEBUG2);
			locations.add(s);
			/*
			Location l = userDataHolder.getLocation(s);
			if (l!=null) {
				locations.add(l);
			}
			*/
		}
		
		return locations;
		
	}

}
