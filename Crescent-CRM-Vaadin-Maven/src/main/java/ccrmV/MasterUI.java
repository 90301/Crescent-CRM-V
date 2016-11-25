/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */

package ccrmV;


import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

import clientInfo.DataHolder;
import clientInfo.UserDataHolder;
import dbUtils.InhalerUtils;
import dbUtils.MaxDB;
import dbUtils.MaxDBTable;
import dbUtils.MaxField;
import debugging.Debugging;
import inventory.InventoryItem;
import uiElements.NavBar;
import users.User;

@Theme("mytheme")
public class MasterUI extends UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final double versionNumber = .91;
	public static final String versionDescription = " Custom Field Storage";

	public MasterUI() {
		// TODO Auto-generated constructor stub
	}

	public MasterUI(Component content) {
		super(content);
		// TODO Auto-generated constructor stub
	}
	
	Navigator mainNavigator;
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = MasterUI.class)
	public static class Servlet extends VaadinServlet {


		private static final long serialVersionUID = 1L;

	}
	public static final String LOGIN = "Login";
	public static final String MAIN_APP = "mainApp";
	public static final String USER_EDITOR = "userEditor";
	public static final String SCHEDULER = "scheduler";
	public static final String INVENTORY = "inventory";
	
	//When DEVELOPER_MODE is set to true, developer settings will be enabled
	//this includes auto-login, and bypassing certain aspects of the software.
	//if something breaks because of developer mode, turn developer mode off.
	//no error checking will be implemented for developer mode, if you don't know
	//exactly what you are doing with a setting, ask someone who does, or don't enable it.
	public static final Boolean DEVELOPER_MODE = true;
	//auto login will be enabled if set to true, will attempt to login with DEV_AUTOLOGIN_USER
	//if no such user exists, the application will crash.
	public static final Boolean DEV_AUTO_LOGIN = false;
	public static final String DEV_AUTOLOGIN_USER = "ccrmUser";
	public static final Boolean DEV_TEST_CODE = true;
	
	//Automatically navigate to a specific page.
	//This could cause issues when dealing with database initialization
	public static final Boolean DEV_AUTO_NAVIGATE = false;
	public static final String DEV_AUTO_NAVIGATE_PAGE = INVENTORY;
	
	
	
	public boolean loggedIn = false;
	public static ArrayList<String> authenicatedHosts = new ArrayList<String>();
	String userHost = "";
	
	Crescent_crm_vaadinUI mainApp = new Crescent_crm_vaadinUI();
	UserEditor userEditor = new UserEditor();
	SchedulerView schedulerView = new SchedulerView();
	LoginView loginView = new LoginView();
	InventoryView inventoryView = new InventoryView();
	User user = null;//logged in user
	
	
	//This is intended to make it so we can just call masterUI.userDataHolder
	//everywhere dataholder used to be called.
	public UserDataHolder userDataHolder;//Set when logging in
	
	protected void init(VaadinRequest request) {
		
		DataHolder.initalizeDatabases();
		
		userHost = request.getRemoteHost();
		mainNavigator = new Navigator(this,this);
		
		loginView.host = userHost;
		loginView.masterUi = this;
		//Generate the nav bar to use
		NavBar navBar = new NavBar();
		navBar.masterUi = this;
		navBar.generateNavBar();
		
		
		
		mainApp.masterUi = this;
		mainApp.navBar = navBar;
		
		
		userEditor.masterUi = this;
		userEditor.navBar = navBar;
		
		schedulerView.masterUi = this;
		schedulerView.navBar = navBar;
		
		inventoryView.masterUi = this;
		inventoryView.navBar = navBar;
		
		
		if (authenicatedHosts.contains(userHost)) {
			loggedIn = true;
		}
		mainNavigator.addView(LOGIN, loginView);
		mainNavigator.addView(MAIN_APP, mainApp);
		mainNavigator.addView(USER_EDITOR, userEditor);
		mainNavigator.addView(SCHEDULER, schedulerView);
		mainNavigator.addView(INVENTORY, inventoryView);
		
		enterLogin();
		
		/*
		 * Start of capstone
		 */
		
		//Debug unit testing
		Debugging.debugUnitTesting();
		devTestCode();
		
		//dev switch page
		if (DEVELOPER_MODE && DEV_AUTO_NAVIGATE) {
			mainNavigator.navigateTo(DEV_AUTO_NAVIGATE_PAGE);
		}
		
	}

	public void startMainApp() {
		loggedIn = true;
		authenicatedHosts.add(userHost);
		System.out.println("Attempting to navigate to the main application.");
		mainNavigator.navigateTo(MAIN_APP);
		
	}
	
	public void enterCRM() {
		
		mainNavigator.navigateTo(MAIN_APP);
		mainApp.updateClientTable();
		mainApp.updateCreationLists();
		mainApp.fillAllComboBoxes();
	}
	
	public void enterUserEditor() {
		mainNavigator.navigateTo(USER_EDITOR);
	}
	
	public void enterLogin() {
		mainNavigator.navigateTo(LOGIN);
	}
	public void enterScheduler() {
		mainNavigator.navigateTo(SCHEDULER);
	}
	
	public void logout() {
		loggedIn = false;
		userDataHolder = null;
		user = null;
		loginView.clearFields();
		mainNavigator.navigateTo(LOGIN);
	}
	
	public User getUser() {
		return user;
	}

	/**
	 * NO ERROR CHECKING done in this method yet.
	 * sets the user data holder to the given userDataHolder name.
	 * @param databaseName - the user data holder to select
	 */
	public void setUserDataHolder(String databaseName) {
		//TODO make sure user actually can access the database
		this.userDataHolder = DataHolder.getUserDataHolder(databaseName);
		this.user.setDatabaseSelected(databaseName);
		DataHolder.store(this.user,User.class);
	}

	public void enterInventory() {
		mainNavigator.navigateTo(INVENTORY);
		
	}
	
	/**
	 * This method can be used to test things when in developer mode and dev test code
	 * are set to true
	 */
	public void devTestCode() {
		
		if (DEVELOPER_MODE && DEV_TEST_CODE) {
			
			Debugging.output("BEGIN DevTestCode. "
					, Debugging.MASTER_UI_TESTING_OUTPUT
					, Debugging.MASTER_UI_TESTING_OUTPUT_ENABLED);
			
			//inventory unit testing
			
			UserDataHolder udhTest = new UserDataHolder();
			udhTest.setDatabasePrefix("test");
			udhTest.initalizeDatabases();
			DataHolder.store(udhTest, UserDataHolder.class);
			
			InventoryItem invItem = new InventoryItem();
			invItem.setItemKey("RR2016");
			invItem.setItemName("7RR red dye");
			invItem.setItemCategory("Dye");
			invItem.setItemBarcode("0000001");
			invItem.setItemURL("www.google.com");
			invItem.setItemStock(12);
			invItem.setItemReorderPoint(6);
			
			Debugging.output("Created Inventory Item: " + invItem
					, Debugging.MASTER_UI_TESTING_OUTPUT
					, Debugging.MASTER_UI_TESTING_OUTPUT_ENABLED);
			
			udhTest.store(invItem, InventoryItem.class);
			
			
			Debugging.output("Stored Inventory Item. " + invItem.debugOutput()
					, Debugging.MASTER_UI_TESTING_OUTPUT
					, Debugging.MASTER_UI_TESTING_OUTPUT_ENABLED);
			
			
			//TESTING INHALER UTILS
			
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("Key1", "value1");
			map.put("KEY2", "Value2");
			
			String xml= InhalerUtils.mapToXML(map);
			
			HashMap<String, String> map2 = InhalerUtils.xmlToMap(xml);
		}
	}

}
