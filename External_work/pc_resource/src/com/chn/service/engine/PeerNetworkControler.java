package com.chn.service.engine;

import java.io.BufferedReader;
import java.io.IOException; 
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.chn.event.Event;
import com.chn.resource.VisitedEventArgs;

public class PeerNetworkControler {
	private int iServerPort = 10000;
	private int SIZE_PACKETBUFFER = 100;
	private final String BROADCAST_ARRESS = "255.255.255.255";

	private boolean mReviceBroadcastSockeEnable;
	private DatagramSocket mBroadCastDatagramSocket;

	public Event<VisitedEventArgs> mVisited = new Event<VisitedEventArgs>();

	// Server Socket
	private ServerSocket mServerSocket;
	private ArrayList<Socket> mMessageSendSocketList;

	// Client Socket
	private Socket mClientSocket;
	private PrintWriter mClientPrintWriter;

	public void initSocketServer(int port){
		try {
			mServerSocket = new ServerSocket(port);
			mMessageSendSocketList = new ArrayList<Socket>();

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
		}
	}

	public void initClientSocket(String address, int port){
		try {
			mClientSocket = new Socket(address, port);

			if(mClientSocket.isConnected()){
				OutputStream out = mClientSocket.getOutputStream();
				InputStream in = mClientSocket.getInputStream();
				final BufferedReader br = new BufferedReader(new InputStreamReader(in));

				Thread clientMessage = new Thread(new Runnable() {

					@Override
					public void run() {
						while(mClientSocket.isConnected()){
							try {
								String Message = br.readLine();
								System.out.println("Message : " + Message);
							} catch (IOException e) {
								e.printStackTrace();
							}


						}
					}
				});

				clientMessage.start();

				mClientPrintWriter = new PrintWriter(new OutputStreamWriter(out));
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

	public void broadcastNetwork(String command) {
		try {
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);

			InetAddress inetAddress = InetAddress.getByName(BROADCAST_ARRESS);
			DatagramPacket datagramPacket = new DatagramPacket(command.getBytes() , 
					command.length(),
					inetAddress, 
					iServerPort );

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

	public void reveiceBr(){
		try {
			mBroadCastDatagramSocket = new DatagramSocket(iServerPort);
			DatagramPacket datagramPacket = new DatagramPacket( new byte[100] , SIZE_PACKETBUFFER );
			mReviceBroadcastSockeEnable = true;

			while(mReviceBroadcastSockeEnable){
				mBroadCastDatagramSocket.receive(datagramPacket);
				String  strPacket = new String( datagramPacket.getData(), 0, datagramPacket.getLength() );
				StringTokenizer token = new StringTokenizer(strPacket, NetworkCommand.SPLIT);

				System.out.println("-- : " + token.countTokens());
				String command = token.nextToken();
				System.out.println("-+ : " + command);

				if(NetworkCommand.COMMAND_SCAN.equals(command)){
					sendMessage(datagramPacket , "Message");

					VisitedEventArgs arg = new VisitedEventArgs(VisitedEventArgs.NETWORK_MESSGAE, strPacket);
					mVisited.raiseEvent(this, arg);
				} else if(NetworkCommand.COMMAND_INFO.equals(command)){
					VisitedEventArgs arg = new VisitedEventArgs(VisitedEventArgs.NETWORK_MESSGAE, strPacket);
					mVisited.raiseEvent(this, arg);
				} else if(NetworkCommand.COMMAND_SHUTDOWN.equals(command)){
					VisitedEventArgs arg = new VisitedEventArgs(VisitedEventArgs.POWER_OFF, strPacket);
					mVisited.raiseEvent(this, arg);					
				}
			}

			mBroadCastDatagramSocket.close();

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendMessage(DatagramPacket packet, String msg) throws IOException {
		DatagramSocket socket = new DatagramSocket();
		InetAddress inetAddress = packet.getAddress();

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
			if(mBroadCastDatagramSocket != null){
				if(!mBroadCastDatagramSocket.isClosed()){
					mBroadCastDatagramSocket.close();
				}
			}
		}
	}
}
