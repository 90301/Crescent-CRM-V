/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */

package debugging;

import java.util.ArrayList;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import ccrmV.CrescentView;
import ccrmV.MasterUI;
import debugging.profiling.MasterTimer;
import uiElements.NavBar;

/**
 * This is the vaadin debug screen. Avaliable only in developer mode This screen
 * seeks to allow Multiple "consoles" for the purposes of outputting
 * information. It will be hidden in production
 * 
 * @author Inhaler
 *
 */
public class DebuggingVaadinUI extends CrescentView{

	private static final boolean RICHTEXT = false;
	//public NavBar navBar;
	//public MasterUI masterUi;

	VerticalLayout buttonLayout = new VerticalLayout();

	HorizontalLayout consoleLayout = new HorizontalLayout();

	ArrayList<TextArea> consoles = new ArrayList<TextArea>();
	ArrayList<Button> logOutputButtons = new ArrayList<Button>();

	ArrayList<Button> richLogOutputButtons = new ArrayList<Button>();
	ArrayList<DebuggingVaadinRichTextConsole> richTextConsoles = new ArrayList<DebuggingVaadinRichTextConsole>();

	
	TextArea mainConsole = null;
	
	static String consoleWidth = "800px";
	static String consoleHeight = "800px";

	{

		mainConsole = createConsole("Main Console");

		DebuggingVaadinRichTextConsole mainRichConsole = new DebuggingVaadinRichTextConsole();

		mainRichConsole.genConsole();

		richTextConsoles.add(mainRichConsole);
		
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

		if (!RICHTEXT) {
			for (TextArea console : consoles) {
				consoleLayout.addComponent(console);
			}
		} else {
			for (DebuggingVaadinRichTextConsole console : richTextConsoles) {
				consoleLayout.addComponent(console);
			}
		}

		buttonLayout.removeAllComponents();
		
		
		for (Button b : logOutputButtons) {
			buttonLayout.addComponent(b);
		}

		for (Button b : richLogOutputButtons) {
			buttonLayout.addComponent(b);
		}

		//this.removeAllComponents();

		//this.addComponent(navBar);

		this.addComponent(buttonLayout);

		this.addComponent(consoleLayout);

	}

	private void genLogButtons() {
		logOutputButtons.clear();

		for (DebugObject debugObj : Debugging.debugObjectsInUse) {
			if (!RICHTEXT) {
				this.createLogOutputButton(debugObj, consoles.get(0));
			} else {
				this.createLogOutputButton(debugObj, richTextConsoles.get(0));
			}
		}
		/*
		for (DebugObject debugObj : Debugging.debugObjectsInUse) {
			this.createLogOutputButton(debugObj, consoles.get(0));
		}
		*/
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

	public void createLogOutputButton(DebugObject obj, DebuggingVaadinRichTextConsole console) {
		Button button = new Button(obj.getName(), e -> outputLog(obj, console));

		richLogOutputButtons.add(button);
	}

	private void outputLog(DebugObject obj, DebuggingVaadinRichTextConsole console) {
		obj.outputLog();
		// System.out.println(obj + " NAME: " + obj.getName());
		console.setValue(obj.getOutput());

	}

	private void outputLog(DebugObject obj, TextArea console) {
		obj.outputLog();
		// System.out.println(obj + " NAME: " + obj.getName());
		console.setValue(obj.getOutput());

	}

}
