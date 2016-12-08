package dbUtils.Conversions;

import java.util.HashMap;
import java.util.Map;

import clientInfo.ClientField;
import clientInfo.TemplateField;
import dbUtils.InhalerUtils;

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
				cf.setFieldValue(value);

				output.put(key, cf);
			}

		}
		return output;
	}

}
