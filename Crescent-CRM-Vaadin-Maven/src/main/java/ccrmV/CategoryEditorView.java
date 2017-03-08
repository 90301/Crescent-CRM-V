package ccrmV;

import java.util.Collection;
import java.util.HashSet;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.colorpicker.ColorPickerSelect;

import clientInfo.Location;
import clientInfo.Status;
import clientInfo.UserDataHolder;
import dbUtils.InhalerUtils;
import debugging.Debugging;
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
	VerticalLayout statusEditorLayout = new VerticalLayout();
	
	Location selectedLocation;
	
	
	//location editor components
	HorizontalLayout newLocationLayout = new HorizontalLayout();
	TextField newLocationNameTextBox = new TextField();
	Button newLocationCreateButton = new Button("Create", e -> createLocationClick());
	ListSelect newLocationAllLocations = new ListSelect("Locations");//Should Filter as the user types for the new location
	
	HorizontalLayout locationSeperator = new HorizontalLayout();
	Label hrule = new Label("-----------------------------------------------------");
	
	VerticalLayout editExistingLocationLayout = new VerticalLayout();
	Label editLocationsSelectedLabel = new Label("Editing Location: ");
	ComboBox editLocationSelectionBox = new ComboBox();
	TwinColSelect editLocationProximitySelect = new TwinColSelect("Proximity");
	Button editLocationUpdateButton = new Button("Update", e-> editLocationUpdateClick());
	/*
	 * |---------------------------------------------|
	 * | (New Location) - - - - - - - - - - - - - - -|
	 * | (Text Box name) - (Button create) - (exists)|
	 * |---------------------------------------------|
	 * | (Edit Existing Locations) - - - - - - - - - |
	 * | (Selected Location Label) - - - - - - - - - |
	 * | (Location to Edit Drop-down box) - - - - - -|
	 * | (proximity Twin Col Select) - - - - - - - - |
	 * | (update button) - - - - - - - - - - - - - - |
	 * |---------------------------------------------|
	 * 
	 */
	
	HorizontalLayout newStatusLayout = new HorizontalLayout();
	TextField newStatusNameTextBox = new TextField();
	Button newStatusCreateButton = new Button("Create", e -> createStatusClick());
	ListSelect newStatusAllStatus = new ListSelect("Status");//Should Filter as the user types for the new location
	
	HorizontalLayout statusSeperator = new HorizontalLayout();
	
	VerticalLayout editExistingStatusLayout = new VerticalLayout();
	Label editStatusSelectedLabel = new Label("Editing Status: ");
	ComboBox editStatusSelectionBox = new ComboBox();
	ColorPicker editStatusColorPicker = new ColorPicker();
	
	Button editStatusUpdateButton = new Button("Update", e-> editLocationUpdateClick());
	
	/*
	 * |---------------------------------------------|
	 * | (New Status) - - - - - - - - - - - - - - - -|
	 * | (Text Box name) - (Button create) - (exists)|
	 * |---------------------------------------------|
	 * | (Edit Existing Status) - - - - - - - - - - -|
	 * | (Selected Location Label) - - - - - - - - - |
	 * | (Location to Edit Drop-down box) - - - - - -|
	 * | (Color selection) - - - - - - - - - - - - - |
	 * | (update button) - - - - - - - - - - - - - - |
	 * |---------------------------------------------|
	 * 
	 */
	
	{
		this.setSpacing(true);
		newLocationLayout.setSpacing(true);
		newStatusLayout.setSpacing(true);
		editExistingLocationLayout.setSpacing(true);
		editExistingStatusLayout.setSpacing(true);
		//Tab Names
		locationEditorLayout.setCaption("Locations");
		statusEditorLayout.setCaption("Status");
		//Layout Names
		
		//Locations
		newLocationLayout.setCaption("New Locations");	
		editExistingLocationLayout.setCaption("Edit Existing Locations");
		
		newLocationAllLocations.setNullSelectionAllowed(false);
		editLocationSelectionBox.setNullSelectionAllowed(false);
		editLocationProximitySelect.setNullSelectionAllowed(false);
		
		
		//Status
		newStatusLayout.setCaption("New Status");	
		editExistingStatusLayout.setCaption("Edit Existing Status");
		
		newStatusAllStatus.setNullSelectionAllowed(false);
		editStatusSelectionBox.setNullSelectionAllowed(false);
		
		editStatusColorPicker.setCaption("Status Color (Beta)");
		editStatusColorPicker.setSwatchesVisibility(true);
		editStatusColorPicker.setHistoryVisibility(false);
		editStatusColorPicker.setTextfieldVisibility(false);
		editStatusColorPicker.setHSVVisibility(false);
		
		//Events
		editLocationSelectionBox.addValueChangeListener(e -> selectLocation());
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
		
		
		//LOCATION
		
		newLocationLayout.addComponent(newLocationNameTextBox);
		newLocationLayout.addComponent(newLocationCreateButton);
		newLocationLayout.addComponent(newLocationAllLocations);
		
		
		hrule.setCaptionAsHtml(true);
		locationSeperator.addComponent(hrule);
		
		//Edit existing 
		editExistingLocationLayout.addComponent(editLocationsSelectedLabel);
		editExistingLocationLayout.addComponent(editLocationSelectionBox);
		editExistingLocationLayout.addComponent(editLocationProximitySelect);
		editExistingLocationLayout.addComponent(editLocationUpdateButton);
		editExistingLocationLayout.setComponentAlignment(editLocationUpdateButton, Alignment.BOTTOM_RIGHT);
		
		
		locationEditorLayout.addComponent(newLocationLayout);
		
		locationEditorLayout.addComponent(locationSeperator);

		locationEditorLayout.addComponent(editExistingLocationLayout);
		
		
		//STATUS
		newStatusLayout.addComponent(newStatusNameTextBox);
		newStatusLayout.addComponent(newStatusCreateButton);
		newStatusLayout.addComponent(newStatusAllStatus);
		
		statusSeperator.addComponent(hrule);
		
		editExistingStatusLayout.addComponent(editStatusSelectedLabel);
		editExistingStatusLayout.addComponent(editStatusSelectionBox);
		editExistingStatusLayout.addComponent(editStatusColorPicker);
		editExistingStatusLayout.addComponent(editStatusUpdateButton);
		editExistingStatusLayout.setComponentAlignment(editStatusUpdateButton, Alignment.BOTTOM_RIGHT);
		
		
		statusEditorLayout.addComponent(newStatusLayout);
		
		statusEditorLayout.addComponent(statusSeperator);

		statusEditorLayout.addComponent(editExistingStatusLayout);
		
		//Tab Layout
		categoryTabs.addComponent(locationEditorLayout);
		
		categoryTabs.addComponent(statusEditorLayout);
		
		
		this.addComponent(categoryTabs);
		
		//Populate data
		populateAllLocationBoxes();
		populateAllStatusBoxes();
		
		alreadyGenerated = true;
	}
	








	public void createLocationClick() {
		String text = this.newLocationNameTextBox.getValue();
		createLocation(text);
		this.newLocationNameTextBox.clear();
		
	}
	
	
	public void createStatusClick() {
		// TODO Auto-generated method stub
		
	}

	public Location createLocation(String locationName) {
		Location l = null;
		// Check for valid input
		if (!InhalerUtils.stringNullCheck(locationName)) {
			l = new Location();
			l.setLocationName(locationName);
			masterUi.userDataHolder.store(l, Location.class);
		}
		populateAllLocationBoxes();
		
		newLocationAllLocations.setValue(l);
		
		return l;
	}

	public Status createStatus(String statusName) {
		Status s = null;
		// Check for valid input
		if (!InhalerUtils.stringNullCheck(statusName)) {
			s = new Status();
			s.setStatusName(statusName);
			masterUi.userDataHolder.store(s, Status.class);
		}
		populateAllStatusBoxes();
		
		newStatusAllStatus.setValue(s);
		
		return s;
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
	
	private void populateAllStatusBoxes() {
		UserDataHolder udh = masterUi.userDataHolder;
		
		newStatusAllStatus.removeAllItems();
		newStatusAllStatus.addItems(udh.getAllStatus());
		
		editStatusSelectionBox.removeAllItems();
		editStatusSelectionBox.addItems(udh.getAllStatus());
		
	}

	
	private void loadProximity(Location l) {
		editLocationProximitySelect.removeAllItems();
		editLocationProximitySelect.addItems(masterUi.userDataHolder.getAllLocations());
		
		
		//Must remove own location from combo box items
		editLocationProximitySelect.removeItem(l);
		//then add all the locations to the "proximity side" if they are a proximity location
		for (Location closeLocation : l.getRealCloseLocations()) {
			editLocationProximitySelect.select(closeLocation);
			Debugging.output("Close Location loaded: " + closeLocation , Debugging.CATEGORY_EDITOR_DEBUG);
		}
	}
	private void selectLocation() {
		Debugging.output("Selected Location: " + editLocationSelectionBox.getValue() , Debugging.CATEGORY_EDITOR_DEBUG);
		
		if (editLocationSelectionBox.getValue()==null) {
			//null selection
			return;
		}
		
		String locationName = editLocationSelectionBox.getValue().toString();
		
		if (locationName!=null) {
			selectEditLocation(locationName);
		} else {
			//NULL SELECTION!
		}
	}
	
	public void selectEditLocation(String locationName) {
		UserDataHolder udh = masterUi.userDataHolder;
		
		editLocationsSelectedLabel.setValue("Editing Location: " + locationName);
		//Find the location in the user data holder
		
		Location l = udh.getLocation(locationName);
		
		//load proximitys for location L
		loadProximity(l);
		
		selectedLocation = l;
	}
	private void editLocationUpdateClick() {
		if (selectedLocation!=null) {
			editLocationUpdate();
		}
	}
	
	private void editLocationUpdate() {
		UserDataHolder udh = masterUi.userDataHolder;
		
		HashSet<Location> closeLocations = new HashSet<Location>();
		for (Location proximityLocation : (Collection<Location>) editLocationProximitySelect.getValue()) {
			//System.out.println("Prox Location found: " + proximityLocation);
			closeLocations.add(proximityLocation);
			
		}
		
		selectedLocation.setRealCloseLocations(closeLocations);
		//check to see if the close locations have been edited
		for (Location l : selectedLocation.getRealCloseLocations()) {
			Debugging.output("Close Location: " + l , Debugging.CATEGORY_EDITOR_DEBUG);
		}
		
		//update the database
		udh.store(selectedLocation, Location.class);
		
	}

}
