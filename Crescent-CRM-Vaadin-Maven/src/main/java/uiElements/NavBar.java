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
	Button logoutButton;
	Label statusLabel;
	public static final String BUTTON_WIDTH = "120px";
	public static final String BUTTON_HEIGHT = "120px";
	
	public Layout generateNavBar() {
		
		
		
		sidebarLayout.removeAllComponents();
		
		sidebarLayout.setSpacing(true);
		
		
		statusLabel = new Label("NavBar");
		
		crmButton = new Button("CRM", event -> this.crmClick() );
		
		userEditorButton = new Button("User Editor", event -> this.userEditorClick() );
		
		schedulerButton = new Button("Scheduler", event -> this.schedulerClick() );
		
		logoutButton = new Button("Log Out", event -> this.logoutClick());
		
		sidebarLayout.addComponent(statusLabel);
		sidebarLayout.addComponent(crmButton);
		sidebarLayout.addComponent(userEditorButton);
		sidebarLayout.addComponent(schedulerButton);
		sidebarLayout.addComponent(logoutButton);
		
		
		crmButton.setWidth(BUTTON_WIDTH);
		userEditorButton.setWidth(BUTTON_WIDTH);
        schedulerButton.setWidth(BUTTON_WIDTH);
        logoutButton.setWidth(BUTTON_WIDTH);
        
        crmButton.setHeight(BUTTON_HEIGHT);
        userEditorButton.setHeight(BUTTON_HEIGHT);
        schedulerButton.setHeight(BUTTON_HEIGHT);
        logoutButton.setHeight(BUTTON_HEIGHT);
        
        
		generatedLayout = true;
		
		return sidebarLayout;
		
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
