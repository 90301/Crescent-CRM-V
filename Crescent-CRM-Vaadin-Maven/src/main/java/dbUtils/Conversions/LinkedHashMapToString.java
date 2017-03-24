package dbUtils.Conversions;

import java.util.Map;

import dbUtils.InhalerUtils;

public class LinkedHashMapToString extends MaxConversion<Map<String,String>, String> {

	@Override
	public String convertToStore(Map<String, String> input) {
		return InhalerUtils.mapToXML(input);
	}

	@Override
	public Map<String, String> convertToUse(String input) {
		return InhalerUtils.xmlToLinkedHashMap(input);
	}

}
