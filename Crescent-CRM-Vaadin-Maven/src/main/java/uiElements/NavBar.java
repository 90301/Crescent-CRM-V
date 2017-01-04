/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package uiElements;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

import ccrmV.MasterUI;

public class NavBar {

	public NavBar() {
		// TODO Auto-generated constructor stub
	}
	
	public VerticalLayout sidebarLayout = new VerticalLayout();
	
	public Boolean generatedLayout = false;//Intended to prevent generating the nav bar multiple times for the same user
	
	
	public MasterUI masterUi;//This provides the link to the master UI, which allows switching pages
	
	//public Layout sideBarLayout;
	Button crmButton;
	Button userEditorButton;
	Button schedulerButton;
	Button inventoryButton;
	Button categoryEditorButton;
	Button debugButton;
	Button logoutButton;
	Label statusLabel;
	public static final String BUTTON_WIDTH = "120px";
	public static final String BUTTON_HEIGHT = "60px";
	
	public Layout generateNavBar() {
		
		
		
		sidebarLayout.removeAllComponents();
		
		sidebarLayout.setSpacing(true);
		
		
		statusLabel = new Label("NavBar");
		
		crmButton = new Button("CRM", event -> this.crmClick() );
		
		categoryEditorButton = new Button("Categories", event -> this.categoryEditorClick() );
		
		userEditorButton = new Button("User Editor", event -> this.userEditorClick() );
		
		schedulerButton = new Button("Scheduler", event -> this.schedulerClick() );
		
		inventoryButton = new Button("Inventory", event -> this.inventoryClick() );
		
		
		
		debugButton = new Button("Debugging", event -> this.debugClick() );
		
		logoutButton = new Button("Log Out", event -> this.logoutClick());
		
		sidebarLayout.addComponent(statusLabel);
		
		/*
		sidebarLayout.addComponent(crmButton);
		sidebarLayout.addComponent(userEditorButton);
		sidebarLayout.addComponent(schedulerButton);
		sidebarLayout.addComponent(inventoryButton);
		sidebarLayout.addComponent(logoutButton);
		*/
		
		setupButton(crmButton);
		setupButton(categoryEditorButton);
		setupButton(userEditorButton);
		setupButton(schedulerButton);
		setupButton(inventoryButton);
		
		
		if (MasterUI.DEVELOPER_MODE) {
			setupButton(debugButton);
		}
		setupButton(logoutButton);
        
		generatedLayout = true;
		
		return sidebarLayout;
		
	}
	






	public void setupButton(Button b) {
		b.setWidth(BUTTON_WIDTH);
        b.setHeight(BUTTON_HEIGHT);
        sidebarLayout.addComponent(b);
	}
	
		private void categoryEditorClick() {
		masterUi.enterCategoryEditor();
	}
	private void debugClick() {
		masterUi.enterDebug();
	}
	private void inventoryClick() {
		masterUi.enterInventory();
	}

	public void updateInfo() {
		statusLabel.setCaption("" + masterUi.getUser().getPrimaryKey());
		statusLabel.setValue(masterUi.getUser().getDatabaseSelected());
	}

	private void logoutClick() {
		// TODO Auto-generated method stub
		masterUi.logout();
	}

	private void schedulerClick() {
		masterUi.enterScheduler();
	}

	private void userEditorClick() {
		masterUi.enterUserEditor();
	}

	private void crmClick() {
		masterUi.enterCRM();
	}

}
