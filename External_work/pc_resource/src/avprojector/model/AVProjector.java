/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package avprojector.model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import pjLink.PJLinkC1;

/**
 *
 * @author Long
 */
public class AVProjector
{

    private static int sdcpPort             = 51217;
    private static int pjLinkPort           = 4352;
    private static int httpPort             = 80;

    private static int powerStatusIndex     = 7;
    private static int inputStatusIndex     = 7;
    private static String defaultPJLinkPW   = "JBMIAProjectorLink";
    private static String MD5               = "MD5";

    private InetAddress mIPAddress;
    private String mHostName;
    private String mProjName;

    public static enum PowerState{
            on,
            off,
            cooling, 
            warmup,
            undefined
    }
    public static enum InputState{
            input1,
            input2,
            input3,
            video,
            svideo,
            undefined
    }
    protected PowerState mPower;
    protected InputState mInput;

    public AVProjector( String hostName, InetAddress ipAddress, String projName )
    {
        mPower          = PowerState.undefined;
        mInput          = InputState.undefined;
        mIPAddress      = ipAddress;
        mHostName       = hostName;
        mProjName       = projName;
    }

    public AVProjector( String hostName, String ipAddress, String projName )
    {
        try
        {
            mIPAddress  = InetAddress.getByName( ipAddress );
        }
        catch ( UnknownHostException e )
        {
            System.out.println( "Unknown Host Exception: " + e );
        }
        mPower        = PowerState.undefined;
        mInput        = InputState.undefined;
        mHostName   = hostName;
        mProjName   = projName;

    }
    
    // trys to set up a tcp connection to the projector
    public boolean PingProjector()
    {
        Socket socket;

        //connect to the server
        try
        {
            SocketAddress sockaddr = new InetSocketAddress(mIPAddress, pjLinkPort);
            socket = new Socket( );
            socket.connect( sockaddr, 2000 );
            
            socket.close();
            return true;
        }

        catch (IOException e)
        {
            //System.out.println("Server is offline.\n" + e );
        }

        return false;
    }
    
/*        // trys to set up a tcp connection to the projector
    public String CheckPower()
    {
        Socket pjLinkSocket;
        String returnValue = "";

        //connect to the server
        try
        {
            SocketAddress sockaddr = new InetSocketAddress(mIPAddress, pjLinkPort);
            pjLinkSocket = new Socket(mIPAddress, pjLinkPort);
            
            DataOutputStream ostream = new DataOutputStream(pjLinkSocket.getOutputStream());
            BufferedReader istream = new BufferedReader(new InputStreamReader(pjLinkSocket.getInputStream()));

            String line = istream.readLine();
            String[] tokens = line.split(" ");

            MessageDigest digest = MessageDigest.getInstance( MD5 );

            //System.out.println(line);
            String passwordString = tokens[2] +""+ defaultPJLinkPW;
            //System.out.println(passwordString);

            digest.update(passwordString.getBytes());
            byte[] bytes = digest.digest();
            BigInteger bi = new BigInteger(1, bytes);
            String hex = String.format("%0" + (bytes.length << 1) + "x", bi);

            //System.out.println(hex);

            ostream.writeBytes(hex+"%1POWR ?\r");

            //System.out.println(istream.readLine());

            String powerResponse = istream.readLine().substring(7);

            // off
            if( powerResponse.compareTo("0") == 0 )
            {
                mPower = PowerState.off;
                returnValue = "Off";
            }

            // on
            else if ( powerResponse.compareTo("1") == 0 )
            {
                mPower = PowerState.on;
                returnValue = "On";
            }

            // cooling
            else if ( powerResponse.compareTo("2") == 0 )
            {
                mPower = PowerState.cooling;
                returnValue = "Cooling";
            }

            // warmup
            else if ( powerResponse.compareTo("3") == 0 )
            {
                mPower = PowerState.warmup;
                returnValue = "Warmup";
            }

            // Unavailable time
            else if ( powerResponse.compareTo("ERR3") == 0 )
            {
                mPower = PowerState.undefined;
                returnValue = "UNAVAILABLE TIME";
            }

            // Projector failure
            else if ( powerResponse.compareTo("ERR4") == 0)
            {
                mPower = PowerState.undefined;
                returnValue = "PROJECTOR FAILURE";
            }

            pjLinkSocket.close();
        }

        catch (IOException e)
        {
            returnValue = "COULD NOT CONNECT TO PROJECTOR";
        }

        catch (NoSuchAlgorithmException e)
        {
            returnValue = "NO SUCH ALGORITHM";
        }

        return returnValue;
    }
*/

    public void CheckPower( int row, int column )
    {
        PJLinkC1.CheckPower(this, mIPAddress, pjLinkPort, defaultPJLinkPW, row, column );
    }

    public void CheckInput( int row, int column )
    {
        PJLinkC1.CheckInput(this, mIPAddress, pjLinkPort, defaultPJLinkPW, row, column );
    }

    public void TurnOnOff( int row, int column )
    {
        
        if(this.mPower == PowerState.on)
        {
            
            PJLinkC1.TurnOff(this, mIPAddress, pjLinkPort, defaultPJLinkPW, row, column );
            return;
        }
        else if( this.mPower == PowerState.off )
        {
            
            PJLinkC1.TurnOn(this, mIPAddress, pjLinkPort, defaultPJLinkPW, row, column );
            return;
        }

    }

    public void GoToWebServer( int row, int column ) throws IOException
    {
        java.awt.Desktop.getDesktop().browse(java.net.URI.create(this.mHostName));
    }

    public String CheckInput( )
    {
        Socket pjLinkSocket;
        String returnValue = "";

        //connect to the server
        try
        {
            SocketAddress sockaddr = new InetSocketAddress(mIPAddress, pjLinkPort);
            pjLinkSocket = new Socket(mIPAddress, pjLinkPort);

            DataOutputStream ostream = new DataOutputStream(pjLinkSocket.getOutputStream());
            BufferedReader istream = new BufferedReader(new InputStreamReader(pjLinkSocket.getInputStream()));

            String line = istream.readLine();
            String[] tokens = line.split(" ");

            MessageDigest digest = MessageDigest.getInstance( MD5 );

            String passwordString = tokens[2] +""+ defaultPJLinkPW;

            digest.update(passwordString.getBytes());
            byte[] bytes = digest.digest();
            BigInteger bi = new BigInteger(1, bytes);
            String hex = String.format("%0" + (bytes.length << 1) + "x", bi);

            ostream.writeBytes(hex+"%1INPT ?\r");

            String powerResponse = istream.readLine().substring(inputStatusIndex);

            // Video
            if( powerResponse.compareTo("21") == 0 )
            {
                mPower = PowerState.off;
                returnValue = "Video";
            }

            // Computer
            else if ( powerResponse.compareTo("31") == 0 )
            {
                mPower = PowerState.cooling;
                returnValue = "Computer";
            }

            // Unavailable time
            else if ( powerResponse.compareTo("ERR3") == 0 )
            {
                mPower = PowerState.undefined;
                returnValue = "Unavailable Time";
            }

            // Projector failure
            else if ( powerResponse.compareTo("ERR4") == 0)
            {
                mPower = PowerState.undefined;
                returnValue = "Projector Failure";
            }

            // Set to some other input
            else
            {
                mInput = InputState.undefined;
                returnValue = "Unknown Input";
            }

            pjLinkSocket.close();
        }

        catch (IOException e)
        {
            returnValue = "Error: Could not connect to projector";
        }

        catch (NoSuchAlgorithmException e)
        {
            returnValue = "Error: No such algorithm";
        }

        return returnValue;
    }

    public InetAddress GetIPAddress( )
    {
        return mIPAddress;
    }

    public void SetIPAddress( InetAddress ipAddress )
    {
        mIPAddress = ipAddress;
    }

    public String GetHostName( )
    {
        return mHostName;
    }

    public void SetHostName( String hostName )
    {
        mHostName = hostName;
    }

    public String GetProjName( )
    {
        return mProjName;
    }

    public void SetProjName( String projName )
    {
        mProjName = projName;
    }

    public void SetPowerState( PowerState state )
    {
        mPower = state;
    }

    public void SetPowerState( String state )
    {
        int iState = Integer.parseInt(state);

        switch( iState )
        {
            case 0:
                mPower = PowerState.off;
                break;
            case 1:
                mPower = PowerState.on;
                break;
            case 2:
                mPower = PowerState.cooling;
                break;
            case 3:
                mPower = PowerState.warmup;
                break;
            default:
                mPower = PowerState.undefined;
                break;

        }
    }


}
