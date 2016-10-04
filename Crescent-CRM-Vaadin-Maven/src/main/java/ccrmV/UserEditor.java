package ccrmV;

import java.util.SortedMap;
import java.util.TreeMap;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import clientInfo.DataHolder;
import dbUtils.InhalerUtils;
import uiElements.NavBar;
import users.User;

public class UserEditor extends HorizontalLayout implements View {

	
	
	public MasterUI masterUi;
	Label welcomeLabel;
	public NavBar navBar;
	private boolean alreadyGenerated = false;
	
	Accordion userEditorAccordion;
	
	//Layout topHorrizontal,topVertical;
	
	//This  --> Horizontal --> Vertical
	
	Layout settingsLayout;
	
	ComboBox settingsDatabaseComboBox, settingsThemeComboBox;
	Button settingsChangePasswordButton;
	
	//TODO: Allow password changing in settings.
	//PasswordField settingsChangePasswordField;
	
	Layout userCreatorLayout;
	
	TextField createUserNameTextField;
	PasswordField createUserPassField;
	Button createUserButton;
	
	//Admin menu
	Layout adminLayout;
	//may need to change this to a grid later. needs to be improved a lot, bare
	//bones functonality implemented 
	ComboBox adminUserSelector, adminDatabaseSelector;
	Button adminAddDatabaseButton;
	//user options (select a database)
	
	ListSelect adminUserListSelect;
	
	Layout adminSettingsLayout;
	Grid adminSettingsGrid;
	
	public UserEditor() {
		// TODO Auto-generated constructor stub
	}

	/*
	public UserEditor(Component... children) {
		super(children);
		// TODO Auto-generated constructor stub
	}
*/
	
	/*
	 * Layout Options:
	 * Vertical
	 * Horizontal
	 * Grid
	 */
	
	
	/**
	 * 
	 */
	@Override
	public void enter(ViewChangeEvent event) {

		//Ensure the user is logged in
		if (masterUi.loggedIn == false)
			masterUi.enterLogin();
			//return;
		
		if (this.alreadyGenerated ) {
			this.removeAllComponents();
			//return;
		}
		userEditorAccordion = new Accordion();
		//Width to be adjusted, mobile consideration needed
		//userEditorAccordion.setWidth("500px");
		//initialize components
		
		//Settings Menu
		
		settingsLayout = new VerticalLayout();
		settingsLayout.setCaption("Settings");
		((AbstractOrderedLayout) settingsLayout).setMargin(true);
	//	TODO
		settingsDatabaseComboBox = new ComboBox("Database");
		settingsThemeComboBox = new ComboBox ("Theme");
		settingsChangePasswordButton = new Button("Change Password");
		
		
		
		
		settingsLayout.addComponent(settingsDatabaseComboBox);
		settingsLayout.addComponent(settingsThemeComboBox);
		settingsLayout.addComponent(settingsChangePasswordButton);
		
		
		userEditorAccordion.addComponent(settingsLayout);
		
		//User Editor for creating new users
		//welcomeLabel = new Label("User Editor");
		
		
		userCreatorLayout = new VerticalLayout();
		
		((AbstractOrderedLayout) userCreatorLayout).setMargin(true);
		
		createUserNameTextField = new TextField("User Name");
		createUserPassField = new PasswordField("Password");
		createUserButton = new Button("Create New User");
		
		//Listeners
		createUserButton.addClickListener(click -> createNewUserClick());
		
		userCreatorLayout.setCaption("Create Users");
		userCreatorLayout.addComponent(createUserNameTextField);
		userCreatorLayout.addComponent(createUserPassField);
		userCreatorLayout.addComponent(createUserButton);
		
		userEditorAccordion.addComponent(userCreatorLayout);
		
		if (masterUi.user.getAdmin()) {
			//Admin menu!
			adminLayout = new HorizontalLayout();
			adminLayout.setCaption("Edit Users (ADMIN)");
			((AbstractOrderedLayout) adminLayout).setMargin(true);
			
			adminUserSelector = new ComboBox("User");
			
			adminDatabaseSelector = new ComboBox("Database");
			
			adminAddDatabaseButton = new Button("Add database to user");
			
			adminAddDatabaseButton.addClickListener(click -> addDatabaseClick());
			
			adminUserListSelect = new ListSelect("Select User");
			
			adminUserListSelect.setRows(20);
			
			adminSettingsGrid = new Grid();
			
			adminSettingsLayout = new VerticalLayout();
			
			generateSettingsGrid();
			
			
			
			//TODO:change the name of this caption??
			adminSettingsGrid.setCaption("User Settings");
			/*
			adminLayout.addComponent(adminUserSelector);
			adminLayout.addComponent(adminDatabaseSelector);
			adminLayout.addComponent(adminAddDatabaseButton);
			*/
			adminLayout.addComponent(adminUserListSelect);
			
			adminSettingsLayout.addComponent(adminSettingsGrid);
			
			adminSettingsLayout.addComponent(adminDatabaseSelector);
			adminSettingsLayout.addComponent(adminAddDatabaseButton);
			
			adminLayout.addComponent(adminSettingsLayout);
			userEditorAccordion.addComponent(adminLayout);
			
		}
		
		//Data in itmes
		populateAllData();
		
		//put them on the screen
		
		//this.addComponent(welcomeLabel); 
		
		this.addComponent(navBar.sidebarLayout);
		
		this.addComponent(userEditorAccordion);
		
		this.alreadyGenerated = true;
	}
	
	private void generateSettingsGrid() {
		
		adminSettingsGrid.removeAllColumns();
		adminSettingsGrid.addColumn("Setting Name", String.class).setEditable(false);
		adminSettingsGrid.addColumn("Value", Boolean.class).setEditable(true);
		
		//TODO remove test code
		adminSettingsGrid.addRow("Admin", true);
		
		/*
		SortedMap<String,Object> settings = new TreeMap<String,Object>(); 
		settings.put("ADMIN", true);
		*/
		
		
	}

	//populate data!
	


	private void populateAllData() {
		
		if (masterUi.user.getAdmin()) {
			//populate the admin menu stuff
			adminUserSelector.removeAllItems();
			adminUserSelector.addItems(DataHolder.getUserMap().keySet());
			
			adminDatabaseSelector.removeAllItems();
			adminDatabaseSelector.addItems(DataHolder.getUserDataHolderMap().keySet());
			
			adminUserListSelect.removeAllItems();
			adminUserListSelect.addItems(DataHolder.getUserMap().keySet());
			
		}
	}

	/**
	 * Creates a new user with 
	 */
	private void createNewUserClick() {
		String userName = createUserNameTextField.getValue();
		String pass = createUserPassField.getValue();
		
		
		User nUser = new User();
		//Check to see if a user already has a specific name
		if (DataHolder.getUser(userName) == null) {
			nUser.setUserName(userName);
			nUser.setPassword(pass);
			nUser.setAdmin(false);
			DataHolder.store(nUser, User.class);
		} else {
			//User already exists
			
		}
	}
	
	private void addDatabaseClick() {
		//initial null value checking
		String userName = (String) adminUserSelector.getValue();
		
		if (InhalerUtils.stringNullCheck(userName))
			return;
		
		User u = DataHolder.getUser(userName);
		
		if (u==null)
			return;
		
		
		//UserDataHolder udh = ;
	}

}
