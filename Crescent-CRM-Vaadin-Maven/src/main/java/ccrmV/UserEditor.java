package ccrmV;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import uiElements.NavBar;

public class UserEditor extends VerticalLayout implements View {

	
	
	public MasterUI masterUi;
	Label welcomeLabel;
	public NavBar navBar;
	private boolean alreadyGenerated = false;
	
	Accordion userEditorAccordion;
	VerticalLayout userCreatorLayout;
	
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
			return;
		
		if (this.alreadyGenerated ) {
			this.removeAllComponents();
			//return;
		}
		
		//initialize components
		
		//User Editor for creating new users
		welcomeLabel = new Label("User Editor");
		userEditorAccordion = new Accordion();
		
		userCreatorLayout = new VerticalLayout();
		
		TextField createUserNameTextField = new TextField("User Name");
		PasswordField createUserPassField = new PasswordField("Password");
		Button createUserButton = new Button("Create New User");
		
		userCreatorLayout.addComponent(createUserNameTextField);
		userCreatorLayout.addComponent(createUserPassField);
		userCreatorLayout.addComponent(createUserButton);
		
		userEditorAccordion.addComponent(userCreatorLayout);
		
		
		//put them on the screen
		
		this.addComponent(welcomeLabel); 
		
		this.addComponent(navBar.sidebarLayout);
		
		this.addComponent(userEditorAccordion);
		
		this.alreadyGenerated = true;
	}

}
