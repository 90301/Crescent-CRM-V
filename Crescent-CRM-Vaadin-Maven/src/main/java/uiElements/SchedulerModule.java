package uiElements;

import java.util.Locale;

import com.vaadin.ui.Calendar;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;

import ccrmV.SchedulerView;

public class SchedulerModule extends VerticalLayout{
	private SchedulerView schedulerView;
	private ComboBox stylistSelectionComboBox =  new ComboBox("Stylist");
	
	Calendar cal = new Calendar();
	public SchedulerModule(SchedulerView schedulerView) {
		this.schedulerView = schedulerView;
	}
	public void genSchedulerModule(){
		
		
        cal.setHeight("800px");
		cal.setWidth("400px");
		cal.setCaption("Current Month");
		cal.setLocale(Locale.getDefault());
        cal.setImmediate(true);
        
        cal.setFirstVisibleDayOfWeek(2);
        cal.setLastVisibleDayOfWeek(2);
        
        this.addComponent(stylistSelectionComboBox);
        this.addComponent(cal);
        
		populateComboBoxes();
	}

	private void populateComboBoxes() {
		
		
	}
	
	
}
