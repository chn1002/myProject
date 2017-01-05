package com.chn.relaymain;

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

public class GrobalConfig{
	private String grobalConfigFileName = "setting.xml";
	
	private final String USER = "user";
	private final String PASSWORD = "password";
	private final String SERVER = "server";
	private final String PORT = "port";
	
	private String testEmail;
	private String testMode;
	private String mPort;

	public GrobalConfig(){
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
				mPort = doc.getDocumentElement().getAttribute("port");
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

			//ProcessType Name Attr
			Attr attrTestMode = doc.createAttribute("TestMode");
			attrTestMode.setValue("0");
			rootElement.setAttributeNode(attrTestMode);

			//Port Attr
			Attr attrPort = doc.createAttribute("port");
			attrPort.setValue("50000");
			rootElement.setAttributeNode(attrPort);


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
