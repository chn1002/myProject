package com.chn.main;

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

public class grobalConfig
{
	private String grobalConfigFileName = "clientsetting.xml";
	private NodeList mCommandList;
	private NodeList mMediaList;
	private NodeList mRemoteControlServer;
	private String testMode;

	private final String REMOTESERVER_ADDRESS = "Address";
	private final String DEFAULT_ADDRESS = "127.0.0.1";
	private final String REMOTE_PORT = "port";
	private final String DEFAULT_REMOTE_PORT = "50000";
	
	private String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

		Node nValue = (Node) nlList.item(0);

		return nValue.getNodeValue();
	}
	
	private Node getTag(String sTag, Element eElement) {
		Node nValue = eElement.getElementsByTagName(sTag).item(0);

		return nValue;
	}
	
	public int getRemoteServerNumber(){
		int number =0;

		number = mRemoteControlServer.getLength();

		return number;
	}

	public grobalConfig() {
		File fXMLFile = new File(this.grobalConfigFileName);

		if (fXMLFile.exists()) {
			readConfig(fXMLFile);
			return;
		}
		createConfigFile(this.grobalConfigFileName);

		fXMLFile = new File(this.grobalConfigFileName);
		readConfig(fXMLFile);

		System.exit(0);
	}

	private void readConfig(File xmlFile)
	{
		try {
			DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();

			DocumentBuilder dbBuilder = dbFact.newDocumentBuilder();
			Document doc = dbBuilder.parse(xmlFile);
			doc.normalize();

			if (doc.getDocumentElement().getNodeName().equals("Configuration")) {
				this.testMode = doc.getDocumentElement().getAttribute("TestMode");
				this.mRemoteControlServer = doc.getElementsByTagName(REMOTESERVER_ADDRESS);
				this.mCommandList = doc.getElementsByTagName("command");
				this.mMediaList = doc.getElementsByTagName("media");
			}
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		catch (SAXException e) {
			/*  66 */       e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getRemoteServerAddress(int number){
		Node nNode = mRemoteControlServer.item(number);
		Element eElement = (Element)nNode;

		return eElement.getChildNodes().item(0).getNodeValue();
	}

	public String getRemoteServerPort(int number){
		Node nNode = mRemoteControlServer.item(number);
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			return nNode.getAttributes().getNamedItem(REMOTE_PORT).getNodeValue();
		}

		return "";
	}

	public int getMediaLength() {
		return this.mMediaList.getLength();
	}

	public String getMediaName(int num) {
		Node nNode = this.mMediaList.item(num);
		Element eElement = (Element)nNode;
		return eElement.getAttribute("type");
	}

	public String getMediaValue(int num) {
		Node nNode = this.mMediaList.item(num);
		Element eElement = (Element)nNode;
		return eElement.getChildNodes().item(0).getNodeValue();
	}


	public String getMediaValue(String type)
	{
		for (int tmp = 0; tmp < this.mMediaList.getLength(); tmp++) {
			Node nNode = this.mMediaList.item(tmp);
			Element eElement = (Element)nNode;

			if (eElement.getAttribute("type").equals(type)) {
				return eElement.getChildNodes().item(0).getNodeValue();
			}
		}
		return null;
	}

	public int getCommandLength() {
		/* 112 */     return this.mCommandList.getLength();
	}

	public String getCommandName(int num) {
		Node nNode = this.mCommandList.item(num);
		Element eElement = (Element)nNode;
		return eElement.getAttribute("name");
	}

	public String getCommandValue(String commandName) {
		for (int tmp = 0; tmp < this.mCommandList.getLength(); tmp++) {
			Node nNode = this.mCommandList.item(tmp);
			Element eElement = (Element)nNode;
			if (eElement.getAttribute("name").equals(commandName)) {
				return eElement.getChildNodes().item(0).getNodeValue();
			}
		}

		return null;
	}

	public String getCommandValue(int num) {
		Node nNode = this.mCommandList.item(num);
		Element eElement = (Element)nNode;
		return eElement.getChildNodes().item(0).getNodeValue();
	}

	public boolean isTestMode() {
		return this.testMode.equals("1");
	}

	private void createConfigFile(String filename) {
		try {
			DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbBuilder = dbFact.newDocumentBuilder();
			Document doc = dbBuilder.newDocument();


			Element rootElement = doc.createElement("Configuration");
			doc.appendChild(rootElement);


			Attr attrBatExec = doc.createAttribute("TestMode");
			attrBatExec.setValue("0");
			rootElement.setAttributeNode(attrBatExec);

			Element ipElement = doc.createElement(REMOTESERVER_ADDRESS);
			ipElement.appendChild(doc.createTextNode(DEFAULT_ADDRESS));
			rootElement.appendChild(ipElement);

			Attr attrPort = doc.createAttribute(REMOTE_PORT);
			attrPort.setValue(DEFAULT_REMOTE_PORT);
			ipElement.setAttributeNode(attrPort);


			Element commandElement = doc.createElement("command");
			commandElement.appendChild(doc.createTextNode("PLAY"));
			rootElement.appendChild(commandElement);


			Attr attrcommandName = doc.createAttribute("name");
			attrcommandName.setValue("play");
			commandElement.setAttributeNode(attrcommandName);


			Element commandReplayElement = doc.createElement("command");
			commandReplayElement.appendChild(doc.createTextNode("REPLAY"));
			rootElement.appendChild(commandReplayElement);


			Attr attrcommandReplayName = doc.createAttribute("name");
			attrcommandReplayName.setValue("replay");
			commandReplayElement.setAttributeNode(attrcommandReplayName);


			Element mediaElement = doc.createElement("media");
			mediaElement.appendChild(doc.createTextNode("media"));
			rootElement.appendChild(mediaElement);


			Attr attrmediaName = doc.createAttribute("type");
			attrmediaName.setValue("video");
			mediaElement.setAttributeNode(attrmediaName);


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
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}


