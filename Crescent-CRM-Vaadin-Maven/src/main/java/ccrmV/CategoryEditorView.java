package ccrmV;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;

import clientInfo.Location;
import clientInfo.UserDataHolder;
import uiElements.NavBar;

public class CategoryEditorView extends HorizontalLayout implements View {
	
	MasterUI masterUi;
	NavBar navBar;
	Boolean alreadyGenerated = false;
	
	/*
	 * |------------------------------------------------------------------|
	 * |(Nav Bar) | - - - (Main Content) - - - - - - - - - - - - - - - - -|
	 * |(Nav Bar) | - - - (Tabs) - - - - - - - - - - - - - - - - - - - - -|
	 * |(Nav Bar) | - - - (Category Editors) - - - - - - - - - - - - - - -|
	 * |(Nav Bar) | - - - (Location Editor)- - - - - - - - - - - - - - - -|
	 * |------------------------------------------------------------------|
	 */
	
	/*
	 *Each editor will have it's own layout, this way we can move them between tabs or accordians or anything else.
	 */
	TabSheet categoryTabs = new TabSheet();
	
	VerticalLayout locationEditorLayout = new VerticalLayout();
	
	
	
	
	//location editor components
	HorizontalLayout newLocationLayout = new HorizontalLayout();
	TextField newLocationNameTextBox = new TextField();
	Button newLocationCreateButton = new Button("Create");
	ListSelect newLocationAllLocations = new ListSelect("Locations");//Should Filter as the user types for the new location
	
	HorizontalLayout locationSeperator = new HorizontalLayout();
	Label hrule = new Label("-----------------------------------------------------");
	
	VerticalLayout editExistingLocationLayout = new VerticalLayout();
	ComboBox editLocationSelectionBox = new ComboBox();
	TwinColSelect editLocationProximitySelect = new TwinColSelect("Proximity");
	Button editLocationUpdateButton = new Button("Update");
	/*
	 * |---------------------------------------------|
	 * | (New Location) - - - - - - - - - - - - - - -|
	 * | (Text Box name) - (Button create) - (exists)|
	 * |---------------------------------------------|
	 * | (Edit Existing Locations) - - - - - - - - - |
	 * | (Location to Edit Drop-down box) - - - - - -|
	 * | (proximity Twin Col Select) - - - - - - - - |
	 * | (update button) - - - - - - - - - - - - - - |
	 * |---------------------------------------------|
	 * 
	 */
	
	{
		this.setSpacing(true);
		newLocationLayout.setSpacing(true);
		editExistingLocationLayout.setSpacing(true);
		//Tab Names
		locationEditorLayout.setCaption("Locations");
		
		//Layout Names
		newLocationLayout.setCaption("New Locations");	
		editExistingLocationLayout.setCaption("Edit Existing Locations");
		
		newLocationAllLocations.setNullSelectionAllowed(false);
		editLocationSelectionBox.setNullSelectionAllowed(false);
		editLocationProximitySelect.setNullSelectionAllowed(false);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		
		if (masterUi.loggedIn == false)
			masterUi.enterLogin();

		if (this.alreadyGenerated) {
			this.removeAllComponents();
			// return;
		}
		this.addComponent(navBar.sidebarLayout);
		
		newLocationLayout.addComponent(newLocationNameTextBox);
		newLocationLayout.addComponent(newLocationCreateButton);
		newLocationLayout.addComponent(newLocationAllLocations);
		
		
		hrule.setCaptionAsHtml(true);
		locationSeperator.addComponent(hrule);
		
		//Edit existing 
		editExistingLocationLayout.addComponent(editLocationSelectionBox);
		editExistingLocationLayout.addComponent(editLocationProximitySelect);
		editExistingLocationLayout.addComponent(editLocationUpdateButton);
		editExistingLocationLayout.setComponentAlignment(editLocationUpdateButton, Alignment.BOTTOM_RIGHT);
		
		
		locationEditorLayout.addComponent(newLocationLayout);
		
		locationEditorLayout.addComponent(locationSeperator);

		locationEditorLayout.addComponent(editExistingLocationLayout);
		
		//Tab Layout
		categoryTabs.addComponent(locationEditorLayout);
		
		this.addComponent(categoryTabs);
		
		//Populate data
		populateAllLocationBoxes();
		
		
		alreadyGenerated = true;
	}
	
	//Populate Locations
	
	/**
	 * updates all location comboboxes. Useful when adding new locations
	 * or loading the page.
	 * USE CAUTION with this method, and probably don't call this
	 * if something is selected, instead update only the necessary boxes
	 * or only add items that are not currently in the box (with out changing selection)
	 */
	public void populateAllLocationBoxes() {
		//Get the current User Data Holder
		UserDataHolder udh = masterUi.userDataHolder;
		
		newLocationAllLocations.removeAllItems();
		newLocationAllLocations.addItems(udh.getAllLocations());
		
		editLocationSelectionBox.removeAllItems();
		editLocationSelectionBox.addItems(udh.getAllLocations());
		
		editLocationProximitySelect.removeAllItems();
		editLocationProximitySelect.addItems(udh.getAllLocations());
	}
	/**
	 * Adds item only if it doesn't exist (not good for removing / renaming locations)
	 */
	public void safePopulateLocations() {
		UserDataHolder udh = masterUi.userDataHolder;
		
		
		for (Location l : udh.getAllLocations()) {
			if (!newLocationAllLocations.containsId(l)) {
				newLocationAllLocations.addItem(l);
			}
			
			if (!editLocationSelectionBox.containsId(l)) {
				editLocationSelectionBox.addItem(l);
			}
			
			if (!editLocationProximitySelect.containsId(l)) {
				editLocationProximitySelect.addItem(l);
			}
			
		}
	}
	
	
}
