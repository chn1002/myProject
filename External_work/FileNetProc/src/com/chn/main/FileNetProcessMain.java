package com.chn.main;

import java.io.File; 
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import network.HttpCli;
import network.ftpclient;

public class FileNetProcessMain {
	private static boolean mStarted =false;
	
	private static grobalConfig mGrobalconf;
	
	public static void main(String[] args){
		System.out.println("File Process");
		HttpCli httpClient = new HttpCli();
		
		// Program Start
		initSystem();

		if(mStarted){
			fileAccess();
			
			httpClient.httpclient(mGrobalconf.getHTTPUrl());
		}
	}
	
	private static void fileAccess() {
		File[] fileList = (new File(mGrobalconf.getLocalFileDir())).listFiles();
		File copyFile = null;
		long fileModefyTime = 0;
		
		for(int i =0; i < fileList.length; i++){
			System.out.println(fileList[i].getName());
			if(fileList[i].getName().contains(".jpg")){
				if(fileModefyTime < fileList[i].lastModified()){
					copyFile = fileList[i];			
				}
			}
		}
		
		if(copyFile != null)
			makefile(copyFile);
	}

	private static void makefile(File file) {
		if(mGrobalconf.getFileNumber() >= mGrobalconf.getMaxFileNumber()){
			mGrobalconf.setFileNumber(0);
		}
		
		if(file.getName().contains(".jpg")){
			FileInputStream inputStream = null;
			FileOutputStream outputStream = null;
			String FileName = mGrobalconf.getRemoteFileDir() + mGrobalconf.getFileName() + mGrobalconf.getFileNumber() + ".jpg";
			
			File saveFile = new File(FileName);
			
			try {
				inputStream = new FileInputStream(file);
				outputStream = new FileOutputStream(saveFile);
				int fileSize = (int) file.length();
				byte[] buf = new byte[fileSize + 1];
				int len;
				
				while((len = inputStream.read(buf))> 0){
					outputStream.write(buf, 0, len);
				}
				
				try {
					// FTP Client
					ftpclient ftpcli = new ftpclient();
					//upload(String ftpServer, String user, String password, String fileName, File source)
					ftpcli.upload(mGrobalconf.getFTPUrl(), mGrobalconf.getFTPUser(), mGrobalconf.getFTPPassword(), "./html/img.jpg", file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				inputStream.close();
				outputStream.close();
				
				// Original File Delete
				mGrobalconf.setFileNumber();
				//file.delete();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void initSystem(){
		
		mGrobalconf = new grobalConfig();
		
		mStarted = true;

	}
}
