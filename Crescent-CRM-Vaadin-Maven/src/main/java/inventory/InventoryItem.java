package inventory;

import dbUtils.MaxDBTable;
import dbUtils.MaxObject;

public class InventoryItem extends MaxObject {

	private String itemKey;
	private String itemName;
	private String itemCategory;
	private String itemBarcode;
	private String itemURL;
	private Integer itemStock;
	private Integer itemReorderPoint;
	
	@Override
	public void loadInternalFromMap() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateDBMap() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPrimaryKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createTableForClass(MaxDBTable table) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setupDBDatatypes() {
		// TODO Auto-generated method stub
		
	}

}
