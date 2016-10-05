package ccrmV;

import java.util.Date;
import java.util.Locale;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import uiElements.NavBar;

public class SchedulerView extends HorizontalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5854568268650533061L;
	public MasterUI MasterUi;
	public NavBar navBar;
	
	public HorizontalLayout createEventLayout;
	public VerticalLayout schedulerLayout;
	
	/*
	 * UI Components
	 */
	Calendar cal = new Calendar();
	private TextField createEventNameTextField;
	private ComboBox createEventClientComboBox;
	private DateField createEventStartDateField;
	private ComboBox createEventDurrationField;
	private Button createEventButton;
	
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO 
		if (!MasterUi.loggedIn || MasterUi.user == null)
			MasterUi.enterLogin();
		
		schedulerLayout = new VerticalLayout();
		createEventLayout = new HorizontalLayout();
		
		/*
		 * create components here and edit settings
		 */
		cal.setHeight("800px");
		cal.setCaption("Schedule");
		cal.setLocale(Locale.getDefault());
        cal.setImmediate(true);
        //cal.setSizeFull();
        
        Date startDate = new Date();
        Date endDate = new Date();
        
        //This is pretty awful.
        endDate.setDate(startDate.getDate()+5);
        cal.setStartDate(new Date()); 
        cal.setEndDate(endDate);
        
        
        createEventNameTextField = new TextField("Event Name");
        
        createEventClientComboBox = new ComboBox("Client");
        
        createEventStartDateField = new PopupDateField("Event Start");
        String dateTimeFormat = "MM-dd-yyyy HH:mm aa";
		createEventStartDateField.setDateFormat(dateTimeFormat);
        createEventStartDateField.setValue(new Date());
        createEventStartDateField.setWidth("220px");
        
        createEventDurrationField = new ComboBox("Durration");
        
        createEventButton = new Button("Create Event");
        
        
        createEventLayout.addComponent(createEventNameTextField);
        createEventLayout.addComponent(createEventClientComboBox);
        createEventLayout.addComponent(createEventStartDateField);
        createEventLayout.addComponent(createEventDurrationField);
        createEventLayout.addComponent(createEventButton);
        
        
        populateComboBoxes();
        
		/*
		 * Add components here
		 */
		
		this.addComponent(navBar.sidebarLayout);
		//this.addComponent(new Label("Scheduler"));
		
		//this.addComponent(cal);
		schedulerLayout.addComponent(createEventLayout);
		schedulerLayout.addComponent(cal);
		
		this.addComponent(schedulerLayout);
		
	}



	private void populateComboBoxes() {
		
		createEventClientComboBox.removeAllItems();
		createEventClientComboBox.addItems(MasterUi.userDataHolder.getClientMap().keySet());		
	}

}
