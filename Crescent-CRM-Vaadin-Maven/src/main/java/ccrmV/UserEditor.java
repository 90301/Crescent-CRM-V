/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package ccrmV;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;

import clientInfo.DataHolder;
import clientInfo.UserDataHolder;
import dbUtils.InhalerUtils;
import debugging.Debugging;
import integrations.OauthUtils;
import uiElements.NavBar;
import users.User;

public class UserEditor extends HorizontalLayout implements View {

	public MasterUI masterUi;
	Label welcomeLabel;
	public NavBar navBar;
	private boolean alreadyGenerated = false;

	Accordion userEditorAccordion = new Accordion();

	// Layout topHorrizontal,topVertical;

	// This --> Horizontal --> Vertical

	Layout settingsLayout = new VerticalLayout();

	ComboBox settingsDatabaseComboBox = new ComboBox("Database");
	ComboBox settingsThemeComboBox = new ComboBox("Theme");
	Button settingsChangePasswordButton = new Button("Change Password");

	// TODO: Allow password changing in settings.
	// PasswordField settingsChangePasswordField;

	Layout userCreatorLayout = new VerticalLayout();;

	TextField createUserNameTextField = new TextField("User Name");
	PasswordField createUserPassField = new PasswordField("Password");
	Button createUserButton = new Button("Create New User");

	// Admin menu
	Layout adminLayout = new HorizontalLayout();;
	// may need to change this to a grid later. needs to be improved a lot, bare
	// bones functonality implemented
	// ComboBox adminUserSelector, adminDatabaseSelector;
	// Button adminAddDatabaseButton;

	Button adminUpdateSettingsButton = new Button("Update Settings");
	// user options (select a database)

	ListSelect adminUserListSelect = new ListSelect("Select User");

	Layout adminSettingsLayout = new VerticalLayout();
	Grid adminSettingsGrid = new Grid();

	TwinColSelect adminDatabaseTwinColSelect = new TwinColSelect();

	// Database Creator
	TextField databaseCreatorTextField = new TextField("Database Name");
	Button databaseCreatorButton = new Button("Create Database");
	HorizontalLayout databaseCreatorLayout = new HorizontalLayout();

	// oauth2
	HorizontalLayout oauthLayout = new HorizontalLayout();
	Link googleLink = OauthUtils.genGoogleLink();

	{
		settingsDatabaseComboBox.addValueChangeListener(e -> userChangeDatabase());

		createUserButton.addClickListener(click -> createNewUserClick());

		adminUserListSelect.addValueChangeListener(e -> adminSelectUser());

		adminUpdateSettingsButton.addClickListener(e -> updateAdminSettings());

		adminDatabaseTwinColSelect.addValueChangeListener(e -> databasePermissionValueChanged());

		databaseCreatorButton.addClickListener(click -> createDatabaseClick());
	}

	private static final String ADMIN_SETTING_ID = "Admin";
	private static final String SETTING_NAME_ID = "Setting Name";
	private static final String SETTING_VALUE_ID = "Value";

	public UserEditor() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * public UserEditor(Component... children) { super(children); // TODO
	 * Auto-generated constructor stub }
	 */

	/*
	 * Layout Options: Vertical Horizontal Grid
	 */

	/**
	 * 
	 */
	@Override
	public void enter(ViewChangeEvent event) {

		// Ensure the user is logged in
		if (masterUi.loggedIn == false)
			masterUi.enterLogin();
		// return;

		// if (this.alreadyGenerated) {
		this.removeAllComponents();
		// return;
		// }
		// userEditorAccordion
		// Width to be adjusted, mobile consideration needed
		// userEditorAccordion.setWidth("500px");
		// initialize components

		// Settings Menu

		// settingsLayout

		settingsLayout.setCaption("Settings");
		((AbstractOrderedLayout) settingsLayout).setMargin(true);
		((AbstractOrderedLayout) settingsLayout).setSpacing(true);
		// TODO
		// settingsDatabaseComboBox
		// settingsThemeComboBox
		// settingsChangePasswordButton

		// settings action listeners
		settingsDatabaseComboBox.setImmediate(true);
		settingsDatabaseComboBox.setNullSelectionAllowed(false);

		settingsLayout.addComponent(settingsDatabaseComboBox);
		settingsLayout.addComponent(settingsThemeComboBox);
		settingsLayout.addComponent(settingsChangePasswordButton);

		// User Editor for creating new users
		// welcomeLabel = new Label("User Editor");

		// if (masterUi.user.getAdmin()) {

		// userCreatorLayout
		((AbstractOrderedLayout) userCreatorLayout).setMargin(true);
		((AbstractOrderedLayout) userCreatorLayout).setSpacing(true);

		userCreatorLayout.setCaption("Create Users");
		userCreatorLayout.addComponent(createUserNameTextField);
		userCreatorLayout.addComponent(createUserPassField);
		userCreatorLayout.addComponent(createUserButton);

		// Admin menu!
		adminLayout.setCaption("Edit Users Permissions (ADMIN)");
		((AbstractOrderedLayout) adminLayout).setMargin(true);
		((AbstractOrderedLayout) adminLayout).setSpacing(true);

		// adminUserListSelect.setRows(20);
		adminUserListSelect.setSizeFull();
		adminUserListSelect.setNullSelectionAllowed(false);

		// Twin Col Select
		adminDatabaseTwinColSelect.setLeftColumnCaption("Databases Avaliable");
		// Accessible
		adminDatabaseTwinColSelect.setRightColumnCaption("Databases Accessible");
		adminDatabaseTwinColSelect.setImmediate(true);

		adminDatabaseTwinColSelect.setNullSelectionAllowed(false);

		// Admin Settings Layout
		// adminSettingsLayout
		// ((AbstractOrderedLayout) adminSettingsLayout).setMargin(true);
		((AbstractOrderedLayout) adminSettingsLayout).setSpacing(true);

		generateSettingsGrid();

		// TODO:change the name of this caption??
		adminSettingsGrid.setCaption("User Settings");
		adminSettingsGrid.setEditorEnabled(true);
		adminSettingsGrid.setResponsive(true);
		adminSettingsLayout.addComponent(adminSettingsGrid);
		// HeaderRow filterRow = adminSettingsGrid.appendHeaderRow();

		

		
		adminSettingsLayout.addComponent(adminUpdateSettingsButton);

		/*
		 * adminSettingsLayout.addComponent(adminDatabaseSelector);
		 * adminSettingsLayout.addComponent(adminAddDatabaseButton);
		 */

		adminSettingsLayout.addComponent(adminDatabaseTwinColSelect);
		
		adminLayout.addComponent(adminUserListSelect);
		adminLayout.addComponent(adminSettingsLayout);

		// Database Creator
		// databaseCreatorLayout
		databaseCreatorLayout.setCaption("Database Creator");
		databaseCreatorLayout.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER);
		// TODO add a list of databases already in the system.
		// databaseCreatorTextField

		// databaseCreatorButton

		databaseCreatorLayout.addComponent(databaseCreatorTextField);
		databaseCreatorLayout.addComponent(databaseCreatorButton);

		// }

		// oauth
		oauthLayout.setCaption("oauth");

		oauthLayout.addComponent(googleLink);

		// Data in itmes
		populateAllData();

		// put them on the screen

		// this.addComponent(welcomeLabel);

		this.addComponent(navBar.sidebarLayout);

		this.addComponent(userEditorAccordion);

		this.alreadyGenerated = true;

		checkAdmin();
	}

	private void checkAdmin() {
		Boolean isAdmin = masterUi.getUser().getAdmin();

		userCreatorLayout.setVisible(isAdmin);
		adminLayout.setVisible(isAdmin);
		adminSettingsLayout.setVisible(isAdmin);
		databaseCreatorLayout.setVisible(isAdmin);

		userEditorAccordion.removeAllComponents();

		userEditorAccordion.addComponent(settingsLayout);

		// admin only
		if (isAdmin) {
			userEditorAccordion.addComponent(userCreatorLayout);
			userEditorAccordion.addComponent(adminLayout);
			userEditorAccordion.addComponent(databaseCreatorLayout);
		}
		userEditorAccordion.addComponent(oauthLayout);
	}

	/**
	 * Creates a new database with a given name automatically adds it to the
	 * logged in user
	 */
	private void createDatabaseClick() {
		String newDatabaseName = databaseCreatorTextField.getValue();
		if (InhalerUtils.stringNullCheck(newDatabaseName)) {
			Debugging.output("Null new database name: " + newDatabaseName, Debugging.USER_EDITOR_OUTPUT,
					Debugging.USER_EDITOR_OUTPUT_ENABLED);
			return;
		}
		UserDataHolder udh = DataHolder.getUserDataHolder(newDatabaseName);
		DataHolder.store(udh, UserDataHolder.class);

		User currentUser = masterUi.getUser();

		currentUser.addDatabaseAccsessable(newDatabaseName);

		DataHolder.store(currentUser, User.class);
		populateAllData();

	}

	/**
	 * Updates the user's accessible databases
	 */
	private void databasePermissionValueChanged() {
		User adminEditUser = safeGetUser();
		if (adminEditUser == null) {

			return;
		}

		Collection<String> selectedDatabases = (Collection<String>) adminDatabaseTwinColSelect.getValue();

		if (selectedDatabases.size() < 1) {
			Debugging.output(
					"Error: Attempting to make a user have access to NO databases: " + selectedDatabases.size()
							+ " User: " + adminEditUser + " Aborting for safety!",
					Debugging.USER_EDITOR_OUTPUT, Debugging.USER_EDITOR_OUTPUT_ENABLED);
			return;
		}

		adminEditUser.setDatabaseAccessible(selectedDatabases);

		Debugging.output("Selected Databases for user: " + selectedDatabases, Debugging.USER_EDITOR_OUTPUT,
				Debugging.USER_EDITOR_OUTPUT_ENABLED);

		DataHolder.store(adminEditUser, User.class);

		// Experimental
		// DO NOT DO THIS
		// populateAllData();
	}

	/**
	 * Attempts to get the user to edit permissions on. Will output to
	 * USER_EDITOR_OUTPUT if something is null.
	 * 
	 * @return the user to edit or null
	 */
	private User safeGetUser() {
		String usernameSelected = (String) adminUserListSelect.getValue();
		if (usernameSelected == null && InhalerUtils.stringNullCheck(usernameSelected)) {
			Debugging.output("null user selected: " + usernameSelected, Debugging.USER_EDITOR_OUTPUT,
					Debugging.USER_EDITOR_OUTPUT_ENABLED);
			return null;
		}

		User adminEditUser = DataHolder.getUser(usernameSelected);

		if (adminEditUser == null) {
			Debugging.output("User Note Found: " + usernameSelected + " : " + adminEditUser,
					Debugging.USER_EDITOR_OUTPUT, Debugging.USER_EDITOR_OUTPUT_ENABLED);
			return null;
		}

		return adminEditUser;
	}

	/**
	 * Updates settings for the selected user
	 */
	private void updateAdminSettings() {
		String usernameSelected = (String) adminUserListSelect.getValue();
		if (usernameSelected == null && InhalerUtils.stringNullCheck(usernameSelected)) {
			Debugging.output("null user selected: " + usernameSelected, Debugging.USER_EDITOR_OUTPUT,
					Debugging.USER_EDITOR_OUTPUT_ENABLED);
			return;
		}

		User adminEditUser = DataHolder.getUser(usernameSelected);

		if (adminEditUser == null) {
			Debugging.output("User Note Found: " + usernameSelected + " : " + adminEditUser,
					Debugging.USER_EDITOR_OUTPUT, Debugging.USER_EDITOR_OUTPUT_ENABLED);
			return;
		}
		Collection<?> items = adminSettingsGrid.getContainerDataSource().getItemIds();
		// Loop through all settings
		for (Object item : items) {
			Debugging.output("Item Id: " + item, Debugging.USER_EDITOR_OUTPUT, Debugging.USER_EDITOR_OUTPUT_ENABLED);
			// get the "ITEM" (setting)
			Item setting = adminSettingsGrid.getContainerDataSource().getItem(item);
			Debugging.output("Attempting to update settings: " + setting, Debugging.USER_EDITOR_OUTPUT,
					Debugging.USER_EDITOR_OUTPUT_ENABLED);

			// Get the setting name
			String settingName = (String) setting.getItemProperty(SETTING_NAME_ID).getValue();

			Debugging.output("Setting Name: " + settingName, Debugging.USER_EDITOR_OUTPUT,
					Debugging.USER_EDITOR_OUTPUT_ENABLED);

			Boolean settingValue = (Boolean) setting.getItemProperty(SETTING_VALUE_ID).getValue();

			Debugging.output("Setting Value: " + settingValue, Debugging.USER_EDITOR_OUTPUT,
					Debugging.USER_EDITOR_OUTPUT_ENABLED);

			// begin the god awful case statement until we can figure out a way
			// to map this painlessly
			switch (settingName) {
			case ADMIN_SETTING_ID:
				Debugging.output("Admin: " + settingValue, Debugging.USER_EDITOR_OUTPUT,
						Debugging.USER_EDITOR_OUTPUT_ENABLED);
				adminEditUser.setAdmin(settingValue);
				break;
			}

		}

		// update the user
		DataHolder.store(adminEditUser, User.class);
		populateAllData();

	}

	/**
	 * Loads information into the admin table
	 */
	private void adminSelectUser() {

		String usernameSelected = (String) adminUserListSelect.getValue();
		if (usernameSelected == null && InhalerUtils.stringNullCheck(usernameSelected)) {
			Debugging.output("null user selected: " + usernameSelected, Debugging.USER_EDITOR_OUTPUT,
					Debugging.USER_EDITOR_OUTPUT_ENABLED);
			return;
		}

		User adminEditUser = DataHolder.getUser(usernameSelected);

		if (adminEditUser == null) {
			Debugging.output("User Note Found: " + usernameSelected + " : " + adminEditUser,
					Debugging.USER_EDITOR_OUTPUT, Debugging.USER_EDITOR_OUTPUT_ENABLED);
			return;
		}

		// adminSettingsGrid
		adminEditAdmin = adminEditUser.getAdmin();
		adminSettingsGrid.getContainerDataSource().removeAllItems();
		adminSettingsGrid.addRow(ADMIN_SETTING_ID, adminEditAdmin);
		// database twin col select

		adminDatabaseTwinColSelect.removeAllItems();
		// add all available databases
		adminDatabaseTwinColSelect.addItems(DataHolder.getUserDataHolderMap().keySet());
		adminDatabaseTwinColSelect.setValue(adminEditUser.getDatabasesAccsessable());

	}

	/**
	 * Changes the current user's database they are using.
	 */
	private void userChangeDatabase() {
		String databaseName = (String) settingsDatabaseComboBox.getValue();
		Debugging.output("Attempting to change database to: " + databaseName, Debugging.USER_EDITOR_OUTPUT,
				Debugging.USER_EDITOR_OUTPUT_ENABLED);
		if (databaseName == null || !masterUi.user.getDatabasesAccsessable().contains(databaseName)) {
			Debugging.output("Invalid value detected on database change: " + databaseName, Debugging.USER_EDITOR_OUTPUT,
					Debugging.USER_EDITOR_OUTPUT_ENABLED);
			return;
		}

		masterUi.setUserDataHolder(databaseName);
	}

	Boolean adminEditAdmin;

	/**
	 * Generates initial settings for the grid
	 */
	private void generateSettingsGrid() {

		adminSettingsGrid.removeAllColumns();
		adminSettingsGrid.addColumn(SETTING_NAME_ID, String.class).setEditable(false);
		adminSettingsGrid.addColumn(SETTING_VALUE_ID, Boolean.class).setEditable(true);

		adminSettingsGrid.getContainerDataSource().removeAllItems();
		// TODO remove test code
		adminEditAdmin = true;
		adminSettingsGrid.addRow(ADMIN_SETTING_ID, adminEditAdmin);

		/*
		 * SortedMap<String,Object> settings = new TreeMap<String,Object>();
		 * settings.put("ADMIN", true);
		 */

	}

	// populate data!

	private void populateAllData() {

		// settings
		settingsDatabaseComboBox.removeAllItems();
		settingsDatabaseComboBox.addItems(masterUi.getUser().getDatabasesAccsessable());
		settingsDatabaseComboBox.setValue(masterUi.user.getDatabaseSelected());

		if (masterUi.user.getAdmin()) {
			// populate the admin menu stuff
			/*
			 * adminUserSelector.removeAllItems();
			 * adminUserSelector.addItems(DataHolder.getUserMap().keySet());
			 * 
			 * adminDatabaseSelector.removeAllItems();
			 * adminDatabaseSelector.addItems(DataHolder.getUserDataHolderMap().
			 * keySet());
			 */
			adminUserListSelect.removeAllItems();
			adminUserListSelect.addItems(DataHolder.getUserMap().keySet());

		}
	}

	/**
	 * Creates a new user with a username and password by default the user is
	 * not an admin also creates a userDataHolder with that user's name as the
	 * primary key
	 */
	private void createNewUserClick() {
		String userName = createUserNameTextField.getValue();
		String pass = createUserPassField.getValue();

		User nUser = new User();
		// Check to see if a user already has a specific name
		// This doesn't appear to be working. a user can be created with
		// the same username.
		if (DataHolder.getUser(userName) == null) {
			nUser.setUserName(userName);
			nUser.setPassword(pass);
			nUser.setAdmin(false);
			// set the database to the user name
			nUser.setDatabaseSelected(userName);
			nUser.addDatabaseAccsessable(userName);

			DataHolder.store(nUser, User.class);

		} else {
			// User already exists

		}

		populateAllData();
	}

	/*
	 * private void addDatabaseClick() { //initial null value checking String
	 * userName = (String) adminUserSelector.getValue();
	 * 
	 * if (InhalerUtils.stringNullCheck(userName)) return;
	 * 
	 * User u = DataHolder.getUser(userName);
	 * 
	 * if (u==null) return;
	 * 
	 * 
	 * 
	 * }
	 */

}
