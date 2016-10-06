package dbUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import clientInfo.DataHolder;
import clientInfo.Location;
import clientInfo.UserDataHolder;
import users.User;

public class InhalerUtils {
	private static final String DELIMITER = "|";

	/**
	 * Caution: Do not use this method when dealing with maxObject database serialization (dealing with dbMap)
	 * @param items
	 * @return
	 */
	public static String maxObjectListToCSV(Collection<? extends MaxObject> items) {
		boolean firstRun = true;
		String csv ="";
		for (MaxObject item : items) {
			
			if (!firstRun ) {
				csv += DELIMITER;
			} else {
				firstRun = false;
			}
			
			csv+= item.getPrimaryKey();
		}
		return csv;
	}
	

	public static List<MaxObject> maxObjectCSVToList (String csv, UserDataHolder udh, @SuppressWarnings("rawtypes") Class ref) {
		List<MaxObject> items = new ArrayList<MaxObject>();
		
		//Check to see if the class is part of the DataHolder (static data)
		if (DataHolder.containsClass(ref)) {
			String[] allStrs = csv.split(DELIMITER);
			for (String str : allStrs) {
				@SuppressWarnings("unchecked")
				MaxObject item = DataHolder.retrieve(str,ref);
				
				items.add(item);
			}
		} else {
			if (udh==null) {
				System.err.println("Null User Data Holder found for udh Object");
				return null;
			}
		//check to see if udh is null ( if so, fail)
		}
		return items;
	}
	
	
	public static Collection<String> csvToList(String csv) {
		ArrayList<String> list= new ArrayList<String>();
		for (String s : csv.split("\\"+DELIMITER)) {
			System.out.println("CSV ITEM: " + s);
			list.add(s);
		}
		
		return list;
	}
	
	/**
	 * Returns a CSV (string) from a list of strings
	 * @param list
	 * @return
	 */
	public static String listToCsv(Collection<String> list) {
		
		String csv = "";
		boolean firstRun = true;
		for (String s : list) {
			if (!firstRun ) {
				csv += DELIMITER;
			} else {
				firstRun = false;
			}
			
			csv+= s;
		}
		return csv;
		
	}
	
	
	/**
	 * Returns true if the string is null/blank or "null"
	 * 
	 * @param testingString
	 * @return true if null
	 */
	public static Boolean stringNullCheck(String testingString) {

		Boolean rtrn = false;
		if (testingString == null || testingString == "" || testingString == "null") {
			rtrn = true;
		}

		System.out.println("Tested: " + testingString + " Null?: " + rtrn);
		return rtrn;
	}
	
}
