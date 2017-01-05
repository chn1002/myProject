package com.chn.remotecommand;

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
	private NodeList mRemoteControlServer;
	private String testMode;

	private final String CONFIGURATION = "Configuration";
	private final String REMOTESERVER = "RemoteServer";
	private final String REMOTE_ID = "ID";
	private final String DEFAULT_ID = "0";
	private final String CONF_MODE = "Mode";
	private final String REMOTESERVER_ADDRESS = "Address";
	private final String DEFAULT_ADDRESS = "127.0.0.1";
	private final String REMOTE_PORT = "port";
	private final String DEFAULT_REMOTE_PORT = "50000";
	private final String REMOTESERVER_COMMAND = "Command";
	private final String REMOTESERVER_REPEAT_COMMAND = "RepeatCommand";
	private final String DEFAULT_COMMAND = "play";
	private final String COMMANDREPEATTIME = "RepeatTime";
	private final String COMMANDSTARTTIME = "CommandStartTime";
	private final String DEFAULT_START_TIME = "1";
	private final String DEFAULT_REPEAT_TIME = "0";
	private final String PINGPONG_FUNCTION = "checkCommandRunnerAlive";
	private final String POINTPONG_TIME = "pingpong";
	private final String DEFAULT_PINGPONG_TIME = "10";

	public final String CONF_MODE_SERVER = "server";
	public final String CONF_MODE_CONSOLE = "console";

	private static final String ENABLE = "ENABLE";
	private static final String DISABLE = "DISABLE";

	public static final String PING = "PING";
	public static final String PONG = "PONG";

	private int mStartTime = 0;
	private int mRepeatTime = 0;
	private int mPingpongTime = 0;
	
	private boolean isCommandReady = false;
	
	public enum COMMAND_TYPE{
		START_COMMAND,
		REPEAT_COMMAND,
		INPUT_COMMNAD,
		PINGPONG_FUNCTION
	};

	public boolean isCommandReady() {
		return isCommandReady;
	}

	public void setCommandReady(boolean isCommandReady) {
		this.isCommandReady = isCommandReady;
	}

	public int StartCommandTime(){
		return mStartTime;

	}

	public int RepeatCommandTime() {
		return mRepeatTime;
	}

	public int PingpongTime() {
		return mPingpongTime;
	}

	public int getRemoteServerNumber(){
		int number =0;

		number = mRemoteControlServer.getLength();

		return number;
	}
	
	public boolean getPingpongfunction(int number){
		boolean enableFucntion = false;
		String value = null;
		
		Node nNode = mRemoteControlServer.item(number);
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) nNode;

			value = getTagValue(PINGPONG_FUNCTION, eElement);
		}

		if(value != null){
			if(value.equals(ENABLE)){
				enableFucntion = true;
			} else if(value.equals(DISABLE)){
				enableFucntion = false;
			}
		}
		
		return enableFucntion;
	}

	public String getRemoteServerAddress(int number){
		Node nNode = mRemoteControlServer.item(number);
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) nNode;

			return getTagValue(REMOTESERVER_ADDRESS, eElement);
		}

		return "";
	}

	public String getRemoteServerPort(int number){
		Node nNode = mRemoteControlServer.item(number);
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) nNode;

			return getTag(REMOTESERVER_ADDRESS, eElement).getAttributes().getNamedItem(REMOTE_PORT).getNodeValue();
		}

		return "";
	}

	public String getRemoteServerCommand(int number){
		Node nNode = mRemoteControlServer.item(number);
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) nNode;

			return getTagValue(REMOTESERVER_COMMAND, eElement);
		}

		return "";
	}

	public String getRemoteServerRepeatCommand(int number){
		Node nNode = mRemoteControlServer.item(number);
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) nNode;

			return getTagValue(REMOTESERVER_REPEAT_COMMAND, eElement);
		}

		return "";
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
				mStartTime = Integer.parseInt(doc.getDocumentElement().getAttribute(COMMANDSTARTTIME));
				mRepeatTime = Integer.parseInt(doc.getDocumentElement().getAttribute(COMMANDREPEATTIME));
				mPingpongTime = Integer.parseInt(doc.getDocumentElement().getAttribute(POINTPONG_TIME));
				
				mRemoteControlServer = doc.getElementsByTagName(REMOTESERVER);
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

			//Start Time Attr
			Attr attrStartTime = doc.createAttribute(COMMANDSTARTTIME);
			attrStartTime.setValue(DEFAULT_START_TIME);
			rootElement.setAttributeNode(attrStartTime);

			//Start Time Attr
			Attr attrRepeatTime = doc.createAttribute(COMMANDREPEATTIME);
			attrRepeatTime.setValue(DEFAULT_REPEAT_TIME);
			rootElement.setAttributeNode(attrRepeatTime);
			
			// Pingpong Time Attr
			Attr attPingpongTime = doc.createAttribute(POINTPONG_TIME);
			attPingpongTime.setValue(DEFAULT_PINGPONG_TIME);
			rootElement.setAttributeNode(attPingpongTime);


			//remote Control Element
			Element remoteElement = doc.createElement(REMOTESERVER);
			rootElement.appendChild(remoteElement);

			Attr attrRemoteIDtMode = doc.createAttribute(REMOTE_ID);
			attrRemoteIDtMode.setValue(DEFAULT_ID);
			remoteElement.setAttributeNode(attrRemoteIDtMode);

			// remote server information
			Element remoteAddressElement = doc.createElement(REMOTESERVER_ADDRESS);
			remoteAddressElement.setTextContent(DEFAULT_ADDRESS);
			remoteElement.appendChild(remoteAddressElement);

			Attr attrRemoteAddressPorttMode = doc.createAttribute(REMOTE_PORT);
			attrRemoteAddressPorttMode.setValue(DEFAULT_REMOTE_PORT);
			remoteAddressElement.setAttributeNode(attrRemoteAddressPorttMode);

			//remote command information
			Element remoteCommandElement = doc.createElement(REMOTESERVER_COMMAND);
			remoteCommandElement.setTextContent(DEFAULT_COMMAND);
			remoteElement.appendChild(remoteCommandElement);

			//remote command information
			Element remoteRepeatCommandElement = doc.createElement(REMOTESERVER_REPEAT_COMMAND);
			remoteRepeatCommandElement.setTextContent(DEFAULT_COMMAND);
			remoteElement.appendChild(remoteRepeatCommandElement);
			
			Element remotePingpongElement = doc.createElement(PINGPONG_FUNCTION);
			remotePingpongElement.setTextContent(ENABLE);
			remoteElement.appendChild(remotePingpongElement);

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
