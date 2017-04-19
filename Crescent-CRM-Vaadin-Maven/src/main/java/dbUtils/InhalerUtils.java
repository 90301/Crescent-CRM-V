/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package dbUtils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//XML IMPORTS
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
//END XML IMPORTS
import org.xml.sax.SAXException;

import ccrmV.MasterUI;
import clientInfo.DataHolder;
import clientInfo.Location;
import clientInfo.UserDataHolder;
import debugging.Debugging;
import users.User;

public class InhalerUtils {
	private static final String DELIMITER = "|";
	private static final String ROOT_DOC_STRING = "root";

	/**
	 * This method will replace any and all spaces in a given string with
	 * hyphens
	 */
	public static String replaceStringSpaces(String string) {
		string = string.replace(" ", "-");
		return string;
	}

	/**
	 * Caution: Do not use this method when dealing with maxObject database
	 * serialization (dealing with dbMap)
	 * 
	 * @param items
	 * @return
	 */
	public static String maxObjectListToCSV(Collection<? extends MaxObject> items) {
		boolean firstRun = true;
		String csv = "";
		for (MaxObject item : items) {

			if (!firstRun) {
				csv += DELIMITER;
			} else {
				firstRun = false;
			}

			csv += item.getPrimaryKey();
		}
		return csv;
	}

	public static List<MaxObject> maxObjectCSVToList(String csv, UserDataHolder udh,
			@SuppressWarnings("rawtypes") Class ref) {
		List<MaxObject> items = new ArrayList<MaxObject>();

		// Check to see if the class is part of the DataHolder (static data)
		if (DataHolder.containsClass(ref)) {
			String[] allStrs = csv.split(DELIMITER);
			for (String str : allStrs) {
				@SuppressWarnings("unchecked")
				MaxObject item = DataHolder.retrieve(str, ref);

				items.add(item);
			}
		} else {
			if (udh == null) {
				System.err.println("Null User Data Holder found for udh Object");
				return null;
			}
			// check to see if udh is null ( if so, fail)
		}
		return items;
	}

	/**
	 * Converts a csv to a Collection. The underlying data structure is an
	 * arraylist
	 * 
	 * @param csv
	 * @return
	 */
	public static Collection<String> csvToList(String csv) {
		ArrayList<String> list = new ArrayList<String>();
		for (String s : csv.split("\\" + DELIMITER)) {
			Debugging.output("CSV ITEM: " + s, Debugging.DATABASE_OUTPUT);
			list.add(s);
		}

		return list;
	}

	/**
	 * Returns a CSV (string) from a list of strings
	 * 
	 * @param list
	 * @return
	 */
	public static String listToCsv(Collection<String> list) {

		String csv = "";
		boolean firstRun = true;
		for (String s : list) {
			if (!firstRun) {
				csv += DELIMITER;
			} else {
				firstRun = false;
			}

			csv += s;
		}
		return csv;

	}
	/*
	 * public static Map<String,String>csvToMap(String csv) {
	 * HashMap<String,String> map = new HashMap<String,String>(); //TODO
	 * IMPLEMENT THIS return map; }
	 * 
	 * public static String MapToCsv(Map<String,String> map) { String csv = "";
	 * //TODO implement this
	 * 
	 * return csv; }
	 */

	/**
	 * Converts a simple map into an xml document. For more complicated
	 * data-structures this can be used as a guide for writing the XML document.
	 * 
	 * @param map
	 * @return
	 */
	public static String mapToXML(Map<String, String> map) {
		//clean map

		Map<String, String> mapUsed = cleanLinkedMap(map);

		String xml = "";
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;

		try {
			docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(ROOT_DOC_STRING);
			doc.appendChild(rootElement);

			for (String key : mapUsed.keySet()) {

				Debugging.output("Writing (key): " + key, Debugging.XML_CONVERSION);

				Debugging.output("Writing (Value): " + mapUsed.get(key), Debugging.XML_CONVERSION);

				Element child = doc.createElement(key);
				// Attr child = doc.createAttribute(key);
				child.appendChild(doc.createTextNode(mapUsed.get(key)));

				rootElement.appendChild(child);

			}

			/*
			 * Convert the XML class into a String
			 */

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			DOMSource source = new DOMSource(doc);

			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);

			transformer.transform(source, result);

			xml = sw.toString();
			Debugging.output("Created XML: " + xml, Debugging.XML_CONVERSION);

		} catch (ParserConfigurationException e) {
			Debugging.output("" + e.getLocalizedMessage() + " | Cause: " + e.getCause(),
					Debugging.XML_CONVERSION_ERROR);
			Debugging.output("" + e.getLocalizedMessage() + " | Cause: " + e.getCause(), Debugging.XML_CONVERSION);
			//e.printStackTrace();
		} catch (TransformerException e) {
			Debugging.output("" + e.getLocalizedMessage() + " | Cause: " + e.getCause(),
					Debugging.XML_CONVERSION_ERROR);
			Debugging.output("" + e.getLocalizedMessage() + " | Cause: " + e.getCause(), Debugging.XML_CONVERSION);
			//e.printStackTrace();
		} catch (DOMException e) {
			Debugging.output("" + e.getLocalizedMessage() + " | Cause: " + e.getCause(), Debugging.XML_CONVERSION);
			Debugging.output("" + e.getLocalizedMessage() + " | Cause: " + e.getCause(),
					Debugging.XML_CONVERSION_ERROR);

		}
		return xml;
	}

	/**
	 * Cleans a map for storage.
	 * 
	 * @param map
	 *            - used for storage
	 * @return
	 */
	public static Map<String, String> cleanLinkedMap(Map<String, String> map) {
		Map<String, String> cleanMap = new LinkedHashMap<String, String>();
		for (String key : map.keySet()) {
			String nKey = key;
			String value = map.get(key);
			String nValue = value;
			//test if key is proper
			if (key.contains(" ")) {
				nKey = key.replace(' ', '-');

			}
			//test if  body is correct

			cleanMap.put(nKey, nValue);
		}
		return cleanMap;
	}

	public static Map<String, String> cleanHashMap(Map<String, String> map) {
		Map<String, String> cleanMap = new HashMap<String, String>();
		for (String key : map.keySet()) {
			String nKey = key;
			String value = map.get(key);
			String nValue = value;
			//test if key is proper
			if (key.contains(" ")) {
				nKey = key.replace(' ', '-');

			}
			//test if  body is correct

			cleanMap.put(nKey, nValue);
		}
		return cleanMap;
	}

	/**
	 * Converts xml to a simple map to be used in conjection with mapToXML
	 * 
	 * @param xml-
	 *            the xml to convert
	 * @return
	 */
	public static HashMap<String, String> xmlToMap(String xml) {
		HashMap<String, String> map = new HashMap<String, String>();
		if (xml == null || xml.equals("")) {
			return map;
		}
		try {

			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			InputSource is = new InputSource(new StringReader(xml));
			//builder.parse(is);

			Document doc = dBuilder.parse(is);

			Element rootElement = doc.getDocumentElement();

			NodeList childElementList = rootElement.getChildNodes();

			for (int i = 0; i < childElementList.getLength(); i++) {
				Node child = childElementList.item(i);

				if (child.getNodeType() == Node.ELEMENT_NODE) {
					String key = child.getNodeName();

					String value = child.getTextContent();

					//String value1 = child.getNodeValue();
					map.put(key, value);

					Debugging.output("Loaded XML Key Value Pair: " + key + " | " + value, Debugging.XML_CONVERSION);
				}
			}

		} catch (SAXException e) {
			if (MasterUI.DEVELOPER_MODE) {
				//e.printStackTrace();
				Debugging.output("" + e.getLocalizedMessage() + " | Cause: " + e.getCause(),
						Debugging.XML_CONVERSION_ERROR);
			} else {

			}
		} catch (IOException e) {
			if (MasterUI.DEVELOPER_MODE) {
				//e.printStackTrace();
				Debugging.output("" + e.getLocalizedMessage() + " | Cause: " + e.getCause(),
						Debugging.XML_CONVERSION_ERROR);
			} else {

			}
		} catch (ParserConfigurationException e) {
			if (MasterUI.DEVELOPER_MODE) {
				//e.printStackTrace();
				Debugging.output("" + e.getLocalizedMessage() + " | Cause: " + e.getCause(),
						Debugging.XML_CONVERSION_ERROR);
			} else {

			}
		} catch (DOMException e) {
			//map = new HashMap<String,String>();
			Debugging.output("" + e.getLocalizedMessage() + " | Cause: " + e.getCause(),
					Debugging.XML_CONVERSION_ERROR);
		}

		return map;

	}

	public static HashMap<String, String> xmlToLinkedHashMap(String xml) {
		HashMap<String, String> map = new LinkedHashMap<String, String>();
		if (xml == null || xml.equals("")) {
			return map;
		}
		try {

			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			InputSource is = new InputSource(new StringReader(xml));
			//builder.parse(is);

			Document doc = dBuilder.parse(is);

			Element rootElement = doc.getDocumentElement();

			NodeList childElementList = rootElement.getChildNodes();

			for (int i = 0; i < childElementList.getLength(); i++) {
				Node child = childElementList.item(i);

				if (child.getNodeType() == Node.ELEMENT_NODE) {
					String key = child.getNodeName();

					String value = child.getTextContent();

					//String value1 = child.getNodeValue();
					map.put(key, value);

					Debugging.output("Loaded XML Key Value Pair: " + key + " | " + value, Debugging.XML_CONVERSION);
				}
			}

		} catch (SAXException e) {
			if (MasterUI.DEVELOPER_MODE) {
				//e.printStackTrace();
				Debugging.output("" + e.getLocalizedMessage() + " | Cause: " + e.getCause(),
						Debugging.XML_CONVERSION_ERROR);
			} else {

			}
		} catch (IOException e) {
			if (MasterUI.DEVELOPER_MODE) {
				//e.printStackTrace();
				Debugging.output("" + e.getLocalizedMessage() + " | Cause: " + e.getCause(),
						Debugging.XML_CONVERSION_ERROR);
			} else {

			}
		} catch (ParserConfigurationException e) {
			if (MasterUI.DEVELOPER_MODE) {
				//e.printStackTrace();
				Debugging.output("" + e.getLocalizedMessage() + " | Cause: " + e.getCause(),
						Debugging.XML_CONVERSION_ERROR);
			} else {

			}
		} catch (DOMException e) {
			//map = new HashMap<String,String>();
			Debugging.output("" + e.getLocalizedMessage() + " | Cause: " + e.getCause(),
					Debugging.XML_CONVERSION_ERROR);
		}

		return map;

	}

	/**
	 * Converts a map into a serial map using the toString function of each
	 * object
	 * 
	 * @param inputMap
	 * @return
	 */
	public static Map<String, String> mapToStringMap(Map<String, Object> inputMap) {
		HashMap<String, String> outputMap = new HashMap<String, String>();

		for (String key : inputMap.keySet()) {
			outputMap.put(key, inputMap.get(key).toString());
		}

		return outputMap;
	}

	/**
	 * Returns true if the string is null/blank or "null"
	 * 
	 * @param testingString
	 * @return true if null
	 */
	public static Boolean stringNullCheck(String testingString) {

		Boolean rtrn = false;
		if (testingString == null || testingString.equals("") || testingString.equals("null")) {
			rtrn = true;
		}

		Debugging.output("Tested: " + testingString + " Null?: " + rtrn, Debugging.DATABASE_OUTPUT);
		return rtrn;
	}

	public static String genRandomKey() {
		UUID uid = UUID.randomUUID();

		return uid.toString();
	}

	public static String removeSpecialCharacters(String str) {
		String str2 = str;
		str2 = str2.replaceAll("[^A-Za-z0-9]", "");

		return str2;
	}

	/**
	 * ._____________________________________________________________. |
	 * Attempts to Format a block of text by surrounding it with a | | box . . .
	 * . . . . . . . . . . . . . . . . . . . . . . . . . |
	 * |_____________________________________________________________|
	 * 
	 * @param s
	 *            the string to box
	 * @return the string with a box around it
	 */
	public static String boxString(String s) {
		String output = "";

		//split string into an array of lines
		String lines[] = s.split("\\r?\\n");

		//find the longest line
		int longestLine = 0;

		for (String line : lines) {
			if (line.length() > longestLine) {
				longestLine = line.length();
			}
		}

		//create top and bottom sections of the box
		String topBox = "._";
		String bottomBox = "|_";
		for (int i = 0; i < longestLine; i++) {
			topBox += "_";
			bottomBox += "_";
		}
		topBox += "_.";
		bottomBox += "_|";

		output += topBox + System.lineSeparator();

		for (String line : lines) {
			String spacedLine = addSpacing(line, longestLine);

			output += "| " + spacedLine + " |" + System.lineSeparator();
		}

		output += bottomBox;

		return output;
	}

	public static final String SPACING_FORMAT_BLANK_LINE = "<BLANK_LINE>";
	public static final String SPACING_FORMAT_CENTERED = "<CENTERED>";

	/**
	 * Adds spacing to string to the length. dots are added every other
	 * character to help with formatting
	 * 
	 * Other advanced features are included if certain constants are found in
	 * the string, to see all the available constants, look at SPACING_FORMAT
	 * constants.
	 * 
	 * @param input
	 *            the string to add spacing to
	 * @param length
	 *            the length of the string to space to
	 * @return the formatted string
	 */
	public static String addSpacing(String input, int length) {
		String output = input;

		//check input for special formatting
		if (input.contains(SPACING_FORMAT_BLANK_LINE)) {
			//Creates a blank line
			//____________________
			output = "";
			for (int i = 0; i < length; i++) {
				output += "_";
			}

		} else if (input.contains(SPACING_FORMAT_CENTERED)) {
			//Creates centered text
			//____Centered Text____
			String strippedInput = input.replace(SPACING_FORMAT_CENTERED, "");
			//determine spacing to add
			output = "";
			int inputLength = strippedInput.length();
			int halfway = (length - inputLength) / 2;

			if (halfway < 0) {
				return "Error formatting: " + input;
			}

			String spacingStart = "";
			for (int i = 0; i < halfway; i++) {
				spacingStart += "_";
			}
			output = spacingStart + strippedInput;
			//add remaining spacing
			for (int i = output.length(); i < length; i++) {
				output += "_";
			}

		} else {

			//STANDARD SPACING
			for (int i = output.length(); i < length; i++) {
				if (i % 2 == 0) {
					output += " ";
				} else {
					output += ".";
				}
			}

		}

		return output;
	}

	public static Collection<String> reverseList(Collection<String> input) {
		ArrayList<String> output = new ArrayList<String>();
		for (String inputValue : input) {
			output.add(0, inputValue);
		}

		return output;
	}

	/*
	 * CONVERT Collections to String representation
	 */
	public static String toString(Collection<?> col) {
		String output = col.getClass() + " {";
		for (Object o : col) {
			if (o != null) {
				output += o + DELIMITER;
			} else {
				output += "NULL" + DELIMITER;
			}
		}

		output += "}";

		return output;
	}

	public static String toStringColumns(ResultSet rs) {
		String output = "";
		int cols;

		try {
			cols = rs.getMetaData().getColumnCount();

			output += "Columns: " + cols;
			//end metadata
			output += System.getProperty("line.separator");

			for (int i = 1; i <= cols; i++) {
				//This is 1 indexed for some reason
				output += rs.getMetaData().getColumnName(i) + " | " + rs.getMetaData().getColumnTypeName(i);
				output += System.getProperty("line.separator");
			}

		} catch (SQLException e) {

			output += " Error occured: " + e.getMessage();
			//e.printStackTrace();
		}

		return output;
	}

}
