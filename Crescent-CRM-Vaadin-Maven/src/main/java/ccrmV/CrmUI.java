/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package ccrmV;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.*;
import clientInfo.*;
import dbUtils.InhalerUtils;
import dbUtils.MaxObject;
import debugging.Debugging;
import debugging.profiling.ProfilingTimer;
import debugging.profiling.RapidProfilingTimer;
import uiElements.ClientEditor;
import uiElements.ClientFilter;
//import de.steinwedel.messagebox.MessageBox;
import uiElements.NavBar;
import users.User;

@SuppressWarnings("serial")
@Theme("crescent_crm_vaadin")
public class CrmUI extends CrescentView {

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
	public TextField createLocationName = new TextField();//"Location Name");
	public TextField createStatusName  = new TextField();//"Status Name");
	public TextField createGroupName = new TextField();//"Group Name");
	public TextField createClientName = new TextField("Name");
	
	public ComboBox<Status> createClientStatus = new ComboBox<Status>("Status");
	public ComboBox<Location> createClientLocation = new ComboBox<Location>("Location");
	public ComboBox<Group> createClientGroup = new ComboBox<Group>("Group");
	
	//ListBox is containing current statuses...
	ListSelect<Location> createLocationListSelect = new ListSelect<Location>();
	ListSelect<Status> createStatusListSelect = new ListSelect<Status>();
	ListSelect<Group> createGroupListSelect = new ListSelect<Group>();
	
	public static final int CREATE_LIST_SELECT_ROWS = 4;
 	//private static final int MAX_NOTE_ROWS = 20;
	private static final String PANEL_HEIGHT = "950px";
	//private static final String NOTE_WIDTH = "600px";
	private static final boolean PANNEL_ENABLED = false;
	
	public String gridWidth = "900px";
	public String gridHeight = "600px";
	
	public String smallGridWidth = "600px";
	public String smallGridHeight = "600px";
	
	
	//Clears textboxes and combo boxes upon creating a client
	private static final boolean CLEAR_ON_CREATE = true;
	private static final boolean BOTTOM_CREATION_TABS = false;
	private static final boolean USE_VAADIN_FILTER = true;
	public static Boolean CREATION_ALLOW_NEW_VALUES = true;
	
	//Filtering 2.0
	public ClientFilter clientFilter = new ClientFilter(this);
	
	boolean alreadyGenerated = false;
	
	
	public Grid<Client> clientGrid = new Grid<Client>(Client.class);
	//public IndexedContainer clients = new IndexedContainer();
	public List<Client> clients;
	//nav bar
	//public NavBar navBar;
	
	VerticalLayout linkLayout = new VerticalLayout();

	Link devGitHub = new Link("GitHub", new ExternalResource("https://github.com/90301/Crescent-CRM-V/issues"));
	Link devAsciiArt = new Link("Ascii Art",
			new ExternalResource("http://patorjk.com/software/taag/#p=display&c=c&f=Letters"));

	Label versionLabel = new Label("Version");

	public Client selectedClient;
	Boolean discard = false;
	boolean unsavedProgress = false;
	private Client localSelClient = null;
	private String cacheDatabaseName = "";
	// holds all possible values that mean null.
	HashSet<String> nullStrings = new HashSet<String>();
	//public MasterUI masterUi;

	
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
	
	Button createLocationButton = new Button("Create Location", event -> this.createLocationClick());
	Button createStatusButton = new Button("Create Status", event -> this.createStatusClick());
	Button createGroupButton = new Button("Create Group", event -> this.createGroupClick());
	Panel panel = new Panel();
	
	//Client Editing
	ClientEditor clientEditor = new ClientEditor(this);
	
	{
		clientGrid.addSelectionListener(event -> this.selectItem());
	}

	public void createClientGrid() {
		//Client exampleClient = new Client();
		//TODO: do we really need to create a new container every time?
		
		
		//exampleClient.populateContainer();
		
		//clientGrid.setItems(masterUi.userDataHolder.getAllClients());
		
		clientGrid.setFrozenColumnCount(1);
		
	}
	/**
	 * Updates the client table with the list of filtered clients.
	 */
	public void updateClientGrid() {
		
		updateColors();
		
		if (masterUi.userDataHolder.getDatabasePrefix()!=cacheDatabaseName) {
			//try clearing the table if the database changes?
		//clientTable.clear();
		//clientTable.removeAllItems();
		
		//createClientGrid();
		
		
		Debugging.output("Detected database change: " + masterUi.userDataHolder.getDatabasePrefix() + " old: " + cacheDatabaseName
				,Debugging.CRM_OUTPUT
				,Debugging.CRM_OUTPUT_ENABLED);
		
		
		cacheDatabaseName = masterUi.userDataHolder.getDatabasePrefix();
		
		} else {
			//force clear always!
			//clientTable.clear();
			//clientTable.removeAllItems();
			
		}
		
		createClientGrid();
		
		clients = masterUi.userDataHolder.getAllClients().stream().filter(c -> clientFilter.checkClientMeetsFilter(c)).collect(Collectors.toList());
		clientGrid.setItems(clients);
		/*
		for (Client c : masterUi.userDataHolder.getAllClients()) {
			
			if (clients.containsId(c.getPrimaryKey())) {
				// remove old item
				clients.removeItem(c.getPrimaryKey());
			}
			
			
			if (!USE_VAADIN_FILTER) {
				if (clientFilter.checkClientMeetsFilter(c)) {
				//add item to indexed container
				clients.addItem(c);
				//generate client "item" for grid.
				Item clientItem = clients.getItem(c);
				c.genItem(clientItem);
				}
			
			} else {
				//add item to indexed container
				clients.addItem(c);
				//generate client "item" for grid.
				Item clientItem = clients.getItem(c);
				c.genItem(clientItem);
			}
		}
		*/
		
		//Filter 2.0
		/*
		if (USE_VAADIN_FILTER && clientFilter.getFilterHasChanged()){
			ProfilingTimer filterTimer1 = new ProfilingTimer("filter timer1");
			//Determine the time it takes to remove the old filter and add the new one.
			//TODO
			//It may be possible to move this code out to another method
			//clients.removeAllContainerFilters();
			//clients.addContainerFilter(clientFilter);
			
			clientFilter.setFilterHasChanged(false);
			filterTimer1.stopTimer();
		}
		*/

	}

	/**
	 * Fill combo boxes
	 */
	public void updateAllComboBoxes() {
		
		ProfilingTimer updateBoxTimer = new ProfilingTimer("update crm comboBoxes");
		// clear all combo boxes
		createClientStatus.clear();
		createClientLocation.clear();
		createClientGroup.clear();
		
		/*
		createClientStatus.removeAllItems();
		createClientLocation.removeAllItems();
		createClientGroup.removeAllItems();
		*/
		// create clients
		
		fillComboBox(createClientStatus, masterUi.userDataHolder.getAllStatus());
		fillComboBox(createClientLocation, masterUi.userDataHolder.getAllLocations());
		fillComboBox(createClientGroup, masterUi.userDataHolder.getAllGroups());
		
		/*
		createClientStatus.addItems(masterUi.userDataHolder.getAllStatus()());
		createClientLocation.addItems(masterUi.userDataHolder.getAllLocations());
		createClientGroup.addItems(masterUi.userDataHolder.getAllGroups());
		*/
		// filter
		clientFilter.updateAllComboBoxes();

		clientEditor.updateAllComboBoxes();
		
		
		updateBoxTimer.stopTimer();
		//fillComboBox(csvBackupSelect, BackupManager.getCsvBackups());
	}

	
	public <T extends Object> void fillComboBox(ComboBox<T> box, Collection<T> values) {
		
		box.setItems(values);
		//RapidProfilingTimer rpt = new RapidProfilingTimer("CrmUI fillComboBox");
		/*
		for (Object val : values) {
			// add any non template entity. (unless template is selected)
			//if (val != null && !val.toString().contains(DataHolder.TEMPLATE_STRING)) {
				box.addItem(val.toString());
			//}
			rpt.logTime();
		}
		*/
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
	 */
	public void selectItem() {

		Debugging.output("SELECTED AN ITEM." + clientGrid.getSelectedItems(),Debugging.OLD_OUTPUT);

		if (!clientGrid.getSelectedItems().isEmpty()) {
		Client selectedClient = (Client) clientGrid.getSelectedItems().toArray()[0];
		// null check
		if (selectedClient != null) {
			localSelClient = masterUi.userDataHolder.getClient(selectedClient.getPrimaryKey());
			if (localSelClient == null) {
				Debugging.output(
						"Null value: " + localSelClient + " found for client: " + ((Client)selectedClient).getPrimaryKey()
						,Debugging.OLD_OUTPUT);
				return;
			}

		} else {
			return;
		}
		if (clientEditor.checkUpdate() && this.discard == false) {
			this.discard = true;
			Notification n = new Notification("You have unsaved changes! <br> Click message to dismiss.","", Notification.Type.WARNING_MESSAGE,true);
			
			n.show(Page.getCurrent());
		} else {
			selectClient(localSelClient);
			
		}
		}

	}

	/**
	 * Populates comboboxes/note fields ect when selecting a client from the table.
	 * @param c
	 */
	public void selectClient(Client c) {
		
		
		this.discard = false;
		
		
		if (c != null) {

		} else {
			Debugging.output("Null value made it to selectClient: " + c,Debugging.OLD_OUTPUT);
			clientEditor.setVisible(false);
			return;
		}
		clientEditor.setVisible(true);
		clientGrid.select(c);
		this.selectedClient = c;
		clientEditor.selectClient(c);

	}

	public void createLocationClick() {
		String text = this.createLocationName.getValue();
		createLocation(text);
		this.createLocationName.clear();
	}

	public void createStatusClick() {
		String text = this.createStatusName.getValue();
		createStatus(text);
		this.createStatusName.clear();
	}

	public void createGroupClick() {
		String text = this.createGroupName.getValue();
		createGroup(text);
		this.createGroupName.clear();
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
				Debugging.output("Null value detected for client location", Debugging.CRM_ERROR);
				return;
			}
			if (InhalerUtils.stringNullCheck(createClientStatus.getValue().toString())) {
				Debugging.output("Null value detected for client status", Debugging.CRM_ERROR);
				return;
			}
			if (InhalerUtils.stringNullCheck(createClientGroup.getValue().toString())) {
				Debugging.output("Null value detected for client group", Debugging.CRM_ERROR);
				return;
			}
		} catch (NullPointerException e) {
			
			Debugging.output("Null value detected for client: " + e.getMessage(), Debugging.CRM_ERROR);
			return; // a null value was found
		}
		
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

		// set notes to template client
		if (masterUi.userDataHolder.templateClient != null) {
			c.setNotes(masterUi.userDataHolder.templateClient.getNotes());
			c.setProfilePicture(masterUi.userDataHolder.templateClient.getProfilePicture());
			
		} else {
			c.setNotes("Notes:");
		}

		Debugging.output("Created Client: " + c,Debugging.OLD_OUTPUT);

		masterUi.userDataHolder.store(c, Client.class);
		
		//reset filter if the current filter does not contain the client that was updated.
		if (!clientFilter.checkClientMeetsFilter(c)) {
			clientFilter.resetFilterClick();
		}
		
		updateClientGrid();
		
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
	public void enterView(ViewChangeEvent VCevent) {

		
		if (masterUi.loggedIn == false)
			masterUi.enterLogin();
		
		
		//this.setSpacing(true);
		//this.addStyleName("topScreenPadding");
		
		//only runs if it hasn't already.
		masterUi.userDataHolder.initalizeDatabases();

		// Nav Bar Code
		navBar.updateInfo();
		
		this.addComponent(navBar);
		

		((AbstractOrderedLayout) layout).setMargin(false);
		((AbstractOrderedLayout) layout).setSpacing(true);
		
		if (PANNEL_ENABLED) {
		panel.setContent(layout);
		panel.setHeight(PANEL_HEIGHT);
		panel.getContent().setSizeUndefined();
		this.setSizeUndefined();

		layout.setSizeUndefined();

		this.addComponent(panel);
		} else {
			this.addComponent(layout);
		}
		// setContent(layout);

		/***
		 * T A B S
		 */
		creationTabs.setStyleName("createClientBorder");
		// add a location
		
		//createLocationName 
	
		//createLocationListSelect.setNullSelectionAllowed(false);
		createLocationListSelect.setRows(CREATE_LIST_SELECT_ROWS);
		
		createLocationLayout.addComponent(createLocationName);
		createLocationLayout.addComponent(createLocationButton);
		createLocationLayout.addComponent(createLocationListSelect);
		
		createLocationLayout.setComponentAlignment(createLocationButton, Alignment.TOP_CENTER);
		createLocationLayout.setMargin(true);
		createLocationLayout.setSpacing(true);
		
		creationTabs.addTab(createLocationLayout, "Add Location");

		// Add status

		//createStatusListSelect.setNullSelectionAllowed(false);
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
		//createGroupListSelect.setNullSelectionAllowed(false);
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
			
			/*
			createClientStatus.setInvalidAllowed(invalidAllow);
			createClientLocation.setInvalidAllowed(invalidAllow);
			createClientGroup.setInvalidAllowed(invalidAllow);
			*/
			createClientStatus.setTextInputAllowed(textInputAllow);
			//createClientStatus.setNewItemsAllowed(newItemsAllowed);
			//createClientStatus.setNullSelectionAllowed(nullSelectionAllow);
			
			
			createClientLocation.setTextInputAllowed(textInputAllow);
			//createClientLocation.setNewItemsAllowed(newItemsAllowed);
			//createClientLocation.setNullSelectionAllowed(nullSelectionAllow);
			
			createClientGroup.setTextInputAllowed(textInputAllow);
			//createClientGroup.setNewItemsAllowed(newItemsAllowed);
			//createClientGroup.setNullSelectionAllowed(nullSelectionAllow);
			
		} else {
			/*
			createClientStatus.setInvalidAllowed(false);
			createClientLocation.setInvalidAllowed(false);
			createClientGroup.setInvalidAllowed(false);
			
			createClientStatus.setNullSelectionAllowed(false);
			createClientLocation.setNullSelectionAllowed(false);
			createClientGroup.setNullSelectionAllowed(false);
			*/
		}
		
		createClientLayout.setMargin(true);
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

		if (!BOTTOM_CREATION_TABS) {
		//Adding creation tab
		layout.addComponent(creationTabs);
		}
		
		layout.addComponent(clientFilter);
		
		/***
		 * M I D _ L A Y O U T
		 * 
		 */
		//Client Grid
		if (masterUi.user.getViewMode().equals(User.VIEW_MODE_SMALL)) {
			clientGrid.setWidth(smallGridWidth);
			clientGrid.setHeight(smallGridHeight);
		} else {
			clientGrid.setWidth(gridWidth);
			clientGrid.setHeight(gridHeight);
		}
		
		midLayout.setSpacing(true);
		layout.addComponent(midLayout);
		midLayout.addComponent(clientGrid);
		//clientTable.setSelectionMode(true);
		//clientGrid.setImmediate(true);
		//clientTable.addValueChangeListener(event -> this.selectItem(event));
		

		
		
		updateClientGrid();

		//Populating all Status, Location, and Group Lists
		updateCreationLists();
		
		/***
		 * C L I E N T
		 * E D I T I N G
		 */
		// CLIENT EDITING UI
		clientEditor.setVisible(false);
		
		

		updateAllComboBoxes();
		
		if (masterUi.mobileUser) {
			layout.addComponent(clientEditor);
		} else {
			midLayout.addComponent(clientEditor);
		}
		
		if (BOTTOM_CREATION_TABS) {
		//Adding creation tab
		layout.addComponent(creationTabs);
		}
		
		// version label
		versionLabel.setValue("Version: " + MasterUI.versionNumber + MasterUI.versionDescription);
		
		layout.addComponent(versionLabel);
		
		this.alreadyGenerated = true;
		
		creationTabs.setSelectedTab(createClientLayout);

	}
	
	public void updateColors() {
		//Dynamic CSS
		
		Styles styles = Page.getCurrent().getStyles();
		
		//styles.add(".Prospect { color: " + "blue" + "; }");
		
		for (Status s : masterUi.userDataHolder.getAllStatus()) {
			String statusName = InhalerUtils.removeSpecialCharacters(s.getStatusName());
			
			//convert int color to hex for use with  CSS
			if (s.getColor()!=0) {
			String statusColor = String.format("#%06X", (0xFFFFFF & s.getColor()));
			
			
			styles.add("."+ statusName+ " { color: " + statusColor + "; }");
			}
		}
		//TODO update this to work with vaadin 8
		
		/*
		clientGrid.setStyleGenerator(client -> {
			
			if (((Status)client.getItem().getItemProperty(Client.STATUS_GRID_NAME).getValue())!= null) {
			
			
				String cssName = ((Status)client.getItem().getItemProperty(Client.STATUS_GRID_NAME).getValue()).getStatusName();
				
				cssName = InhalerUtils.removeSpecialCharacters(cssName);
				
				return ""+ cssName;
			} else {
				return null;
			}
		});
		*/
	}
	


	public void updateCreationLists() {
		createStatusListSelect.setItems(masterUi.userDataHolder.getAllStatus());
		createLocationListSelect.setItems(masterUi.userDataHolder.getAllLocations());
		createGroupListSelect.setItems(masterUi.userDataHolder.getAllGroups());
		/*
		createStatusListSelect.removeAllItems();
		createStatusListSelect.addItems(masterUi.userDataHolder.getAllStatus());
		createLocationListSelect.removeAllItems();
		createLocationListSelect.addItems(masterUi.userDataHolder.getAllLocations());
		createGroupListSelect.removeAllItems();
		createGroupListSelect.addItems(masterUi.userDataHolder.getAllGroups());
		*/
	}



}