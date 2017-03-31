package clientInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.vaadin.sass.internal.parser.ParseException;

import dbUtils.MaxDBTable;
import dbUtils.MaxField;
import dbUtils.MaxObject;
import debugging.Debugging;

public class TemplateField extends MaxObject{
	

	//Constants
	public static final String DATA_TYPE_NUMBER = "Number";
	public static final String DATA_TYPE_DATE = "Date";
	public static final String DATA_TYPE_TEXT = "Text";
	public static final String DATA_TYPE_LINK = "Link";
	public static final String[] DataTypes = {DATA_TYPE_NUMBER,DATA_TYPE_DATE, DATA_TYPE_TEXT};
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd"); //Or whatever format fits best your needs.
	public static String DEFAULT_DATE_STRING = DATE_FORMAT.format(new Date()); //Default date is current day

	public static Date DEFAULT_DATE;
	//public static final Date DEFAULT_DATE = defaultDateParsed;
	MaxField<String> fieldName = new MaxField<String>("fieldName", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "", this);
	MaxField<String> dataType = new MaxField<String>("dataType", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "", this);
	MaxField<String> defaultValue = new MaxField<String>("defaultValue", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "", this);
	MaxField<Integer> fieldPosition = new MaxField<Integer>("fieldPos", MaxDBTable.DATA_MYSQL_TYPE_INT, 0, 0, this);
	
	static {
		try
		{
		DEFAULT_DATE = (Date)DATE_FORMAT.parse("2000/01/01"); //Default date is 1/1/2000
		DEFAULT_DATE_STRING = DATE_FORMAT.format(DEFAULT_DATE);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//DEFAULT_DATE_STRING = DATE_FORMAT.format(DEFAULT_DATE);
	}


	{
		
		this.setKeyField(fieldName);
		
		this.addMaxField(fieldName);
		this.addMaxField(dataType);
		this.addMaxField(defaultValue);
		this.addMaxField(fieldPosition);
	}
	
	public TemplateField() {
		// TODO Auto-generated constructor stub
	}
	
	public void genDefaultValue() {
		// TODO Auto-generated method stub
		//Edit if you need to add more default values
		if(this.getDataType() == DATA_TYPE_DATE){
			
			this.setDefaultValue(DEFAULT_DATE_STRING);
			Debugging.output(" Setting default date here as:"+ this, Debugging.CUSTOM_FIELD_DEBUG);
			
		} else if(this.getDataType() == DATA_TYPE_NUMBER){
			
			this.setDefaultValue("0");
			
		}  else if(this.getDataType() == DATA_TYPE_TEXT){
			
			this.setDefaultValue("");
			
		}  else if(this.getDataType() == DATA_TYPE_LINK){
			
			this.setDefaultValue("");
		}
	}
	
	

	@Override
	public void loadInternalFromMap() {
		this.autoGenLoadInternalFromMap(this.autoGenList);

	}

	@Override
	public void updateDBMap() {
		this.autoGenUpdateDBMap(this.autoGenList);

	}

	@Override
	public String getPrimaryKey() {
		String pKey = fieldName.getFieldValue();
		Debugging.output("Inventory Primary key: " + pKey, Debugging.INVENTORY_OUTPUT,
				Debugging.INVENTORY_OUTPUT_ENABLED);
		//change this return statement
		return pKey;
	}

	@Override
	public void createTableForClass(MaxDBTable table) {
		this.autoGenCreateTableForClass(this.autoGenList, this.fieldName, table);
		
	}

	@Override
	public void setupDBDatatypes() {
		this.autoGenSetupDBTypes(this.autoGenList);

	}

	public String getFieldName() {
		
		return fieldName.getFieldValue();
	}

	public void setFieldName(String fieldName) {
		this.fieldName.setFieldValue(fieldName);
	}

	public String getDataType() {
		return dataType.getFieldValue();
	}

	public void setDataType(String dataType) {
		this.dataType.setFieldValue(dataType);
	}

	public String getDefaultValue() {
		return defaultValue.getFieldValue();
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue.setFieldValue(defaultValue);
	}
	
	public Integer getFieldPosition() {
		return fieldPosition.getFieldValue();
	}

	public void setFieldPosition(Integer fieldPosition) {
		this.fieldPosition.setFieldValue(fieldPosition);
	}




}
