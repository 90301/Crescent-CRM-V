/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */

package debugging;

import java.util.ArrayList;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import ccrmV.MasterUI;
import uiElements.NavBar;

/**
 * This is the vaadin debug screen. Avaliable only in developer mode This screen
 * seeks to allow Multiple "consoles" for the purposes of outputting
 * information. It will be hidden in production
 * 
 * @author Inhaler
 *
 */
public class DebuggingVaadinUI extends HorizontalLayout implements View {

	public NavBar navBar;
	public MasterUI masterUi;

	VerticalLayout buttonLayout = new VerticalLayout();

	HorizontalLayout consoleLayout = new HorizontalLayout();

	ArrayList<TextArea> consoles = new ArrayList<TextArea>();
	ArrayList<Button> logOutputButtons = new ArrayList<Button>();
	
	static String consoleWidth = "800px";
	static String consoleHeight = "800px";

	{

		TextArea mainConsole = createConsole("Main Console");
		
		/*
		createLogOutputButton(Debugging.CLIENT_GRID_DEBUG, mainConsole);
		createLogOutputButton(Debugging.FRONT_DESK_DEBUGGING, mainConsole);
		createLogOutputButton(Debugging.OAUTH2, mainConsole);
		createLogOutputButton(Debugging.CONVERSION_DEBUG2, mainConsole);
		createLogOutputButton(Debugging.TEMPLATE_DEBUG, mainConsole);
		*/
	}

	@Override
	public void enter(ViewChangeEvent event) {
		
		//this.setSizeFull();
		//consoleLayout.setWidth("80%");
		//buttonLayout.setWidth("10%");
		//consoleLayout.setWidth("100%");
		
		genLogButtons();
		
		consoleLayout.removeAllComponents();
		for (TextArea console : consoles) {
			consoleLayout.addComponent(console);
		}

		buttonLayout.removeAllComponents();
		for (Button b : logOutputButtons) {
			buttonLayout.addComponent(b);
		}
		
		
		
		this.removeAllComponents();

		this.addComponent(navBar.sidebarLayout);

		this.addComponent(buttonLayout);

		this.addComponent(consoleLayout);

	}

	private void genLogButtons() {
		logOutputButtons.clear();
		
		for (DebugObject debugObj : Debugging.debugObjectsInUse) {
			this.createLogOutputButton(debugObj, consoles.get(0));
		}
	}

	public TextArea createConsole(String consoleName) {
		TextArea console = new TextArea(consoleName);
		
		console.setWidth(consoleWidth);
		console.setHeight(consoleHeight);
		
		consoles.add(console);

		return console;
	}

	public void createLogOutputButton(DebugObject obj, TextArea console) {
		Button button = new Button(obj.getName(), e -> outputLog(obj, console));

		logOutputButtons.add(button);
	}

	private void outputLog(DebugObject obj, TextArea console) {
		obj.outputLog();
		//System.out.println(obj + " NAME: " + obj.getName());
		console.setValue(obj.debugLog);
	}

}
