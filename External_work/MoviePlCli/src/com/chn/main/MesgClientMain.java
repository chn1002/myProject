package com.chn.main;

import com.chn.network.networkClient; 
import java.io.BufferedReader;
import java.io.IOException;

public class MesgClientMain
{
	public static networkClient mCli;
	private static networkClient.ModeType mMode;
	private static boolean mStarted = false;
	private static grobalConfig mGrobalconf;

	public static void main(String[] args)
	{
		System.out.println("Message Client");
		String ipAddress;
		int port;


		initSystem();

		if (mStarted) {
			if (mMode == networkClient.ModeType.NETWORK_MODE) {
				if (args[0] == null) {
					CommandError();
					return;
				}

				String Command = null;

				if (args[0].equals("play")) {
					Command = mGrobalconf.getCommandValue("play") + "|" + args[1];
				}
				else if (args[0].equals("replay")) {
					Command = mGrobalconf.getCommandValue("replay") + "|" + args[1];
				}
				else {
					Command = mGrobalconf.getCommandValue("play") + "|" + args[0];
				}

				for(int i=0; i < mGrobalconf.getRemoteServerNumber(); i++){
					System.out.println("Item : " + i + " : " + mGrobalconf.getRemoteServerNumber());
					System.out.println("Remote Server : " + mGrobalconf.getRemoteServerAddress(i));
					System.out.println("Remote Server Port : " + mGrobalconf.getRemoteServerPort(i));
					ipAddress = mGrobalconf.getRemoteServerAddress(i);
					port = Integer.parseInt(mGrobalconf.getRemoteServerPort(i));
					
					mCli = new networkClient(ipAddress, port);
					System.out.println("Command : " + Command);
					mCli.sendMessage(Command);
				}
				
			} else {
				System.out.print("TestMode Input Message : ");
				java.io.InputStreamReader isr = new java.io.InputStreamReader(System.in);
				BufferedReader InMsg = new BufferedReader(isr);
				try
				{
					mCli.sendMessage(InMsg.readLine());
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
//			mCli.close();
		}
	}

	private static void CommandError() {
		System.out.println("Miss Command");
		System.out.println("Usage: excu command playername");
		System.out.println(" command: play, replay");
	}

	private static void initSystem()
	{
		mGrobalconf = new grobalConfig();

		for (int temp = 0; temp < mGrobalconf.getCommandLength(); temp++) {
			System.out.print("Grobal Conf Name : " + mGrobalconf.getCommandName(temp) + 
					", Grobal Conf Value : " + mGrobalconf.getCommandValue(temp) + "\n");
		}

		for (int temp = 0; temp < mGrobalconf.getMediaLength(); temp++) {
			System.out.print("Grobal Conf Name : " + mGrobalconf.getMediaName(temp) + 
					", Grobal Conf Value : " + mGrobalconf.getMediaValue(temp) + "\n");
		}

		if (mGrobalconf.isTestMode()) {
			mMode = networkClient.ModeType.CONSOL_MODE;
		}
		else {
			mMode = networkClient.ModeType.NETWORK_MODE;
		}

		mStarted = true;
	}
}


