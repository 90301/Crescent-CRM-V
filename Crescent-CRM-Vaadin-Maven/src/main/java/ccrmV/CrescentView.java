package ccrmV;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.v7.ui.HorizontalLayout;

import uiElements.NavBar;

public abstract class CrescentView extends HorizontalLayout implements View {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MasterUI masterUi;
	public NavBar navBar;
	private String viewName;//The name that appears on the button
	private String viewLink;//the internal program name (for navigation)
	
	public void setVitals(MasterUI masterUi, NavBar navBar, String viewName, String viewLink) {
		this.masterUi = masterUi;
		this.navBar = navBar;
		this.setViewName(viewName);
		this.setViewLink(viewLink);
		
		masterUi.mainNavigator.addView(viewLink, this);
		navBar.addNavButton(this);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		
		//styling
		this.setSpacing(true);
		this.addStyleName("topScreenPadding");
		this.addStyleName("rightPadding");
		
		//adding components
		this.addComponent(navBar);
		
		
		enterView(event);
	}
	
	public abstract void enterView(ViewChangeEvent event);

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getViewLink() {
		return viewLink;
	}

	public void setViewLink(String viewLink) {
		this.viewLink = viewLink;
	}
	
	

}
