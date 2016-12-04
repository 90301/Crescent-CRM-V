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
import com.vaadin.ui.components.calendar.event.CalendarEvent;
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
import uiElements.SchedulerModule;
import users.User;

public class SchedulerView extends HorizontalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5854568268650533061L;
	public MasterUI masterUi;
	public NavBar navBar;

	public HorizontalLayout createEventLayout = new HorizontalLayout();
	public VerticalLayout schedulerLayout = new VerticalLayout();
	public HorizontalLayout frontDeskLayout = new HorizontalLayout();
	public HorizontalLayout singleUserLayout = new HorizontalLayout();

	/*
	 * UI Components
	 */
	Calendar cal = new Calendar();
	private TextField createEventNameTextField = new TextField("Event Name");
	private ComboBox createEventClientComboBox = new ComboBox("Client");

	private ComboBox createEventUserComboBox = new ComboBox("Stylist");

	private DateField createEventStartDateField = new PopupDateField("Event Start");
	private DateField createEventEndDateField = new PopupDateField("Event End");
	private ComboBox repeatComboBox = new ComboBox("Repeat");
	private Button switchToFrontDeskMode = new Button("Front Desk Mode");
	private Button switchToSingleMode = new Button("Single Mode");

	/*
	 * replaced by createEventEndDateField private TextField
	 * createEventDurationTextField = new TextField("Duration"); private
	 * ComboBox createEventDurationComboBox = new ComboBox("Duration Unit");
	 */

	private Button createEventButton = new Button("Create Event");

	public static final ArrayList<String> baseTimeList = new ArrayList<>();

	// public SchedulerModule smTest = new SchedulerModule(this);
	public ArrayList<SchedulerModule> schedulerModules = new ArrayList<SchedulerModule>();
	public Integer frontDeskModeStylists = 3;
	private Boolean frontDeskMode = false;
	
	//Initialization code, please add all event listeners here to prevent
	//Ram leaks and duplicated event calls
	{
		createEventStartDateField.addValueChangeListener(e -> updateEndDate());
		
		createEventButton.addClickListener(click -> createEventButtonClick());
		switchToFrontDeskMode.addClickListener(click -> switchToFrontDeskModeClick());
		switchToSingleMode.addClickListener(click -> switchToSingleModeClick());
		
		// Used for weekly view
		cal.setHandler(new BasicDateClickHandler() {
			public void dateClick(DateClickEvent event) {
				Calendar cal = event.getComponent();

				// Check if the current range is already one day long
				long currentCalDateRange = cal.getEndDate().getTime() - cal.getStartDate().getTime();

				// From one-day view, zoom out to week view
				if (currentCalDateRange <= DateConstants.DAYINMILLIS) {
					// Change the date range to the current week
					GregorianCalendar weekstart = new GregorianCalendar();
					GregorianCalendar weekend = new GregorianCalendar();
					weekstart.setTime(event.getDate());
					weekend.setTime(event.getDate());
					weekstart.setFirstDayOfWeek(java.util.Calendar.SUNDAY);
					weekstart.set(java.util.Calendar.HOUR_OF_DAY, 0);
					weekstart.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY);
					weekend.set(java.util.Calendar.HOUR_OF_DAY, 23);
					weekend.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SATURDAY);
					cal.setStartDate(weekstart.getTime());
					cal.setEndDate(weekend.getTime());

					Notification.show("Custom zoom to week");

				} else {
					// Default behavior, change date range to one day
					super.dateClick(event);
				}
			}
		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		this.setSpacing(true);
		// TODO
		if (!masterUi.loggedIn || masterUi.user == null)
			masterUi.enterLogin();

		createEventLayout.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER);
		frontDeskLayout.setVisible(false);

		if (schedulerModules.size() <= 0) {
			for (int i = 0; i < frontDeskModeStylists; i++) {
				SchedulerModule sm = new SchedulerModule(this);
				sm.genSchedulerModule();
				sm.navigateToDay(new Date());
				frontDeskLayout.addComponent(sm);
				schedulerModules.add(sm);
			}
		}

		// smTest.genSchedulerModule();
		// frontDeskLayout.addComponent(smTest);

		/*
		 * create components here and edit settings added width setting to allow
		 * forward advancing
		 */
		cal.setHeight("800px");
		cal.setWidth("1200px");
		cal.setCaption("Schedule");
		cal.setLocale(Locale.getDefault());
		cal.setImmediate(true);
		// cal.setSizeFull();

		Date startDate = new Date();
		Date endDate = new Date(startDate.getTime() + TimeUnit.HOURS.toMillis(1));

		// date selection
		// createEventStartDateField = ;
		// createEventEndDateField = ;
		String dateTimeFormat = "MM-dd-yyyy h:mm aa";
		createEventStartDateField.setDateFormat(dateTimeFormat);
		createEventStartDateField.setValue(new Date());
		createEventStartDateField.setWidth("220px");


		Date endDate2 = new Date(startDate.getTime() + TimeUnit.HOURS.toMillis(1));
		createEventEndDateField.setDateFormat(dateTimeFormat);
		createEventEndDateField.setValue(endDate2);
		createEventEndDateField.setWidth("220px");

		// Show the popup with minute / hour selectors
		createEventStartDateField.setResolution(Resolution.MINUTE);
		createEventEndDateField.setResolution(Resolution.MINUTE);

		// createEventButton

		createEventLayout.setSpacing(true);
		schedulerLayout.setSpacing(true);

		createEventLayout.addComponent(createEventNameTextField);
		createEventLayout.addComponent(createEventClientComboBox);

		createEventLayout.addComponent(createEventUserComboBox);

		createEventLayout.addComponent(createEventStartDateField);
		createEventLayout.addComponent(createEventEndDateField);
		createEventLayout.addComponent(repeatComboBox);
		createEventLayout.addComponent(createEventButton);
		createEventLayout.addComponent(switchToFrontDeskMode);
		createEventLayout.addComponent(switchToSingleMode);

		populateComboBoxes();
		updateScheduler();
		/*
		 * Add components here
		 */

		this.addComponent(navBar.sidebarLayout);
		// this.addComponent(new Label("Scheduler"));

		// this.addComponent(cal);

		switchToSingleModeClick();

		singleUserLayout.addComponent(cal);

		schedulerLayout.addComponent(createEventLayout);

		schedulerLayout.addComponent(singleUserLayout);
		schedulerLayout.addComponent(frontDeskLayout);

		this.addComponent(schedulerLayout);

		hideUnimplemented();
	}

	/**
	 * A method for hiding ui elements that are not currently fully implemented
	 * for the purposes of demoing
	 */
	private void hideUnimplemented() {
		if (!MasterUI.DEVELOPER_MODE) {
			repeatComboBox.setVisible(false);
		}

	}

	public void updateEndDate() {

		Date endDate3 = new Date(createEventStartDateField.getValue().getTime() + TimeUnit.HOURS.toMillis(1));
		createEventEndDateField.setValue(endDate3);
	}

	// adds the event using the fields when clicked
	private void createEventButtonClick() {

		String eventName = createEventNameTextField.getValue();
		String client = (String) createEventClientComboBox.getValue();
		String currentUser = masterUi.user.getPrimaryKey();

		Date eventStart = createEventStartDateField.getValue();
		Date eventEnd = createEventEndDateField.getValue();

		if (frontDeskMode) {
			currentUser = createEventUserComboBox.getValue().toString();
		}

		// TODO error check to ensure nothing is null

		ScheduleEvent event = new ScheduleEvent();
		event.setEventName(eventName);
		event.setEventDescription(client);
		event.setStart(eventStart);
		event.setEnd(eventEnd);
		event.setUser(currentUser);
		event.genKey();
		DataHolder.store(event, ScheduleEvent.class);
		// BasicEvent event = new BasicEvent(eventName, client, eventStart,
		// eventEnd);
		addEvent(event);
		updateScheduler();
	}

	ArrayList<CalendarEvent> calendarEvents = new ArrayList<CalendarEvent>();

	public void addEvent(ScheduleEvent se) {
		calendarEvents.add(se);
		cal.addEvent(se);
	}

	public void updateScheduler() {
		for (CalendarEvent ce : calendarEvents) {
			cal.removeEvent(ce);
		}
		calendarEvents.clear();
		for (ScheduleEvent se : DataHolder.getMap(ScheduleEvent.class).values()) {
			addEvent(se);
		}

		for (SchedulerModule sm : schedulerModules) {
			sm.fillCalender();
		}
	}

	private void switchToFrontDeskModeClick() {
		setFrontDeskMode(true);
	}

	private void switchToSingleModeClick() {
		setFrontDeskMode(false);
	}

	private void setFrontDeskMode(Boolean frontDeskMode) {
		this.frontDeskMode = frontDeskMode;
		/*
		 * Set's the visibility of items based on weather or not the scheduler
		 * is in front desk mode to have something be visible in front desk mode
		 * (and not visible in single mode), . . . . set visible to
		 * "frontDeskMode" to make something only visible in single mode, . . .
		 * . set visible to "!frontDeskMode" (also known as "not" front desk
		 * mode)
		 */
		singleUserLayout.setVisible(!frontDeskMode);
		frontDeskLayout.setVisible(frontDeskMode);
		switchToSingleMode.setVisible(frontDeskMode);
		switchToFrontDeskMode.setVisible(!frontDeskMode);
		createEventUserComboBox.setVisible(frontDeskMode);
	}

	private void populateComboBoxes() {

		createEventClientComboBox.removeAllItems();
		createEventClientComboBox.addItems(masterUi.userDataHolder.getClientMap().keySet());

		createEventUserComboBox.addItems(DataHolder.getMap(User.class).keySet());

		if (baseTimeList.size() == 0) {
			baseTimeList.add("Minutes");
			baseTimeList.add("Hours");
			baseTimeList.add("Days");
		}
		// createEventDurationComboBox.addItems(baseTimeList);

	}

}
