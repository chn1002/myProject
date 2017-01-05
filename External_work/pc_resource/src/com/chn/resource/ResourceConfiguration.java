package com.chn.resource;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class ResourceConfiguration {
	XMLConfiguration mXMLConfig;
	
	private final String VERSION_KEY = "version";
	
	private final String DEFAULT_VERSION = "1.0";

	final String ConfigFileName = "config.xml";

	public ResourceConfiguration() {
		initXMLConfiguration();
	}
	
	private void initXMLConfiguration(){
		try {
			mXMLConfig = new XMLConfiguration();
			mXMLConfig.setFileName(ConfigFileName);
			mXMLConfig.load(ConfigFileName);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public void saveXMLConfiguration(){
		try {
			mXMLConfig.save();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public void setVersion(String version){
		if(version == null)
			version = DEFAULT_VERSION;
		
		mXMLConfig.addProperty(VERSION_KEY, version);
	}
	
	public String getVersion(){
		return mXMLConfig.getString(VERSION_KEY, DEFAULT_VERSION);
	}

}
