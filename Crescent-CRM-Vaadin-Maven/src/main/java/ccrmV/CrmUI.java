/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package ccrmV;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.*;
import clientInfo.*;
import dbUtils.BackupManager;
import dbUtils.InhalerUtils;
import dbUtils.MaxObject;
import debugging.Debugging;
import uiElements.ClientEditor;
//import de.steinwedel.messagebox.MessageBox;
import uiElements.NavBar;

@SuppressWarnings("serial")
@Theme("crescent_crm_vaadin")
public class CrmUI extends HorizontalLayout implements View {

	/*
	 * in order for databases to work, The driver must be added to the server.
	 * Example: /Glassfish4/glassfish/domains/domain1/lib
	 * 
	 * See also:
	 * 
	 * https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-usagenotes-
	 * glassfish-config.html
	 * 
	 * 
	 */
	/*
	 * UI elements (accessible)
	 */
	TextField createLocationName = new TextField();//"Location Name");
	TextField createStatusName  = new TextField();//"Status Name");
	TextField createGroupName = new TextField();//"Group Name");
	TextField createClientName = new TextField("Name");
	
	ComboBox createClientStatus = new ComboBox("Status");
	ComboBox createClientLocation = new ComboBox("Location");
	ComboBox createClientGroup = new ComboBox("Group");
	
	//ListBox is containing current statuses...
	ListSelect createLocationListSelect = new ListSelect();
	ListSelect createStatusListSelect = new ListSelect();
	ListSelect createGroupListSelect = new ListSelect();
	
	public static final int CREATE_LIST_SELECT_ROWS = 4;
 	private static final int MAX_NOTE_ROWS = 20;
	private static final String PANEL_HEIGHT = "950px";
	private static final String NOTE_WIDTH = "600px";
	private static final boolean PANNEL_ENABLED = false;
	private static final boolean DEFAULT_FILTER_SHOW = false;
	
	//Clears textboxes and combo boxes upon creating a client
	private static final boolean CLEAR_ON_CREATE = true;
	public static Boolean CREATION_ALLOW_NEW_VALUES = true;
	
	// filtering
	HorizontalLayout filterLayout = new HorizontalLayout();
	Button filterShowFilter = new Button(">>",e -> showFilterClick());
	Button filterHideFilter = new Button("<<", e -> hideFilterClick());
	Boolean filterShowing = false;
	
	ComboBox filterStatus  = new ComboBox("Status");
	ComboBox filterLocation = new ComboBox("Location");
	ComboBox filterGroup = new ComboBox("Group");
	Button filterButton = new Button("Filter", event -> this.filterClick());
	Button resetFilterButton = new Button("Reset", event -> this.resetFilterClick());;
	TextField filterClientTextField = new TextField(" Name ");
	TextField filterClientNotesField = new TextField("Notes Include:");
	Label filterLabel = new Label("Filter :");
	CheckBox filterContactNowCheckBox = new CheckBox("Contact Now Only");

	boolean alreadyGenerated = false;
	
	
	public Table clientTable = new Table();

	//nav bar
	public NavBar navBar;
	
	VerticalLayout linkLayout = new VerticalLayout();

	Link devGitHub = new Link("GitHub", new ExternalResource("https://github.com/90301/Crescent-CRM-V/issues"));
	Link devAsciiArt = new Link("Ascii Art",
			new ExternalResource("http://patorjk.com/software/taag/#p=display&c=c&f=Letters"));

	Label versionLabel = new Label("Version");



	


	public Client selectedClient;
	Boolean discard = false;
	private boolean unsavedProgress = false;
	private Client localSelClient = null;
	private String cacheDatabaseName = "";
	// holds all possible values that mean null.
	HashSet<String> nullStrings = new HashSet<String>();
	public MasterUI masterUi;

	
	Layout layout = new VerticalLayout();
	TabSheet creationTabs = new TabSheet();
	HorizontalLayout createLocationLayout = new HorizontalLayout();
	HorizontalLayout createStatusLayout = new HorizontalLayout();
	HorizontalLayout createGroupLayout = new HorizontalLayout();
	HorizontalLayout createClientLayout = new HorizontalLayout();
	//GridLayout optionsGridLayout = new GridLayout(4, 4);
	Button createClientButton = new Button("Create Client", event -> this.createClientClick());
	HorizontalLayout midLayout = new HorizontalLayout();
	//GridLayout clientGridLayout = new GridLayout(4, 10);
	//TODO
	Button createLocationButton = new Button("Create Location", event -> this.createLocationClick());
	Button createStatusButton = new Button("Create Status", event -> this.createStatusClick());
	Button createGroupButton = new Button("Create Group", event -> this.createGroupClick());
	Panel panel = new Panel();
	
	//Client Editing
	ClientEditor clientEditor = new ClientEditor(this);
	


	private void hideFilterClick() {
		setFilterShow(false);
	}

	private void showFilterClick() {
		setFilterShow(true);
	}

	public void setFilterShow(boolean showFilter) {
		this.filterShowing = showFilter;
		
		filterStatus.setVisible(showFilter);
		filterLocation.setVisible(showFilter);
		filterGroup.setVisible(showFilter);
		filterButton.setVisible(showFilter);
		resetFilterButton.setVisible(showFilter);
		filterClientTextField.setVisible(showFilter);
		filterClientNotesField.setVisible(showFilter);
		filterContactNowCheckBox.setVisible(showFilter);
		
		filterShowFilter.setVisible(!showFilter);
		filterHideFilter.setVisible(showFilter);
	}

	/**
	 * Updates the client table with the list of filtered clients.
	 */
	public void updateClientTable() {
		if (masterUi.userDataHolder.getDatabasePrefix()!=cacheDatabaseName) {
			//try clearing the table if the database changes?
		clientTable.clear();
		clientTable.removeAllItems();
		Debugging.output("Detected database change: " + masterUi.userDataHolder.getDatabasePrefix() + " old: " + cacheDatabaseName
				,Debugging.CRM_OUTPUT
				,Debugging.CRM_OUTPUT_ENABLED);
		
		
		cacheDatabaseName = masterUi.userDataHolder.getDatabasePrefix();
		
		} else {
			//force clear always!
			clientTable.clear();
			clientTable.removeAllItems();
		}
		clientTable.addContainerProperty("Name", String.class, "<no name>");
		clientTable.addContainerProperty("Location", String.class, null);
		clientTable.addContainerProperty("Status", String.class, null);
		clientTable.addContainerProperty("Groups", String.class, "<no group>");

		// set up filter comparison objects
		Status filterStatusTest = null;
		if (filterStatus.getValue() != null)
			filterStatusTest = masterUi.userDataHolder.getStatus(filterStatus.getValue().toString());
		Location filterLocationTest = null;
		if (filterLocation.getValue() != null)
			filterLocationTest = masterUi.userDataHolder.getLocation(filterLocation.getValue().toString());
		Group filterGroupTest = null;
		if (filterGroup.getValue() != null)
			filterGroupTest = masterUi.userDataHolder.getGroup(filterGroup.getValue().toString());

		String filterNameTest = null;
		if (filterClientTextField.getValue() != null && !InhalerUtils.stringNullCheck(filterClientTextField.getValue()))
			filterNameTest = filterClientTextField.getValue().toString();
		String[] filterNotesTests = null;
		if (filterClientNotesField.getValue() != null && !InhalerUtils.stringNullCheck(filterClientNotesField.getValue()))
			filterNotesTests = filterClientNotesField.getValue().toLowerCase().split("\\s+");

		System.out.println("Looking for: " + filterNameTest);

		Boolean contactNowOnly = filterContactNowCheckBox.getValue();
		
		for (Client c : masterUi.userDataHolder.getAllClients()) {

			if (clientTable.containsId(c.getPrimaryKey())) {
				// remove old item
				clientTable.removeItem(c.getPrimaryKey());
			}

			// filter settings

			if ((filterStatusTest == c.getStatus() || filterStatusTest == null)
					&& (filterLocationTest == c.getLocation() || filterLocationTest == null)
					&& (filterGroupTest == c.getGroup() || filterGroupTest == null)
					&& (filterNameTest == null || c.getName().toLowerCase().contains(filterNameTest.toLowerCase()))
					&& (!contactNowOnly || c.getContactNow())) {

				boolean noteQueryFound = true;
				// Make sure the notes contain all the terms
				if (filterNotesTests != null)
					for (String noteKeyword : filterNotesTests) {
						if (!c.getNotes().toLowerCase().contains(noteKeyword)) {
							noteQueryFound = false;
						}
					}
				if (noteQueryFound == true) {
					// add the new item
					clientTable.addItem(
							new Object[] { c.getName(), c.getLocationName(), c.getStatusName(), c.getGroupName() },
							c.getPrimaryKey());
				}
			}
		}

	}

	/**
	 * Fill combo boxes
	 */
	public void updateAllComboBoxes() {
		// clear all combo boxes
		createClientStatus.clear();
		createClientLocation.clear();
		createClientGroup.clear();

		filterStatus.clear();
		filterLocation.clear();
		filterGroup.clear();
		
		createClientStatus.removeAllItems();
		createClientLocation.removeAllItems();
		createClientGroup.removeAllItems();
		
		filterStatus.removeAllItems();
		filterLocation.removeAllItems();
		filterGroup.removeAllItems();

		//csvBackupSelect.clear();

		// create clients
		fillComboBox(createClientStatus, masterUi.userDataHolder.getAllStatus());
		fillComboBox(createClientLocation, masterUi.userDataHolder.getAllLocations());
		fillComboBox(createClientGroup, masterUi.userDataHolder.getAllGroups());

		// filter
		fillComboBox(filterStatus, masterUi.userDataHolder.getAllStatus());
		fillComboBox(filterLocation, masterUi.userDataHolder.getAllLocations());
		fillComboBox(filterGroup, masterUi.userDataHolder.getAllGroups());

		clientEditor.updateAllComboBoxes();
		
		//fillComboBox(csvBackupSelect, BackupManager.getCsvBackups());
	}

	public <T extends Object> void fillComboBox(ComboBox box, Collection<T> values) {
		for (Object val : values) {
			// add any non template entity. (unless template is selected)
			if (val != null && !val.toString().contains(DataHolder.TEMPLATE_STRING)) {
				box.addItem(val.toString());
			}
		}
	}

	// temp value to set the current client to

	/*
	 * C L I C K
	 * 
	 * E V E N T S
	 */

	/**
	 * Selection event for selecting a client in the client table
	 * (this method can be called with null selection safely)
	 * @param event
	 */
	public void selectItem(ValueChangeEvent event) {

		System.out.println("SELECTED AN ITEM." + clientTable.getValue());
		// TODO: ASK before switching

		// null check
		if (clientTable.getValue() != null && !InhalerUtils.stringNullCheck((String) clientTable.getValue())) {
			localSelClient = masterUi.userDataHolder.getClient((String) clientTable.getValue());
			if (localSelClient == null) {
				System.out.println(
						"Null value: " + localSelClient + " found for client: " + (String) clientTable.getValue());
				return;
			}

		} else {
			return;
		}
		if (unsavedProgress) {
			// TODO: implement unsaved progress
			this.discard = false;
			/*
			MessageBox switchQuestion = MessageBox.createQuestion().withCaption("Discard Changes?")
					.withMessage("You have unsaved information, are you sure you want to discard that?")
					.withOkButton(() -> this.selectClient(this.localSelClient)).withAbortButton();
			switchQuestion.open();
			*/
		} else {
			selectClient(localSelClient);
		}

	}

	/**
	 * Populates comboboxes/note fields ect when selecting a client from the table.
	 * @param c
	 */
	public void selectClient(Client c) {
		if (c != null) {

		} else {
			System.out.println("Null value made it to selectClient: " + c);
			clientEditor.setVisible(false);
			return;
		}
		clientEditor.setVisible(true);
		clientTable.select(c.getName());
		this.selectedClient = c;
		clientEditor.selectClient(c);


	}

	public void createLocationClick() {
		String text = this.createLocationName.getValue();
		createLocation(text);
	}

	public void createStatusClick() {
		String text = this.createStatusName.getValue();
		createStatus(text);
	}

	public void createGroupClick() {
		String text = this.createGroupName.getValue();
		createGroup(text);
		
	}
	
	
	//TODO MIGRATE these methods to user data holder
	
	
	public Status createStatus(String statusName) {
		Status s = null;
		// Check for valid input
		if (!InhalerUtils.stringNullCheck(statusName)) {
			s = new Status();
			s.setStatusName(statusName);
			masterUi.userDataHolder.store(s, Status.class);
		}
		updateAllComboBoxes();
		updateCreationLists();
		return s;
	}
	
	public Location createLocation(String locationName) {
		Location l = null;
		// Check for valid input
		if (!InhalerUtils.stringNullCheck(locationName)) {
			l = new Location();
			l.setLocationName(locationName);
			masterUi.userDataHolder.store(l, Location.class);
		}
		updateAllComboBoxes();
		updateCreationLists();
		return l;
	}
	
	public Group createGroup(String groupName) {
		Group g = null;
		// Check for valid input
		if (!InhalerUtils.stringNullCheck(groupName)) {
			g = new Group();
			g.setGroupName(groupName);
			masterUi.userDataHolder.store(g, Group.class);
		}
		updateAllComboBoxes();
		updateCreationLists();
		return g;
	}

	/*
	 * C L I E N T
	 * 
	 * C R E A T I O N
	 * 
	 */
	
	/**
	 * Click event to create a new client
	 */
	public void createClientClick() {
		// Null checking
		try {
			if (InhalerUtils.stringNullCheck(createClientLocation.getValue().toString())) {
				return;
			}
			if (InhalerUtils.stringNullCheck(createClientStatus.getValue().toString())) {
				return;
			}
			if (InhalerUtils.stringNullCheck(createClientGroup.getValue().toString())) {
				return;
			}
		} catch (NullPointerException e) {
			System.err.println("Null value was entered: " + e.getMessage());
			return; // a null value was found
		}
		
		//TODO: debugging
		String locationName = createClientLocation.getValue().toString();
		String statusName = createClientStatus.getValue().toString();
		String groupName =  createClientGroup.getValue().toString();
		//Create new values if one the value is not already selected.
		if (CREATION_ALLOW_NEW_VALUES) {
			//TODO implement something to check for similar names, maybe prompt user to select that value?
			
			
			Location l = masterUi.userDataHolder.getLocationMap().get(locationName);
			Status s = masterUi.userDataHolder.getStatusMap().get(statusName);
			Group g = masterUi.userDataHolder.getGroupMap().get(groupName);
			
			if (l==null) {
				createLocation(locationName);
			}
			
			if (s==null) {
				createStatus(statusName);
			}
			
			if (g==null) {
				createGroup(groupName);
			}
			
			
		}
		
		
		
		String name = createClientName.getValue();
		
		if (InhalerUtils.stringNullCheck(name)) {
			return;// Name is null
		}
		// The group is ok to be null.
		// Location is a required field
		// Status is a required field
		

		Client c = new Client();
		// make sure the location and status are valid
		Location l = masterUi.userDataHolder.getLocationMap().get(locationName);
		Status s = masterUi.userDataHolder.getStatusMap().get(statusName);
		Group g = masterUi.userDataHolder.getGroupMap().get(groupName);
		if (l != null && s != null && g != null) {
			c.setLocation(l);
			c.setStatus(s);
			c.setGroup(g);
		} else {
			return;// Either the location or status or group is null
		}

		c.setLastUpdatedToNow();

		c.setName(name);

		c.setId(Client.genId());
		// set notes to template client
		if (masterUi.userDataHolder.templateClient != null) {
			c.setNotes(masterUi.userDataHolder.templateClient.getNotes());
		} else {
			c.setNotes("Notes:");
		}

		System.out.println("Created Client: " + c);

		masterUi.userDataHolder.store(c, Client.class);
		updateClientTable();
		
		selectClient(c);
		
		if (CLEAR_ON_CREATE) {
			createClientLocation.clear();
			createClientStatus.clear();
			createClientGroup.clear();
			createClientName.clear();
			createClientName.setValue("");
		}
		
	}


	/**
	 * returns the object or null
	 * 
	 * @param key
	 * @param concurrentHashMap
	 * @return
	 */
	public static MaxObject findValid(String key, ConcurrentHashMap<String, MaxObject> concurrentHashMap) {
		MaxObject rtrn = concurrentHashMap.get(key);
		// if found, return the object
		return rtrn;
	}

	@Override
	public void enter(ViewChangeEvent VCevent) {

		//TODO START OF THE UI
		if (masterUi.loggedIn == false)
			masterUi.enterLogin();
		
		
		this.setSpacing(true);
		
		masterUi.userDataHolder.initalizeDatabases();

		// Nav Bar Code
		navBar.updateInfo();
		
		this.addComponent(navBar.sidebarLayout);
		

		((AbstractOrderedLayout) layout).setMargin(false);
		((AbstractOrderedLayout) layout).setSpacing(true);
		
		if (PANNEL_ENABLED) {
		panel.setContent(layout);
		//panel.setSizeFull();
		panel.setHeight(PANEL_HEIGHT);
		panel.getContent().setSizeUndefined();
		this.setSizeUndefined();
		// panel.setHeight("100%");
		// panel.setWidth("100%");
		layout.setSizeUndefined();
		// this.addStyleName("v-scrollable");
		// this.addStyleName("h-scrollable");
		// this.setHeight("100%");
		// this.setWidth("100%");
		this.addComponent(panel);
		} else {
			this.addComponent(layout);
		}
		// setContent(layout);

		/***
		 * T A B S
		 */

		// add a location
		
		//createLocationName 
	
		createLocationListSelect.setNullSelectionAllowed(false);
		createLocationListSelect.setRows(CREATE_LIST_SELECT_ROWS);
		
		createLocationLayout.addComponent(createLocationName);
		createLocationLayout.addComponent(createLocationButton);
		createLocationLayout.addComponent(createLocationListSelect);
		
		createLocationLayout.setComponentAlignment(createLocationButton, Alignment.TOP_CENTER);
		createLocationLayout.setMargin(true);
		createLocationLayout.setSpacing(true);
		
		creationTabs.addTab(createLocationLayout, "Add Location");

		// Add status

		createStatusListSelect.setNullSelectionAllowed(false);
		createStatusListSelect.setRows(CREATE_LIST_SELECT_ROWS);
		
		createStatusLayout.addComponent(createStatusName);
		createStatusLayout.addComponent(createStatusButton);
		createStatusLayout.addComponent(createStatusListSelect);
		
		createStatusLayout.setComponentAlignment(createStatusButton, Alignment.TOP_CENTER);
		createStatusLayout.setMargin(true);
		createStatusLayout.setSpacing(true);
		
		creationTabs.addTab(createStatusLayout, "Add Status");
		
		// Add Group

		//createGroupName 
		createGroupListSelect.setNullSelectionAllowed(false);
		createGroupListSelect.setRows(CREATE_LIST_SELECT_ROWS);

		createGroupLayout.addComponent(createGroupName);
		createGroupLayout.addComponent(createGroupButton);
		createGroupLayout.addComponent(createGroupListSelect);
		createGroupLayout.setComponentAlignment(createGroupButton, Alignment.TOP_CENTER);
		createGroupLayout.setMargin(true);
		createGroupLayout.setSpacing(true);
		
		
		creationTabs.addTab(createGroupLayout, "Add Group");
		// Add a client
		
		if (CREATION_ALLOW_NEW_VALUES) {
			Boolean invalidAllow = true;
			Boolean textInputAllow = true;
			Boolean nullSelectionAllow = false;
			Boolean newItemsAllowed = true;
			
			
			createClientStatus.setInvalidAllowed(invalidAllow);
			createClientLocation.setInvalidAllowed(invalidAllow);
			createClientGroup.setInvalidAllowed(invalidAllow);
			
			createClientStatus.setTextInputAllowed(textInputAllow);
			createClientStatus.setNewItemsAllowed(newItemsAllowed);
			createClientStatus.setNullSelectionAllowed(nullSelectionAllow);
			
			
			createClientLocation.setTextInputAllowed(textInputAllow);
			createClientLocation.setNewItemsAllowed(newItemsAllowed);
			createClientLocation.setNullSelectionAllowed(nullSelectionAllow);
			
			createClientGroup.setTextInputAllowed(textInputAllow);
			createClientGroup.setNewItemsAllowed(newItemsAllowed);
			createClientGroup.setNullSelectionAllowed(nullSelectionAllow);
			
		} else {
			createClientStatus.setInvalidAllowed(false);
			createClientLocation.setInvalidAllowed(false);
			createClientGroup.setInvalidAllowed(false);
			
			createClientStatus.setNullSelectionAllowed(false);
			createClientLocation.setNullSelectionAllowed(false);
			createClientGroup.setNullSelectionAllowed(false);
		}
		
		
		//createClientName 
		createClientLayout.addComponent(createClientName);

		createClientLayout.addComponent(createClientStatus);
		
		createClientLayout.addComponent(createClientLocation);
		// Create groups
		//createClientGroup 
		
		
		// Add all groups

		createClientLayout.addComponent(createClientGroup);

		//createClientButton
		createClientLayout.addComponent(createClientButton);

		createClientLayout.setComponentAlignment(createClientButton, Alignment.BOTTOM_LEFT);
		createClientLayout.setSpacing(true);
		

		creationTabs.addTab(createClientLayout, "Add Client");

		// add the client tab
		// END CLIENT
		/***
		 *O P T I O N S
		 */
		// options menu
		// OPTIONS TAB---------------------------------------------------------
		

		//optionsGridLayout.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);

		//
		//csvBackupSelect
		creationTabs.addTab(linkLayout, "Links");
		
		linkLayout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
		linkLayout.addComponent(devGitHub);
		linkLayout.addComponent(devAsciiArt);
		
		//optionsGridLayout.addComponent(linkLayout, 0, 3);

		
		//layout.addComponent(creationTabs);
		
		/***
		 * 
		 * F I L T E R
		 * 
		 */

		genFilterLayout();
		layout.addComponent(filterLayout);

		/***
		 * M I D _ L A Y O U T
		 * 
		 */

		
		midLayout.setSpacing(true);
		layout.addComponent(midLayout);
		midLayout.addComponent(clientTable);
		clientTable.setSelectable(true);
		clientTable.setImmediate(true);
		clientTable.addValueChangeListener(event -> this.selectItem(event));

		updateClientTable();

		//Populating all Status, Location, and Group Lists
		updateCreationLists();
		
		/***
		 * C L I E N T
		 * E D I T I N G
		 */
		// CLIENT EDITING UI
		clientEditor.setVisible(false);
		
		midLayout.addComponent(clientEditor);

		updateAllComboBoxes();
		
		//Adding creation tab
		layout.addComponent(creationTabs);
		
		// version label
		versionLabel.setValue("Version: " + MasterUI.versionNumber + MasterUI.versionDescription);
		
		layout.addComponent(versionLabel);
		
		this.alreadyGenerated = true;
		

	}
	


	public void updateCreationLists() {
		// TODO Auto-generated method stub
		createStatusListSelect.removeAllItems();
		createStatusListSelect.addItems(masterUi.userDataHolder.getAllStatus());
		createLocationListSelect.removeAllItems();
		createLocationListSelect.addItems(masterUi.userDataHolder.getAllLocations());
		createGroupListSelect.removeAllItems();
		createGroupListSelect.addItems(masterUi.userDataHolder.getAllGroups());
	}



	/**
	 * Adds all the components related to the filter layout
	 */
	private void genFilterLayout() {
		
		filterLayout.setSpacing(true);
		filterLayout.setMargin(false);
		//filterLayout.addStyleName("filterBorder");
		//filterLayout.setColumns(8);
		filterLayout.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
		filterLayout.addComponent(filterLabel);
		
		filterClientTextField.addValueChangeListener(e -> updateClientTable());
		filterStatus.addValueChangeListener(e -> updateClientTable());
		filterLocation.addValueChangeListener(e -> updateClientTable());
		filterGroup.addValueChangeListener(e -> updateClientTable());
		filterClientNotesField.addValueChangeListener(e -> updateClientTable());
		filterContactNowCheckBox.addValueChangeListener(e -> updateClientTable());

		//filterClientTextField 
		filterLayout.addComponent(filterShowFilter);
		
		filterLayout.addComponent(filterClientTextField);
		//filterStatus
		filterLayout.addComponent(filterStatus);
		//filterLocation 
		filterLayout.addComponent(filterLocation);
		//filterGroup 
		filterLayout.addComponent(filterGroup);
		// filter notes

		filterLayout.addComponent(filterClientNotesField);
		
		filterLayout.addComponent(filterContactNowCheckBox);

		//filterButton
		filterLayout.addComponent(filterButton);
		//resetFilterButton 
		filterLayout.addComponent(resetFilterButton);
		
		filterLayout.addComponent(filterHideFilter);
		
		setFilterShow(DEFAULT_FILTER_SHOW);
		
	}

	private void resetFilterClick() {
		// TODO Auto-generated method stub
		filterStatus.setValue(null);
		filterLocation.setValue(null);
		filterGroup.setValue(null);
		filterClientTextField.setValue("");
		filterClientNotesField.setValue("");
		filterContactNowCheckBox.setValue(false);
		updateClientTable();
	}

	/**
	 * If not found, do not filter by the propriety
	 */
	private void filterClick() {
		updateClientTable();

	}

}