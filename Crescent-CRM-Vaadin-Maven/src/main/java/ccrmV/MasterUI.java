/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */

package ccrmV;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

import clientInfo.DataHolder;
import clientInfo.UserDataHolder;
import configuration.Configuration;
import dbUtils.InhalerUtils;
import debugging.Debugging;
import debugging.DebuggingVaadinUI;
import debugging.profiling.ProfilingTimer;
import debugging.unitTest.testSuiteExecutors.CRMTestExecutor;
import debugging.unitTest.testSuiteExecutors.CrmStressTestExecutor;
import debugging.unitTest.testSuiteExecutors.DatabaseTestExecutor;
import debugging.unitTest.testSuiteExecutors.LoginTestExecutor;
import debugging.unitTest.testSuiteExecutors.RestTestExecutor;
import debugging.unitTest.testSuiteExecutors.TestSuiteMetaExecutor;
import integrations.ChatSocket;
import themes.UserAgentProcessor;
import uiElements.NavBar;
import users.User;
@PreserveOnRefresh
@JavaScript({"https://www.gstatic.com/firebasejs/3.7.3/firebase.js","javascript/firebaseClient.js"})
@Theme("darkTheme")
@Widgetset("com.vaadin.v7.Vaadin7WidgetSet")
public class MasterUI extends UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final double versionNumber = 1.57;
	public static final String versionDescription = " Capstone Stable";

	public MasterUI() {

	}

	public MasterUI(Component content) {
		super(content);
	}

	Navigator mainNavigator;

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = true, ui = MasterUI.class)
	public static class Servlet extends VaadinServlet {

		private static final long serialVersionUID = 1L;

	}

	public static final String LOGIN = "Login";
	public static final String MAIN_APP = "mainApp";
	public static final String USER_EDITOR = "userEditor";
	public static final String SCHEDULER = "scheduler";
	public static final String INVENTORY = "inventory";
	public static final String DEBUGGING = "debugging";
	public static final String CATEGORY_EDITOR = "categoryEditor";

	// When DEVELOPER_MODE is set to true, developer settings will be enabled
	// this includes auto-login, and bypassing certain aspects of the software.
	// if something breaks because of developer mode, turn developer mode off.
	// no error checking will be implemented for developer mode, if you don't
	// know
	// exactly what you are doing with a setting, ask someone who does, or don't
	// enable it.
	public static final Boolean DEVELOPER_MODE = true;
	// auto login will be enabled if set to true, will attempt to login with
	// DEV_AUTOLOGIN_USER
	// if no such user exists, the application will crash.
	public static final Boolean DEV_AUTO_LOGIN = false;
	public static final String DEV_AUTOLOGIN_USER = "ccrmUser";
	
	public static final Boolean DEV_TEST_CODE = false; //UNIT TESTING
	public static final Boolean DEV_STRESS_TEST = false;//stress testing.
	
	public static Boolean unitTestsAlreadyRan = false;
	
	//public static final Boolean DEV_FORCE_MOBILE = false;

	// Automatically navigate to a specific page.
	// This could cause issues when dealing with database initialization
	public static final Boolean DEV_AUTO_NAVIGATE = false;
	public static final String DEV_AUTO_NAVIGATE_PAGE = INVENTORY;

	public boolean loggedIn = false;
	public static ArrayList<String> authenicatedHosts = new ArrayList<String>();
	String userHost = "";
	
	NavBar navBar = new NavBar();

	CrmUI mainApp = new CrmUI();
	UserEditor userEditor = new UserEditor();
	SchedulerView schedulerView = new SchedulerView();
	LoginView loginView = new LoginView();
	InventoryView inventoryView = new InventoryView();
	DebuggingVaadinUI debugView = new DebuggingVaadinUI();
	DevPlayground devPlayground = new DevPlayground();
	
	CategoryEditorView categoryEditorView = new CategoryEditorView();

	User user = null;// logged in user

	// This is intended to make it so we can just call masterUI.userDataHolder
	// everywhere dataholder used to be called.
	public UserDataHolder userDataHolder;// Set when logging in

	public static final String[] avaliableThemes = { "lightTheme", "darkTheme" };

	public static final String DEFAULT_THEME = avaliableThemes[1];

	public String currentTheme = DEFAULT_THEME;

	String userAgent = "";
	Boolean mobileUser = false;
	

	
	/*
	 * UNIT TESTING VARIABLES
	 */
	
	
	
	protected void init(VaadinRequest request) {
		
		Debugging.output("INIT CODE RUNNING. attributes: " + request.getAttributeNames(), Debugging.GOOGLE_FURY_DEBUG);
		
		Configuration.loadConfig();
		
		Debugging.output("Config loaded: " + Configuration.loadedConfig.get(Configuration.DOMAIN_NAME_KEY), Debugging.CONFIG_DEBUG);
		
		if (Configuration.loadedConfig.get(Configuration.DEVELOPER_MODE_OVERRIDE_KEY).equals("true")) {
			//System.out.println("Config turned off developer mode.");
			//DEVELOPER_MODE = false;
		}
		
		//disabled due to deprecation
		/*
		getPage()
		.addUriFragmentChangedListener(
	               new UriFragmentChangedListener() {
	           public void uriFragmentChanged(
	                   UriFragmentChangedEvent source) {
	               enter(source);
	            }
	        });
	        */
		
		userAgent = request.getHeader("User-Agent");
		
		mobileUser = UserAgentProcessor.isAgentMobile(userAgent);
		/*
		if (DEVELOPER_MODE && DEV_FORCE_MOBILE) {
			mobileUser = true;
		}
		*/
		changeViewMode();
		
		Debugging.output("User Agent: " + userAgent, Debugging.MOBILE_DEBUG);
		
		oAuthManage(request);

		this.setTheme(currentTheme);

		DataHolder.initalizeDatabases();

		userHost = request.getRemoteHost();
		mainNavigator = new Navigator(this, this);

		loginView.host = userHost;
		loginView.masterUi = this;
		// Generate the nav bar to use
		
		navBar.masterUi = this;
		navBar.generateNavBar();

		/*
		mainApp.masterUi = this;
		mainApp.navBar = navBar;
		*/
		mainApp.setVitals(this, navBar, "Clients", MAIN_APP);
		
		categoryEditorView.setVitals(this, navBar, "Categories", CATEGORY_EDITOR);
		
		userEditor.setVitals(this, navBar, "Settings", USER_EDITOR);
		
		schedulerView.setVitals(this, navBar, "Scheduler", SCHEDULER);
		
		inventoryView.setVitals(this, navBar, "Inventory", INVENTORY);
		
		if (DEVELOPER_MODE) {
			debugView.setVitals(this, navBar, "Debugging", DEBUGGING);
		
		
		devPlayground.setVitals(this, navBar, "Dev", "devPlay");
		}
		

		if (authenicatedHosts.contains(userHost)) {
			loggedIn = true;
		}
		
		mainNavigator.addView(LOGIN, loginView);
		/*
		mainNavigator.addView(MAIN_APP, mainApp);
		mainNavigator.addView(USER_EDITOR, userEditor);
		mainNavigator.addView("", userEditor);//attempting to fix a bug 
		mainNavigator.addView(SCHEDULER, schedulerView);
		mainNavigator.addView(INVENTORY, inventoryView);
		mainNavigator.addView(DEBUGGING, debugView);
		mainNavigator.addView(CATEGORY_EDITOR, categoryEditorView);
		*/
		
		enterLogin();

		/*
		 * Start of capstone
		 */

		// Debug unit testing
		Debugging.debugUnitTesting();
		devTestCode();

		// dev switch page
		if (DEVELOPER_MODE && DEV_AUTO_NAVIGATE) {
			mainNavigator.navigateTo(DEV_AUTO_NAVIGATE_PAGE);
		}
		
		
		ChatSocket.setupNodeSocket();
		
	}

	/**
	 * Runs when URI Fragement is changed
	 * 
	 * Can be used to get oAuth tokens
	 * @param source
	 */
	protected void enter(UriFragmentChangedEvent source) {
		Debugging.output("URI FRAGMENT CHANGE DETECTION. new URI: " + source, Debugging.GOOGLE_FURY_DEBUG);
		
		Debugging.output("PAGE GET LOCATION: " + getPage().getLocation(), Debugging.GOOGLE_FURY_DEBUG);
		
		//URI uriOfPage = new URI(getPage().getLocation());
		
		if (InhalerUtils.stringNullCheck(getPage().getLocation().getQuery())) {
			
			Debugging.output("Null query detected: " + getPage().getLocation(), Debugging.GOOGLE_FURY_DEBUG);
			return;
		}
		
		String[] querySplit = getPage().getLocation().getQuery().split("&");
		
		Debugging.outputArray(querySplit, Debugging.GOOGLE_FURY_DEBUG);
		
		String code = null;
		for (String q : querySplit) {
			if (q.contains("code=")) {
				code = q.substring(5, q.length()-1);
				Debugging.output("code: " + code, Debugging.GOOGLE_FURY_DEBUG);
			}
		}
		
		if (this.user!= null) {
			this.user.setGoogleKey(code);
			DataHolder.store(this.user, User.class);
			Debugging.output("google code updated: " + this.user.getGoogleKey(), Debugging.GOOGLE_FURY_DEBUG);
		}
	}

	/**
	 * Handles OAuth info for the request (HOPEFULLY)
	 * @param request
	 */
	private void oAuthManage(VaadinRequest request) {
		
		Debugging.output("Vaadin REQUEST: "+ request,Debugging.OAUTH2);
		
		Enumeration<String> attributes = request.getAttributeNames();
		while (attributes.hasMoreElements()) {
			String atribute = attributes.nextElement();
			String value = request.getAttribute(atribute).toString();
			Debugging.output("Found Attribute: "+ atribute + " Value: " + value,Debugging.OAUTH2);
		}
		Map<String, String[]> parameters = request.getParameterMap();
		
		//String queryString = request.getQueryString();
		//Debugging.output("Query String: " + queryString ,Debugging.OAUTH2);
		
		for (String parameterKey : parameters.keySet()) {
			String[] value = parameters.get(parameterKey);
			
			Debugging.output("Found Parameter: "+ parameterKey + " Value: " + value,Debugging.OAUTH2);
		}
		
		//String requestUrl = request.getRequestURI();
		//Debugging.output("Request URL: " + requestUrl ,Debugging.OAUTH2);
	}

	public void startMainApp() {
		ProfilingTimer startTime = new ProfilingTimer("Start Main App");
		loggedIn = true;
		authenicatedHosts.add(userHost);
		Debugging.output("Attempting to navigate to the main application.",Debugging.OLD_OUTPUT);
		//Change theme
		if (user!=null) {
			if (Stream.of(avaliableThemes).anyMatch(x -> x.equals(user.getTheme()))) {
				setTheme(user.getTheme());
				changeViewMode();
			} else {
				user.setTheme(DEFAULT_THEME);
				DataHolder.store(user, User.class);
			}
		
		}
		
		mainNavigator.navigateTo(MAIN_APP);
		
		startTime.stopTimer();
	}
	
	public void enterView(String viewLink) {
		this.mainNavigator.navigateTo(viewLink);
	}

	public void enterCRM() {
		ProfilingTimer crmTimer = new ProfilingTimer("enter CRM");
		
		
		mainNavigator.navigateTo(MAIN_APP);
		mainApp.updateClientGrid();
		mainApp.updateCreationLists();
		mainApp.updateAllComboBoxes();
		
		crmTimer.stopTimer();
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
	
	public void enterDebug() {
		mainNavigator.navigateTo(DEBUGGING);
	}
	
	public void enterCategoryEditor() {
		mainNavigator.navigateTo(CATEGORY_EDITOR);
	}
	
	public void logout() {
		user.deleteRememberMeCookie();
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
	 * NO ERROR CHECKING done in this method yet. sets the user data holder to
	 * the given userDataHolder name.
	 * 
	 * @param databaseName
	 *            - the user data holder to select
	 */
	public void setUserDataHolder(String databaseName) {
		ProfilingTimer databaseSwitchTime = new ProfilingTimer("Database Switch");
		// TODO make sure user actually can access the database
		this.userDataHolder = DataHolder.getUserDataHolder(databaseName);
		this.user.setDatabaseSelected(databaseName);
		DataHolder.store(this.user, User.class);
		this.userDataHolder.initalizeDatabases();
		navBar.updateInfo();
		
		databaseSwitchTime.stopTimer();
		
	}

	public void enterInventory() {
		mainNavigator.navigateTo(INVENTORY);

	}

	/**
	 * This method can be used to test things when in developer mode and dev
	 * test code are set to true
	 */
	public void devTestCode() {

		if (DEVELOPER_MODE && DEV_TEST_CODE && !unitTestsAlreadyRan) {

			Debugging.output("BEGIN DevTestCode. ", Debugging.MASTER_UI_TESTING_OUTPUT,
					Debugging.MASTER_UI_TESTING_OUTPUT_ENABLED);
			
			DatabaseTestExecutor dTestExecutor = new DatabaseTestExecutor();
			TestSuiteMetaExecutor metaTests = new TestSuiteMetaExecutor();
			LoginTestExecutor loginTests = new LoginTestExecutor(loginView,this);
			CRMTestExecutor crmTests = new CRMTestExecutor(mainApp,this);
			CrmStressTestExecutor crmStressTest = new CrmStressTestExecutor(mainApp,this);
			RestTestExecutor restTest = new RestTestExecutor();
			
			dTestExecutor.runTests();
			dTestExecutor.debugOutputTestCases();
			
			metaTests.runTests();
			metaTests.debugOutputTestCases();
			
			loginTests.runTests();
			loginTests.debugOutputTestCases();
			
			//Change active database here?
			
			crmTests.runTests();
			crmTests.debugOutputTestCases();
			
			if (DEV_STRESS_TEST) {
				crmStressTest.runTests();
			}
			
			restTest.runTests();
			
			unitTestsAlreadyRan = true;
		}
	}

	public void changeTheme(String themeSelected) {

		// do nothing if the current theme is already this theme
		if (this.currentTheme.equals(themeSelected)) {
			return;
		}
		// ensure the theme is in the list of available themes
		for (String s : avaliableThemes) {
			if (s.equals(themeSelected)) {
				this.currentTheme = themeSelected;
				this.setTheme(this.currentTheme);
			}
		}

	}

	public boolean getMobileUser() {
		return mobileUser;
	}

	public void changeViewMode() {
		
		if (user==null) {
			mobileUser = UserAgentProcessor.isAgentMobile(userAgent);
			return;
		}
		
		
		if (user.getViewMode().equals(User.VIEW_MODE_DESKTOP)) {
			mobileUser = false;
		} else if (user.getViewMode().equals(User.VIEW_MODE_MOBILE)) {
			mobileUser = true;
		} else if (user.getViewMode().equals(User.VIEW_MODE_DEFAULT) || user.getViewMode().equals(User.VIEW_MODE_SMALL)) {
			//TODO
			//detect mobile
			mobileUser = UserAgentProcessor.isAgentMobile(userAgent);
		} else {
			user.setViewMode(User.VIEW_MODE_DEFAULT);
			DataHolder.store(user, User.class);
		}
		
		navBar.generateNavBar();
		
	}

}
