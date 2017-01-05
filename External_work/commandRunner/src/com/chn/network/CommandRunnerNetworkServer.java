package com.chn.network;

import java.awt.AWTException; 
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader; 
import java.io.BufferedWriter;
import java.io.IOException;   
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.chn.commandrunner.MainService;
import com.chn.commandrunner.grobalConfig;

public class CommandRunnerNetworkServer extends MainService{
	private boolean mNetworkStart = false;
	private ServerSocket mSerSockDes;
	private Socket mSockDes;
	private ArrayList<CommandRunnerProcess> mList;
	
	private final String COMMAND_SPACE = "SPACE";
	private final String COMMAND_ENTER = "ENTER";
	private final String COMMAND_RIGHT = "RIGHT";
	private final String COMMAND_LEFT = "LEFT";
	private final String COMMAND_UP = "UP";
	private final String COMMAND_DOWN = "DOWN";
	private final String COMMAND_CLICK = "CLICK";
	private final String COMMAND_DOUBLE_CLICK = "DOUBLECLICK";
	private final String PING = "PING";
	private final String PONG = "PONG";

	public CommandRunnerNetworkServer(){
		CreateNetworkServer(0);
	}

	public CommandRunnerNetworkServer(int port){
		CreateNetworkServer(port);
	}

	// Create Network Socket
	private void CreateNetworkServer(int port){
		try{
			if(port != 0){
				mSerSockDes = new ServerSocket(port);
				mList = new ArrayList<CommandRunnerProcess>();
				
				if(mSerSockDes.isBound()){
					mNetworkStart = true;
				}
			}
			else{
				mNetworkStart = false;
			}
		} catch(MalformedURLException e){
			shutdownServer();
			e.printStackTrace();
		} catch (IOException e) {
			shutdownServer();
			e.printStackTrace();
		}
	}

	public Socket getSocket(){
		return this.mSockDes;
	}

	public ServerSocket getSerSocket(){
		return this.mSerSockDes;
	}

	public void Start(grobalConfig grobalconf){
		try{
			mMainUI.textFiledApped("Server IP : "  + mServer.getSerSocket().getLocalSocketAddress());

			CommandRunnerProcess netProcess = null;

			while(true){
				if(mNetworkStart){
					mMainUI.textFiledApped("System Message Watting");
					netProcess = new CommandRunnerProcess(mSerSockDes.accept());
					mList.add(netProcess);
					netProcess.start();
				} else{
					mMainUI.textFiledApped("Socket Open Error\n");
					break;
				}
			}
		} catch(IOException e){
			shutdownServer();
			e.printStackTrace();
		}
	}

	// Network Connection Shutdown
	public void shutdownServer() {
		try {
			if(mSockDes != null){
				if(mSockDes.isConnected()){
					if(!mSockDes.isInputShutdown())
						mSockDes.shutdownInput();

					if(!mSockDes.isOutputShutdown())
						mSockDes.shutdownOutput();

					if(!mSockDes.isClosed())
						mSockDes.close();
				}
			}
			if(mSerSockDes != null){
				if(mSerSockDes.isClosed())
					mSerSockDes.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	class CommandRunnerProcess extends Thread{
		private BufferedReader mInMsg;
		private BufferedWriter mOutMsg;
		private Socket mSocketDes;
		
		/**
		 * @param accept
		 */
		public CommandRunnerProcess(Socket accept) {
			mSocketDes = accept;
		}

		@Override
		public void run() {
			boolean isStop = false;  //  flag value(깃발 값)
			
			try {
				mInMsg = new BufferedReader(new InputStreamReader(mSocketDes.getInputStream()), 1024);
				mOutMsg = new BufferedWriter(new OutputStreamWriter(mSocketDes.getOutputStream()));
				
				String message = null;  
			
				while(! isStop) {
					if(!mSocketDes.isConnected()){
						break;
					}
					
					String context = mInMsg.readLine();
					
					if(context != null){
						message = String.format("%s", context);
					} else {
						message = "exit";
					}
					
					if(message.equals("exit")) { // 홍길동#exit, 종료하겠다는 뜻
						mMainUI.textFiledApped(mSocketDes.getInetAddress() + " : " + message);
						isStop = true;  //  종료
					} else if(message.equals(PING)){
						send(PONG);
					} else {
						systemCommand(message);
					}//else

				}//while
				
				mList.remove(this);//홍길동을 뺀다.
				mMainUI.textFiledApped(mSocketDes.getInetAddress() + 
						" IP 주소의 사용자께서 종료하셨습니다.");
			} catch (Exception e) {
				e.printStackTrace();
				
				mList.remove(this);
				mMainUI.textFiledApped(mSocketDes.getInetAddress() + 
						" IP 주소의 사용자께서 비정상 종료하셨습니다.");
			}//catch
		}//run
		
		public void systemCommand(String message) throws AWTException{
			mMainUI.textFiledApped(message);
			Robot robot = new Robot();
			int mouseX, mouseY;
			
			if(message.toUpperCase().equals(COMMAND_SPACE)){
			    pressKeyEvent(robot, KeyEvent.VK_SPACE);
			} else if(message.toUpperCase().equals(COMMAND_ENTER)){
			    pressKeyEvent(robot, KeyEvent.VK_ENTER);
			} else if(message.toUpperCase().equals(COMMAND_UP)){
			    pressKeyEvent(robot, KeyEvent.VK_UP);
			} else if(message.toUpperCase().equals(COMMAND_DOWN)){
			    pressKeyEvent(robot, KeyEvent.VK_DOWN);
			} else if(message.toUpperCase().equals(COMMAND_LEFT)){
			    pressKeyEvent(robot, KeyEvent.VK_LEFT);
			} else if(message.toUpperCase().equals(COMMAND_RIGHT)){
			    pressKeyEvent(robot, KeyEvent.VK_RIGHT);
			} else if(message.toUpperCase().equals(COMMAND_CLICK)){
				mouseX = Integer.parseInt(mGrobalconf.getMousePointX(0));
				mouseY = Integer.parseInt(mGrobalconf.getMousePointY(0));

				robot.mouseMove(mouseX, mouseY);
				MouseClick(robot);
			} else if(message.toUpperCase().equals(COMMAND_DOUBLE_CLICK)){
				mouseX = Integer.parseInt(mGrobalconf.getMousePointX(0));
				mouseY = Integer.parseInt(mGrobalconf.getMousePointY(0));

				robot.mouseMove(mouseX, mouseY);
			    robot.delay(500);
				MousedoubleClick(robot);
			} else {
				Process process;
				try {
	                Runtime runtime = Runtime.getRuntime();

					process = runtime.exec(message);
					
					robot.keyPress(KeyEvent.VK_ALT);
					robot.keyPress(KeyEvent.VK_TAB);
					robot.keyRelease(KeyEvent.VK_ALT);
					robot.keyRelease(KeyEvent.VK_TAB);
					
					robot.setAutoDelay(40);
				    robot.setAutoWaitForIdle(true);
				     
					BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line = null;
					while( (line = br.readLine()) != null )
						mMainUI.textFiledApped(line);
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		

		private void pressKeyEvent(Robot robot, int vkSpace) {
			robot.keyPress(vkSpace);
			robot.keyRelease(vkSpace);
		}

		private void MouseClick(Robot robot) {
		    robot.mousePress(InputEvent.BUTTON1_MASK);
		    robot.delay(200);
		    robot.mouseRelease(InputEvent.BUTTON1_MASK);
		    robot.delay(200);			    
		}
		
		private void MousedoubleClick(Robot robot) {
		    robot.mousePress(InputEvent.BUTTON1_MASK);
		    robot.delay(200);
		    robot.mouseRelease(InputEvent.BUTTON1_MASK);
		    robot.delay(200);			    
		    robot.mousePress(InputEvent.BUTTON1_MASK);
		    robot.delay(200);
		    robot.mouseRelease(InputEvent.BUTTON1_MASK);
		    robot.delay(200);
		}

		public void broadCasting(String message) {//모두에게 전송
			for (CommandRunnerProcess ct : mList) {
				ct.send(message);
			}//for
		}//broadCasting
		
		public void send(String message) {  //  한 사용자에게 전송
			try {
				mOutMsg.write(message);				
				mOutMsg.newLine();
				mOutMsg.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}//catch
		}//send
	}//내부 클래스
}
