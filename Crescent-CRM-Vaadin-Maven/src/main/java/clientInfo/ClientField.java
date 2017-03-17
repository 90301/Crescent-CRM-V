package clientInfo;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

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
		if (this.currentDataType.equals(TemplateField.DATA_TYPE_TEXT)) {
			//TEXT data
			Object oldFieldValue = this.fieldValue;
			this.fieldValue = oldFieldValue.toString();


		} else if (this.currentDataType.equals(TemplateField.DATA_TYPE_DATE)) {
			//DATE data
			this.fieldValue = new Date();

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2000);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			Date dateRepresentation = cal.getTime();

			this.fieldValue = dateRepresentation;
		} else if (this.currentDataType.equals(TemplateField.DATA_TYPE_NUMBER)) {
			//Number Value
			this.fieldValue = new Integer(0);
		} else if (this.currentDataType.equals(TemplateField.DATA_TYPE_LINK))  {
			//Link data
			this.fieldValue = "";
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
		
		Debugging.output("String Field Value(): currentDataType: "+this.currentDataType, Debugging.CUSTOM_FIELD_DEBUG);
		//TODO date support
		if(this.currentDataType.equals(TemplateField.DATA_TYPE_DATE)){
			Debugging.output("String Field Value(): Converting date to string: "+this.fieldValue, Debugging.CUSTOM_FIELD_DEBUG);
			String stringDate = "";
			try {
				stringDate = TemplateField.DATE_FORMAT.format(fieldValue);
			} catch (Exception e) {
				//When running this try catch it seems to make several error messages meaning this is called several 
				//times for the first client selected.
				Debugging.output("Error converting date: " + this.fieldValue, Debugging.CUSTOM_FIELD_DEBUG);
				Notification.show("Error when converting date. \n Click to remove message", Type.ERROR_MESSAGE);
				this.fieldValue = TemplateField.DEFAULT_DATE;
				stringDate = TemplateField.DATE_FORMAT.format(fieldValue);
			}
			return stringDate;
		} else {
			return fieldValue.toString();
		}
	}

	@SuppressWarnings("unused")
	public void setFieldValue(Object fieldValue) {
		typeCheck();

		if (this.currentDataType.equals(TemplateField.DATA_TYPE_TEXT)) {
			this.fieldValue = fieldValue.toString();
		} else if (this.currentDataType.equals(TemplateField.DATA_TYPE_DATE)) {
			//Ensure a date is provided
			if (fieldValue.getClass().isInstance(Date.class)) {
				//We think this means it's a java.date
				this.fieldValue = fieldValue;
			} else {
				//TODO print error and debug info
			}
		} else if (this.currentDataType.equals(TemplateField.DATA_TYPE_NUMBER)) {
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
	
	/**
	 * This takes a string representation of the object
	 * and converts it to the actual object
	 * @param str - the string to convert
	 */
	public void setStringFieldValue(String str) {
		if (this.currentDataType.equals(TemplateField.DATA_TYPE_DATE)) {
			//convert string date to java.date
			Date convertedDate;
			try {
				convertedDate = TemplateField.DATE_FORMAT.parse(str);
				this.fieldValue = convertedDate;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				if (MasterUI.DEVELOPER_MODE) {
					//TODO error message
					Notification.show("Error when parsing date. \n Click to remove message", Type.ERROR_MESSAGE);
				}
				//e.printStackTrace();
				
				this.fieldValue = TemplateField.DEFAULT_DATE;
			}
			
		} else {
			setCurrentDataType(str);
		}
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
