package com.chn.main;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

public class grobalConfig extends mainServer{
	private String grobalConfigFileName = "setting.xml";
	private NodeList mCommandList;
	private NodeList mMediaList;
	private String batexecu;
	private String testMode;
	private String mPort;
	
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
			
			if(doc.getDocumentElement().getNodeName().equals("Configuration")){
				testMode = doc.getDocumentElement().getAttribute("TestMode");
				batexecu = doc.getDocumentElement().getAttribute("batexec");
				mPort = doc.getDocumentElement().getAttribute("port");
				mCommandList = doc.getElementsByTagName("command");
				mMediaList = doc.getElementsByTagName("media");
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
	
	public int getMediaLength(){
		return mMediaList.getLength();
	}

	public String getMediaName(int num){
		Node nNode = mMediaList.item(num);
		Element eElement = (Element) nNode;
		return eElement.getAttribute("type");
	}

	public String getMediaValue(int num){
		Node nNode = mMediaList.item(num);
		Element eElement = (Element) nNode;
		return eElement.getChildNodes().item(0).getNodeValue();
	}
	
	public String getMediaValue(String type){
		Element eElement;
		
		for(int tmp =0; tmp < mMediaList.getLength(); tmp++){
			Node nNode = mMediaList.item(tmp);
			eElement = (Element) nNode;
			
			if(eElement.getAttribute("type").equals(type))
				return eElement.getChildNodes().item(0).getNodeValue();
		}
		
		return null;
	}
	
	public int getCommandLength(){
		return mCommandList.getLength();
	}

	public String getCommandName(int num){
		Node nNode = mCommandList.item(num);
		Element eElement = (Element) nNode;
		return eElement.getAttribute("name");
	}
	
	public int getCommandTime(int num){
		Node nNode = mCommandList.item(num);
		Element eElement = (Element) nNode;
		return Integer.parseInt(eElement.getAttribute("time"));
	}

	public String getCommandValue(int num){
		Node nNode = mCommandList.item(num);
		Element eElement = (Element) nNode;
		return eElement.getChildNodes().item(0).getNodeValue();
	}
	
	public int getPort(){
		return Integer.parseInt(mPort);
	}
	
	public boolean isTestMode(){
		return testMode.equals("1")? true : false;
	}
	
	public boolean isBatexecu(){
		return batexecu.equals("1") ? true : false;
	}

	private void createConfigFile(String filename){
		try {
			DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();	
			DocumentBuilder dbBuilder = dbFact.newDocumentBuilder();
			Document doc = dbBuilder.newDocument();
	
			//Root Element
			Element rootElement = doc.createElement("Configuration");
			doc.appendChild(rootElement);
			
			//BatExecu Name Attr
			Attr attrBatExec = doc.createAttribute("batexec");
			attrBatExec.setValue("1");
			rootElement.setAttributeNode(attrBatExec);

			//ProcessType Name Attr
			Attr attrTestMode = doc.createAttribute("TestMode");
			attrTestMode.setValue("0");
			rootElement.setAttributeNode(attrTestMode);
			
			//Port Attr
			Attr attrPort = doc.createAttribute("port");
			attrPort.setValue("50000");
			rootElement.setAttributeNode(attrPort);

			//Command Element
			Element commandPlayElement = doc.createElement("command");
			commandPlayElement.appendChild(doc.createTextNode("PLAY"));
			rootElement.appendChild(commandPlayElement);
			
			//Command Name Attr
			Attr attrcommandName = doc.createAttribute("name");
			attrcommandName.setValue("play");
			commandPlayElement.setAttributeNode(attrcommandName);
			
			//Command Element
			Element commandReplayElement = doc.createElement("command");
			commandReplayElement.appendChild(doc.createTextNode("REPLAY"));
			rootElement.appendChild(commandReplayElement);
			
			//Command Name Attr
			Attr attrcommandReplayName = doc.createAttribute("name");
			attrcommandReplayName.setValue("replay");
			commandReplayElement.setAttributeNode(attrcommandReplayName);
			
			//Command Name Attr
			Attr attrcommanTime = doc.createAttribute("time");
			attrcommanTime.setValue("60");
			commandReplayElement.setAttributeNode(attrcommanTime);
			
			//Media Element
			Element mediaElement = doc.createElement("media");
			mediaElement.appendChild(doc.createTextNode("media"));
			rootElement.appendChild(mediaElement);
			
			//Media Name Attr
			Attr attrmediaName = doc.createAttribute("type");
			attrmediaName.setValue("video");
			mediaElement.setAttributeNode(attrmediaName);
			
			//write the configuration xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filename));
			transformer.transform(source, result);

			System.out.println("Create Grobal Configuration");
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
