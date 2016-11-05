package ccrmV;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;

import uiElements.NavBar;

public class InventoryView extends HorizontalLayout implements View {

	public MasterUI masterUi;
	public NavBar navBar;
	private boolean alreadyGenerated;
	
	@Override
	public void enter(ViewChangeEvent event) {
		if (masterUi.loggedIn == false)
			masterUi.enterLogin();
		
		if (this.alreadyGenerated ) {
			this.removeAllComponents();
			//return;
		}
		this.addComponent(navBar.sidebarLayout);
		
		this.alreadyGenerated = true;
	}

}
