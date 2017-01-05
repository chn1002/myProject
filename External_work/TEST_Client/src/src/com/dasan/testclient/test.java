package src.com.dasan.testclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class test
{

	public static void main(String[] args ){
		Thread socketThread = new Thread(new Runnable()
		{
			public void run()
			{
				for (;;)
				{
					int ringingFlag;
					int tryingFlag;
					int m;
					int n;
					String message;
					try
					{
						ServerSocket localServerSocket = new ServerSocket(8888);

						Socket sipClientSocket = localServerSocket.accept();
						String sipClientMessage;
						ringingFlag = 0;
						tryingFlag = 0;
						int sessionPregressFlag = 0;
						try
						{
							InputStream clientInputStream = ((Socket)sipClientSocket).getInputStream();
							new PrintWriter(((Socket)sipClientSocket).getOutputStream(), true).println("Hello from corpus middleware");
							BufferedReader clientBufferReader = new BufferedReader(new InputStreamReader((InputStream)clientInputStream));
							sipClientMessage = ((BufferedReader) clientBufferReader).readLine();
							if (sipClientMessage == null) {
								continue;
							}
							System.out.println("Parse SIP_RESPONSE_LINE" + (String) sipClientMessage);
							if (((String)sipClientMessage).trim().endsWith("Ringing"))
							{
								System.out.println("Parse " + "Ringing matched");
								ringingFlag = 1;
							}
							if (((String)sipClientMessage).trim().endsWith("Trying"))
							{
								System.out.println("Parse "+ "Trying matched");
								tryingFlag = 1;
							}
							if (((String)sipClientMessage).trim().endsWith("Session Progress"))
							{
								System.out.println("Parse "+ "Session in progress");
								sessionPregressFlag = 1;
							}
							if (sessionPregressFlag == 0) {
								if ((tryingFlag != 0) && (ringingFlag == 0)) // Ring Flag
								{
									m = ringingFlag;
									n = tryingFlag;
									if (sipClientMessage.trim().startsWith("To"))
									{
										System.out.println("Parse Trying"+ "Outgoing trying call");
										message = sipClientMessage.substring(9, sipClientMessage.lastIndexOf("@"));
										System.out.println("Parse Trying"+ message);
//										MainActivity.myJavaScriptInterface.showClipMessage(3, message);
										n = 1;
										m = ringingFlag;
									}
								}
								else // trying Flag
								{
									m = ringingFlag;
									n = tryingFlag;
									if (ringingFlag != 0) {
										if (tryingFlag != 0)
										{
											m = ringingFlag;
											n = tryingFlag;
											if (sipClientMessage.trim().startsWith("To"))
											{
												System.out.println("Parse Trying + Ringing"+ "Outgoing trying call ringing now");
												message = sipClientMessage.substring(9, sipClientMessage.lastIndexOf("@"));
												System.out.println("Parse Trying + Ringing"+ message);
//												MainActivity.myJavaScriptInterface.showClipMessage(4, message);
												System.out.println("4 "+ message);
												m = ringingFlag;
												n = tryingFlag;
											}
										}
										else
										{
											m = ringingFlag;
											n = tryingFlag;
											if (sipClientMessage.trim().startsWith("From"))
											{
												System.out.println("Parse Ringing"+ "Matched from");
												message = sipClientMessage.substring(sipClientMessage.lastIndexOf("<") + 5, sipClientMessage.lastIndexOf("@"));
												System.out.println("Parse Ringing 1"+ message);
//												MainActivity.myJavaScriptInterface.showClipMessage(1, message);
												System.out.println("1 "+ message);
												m = 0;
												n = tryingFlag;
											}
										}
									}
								}							}

							if ((ringingFlag != 0) && (tryingFlag != 0)) // Session Progress
							{
								m = ringingFlag;
								n = tryingFlag;
								if (((String)sipClientMessage).trim().startsWith("To"))
								{
									System.out.println("Parse in progress " + "Outgoing progress");
									message = ((String)sipClientMessage).substring(9, ((String)sipClientMessage).lastIndexOf("@"));
									System.out.println("Parse in progress " + message);
									//MainActivity.myJavaScriptInterface.showClipMessage(4, message);
									System.out.println("4 : " + message);
									n = tryingFlag;
									m = ringingFlag;
								}
								if ((!((String)sipClientMessage).trim().endsWith("Request Terminated")) && (!((String)sipClientMessage).trim().endsWith("BYE")))
								{
									tryingFlag = n;
									if (!((String)sipClientMessage).trim().endsWith("Temporarily Unavailable")) {}
								}
								else
								{
									System.out.println("Parse" + "Call terminated");
									//MainActivity.myJavaScriptInterface.hideClipMessage();
									System.out.println("Hidden ");
									tryingFlag = 0;
									m = 0;
									sessionPregressFlag = 0;
								}
								sipClientMessage = ((BufferedReader) clientBufferReader).readLine();
								ringingFlag = m;
								continue;
							}

							// Ringing Flag & Trying Flag
							m = ringingFlag;
							n = tryingFlag;

							if (!((String)sipClientMessage).trim().startsWith("From")) {
								continue;
							}
							System.out.println("Parse in progress" + "Incoming in progress");
							message = ((String)sipClientMessage).substring(6, ((String)sipClientMessage).lastIndexOf("<"));
							System.out.println("Parse in progress" + message);
//							MainActivity.myJavaScriptInterface.showClipMessage(2, message);
							System.out.println("2 : " + message);
							m = ringingFlag;
							n = tryingFlag;
							continue;
						}
						catch (IOException localIOException1)
						{
							localIOException1.printStackTrace();
						}

						return;
					}
					catch (IOException localIOException2)
					{
						localIOException2.printStackTrace();
					}
				}
			}
		});
		
		socketThread.start();

		return;
	}
}
