/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package debugging;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ccrmV.MasterUI;

/**
 * This class is how we will handle debugging.
 * 
 * It will contain various methods / constants that are intended to help with
 * debugging and reduce the amount of console spam.
 * 
 * toggle debug info by switching constants on and off. Write to special log
 * files if that is helpful.
 * 
 * @author Josh Benton
 *
 */
public class Debugging {

	/*
	 * ALL DEBUGGING IS TURNED OFF WHEN NOT IN DEV MODE
	 */
	public static final Boolean DEV_MODE = MasterUI.DEVELOPER_MODE;
	public static final boolean OUTPUT_EVERYTHING = false;
	public static final Boolean ENABLE_GUI_DEBUGGING = false; // hard switch for
																// GUI debugging

	// Output forms
	public static final String CONSOLE_OUTPUT = "Console";// simple console
															// output
	public static final String CONSOLE_ERROR_OUTPUT = "Error";

	public static final String FILE_OUTPUT = "FILE:";// << Expecting a file path
														// after this, if none
														// found, default to
														// DEFAULT_FILE_LOCATION

	public static final String DEFAULT_FILE_LOCATION = "./defaultOutput.txt";
	public static final String HOME_DIRECTORY = System.getProperty("user.home");
	// important paths
	// System.getProperty("user.home")

	public static final String GUI_OUTPUT = "GUI:";// May Require additional
													// arguments, GUI DEBUGGING
													// NOT IMPLEMENTED YET

	public static final String VAADIN_OUTPUT = "Vaadin:";// may require
															// additional
															// arguments NOT
															// IMPLEMENTED YET

	// output variables
	
	public static final String LINE = "--------------------------------------------";

	// output errors related to writing debug files
	private static final String FILE_OUT_ERROR_METHOD = CONSOLE_ERROR_OUTPUT;
	private static final Boolean FILE_OUT_ERROR_ENABLED = true;
	// DO NOT MAKE THIS A FILE OUTPUT, it will infinite loop.
	private static final String FILE_OUT_SUCCESS_METHOD = CONSOLE_OUTPUT;
	private static final Boolean FILE_OUT_SUCCESS_ENABLED = true;

	// ensure the debug functions work
	// the static method must be called to actually test, this just suppresses
	// output.
	private static final Boolean DEBUG_UNIT_TESTING = false;
	private static final String DEBUG_UNIT_CUSTOM_FILE_OUT = FILE_OUTPUT + HOME_DIRECTORY + "/homeOutputTest.txt";
	private static final String DEBUG_UNIT_CUSTOM_DIRECTORY_OUT = FILE_OUTPUT + HOME_DIRECTORY
			+ "/debugDir/debugDirOutputTest.txt";

	// USER EDITOR DEBUGGING
	public static final String USER_EDITOR_OUTPUT = CONSOLE_OUTPUT;
	public static final Boolean USER_EDITOR_OUTPUT_ENABLED = true;

	// CRM Debugging
	public static final String CRM_OUTPUT = CONSOLE_OUTPUT;
	public static final Boolean CRM_OUTPUT_ENABLED = true;

	// Max Object Debugging
	public static final String MAX_OBJECT_OUTPUT = CONSOLE_OUTPUT;
	public static final Boolean MAX_OBJECT_OUTPUT_ENABLED = true;

	// Schedule Event Debugging
	public static final String SCHEDULE_EVENT_OUTPUT = CONSOLE_OUTPUT;
	public static final Boolean SCHEDULE_EVENT_OUTPUT_ENABLED = true;

	// MASTER UI TESTING
	public static final String MASTER_UI_TESTING_OUTPUT = CONSOLE_OUTPUT;
	public static final Boolean MASTER_UI_TESTING_OUTPUT_ENABLED = true;

	// Inventory Item Debug
	public static final String INVENTORY_OUTPUT = CONSOLE_OUTPUT;
	public static final Boolean INVENTORY_OUTPUT_ENABLED = true;

	// Inventory View Debugging
	public static final String INVENTORY_VIEW_OUTPUT = CONSOLE_OUTPUT;
	public static final Boolean INVENTORY_VIEW_OUTPUT_ENABLED = true;

	// Inhaler Utils Debugging
	public static final String INHALER_UTILS_DEBUG = CONSOLE_OUTPUT;
	public static final Boolean INHALER_UTILS_DEBUG_ENABLED = true;

	// Client Field Debugging
	public static final String CLIENT_FIELD_DEBUGGING = CONSOLE_OUTPUT;
	public static final Boolean CLIENT_FIELD_DEBUGGING_ENABLED = true;

	// O-auth2 Debugging
	public static final String OAUTH_OUTPUT = CONSOLE_OUTPUT;
	public static final Boolean OAUTH_OUTPUT_ENABLED = true;
	
	//Josh's Debugging Variables
	public static final String DELETE_OUTPUT = CONSOLE_OUTPUT;
	public static final Boolean DELETE_OUTPUT_ENABLED = true;
	
	public static final String CONVERSION_DEBUG = CONSOLE_OUTPUT;
	public static final Boolean CONVERSION_DEBUG_ENABLED = true;
	
	public static final String CUSTOM_FIELD = CONSOLE_OUTPUT;
	public static final Boolean CUSTOM_FIELD_ENABLED = true;
	
	//Version 2 of debugging variables
	public static final DebugObject FRONT_DESK_DEBUGGING = new DebugObject(CONSOLE_OUTPUT, true, true, "Front Desk Debugging: ");
	public static final DebugObject OAUTH2 = new DebugObject(CONSOLE_OUTPUT, true, true, "OAUTH2: ");
	public static final DebugObject CONVERSION_DEBUG2 = new DebugObject(CONSOLE_OUTPUT, true, true, "Conversion Debug2: ");
	public static final DebugObject TEMPLATE_DEBUG = new DebugObject(CONSOLE_OUTPUT, true, true, "Template Debug: ");
	public static final DebugObject CLIENT_GRID_DEBUG = new DebugObject(CONSOLE_OUTPUT, true, true, "Client Grid: ");
	public static final DebugObject CATEGORY_EDITOR_DEBUG = new DebugObject(CONSOLE_OUTPUT, true, true, "Category Editor: ");
	public static final DebugObject MOBILE_DEBUG = new DebugObject(CONSOLE_OUTPUT, true, true, "Mobile UI: ");
	
	public static final DebugObject GOOGLE_FURY_DEBUG = new DebugObject(CONSOLE_OUTPUT, true, true, "Google Fury: ");
	
	
	public static Collection<DebugObject> debugObjectsInUse = new ArrayList<DebugObject>();
	//Andrews Debugging Variables
	
	
	
	//Troy's Debugging Variables
	
	
	//Mark's Debug Variables
	public static final DebugObject UNIT_TEST_TRACK_CHANGES = new DebugObject(CONSOLE_OUTPUT, true, true, "Unit Test Changes: ");
	
	
	
	//Tony's Debugging Variables
	
	static
	{
		//This should execute when the program starts.
		//static initialization 
	}

	public static void output(String output, String method, Boolean enabled) {
		if (DEV_MODE) {
			if (OUTPUT_EVERYTHING || enabled) {
				if (method.equals(CONSOLE_OUTPUT)) {
					System.out.println(output);
				} else if (method.equals(CONSOLE_ERROR_OUTPUT)) {
					System.err.println(output);
				} else if (method.contains(FILE_OUTPUT)) {
					// file output

					// parse the file location
					String fileLocation = method.replace(FILE_OUTPUT, "");

					// if no file is specified, use the default file
					if (fileLocation.equals("")) {
						fileLocation = DEFAULT_FILE_LOCATION;
					}

					fileOutput(output, fileLocation);

				} else if (method.contains(GUI_OUTPUT)) {

				} else if (method.contains(VAADIN_OUTPUT)) {

				}
			}
		} // end dev mode check
	}

	
	public static void output(String output, DebugObject debugObject) {
		debugObject.output(output);
		//add the debug object ot debug objects in use
		if (!debugObjectsInUse.contains(debugObject)) {
			debugObjectsInUse.add(debugObject);
		}
	}
	
	public static void fileOutput(String output, String filePath) {
		Path file = Paths.get(filePath);

		List<String> lines = Arrays.asList(output.split("\n"));
		try {
			// File createDirFile = new File(file);
			Files.createDirectories(file.getParent());
			Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			output("Wrote debug file: " + filePath, FILE_OUT_SUCCESS_METHOD, FILE_OUT_SUCCESS_ENABLED);
		} catch (IOException e) {
			// TODO Auto-generated catch block

			output("Failed to write file: " + filePath, FILE_OUT_ERROR_METHOD, FILE_OUT_ERROR_ENABLED);
			e.printStackTrace();
		}
		// Files.write(file, lines, Charset.forName("UTF-8"),
		// StandardOpenOption.APPEND);
	}

	/**
	 * Tests the debug methods to ensure they will work
	 */
	public static void debugUnitTesting() {
		// Test console output
		output("Console out test", CONSOLE_OUTPUT, DEBUG_UNIT_TESTING);
		output("Console error test", CONSOLE_ERROR_OUTPUT, DEBUG_UNIT_TESTING);
		// default file output
		output("Test File Output", FILE_OUTPUT, DEBUG_UNIT_TESTING);
		// append file output test
		output("Appending to a file", FILE_OUTPUT, DEBUG_UNIT_TESTING);
		// create a file in the users home directory
		output("Created a file in the users directory", DEBUG_UNIT_CUSTOM_FILE_OUT, DEBUG_UNIT_TESTING);
		// try creating a directory
		output("Created a file in the users custom directory", DEBUG_UNIT_CUSTOM_DIRECTORY_OUT, DEBUG_UNIT_TESTING);
	}
}
