package dbUtils.Conversions;

import java.util.ArrayList;

import dbUtils.InhalerUtils;

public class ArrayListToString extends MaxConversion<ArrayList<String>, String> {

	@Override
	public String convertToStore(ArrayList<String> input) {
		String csvDatabaseAccsessable = InhalerUtils.listToCsv(input);
		return csvDatabaseAccsessable;
	}

	@Override
	public ArrayList<String> convertToUse(String input) {
		ArrayList<String> output = new ArrayList<String>();
		output.addAll(InhalerUtils.csvToList(input));
		return output;
	}

}
