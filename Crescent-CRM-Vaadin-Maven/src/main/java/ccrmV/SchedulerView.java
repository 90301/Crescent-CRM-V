package ccrmV;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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
	private TextField createEventNameTextField = new TextField("Event Name");
	private ComboBox createEventClientComboBox =  new ComboBox("Client");
	private DateField createEventStartDateField = new PopupDateField("Event Start");
	private TextField createEventDurrationTextField  = new TextField("Durration");
	private ComboBox createEventDurrationComboBox = new ComboBox("Durration Unit");
	
	private Button createEventButton  = new Button("Create Event");
	
	
	public static final ArrayList<String> baseTimeList = new ArrayList<>();
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO 
		if (!MasterUi.loggedIn || MasterUi.user == null)
			MasterUi.enterLogin();
		
		schedulerLayout = new VerticalLayout();
		createEventLayout = new HorizontalLayout();
		createEventLayout.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER);
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
        
        //TODO fix this code displaying 5 days
        endDate.setDate(startDate.getDate()+5);
        cal.setStartDate(new Date()); 
        cal.setEndDate(endDate);
        
        
        //createEventNameTextField = ;
        
        //createEventClientComboBox =
        
        
        //date selection
        //createEventStartDateField = ;
        String dateTimeFormat = "MM-dd-yyyy HH:mm aa";
		createEventStartDateField.setDateFormat(dateTimeFormat);
        createEventStartDateField.setValue(new Date());
        createEventStartDateField.setWidth("220px");
        
         //Show the popup with minute / hour  selectors
        createEventStartDateField.setResolution(Resolution.MINUTE);
        
        //createEventDurrationTextField
        createEventDurrationTextField.setWidth("80px");
        
        //createEventDurrationComboBox 
        createEventDurrationComboBox.setWidth("120px");
        
        //createEventButton
        createEventButton.addClickListener(click -> createEventButtonClick());
        
        createEventLayout.addComponent(createEventNameTextField);
        createEventLayout.addComponent(createEventClientComboBox);
        createEventLayout.addComponent(createEventStartDateField);
        createEventLayout.addComponent(createEventDurrationTextField);
        createEventLayout.addComponent(createEventDurrationComboBox);
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





	private void createEventButtonClick() {
		String eventName = createEventNameTextField.getValue();
	}



	private void populateComboBoxes() {
		
		createEventClientComboBox.removeAllItems();
		createEventClientComboBox.addItems(MasterUi.userDataHolder.getClientMap().keySet());
		
		if (baseTimeList.size()==0) {
		baseTimeList.add("Minutes");
		baseTimeList.add("Hours");
		baseTimeList.add("Days");
		}
		createEventDurrationComboBox.addItems(baseTimeList);
		
		
	}

}
