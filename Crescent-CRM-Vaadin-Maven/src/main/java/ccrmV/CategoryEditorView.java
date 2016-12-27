package ccrmV;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;

import uiElements.NavBar;

public class CategoryEditorView extends HorizontalLayout implements View {
	
	MasterUI masterUi;
	NavBar navBar;
	
	@Override
	public void enter(ViewChangeEvent event) {
		this.addComponent(navBar.sidebarLayout);
	}
	
	
	
}
