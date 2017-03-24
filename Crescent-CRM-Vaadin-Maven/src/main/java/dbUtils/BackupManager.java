/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package dbUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;

//import org.apache.commons.io.FileUtils;

import clientInfo.DataHolder;

import java.util.zip.*;

/**
 * Static class for backing up and restoring.
 * 
 * @author bentonjc
 *
 */
@Deprecated
public class BackupManager {

	private static final String END_OF_LINE_DELIMITER = "<Next>";
	private static final String DELIMITER = "<Del>";
	private static final String BACKUP_DIR = "/ccrmBackups/";
	private static final String PRE_ZIP_DIR = "preZip/";
	private static final String ZIP_EXTENSION = ".zip";
	private static final String CSV_EXTENSION = ".csv";
	private static final int COMPRESSION_LEVEL = 1;
	private static final int BUFFER_SIZE = 1024;
	private static final String RESTORE_FOLDER = "restore/";
	private static final boolean LOCAL_ONLY_RESTORE = false;

	/*
	public static <T extends MaxObject> void backupToCSV(String fileNameAddion, Collection<T> col) {
		// create MAP (arraylist is ordered by nature, if nothing is found
		// A blank value MUST be entered.

		String destination = BACKUP_DIR + PRE_ZIP_DIR + fileNameAddion;
		// System.getProperty("user.dir")
		File backupFile = new File(System.getProperty("user.dir"), destination);

		System.out.println("BACKING UP: " + backupFile.getAbsolutePath());

		backupFile.getParentFile().mkdirs();
		try {
			FileWriter writer = new FileWriter(backupFile);

			HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

			// Get all the fields, and put them in a map.
			for (MaxObject m : col) {
				Collection<String> keys = m.getFields();
				for (String key : keys) {
					map.put(key, new ArrayList<String>());
				}
				break;
			}
			// Fill each field in the map in with each set of MaxObject data.
			String backupString = "";
			Boolean firstRunKeys = true;
			for (String key : map.keySet()) {
				if (firstRunKeys) {
					firstRunKeys = false;
				} else {
					backupString += DELIMITER;
				}
				backupString += key;
			}
			backupString += END_OF_LINE_DELIMITER;

			for (MaxObject m : col) {
				Map<String, Object> dMap = m.dbMap;
				String line = "";
				boolean firstRun = true;
				for (String key : map.keySet()) {
					if (firstRun) {
						firstRun = false;
					} else {
						line += DELIMITER;
					}
					// VALUE to store
					Object o = dMap.get(key);
					if (o != null) {
						map.get(key).add(o.toString());
						line += o.toString();
					} else {
						// insert a blank.
						map.get(key).add("");
						line += "";
					}

				}

				line += END_OF_LINE_DELIMITER;
				backupString += line;
			}

			System.out.println(backupString);

			writer.write(backupString);
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	*/

	//@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param localFileName
	 * @param ref
	 * @param storeIn
	 */
	/*
	public static <T extends MaxObject> void restore(String localFileName, Class<T> ref,
			AbstractMap<String, T> storeIn) {
		// TODO: PROGRAM THE RESTORE FUNCTION
		String location = BACKUP_DIR + RESTORE_FOLDER + localFileName + CSV_EXTENSION;
		File backupFile = new File(System.getProperty("user.dir"), location);
		System.out.println("Opening backup file: " + backupFile);
		
		
		//TODO: change this code to work by splitting the string on the END_OF_LINE delimiter ****
		if (backupFile.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(backupFile));
				String entireFile = "";
				String textLine;
				//Read every line in the file, put all those lines in the "entireFile"
				while ((textLine = reader.readLine()) != null) {
					entireFile += textLine + "\n";
				}
				
				Map<String, ArrayList<String>> fileMap = new HashMap<>();
				ArrayList<String> keys = new ArrayList<String>();
				// read the top line of the file
				ArrayList<String> allLines = new ArrayList<String>();
				//This is an arraylist to remove the header line
				for (String line : entireFile.split(END_OF_LINE_DELIMITER)) {
					allLines.add(line);
				}
				
				//Technically the line is delimited by END_OF_LINE, which could
				//be any string. a "Line" can contain multiple new line characters.
				String headerLine;
				if ((headerLine = allLines.get(0)) != null) {
					System.out.println("Header Line: " + headerLine);
					String[] infoList = headerLine.split(DELIMITER);
					// loop through fields
					// create an arraylist for every field.
					// adds all fields (keys) to an arraylist for positioning
					for (String info : infoList) {
						System.out.println("Found field: " + info);
						fileMap.put(info, new ArrayList<String>());
						keys.add(info);
					}
					//Working to at least here
					allLines.remove(0);

				}
				//Loop through all the Line items add their contents to
				//the respective arraylist
				int rows = 0;
				for (String line : allLines) {
					rows++;
					String[] fields = line.split(DELIMITER);
					int i = 0;
					for (String field : fields) {
						String key = keys.get(i);
						fileMap.get(key).add(field);
						i++;
						System.out
								.println("Loaded field value: " + field + " to: " + fileMap.get(key) + " with: " + key);
					}
				}
				System.out.println(fileMap);

				reader.close();

				// CREATE CLASSES

				// EACH row indicates a client or other MaxObject based class
				for (int i = 0; i < rows; i++) {
					//create a map for this class
					//The map consists of a string key indicating a variable name
					//and a value key, indicating the string representation of that variables value
					//EX: [Color:0],[Name:Jimmy] ...
					Map<String, String> entity = new HashMap<String, String>();
					//loop through the field names.
					for (String key : keys) {
						System.out.println("Key: " + key);
						// added to fix an index out of bounds error.
						//Ignores all lines with less then 1 element or greater then the limit
						if (fileMap.get(key).size() >= 1 && fileMap.get(key).size()>i) {
							System.out.println("Value: " + fileMap.get(key).get(i));
							entity.put(key, fileMap.get(key).get(i));
						} else {
							System.out.println("INVALID ROW: " + i + " Line Size: " + fileMap.get(key).size());
						}
					}
					
					//Create a new instance of the object being restored
					MaxObject obj = ref.newInstance();
					//convert the map to a MaxObject, IE client/status
					obj.loadFromCSV(entity);
					
					System.out.println("Attempting to store: " + obj + " in: " + storeIn);
					if (obj != null && obj.getPrimaryKey() != null) {
						if (LOCAL_ONLY_RESTORE) {
						
						storeIn.put(obj.getPrimaryKey(), (T) obj);
						} else {
							DataHolder.store((T)obj, ref);
						}
					}

					if (obj.getPrimaryKey() == null) {
						System.out.println(
								"Error restoring: " + obj.getClass().getName() + " as the primary key was null");
					}
				}

			} catch (IOException | InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			System.err.println("Backup file: " + backupFile + " does not exist");
		}

	}
	*/

	/**
	 * Zips up a collection of files into a single zip file. this AUTOMATICALLY
	 * adds the .zip extension.
	 * 
	 * @param files
	 * @param zipFile
	 */
	public static void zipFiles(Collection<String> files, String zipFile) {

		File zipOutput = new File(System.getProperty("user.dir"), BACKUP_DIR + zipFile + ZIP_EXTENSION);
		zipOutput.getParentFile().mkdirs();
		if (!zipOutput.exists()) {
			try {
				zipOutput.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(zipOutput);

			ZipOutputStream zos = new ZipOutputStream(fos);
			zos.setLevel(COMPRESSION_LEVEL);// set the compression level
			for (String f : files) {
				// Read the file in:
				File inputFile = new File(System.getProperty("user.dir"), BACKUP_DIR + PRE_ZIP_DIR + f);
				FileInputStream fis = new FileInputStream(inputFile);

				ZipEntry ze = new ZipEntry(inputFile.getName());
				zos.putNextEntry(ze);

				byte[] tmp = new byte[BUFFER_SIZE];
				int size;
				// read until there is not any more data
				while ((size = fis.read(tmp)) != -1) {
					zos.write(tmp, 0, size);
				}
				zos.closeEntry();
				fis.close();

			}
			zos.close();
			System.out.println("wrote zip file: " + zipFile);
			System.out.println("absolute path: " + zipOutput.getAbsolutePath());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Unzips a backup to the restore directory
	 * 
	 * @param fileName
	 *            the backup file to unzip. (with the .zip extension)
	 */
	public static void unzipFiles(String fileName) {

		File zipfile = new File(System.getProperty("user.dir"), BACKUP_DIR + fileName);
		File outdir = new File(System.getProperty("user.dir"), BACKUP_DIR + RESTORE_FOLDER);

		
		
		outdir.mkdirs();
		//delete all files in the restore folder (just in case)
		for (File file: outdir.listFiles()) if (!file.isDirectory()) file.delete();
		
		
		ZipUtils.extract(zipfile, outdir);

	}

	public static Collection<String> getCsvBackups() {

		ArrayList<String> csvBackups = new ArrayList<String>();
		File backupTest = new File(System.getProperty("user.dir"), BACKUP_DIR);
		backupTest.mkdirs();
		File[] listOfFiles = backupTest.listFiles();
		
		for (File file : listOfFiles) {
			if (file.isFile() && file.getName().contains(ZIP_EXTENSION)) {
				System.out.println(file);
				csvBackups.add(file.getName().toString());
			
			}
		}
		return csvBackups;
	}

}
