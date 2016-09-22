package ccrmV;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import uiElements.NavBar;

public class UserEditor extends VerticalLayout implements View {

	
	
	public MasterUI masterUi;
	Label welcomeLabel;
	public NavBar navBar;
	private boolean alreadyGenerated = false;
	
	public UserEditor() {
		// TODO Auto-generated constructor stub
	}

	/*
	public UserEditor(Component... children) {
		super(children);
		// TODO Auto-generated constructor stub
	}
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
		welcomeLabel = new Label("User Editor");
		
		
		//put them on the screen
		
		this.addComponent(welcomeLabel);
		
		this.addComponent(navBar.sidebarLayout);
		
		this.alreadyGenerated = true;
	}

}
