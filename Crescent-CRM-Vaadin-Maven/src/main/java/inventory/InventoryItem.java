package inventory;

import dbUtils.MaxDBTable;
import dbUtils.MaxField;
import dbUtils.MaxObject;
import debugging.Debugging;

public class InventoryItem extends MaxObject {

	//the old way
	/*
	private String itemKey;
	private String itemName;
	private String itemCategory;
	private String itemBarcode;
	private String itemURL;
	private Integer itemStock;
	private Integer itemReorderPoint;
	*/
	
	//the new way
	//Note that special things must be done for the primary key
	//working on trying to make that a thing of the past
	private MaxField<String> itemKey = 
			new MaxField<String>("itemKey", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "", this);
	
	private MaxField<String> itemName = 
			new MaxField<String>("itemName", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "", this);

	private MaxField<String> itemCategory = 
			new MaxField<String>("itemCategory", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "", this);
	
	private MaxField<String> itemBarcode = 
			new	MaxField<String>("itemBarcode", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "", this);

	private MaxField<String> itemURL = 
			new MaxField<String>("itemURL", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "", this);

	private MaxField<Integer> itemStock = 
			new MaxField<Integer>("itemStock", MaxDBTable.DATA_MYSQL_TYPE_INT, 0, 0, this);

	private MaxField<Integer> itemReorderPoint = 
			new MaxField<Integer>("itemStock", MaxDBTable.DATA_MYSQL_TYPE_INT, 0, 0, this);

	{
		//A list that contains all the datatypes/field info/default values
		this.autoGenList.add(itemKey);
		this.autoGenList.add(itemName);
		this.autoGenList.add(itemCategory);
		this.autoGenList.add(itemBarcode);
		this.autoGenList.add(itemURL);
		this.autoGenList.add(itemStock);
		this.autoGenList.add(itemReorderPoint);
	}
	
		
	public String debugOutput() {
		return "InventoryItem [itemKey=" + itemKey + ", itemName=" + itemName + ", itemCategory=" + itemCategory
				+ ", itemBarcode=" + itemBarcode + ", itemURL=" + itemURL + ", itemStock=" + itemStock
				+ ", itemReorderPoint=" + itemReorderPoint + "]";
	}

	
	/*
	 * Max Object overrides
	 */

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
		String pKey = itemKey.getFieldValue();
		Debugging.output("Inventory Primary key: " + pKey
				, Debugging.INVENTORY_OUTPUT
				, Debugging.INVENTORY_OUTPUT_ENABLED);
		return pKey;
	}

	@Override
	public void createTableForClass(MaxDBTable table) {
		this.autoGenCreateTableForClass(this.autoGenList, this.itemKey, table);
		
	}

	@Override
	public void setupDBDatatypes() {
		this.autoGenSetupDBTypes(this.autoGenList);
		
	}
	
	/*
	 * Getters / setters
	 * NOTE how they are different.
	 */

	public String getItemKey() {
		return itemKey.getFieldValue();
	}

	public void setItemKey(String itemKey) {
		this.itemKey.setFieldValue(itemKey);
	}

	public String getItemName() {
		return itemName.getFieldValue();
	}

	public void setItemName(String itemName) {
		this.itemName.setFieldValue(itemName);
	}

	public String getItemCategory() {
		return itemCategory.getFieldValue();
	}

	public void setItemCategory(String itemCategory) {
		this.itemCategory.setFieldValue(itemCategory);
	}

	public String getItemBarcode() {
		return itemBarcode.getFieldValue();
	}

	public void setItemBarcode(String itemBarcode) {
		this.itemBarcode.setFieldValue(itemBarcode);
	}

	public String getItemURL() {
		return itemURL.getFieldValue();
	}

	public void setItemURL(String itemURL) {
		this.itemURL.setFieldValue(itemURL);
	}

	public Integer getItemStock() {
		return itemStock.getFieldValue();
	}

	public void setItemStock(Integer itemStock) {
		this.itemStock.setFieldValue(itemStock);
	}

	public Integer getItemReorderPoint() {
		return itemReorderPoint.getFieldValue();
	}

	public void setItemReorderPoint(Integer itemReorderPoint) {
		this.itemReorderPoint.setFieldValue(itemReorderPoint);
	}
	
	

}
