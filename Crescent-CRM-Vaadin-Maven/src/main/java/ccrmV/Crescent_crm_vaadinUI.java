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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import com.vaadin.ui.*;
import clientInfo.*;
import dbUtils.BackupManager;
import dbUtils.MaxObject;
//import de.steinwedel.messagebox.MessageBox;
import uiElements.NavBar;

@SuppressWarnings("serial")
@Theme("crescent_crm_vaadin")
public class Crescent_crm_vaadinUI extends VerticalLayout implements View {

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
	TextField createLocationName, createStatusName, createGroupName, createClientName;
	ComboBox createClientStatus, createClientLocation, createClientGroup, csvBackupSelect;
	Button csvBackupRestoreButton, csvBackupDownloadButton, csvBackupUploadButton, csvBackupNowButton;

	// Current Client Editing
	TextArea clientNoteBox;
	ComboBox clientStatus, clientLocation, clientGroup;
	Button clientUpdateButton, clientArchiveButton;
	Label clientNameLabel, clientLastUpdate;

	// filtering
	GridLayout filterLayout = new GridLayout();
	ComboBox filterStatus, filterLocation, filterGroup;
	Button filterButton, resetFilterButton;
	TextField filterClientTextField, filterClientNotesField;
	Label filterLabel = new Label("Filter :");

	boolean alreadyGenerated = false;
	
	
	public Table clientTable = new Table();

	//nav bar
	public NavBar navBar;
	
	VerticalLayout linkLayout = new VerticalLayout();

	Link devGitHub = new Link("GitHub", new ExternalResource("https://github.com/90301/Crescent-CRM-V/issues"));
	Link devAsciiArt = new Link("Ascii Art",
			new ExternalResource("http://patorjk.com/software/taag/#p=display&c=c&f=Letters"));

	Label versionLabel;

	public static final double versionNumber = .62;
	public static final String versionDescription = " main user login";
	private static final int MAX_NOTE_ROWS = 20;

	public Client selectedClient;
	Boolean discard = false;
	private boolean unsavedProgress = false;
	private Client localSelClient = null;

	// holds all possible values that mean null.
	HashSet<String> nullStrings = new HashSet<String>();
	public MasterUI masterUi;

	/*
	 * Draw UI elements TODO: transition more elements to their own methods
	 * TODO: add an update all for anything that takes dynamic data
	 */

	/*
	 * @WebServlet(value = "/*", asyncSupported = true)
	 * 
	 * @VaadinServletConfiguration(productionMode = false, ui =
	 * Crescent_crm_vaadinUI.class)
	 * 
	 * public static class Servlet extends VaadinServlet {
	 * 
	 * }
	 */

	private void backupNowClick() {
		// TODO Auto-generated method stub
		DataHolder.backupAllCsv();
	}

	private void restoreClick() {

		String csvFileString = csvBackupSelect.getValue().toString();
		if (stringNullCheck(csvFileString)) {
			return;
		} else {
			DataHolder.restoreFromBackup(csvFileString);
		}
	}

	private void updateClient(ClickEvent event) {
		// TODO Auto-generated method stub

		// UPDATE fields in client

		selectedClient.setNotes(clientNoteBox.getValue());

		// Resolve field

		// Template code
		if (selectedClient.getName().contains(DataHolder.TEMPLATE_STRING)) {

			Group tGroup = DataHolder.getGroup(DataHolder.TEMPLATE_STRING);
			Location tLocation = DataHolder.getLocation(DataHolder.TEMPLATE_STRING);
			Status tStatus = DataHolder.getStatus(DataHolder.TEMPLATE_STRING);
			selectedClient.setGroup(tGroup);
			selectedClient.setLocation(tLocation);
			selectedClient.setStatus(tStatus);

		} else {
			// normal client creation
			// if valid, set the field
			Group cGroup = DataHolder.getGroup((String) clientGroup.getValue());
			if (cGroup != null) {
				selectedClient.setGroup(cGroup);
			}
			// Resolve field
			Location cLocation = DataHolder.getLocation((String) clientLocation.getValue());
			// if valid, set the field
			if (cLocation != null) {
				selectedClient.setLocation(cLocation);
			}
			// Resolve field
			Status cStatus = DataHolder.getStatus((String) clientStatus.getValue());
			// if valid, set the field
			if (cStatus != null) {
				selectedClient.setStatus(cStatus);
			}

		}
		selectedClient.setLastUpdatedToNow();

		DataHolder.store(selectedClient, Client.class);
		updateClientTable();
	}

	public void updateClientTable() {
		// clientTable.clear();
		clientTable.addContainerProperty("Name", String.class, "<no name>");
		clientTable.addContainerProperty("Location", String.class, null);
		clientTable.addContainerProperty("Status", String.class, null);
		clientTable.addContainerProperty("Groups", String.class, "<no group>");

		// set up filter comparison objects
		Status filterStatusTest = null;
		if (filterStatus.getValue() != null)
			filterStatusTest = DataHolder.getStatus(filterStatus.getValue().toString());
		Location filterLocationTest = null;
		if (filterLocation.getValue() != null)
			filterLocationTest = DataHolder.getLocation(filterLocation.getValue().toString());
		Group filterGroupTest = null;
		if (filterGroup.getValue() != null)
			filterGroupTest = DataHolder.getGroup(filterGroup.getValue().toString());

		String filterNameTest = null;
		if (filterClientTextField.getValue() != null && !stringNullCheck(filterClientTextField.getValue()))
			filterNameTest = filterClientTextField.getValue().toString();
		String[] filterNotesTests = null;
		if (filterClientNotesField.getValue() != null && !stringNullCheck(filterClientNotesField.getValue()))
			filterNotesTests = filterClientNotesField.getValue().toLowerCase().split("\\s+");

		System.out.println("Looking for: " + filterNameTest);

		for (Client c : DataHolder.getAllClients()) {

			if (clientTable.containsId(c.getPrimaryKey())) {
				// remove old item
				clientTable.removeItem(c.getPrimaryKey());
			}

			// filter settings

			if ((filterStatusTest == c.getStatus() || filterStatusTest == null)
					&& (filterLocationTest == c.getLocation() || filterLocationTest == null)
					&& (filterGroupTest == c.getGroup() || filterGroupTest == null)
					&& (filterNameTest == null || c.getName().toLowerCase().contains(filterNameTest.toLowerCase()))) {

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

		csvBackupSelect.clear();

		// create clients
		fillComboBox(createClientStatus, DataHolder.getAllStatus());
		fillComboBox(createClientLocation, DataHolder.getAllLocations());
		fillComboBox(createClientGroup, DataHolder.getAllGroups());

		// Client editor
		fillComboBox(clientStatus, DataHolder.getAllStatus());
		fillComboBox(clientLocation, DataHolder.getAllLocations());
		fillComboBox(clientGroup, DataHolder.getAllGroups());

		// filter
		fillComboBox(filterStatus, DataHolder.getAllStatus());
		fillComboBox(filterLocation, DataHolder.getAllLocations());
		fillComboBox(filterGroup, DataHolder.getAllGroups());

		fillComboBox(csvBackupSelect, BackupManager.getCsvBackups());
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
		if (clientTable.getValue() != null && !stringNullCheck((String) clientTable.getValue())) {
			localSelClient = DataHolder.getClient((String) clientTable.getValue());
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
		clientNoteBox.setRows(Math.min(c.getNotes().split("\\r?\\n").length+2,MAX_NOTE_ROWS));
		// set last updated
		if (c.getLastUpdated() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("MMMM-dd-yy h:mm a");
			Date resultdate = c.getLastUpdated();

			clientLastUpdate.setValue(sdf.format(resultdate));
		} else {
			clientLastUpdate.setValue("Never updated");
		}

	}

	public void createLocationClick() {
		String text = this.createLocationName.getValue();
		String nullValue = this.createLocationName.getNullRepresentation();
		// Check for valid input
		if (text != nullValue && text.compareTo("") != 0) {
			Location location = new Location();
			location.setLocationName(text);
			DataHolder.store(location, Location.class);
		}
		fillAllComboBoxes();
	}

	public void createStatusClick() {
		String text = this.createStatusName.getValue();
		String nullValue = this.createStatusName.getNullRepresentation();
		// Check for valid input
		if (text != nullValue && text.compareTo("") != 0) {
			Status s = new Status();
			s.setStatusName(text);
			DataHolder.store(s, Status.class);
		}
		fillAllComboBoxes();
	}

	public void createGroupClick() {
		String text = this.createGroupName.getValue();
		String nullValue = this.createGroupName.getNullRepresentation();
		// Check for valid input
		if (text != nullValue && text.compareTo("") != 0) {
			Group g = new Group();
			g.setGroupName(text);
			DataHolder.store(g, Group.class);
		}
		fillAllComboBoxes();
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

		String name = createClientName.getValue();
		// Null checking
		if (stringNullCheck(name)) {
			return;// Name is null
		}
		// The group is ok to be null.
		// Location is a required field
		// Status is a required field
		try {
			if (stringNullCheck(createClientLocation.getValue().toString())) {
				return;
			}
			if (stringNullCheck(createClientStatus.getValue().toString())) {
				return;
			}
			if (stringNullCheck(createClientGroup.getValue().toString())) {
				return;
			}
		} catch (NullPointerException e) {
			System.err.println("Null value was entered: " + e.getMessage());
			return; // a null value was found
		}

		Client c = new Client();
		// make sure the location and status are valid
		Location l = DataHolder.getLocationMap().get(createClientLocation.getValue().toString());
		Status s = DataHolder.getStatusMap().get(createClientStatus.getValue().toString());
		Group g = DataHolder.getGroupMap().get(createClientGroup.getValue().toString());
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
		if (DataHolder.templateClient != null) {
			c.setNotes(DataHolder.templateClient.getNotes());
		} else {
			c.setNotes("Notes:");
		}

		System.out.println("Created Client: " + c);

		DataHolder.store(c, Client.class);
		updateClientTable();
	}

	/*
	 * UTILITIES
	 */

	/**
	 * Returns true if the string is null/blank or "null"
	 * 
	 * @param testingString
	 * @return true if null
	 */
	public static Boolean stringNullCheck(String testingString) {

		Boolean rtrn = false;
		if (testingString == null || testingString == "" || testingString == "null") {
			rtrn = true;
		}

		System.out.println("Tested: " + testingString + " Null?: " + rtrn);
		return rtrn;
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
	VerticalLayout layout = new VerticalLayout();
	TabSheet creationTabs = new TabSheet();
	HorizontalLayout createLocationLayout = new HorizontalLayout();
	HorizontalLayout createStatusLayout = new HorizontalLayout();
	HorizontalLayout createGroupLayout = new HorizontalLayout();
	HorizontalLayout createClientLayout = new HorizontalLayout();
	GridLayout optionsGridLayout = new GridLayout(4, 4);
	Button createClientButton;
	HorizontalLayout midLayout = new HorizontalLayout();
	GridLayout clientGridLayout = new GridLayout(4, 10);
	Button createLocationButton = new Button("Create Location", event -> this.createLocationClick());
	Button createStatusButton = new Button("Create Status", event -> this.createStatusClick());
	Button createGroupButton = new Button("Create Group", event -> this.createGroupClick());
	Panel panel = new Panel();
	@Override
	public void enter(ViewChangeEvent VCevent) {

		
		
		if (masterUi.loggedIn == false)
			return;
		
		/*
		 * This is a bug fix which removes all the layout information
		 * which means everything has to be regenerated when entering the URL again.
		 * 
		 * This is not desirable, but for some reason the nav bar doesn't appear if it's done a
		 * different way.
		 * -Josh Benton
		 */
		if (this.alreadyGenerated) {
			//layout.addComponent(navBar.sidebarLayout);
			//return;
			layout.removeAllComponents();
			creationTabs.removeAllComponents();
			createLocationLayout.removeAllComponents();
			createGroupLayout.removeAllComponents();
			createStatusLayout.removeAllComponents();
			createClientLayout.removeAllComponents();
			optionsGridLayout.removeAllComponents();
			clientGridLayout.removeAllComponents();
			filterLayout.removeAllComponents();
		}
			//return;
		// This may not need to run every time

		// but a better fix may be to just do a test, instead of running the
		// whole process.
		DataHolder.initalizeDatabases();

		// This is the top level layout (for now)
		// hopefully this can be changed later.

		

		// Nav Bar Code
		//NavBar navBar;
		layout.addComponent(navBar.sidebarLayout);

		layout.setMargin(true);
		
		panel.setContent(layout);
		panel.setSizeFull();
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
		
		layout.addComponent(creationTabs);

		// add a location
		
		createLocationName = new TextField("Location Name");
		createLocationLayout.addComponent(createLocationName);
		

		createLocationLayout.addComponent(createLocationButton);

		createLocationLayout.setComponentAlignment(createLocationButton, Alignment.BOTTOM_LEFT);

		creationTabs.addTab(createLocationLayout, "Add Location");

		// Add status
		
		createStatusName = new TextField("Status Name");


		createStatusLayout.addComponent(createStatusName);
		createStatusLayout.addComponent(createStatusButton);

		createStatusLayout.setComponentAlignment(createStatusButton, Alignment.BOTTOM_LEFT);

		creationTabs.addTab(createStatusLayout, "Add Status");

		// Add Group

		
		
		createGroupName = new TextField("Group Name");

		

		createGroupLayout.addComponent(createGroupName);
		createGroupLayout.addComponent(createGroupButton);

		createGroupLayout.setComponentAlignment(createGroupButton, Alignment.BOTTOM_LEFT);

		creationTabs.addTab(createGroupLayout, "Add Group");
		// Add a client

		
		createClientName = new TextField("Name");
		createClientLayout.addComponent(createClientName);
		createClientStatus = new ComboBox("Status");
		// Add all statuses

		createClientLayout.addComponent(createClientStatus);

		createClientLocation = new ComboBox("Location");
		// Add all locations

		createClientLayout.addComponent(createClientLocation);
		// Create groups
		createClientGroup = new ComboBox("Group");
		// Add all groups

		createClientLayout.addComponent(createClientGroup);

		createClientButton = new Button("Create Client", event -> this.createClientClick());
		// createClientButton.setSizeFull();
		createClientLayout.addComponent(createClientButton);

		createClientLayout.setComponentAlignment(createClientButton, Alignment.BOTTOM_LEFT);

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
		

		optionsGridLayout.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);

		creationTabs.addTab(optionsGridLayout, "Options");
		csvBackupSelect = new ComboBox("CSV Restore");

		optionsGridLayout.addComponent(csvBackupSelect, 0, 0);
		// adding buttons
		csvBackupRestoreButton = new Button("Restore", event -> this.restoreClick());
		csvBackupDownloadButton = new Button("Download Backup");
		csvBackupUploadButton = new Button("Upload Backup");
		csvBackupNowButton = new Button("Backup Now", event -> this.backupNowClick());

		optionsGridLayout.addComponent(csvBackupRestoreButton, 1, 0);

		optionsGridLayout.addComponent(csvBackupDownloadButton, 2, 0);

		optionsGridLayout.addComponent(csvBackupUploadButton, 3, 0);

		optionsGridLayout.addComponent(csvBackupNowButton, 1, 1);

		// dev links

		// optionsGridLayout.addComponent(devGitHub, 0, 3);
		// optionsGridLayout.addComponent(devAsciiArt, 1, 3);
		// optionsGridLayout.setComponentAlignment(devGitHub,
		// Alignment.TOP_LEFT);
		// optionsGridLayout.setComponentAlignment(devAsciiArt,
		// Alignment.TOP_LEFT);
		
		
		linkLayout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
		linkLayout.addComponent(devGitHub);
		linkLayout.addComponent(devAsciiArt);
		
		optionsGridLayout.addComponent(linkLayout, 0, 3);

		/***
		 * FFFFFFF IIIII LL TTTTTTT EEEEEEE RRRRRR FF III LL TTT EE RR RR FFFF
		 * III LL TTT EEEEE RRRRRR FF III LL TTT EE RR RR FF IIIII LLLLLLL TTT
		 * EEEEEEE RR RR
		 * 
		 */

		genFilterLayout();
		layout.addComponent(filterLayout);

		/***
		 * MM MM IIIII DDDDD LL AAA YY YY OOOOO UU UU TTTTTTT MMM MMM III DD DD
		 * LL AAAAA YY YY OO OO UU UU TTT MM MM MM III DD DD _____ LL AA AA
		 * YYYYY OO OO UU UU TTT MM MM III DD DD LL AAAAAAA YYY OO OO UU UU TTT
		 * MM MM IIIII DDDDDD LLLLLLL AA AA YYY OOOO0 UUUUU TTT
		 * 
		 */

		
		midLayout.setSpacing(true);
		layout.addComponent(midLayout);
		midLayout.addComponent(clientTable);
		clientTable.setSelectable(true);
		clientTable.setImmediate(true);
		clientTable.addValueChangeListener(event -> this.selectItem(event));

		updateClientTable();

		/***
		 * CCCCC LL IIIII EEEEEEE NN NN TTTTTTT CC C LL III EE NNN NN TTT CC LL
		 * III EEEEE NN N NN TTT CC C LL III EE NN NNN TTT CCCCC LLLLLLL IIIII
		 * EEEEEEE NN NN TTT
		 * 
		 * EEEEEEE DDDDD IIIII TTTTTTT IIIII NN NN GGGG EE DD DD III TTT III NNN
		 * NN GG GG EEEEE DD DD III TTT III NN N NN GG EE DD DD III TTT III NN
		 * NNN GG GG EEEEEEE DDDDDD IIIII TTT IIIII NN NN GGGGGG
		 */
		// CLIENT EDITING UI
		clientNameLabel = new Label("Client Name");
		clientNoteBox = new TextArea("Client Notes");
		clientNoteBox.setSizeFull();
		clientNoteBox.setResponsive(true);
		clientLocation = new ComboBox("Location");
		clientGroup = new ComboBox("Group");
		clientStatus = new ComboBox("Status");
		clientUpdateButton = new Button("Update", event -> this.updateClient(event));
		clientLastUpdate = new Label("Last Updated: --/--/----");
		clientArchiveButton = new Button("Archive");

		clientLastUpdate.setSizeFull();
		// client editing events

		
		// clientGridLayout.setWidth("600px");
		// clientGridLayout.setHeight("600px");
		// clientGridLayout.setSizeFull();
		clientGridLayout.setSpacing(false);
		clientGridLayout.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);

		midLayout.addComponent(clientGridLayout);
		clientGridLayout.addComponent(clientNameLabel, 0, 0);
		clientGridLayout.addComponent(clientLocation, 0, 1);
		clientGridLayout.addComponent(clientStatus, 1, 1);
		clientGridLayout.addComponent(clientGroup, 2, 1);
		clientGridLayout.addComponent(clientNoteBox, 0, 2, 3, 7);
		clientGridLayout.addComponent(clientLastUpdate, 0, 9);
		clientGridLayout.addComponent(clientUpdateButton, 1, 9);
		clientGridLayout.addComponent(clientArchiveButton, 2, 9);

		fillAllComboBoxes();

		// version label
		versionLabel = new Label("Version: " + versionNumber + versionDescription);

		layout.addComponent(versionLabel);
		
		this.alreadyGenerated = true;

	}

	/**
	 * Adds all the components related to the filter layout
	 */
	private void genFilterLayout() {
		filterLayout.setColumns(8);
		filterLayout.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
		filterLayout.addComponent(filterLabel, 0, 0);

		filterClientTextField = new TextField(" Name ");
		filterLayout.addComponent(filterClientTextField, 1, 0);
		filterStatus = new ComboBox("Status");
		filterLayout.addComponent(filterStatus, 2, 0);
		filterLocation = new ComboBox("Location");
		filterLayout.addComponent(filterLocation, 3, 0);
		filterGroup = new ComboBox("Group");
		filterLayout.addComponent(filterGroup, 4, 0);
		// filter notes
		filterClientNotesField = new TextField("Notes (seperate words by spaces)");
		filterLayout.addComponent(filterClientNotesField, 5, 0);

		filterButton = new Button("Filter", event -> this.filterClick());
		filterLayout.addComponent(filterButton, 6, 0);
		resetFilterButton = new Button("Reset", event -> this.resetFilterClick());
		filterLayout.addComponent(resetFilterButton, 7, 0);
	}

	private void resetFilterClick() {
		// TODO Auto-generated method stub
		filterStatus.setValue(null);
		filterLocation.setValue(null);
		filterGroup.setValue(null);
		filterClientTextField.setValue("");
		filterClientNotesField.setValue("");
		updateClientTable();
	}

	/**
	 * If not found, do not filter by the propriety
	 */
	private void filterClick() {
		updateClientTable();

	}

}