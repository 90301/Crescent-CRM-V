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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
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
	Label welcomeLabel;
	TextField userField;
	PasswordField passField;
	Button loginButton;
	public String host;
	//private static final ShortcutAction enterKeyShortcut = new ShortcutAction(null, ShortcutAction.KeyCode.ENTER, null);
	
	
	@Override
	public void enter(ViewChangeEvent VCevent) {
		//define components
		welcomeLabel = new Label("Welcome : " + host);
		userField = new TextField("User: ");
		passField = new PasswordField("Pass: ");
		loginButton = new Button("Login", event -> attemptLogin());
		Resource res = new ThemeResource("images/StyleC_Logo_London_9-25-16_2InchWide.svg");
		
		Image image = new Image(null, res);
		
		addEnterKeyActionToTextField(passField);
	
		//add components
	    this.setMargin(true);
		this.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		this.addComponent(image);
		this.addComponent(welcomeLabel);
		this.addComponent(userField);
		this.addComponent(passField);
		this.addComponent(loginButton);
		
		
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
	private void attemptLogin() {
		//welcomeLabel.setValue("U: " + userField.getValue() + " P: " + passField.getValue());
		// TODO Auto-generated method stub
		//if (userField.getValue().contains("ccrmUser") && passField.getValue().contains("ccrmPass") || MasterUI.authenicatedHosts.contains(host)) {
		String code = "";
		if ((code=DataHolder.attemptLogin(userField.getValue(), passField.getValue()))==DataHolder.SUCCESS_CODE) {
			loginSucsess = true;
			User loggedInUser = DataHolder.getUser(userField.getValue());
			masterUi.user = loggedInUser;
			masterUi.startMainApp();
		} else {
			welcomeLabel.setData(code);
		}
	}

}
