package com.chn.networkSolution;

import java.io.BufferedReader; 
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

import android.os.Handler;
import android.os.Message;

public class NetworkControl {
	private int iServerPort = 10000;

	private int SIZE_PACKETBUFFER = 100;
	private final String BROADCAST_ARRESS = "255.255.255.255";

	private boolean mReviceBroadcastSockeEnable;

	private DatagramSocket mBroadCastDatagramSocket;

	public final static int INET4ADDRESS = 1;
	public final static int INET6ADDRESS = 2;

	private static final int SERVER_MESSAGE = 0;


	// Server Socket
	private Handler mServerHandler;
	private ServerSocket mServerSocket;
	private ArrayList<Socket> mMessageSendSocketList;

	// Client Socket
	private Handler mClientHandler;
	private Socket mClientSocket;
	private PrintWriter mClientPrintWriter;

	public void initSocketServer(int port, Handler handler){
		try {
			mServerSocket = new ServerSocket(port);
			mMessageSendSocketList = new ArrayList<Socket>();
			mServerHandler = handler;

			Thread socketThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						System.out.println("Wating Connect...");
						Socket sock = mServerSocket.accept();
						mMessageSendSocketList.add(sock);

						ServerMessage reciveMessage = new ServerMessage(sock);
						reciveMessage.start();

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});

			socketThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendServerMessage(String Message){
		if(mServerSocket == null)
			return;

		try {
			for(Socket sock : mMessageSendSocketList){
				OutputStream out = sock.getOutputStream();
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));

				pw.println(Message);
				pw.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendServerMessage(String dest, String Message){
		if(mServerSocket == null)
			return;

		try {
			for(Socket sock : mMessageSendSocketList){
				InetAddress  inetaddr = sock.getInetAddress();
				if(inetaddr.getHostAddress().equals(Message)){
					OutputStream out = sock.getOutputStream();
					PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));

					pw.println(Message);
					pw.flush();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	class ServerMessage extends Thread{
		Socket mSocket;

		public ServerMessage(Socket socket) {
			mSocket = socket;
		}

		@Override
		public void run() {
			InetAddress  inetaddr = mSocket.getInetAddress();
			System.out.println(inetaddr.getHostAddress()+ " Connect");
			
			String msg = "";
			
			try {
				InputStream in = mSocket.getInputStream();
	            BufferedReader br = new BufferedReader(new InputStreamReader(in));
	            
	            msg = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Message serverMessage = mServerHandler.obtainMessage(SERVER_MESSAGE);
			serverMessage.obj = msg;
			mServerHandler.sendMessageAtFrontOfQueue(serverMessage);
		}
	}

	public void initClientSocket(String address, int port, Handler handler){
		try {
			mClientSocket = new Socket(address, port);
			mClientHandler = handler;

			if(mClientSocket.isConnected()){
				OutputStream out = mClientSocket.getOutputStream();
				mClientPrintWriter = new PrintWriter(new OutputStreamWriter(out));

				Thread clientMessage = new Thread(new Runnable() {

					@Override
					public void run() {
						while(mClientSocket.isConnected()){
							try {
								InputStream in = mClientSocket.getInputStream();
								final BufferedReader br = new BufferedReader(new InputStreamReader(in));

								String Message = br.readLine();
								System.out.println("Message : " + Message);
							} catch (IOException e) {
								e.printStackTrace();
							}


						}
					}
				});

				clientMessage.start();
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendClientMessage(String message){
		if(mClientSocket == null){
			System.out.println("Message Send Failed : Client Socket is null");
			return;
		} else if(mClientSocket.isConnected()){
			mClientPrintWriter.println(message);
			mClientPrintWriter.flush();
		} else {
			System.out.println("Message Send Failed : Client Socket is not connected");
			return;
		}
	}
	
	public void broadcastNetwork(String command){
		this.broadcastNetwork(command, iServerPort);
	}

	public void broadcastNetwork(String command, int port) {
		try {
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);

			InetAddress inetAddress = InetAddress.getByName(BROADCAST_ARRESS);
			DatagramPacket datagramPacket = new DatagramPacket( command.getBytes() , 
					command.length(),
					inetAddress, 
					port );

			socket.send(datagramPacket);
			socket.close();

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean SetBroadCast(){
		return this.SetBroadCast(iServerPort);
	}

	public boolean SetBroadCast(int port){
		try {
			mBroadCastDatagramSocket = new DatagramSocket(port);
			mReviceBroadcastSockeEnable = true;

		} catch (SocketException e) {
			e.printStackTrace();
		} 

		return mReviceBroadcastSockeEnable;
	}

	public DatagramPacket reveiceMessage(){
		DatagramPacket datagramPacket = null;

		try {
			datagramPacket = new DatagramPacket( new byte[100] , SIZE_PACKETBUFFER );

			if(mReviceBroadcastSockeEnable){
				mBroadCastDatagramSocket.receive(datagramPacket);
			} 
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return datagramPacket;
	}

	public PeerInformation getData(DatagramPacket packet){
		PeerInformation  peerinfor = new PeerInformation();
		peerinfor.mName = packet.getAddress().getHostAddress();
		peerinfor.mAddress = packet.getAddress();
		peerinfor.data =  new String( packet.getData(), 0, packet.getLength() );

		return peerinfor;
	}

	public void sendMessage(DatagramPacket packet, String msg) throws IOException {
		DatagramSocket socket = new DatagramSocket();
		InetAddress inetAddress = packet.getAddress();

		DatagramPacket datagramPacket = new DatagramPacket( msg.getBytes() , 
				msg.length(),
				inetAddress, 
				iServerPort );

		socket.send(datagramPacket);
		socket.close();
	}

	public void sendMessage(InetAddress inetAddress, String msg) throws IOException {
		DatagramSocket socket = new DatagramSocket();

		DatagramPacket datagramPacket = new DatagramPacket( msg.getBytes() , 
				msg.length(),
				inetAddress, 
				iServerPort );

		socket.send(datagramPacket);
		socket.close();
	}

	public void setmReviceBroadcastSockeEnable(boolean enable) {
		mReviceBroadcastSockeEnable = enable;

		if(!enable){
			if(mBroadCastDatagramSocket != null)
				mBroadCastDatagramSocket.close();
		}
	}

	public String getLocalIpAddress(int type) {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = ( NetworkInterface ) en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = ( InetAddress ) enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						switch (type) {
						case INET6ADDRESS:
							if (inetAddress instanceof Inet6Address) {
								return inetAddress.getHostAddress().toString();
							}
							break;
						case INET4ADDRESS:
							if (inetAddress instanceof Inet4Address) {
								return inetAddress.getHostAddress().toString();
							}
							break;
						}

					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}

		return null;
	}


}
