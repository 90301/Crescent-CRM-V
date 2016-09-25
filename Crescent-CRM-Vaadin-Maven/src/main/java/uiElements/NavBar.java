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
	Label statusLabel;
	public static final String BUTTON_WIDTH = "120px";
	
	public Layout generateNavBar() {
		
		
		
		sidebarLayout.removeAllComponents();
		
		statusLabel = new Label("Nav Bar Generated on: " + System.currentTimeMillis());
		
		crmButton = new Button("CRM", event -> this.crmClick() );
		
		userEditorButton = new Button("User Editor", event -> this.userEditorClick() );
		
		schedulerButton = new Button("Scheduler", event -> this.schedulerClick() );
		
		sidebarLayout.addComponent(statusLabel);
		sidebarLayout.addComponent(crmButton);
		sidebarLayout.addComponent(userEditorButton);
		sidebarLayout.addComponent(schedulerButton);
		
		crmButton.setWidth(BUTTON_WIDTH);
		userEditorButton.setWidth(BUTTON_WIDTH);
        schedulerButton.setWidth(BUTTON_WIDTH);
		generatedLayout = true;
		
		return sidebarLayout;
		
	}

	private void schedulerClick() {
		// TODO Auto-generated method stub
		
	}

	private void userEditorClick() {
		// TODO Auto-generated method stub
		masterUi.enterUserEditor();
	}

	private void crmClick() {
		// TODO Auto-generated method stub
		masterUi.enterCRM();
	}

}
