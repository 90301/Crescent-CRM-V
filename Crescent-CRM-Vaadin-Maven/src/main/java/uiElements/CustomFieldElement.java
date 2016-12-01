package uiElements;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;

import dbUtils.InhalerUtils;
import clientInfo.TemplateField;
import clientInfo.UserDataHolder;

//Generate Labels and Fields here for the clients
public class CustomFieldElement extends HorizontalLayout{

	Label fieldNameLabel = new Label();
	Component fieldComponent;
	UserDataHolder userDataHolder;
	String fieldName = "";

	public String getFieldName() {
		return fieldName;
	}
	
	public Object getFieldValue(){
		
		if(fieldComponent instanceof TextField){
			return ((TextField)fieldComponent).getValue();
		}
		
		
		return null;
	}

	//Different UI elements
	TextField textF;
	PopupDateField dateF;
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
			dateF = new PopupDateField();
			dateF.addValueChangeListener(e -> Notification.show("Value changed:",
					String.valueOf(e.getProperty().getValue()),
					Type.TRAY_NOTIFICATION));
			fieldComponent = dateF;
		}

		this.removeAllComponents();
		this.addComponent(fieldNameLabel);
		if (fieldComponent != null) 
			this.addComponent(fieldComponent);
	}
	
	public void setFieldName(String fieldName) {
		// TODO Auto-generated method stub
		//Tried replaceStringSpaces() here and it will not work
		fieldNameLabel.setCaption(fieldName);
		this.fieldName = fieldName;
	}

	public void setUserDataHolder(UserDataHolder userDataHolder) {
		// TODO Auto-generated method stub
		this.userDataHolder = userDataHolder;

	}
}
