package dbUtils.Conversions;

import java.util.HashMap;
import java.util.Map;

import clientInfo.ClientField;
import clientInfo.TemplateField;
import dbUtils.InhalerUtils;
import debugging.Debugging;

public class ClientFieldMapToString extends MaxConversion<Map<String, ClientField>,String> {

	@Override
	public String convertToStore(Map<String, ClientField> input) {
		String xml = "";

		// Convert to a map of strings
		HashMap<String, String> tempMap = new HashMap<String, String>();

		for (ClientField cf : input.values()) {
			tempMap.put(cf.getFieldName(), cf.getStringFieldValue());
		}

		xml = InhalerUtils.mapToXML(tempMap);
		return xml;
	}

	@Override
	public Map<String, ClientField> convertToUse(String input) {
		HashMap<String,ClientField> output = new HashMap<String, ClientField>();
		
		HashMap<String, String> tempMap = new HashMap<String, String>();

		tempMap = InhalerUtils.xmlToMap(input);
		//Debugging.output("tempMap is: " + tempMap, Debugging.CUSTOM_FIELD_DEBUG);
		// TODO finish this method
		for (String key : tempMap.keySet()) {

			String value = tempMap.get(key);
			// create a new client field
			ClientField cf = new ClientField();
			cf.setUserDataHolder(userDataHolder);
			
			// Check to see if a template field of the same name exists
			
			TemplateField tf = userDataHolder.getMap(TemplateField.class).get(key);

			if (tf != null) {
				cf.setCurrentDataType(tf.getDataType());
				cf.setFieldName(key);
				Debugging.output("fieldvalue is: " + value + "after String is seperated", Debugging.CUSTOM_FIELD, Debugging.CUSTOM_FIELD_ENABLED);
				cf.setFieldValue(value);
				//Debugging.output("fieldValue is: " + value, Debugging.CUSTOM_FIELD_DEBUG);
				output.put(key, cf);
			}

		}
		return output;
	}

}
