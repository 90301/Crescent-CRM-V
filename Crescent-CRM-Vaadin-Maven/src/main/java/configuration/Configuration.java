package configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import dbUtils.InhalerUtils;
import debugging.Debugging;

/**
 * This class loads an XML configuration at a predetermined location, if it's not there one
 * is generated with default values
 * This class is also used to reference said settings.
 * @author Joshua Benton
 * (c) 2017 Joshua Benton All Rights Reserved
 */
public class Configuration {
	
	public static final String CONFIG_LOCATION = System.getProperty("user.home")+"/StyleConnectConfig.cfg";
	
	
	public static final String DEVELOPER_MODE_OVERRIDE_KEY = "DeveloperModeOverride";
	public static final String DOMAIN_NAME_KEY = "DomainName";
	
	
	public static Boolean loadMutex = false;
	public static Boolean loadComplete = false;
	
	public static HashMap<String,String> loadedConfig = new HashMap<String,String>();
	
	//Used for update purposes as well as creating a new config
	public static HashMap<String,String> defaultConfig = new HashMap<String,String>();
	
	/**
	 * Attempts to load a config file.
	 * If one can not be loaded this method creates a config file.
	 */
	public static void loadConfig() {
		//ensure config is not already loaded and not currently loading
		if (!loadMutex && !loadComplete) { 
		//check to see if file exists
		File configFile = new File(CONFIG_LOCATION); 
		
		if (configFile.exists()) {
			//attempt loading the file
			BufferedReader configReader = null;
			try {
				configReader = new BufferedReader(new FileReader(configFile));
				String line = "";
				String configXml = "";
				while ((line = configReader.readLine()) != null) {
					configXml += line;
					
				}
				
				//parse the XML
				
				loadedConfig = InhalerUtils.xmlToMap(configXml);
				
				outputConfig();
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				
				try {
				if (configReader != null) {
					
						configReader.close();
					
				}
			} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			
				//end config file exists
		} else {
			//config file doesn't exist
			setupDefaultConfig();
			
			writeConfig(configFile, defaultConfig);
			
			loadedConfig = defaultConfig;
		}
		
				
			loadComplete = true;
		}
	}
	
	private static void outputConfig() {
		
		for (String key : loadedConfig.keySet()) {
		Debugging.output(key + " : "  + loadedConfig.get(key), Debugging.CONFIG_DEBUG);
		}
	}

	/**
	 * Creates a config file at a particular location or overwrites
	 * the existing file.
	 * @param configFile to write
	 * @param config map to write
	 */
	public static void writeConfig(File configFile,HashMap<String,String> config) {
		
		BufferedWriter configWriter = null;
		try {
			
			configWriter = new BufferedWriter(new FileWriter(configFile));
			
			
			
			String xml = InhalerUtils.mapToXML(config);
			
			configWriter.write(xml);
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (configWriter != null) {
				try {
					configWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		
		
		
	}
	
	public static void setupDefaultConfig() {
		defaultConfig.clear();
		
		defaultConfig.put(DEVELOPER_MODE_OVERRIDE_KEY, ""+ false);
		defaultConfig.put(DOMAIN_NAME_KEY, "localhost");
		
		
		
		
	}
}
