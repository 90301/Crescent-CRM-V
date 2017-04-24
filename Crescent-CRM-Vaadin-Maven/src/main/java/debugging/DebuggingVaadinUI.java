/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */

package debugging;

import java.util.ArrayList;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import ccrmV.CrescentView;
import dbUtils.InhalerUtils;
import debugging.profiling.MasterTimer;

/**
 * This is the vaadin debug screen. Avaliable only in developer mode This screen
 * seeks to allow Multiple "consoles" for the purposes of outputting
 * information. It will be hidden in production
 * 
 * @author Inhaler
 *
 */
public class DebuggingVaadinUI extends CrescentView{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	VerticalLayout buttonLayout = new VerticalLayout();

	HorizontalLayout consoleLayout = new HorizontalLayout();

	ArrayList<TextArea> consoles = new ArrayList<TextArea>();
	ArrayList<Button> logOutputButtons = new ArrayList<Button>();

	ArrayList<Button> richLogOutputButtons = new ArrayList<Button>();
	
	//ArrayList<DebuggingVaadinRichTextConsole> richTextConsoles = new ArrayList<DebuggingVaadinRichTextConsole>();

	
	TextArea mainConsole = null;
	
	VerticalLayout searchLayout = new VerticalLayout();
	
	TextField searchField = new TextField("Search Debug");
	CheckBox searchLines = new CheckBox("Search Lines");
	CheckBox searchAll = new CheckBox("Search Everything");
	
	Button searchButton = new Button("Search", e->searchDebug());
	
	DebugObject selectedDebugObject;
	
	static String consoleWidth = "800px";
	static String consoleHeight = "800px";

	{

		mainConsole = createConsole("Main Console");
		
		buttonLayout.setSpacing(true);
	}

	@Override
	public void enterView(ViewChangeEvent event) {
		
		MasterTimer.outputAllTimers();
		
		Styles styles = Page.getCurrent().getStyles();
		styles.add(".consoleOutput {font-family: monospace; }" );
		
		styles.add(".v-textarea-consoleOutput {font-family: monospace; }" );
		
		styles.add(".v-app .v-textarea { font-family:" + "monospace" + "; }");
		
		mainConsole.addStyleName("consoleOutput");

		genLogButtons();

		consoleLayout.removeAllComponents();

		
			for (TextArea console : consoles) {
				consoleLayout.addComponent(console);
			}
		

		buttonLayout.removeAllComponents();
		
		
		for (Button b : logOutputButtons) {
			buttonLayout.addComponent(b);
		}

		for (Button b : richLogOutputButtons) {
			buttonLayout.addComponent(b);
		}
		
		searchLayout.addComponent(searchField);
		searchLayout.addComponent(searchAll);
		searchLayout.addComponent(searchLines);
		searchLayout.addComponent(searchButton);

		this.addComponent(buttonLayout);
		
		this.addComponent(searchLayout);

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
		console.setValue(obj.getOutput());
		
		selectedDebugObject = obj;

	}
	
	/**
	 * Searches debug objects to find console output containing the input
	 */
	private void searchDebug() {
		String searchString = searchField.getValue();
		if (searchString!=null && !searchString.equals("")) {
			//filter text

			if (searchAll.getValue()) {
				//TODO implement search through all
			} else {
				if (selectedDebugObject==null) {
					//can't search through null object
					return;
				}
				boolean blockSearch = selectedDebugObject.blocks.size()>=1;
				boolean lineSearch = searchLines.getValue();
				
				if (blockSearch && !lineSearch) {
					//perform a block search
					String output = selectedDebugObject.getBlockFilteredOutput(searchString);
					mainConsole.setValue(output);
				} else {
					//get all lines and search
					String output = selectedDebugObject.getLineFilteredOutput(searchString);
					mainConsole.setValue(output);
				}
				
				
				
				
			}
			
			
		} else {
			//display regular text if no filter is found
			if (selectedDebugObject!=null) {
				mainConsole.setValue(selectedDebugObject.getOutput());
			}
		}
	}
}
