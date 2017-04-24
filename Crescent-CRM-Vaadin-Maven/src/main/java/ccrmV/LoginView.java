/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package ccrmV;

import java.util.Arrays;
import java.util.Optional;

import javax.servlet.http.Cookie;

//import com.vaadin.client.ui.Action;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import clientInfo.DataHolder;
import dbUtils.InhalerUtils;
import debugging.Debugging;
import debugging.profiling.ProfilingTimer;
import users.User;

public class LoginView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Label welcomeLabel = new Label();
	public TextField userField = new TextField("User: ");
	public PasswordField passField = new PasswordField("Pass: ");
	public Button loginButton = new Button("Login", event -> attemptLogin());
	public Button registerButton = new Button("Register", event -> createNewUserClick());
	public CheckBox rememberMe = new CheckBox();
	public Label rememberMeLabel = new Label("Remember Me");
	public Layout rememberLayout = new HorizontalLayout();
	public Layout userCreatorLayout = new VerticalLayout();
	public HorizontalLayout buttonLayout = new HorizontalLayout();
	private static final int PASS_MIN_LENGTH = 5;
	public String host;
	User u = new User();
	//public HorizontalLayout hLayoutIncorrect = new HorizontalLayout();
	//public HorizontalLayout hLayoutUser = new HorizontalLayout();

	Resource res = new ThemeResource("images/StyleC_Logo_London_9-25-16_2InchWide.svg");
	Resource res2 = new ThemeResource("images/StyleC_Logo_Dark_Theme_Login_11-30-16.svg");
	Image logo = new Image(null, res);
	Image logo2 = new Image(null, res2);
	//private static final ShortcutAction enterKeyShortcut = new ShortcutAction(null, ShortcutAction.KeyCode.ENTER, null);
	Label versionLabel = new Label();

	public boolean loginSuccess = false;
	public MasterUI masterUi;

	@Override
	public void enter(ViewChangeEvent VCevent) {
		//define components

		//logo
		this.removeAllComponents();

		versionLabel.setCaption("Version: " + MasterUI.versionNumber + MasterUI.versionDescription);
		addEnterKeyActionToTextField(passField);

		//add components
		this.setMargin(true);
		this.setSpacing(true);
		this.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		if (masterUi.currentTheme.equals(MasterUI.avaliableThemes[0])) {
			//Black Style Connect Logo for light theme
			this.addComponent(logo);
		} else {
			//White Style Connect Logo for dark theme
			this.addComponent(logo2);
		}
		//this.addComponent(welcomeLabel);
		this.addComponent(userField);
		this.addComponent(passField);
		userCreatorLayout.setCaption("Create Users");

		userCreatorLayout.addComponent(registerButton);
		buttonLayout.setSpacing(true);
		buttonLayout.addComponent(loginButton);
		buttonLayout.addComponent(registerButton);

		this.addComponent(buttonLayout);
		rememberLayout.addComponent(rememberMe);
		rememberLayout.addComponent(rememberMeLabel);
		this.addComponent(rememberLayout);
		this.addComponent(versionLabel);
		this.setComponentAlignment(versionLabel, Alignment.BOTTOM_CENTER);

		if (MasterUI.DEV_AUTO_LOGIN && MasterUI.DEVELOPER_MODE) {
			attemptLogin();
		}

		checkForCookie();
	}

	private void checkForCookie() {
		Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
		Optional<Cookie> cookie = Arrays.stream(cookies).filter(c -> c.getName().equals(User.COOKIE_NAME)).findFirst();
		if (cookie.isPresent()) {
			String val = cookie.get().getValue();

			User u = findUserForCookie(val);
			if (u != null) {
				//log in as the user.
				logInAs(u);
			}

		}
	}

	/**
	 * Searches through the user database looking for the auth key if found,
	 * returns the user. if not, returns null.
	 * 
	 * @param val
	 *            the auth key
	 * @return the user (if found)
	 */
	public User findUserForCookie(String val) {
		// TODO Auto-generated method stub
		for (User u : DataHolder.getUserMap().values()) {
			if (u.containsAuthKey(val)) {
				return u;
			}
		}

		return null;
	}

	public void clearFields() {
		this.userField.clear();
		this.passField.clear();
	}

	/**
	 * Adds the login command to the password box If you hit enter, it's the
	 * same as clicking the login button
	 * 
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

	public void attemptLogin() {
		ProfilingTimer loginTime = new ProfilingTimer("Login Time");
		String code = "";
		if ((code = DataHolder.attemptLogin(userField.getValue(), passField.getValue())) == DataHolder.SUCCESS_CODE
				|| (MasterUI.DEVELOPER_MODE && MasterUI.DEV_AUTO_LOGIN)) {
			if ((MasterUI.DEVELOPER_MODE && MasterUI.DEV_AUTO_LOGIN)) {
				//dev mode auto login	
				User loggedInUser = DataHolder.getUser(MasterUI.DEV_AUTOLOGIN_USER);
				logInAs(loggedInUser);
			} else {

				User loggedInUser = DataHolder.getUser(userField.getValue());

				if (rememberMe.getValue()) {
					//store the cookie
					loggedInUser.rememberUser();
					
					Debugging.output("Remembered user: " + InhalerUtils.toStringMaxObject(loggedInUser), Debugging.USER_DATABASE_DEBUG);
					
					DataHolder.store(loggedInUser,User.class);
				}
				logInAs(loggedInUser);
			}

			//Switch to CRM

		} else {

			if ((code = DataHolder.attemptLogin(userField.getValue(),
					passField.getValue())) == DataHolder.WRONG_PASS_CODE) {
				Notification.show("Incorrect username/password!", "\nClick to dismiss", Type.ERROR_MESSAGE);
				//hLayoutIncorrect.setVisible(true);
				//hLayoutUser.setVisible(false);
			} else if ((code = DataHolder.attemptLogin(userField.getValue(),
					passField.getValue())) == DataHolder.NO_USER_CODE) {
				Notification.show("No such user exists!", "\nClick to dismiss", Type.ERROR_MESSAGE);
				//hLayoutUser.setVisible(true);
				//hLayoutIncorrect.setVisible(false);
			}
			welcomeLabel.setData(code);
		}
		loginTime.stopTimer();
	}

	public void logInAs(User u) {
		loginSuccess = true;
		masterUi.user = u;
		masterUi.userDataHolder = DataHolder.getUserDataHolder(u);
		masterUi.startMainApp();
	}

	private void createNewUserClick() {
		String userName = userField.getValue();
		String pass = passField.getValue();

		User newUser = new User();
		// Check to see if a user already has a specific name
		// This doesn't appear to be working. a user can be created with
		// the same username.
		if (DataHolder.getUser(userName) == null && pass.length() >= PASS_MIN_LENGTH) {
			newUser.setUserName(userName);
			newUser.setPassword(pass);
			newUser.setAdmin(false);
			// set the database to the user name
			newUser.setDatabaseSelected(userName);
			newUser.addDatabaseAccsessable(userName);
			DataHolder.store(newUser, User.class);
			userField.setValue("");
			passField.setValue("");
			//Notification notif = new Notification("User Created!", "\nClick to dismiss", Type.HUMANIZED_MESSAGE);
			//notif.setDelayMsec(-1);
			//notif.show(Page.getCurrent());
		} else {

			if (pass.length() < PASS_MIN_LENGTH) {
				Notification notification = new Notification("Password is too short.",
						"<br>Minimum Length: " + PASS_MIN_LENGTH + "<br>Click to Dismiss",
						Notification.Type.ERROR_MESSAGE, true);
				notification.show(Page.getCurrent());
			} else {
				// User already exists
				Notification notification = new Notification("User: " + userName + " Already Exists.",
						"<br>Click to Dismiss", Notification.Type.ERROR_MESSAGE, true);
				notification.show(Page.getCurrent());
			}
		}

		//populateAllData();

		//clear textbox

	}

}
