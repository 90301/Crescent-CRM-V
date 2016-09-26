package ccrmV;


import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

import clientInfo.DataHolder;
import uiElements.NavBar;
import users.User;

@Theme("mytheme")
public class MasterUI extends UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}
	public static final String MAIN_APP = "mainApp";
	public static final String USER_EDITOR = "userEditor";
	
	
	public boolean loggedIn = false;
	public static ArrayList<String> authenicatedHosts = new ArrayList<String>();
	String userHost = "";
	
	Crescent_crm_vaadinUI mainApp = new Crescent_crm_vaadinUI();
	UserEditor userEditor = new UserEditor();
	
	User user = null;//logged in user
	
	protected void init(VaadinRequest request) {
		
		DataHolder.initalizeDatabases();
		
		userHost = request.getRemoteHost();
		mainNavigator = new Navigator(this,this);
		LoginView lv = new LoginView();
		lv.host = userHost;
		lv.masterUi = this;
		//Generate the nav bar to use
		NavBar navBar = new NavBar();
		navBar.masterUi = this;
		navBar.generateNavBar();
		
		
		
		mainApp.masterUi = this;
		mainApp.navBar = navBar;
		
		
		userEditor.masterUi = this;
		userEditor.navBar = navBar;
		
		
		
		if (authenicatedHosts.contains(userHost)) {
			loggedIn = true;
		}
		mainNavigator.addView("", lv);
		mainNavigator.addView(MAIN_APP, mainApp);
		mainNavigator.addView(USER_EDITOR, userEditor);
		
		//mainNavigator.navigateTo("Login View");
		
		/*
		 * Start of capstone
		 */
	}

	public void startMainApp() {
		loggedIn = true;
		authenicatedHosts.add(userHost);
		System.out.println("Attempting to navigate to the main application.");
		mainNavigator.navigateTo(MAIN_APP);
		
	}
	
	public void enterCRM() {
		
		mainNavigator.navigateTo(MAIN_APP);
	}
	
	public void enterUserEditor() {
		mainNavigator.navigateTo(USER_EDITOR);
	}
	

}
