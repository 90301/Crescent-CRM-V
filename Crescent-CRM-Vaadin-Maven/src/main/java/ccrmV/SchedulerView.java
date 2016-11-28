/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package ccrmV;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.vaadin.navigator.Navigator;
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
import clientInfo.UserDataHolder;

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
	public VerticalLayout frontDeskLayout;
    public VerticalLayout stylist1Layout;	
    public VerticalLayout stylist2Layout;	
    public VerticalLayout stylist3Layout;	

	
	
	/*
	 * UI Components
	 */
	Calendar cal = new Calendar();
	private TextField createEventNameTextField = new TextField("Event Name");
	private ComboBox createEventClientComboBox =  new ComboBox("Client");
	private DateField createEventStartDateField = new PopupDateField("Event Start");
	private DateField createEventEndDateField = new PopupDateField("Event End");
	private ComboBox repeatComboBox = new ComboBox("Repeat");
	private Button switchToFrontDeskMode = new Button("Front Desk Mode");
	
	private ComboBox stylistComboBox =  new ComboBox("Stylist 1");
	private ComboBox stylistComboBox2 =  new ComboBox("Stylist 2");
	private ComboBox stylistComboBox3 =  new ComboBox("Stylist 3");
	
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
		frontDeskLayout = new VerticalLayout();
		stylist1Layout = new VerticalLayout();
		stylist2Layout = new VerticalLayout();
		stylist3Layout = new VerticalLayout();
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
        Date endDate = new Date(startDate.getTime() + TimeUnit.HOURS.toMillis(1));
        
        /*TODO fix this code displaying 5 days  -- not needed due to the implementation of weekly view below
        endDate.setDate(startDate.getDate()+5);
        cal.setStartDate(new Date()); 
        cal.setEndDate(new Date());
        */
        
        //createEventNameTextField = ;
        
        //createEventClientComboBox =
        
        // Creates the first monthly calendar
        
        Calendar cal2 = new Calendar();
        cal2.setHeight("200px");
		cal2.setWidth("200px");
		cal2.setCaption("Current Month");
		cal2.setLocale(Locale.getDefault());
        cal2.setImmediate(true);
        
        GregorianCalendar gCal = new GregorianCalendar(cal2.getLocale());
        
        final int rollAmount = gCal.get(GregorianCalendar.DAY_OF_MONTH) - 1;
        gCal.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount);
    //    resetTime(false);
        Date currentMonthsFirstDate = gCal.getTime();
        cal2.setStartDate(currentMonthsFirstDate);
        gCal.add(GregorianCalendar.MONTH, 1);
        gCal.add(GregorianCalendar.DATE, -1);
        cal2.setEndDate(gCal.getTime());
        
        
        
        Calendar cal3 = new Calendar();
        cal3.setHeight("400px");
		cal3.setWidth("400px");
		cal3.setCaption("Current Month");
		cal3.setLocale(Locale.getDefault());
        cal3.setImmediate(true);
        
        GregorianCalendar gCal3 = new GregorianCalendar(cal2.getLocale());
        
        final int rollAmount3 = gCal.get(GregorianCalendar.DAY_OF_MONTH) - 1;
        gCal3.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount3);
    //    resetTime(false);
        Date currentMonthsFirstDate3 = gCal3.getTime();
        cal3.setStartDate(currentMonthsFirstDate3);
        gCal3.add(GregorianCalendar.MONTH, 1);
        gCal3.add(GregorianCalendar.DATE, -1);
        cal3.setEndDate(gCal3.getTime());
        
        Calendar cal4 = new Calendar();
        cal4.setHeight("400px");
		cal4.setWidth("400px");
		cal4.setCaption("Current Month");
		cal4.setLocale(Locale.getDefault());
        cal4.setImmediate(true);
        
        
        Calendar cal5 = new Calendar();
        cal5.setHeight("400px");
		cal5.setWidth("400px");
		cal5.setCaption("Current Month");
		cal5.setLocale(Locale.getDefault());
        cal5.setImmediate(true);
        
        //date selection
        //createEventStartDateField = ;
        //createEventEndDateField = ;
        String dateTimeFormat = "MM-dd-yyyy h:mm aa";
		createEventStartDateField.setDateFormat(dateTimeFormat);
        createEventStartDateField.setValue(new Date());
        createEventStartDateField.setWidth("220px");
        createEventStartDateField.addValueChangeListener(e -> updateEndDate());
        
        Date endDate2 = new Date(startDate.getTime() + TimeUnit.HOURS.toMillis(1));
        createEventEndDateField.setDateFormat(dateTimeFormat);
        createEventEndDateField.setValue(endDate2);
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
        
        
        
      //Used for weekly view
        cal2.setHandler(new BasicDateClickHandler() 
        {
            public void dateClick(DateClickEvent event) 
            {
              Calendar cal2 = event.getComponent();

              // Check if the current range is already one day long
              long currentCalDateRange2 = cal2.getEndDate().getTime() - cal2.getStartDate().getTime();

              // From one-day view, zoom out to week view
              if (currentCalDateRange2 <= DateConstants.DAYINMILLIS) 
              {
                  // Change the date range to the current week
                  GregorianCalendar weekstart2 = new GregorianCalendar();
                  GregorianCalendar weekend2   = new GregorianCalendar();
                  weekstart2.setTime(event.getDate());
                  weekend2.setTime(event.getDate());
                  weekstart2.setFirstDayOfWeek(java.util.Calendar.SUNDAY);
                  weekstart2.set(java.util.Calendar.HOUR_OF_DAY, 0);
                  weekstart2.set(java.util.Calendar.DAY_OF_WEEK,
                               java.util.Calendar.SUNDAY);
                  weekend2.set(java.util.Calendar.HOUR_OF_DAY, 23);
                  weekend2.set(java.util.Calendar.DAY_OF_WEEK,
                               java.util.Calendar.SATURDAY);
                  cal2.setStartDate(weekstart2.getTime());
                  cal2.setEndDate(weekend2.getTime());

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
        switchToFrontDeskMode.addClickListener(click -> switchToFrontDeskModeClick());
        
        createEventLayout.addComponent(createEventNameTextField);
        createEventLayout.addComponent(createEventClientComboBox);
        createEventLayout.addComponent(createEventStartDateField);
        createEventLayout.addComponent(createEventEndDateField);
        createEventLayout.addComponent(repeatComboBox);
        createEventLayout.addComponent(createEventButton);
        createEventLayout.addComponent(switchToFrontDeskMode);
        
        
        stylist1Layout.addComponent(stylistComboBox);
        stylist2Layout.addComponent(stylistComboBox2);
        stylist3Layout.addComponent(stylistComboBox3);
   //     fdHLayout.addComponent(cal3);
//        fdHLayout.addComponent(cal4);
 //       fdHLayout.addComponent(cal5);
        
        populateComboBoxes();
        
		/*
		 * Add components here
		 */
		
		this.addComponent(navBar.sidebarLayout);
		//this.addComponent(new Label("Scheduler"));
		
		//this.addComponent(cal);
		schedulerLayout.addComponent(createEventLayout);
		schedulerLayout.addComponent(cal);
		
//		frontDeskLayout.addComponent(schedulerLayout);
//		frontDeskLayout.addComponent(cal);
		frontDeskLayout.addComponent(cal2);
		
		stylist1Layout.addComponent(cal3);
		stylist2Layout.addComponent(cal4);
		stylist3Layout.addComponent(cal5);

		
		this.addComponent(schedulerLayout);
		this.addComponent(frontDeskLayout);
		this.addComponent(stylist1Layout);
		this.addComponent(stylist2Layout);
		this.addComponent(stylist3Layout);
	}




    public void updateEndDate() {
    	
		Date endDate3 = new Date(createEventStartDateField.getValue().getTime() + TimeUnit.HOURS.toMillis(1));
		createEventEndDateField.setValue(endDate3);
	}




	//adds the event using the fields when clicked
	private void createEventButtonClick() {
		
		String eventName = createEventNameTextField.getValue();
		String client = (String) createEventClientComboBox.getValue();
		String currentUser = MasterUi.user.getPrimaryKey();
		Date eventStart = createEventStartDateField.getValue();
		Date eventEnd =  createEventEndDateField.getValue();
		
		ScheduleEvent event = new ScheduleEvent();
		event.setEventName(eventName);
		event.setEventDescription(client);
		event.setStart(eventStart);
		event.setEnd(eventEnd);
		event.setUser(currentUser);
		event.genKey();
		DataHolder.store(event, ScheduleEvent.class);
		//BasicEvent event = new BasicEvent(eventName, client, eventStart, eventEnd);
		cal.addEvent(event.genBasicEvent());
	}


	private void switchToFrontDeskModeClick(){
		/**
		frontDeskLayout.addComponent(createEventNameTextField);
		frontDeskLayout.addComponent(createEventClientComboBox);
        frontDeskLayout.addComponent(createEventStartDateField);
        frontDeskLayout.addComponent(createEventEndDateField);
        frontDeskLayout.addComponent(repeatComboBox);
        frontDeskLayout.addComponent(createEventButton);
        frontDeskLayout.addComponent(switchToFrontDeskMode);
        **/
		
		//frontDeskLayout.addComponent(cal);
		createEventLayout.setVisible(false);
		schedulerLayout.setVisible(false);
		frontDeskLayout.setVisible(true);
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
