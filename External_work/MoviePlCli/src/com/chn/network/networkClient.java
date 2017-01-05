package com.chn.network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;

public class networkClient
{
	private Socket mSockDes;

	public static enum ModeType
	{
		NETWORK_MODE, 
		CONSOL_MODE;
	}



	public networkClient(String ip, int port)
	{
		CreateNetworkClient(ip, port);
	}

	private void CreateNetworkClient(String ip, int port)
	{
		try {
			this.mSockDes = new Socket(ip, port);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Socket getSocket() {
		return this.mSockDes;
	}

	public void sendMessage(String msg) {
		try {
			PrintWriter out = new PrintWriter(new java.io.OutputStreamWriter(this.mSockDes.getOutputStream()));
			out.println(msg);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			this.mSockDes.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}

