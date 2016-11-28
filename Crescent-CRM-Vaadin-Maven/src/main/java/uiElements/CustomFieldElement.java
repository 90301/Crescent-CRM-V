package uiElements;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import clientInfo.TemplateField;
import clientInfo.UserDataHolder;

//Generate Labels and Fields here for the clients
public class CustomFieldElement extends HorizontalLayout{
	
	Label fieldNameLabel = new Label();
	Component fieldComponent;
	UserDataHolder userDataHolder;
	String fieldName = "";
		
	public void createField(){
		TemplateField tF = userDataHolder.getMap(TemplateField.class).get(fieldName);
		String dataType = tF.getDataType();
		
		if(dataType == TemplateField.DATA_TYPE_TEXT){
			TextField textF = new TextField(dataType);
			fieldComponent = textF;
		} else if(dataType == TemplateField.DATA_TYPE_NUMBER){
			
		} else if(dataType == TemplateField.DATA_TYPE_DATE){
			
		}
	}

	public void setFieldName(String fieldName) {
		// TODO Auto-generated method stub
		fieldNameLabel.setCaption(fieldName);
		this.fieldName = fieldName;
	}

	public void setUserDataHolder(UserDataHolder userDataHolder) {
		// TODO Auto-generated method stub
		this.userDataHolder = userDataHolder;
		
	}
}
