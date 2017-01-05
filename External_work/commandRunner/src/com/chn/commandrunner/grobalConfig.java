package com.chn.commandrunner;

import java.io.File; 
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class grobalConfig{
	private String grobalConfigFileName = "setting.xml";
	private NodeList mCommandRunner;
	private String testMode;

	private final String CONFIGURATION = "Configuration";
	private final String COMMANDRUNNER = "Commandrunner";
	private final String COMMAND_ID = "ID";
	private final String DEFAULT_ID = "0";
	private final String CONF_MODE = "Mode";
	private final String COMMAND_INFORMATION = "Information";
	private static final String PORT = "port";
	private static final String DEFAULT_PORT = "50000";
	private static final String MOUSE_POINT_X = "mousePointX";
	private static final String MOUSE_POINT_Y = "mousePointY";
	private static final String DEFAULT_POINT = "100";

	public final String CONF_MODE_SERVER = "server";
	public final String CONF_MODE_CONSOLE = "console";

	public int getCommandRunnerNumber(){
		int number =0;

		number = mCommandRunner.getLength();

		return number;
	}
	
	public String getCommandInformation(){
		return getCommandInformation(0);
	}

	public String getCommandInformation(int number){
		Node nNode = mCommandRunner.item(number);
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) nNode;

			return getTagValue(MOUSE_POINT_X, eElement);
		}

		return "";
	}
	
	public String getMousePointX(int number){
		Node nNode = mCommandRunner.item(number);
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) nNode;

			return getTagValue(MOUSE_POINT_X, eElement);
		}

		return "";
	}

	public String getMousePointY(int number){
		Node nNode = mCommandRunner.item(number);
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) nNode;

			return getTagValue(MOUSE_POINT_Y, eElement);
		}

		return "";
	}

	public int getPort() {
		return getPort(0);
	}


	public int getPort(int number){
		int port = 0;
		
		Node nNode = mCommandRunner.item(number);
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) nNode;
			
			port = Integer.parseInt(getTag(COMMAND_INFORMATION, eElement).
					getAttributes().getNamedItem(PORT).getNodeValue());
		}

		return port;
	}

	private String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

		Node nValue = (Node) nlList.item(0);

		return nValue.getNodeValue();
	}

	private Node getTag(String sTag, Element eElement) {
		Node nValue = eElement.getElementsByTagName(sTag).item(0);

		return nValue;
	}

	public grobalConfig(){
		File fXMLFile = new File(grobalConfigFileName);

		if(fXMLFile.exists()){
			readConfig(fXMLFile);
			return;
		} else{
			createConfigFile(grobalConfigFileName);		

			fXMLFile = new File(grobalConfigFileName);
			readConfig(fXMLFile);

			System.exit(0);
		}
	}

	private void readConfig(File xmlFile){
		try {
			DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();	
			DocumentBuilder dbBuilder;
			dbBuilder = dbFact.newDocumentBuilder();
			Document doc = dbBuilder.parse(xmlFile);
			doc.normalize();

			if(doc.getDocumentElement().getNodeName().equals(CONFIGURATION)){
				testMode = doc.getDocumentElement().getAttribute(CONF_MODE);
				mCommandRunner = doc.getElementsByTagName(COMMANDRUNNER);
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isConsleMode(){
		return testMode.equals(CONF_MODE_CONSOLE)? true : false;
	}

	private void createConfigFile(String filename){
		try {
			DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();	
			DocumentBuilder dbBuilder = dbFact.newDocumentBuilder();
			Document doc = dbBuilder.newDocument();

			//Root Element
			Element rootElement = doc.createElement(CONFIGURATION);
			doc.appendChild(rootElement);

			//ProcessType Name Attr
			Attr attrTestMode = doc.createAttribute(CONF_MODE);
			attrTestMode.setValue(CONF_MODE_SERVER);
			rootElement.setAttributeNode(attrTestMode);

			//command runner Element
			Element commandRunnerElement = doc.createElement(COMMANDRUNNER);
			rootElement.appendChild(commandRunnerElement);

			Attr attrCommandRunnerIDtMode = doc.createAttribute(COMMAND_ID);
			attrCommandRunnerIDtMode.setValue(DEFAULT_ID);
			commandRunnerElement.setAttributeNode(attrCommandRunnerIDtMode);

			// remote server information
			Element commandrunnerInformationElement = doc.createElement(COMMAND_INFORMATION);
			commandRunnerElement.appendChild(commandrunnerInformationElement);

			// mouse point x
			Element commandrunnerPointXElement = doc.createElement(MOUSE_POINT_X);
			commandrunnerPointXElement.setTextContent(DEFAULT_POINT);
			commandrunnerInformationElement.appendChild(commandrunnerPointXElement);

			// mouse point y
			Element commandrunnerPointYElement = doc.createElement(MOUSE_POINT_Y);
			commandrunnerPointYElement.setTextContent(DEFAULT_POINT);
			commandrunnerInformationElement.appendChild(commandrunnerPointYElement);

			Attr attrCommandRunnerPorttMode = doc.createAttribute(PORT);
			attrCommandRunnerPorttMode.setValue(DEFAULT_PORT);
			commandrunnerInformationElement.setAttributeNode(attrCommandRunnerPorttMode);

			//write the configuration xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "euc-kr");

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filename));
			transformer.transform(source, result);

			System.out.println("Create Grobal Configuration");
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}
