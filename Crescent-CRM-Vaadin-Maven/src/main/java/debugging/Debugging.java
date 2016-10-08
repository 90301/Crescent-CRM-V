package debugging;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;


import ccrmV.MasterUI;

/**
 * This class is how we will handle debugging.
 * 
 * It will contain various methods / constants
 * that are intended to help with debugging and
 * reduce the amount of console spam.
 * 
 * toggle debug info by switching constants on and off.
 * Write to special log files if that is helpful.
 * @author Josh Benton
 *
 */
public class Debugging {
	
	/*
	 * ALL DEBUGGING IS TURNED OFF WHEN NOT IN DEV MODE
	 */
	public static final Boolean DEV_MODE = MasterUI.DEVELOPER_MODE;
	public static final boolean OUTPUT_EVERYTHING = false;
	public static final Boolean ENABLE_GUI_DEBUGGING = false; //hard switch for GUI debugging
	
	
	//Output forms
	public static final String CONSOLE_OUTPUT = "Console";//simple console output
	public static final String CONSOLE_ERROR_OUTPUT = "Error";
		
	public static final String FILE_OUTPUT = "FILE:";//<< Expecting a file path after this, if none found, default to DEFAULT_FILE_LOCATION 
	
	public static final String DEFAULT_FILE_LOCATION = "./defaultOutput.txt";
	public static final String HOME_DIRECTORY = System.getProperty("user.home");
	//important paths
	//System.getProperty("user.home")
	
	
	
	public static final String GUI_OUTPUT = "GUI:";//May Require additional arguments, GUI DEBUGGING NOT IMPLEMENTED YET
	
	public static final String VAADIN_OUTPUT = "Vaadin:";//may require additional arguments NOT IMPLEMENTED YET

	
	//output variables
	
	//output errors related to writing debug files
	private static final String FILE_OUT_ERROR_METHOD = CONSOLE_ERROR_OUTPUT;
	private static final Boolean FILE_OUT_ERROR_ENABLED = true;
	//DO NOT MAKE THIS A FILE OUTPUT, it will infinite loop.
	private static final String FILE_OUT_SUCSESS_METHOD = CONSOLE_OUTPUT;
	private static final Boolean FILE_OUT_SUCSESS_ENABLED = true;
	
	
	//ensure the debug functions work
	//the static method must be called to actually test, this just suppresses output.
	private static final Boolean DEBUG_UNIT_TESTING = false;
	private static final String DEBUG_UNIT_CUSTOM_FILE_OUT = FILE_OUTPUT + HOME_DIRECTORY + "/homeOutputTest.txt";
	private static final String DEBUG_UNIT_CUSTOM_DIRECTORY_OUT = FILE_OUTPUT + HOME_DIRECTORY + "/debugDir/debugDirOutputTest.txt";
	

	
	
	
	
	public static void output(String output,String method,Boolean enabled) {
		if (DEV_MODE) {
			if (OUTPUT_EVERYTHING || enabled) {
				if (method.equals(CONSOLE_OUTPUT)) {
					System.out.println(output);
				} else if (method.equals(CONSOLE_ERROR_OUTPUT)) {
					System.err.println(output);
				} else if (method.contains(FILE_OUTPUT)) {
					//file output
					
					//parse the file location
					String fileLocation = method.replace(FILE_OUTPUT, "");
					
					//if no file is specified, use the default file
					if (fileLocation.equals("")) {
						fileLocation = DEFAULT_FILE_LOCATION;
					}
					
					
					fileOutput(output,fileLocation);
					
					
				} else if (method.contains(GUI_OUTPUT)) {
					
				} else if (method.contains(VAADIN_OUTPUT)) {
					
				}
			}
		}//end dev mode check
	}
	
	public static void fileOutput(String output,String filePath) {
		Path file = Paths.get(filePath);
		
		List<String> lines =  Arrays.asList(output.split("\n"));
		try {
			//File createDirFile = new File(file);
			Files.createDirectories(file.getParent());
			Files.write(file, lines, Charset.forName("UTF-8"),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
			output("Wrote debug file: " + filePath,FILE_OUT_SUCSESS_METHOD,FILE_OUT_SUCSESS_ENABLED);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			output("Failed to write file: " + filePath,FILE_OUT_ERROR_METHOD,FILE_OUT_ERROR_ENABLED);
			e.printStackTrace();
		}
		//Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
	}
	
	/**
	 * Tests the debug methods to ensure they will work
	 */
	public static void debugUnitTesting() {
		//Test console output
		output("Console out test",CONSOLE_OUTPUT,DEBUG_UNIT_TESTING);
		output("Console error test",CONSOLE_ERROR_OUTPUT,DEBUG_UNIT_TESTING);
		//default file output
		output("Test File Output", FILE_OUTPUT, DEBUG_UNIT_TESTING);
		//append file output test
		output("Appending to a file", FILE_OUTPUT, DEBUG_UNIT_TESTING);
		//create a file in the users home directory
		output("Created a file in the users directory", DEBUG_UNIT_CUSTOM_FILE_OUT, DEBUG_UNIT_TESTING);
		//try creating a directory
		output("Created a file in the users custom directory", DEBUG_UNIT_CUSTOM_DIRECTORY_OUT, DEBUG_UNIT_TESTING);
	}
}
