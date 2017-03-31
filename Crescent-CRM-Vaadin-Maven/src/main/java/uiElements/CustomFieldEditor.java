package uiElements;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import clientInfo.Client;
import clientInfo.TemplateField;
import clientInfo.UserDataHolder;
import debugging.Debugging;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class CustomFieldEditor extends VerticalLayout {

	ArrayList<CustomFieldElement> customFieldElements = new ArrayList<CustomFieldElement>();
	
	/*
	UserDataHolder userDataHolder;

	public UserDataHolder getUserDataHolder() {

		return userDataHolder;
	}

	public void setUserDataHolder(UserDataHolder userDataHolder) {

		this.userDataHolder = userDataHolder;
	}
	*/

	public CustomFieldEditor() {
		// TODO Auto-generated constructor stub

	}

	/**
	 * Loads the fields from the database, then populates the data from the
	 * client into those custom fields.
	 * 
	 * @param c
	 */
	public void loadCustomFields(Client c, UserDataHolder userDataHolder) {
		Debugging.output(
				"Attempting to load custom fields for Client: " + c + " From userDataHolder: " + userDataHolder,
				Debugging.CUSTOM_FIELD, Debugging.CUSTOM_FIELD_ENABLED);
		
		if (userDataHolder == null) {
			// ERROR CONDITION
			Debugging.output(
					"ERROR OCCURED! User Data Holder is null for custom field editor. (you probably forgot to set it).",
					Debugging.CONSOLE_ERROR_OUTPUT, true);
			return;
		}
		// clears all fields
		// TODO remove this code and improve caching (ANDREW)
		this.removeAllComponents();
		customFieldElements.clear();

		// Load all the template fields from the user data holder
		ConcurrentHashMap<String, TemplateField> templateFieldMap = userDataHolder.getMap(TemplateField.class);

		// Add a field element for every field in the user data holder
		for (String key : templateFieldMap.keySet()) {
			
			CustomFieldElement customFieldElement = new CustomFieldElement();
			customFieldElement.setUserDataHolder(userDataHolder);
			customFieldElement.setFieldName(templateFieldMap.get(key).getFieldName());
			customFieldElement.createField();
			
			//Test to see if the Client has the data, if it does, load the data
			
			customFieldElement.setFieldValue(c.getCustomFieldValue(key));
			
			this.addComponent(customFieldElement);
			customFieldElements.add(customFieldElement);
		}

	}

	public Client updateClient(Client selectedClient) {
		
		Debugging.output("Attempting to store custom fields for client: " + selectedClient, Debugging.CUSTOM_FIELD, Debugging.CUSTOM_FIELD_ENABLED);

		for(CustomFieldElement cF : customFieldElements){
			//Issue occurs here when setCustomFieldValue is called...
			selectedClient.setCustomFieldValue(cF.getFieldName(), cF.getFieldValue());
		}
		
		return selectedClient;
	}

}
