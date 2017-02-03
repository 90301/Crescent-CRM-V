/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package ccrmV;

//import com.vaadin.client.ui.Action;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import clientInfo.DataHolder;
import users.User;

public class LoginView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Label welcomeLabel = new Label();;
	public TextField userField = new TextField("User: ");;
	public PasswordField passField  = new PasswordField("Pass: ");
	public Label loginError = new Label(String.format("<color=\"red\"> Incorrect Username/Password!"), ContentMode.HTML);
	public Button loginButton  = new Button("Login", event -> attemptLogin());
	public String host;
	public HorizontalLayout hLayout = new HorizontalLayout();
	
	
	Resource res = new ThemeResource("images/StyleC_Logo_London_9-25-16_2InchWide.svg");
	Resource res2 = new ThemeResource("images/StyleC_Logo_Dark_Theme_Login_11-30-16.svg");
	Image logo = new Image(null, res);
	Image logo2 = new Image(null, res2);
	//private static final ShortcutAction enterKeyShortcut = new ShortcutAction(null, ShortcutAction.KeyCode.ENTER, null);
	Label versionLabel = new Label();
	
	@Override
	public void enter(ViewChangeEvent VCevent) {
		//define components
		//welcomeLabel
		//welcomeLabel.setCaption("Welcome: " + host);
		
		//userField 
		
		//passField
		
		//loginButton
		
		
		 //logo
		this.removeAllComponents();
		
		versionLabel.setCaption("Version: " + MasterUI.versionNumber + MasterUI.versionDescription);
		addEnterKeyActionToTextField(passField);
	
		//add components
	    this.setMargin(true);
	    this.setSpacing(true);
		this.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		if (masterUi.currentTheme.equals(MasterUI.avaliableThemes[0])) {
			//Black Logo
			this.addComponent(logo);
		} else {
			//white logo
			this.addComponent(logo2);
		}
		//this.addComponent(welcomeLabel);
		this.addComponent(userField);
		this.addComponent(passField);
		this.addComponent(hLayout);
		hLayout.addComponent(loginError);
		this.addComponent(loginButton);
		this.addComponent(versionLabel);
		this.setComponentAlignment(versionLabel, Alignment.BOTTOM_CENTER);
		
		
		hLayout.setVisible(false);
		
		
		if (MasterUI.DEV_AUTO_LOGIN && MasterUI.DEVELOPER_MODE) {
			attemptLogin();
		}
	}
	
	public void clearFields() {
		this.userField.clear();
		this.passField.clear();
	}
	
	
	/**
	 * Adds the login command to the password box
	 * If you hit enter, it's the same as clicking the login button
	 * @param passField2
	 */
	
	   private void addEnterKeyActionToTextField(final PasswordField passField2) {
	        passField2.addFocusListener(new FocusListener() {
	            /**
				 * 
				 */
				private static final long serialVersionUID = -1716906807499361465L;

				//What will be done when your Text Field is active is Enter Key is pressed
	            public void focus(final FocusEvent event) {
	                //Whatever you want to do on Enter Key pressed
	                // for example: sasveButton.setClickShortcut(KeyCode.ENTER);
	            	//attemptLogin();
	            	loginButton.setClickShortcut(KeyCode.ENTER);
	            }
	        });
	        
	    }
	   
public boolean loginSucsess = false;
public MasterUI masterUi; 
	public void attemptLogin() {
		//welcomeLabel.setValue("U: " + userField.getValue() + " P: " + passField.getValue());
		// TODO Auto-generated method stub
		//if (userField.getValue().contains("ccrmUser") && passField.getValue().contains("ccrmPass") || MasterUI.authenicatedHosts.contains(host)) {
		String code = "";
		if ((code=DataHolder.attemptLogin(userField.getValue(), passField.getValue()))==DataHolder.SUCCESS_CODE ||
				(MasterUI.DEVELOPER_MODE && MasterUI.DEV_AUTO_LOGIN)) {
			if ((MasterUI.DEVELOPER_MODE && MasterUI.DEV_AUTO_LOGIN)) {
			//dev mode auto login	
				loginSucsess = true;
				User loggedInUser = DataHolder.getUser(MasterUI.DEV_AUTOLOGIN_USER);
				masterUi.user = loggedInUser;
				masterUi.userDataHolder = DataHolder.getUserDataHolder(loggedInUser);
			} else {
			loginSucsess = true;
			User loggedInUser = DataHolder.getUser(userField.getValue());
			masterUi.user = loggedInUser;
			masterUi.userDataHolder = DataHolder.getUserDataHolder(loggedInUser);
			}
			hLayout.setVisible(false);

			masterUi.startMainApp();
			
		} else {
			
			if((code=DataHolder.attemptLogin(userField.getValue(), passField.getValue()))==DataHolder.WRONG_PASS_CODE) {
				hLayout.setVisible(true);
			}
			welcomeLabel.setData(code);
		}
	}

}
