package com.chn.emailmain;

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
	private NodeList mEmailList;
	private NodeList mEmailContent;
	private String testEmail;
	private String testMode;
	private String mPort;


	public String getEmailUser() {
		Node nNode = mEmailList.item(0);
		Element eElement = (Element) nNode;
		return eElement.getAttribute("user");	
	}

	public String getEmailPassword() {
		Node nNode = mEmailList.item(0);
		Element eElement = (Element) nNode;
		return eElement.getAttribute("password");	
	}

	public String getEmailServer() {
		Node nNode = mEmailList.item(0);
		Element eElement = (Element) nNode;
		return eElement.getAttribute("server");	
	}

	public String getEmailServerPort() {
		Node nNode = mEmailList.item(0);
		Element eElement = (Element) nNode;
		return eElement.getAttribute("port");	
	}
	
	public String getEmailSubject(){
		Node nNode = mEmailContent.item(0);
		Element eElement = (Element) nNode;
		return eElement.getAttribute("subject");	
	}
	
	public String getEmailContent(){
		Node nNode = mEmailContent.item(0);
		Element eElement = (Element) nNode;
		return eElement.getChildNodes().item(0).getNodeValue();
	}
	
	public String getFileLocation(){
		Node nNode = mEmailList.item(0);
		Element eElement = (Element) nNode;
		return eElement.getChildNodes().item(0).getNodeValue();
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

			if(doc.getDocumentElement().getNodeName().equals("Configuration")){
				testMode = doc.getDocumentElement().getAttribute("TestMode");
				testEmail = doc.getDocumentElement().getAttribute("testEmail");
				mPort = doc.getDocumentElement().getAttribute("port");
				mEmailList = doc.getElementsByTagName("Email");
				mEmailContent = doc.getElementsByTagName("EmailContent");
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

	public int getPort(){
		return Integer.parseInt(mPort);
	}

	public boolean isTestMode(){
		return testMode.equals("1")? true : false;
	}

	public String getTestEmail(){
		return testEmail;
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
			Attr attrBatExec = doc.createAttribute("testEmail");
			attrBatExec.setValue("chn1002@nate.com");
			rootElement.setAttributeNode(attrBatExec);

			//ProcessType Name Attr
			Attr attrTestMode = doc.createAttribute("TestMode");
			attrTestMode.setValue("0");
			rootElement.setAttributeNode(attrTestMode);

			//Port Attr
			Attr attrPort = doc.createAttribute("port");
			attrPort.setValue("50000");
			rootElement.setAttributeNode(attrPort);

			// Email
			Element EmailElement = doc.createElement("Email");
			EmailElement.appendChild(doc.createTextNode("파일 위치"));
			rootElement.appendChild(EmailElement);

			Attr attrEmailUser = doc.createAttribute("user");
			attrEmailUser.setValue("user");
			EmailElement.setAttributeNode(attrEmailUser);

			Attr attrEmailPassword = doc.createAttribute("password");
			attrEmailPassword.setValue("password");
			EmailElement.setAttributeNode(attrEmailPassword);
			
			Attr attrEmailServer = doc.createAttribute("server");
			attrEmailServer.setValue("smtp.gmail.com");
			EmailElement.setAttributeNode(attrEmailServer);
			
			Attr attrEmailServerPort = doc.createAttribute("port");
			attrEmailServerPort.setValue("465");
			EmailElement.setAttributeNode(attrEmailServerPort);
			
			Element EmailContentElement = doc.createElement("EmailContent");
			EmailContentElement.appendChild(doc.createTextNode("내용"));
			rootElement.appendChild(EmailContentElement);
			
			Attr attrEmailSubject = doc.createAttribute("subject");
			attrEmailSubject.setValue("제목");
			EmailContentElement.setAttributeNode(attrEmailSubject);


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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
