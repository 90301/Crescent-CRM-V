package uiElements;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import clientInfo.TemplateField;
import clientInfo.UserDataHolder;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class CustomFieldEditor extends VerticalLayout{

	ArrayList<CustomFieldElement> customFieldElements = new ArrayList<CustomFieldElement>();
	UserDataHolder userDataHolder;
	
	
	public UserDataHolder getUserDataHolder() {
		
		return userDataHolder;
	}
	public void setUserDataHolder(UserDataHolder userDataHolder) {
		
		this.userDataHolder = userDataHolder;
	}
	public CustomFieldEditor() {
		// TODO Auto-generated constructor stub
		
	}
	
	public void genCustomFields(){
		
		ConcurrentHashMap<String, TemplateField> templateFieldMap = userDataHolder.getMap(TemplateField.class);
		
		for(String key:templateFieldMap.keySet()){
			
			CustomFieldElement customFieldElement = new CustomFieldElement();
			customFieldElement.setUserDataHolder(userDataHolder);
			customFieldElement.setFieldName(templateFieldMap.get(key).getFieldName());
		}
		
	}
	
	
	

}
