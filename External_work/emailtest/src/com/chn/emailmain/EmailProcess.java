package com.chn.emailmain;

import java.io.BufferedReader;  
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import javax.mail.MessagingException;

import com.chn.emailservice.emailService;

public class EmailProcess extends mainServer{
	public enum MessageReadType{
		NETWORK_READ,
		CONSOL_READ
	};

	private Socket mSocket;
	private BufferedReader mInMsg;
	private BufferedWriter mOutMsg;

	private grobalConfig mGroConf;

	MessageReadType mReadType;

	public EmailProcess(grobalConfig grobalconf){
		mGroConf = grobalconf;
	}

	public void run() throws MessagingException{
		mMainUI.mTextArea.append("Movie Player Thr Start\n");
		mReadType = MessageReadType.CONSOL_READ;

		MessageBufferRead(mReadType);

		try {
			processMsg(mInMsg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run(Socket socket) throws MessagingException{
		mMainUI.mTextArea.append("Movie Player Thr Start\n");
		mSocket = socket;
		mReadType = MessageReadType.NETWORK_READ;

		MessageBufferRead(mReadType);

		try {
			processMsg(mInMsg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Message Process
	private void processMsg(BufferedReader InMsg) throws IOException, MessagingException {
		boolean sendMailSuccess = false;

		while(true){
			String inLine = InMsg.readLine();

			if(inLine == null){
				mMainUI.mTextArea.append("command is null\n");
				System.out.println("command is null");
				return;
			} 

			if(mReadType == MessageReadType.NETWORK_READ){
				if(!mSocket.isConnected()){
					return;
				}
			} 

			String emailFromAddress = "google_id@gmail.com";
			emailService service = new emailService(mGroConf);

			System.out.println(mGroConf.getTestEmail());
			if(mReadType == MessageReadType.CONSOL_READ){
				String[] sendTo = { mGroConf.getTestEmail()};
				service.sendSSLMessage(sendTo, mGroConf.getEmailSubject(), 
						mGroConf.getEmailContent(), mGrobalconf.getFileLocation(), 
						emailFromAddress);
				mMainUI.mTextArea.append(sendTo[0]);
			} else {
				String email = inLine.trim();

				if(checkEmail(email)){
					String[] sendTo = { inLine};
					service.sendSSLMessage(sendTo, mGroConf.getEmailSubject(), 
							mGroConf.getEmailContent(), mGrobalconf.getFileLocation(), 
							emailFromAddress);
					mMainUI.mTextArea.append(sendTo[0]);

					sendMailSuccess = true;
					mMainUI.mTextArea.append("E-mail successfully sent!!\n");
				} else {
					sendMailSuccess = false;
					mMainUI.mTextArea.append("E-mail failed sent!!\n");				
				}

				mOutMsg = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));

				if(sendMailSuccess){
					mOutMsg.write("<?xml version=\"1.0\" encoding=\"utf-8\"?><MESSAGE text=\"true\" />");				
				}else {
					mOutMsg.write("<?xml version=\"1.0\" encoding=\"utf-8\"?><MESSAGE text=\"false\" />");				
				}

				mOutMsg.newLine();
				mOutMsg.flush();

			}

			mMainUI.mTextArea.append("Client Send Msg " + sendMailSuccess + " \n");
		}
	}

	private boolean checkEmail(String email) {

		if(email.isEmpty()){ // 공백 문자열 확인
			return false;
		}

		if(!email.contains("@")){ //@ 문자 확인
			return false;
		}

		return true;
	}

	// Message Buffer Init
	private void MessageBufferRead(MessageReadType readType){
		try {
			if(readType == MessageReadType.NETWORK_READ){
				mInMsg = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "KSC5601"), 1024);
			}
			else if(readType == MessageReadType.CONSOL_READ){
				System.out.println("CONSOLMODE : ");
				InputStreamReader isr = new InputStreamReader(System.in);
				mInMsg = new BufferedReader(isr);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
