/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package uiElements;

import java.util.LinkedHashMap;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

import ccrmV.CrescentView;
import ccrmV.MasterUI;
import debugging.Debugging;

public class NavBar extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NavBar() {
		// TODO Auto-generated constructor stub
	}
	
	//public VerticalLayout sidebarLayout = new VerticalLayout();
	
	public Boolean generatedLayout = false;//Intended to prevent generating the nav bar multiple times for the same user
	
	
	public MasterUI masterUi;//This provides the link to the master UI, which allows switching pages
	
	//public Layout sideBarLayout;
	/*
	Button crmButton;
	Button userEditorButton;
	Button schedulerButton;
	Button inventoryButton;
	Button categoryEditorButton;
	Button debugButton;
	*/
	Button logoutButton;
	Label statusLabel = new Label();
	
	VerticalLayout navButtonLayout = new VerticalLayout();
	
	LinkedHashMap<String,Button> navButtons = new LinkedHashMap<String,Button>();
	
	public static String BUTTON_WIDTH = "120px";
	public static String BUTTON_HEIGHT = "60px";
	
	{
		
		navButtonLayout.setSpacing(true);
		navButtonLayout.setMargin(false);
		
		logoutButton = new Button("Log Out", event -> this.logoutClick());
		
	}
	
	public Layout generateNavBar() {
		
		if (masterUi.getMobileUser()) {
			BUTTON_WIDTH = "120px";
			BUTTON_HEIGHT = "120px";
		} else {
			BUTTON_WIDTH = "120px";
			BUTTON_HEIGHT = "60px";
		}
		
		this.addStyleName("navBarMargin");
		
		
		for (Button b : navButtons.values()) {
			b.setWidth(BUTTON_WIDTH);
			b.setHeight(BUTTON_HEIGHT);
		}
		
		this.setSpacing(true);
		this.setMargin(false);

		this.addComponent(statusLabel);

		this.addComponent(navButtonLayout);
		//add logout button
		setupButton(logoutButton);
        
		generatedLayout = true;
		
		return this;
		
	}
	



	public void addNavButton(CrescentView cView) {
		if (!navButtons.containsKey(cView.getViewName())) {
			//generate a button for the view
			Button linkButton = new Button(cView.getViewName(),e -> navButtonClick(cView.getViewLink(),e));
			setupNavButton(linkButton);
			navButtons.put(cView.getViewName(), linkButton);
			navButtonLayout.addComponent(linkButton);
			
		}
	}




	private void navButtonClick(String viewLink, ClickEvent e) {
		 Debugging.output("Nav Button Clicked: " + viewLink, Debugging.NAV_DEBUG);
		 masterUi.enterView(viewLink);
	}

	public void setupNavButton(Button b) {
		b.setWidth(BUTTON_WIDTH);
        b.setHeight(BUTTON_HEIGHT);
	}


	public void setupButton(Button b) {
		b.setWidth(BUTTON_WIDTH);
        b.setHeight(BUTTON_HEIGHT);
        this.addComponent(b);
	}

	public void updateInfo() {
		statusLabel.setCaption("" + masterUi.getUser().getPrimaryKey());
		statusLabel.setValue(masterUi.getUser().getDatabaseSelected());
	}

	private void logoutClick() {
		masterUi.logout();
	}

}
