package inventory;

import dbUtils.MaxDBTable;
import dbUtils.MaxField;
import dbUtils.MaxObject;

public class InventoryCategory extends MaxObject {

	/*
	public Integer id;
	String categoryName;
	*/
	MaxField<String> categoryName = new MaxField<String>("categoryName",MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING,"","",this);
	
	{
		this.autoGenList.add(categoryName);
	}
	public InventoryCategory() {
		
	}
	
	public InventoryCategory(String categoryName) {
		// TODO Auto-generated constructor stub
		this.categoryName.setFieldValue(categoryName); 
	}
	
	//All inventory categories  can be found with a sql query (unique on category)
	
	@Override
	public String toString() {
		return categoryName.getFieldValue();
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
		// TODO Auto-generated method stub
		return categoryName.getFieldValue();
	}

	@Override
	public void createTableForClass(MaxDBTable table) {
		this.autoGenCreateTableForClass(this.autoGenList,categoryName,table);
		
	}

	@Override
	public void setupDBDatatypes() {
		this.autoGenSetupDBTypes(this.autoGenList);
		
	}


	/*
	 * Getters / Setters
	 */
	public String getCategoryName() {
		return categoryName.getFieldValue();
	}

	public void setCategoryName(String categoryName) {
		this.categoryName.setFieldValue(categoryName);
	}

	
}
