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
//import de.steinwedel.messagebox.MessageBox;
import uiElements.NavBar;
import uiElements.TemplateEditor;

@SuppressWarnings("serial")
@Theme("crescent_crm_vaadin")
public class Crescent_crm_vaadinUI extends HorizontalLayout implements View {

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
	
	// TODO
	// Add textfield and label only to the vertical layouts
	TextField createLocationName = new TextField();
	TextField createStatusName = new TextField();
	TextField createGroupName = new TextField();
	TextField createClientName = new TextField("Name");
	VerticalLayout addLocations = new VerticalLayout();
	VerticalLayout addStatuses = new VerticalLayout();
	VerticalLayout addGroups = new VerticalLayout();

	ComboBox createClientStatus = new ComboBox("Status");
	ComboBox createClientLocation = new ComboBox("Location");
	ComboBox createClientGroup = new ComboBox("Group");

	TemplateEditor Template = new TemplateEditor();

	// ListBoxs containing current statuses, locations, groups
	ListSelect createLocationListSelect = new ListSelect();
	ListSelect createStatusListSelect = new ListSelect();
	ListSelect createGroupListSelect = new ListSelect();

	// Current Client Editing
	TextArea clientNoteBox = new TextArea("Client Notes");
	ComboBox clientStatus = new ComboBox("Status");
	ComboBox clientLocation = new ComboBox("Location");;
	ComboBox clientGroup = new ComboBox("Group");;
	Button clientUpdateButton = new Button("Update", event -> this.updateClient(event));
	Button clientArchiveButton = new Button("Archive");
	Label clientNameLabel = new Label("Client Name");
	Label clientLastUpdate = new Label("Last Updated: --/--/----");
	CheckBox clientContactNowCheckBox = new CheckBox("Contact Now");
	ComboBox clientContactFrequency = new ComboBox("Contact Frequency");
	// TextField urlTextField = new TextField("URL: ");

	// filtering
	HorizontalLayout filterLayout = new HorizontalLayout();
	ComboBox filterStatus = new ComboBox("Status");
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

	// nav bar
	public NavBar navBar;

	VerticalLayout linkLayout = new VerticalLayout();

	Link devGitHub = new Link("GitHub", new ExternalResource("https://github.com/90301/Crescent-CRM-V/issues"));
	Link devAsciiArt = new Link("Ascii Art",
			new ExternalResource("http://patorjk.com/software/taag/#p=display&c=c&f=Letters"));

	Label versionLabel = new Label("Version");

	private static final int MAX_NOTE_ROWS = 20;
	private static final String PANEL_HEIGHT = "950px";
	private static final String NOTE_WIDTH = "600px";

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
	// GridLayout optionsGridLayout = new GridLayout(4, 4);
	Button createClientButton = new Button("Create Client", event -> this.createClientClick());
	HorizontalLayout midLayout = new HorizontalLayout();
	GridLayout clientGridLayout = new GridLayout(4, 10);
	// TODO
	Button createLocationButton = new Button("Create Location", event -> this.createLocationClick());
	Button createStatusButton = new Button("Create Status", event -> this.createStatusClick());
	Button createGroupButton = new Button("Create Group", event -> this.createGroupClick());
	Panel panel = new Panel();
	VerticalLayout clientEditorLayout = new VerticalLayout();
	HorizontalLayout clientEditorMetaLayout = new HorizontalLayout();
	HorizontalLayout clientEditorActionLayout = new HorizontalLayout();

	/**
	 * Updates the current client with the information entered
	 * Fires when update button is clicked.
	 * 
	 * @param event
	 */
	private void updateClient(ClickEvent event) {
		// TODO Auto-generated method stub

		// UPDATE fields in client
		
		selectedClient.setNotes(clientNoteBox.getValue());

		// Resolve field

		// Template code
		if (selectedClient.getName().contains(DataHolder.TEMPLATE_STRING)) {

			Group tGroup = masterUi.userDataHolder.getGroup(DataHolder.TEMPLATE_STRING);
			Location tLocation = masterUi.userDataHolder.getLocation(DataHolder.TEMPLATE_STRING);
			Status tStatus = masterUi.userDataHolder.getStatus(DataHolder.TEMPLATE_STRING);
			selectedClient.setGroup(tGroup);
			selectedClient.setLocation(tLocation);
			selectedClient.setStatus(tStatus);
			
			Template.updateTemplates(masterUi.userDataHolder);

		} else {
			// normal client creation
			// if valid, set the field
			Group cGroup = masterUi.userDataHolder.getGroup((String) clientGroup.getValue());
			if (cGroup != null) {
				selectedClient.setGroup(cGroup);
			}
			// Resolve field
			Location cLocation = masterUi.userDataHolder.getLocation((String) clientLocation.getValue());
			// if valid, set the field
			if (cLocation != null) {
				selectedClient.setLocation(cLocation);
			}
			// Resolve field
			Status cStatus = masterUi.userDataHolder.getStatus((String) clientStatus.getValue());
			// if valid, set the field
			if (cStatus != null) {
				selectedClient.setStatus(cStatus);
			}

		}
		selectedClient.setLastUpdatedToNow();

		selectedClient.setContactNow(clientContactNowCheckBox.getValue());
		masterUi.userDataHolder.store(selectedClient, Client.class);
		updateClientTable();
	}

	/**
	 * Updates the client table with the list of filtered clients.
	 */
	public void updateClientTable() {
		if (masterUi.userDataHolder.getDatabasePrefix() != cacheDatabaseName) {
			// try clearing the table if the database changes?
			clientTable.clear();
			clientTable.removeAllItems();
			Debugging.output("Detected database change: " + masterUi.userDataHolder.getDatabasePrefix() + " old: "
					+ cacheDatabaseName, Debugging.CRM_OUTPUT, Debugging.CRM_OUTPUT_ENABLED);

			cacheDatabaseName = masterUi.userDataHolder.getDatabasePrefix();

		} else {
			// force clear always!
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
		if (filterClientNotesField.getValue() != null
				&& !InhalerUtils.stringNullCheck(filterClientNotesField.getValue()))
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

	/*
	 * Fill combo boxes
	 */
	public void fillAllComboBoxes() {
		// clear all combo boxes
		createClientStatus.clear();
		createClientLocation.clear();
		createClientGroup.clear();

		clientStatus.clear();
		clientLocation.clear();
		clientGroup.clear();

		filterStatus.clear();
		filterLocation.clear();
		filterGroup.clear();

		// csvBackupSelect.clear();

		// create clients
		fillComboBox(createClientStatus, masterUi.userDataHolder.getAllStatus());
		fillComboBox(createClientLocation, masterUi.userDataHolder.getAllLocations());
		fillComboBox(createClientGroup, masterUi.userDataHolder.getAllGroups());

		// Client editor
		fillComboBox(clientStatus, masterUi.userDataHolder.getAllStatus());
		fillComboBox(clientLocation, masterUi.userDataHolder.getAllLocations());
		fillComboBox(clientGroup, masterUi.userDataHolder.getAllGroups());

		// filter
		fillComboBox(filterStatus, masterUi.userDataHolder.getAllStatus());
		fillComboBox(filterLocation, masterUi.userDataHolder.getAllLocations());
		fillComboBox(filterGroup, masterUi.userDataHolder.getAllGroups());

		// fillComboBox(csvBackupSelect, BackupManager.getCsvBackups());
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

	/***
	 * CCCCC LL IIIII CCCCC KK KK CC C LL III CC C KK KK CC LL III CC KKKK CC C
	 * LL III CC C KK KK CCCCC LLLLLLL IIIII CCCCC KK KK
	 * 
	 * EEEEEEE VV VV EEEEEEE NN NN TTTTTTT SSSSS EE VV VV EE NNN NN TTT SS EEEEE
	 * VV VV EEEEE NN N NN TTT SSSSS EE VV VV EE NN NNN TTT SS EEEEEEE VVV
	 * EEEEEEE NN NN TTT SSSSS
	 * 
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
			 * MessageBox switchQuestion =
			 * MessageBox.createQuestion().withCaption("Discard Changes?")
			 * .withMessage("You have unsaved information, are you sure you want to discard that?"
			 * ) .withOkButton(() ->
			 * this.selectClient(this.localSelClient)).withAbortButton();
			 * switchQuestion.open();
			 */
		} else {
			selectClient(localSelClient);
		}

	}

	
	/***
	 * This runs when you select a client.
	 * 
	 * @param c
	 */
	public void selectClient(Client c) {
		if (c != null) {

		} else {
			System.out.println("Null value made it to selectClient: " + c);
			return;
		}

		this.selectedClient = c;

		System.out.println("showing client information for: " + c);
		// TODO: load information into the ui.
		// LOAD INFORMATION
		clientNameLabel.setValue(c.getName());
		clientStatus.setValue(c.getStatusName());
		clientLocation.setValue(c.getLocationName());
		clientGroup.setValue(c.getGroupName());
		
		clientNoteBox.setValue(c.getNotes());
		clientNoteBox.setRows(Math.min(c.getNotes().split("\\r?\\n").length + 2, MAX_NOTE_ROWS));
		// set last updated
		if (c.getLastUpdated() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("MMMM-dd-yy h:mm a");
			Date resultdate = c.getLastUpdated();

			clientLastUpdate.setValue(sdf.format(resultdate));
		} else {
			clientLastUpdate.setValue("Never updated");
		}

		clientContactNowCheckBox.setValue(c.getContactNow());

		// When template client is selected the Template Editing should be
		// visible
		if (selectedClient.getName().contains(DataHolder.TEMPLATE_STRING)) {
			Template.setVisible(true);
			clientArchiveButton.setVisible(false);
		} else {
			Template.setVisible(false);
			clientArchiveButton.setVisible(true);
		}
	}

	public void createLocationClick() {
		String text = this.createLocationName.getValue();
		String nullValue = this.createLocationName.getNullRepresentation();
		// Check for valid input
		if (text != nullValue && text.compareTo("") != 0) {
			Location location = new Location();
			location.setLocationName(text);
			masterUi.userDataHolder.store(location, Location.class);
		}
		fillAllComboBoxes();
		updateCreationLists();
	}

	public void createStatusClick() {
		String text = this.createStatusName.getValue();
		String nullValue = this.createStatusName.getNullRepresentation();
		// Check for valid input
		if (text != nullValue && text.compareTo("") != 0) {
			Status s = new Status();
			s.setStatusName(text);
			masterUi.userDataHolder.store(s, Status.class);
		}
		fillAllComboBoxes();
		updateCreationLists();
	}

	public void createGroupClick() {
		String text = this.createGroupName.getValue();
		String nullValue = this.createGroupName.getNullRepresentation();
		// Check for valid input
		if (text != nullValue && text.compareTo("") != 0) {
			Group g = new Group();
			g.setGroupName(text);
			masterUi.userDataHolder.store(g, Group.class);
		}
		fillAllComboBoxes();
		updateCreationLists();
	}

	/***
	 * CCCCC LL IIIII EEEEEEE NN NN TTTTTTT CC C LL III EE NNN NN TTT CC LL III
	 * EEEEE NN N NN TTT CC C LL III EE NN NNN TTT CCCCC LLLLLLL IIIII EEEEEEE
	 * NN NN TTT
	 * 
	 * CCCCC RRRRRR EEEEEEE AAA TTTTTTT IIIII OOOOO NN NN CC C RR RR EE AAAAA
	 * TTT III OO OO NNN NN CC RRRRRR EEEEE AA AA TTT III OO OO NN N NN CC C RR
	 * RR EE AAAAAAA TTT III OO OO NN NNN CCCCC RR RR EEEEEEE AA AA TTT IIIII
	 * OOOO0 NN NN
	 * 
	 */
	public void createClientClick() {

		//Fucked up code
		/*
		String name = createClientName.getValue();
		// Null checking
		if (InhalerUtils.stringNullCheck(name)) {
			return;// Name is null
		}
		// The group is ok to be null.
		// Location is a required field
		// Status is a required field
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

		Client c = new Client();
		// make sure the location and status are valid
		Location l = masterUi.userDataHolder.getLocationMap().get(createClientLocation.getValue().toString());
		Status s = masterUi.userDataHolder.getStatusMap().get(createClientStatus.getValue().toString());
		Group g = masterUi.userDataHolder.getGroupMap().get(createClientGroup.getValue().toString());
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
		*/
		//correct code
		
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

		// TODO START OF THE UI
		if (masterUi.loggedIn == false)
			masterUi.enterLogin();

		/*
		 * This is a bug fix which removes all the layout information which
		 * means everything has to be regenerated when entering the URL again.
		 * 
		 * This is not desirable, but for some reason the nav bar doesn't appear
		 * if it's done a different way. (update) Think I Fixed it. -Josh Benton
		 */
		/*
		 * if (this.alreadyGenerated) {
		 * //layout.addComponent(navBar.sidebarLayout); //return;
		 * layout.removeAllComponents(); creationTabs.removeAllComponents();
		 * createLocationLayout.removeAllComponents();
		 * createGroupLayout.removeAllComponents();
		 * createStatusLayout.removeAllComponents();
		 * createClientLayout.removeAllComponents();
		 * optionsGridLayout.removeAllComponents();
		 * clientGridLayout.removeAllComponents();
		 * clientEditorMetaLayout.removeAllComponents();
		 * clientEditorLayout.removeAllComponents();
		 * clientEditorActionLayout.removeAllComponents();
		 * filterLayout.removeAllComponents(); }
		 */
		// return;
		// This may not need to run every time

		// but a better fix may be to just do a test, instead of running the
		// whole process.
		masterUi.userDataHolder.initalizeDatabases();

		// This is the top level layout (for now)
		// hopefully this can be changed later.

		// Nav Bar Code
		navBar.updateInfo();

		this.addComponent(navBar.sidebarLayout);

		((AbstractOrderedLayout) layout).setMargin(true);
		((AbstractOrderedLayout) layout).setSpacing(true);

		panel.setContent(layout);
		// panel.setSizeFull();
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
		// setContent(layout);

		/***
		 * T A B S
		 */

		// add a location

		// createLocationName

		createLocationListSelect.setNullSelectionAllowed(false);

		createLocationLayout.addComponent(createLocationName);
		createLocationLayout.addComponent(createLocationButton);
		createLocationLayout.addComponent(createLocationListSelect);

		createLocationLayout.setComponentAlignment(createLocationButton, Alignment.TOP_LEFT);
		createLocationLayout.setComponentAlignment(createLocationName, Alignment.TOP_LEFT);

		creationTabs.addTab(createLocationLayout, "Add Location");

		// Add status

		// createStatusName

		createStatusListSelect.setNullSelectionAllowed(false);

		createStatusLayout.addComponent(createStatusName);
		createStatusLayout.addComponent(createStatusButton);
		createStatusLayout.addComponent(createStatusListSelect);

		createStatusLayout.setComponentAlignment(createStatusButton, Alignment.TOP_LEFT);

		creationTabs.addTab(createStatusLayout, "Add Status");

		// Add Group

		// createGroupName
		createGroupListSelect.setNullSelectionAllowed(false);

		createGroupLayout.addComponent(createGroupName);
		createGroupLayout.addComponent(createGroupButton);
		createGroupLayout.addComponent(createGroupListSelect);
		createGroupLayout.setComponentAlignment(createGroupButton, Alignment.TOP_LEFT);

		creationTabs.addTab(createGroupLayout, "Add Group");
		// Add a client

		// createClientName
		createClientLayout.addComponent(createClientName);

		// createClientStatus
		createClientStatus.setNullSelectionAllowed(false);
		createClientStatus.setInvalidAllowed(false);
		// Add all statuses

		createClientLayout.addComponent(createClientStatus);

		// createClientLocation
		createClientLocation.setNullSelectionAllowed(false);
		createClientLocation.setInvalidAllowed(false);
		// Add all locations

		createClientLayout.addComponent(createClientLocation);
		// Create groups
		// createClientGroup
		createClientGroup.setNullSelectionAllowed(false);
		createClientGroup.setInvalidAllowed(false);
		// Add all groups

		createClientLayout.addComponent(createClientGroup);

		// createClientButton
		createClientLayout.addComponent(createClientButton);

		createClientLayout.setComponentAlignment(createClientButton, Alignment.BOTTOM_LEFT);
		createClientLayout.setSpacing(true);

		creationTabs.addTab(createClientLayout, "Add Client");

		// add the client tab
		// END CLIENT
		/***
		 * OOOOO PPPPPP TTTTTTT IIIII OOOOO NN NN SSSSS OO OO PP PP TTT III OO
		 * OO NNN NN SS OO OO PPPPPP TTT III OO OO NN N NN SSSSS OO OO PP TTT
		 * III OO OO NN NNN SS OOOO0 PP TTT IIIII OOOO0 NN NN SSSSS
		 */
		// options menu
		// OPTIONS TAB---------------------------------------------------------

		// optionsGridLayout.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);

		//
		// csvBackupSelect
		creationTabs.addTab(linkLayout, "Links");

		linkLayout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
		linkLayout.addComponent(devGitHub);
		linkLayout.addComponent(devAsciiArt);

		// optionsGridLayout.addComponent(linkLayout, 0, 3);

		// layout.addComponent(creationTabs);

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

		// Populating all Status, Location, and Group Lists
		updateCreationLists();

		/***
		 * C L I E N T E D I T I N G
		 */
		// CLIENT EDITING UI
		// clientNameLabel

		// clientNoteBox
		clientNoteBox.setSizeFull();
		clientNoteBox.setWidth(NOTE_WIDTH);
		clientNoteBox.setResponsive(true);

		// clientLocation
		clientLocation.setNullSelectionAllowed(false);
		clientLocation.setInvalidAllowed(false);

		// clientGroup
		clientGroup.setNullSelectionAllowed(false);
		clientGroup.setInvalidAllowed(false);

		// clientStatus
		clientStatus.setNullSelectionAllowed(false);
		clientStatus.setInvalidAllowed(false);

		// clientUpdateButton

		// clientLastUpdate

		// clientArchiveButton

		clientLastUpdate.setSizeFull();
		// client editing events

		// Client Editor V2

		genClientEditor();

		midLayout.addComponent(clientEditorLayout);

		fillAllComboBoxes();

		// Adding creation tab
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
	 * Adds all the components for the clientEditor
	 */
	private void genClientEditor() {
		// Editor for the client meta data (location status group)
		clientEditorMetaLayout.setSpacing(true);
		clientEditorMetaLayout.setSizeFull();

		clientEditorMetaLayout.addComponent(clientLocation);
		clientEditorMetaLayout.addComponent(clientStatus);
		clientEditorMetaLayout.addComponent(clientGroup);

		// Editor actions (archive update ect)
		clientEditorActionLayout.setSpacing(true);

		clientEditorActionLayout.addComponent(clientLastUpdate);
		clientEditorActionLayout.addComponent(clientUpdateButton);
		clientEditorActionLayout.addComponent(clientArchiveButton);
		clientEditorActionLayout.addComponent(clientContactNowCheckBox);
		clientEditorActionLayout.addComponent(clientContactFrequency);
		// clientEditorActionLayout.addComponent(urlTextField);

		// holds the client editor
		// TODO
		clientEditorLayout.setSpacing(true);
		Template.setVisible(false);
		
		clientEditorLayout.addComponent(clientNameLabel);
		clientEditorLayout.addComponent(clientEditorMetaLayout);
		clientEditorLayout.addComponent(Template);
		clientEditorLayout.addComponent(clientNoteBox);
		clientEditorLayout.addComponent(clientEditorActionLayout);
	}

	/**
	 * Adds all the components related to the filter layout
	 */
	private void genFilterLayout() {

		filterLayout.setSpacing(true);
		filterLayout.setMargin(false);
		// filterLayout.addStyleName("filterBorder");
		// filterLayout.setColumns(8);
		filterLayout.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
		filterLayout.addComponent(filterLabel);

		filterClientTextField.addValueChangeListener(e -> updateClientTable());
		filterStatus.addValueChangeListener(e -> updateClientTable());
		filterLocation.addValueChangeListener(e -> updateClientTable());
		filterGroup.addValueChangeListener(e -> updateClientTable());
		filterClientNotesField.addValueChangeListener(e -> updateClientTable());
		filterContactNowCheckBox.addValueChangeListener(e -> updateClientTable());

		// filterClientTextField

		filterLayout.addComponent(filterClientTextField);
		// filterStatus
		filterLayout.addComponent(filterStatus);
		// filterLocation
		filterLayout.addComponent(filterLocation);
		// filterGroup
		filterLayout.addComponent(filterGroup);
		// filter notes

		filterLayout.addComponent(filterClientNotesField);

		filterLayout.addComponent(filterContactNowCheckBox);

		// filterButton
		filterLayout.addComponent(filterButton);
		// resetFilterButton
		filterLayout.addComponent(resetFilterButton);
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