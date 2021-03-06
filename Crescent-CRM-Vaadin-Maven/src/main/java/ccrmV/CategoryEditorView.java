package ccrmV;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import clientInfo.Group;
import clientInfo.Location;
import clientInfo.Status;
import clientInfo.UserDataHolder;
import dbUtils.InhalerUtils;
import debugging.Debugging;

public class CategoryEditorView extends CrescentView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String TAB_WIDTH = "600px";
	private static final String TAB_HEIGHT = "800px";
	private static final Boolean MANUAL_TAB_SIZE = false;
	private static final Boolean MANUAL_TAB_HEIGHT = true;

	//MasterUI masterUi;
	//NavBar navBar;
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
	//Accordion categoryTabs = new Accordion();

	VerticalLayout locationEditorLayout = new VerticalLayout();
	VerticalLayout statusEditorLayout = new VerticalLayout();
	VerticalLayout groupEditorLayout = new VerticalLayout();

	Location selectedLocation;
	Status selectedStatus;
	Group selectedGroup;

	//location editor components
	HorizontalLayout newLocationLayout = new HorizontalLayout();
	TextField newLocationNameTextBox = new TextField();
	Button newLocationCreateButton = new Button("Create", e -> createLocationClick());
	ListSelect<Location> newLocationAllLocations = new ListSelect<Location>("Locations");//Should Filter as the user types for the new location

	HorizontalLayout locationSeperator = new HorizontalLayout();
	//Label hrule = new Label("------------------------------------------------------------------------------------------------------------------");
	
	public static final String hRuleText =
			"------------------------------------------------------------------------------------------------------------------";
	
	Label groupHrule = CategoryEditorView.genHrule();
	Label statusHrule = CategoryEditorView.genHrule();
	Label locationHrule = CategoryEditorView.genHrule();
	
	VerticalLayout editExistingLocationLayout = new VerticalLayout();
	Label editLocationsSelectedLabel = new Label("Editing Location: ");
	ComboBox<Location> editLocationSelectionBox = new ComboBox<Location>();
	TwinColSelect<Location> editLocationProximitySelect = new TwinColSelect<Location>("Proximity");
	
	HorizontalLayout editLocationActionLayout = new HorizontalLayout();
	
	Button editLocationUpdateButton = new Button("Update", e -> editLocationUpdateClick());
	Button editLocationDeleteButton = new Button("Delete", e -> editLocationDeleteClick());
	
	
	/*
	 * |---------------------------------------------|
	 * | (New Location) - - - - - - - - - - - - - - -|
	 * | (Text Box name) - (Button create) - (exists)|
	 * |---------------------------------------------|
	 * | (Edit Existing Locations) - - - - - - - - - |
	 * | (Selected Location Label) - - - - - - - - - |
	 * | (Location to Edit Drop-down box) - - - - - -|
	 * | (proximity Twin Col Select) - - - - - - - - |
	 * | (Delete button) (update button) - - - - - - |
	 * |---------------------------------------------|
	 * 
	 */

	HorizontalLayout newStatusLayout = new HorizontalLayout();
	TextField newStatusNameTextBox = new TextField();
	Button newStatusCreateButton = new Button("Create", e -> createStatusClick());
	ListSelect<Status> newStatusAllStatus = new ListSelect<Status>("Status");//Should Filter as the user types for the new location

	HorizontalLayout statusSeperator = new HorizontalLayout();

	VerticalLayout editExistingStatusLayout = new VerticalLayout();
	Label editStatusSelectedLabel = new Label("Editing Status: ");
	ComboBox<Status> editStatusSelectionBox = new ComboBox<Status>();
	ColorPicker editStatusColorPicker = new ColorPicker();
	
	HorizontalLayout editStatusActionLayout = new HorizontalLayout();

	Button editStatusUpdateButton = new Button("Update", e -> editStatusUpdateClick());
	Button editStatusDeleteButton = new Button("Delete", e -> editStatusDeleteClick());

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

	HorizontalLayout newGroupLayout = new HorizontalLayout();
	TextField newGroupNameTextBox = new TextField();
	Button newGroupCreateButton = new Button("Create", e -> createGroupClick());
	ListSelect<Group> newGroupAllGroups = new ListSelect<Group>("Group");//Should Filter as the user types for the new location

	HorizontalLayout GroupSeperator = new HorizontalLayout();

	VerticalLayout editExistingGroupLayout = new VerticalLayout();
	Label editGroupSelectedLabel = new Label("Editing Group: ");
	ComboBox<Group> editGroupSelectionBox = new ComboBox<Group>();
	ColorPicker editGroupColorPicker = new ColorPicker();
	
	HorizontalLayout editGroupActionLayout = new HorizontalLayout();

	Button editGroupUpdateButton = new Button("Update", e -> editGroupUpdateClick());
	Button editGroupDeleteButton = new Button("Delete", e -> editGroupDeleteClick());

	/*
	 * |---------------------------------------------|
	 * | (New Group) - - - - - - - - - - - - - - - - |
	 * | (Text Box name) - (Button create) - (exists)|
	 * |---------------------------------------------|
	 * | (Edit Existing Groups) - - - - - - - - - - -|
	 * | (Selected Location Label) - - - - - - - - - |
	 * | (Group to Edit Drop-down box) - - - - - - - |
	 * | (Color selection) - - - - - - - - - - - - - |
	 * | (update button) - - - - - - - - - - - - - - |
	 * |---------------------------------------------|
	 * 
	 */

	{

		setupUI(newLocationLayout, editExistingLocationLayout);
		setupUI(newStatusLayout, editExistingStatusLayout);
		setupUI(newGroupLayout, editExistingGroupLayout);
		
		setupActionLayout(editLocationActionLayout);
		setupActionLayout(editStatusActionLayout);
		setupActionLayout(editGroupActionLayout);

		//Tab Names
		locationEditorLayout.setCaption("Locations");
		statusEditorLayout.setCaption("Status");
		groupEditorLayout.setCaption("Groups");
		//Layout Names

		//Locations
		//TODO add this to setup UI
		newLocationLayout.setCaption("New Locations");
		editExistingLocationLayout.setCaption("Edit Existing Locations");

		//Status
		newStatusLayout.setCaption("New Status");
		editExistingStatusLayout.setCaption("Edit Existing Status");

		editStatusColorPicker.setCaption("Status Color (Beta)");
		editStatusColorPicker.setSwatchesVisibility(true);
		editStatusColorPicker.setHistoryVisibility(false);
		editStatusColorPicker.setTextfieldVisibility(false);
		editStatusColorPicker.setHSVVisibility(false);

		//Group
		newGroupLayout.setCaption("New Group");
		editExistingGroupLayout.setCaption("Edit Existing Group");

		editGroupColorPicker.setCaption("Group Color (Beta)");
		editGroupColorPicker.setSwatchesVisibility(true);
		editGroupColorPicker.setHistoryVisibility(false);
		editGroupColorPicker.setTextfieldVisibility(false);
		editGroupColorPicker.setHSVVisibility(false);
		
		editGroupColorPicker.setVisible(false);

		//Events
		editLocationSelectionBox.addValueChangeListener(e -> selectLocation());
		editStatusSelectionBox.addValueChangeListener(e -> selectStatus());
		editGroupSelectionBox.addValueChangeListener(e -> selectGroup());
	}

	public static void setupUI(HorizontalLayout newLayout, VerticalLayout existingLayout) {
		newLayout.setSpacing(true);
		newLayout.addStyleName("leftPadding");
		newLayout.setMargin(false);

		existingLayout.setSpacing(true);
		existingLayout.setMargin(false);
		existingLayout.addStyleName("leftPadding");
		existingLayout.addStyleName("bottomPadding");
	}



	@Override
	public void enterView(ViewChangeEvent event) {

		if (masterUi.loggedIn == false)
			masterUi.enterLogin();

		//LOCATION

		newLocationLayout.addComponent(newLocationNameTextBox);
		newLocationLayout.addComponent(newLocationCreateButton);
		newLocationLayout.addComponent(newLocationAllLocations);

		//hrule.setCaptionAsHtml(true);
		locationSeperator.addComponent(locationHrule);

		//Edit existing 
		editExistingLocationLayout.addComponent(editLocationsSelectedLabel);
		editExistingLocationLayout.addComponent(editLocationSelectionBox);
		editExistingLocationLayout.addComponent(editLocationProximitySelect);
		
		//editExistingLocationLayout.addComponent(editLocationUpdateButton);
		//editExistingLocationLayout.setComponentAlignment(editLocationUpdateButton, Alignment.BOTTOM_RIGHT);
		//editLocationActionLayout.setDefaultComponentAlignment(Alignment.BOTTOM_RIGHT);
		editLocationActionLayout.addComponent(editLocationUpdateButton);
		editLocationActionLayout.addComponent(editLocationDeleteButton);
		
		editExistingLocationLayout.addComponent(editLocationActionLayout);

		locationEditorLayout.addComponent(newLocationLayout);

		locationEditorLayout.addComponent(locationSeperator);

		locationEditorLayout.addComponent(editExistingLocationLayout);

		//STATUS
		newStatusLayout.addComponent(newStatusNameTextBox);
		newStatusLayout.addComponent(newStatusCreateButton);
		newStatusLayout.addComponent(newStatusAllStatus);

		statusSeperator.addComponent(statusHrule);

		editExistingStatusLayout.addComponent(editStatusSelectedLabel);
		editExistingStatusLayout.addComponent(editStatusSelectionBox);
		editExistingStatusLayout.addComponent(editStatusColorPicker);
		/*
		editExistingStatusLayout.addComponent(editStatusUpdateButton);
		editExistingStatusLayout.setComponentAlignment(editStatusUpdateButton, Alignment.BOTTOM_RIGHT);
		*/
		editStatusActionLayout.addComponent(editStatusUpdateButton);
		editStatusActionLayout.addComponent(editStatusDeleteButton);
		
		editExistingStatusLayout.addComponent(editStatusActionLayout);

		statusEditorLayout.addComponent(newStatusLayout);

		statusEditorLayout.addComponent(statusSeperator);

		statusEditorLayout.addComponent(editExistingStatusLayout);

		//Group
		newGroupLayout.addComponent(newGroupNameTextBox);
		newGroupLayout.addComponent(newGroupCreateButton);
		newGroupLayout.addComponent(newGroupAllGroups);

		GroupSeperator.addComponent(groupHrule);

		editExistingGroupLayout.addComponent(editGroupSelectedLabel);
		editExistingGroupLayout.addComponent(editGroupSelectionBox);
		editExistingGroupLayout.addComponent(editGroupColorPicker);
		/*
		editExistingGroupLayout.addComponent(editGroupUpdateButton);
		editExistingGroupLayout.setComponentAlignment(editGroupUpdateButton, Alignment.BOTTOM_RIGHT);
		*/
		editGroupActionLayout.addComponent(editGroupUpdateButton);
		editGroupActionLayout.addComponent(editGroupDeleteButton);
		
		editExistingGroupLayout.addComponent(editGroupActionLayout);

		groupEditorLayout.addComponent(newGroupLayout);

		groupEditorLayout.addComponent(GroupSeperator);

		groupEditorLayout.addComponent(editExistingGroupLayout);

		//Tab Layout
		
		//categoryTabs.setWidth(TAB_WIDTH);
		categoryTabs.setResponsive(true);
		
		if (MANUAL_TAB_SIZE) {
			categoryTabs.setWidth(TAB_WIDTH);
			categoryTabs.setHeight(TAB_HEIGHT);
		} else if (MANUAL_TAB_HEIGHT) {
			categoryTabs.setHeight(TAB_HEIGHT);
		}
		
		categoryTabs.addComponent(locationEditorLayout);

		categoryTabs.addComponent(statusEditorLayout);

		categoryTabs.addComponent(groupEditorLayout);

		this.addComponent(categoryTabs);

		//Populate data
		populateAllLocationBoxes();
		populateAllStatusBoxes();
		populateAllGroupBoxes();

		//alreadyGenerated = true;
	}
	
	public static Label genHrule() {
		return new Label(hRuleText);
	}

	public HorizontalLayout setupActionLayout(HorizontalLayout actionLayout) {
		actionLayout.setMargin(false);
		actionLayout.setDefaultComponentAlignment(Alignment.BOTTOM_RIGHT);
		
		return actionLayout;
	}

	public void createLocationClick() {
		String text = this.newLocationNameTextBox.getValue();
		createLocation(text);
		this.newLocationNameTextBox.clear();

	}

	public void createStatusClick() {
		String text = this.newStatusNameTextBox.getValue();
		createStatus(text);
		this.newStatusNameTextBox.clear();

	}

	public void createGroupClick() {
		String text = this.newGroupNameTextBox.getValue();
		createGroup(text);
		this.newStatusNameTextBox.clear();
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

		newLocationAllLocations.setValue(InhalerUtils.singleSelect(l));

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

		newStatusAllStatus.setValue(InhalerUtils.singleSelect(s));

		return s;
	}

	public Group createGroup(String groupName) {
		Group g = null;
		// Check for valid input
		if (!InhalerUtils.stringNullCheck(groupName)) {
			g = new Group();
			g.setGroupName(groupName);
			masterUi.userDataHolder.store(g, Group.class);
		}
		populateAllGroupBoxes();

		newGroupAllGroups.setValue(InhalerUtils.singleSelect(g));

		return g;
	}

	/*
	 * Update categories
	 */

	private void editLocationUpdateClick() {
		if (selectedLocation != null) {
			editLocationUpdate();
		}
	}

	private void editStatusUpdateClick() {
		if (selectedStatus != null) {
			editStatusUpdate();
		}
	}

	private void editGroupUpdateClick() {
		if (selectedGroup != null) {
			//TODO
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
			Debugging.output("Close Location: " + l, Debugging.CATEGORY_EDITOR_DEBUG);
		}

		//update the database
		udh.store(selectedLocation, Location.class);

	}

	public void editStatusUpdate() {
		UserDataHolder udh = masterUi.userDataHolder;
		int color = editStatusColorPicker.getValue().getRGB();
		selectedStatus.setColor(color);

		udh.store(selectedStatus, Status.class);

	}

	/*
	 * Populate data
	 */

	//Populate Locations

	/**
	 * updates all location comboboxes. Useful when adding new locations or
	 * loading the page. USE CAUTION with this method, and probably don't call
	 * this if something is selected, instead update only the necessary boxes or
	 * only add items that are not currently in the box (with out changing
	 * selection)
	 */
	public void populateAllLocationBoxes() {
		//Get the current User Data Holder
		UserDataHolder udh = masterUi.userDataHolder;

		newLocationAllLocations.setItems(udh.getAllLocations());

		editLocationSelectionBox.setItems(udh.getAllLocations());

		editLocationProximitySelect.setItems(udh.getAllLocations());
	}

	private void populateAllStatusBoxes() {
		UserDataHolder udh = masterUi.userDataHolder;

		newStatusAllStatus.setItems(udh.getAllStatus());

		editStatusSelectionBox.setItems(udh.getAllStatus());

	}

	public void populateAllGroupBoxes() {
		UserDataHolder udh = masterUi.userDataHolder;

		newGroupAllGroups.setItems(udh.getAllGroups());

		editGroupSelectionBox.setItems(udh.getAllGroups());

	}

	private void loadProximity(Location l) {
		//editLocationProximitySelect.removeAllItems();
		ArrayList<Location> allLocations = new ArrayList<Location>();
		allLocations.addAll(masterUi.userDataHolder.getAllLocations());
		//Must remove own location from combo box items
		allLocations.remove(l);
		editLocationProximitySelect.setItems(allLocations);

		
		//editLocationProximitySelect.removeItem(l);
		//then add all the locations to the "proximity side" if they are a proximity location
		for (Location closeLocation : l.getRealCloseLocations()) {
			editLocationProximitySelect.select(closeLocation);
			Debugging.output("Close Location loaded: " + closeLocation, Debugging.CATEGORY_EDITOR_DEBUG);
		}
	}

	/*
	 * Select Items
	 */

	private void selectLocation() {
		Debugging.output("Selected Location: " + editLocationSelectionBox.getValue(), Debugging.CATEGORY_EDITOR_DEBUG);

		if (editLocationSelectionBox.getValue() == null) {
			//null selection
			return;
		}

		String locationName = editLocationSelectionBox.getValue().toString();

		if (locationName != null) {
			selectEditLocation(locationName);
		} else {
			//NULL SELECTION!
		}
	}

	private void selectStatus() {
		Debugging.output("Selected Status: " + editStatusSelectionBox.getValue(), Debugging.CATEGORY_EDITOR_DEBUG);

		if (editStatusSelectionBox.getValue() == null) {
			//null selection
			return;
		}

		String statusName = editStatusSelectionBox.getValue().toString();

		if (statusName != null) {
			selectEditStatus(statusName);
		} else {
			//NULL SELECTION!
		}
	}

	private void selectGroup() {
		Debugging.output("Selected Groups: " + editGroupSelectionBox.getValue(), Debugging.CATEGORY_EDITOR_DEBUG);

		if (editGroupSelectionBox.getValue() == null) {
			//null selection
			return;
		}

		String groupName = editGroupSelectionBox.getValue().toString();

		if (groupName != null) {
			selectEditGroup(groupName);
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

	public void selectEditStatus(String statusName) {
		UserDataHolder udh = masterUi.userDataHolder;

		editLocationsSelectedLabel.setValue("Editing Location: " + statusName);
		//Find the location in the user data holder

		Status s = udh.getStatus(statusName);

		loadStatus(s);

		selectedStatus = s;
	}

	private void selectEditGroup(String groupName) {
		UserDataHolder udh = masterUi.userDataHolder;

		editLocationsSelectedLabel.setValue("Editing Location: " + groupName);
		//Find the location in the user data holder

		Group g = udh.getGroup(groupName);

		g.setUserDataHolder(udh);

		loadGroup(g);

		selectedGroup = g;
	}

	private void loadGroup(Group g) {
		// TODO Auto-generated method stub

	}

	private void loadStatus(Status s) {
		Debugging.output("setting status values: " + s.getStatusName() + " color: " + s.getJavaColor(),
				Debugging.CATEGORY_EDITOR_DEBUG);

		editStatusSelectedLabel.setValue(s.getStatusName());

		editStatusColorPicker.setValue(s.getJavaColor());
	}
	
	
	private void editGroupDeleteClick() {
		Group g = this.selectedGroup;
		if (g != null) {
			//test to see if any client has this group. if a client is a member of this group, throw an error.
			if (this.masterUi.userDataHolder.checkClientsFor(g)) {
				//Throw error
				Notification n = new Notification("Can't delete group : " + g.getGroupName() + " Because it exists in client(s)");
				n.show(UI.getCurrent().getPage());
			} else {
				//delete group
				this.masterUi.userDataHolder.delete(g, Group.class);
				
				Notification n = new Notification("Deleted Group : " + g.getGroupName() );
				n.show(UI.getCurrent().getPage());
				
				editGroupSelectionBox.clear();
				
				populateAllLocationBoxes();
				
			}
			
		}
	}

	private void editStatusDeleteClick() {
		Status s = this.selectedStatus;
		if (s != null) {
			//test to see if any client has this group. if a client is a member of this group, throw an error.
			if (this.masterUi.userDataHolder.checkClientsFor(s)) {
				//Throw error
				Notification n = new Notification("Can't delete status : " + s.getPrimaryKey() + " Because it exists in client(s)");
				n.show(UI.getCurrent().getPage());
			} else {
				//delete group
				this.masterUi.userDataHolder.delete(s, Status.class);
				
				Notification n = new Notification("Deleted Status : " + s.getPrimaryKey() );
				n.show(UI.getCurrent().getPage());
				
				editStatusSelectionBox.clear();
				
				populateAllLocationBoxes();
				
			}
			
		}
	}

	private void editLocationDeleteClick() {
		Location l = this.selectedLocation;
		if (l != null) {
			//test to see if any client has this group. if a client is a member of this group, throw an error.
			if (this.masterUi.userDataHolder.checkClientsFor(l)) {
				//Throw error
				Notification n = new Notification("Can't delete location : " + l.getPrimaryKey() + " Because it exists in client(s)");
				n.show(UI.getCurrent().getPage());
			} else {
				//delete group
				this.masterUi.userDataHolder.delete(l, Location.class);
				
				Notification n = new Notification("Deleted Location : " + l.getPrimaryKey() );
				n.show(UI.getCurrent().getPage());
				
				editLocationSelectionBox.clear();
				
				populateAllLocationBoxes();
				
			}
			
		}
	}

}
