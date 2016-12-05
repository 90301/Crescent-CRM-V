package ccrmV;

import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;

import clientInfo.UserDataHolder;
import dbUtils.MaxField;
import debugging.Debugging;
import inventory.InventoryCategory;
import inventory.InventoryItem;
import uiElements.NavBar;

public class InventoryView extends HorizontalLayout implements View {

	private static final int MAX_CATEGORY_ROWS = 5;
	public MasterUI masterUi;
	public NavBar navBar;
	private boolean alreadyGenerated;

	Accordion inventoryAccordion = new Accordion();

	// adding new items
	HorizontalLayout createInventoryItemLayout = new HorizontalLayout();
	TextField createInventoryName = new TextField("Item Name");

	ComboBox createInventoryCategory = new ComboBox("Category");

	TextField createInventoryBarcode = new TextField("Barcode");

	Button createInventoryButton = new Button("Create Item", e -> createNewItemClick());

	// create inventory categories
	HorizontalLayout createInventoryCategoryLayout = new HorizontalLayout();
	TextField createInventoryCategoryName = new TextField("Category Name");
	Button createInventoryCategoryButton = new Button("Create Category", e -> createNewItemCategoryClick());
	ListSelect inventoryCategoryListSelect = new ListSelect("Categories");

	// Edit inventory items
	HorizontalLayout editInventoryLayout = new HorizontalLayout();

	Grid editInventoryGrid = new Grid();

	Button editInventoryUpdateButton = new Button("update", e -> editInventoryUpdateClick());

	@Override
	public void enter(ViewChangeEvent event) {
		if (masterUi.loggedIn == false)
			masterUi.enterLogin();

		if (this.alreadyGenerated) {
			this.removeAllComponents();
			// return;
		}

		// create item categories
		inventoryCategoryListSelect.setNullSelectionAllowed(false);

		createInventoryCategoryLayout.setCaption("Create Categories");
		createInventoryCategoryLayout.setSpacing(true);
		
		createInventoryCategoryLayout.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER);

		createInventoryCategoryLayout.addComponent(createInventoryCategoryName);

		createInventoryCategoryLayout.addComponent(createInventoryCategoryButton);

		createInventoryCategoryLayout.addComponent(inventoryCategoryListSelect);

		inventoryAccordion.addComponent(createInventoryCategoryLayout);

		// Create item ui
		createInventoryItemLayout.setCaption("Create Items");
		
		createInventoryItemLayout.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER);
		createInventoryItemLayout.setSpacing(true);

		createInventoryItemLayout.addComponent(createInventoryName);

		createInventoryItemLayout.addComponent(createInventoryCategory);

		createInventoryItemLayout.addComponent(createInventoryBarcode);

		createInventoryItemLayout.addComponent(createInventoryButton);

		inventoryAccordion.addComponent(createInventoryItemLayout);

		// edit item grid

		editInventoryLayout.setCaption("Edit Inventory Items");
		editInventoryLayout.setSpacing(true);
		editInventoryLayout.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER);

		editInventoryGrid.setCaption("Inventory Grid");

		editInventoryGrid.setEditorEnabled(true);

		editInventoryGrid.setWidth("900px");
		//editInventoryGrid.setSizeFull();

		editInventoryLayout.addComponent(editInventoryGrid);

		editInventoryLayout.addComponent(editInventoryUpdateButton);

		inventoryAccordion.addComponent(editInventoryLayout);

		populateData();

		this.addComponent(navBar.sidebarLayout);

		this.addComponent(inventoryAccordion);

		this.alreadyGenerated = true;
	}

	/**
	 * Event for clicking the "update" button
	 */
	private void editInventoryUpdateClick() {
		// TODO Finish this
		for (InventoryItem ii : masterUi.userDataHolder.getMap(InventoryItem.class).values()) {
			// get the vaadin item in the collection
			Item item = inventoryItems.getItem(ii);
			// compare that to the old value to see if it's been updated
			Boolean itemUpdated = false;
			for (Object id : ii.getItemPropertyIds()) {
				
				Debugging.output("Checking (ID): " + id,
						Debugging.INVENTORY_OUTPUT,
						Debugging.INVENTORY_VIEW_OUTPUT_ENABLED);
				
				// get the vaadin value (potentially updated)
				Property<?> nObject = item.getItemProperty(id);
				Object nValue = nObject.getValue();
				// Get the old value
				Object oldValue = ii.getField((String) id).getFieldValue();
				
				Debugging.output("Comparing: " + nValue + " Old: " + oldValue,
						Debugging.INVENTORY_OUTPUT,
						Debugging.INVENTORY_VIEW_OUTPUT_ENABLED);
				
				if (nValue != oldValue) {
					// trigger an update for the database
					itemUpdated = true;
					// update the value to the new value

					ii.setFieldValue((String) id, nValue);

					Debugging.output("updating Value: " + nValue + " Old: " + oldValue,
							Debugging.INVENTORY_OUTPUT,
							Debugging.INVENTORY_VIEW_OUTPUT_ENABLED);
				}

			} // end loop for fields

			// store the value if updated
			if (itemUpdated) {
				masterUi.userDataHolder.store(ii, InventoryItem.class);
			}

		}

	}

	IndexedContainer inventoryItems;// = new
									// IndexedContainer(masterUi.userDataHolder.getMap(InventoryCategory.class).values());

	/**
	 * Populates various UI elements.
	 */
	public void populateData() {

		InventoryItem exampleii = new InventoryItem();

		inventoryItems = new IndexedContainer();// masterUi.userDataHolder.getMap(InventoryCategory.class).values());

		exampleii.populateContainer(inventoryItems);

		for (InventoryItem ii : masterUi.userDataHolder.getMap(InventoryItem.class).values()) {
			// RESUME WORKING HERE

			inventoryItems.addItem(ii);

			Item item = inventoryItems.getItem(ii);
			ii.genItem(item);

			// RESUME WORKING HERE
		}
		// inventoryItems.addP

		inventoryCategoryListSelect.removeAllItems();

		createInventoryCategory.removeAllItems();

		editInventoryGrid.removeAllColumns();

		inventoryCategoryListSelect.addItems(masterUi.userDataHolder.getMap(InventoryCategory.class).keySet());

		int categoryRows = masterUi.userDataHolder.getMap(InventoryCategory.class).keySet().size() + 1;
		if (categoryRows > MAX_CATEGORY_ROWS) {
			categoryRows = MAX_CATEGORY_ROWS;
		}
		inventoryCategoryListSelect.setRows(categoryRows);
		
		createInventoryCategory.addItems(masterUi.userDataHolder.getMaxObjects(InventoryCategory.class));

		// editInventoryGrid.addColumn("ItemName", String.class);

		editInventoryGrid.removeAllColumns();

		editInventoryGrid.setContainerDataSource(inventoryItems);

	}

	/**
	 * Click event for creating a new inventory category
	 */
	private void createNewItemCategoryClick() {
		createNewItemCategory(createInventoryCategoryName.getValue());
		createInventoryCategoryName.clear();
	}

	private void createNewItemClick() {
		// TODO Auto-generated method stub
		createNewItem((String) createInventoryName.getValue(), (InventoryCategory) createInventoryCategory.getValue(),
				(String) createInventoryBarcode.getValue());
	}

	public void createNewItemCategory(String categoryName) {
		if (categoryName == null || categoryName == "")
			return;

		InventoryCategory ic = new InventoryCategory(categoryName);
		masterUi.userDataHolder.store(ic, InventoryCategory.class);

		populateData();
		
		inventoryCategoryListSelect.select(categoryName);
		
	}

	public void createNewItem(String name, InventoryCategory category, String barcode) {

		if (name == null || category == null || barcode == null)
			return;

		InventoryItem ii = new InventoryItem();

		ii.setItemName(name);
		ii.setItemCategory(category.getPrimaryKey());
		ii.setItemBarcode(barcode);
		ii.genKey();
		masterUi.userDataHolder.store(ii, InventoryItem.class);

		populateData();

	}

}
