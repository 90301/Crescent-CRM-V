package clientInfo;

import java.util.Calendar;
import java.util.Date;

import ccrmV.MasterUI;
import debugging.Debugging;

/**
 * Class that holds client field information
 * to be used in conjuction with the templateField class
 * @author Administrator
 *
 */
public class ClientField {

	/*
	 * Key decisions:
	 * Data will not be deleted upon removing a field (it just won't be shown)
	 * Data will be cleared upon dataType Changing.
	 */
	
	//fieldName, Value
	//public MasterUI masterUi;
	UserDataHolder userDataHolder;
	private String fieldName = "";
	private String currentDataType = "";
	//To be used for the purpose of determining if the datatype changed
	private Object fieldValue;
	
	public ClientField() {
		// TODO Auto-generated constructor stub
		
	}
	
	/**
	 * ensures the current datatype is the same as the template
	 * if a mismatch occurs, corrective action is taken.
	 * 
	 * This method uses setupDataType to correct data.
	 */
	public void typeCheck() {
		//ensures the master ui and field names are set up.
		if (this.userDataHolder != null && fieldName !="") {
			//Ensure there is a template field with the same name as this field
			if (userDataHolder.getMap(TemplateField.class).contains(fieldName)) {
			
			TemplateField masterTemplateField = userDataHolder.getMap(TemplateField.class).get(fieldName);
			
			if (this.currentDataType != masterTemplateField.getDataType()) {
				String oldDataType = this.currentDataType;
				
				this.currentDataType = masterTemplateField.getDataType();
				
				setupDataType(oldDataType);
				
				
			}
			
			}
			
		} else {
			Debugging.output("Client Field improperly initalized: user data holder: " + userDataHolder + " fieldName: " + this.fieldName,
					Debugging.CLIENT_FIELD_DEBUGGING, Debugging.CLIENT_FIELD_DEBUGGING_ENABLED);
		}
	}

	/**
	 * Sets up the new datatype.
	 * based on the old data type, there may be a conversion
	 * @param oldDataType
	 */
	public void setupDataType(String oldDataType) {
		if (this.currentDataType==TemplateField.DATA_TYPE_TEXT) {
			//TEXT data
			Object oldFieldValue = this.fieldValue;
			this.fieldValue = oldFieldValue.toString();
			
			
		} else if (this.currentDataType==TemplateField.DATA_TYPE_DATE) {
			//DATE data
			this.fieldValue = new Date();
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2000);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			Date dateRepresentation = cal.getTime();
			
			this.fieldValue = dateRepresentation;
		} else if (this.currentDataType==TemplateField.DATA_TYPE_NUMBER) {
			//Number Value
			this.fieldValue = new Integer(0);
		}
	}

	public Object getFieldValue() {
		typeCheck();
		return fieldValue;
	}
	
	/**
	 * String representation return of the data.
	 * Special cases for data not yet implemented.
	 * @return
	 */
	public String getStringFieldValue() {
		//TODO date support
		return fieldValue.toString();
		
	}

	@SuppressWarnings("unused")
	public void setFieldValue(Object fieldValue) {
		typeCheck();
		
		if (this.currentDataType==TemplateField.DATA_TYPE_TEXT) {
			this.fieldValue = fieldValue.toString();
		} else if (this.currentDataType==TemplateField.DATA_TYPE_DATE) {
			//Ensure a date is provided
			
		} else if (this.currentDataType==TemplateField.DATA_TYPE_NUMBER) {
			//Ensure an Integer number is provided
			Integer intRep = new Integer((int)fieldValue);
			if (intRep==null) {
				setupDataType("");
			} else {
				this.fieldValue = intRep;
			}
		}
		
		this.fieldValue = fieldValue;
	}
	
	

	public UserDataHolder getUserDataHolder() {
		return userDataHolder;
	}

	public void setUserDataHolder(UserDataHolder userDataHolder) {
		this.userDataHolder = userDataHolder;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getCurrentDataType() {
		return currentDataType;
	}

	public void setCurrentDataType(String currentDataType) {
		this.currentDataType = currentDataType;
	}
	
	

}
