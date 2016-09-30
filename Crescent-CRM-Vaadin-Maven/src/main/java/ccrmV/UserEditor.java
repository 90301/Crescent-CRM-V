package ccrmV;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import clientInfo.DataHolder;
import uiElements.NavBar;
import users.User;

public class UserEditor extends VerticalLayout implements View {

	
	
	public MasterUI masterUi;
	Label welcomeLabel;
	public NavBar navBar;
	private boolean alreadyGenerated = false;
	
	Accordion userEditorAccordion;
	VerticalLayout userCreatorLayout;
	
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
		
		//initialize components
		
		//User Editor for creating new users
		welcomeLabel = new Label("User Editor");
		userEditorAccordion = new Accordion();
		
		userCreatorLayout = new VerticalLayout();
		
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
			adminLayout.setCaption("Admin");
			
			adminUserSelector = new ComboBox("User");
			
			adminDatabaseSelector = new ComboBox("Database");
			
			adminAddDatabaseButton = new Button("Add database to user!");
			
			adminLayout.addComponent(adminUserSelector);
			adminLayout.addComponent(adminDatabaseSelector);
			adminLayout.addComponent(adminAddDatabaseButton);
			
			userEditorAccordion.addComponent(adminLayout);
			
		}
		
		//Data in itmes
		populateAllData();
		
		//put them on the screen
		
		this.addComponent(welcomeLabel); 
		
		this.addComponent(navBar.sidebarLayout);
		
		this.addComponent(userEditorAccordion);
		
		this.alreadyGenerated = true;
	}
	
	//populate data!
	
	private void populateAllData() {
		
		if (masterUi.user.getAdmin()) {
			//populate the admin menu stuff
			adminUserSelector.removeAllItems();
			adminUserSelector.addItems(DataHolder.getUserMap().keySet());
			
			adminDatabaseSelector.removeAllItems();
			adminDatabaseSelector.addItems(DataHolder.getUserDataHolderMap().keySet());
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

}
