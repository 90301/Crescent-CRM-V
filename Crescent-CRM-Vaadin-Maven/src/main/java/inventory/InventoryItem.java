package inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.ObjectProperty;

import dbUtils.MaxDBTable;
import dbUtils.MaxField;
import dbUtils.MaxObject;
import debugging.Debugging;

public class InventoryItem extends MaxObject implements Item {

	// the old way
	/*
	 * private String itemKey; private String itemName; private String
	 * itemCategory; private String itemBarcode; private String itemURL; private
	 * Integer itemStock; private Integer itemReorderPoint;
	 */

	// the new way
	// Note that special things must be done for the primary key
	// working on trying to make that a thing of the past

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MaxField<String> itemKey = new MaxField<String>("itemKey", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "",
			this);

	private MaxField<String> itemName = new MaxField<String>("itemName", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "",
			this);

	private MaxField<String> itemCategory = new MaxField<String>("itemCategory", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING,
			"", "", this);

	private MaxField<String> itemBarcode = new MaxField<String>("itemBarcode", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING,
			"", "", this);

	private MaxField<String> itemURL = new MaxField<String>("itemURL", MaxDBTable.DATA_MYSQL_TYPE_KEY_STRING, "", "",
			this);

	private MaxField<Integer> itemStock = new MaxField<Integer>("itemStock", MaxDBTable.DATA_MYSQL_TYPE_INT, 0, 0,
			this);

	private MaxField<Integer> itemReorderPoint = new MaxField<Integer>("itemReorder", MaxDBTable.DATA_MYSQL_TYPE_INT, 0,
			0, this);

	{
		itemKey.setShowField(false);
		
		this.setKeyField(this.itemKey);
		// A list that contains all the datatypes/field info/default values
		this.addMaxField(itemKey);

		this.addMaxField(itemName);
		this.addMaxField(itemCategory);
		this.addMaxField(itemBarcode);
		this.addMaxField(itemURL);
		this.addMaxField(itemStock);
		this.addMaxField(itemReorderPoint);
	}

	public String debugOutput() {
		return "InventoryItem [itemKey=" + itemKey + ", itemName=" + itemName + ", itemCategory=" + itemCategory
				+ ", itemBarcode=" + itemBarcode + ", itemURL=" + itemURL + ", itemStock=" + itemStock
				+ ", itemReorderPoint=" + itemReorderPoint + "]";
	}

	@Override
	public String getPrimaryKey() {
		String pKey = itemKey.getFieldValue();
		Debugging.output("Inventory Primary key: " + pKey, Debugging.INVENTORY_OUTPUT,
				Debugging.INVENTORY_OUTPUT_ENABLED);
		return pKey;
	}

	// Generate a key
	public void genKey() {
		UUID uid = UUID.randomUUID();
		this.setItemKey(uid.toString());
	}

	/*
	 * Getters / setters NOTE how they are different.
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

	/**
	 * Generates an indexed container
	 * 
	 * @param indexedContainer
	 * @return
	 */
	public IndexedContainer populateContainer(IndexedContainer indexedContainer) {
		for (MaxField<?> field : this.getAutoGenList()) {
			if (field.getShowField())
				indexedContainer.addContainerProperty(field.getFieldName(), field.getExtendedClass(),
						field.getDefaultFieldValue());
		}
		return indexedContainer;
	}

	/**
	 * Generates an item that can be added to a grid.
	 * 
	 * @param item
	 *            the item to generate data into
	 * @return the item with data generated
	 */
	public Item genItem(Item item) {

		for (MaxField<?> mf : this.getAutoGenList()) {
			if (mf.getShowField()) {
				Property p = item.getItemProperty(mf.getFieldName());
				p.setValue(mf.getFieldValue());
			}
		}
		return item;
	}

	@Override
	public Property getItemProperty(Object id) {
		// TODO Improve this by using a map
		HashMap<String, MaxField<?>> fields = new HashMap<String, MaxField<?>>();
		for (MaxField<?> mf : this.autoGenList) {
			if (mf.getShowField())
				fields.put(mf.getFieldName(), mf);
		}

		MaxField<?> f = fields.get(id);

		@SuppressWarnings({ "unchecked", "rawtypes" })
		ObjectProperty prop = new ObjectProperty(f.getFieldValue(), f.getExtendedClass());
		// prop.setValue(f.getFieldValue());

		return prop;
	}

	@Override
	public Collection<?> getItemPropertyIds() {
		Collection<String> ids = new ArrayList<String>();
		for (MaxField<?> mf : this.autoGenList) {
			if (mf.getShowField())
				ids.add(mf.getFieldName());
		}

		return ids;
	}

	@Override
	public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

}
