package com.chn.main;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import org.xml.sax.SAXException;

public class grobalConfig {
	private String grobalConfigFileName = "filenet_setting.xml";
	private NodeList remoteFileDir;
	private NodeList localFileDir;
	private NodeList networkDir;
	private NodeList ftpNetwork;
	private NodeList httpNetwork;
	private Document mDoc;
	
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
			mDoc = dbBuilder.parse(xmlFile);
			mDoc.normalize();
			
			remoteFileDir = mDoc.getElementsByTagName("remoteFileDir");
			localFileDir = mDoc.getElementsByTagName("localFileDir");
			networkDir = mDoc.getElementsByTagName("network");
			ftpNetwork = mDoc.getElementsByTagName("ftp");
			httpNetwork = mDoc.getElementsByTagName("httpurl");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getRemoteFileDir(){
		Node nNode = remoteFileDir.item(0);
		Element eElement = (Element) nNode;
		return eElement.getChildNodes().item(0).getNodeValue();
	}
	
	public String getFileName(){
		Node nNode = remoteFileDir.item(0);
		Element eElement = (Element) nNode;
		return eElement.getAttribute("filename");
	}
	
	public int getMaxFileNumber(){
		Node nNode = remoteFileDir.item(0);
		Element eElement = (Element) nNode;
		return Integer.parseInt(eElement.getAttribute("Maxfilenumber"));
	}
	
	public String getLocalFileDir(){
		Node nNode = localFileDir.item(0);
		Element eElement = (Element) nNode;
		return eElement.getChildNodes().item(0).getNodeValue();
	}

	public int getFileNumber(){
		Node nNode = remoteFileDir.item(0);
		Element eElement = (Element) nNode;
		return Integer.parseInt(eElement.getAttribute("filenumber"));
	}

	public void setFileNumber(){
		Node nNode = remoteFileDir.item(0);
		Element eElement = (Element) nNode;
		Attr attfilenumber = eElement.getAttributeNode("filenumber");
		int fileNum = getFileNumber() + 1;
		attfilenumber.setNodeValue(fileNum + "");
		
		writeXML(mDoc);
	}
	
	public void setFileNumber(int number){
		Node nNode = remoteFileDir.item(0);
		Element eElement = (Element) nNode;
		Attr attfilenumber = eElement.getAttributeNode("filenumber");
		int fileNum = number;
		attfilenumber.setNodeValue(fileNum + "");
		
		writeXML(mDoc);
	}
	
	public String getFTPUrl(){
		Node nNode = ftpNetwork.item(0);
		Element eElement = (Element) nNode;
		return eElement.getChildNodes().item(0).getNodeValue();		
	}
	
	public String getFTPUser(){
		Node nNode = ftpNetwork.item(0);
		Element eElement = (Element) nNode;
		return eElement.getAttribute("user");		
	}
	
	public String getFTPPassword(){
		Node nNode = ftpNetwork.item(0);
		Element eElement = (Element) nNode;
		return eElement.getAttribute("password");		
	}
	
	public String getHTTPUrl(){
		Node nNode = httpNetwork.item(0);
		Element eElement = (Element) nNode;
		return eElement.getChildNodes().item(0).getNodeValue();			
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
			Attr attrBatExec = doc.createAttribute("TestMode");
			attrBatExec.setValue("0");
			rootElement.setAttributeNode(attrBatExec);

			//Command Element
			Element commandElement = doc.createElement("remoteFileDir");
			commandElement.appendChild(doc.createTextNode("remote"));
			rootElement.appendChild(commandElement);
			
			//FileName Attr
			Attr attrFileName = doc.createAttribute("filename");
			attrFileName.setValue("image");
			commandElement.setAttributeNode(attrFileName);
			
			//FileNumber Attr
			Attr attrMaxFileNum = doc.createAttribute("Maxfilenumber");
			attrMaxFileNum.setValue("1");
			commandElement.setAttributeNode(attrMaxFileNum);

			//FileNumber Attr
			Attr attrFileNum = doc.createAttribute("filenumber");
			attrFileNum.setValue("0");
			commandElement.setAttributeNode(attrFileNum);

			//localFileDir Element
			Element localDirElement = doc.createElement("localFileDir");
			localDirElement.appendChild(doc.createTextNode("local"));
			rootElement.appendChild(localDirElement);
			
			//localFileDir Element
			Element networkElement = doc.createElement("network");
			rootElement.appendChild(networkElement);
			
			// FTP
			Element ftpElement = doc.createElement("ftp");
			ftpElement.appendChild(doc.createTextNode("ftpaddress"));
			networkElement.appendChild(ftpElement);
			
			Attr attrFtpUser = doc.createAttribute("user");
			attrFtpUser.setValue("user");
			ftpElement.setAttributeNode(attrFtpUser);
			
			Attr attrFtpPassword = doc.createAttribute("password");
			attrFtpPassword.setValue("password");
			ftpElement.setAttributeNode(attrFtpPassword);

			//HTTP URL
			Element httpURLElement = doc.createElement("httpurl");
			httpURLElement.appendChild(doc.createTextNode("httpurl"));
			networkElement.appendChild(httpURLElement);


			//write the configuration xml file
			writeXML(doc);
			
			
			System.out.print("Create Grobal Configuration\n");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private void writeXML(Document doc){
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(grobalConfigFileName));
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e1) {
			e1.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
	}
}
