package uiElements;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.vaadin.ui.Calendar;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;

import ccrmV.SchedulerView;
import clientInfo.DataHolder;
import clientInfo.ScheduleEvent;
import debugging.DebugObject;
import debugging.Debugging;
import users.User;

public class SchedulerModule extends VerticalLayout{
	
	private static final String CAL_HEIGHT = "750px";
	private static final String CAL_WIDTH = "280px";
	
	private SchedulerView schedulerView;
	private ComboBox stylistSelectionComboBox =  new ComboBox("Stylist");
	
	Calendar cal = new Calendar();
	
	User selectedUser;
	
	//an Array List to hold all the calendar events being displayed, because vaadin was too
	//lazy to give us this list directly.
	ArrayList<ScheduleEvent> scheduleEventList = new ArrayList<ScheduleEvent>();
	public SchedulerModule(SchedulerView schedulerView) {
		this.setSchedulerView(schedulerView);
	}
	
	/**
	 * Generates the component and sets variables
	 */
	public void genSchedulerModule(){
		
		cal.setCaption("----");
        cal.setHeight(CAL_HEIGHT);
		cal.setWidth(CAL_WIDTH);
		cal.setCaption("Current Month");
		cal.setLocale(Locale.getDefault());
        cal.setImmediate(true);
        
        /*
        cal.setFirstVisibleDayOfWeek(2);
        cal.setLastVisibleDayOfWeek(2);
        */
        
        //right click menu.
        //stylistSelectionComboBox.addContextClickListener(e -> stylistSelectedClicked());
        stylistSelectionComboBox.setImmediate(true);
        
        stylistSelectionComboBox.addValueChangeListener(e -> stylistSelectedClicked());
        
        
        this.addComponent(stylistSelectionComboBox);
        this.addComponent(cal);
        
		populateComboBoxes();
		
		
	}

	private void stylistSelectedClicked() {
		selectUser((User) stylistSelectionComboBox.getValue());
	}

	public void selectUser(User u) {
		Debugging.output("Front Desk Mode: Selected user:" + u , Debugging.FRONT_DESK_DEBUGGING);
		if (u==null) {
			//null check
			return;
		}
		this.selectedUser = u;
		fillCalender();
		
	}
	
	/**
	 * Attempts to fill the calendar with events for the selected user
	 */
	public void fillCalender() {
		Debugging.output("Front Desk Mode: Filling Calendar." , Debugging.FRONT_DESK_DEBUGGING);
		if (this.selectedUser==null) {
			Debugging.output("Front Desk Mode: null user selected." , Debugging.FRONT_DESK_DEBUGGING);
			return;
		}
		clearAllEvents();
		//loop through all calendar events
		//display only those that have the same user as the selected user
		for (ScheduleEvent se : DataHolder.getMap(ScheduleEvent.class).values()) {
			Debugging.output("Front Desk Mode: Fould Schedule Event:" + se , Debugging.FRONT_DESK_DEBUGGING);
			if (se.getUser().equals(this.selectedUser.getPrimaryKey())) {
				//display the event
				this.addEvent(se);
			}
		}
		
	}
	
	public void addEvent(ScheduleEvent se) {
		this.cal.addEvent(se);
		this.scheduleEventList.add(se);
	}
	
	public void clearAllEvents() {
		for (ScheduleEvent se : scheduleEventList) {
			cal.removeEvent(se);
		}
		scheduleEventList.clear();
	}

	private void populateComboBoxes() {
		stylistSelectionComboBox.clear();
		stylistSelectionComboBox.removeAllItems();
		//fill stylist (user) combo box
		for (User u : DataHolder.getMap(User.class).values()) {
			stylistSelectionComboBox.addItem(u);
		}
	}
	
	public void navigateToDay(Date date) {
		cal.setStartDate(date);
		cal.setEndDate(date);
	}

	public SchedulerView getSchedulerView() {
		return schedulerView;
	}

	public void setSchedulerView(SchedulerView schedulerView) {
		this.schedulerView = schedulerView;
	}
	
	
}
