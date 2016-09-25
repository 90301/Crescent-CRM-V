package ccrmV;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import uiElements.NavBar;

public class UserEditor extends VerticalLayout implements View {

	
	
	public MasterUI masterUi;
	Label welcomeLabel;
	public NavBar navBar;
	private boolean alreadyGenerated = false;
	
	Accordion userEditorAccordion;
	
	
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
		welcomeLabel = new Label("User Editor");
		userEditorAccordion = new Accordion();
		
		
		
		//put them on the screen
		
		this.addComponent(welcomeLabel); 
		
		this.addComponent(navBar.sidebarLayout);
		
		this.alreadyGenerated = true;
	}

}
