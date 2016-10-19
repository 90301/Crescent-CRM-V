package ccrmV;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.calendar.DateConstants;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.DateClickEvent;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import com.vaadin.ui.components.calendar.handler.BasicDateClickHandler;

import clientInfo.DataHolder;
import clientInfo.ScheduleEvent;

import com.vaadin.ui.Calendar;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
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
	private DateField createEventEndDateField = new PopupDateField("Event End");
	
	/* replaced by createEventEndDateField
	private TextField createEventDurationTextField  = new TextField("Duration");
	private ComboBox createEventDurationComboBox = new ComboBox("Duration Unit");
	*/
	
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
		 * added width setting to allow forward advancing
		 */
		cal.setHeight("800px");
		cal.setWidth("1200px");
		cal.setCaption("Schedule");
		cal.setLocale(Locale.getDefault());
        cal.setImmediate(true);
        //cal.setSizeFull();
        
        Date startDate = new Date();
        Date endDate = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
        
        /*TODO fix this code displaying 5 days  -- not needed due to the implementation of weekly view below
        endDate.setDate(startDate.getDate()+5);
        cal.setStartDate(new Date()); 
        cal.setEndDate(new Date());
        */
        
        //createEventNameTextField = ;
        
        //createEventClientComboBox =
        
        
        //date selection
        //createEventStartDateField = ;
        //createEventEndDateField = ;
        String dateTimeFormat = "MM-dd-yyyy HH:mm aa";
		createEventStartDateField.setDateFormat(dateTimeFormat);
        createEventStartDateField.setValue(new Date());
        createEventStartDateField.setWidth("220px");
        
        createEventEndDateField.setDateFormat(dateTimeFormat);
        createEventEndDateField.setValue(endDate);
        createEventEndDateField.setWidth("220px");
        
         //Show the popup with minute / hour  selectors
        createEventStartDateField.setResolution(Resolution.MINUTE);
        createEventEndDateField.setResolution(Resolution.MINUTE);

        //Used for weekly view
        cal.setHandler(new BasicDateClickHandler() 
        {
            public void dateClick(DateClickEvent event) 
            {
              Calendar cal = event.getComponent();

              // Check if the current range is already one day long
              long currentCalDateRange = cal.getEndDate().getTime() - cal.getStartDate().getTime();

              // From one-day view, zoom out to week view
              if (currentCalDateRange <= DateConstants.DAYINMILLIS) 
              {
                  // Change the date range to the current week
                  GregorianCalendar weekstart = new GregorianCalendar();
                  GregorianCalendar weekend   = new GregorianCalendar();
                  weekstart.setTime(event.getDate());
                  weekend.setTime(event.getDate());
                  weekstart.setFirstDayOfWeek(java.util.Calendar.SUNDAY);
                  weekstart.set(java.util.Calendar.HOUR_OF_DAY, 0);
                  weekstart.set(java.util.Calendar.DAY_OF_WEEK,
                               java.util.Calendar.SUNDAY);
                  weekend.set(java.util.Calendar.HOUR_OF_DAY, 23);
                  weekend.set(java.util.Calendar.DAY_OF_WEEK,
                               java.util.Calendar.SATURDAY);
                  cal.setStartDate(weekstart.getTime());
                  cal.setEndDate(weekend.getTime());

                  Notification.show("Custom zoom to week");
                  
              } else 
              	{
            	  // Default behavior, change date range to one day
            	  super.dateClick(event);
              	}
            }
          });
        
        
        //createEventDurationTextField
        //createEventDurationTextField.setWidth("80px");
        
        //createEventDurationComboBox 
        //createEventDurationComboBox.setWidth("120px");
        
        //createEventButton
        createEventButton.addClickListener(click -> createEventButtonClick());
        
        createEventLayout.addComponent(createEventNameTextField);
        createEventLayout.addComponent(createEventClientComboBox);
        createEventLayout.addComponent(createEventStartDateField);
        createEventLayout.addComponent(createEventEndDateField);
        createEventLayout.addComponent(createEventButton);
 //     createEventLayout.addComponent(createEventDurationTextField);
 //     createEventLayout.addComponent(createEventDurationComboBox);
        
        
        
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




    //adds the event using the fields when clicked
	private void createEventButtonClick() {
		
		String eventName = createEventNameTextField.getValue();
		String client = (String) createEventClientComboBox.getValue();
		Date eventStart = createEventStartDateField.getValue();
		Date eventEnd =  createEventEndDateField.getValue();
		
		ScheduleEvent event = new ScheduleEvent();
		event.setEventName(eventName);
		event.setEventDescription(client);
		event.setStart(eventStart);
		event.setEnd(eventEnd);
		event.genKey();
		DataHolder.store(event, ScheduleEvent.class);
		//BasicEvent event = new BasicEvent(eventName, client, eventStart, eventEnd);
		cal.addEvent(event.genBasicEvent());
	}



	private void populateComboBoxes() {
		
		createEventClientComboBox.removeAllItems();
		createEventClientComboBox.addItems(MasterUi.userDataHolder.getClientMap().keySet());
		
		if (baseTimeList.size()==0) {
		baseTimeList.add("Minutes");
		baseTimeList.add("Hours");
		baseTimeList.add("Days");
		}
	//	createEventDurationComboBox.addItems(baseTimeList);
		
		
	}

}
