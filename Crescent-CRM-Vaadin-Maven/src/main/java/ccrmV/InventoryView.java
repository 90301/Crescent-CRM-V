package ccrmV;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;

import inventory.InventoryCategory;
import uiElements.NavBar;

public class InventoryView extends HorizontalLayout implements View {

	public MasterUI masterUi;
	public NavBar navBar;
	private boolean alreadyGenerated;
	
	Accordion inventoryAccordion = new Accordion();
	
	//adding new items
	HorizontalLayout createInventoryItemLayout = new HorizontalLayout();
	TextField createInventoryName = new TextField("Item Name");
	
	ComboBox createInventoryCategory = new ComboBox("Category");
	
	TextField createInventoryBarcode = new TextField("Barcode");
	
	Button createInventoryButton = new Button("Create Item",e -> createNewItemClick());
	
	//create inventory categories
	HorizontalLayout createInventoryCategoryLayout = new HorizontalLayout();
	TextField createInventoryCategoryName = new TextField("Category Name");
	Button createInventoryCategoryButton = new Button("Create Category",e -> createNewItemCategoryClick());
	ListSelect inventoryCategoryListSelect = new ListSelect("Categories");
	
	@Override
	public void enter(ViewChangeEvent event) {
		if (masterUi.loggedIn == false)
			masterUi.enterLogin();
		
		if (this.alreadyGenerated ) {
			this.removeAllComponents();
			//return;
		}
		
		//create item categories
		
		createInventoryCategoryLayout.setCaption("Create Categories");
		
		createInventoryCategoryLayout.addComponent(createInventoryCategoryName);
		
		createInventoryCategoryLayout.addComponent(createInventoryCategoryButton);
		
		createInventoryCategoryLayout.addComponent(inventoryCategoryListSelect);
		
		inventoryAccordion.addComponent(createInventoryCategoryLayout);
		
		
		//Create item ui
		createInventoryItemLayout.setCaption("Create Items");
		
		createInventoryItemLayout.addComponent(createInventoryName);
		
		createInventoryItemLayout.addComponent(createInventoryCategory);

		
		createInventoryItemLayout.addComponent(createInventoryBarcode);
		
		createInventoryItemLayout.addComponent(createInventoryButton);
		
		inventoryAccordion.addComponent(createInventoryItemLayout);
		
		
		populateData();
		
		
		this.addComponent(navBar.sidebarLayout);
		
		this.addComponent(inventoryAccordion);
		
		this.alreadyGenerated = true;
	}


	/**
	 * Populates various UI elements.
	 */
	public void populateData() {
		
		inventoryCategoryListSelect.removeAllItems();
		
		createInventoryCategory.removeAllItems();
		
		inventoryCategoryListSelect.addItems(masterUi.userDataHolder.getMaxObjects(InventoryCategory.class));
		
		createInventoryCategory.addItems(masterUi.userDataHolder.getMaxObjects(InventoryCategory.class));
		
		
	}


	/**
	 * Click event for creating a new inventory category
	 */
	private void createNewItemCategoryClick() {
		createNewItemCategory(createInventoryCategoryName.getValue());
	}


	private void createNewItemClick() {
		// TODO Auto-generated method stub
		createNewItem((String) createInventoryName.getValue(),(InventoryCategory) createInventoryCategory.getValue(), (String) createInventoryBarcode.getValue());
	}

	public void createNewItemCategory(String categoryName) {
		if (categoryName==null || categoryName=="")
			return;
		
		InventoryCategory ic = new InventoryCategory(categoryName);
		masterUi.userDataHolder.store(ic, InventoryCategory.class);
		
		populateData();
	}
	
	public void createNewItem(String name, InventoryCategory category, String barcode) {
		//System.out.println("ID: " + category.id);
		
	}

}
