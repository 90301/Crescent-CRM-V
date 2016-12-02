package uiElements;

import java.util.Date;
import java.util.Locale;

import com.vaadin.ui.Calendar;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;

import ccrmV.SchedulerView;

public class SchedulerModule extends VerticalLayout{
	
	private static final String CAL_HEIGHT = "750px";
	private static final String CAL_WIDTH = "280px";
	
	private SchedulerView schedulerView;
	private ComboBox stylistSelectionComboBox =  new ComboBox("Stylist");
	
	Calendar cal = new Calendar();
	public SchedulerModule(SchedulerView schedulerView) {
		this.schedulerView = schedulerView;
	}
	
	/**
	 * Generates the component and sets variables
	 */
	public void genSchedulerModule(){
		
		
        cal.setHeight(CAL_HEIGHT);
		cal.setWidth(CAL_WIDTH);
		cal.setCaption("Current Month");
		cal.setLocale(Locale.getDefault());
        cal.setImmediate(true);
        
        /*
        cal.setFirstVisibleDayOfWeek(2);
        cal.setLastVisibleDayOfWeek(2);
        */
        
        this.addComponent(stylistSelectionComboBox);
        this.addComponent(cal);
        
		populateComboBoxes();
	}

	private void populateComboBoxes() {
		
		
	}
	
	public void navigateToDay(Date date) {
		cal.setStartDate(date);
		cal.setEndDate(date);
	}
	
	
}
