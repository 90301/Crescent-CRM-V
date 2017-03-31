package uiElements;

import java.text.ParseException;
import java.util.Date;

import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;

import dbUtils.InhalerUtils;
import debugging.Debugging;
import clientInfo.TemplateField;
import clientInfo.UserDataHolder;

//Generate Labels and Fields here for the clients
public class CustomFieldElement extends HorizontalLayout{

	Label fieldNameLabel = new Label();
	Component fieldComponent;
	UserDataHolder userDataHolder;
	String fieldName = "";
	String dataType;

	public String getFieldName() {
		return fieldName;
	}

	public Object getFieldValue(){

		if(fieldComponent instanceof TextField){
			return ((TextField)fieldComponent).getValue();
		}
		if (fieldComponent instanceof DateField) {
			return ((DateField)fieldComponent).getValue();
		}

		return null;
	}

	//Different UI elements
	TextField textF;
	PopupDateField dateF;
	public void createField(){

		this.setSpacing(true);

		TemplateField tF = userDataHolder.getMap(TemplateField.class).get(fieldName);
		dataType = tF.getDataType();
		//fieldNameLabel.setCaption(fieldName);

		if(dataType.contains(TemplateField.DATA_TYPE_TEXT)){
			textF = new TextField();
			fieldComponent = textF;
		} else if(dataType.contains(TemplateField.DATA_TYPE_NUMBER)){
			textF = new TextField();
			fieldComponent = textF;
		} else if(dataType.contains(TemplateField.DATA_TYPE_DATE)){
			dateF = new PopupDateField();
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

	/**
	 * Loads the custom field value
	 * TODO extend functionality to other datatypes
	 * @param customFieldValue
	 */
	public void setFieldValue(Object customFieldValue) {

		if (fieldComponent instanceof TextField) {
			TextField textField = (TextField)fieldComponent;
			textField.setValue((String)customFieldValue);
		}
		
		if (fieldComponent instanceof DateField) {
			
			DateField dateField = (DateField)fieldComponent;
			
			if (customFieldValue.getClass().isInstance(Date.class)) {
			
			dateField.setValue((Date)customFieldValue);
			} else if (customFieldValue.getClass().isInstance(String.class)) {
				try {
					dateField.setValue(TemplateField.DATE_FORMAT.parse((String) customFieldValue));
				} catch (ReadOnlyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ConversionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
