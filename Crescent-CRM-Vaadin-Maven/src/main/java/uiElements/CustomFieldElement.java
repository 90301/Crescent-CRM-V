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
		
	//Different UI elements
	TextField textF;
	
	public void createField(){
		
		this.setSpacing(true);
		
		TemplateField tF = userDataHolder.getMap(TemplateField.class).get(fieldName);
		String dataType = tF.getDataType();
		//fieldNameLabel.setCaption(fieldName);
		
		if(dataType.contains(TemplateField.DATA_TYPE_TEXT)){
			textF = new TextField();
			fieldComponent = textF;
		} else if(dataType.contains(TemplateField.DATA_TYPE_NUMBER)){
			
		} else if(dataType.contains(TemplateField.DATA_TYPE_DATE)){
			
		}
		
		this.removeAllComponents();
		this.addComponent(fieldNameLabel);
		if (fieldComponent != null) 
			this.addComponent(fieldComponent);
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
