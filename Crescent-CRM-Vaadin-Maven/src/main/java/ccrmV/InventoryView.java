package ccrmV;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
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
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		if (masterUi.loggedIn == false)
			masterUi.enterLogin();
		
		if (this.alreadyGenerated ) {
			this.removeAllComponents();
			//return;
		}
		//Test categories
		InventoryCategory ic1 = new InventoryCategory("Dye");
		InventoryCategory ic2 = new InventoryCategory("Shampoo");
		
		createInventoryItemLayout.setCaption("Create Items");
		
		createInventoryItemLayout.addComponent(createInventoryName);
		
		createInventoryItemLayout.addComponent(createInventoryCategory);
		
		createInventoryCategory.addItem(ic1);
		createInventoryCategory.addItem(ic2);
		
		createInventoryItemLayout.addComponent(createInventoryBarcode);
		
		createInventoryItemLayout.addComponent(createInventoryButton);
		
		inventoryAccordion.addComponent(createInventoryItemLayout);
		
		
		
		
		
		
		
		this.addComponent(navBar.sidebarLayout);
		
		this.addComponent(inventoryAccordion);
		
		this.alreadyGenerated = true;
	}


	private void createNewItemClick() {
		// TODO Auto-generated method stub
		createNewItem((String) createInventoryName.getValue(),(InventoryCategory) createInventoryCategory.getValue(), (String) createInventoryBarcode.getValue());
	}


	public void createNewItem(String name, InventoryCategory category, String barcode) {
		System.out.println("ID: " + category.id);
		
	}

}
