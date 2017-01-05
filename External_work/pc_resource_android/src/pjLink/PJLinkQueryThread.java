/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pjLink;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Long
 */
public class PJLinkQueryThread extends Thread
{

    private static final int    PasswordIndex   = 2;
    private static final String MD5             = "MD5";

    PJLinkC1.CommandType mCommandType;

    int mPJLinkPort;
    
    String mQuery;
    String mPassword;
    String mResponse;

    int mRow;
    int mColumn;
    int mWaitTime;
    
    InetAddress mProjIP;

    PJLinkQueryThread(PJLinkC1.CommandType commandType, String query, String password, InetAddress projIP, int port, int row, int column, int waitTime )
    {
        mCommandType= commandType;
        mQuery      = query;
        mPassword   = password;
        mPJLinkPort = port;
        mProjIP     = projIP;

        mRow = row;
        mColumn = column;
        mWaitTime = waitTime;

    }

    @Override
    public void run()
    {
        // if theres a wait time, put this thread to sleep for that time
        if( mWaitTime != 0 )
        {
            try
            {
                this.sleep(mWaitTime);
            }
            catch( InterruptedException e )
            {
                System.out.println(e);
            }
        }
        Socket pjLinkSocket;

        //connect to the server
        try
        {
            
            pjLinkSocket = new Socket(mProjIP, mPJLinkPort);

            DataOutputStream ostream = new DataOutputStream(pjLinkSocket.getOutputStream());
            BufferedReader istream = new BufferedReader(new InputStreamReader(pjLinkSocket.getInputStream()));

            String line = istream.readLine();
            String[] tokens = line.split(" ");

            String digest = GetPasswordDigest( mPassword, tokens[PasswordIndex]);
            ostream.writeBytes( digest + mQuery );

            String response = istream.readLine().substring(7);

            switch(mCommandType)
            {
                case Power:
                {
                    mResponse = (String)PJLinkC1.sPowerStatus.get(response);
                    
                    if( mResponse == null )
                    {
                        mResponse = (String)PJLinkC1.sPowerErrorCodes.get(response);
                    }

                    if( mResponse == null )
                    {
                        mResponse = "Status value: " + response + " not found.";
                    }
                    break;
                }
                case Input:
                {
                    mResponse = (String)PJLinkC1.sInputStatus.get(response);

                    if( mResponse == null )
                    {
                        mResponse = (String)PJLinkC1.sInputErrorCodes.get(response);
                    }

                    if( mResponse == null )
                    {
                        mResponse = "Status value: " + response + " not found.";
                    }
                    break;
                }
            }
            pjLinkSocket.close();
        }

        catch (IOException e)
        {
            mResponse = "COULD NOT CONNECT TO PROJECTOR";
        }

        return;


    }

    public static String GetPasswordDigest( String password, String randomString )
    {
        String pwDigest = "";
        try
        {
            MessageDigest digest = MessageDigest.getInstance( MD5 );

            String passwordString = randomString + password;

            digest.update(passwordString.getBytes());
            byte[] bytes = digest.digest();
            BigInteger bi = new BigInteger(1, bytes);
            pwDigest = String.format("%0" + (bytes.length << 1) + "x", bi);
        }

        catch (NoSuchAlgorithmException e)
        {
            //assert
            pwDigest = "NO SUCH ALGORITHM";
        }

        return pwDigest;
    }

    public String GetResponse()
    {
        return mResponse;
    }
}

