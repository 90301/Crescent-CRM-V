/*
 * (c) 2016 Josh Benton. All Rights Reserved.
 */
package dbUtils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
//END XML IMPORTS
import org.xml.sax.SAXException;

import clientInfo.DataHolder;
import clientInfo.Location;
import clientInfo.UserDataHolder;
import debugging.Debugging;
import users.User;

public class InhalerUtils {
	private static final String DELIMITER = "|";
	private static final String ROOT_DOC_STRING = "root";

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
			System.out.println("CSV ITEM: " + s);
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
		String xml = "";
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(ROOT_DOC_STRING);
			doc.appendChild(rootElement);

			for (String key : map.keySet()) {
				Element child = doc.createElement(key);
				// Attr child = doc.createAttribute(key);
				child.appendChild(doc.createTextNode(map.get(key)));

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
			Debugging.output("Created XML: " + xml, Debugging.INHALER_UTILS_DEBUG,
					Debugging.INHALER_UTILS_DEBUG_ENABLED);

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xml;
	}

	/**
	 * Converts xml to a simple map to be used in conjection with mapToXML
	 * 
	 * @param xml-
	 *            the xml to convert
	 * @return
	 */
	public static HashMap<String, String> xmlToMap(String xml) {
		HashMap<String,String> map = new HashMap<String,String>();
		try {
			
		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		 InputSource is = new InputSource(new StringReader(xml));
		    //builder.parse(is);
		    
			Document doc = dBuilder.parse(is);
			
			Element rootElement = doc.getDocumentElement();
			
			NodeList childElementList = rootElement.getChildNodes();
			
			for (int i=0;i<childElementList.getLength();i++) {
				Node child = childElementList.item(i);
				
				if (child.getNodeType()== Node.ELEMENT_NODE) {
				String key = child.getNodeName();
				
				String value = child.getTextContent();
				
				//String value1 = child.getNodeValue();
				map.put(key, value);
				
				Debugging.output("Loaded XML Key Value Pair: " + key + " | " + value, Debugging.INHALER_UTILS_DEBUG,
						Debugging.INHALER_UTILS_DEBUG_ENABLED);
				}
			}
			

		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		if (testingString == null || testingString == "" || testingString == "null") {
			rtrn = true;
		}

		System.out.println("Tested: " + testingString + " Null?: " + rtrn);
		return rtrn;
	}

}
