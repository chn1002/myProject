/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pjLink;

import java.math.BigInteger;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 *
 * @author Long
 */
public class PJLinkC1 {

    enum CommandType
    {
        Power,
        Input
    }

    enum PowerCommands
    {
        Off,
        On,
        Status
    }

    enum InputCommands
    {
        Video,
        InputA,
        Status
    }

    private static final int    CLASS_NUMBER    = 1;
    private static final String MD5             = "MD5";
    protected static boolean    Initialized     = false;

    protected static final String HEADER_PREFIX     = "%";
    protected static final String POWER             = "POWR";
    protected static final String INPUT             = "INPT";

    protected static final int WAIT_TIME           = 1500;

    // translation from projector status number to human readable information
    static HashMap sPowerStatus;

    // translation from human command to projector number command
    static HashMap sPowerCommands;

    // translation from projector error to human readable error
    static HashMap sPowerErrorCodes;

    // translation from projector status number to human readable information
    static HashMap sInputStatus;

    // translation from human command to projector number command
    static HashMap sInputCommands;

    // translation from projector error to human readable error
    static HashMap sInputErrorCodes;

    // put all commands into the hashmaps
    public static void IntializeCommands()
    {

        if( Initialized )
        {
            return;
        }
        //Power commands/status
        sPowerStatus = new HashMap();
        sPowerStatus.put( "0", "Off");
        sPowerStatus.put( "1", "On");
        sPowerStatus.put( "2", "Cooling");
        sPowerStatus.put( "3", "Warmup");

        sPowerCommands = new HashMap();
        sPowerCommands.put( PowerCommands.Off, "0");
        sPowerCommands.put( PowerCommands.On, "1");
        sPowerCommands.put( PowerCommands.Status, "?");

        sPowerErrorCodes = new HashMap();
        sPowerErrorCodes.put( "OK"  , "OK");
        sPowerErrorCodes.put( "ERR2", "Parameter out of range");
        sPowerErrorCodes.put( "ERR3", "Projector not on");
        sPowerErrorCodes.put( "ERR4", "Projector error");

        //Input
        sInputStatus = new HashMap();
        sInputStatus.put( "21", "Video");
        sInputStatus.put( "22", "S-Video");
        sInputStatus.put( "31", "Input A");
        sInputStatus.put( "32", "Input B");
        sInputStatus.put( "33", "Input C");
        sInputStatus.put( "34", "Input D");
        sInputStatus.put( "35", "Input E");
        sInputStatus.put( "36", "Input F");
        sInputStatus.put( "51", "Network");

        sInputCommands = new HashMap();
        sInputCommands.put( InputCommands.Video,   "21");
        sInputCommands.put( InputCommands.InputA, "31");
        sInputCommands.put( InputCommands.Status, "?" );

        sInputErrorCodes = new HashMap();
        sInputErrorCodes.put("ERR2", "Non-existent input");
        sInputErrorCodes.put("ERR3", "Projector not on");
        sInputErrorCodes.put("ERR4", "Projector error");

        Initialized = true;
    }

    // takes a password and the random string and returns the digest
    public static String GetPasswordDigest( String password, String randomString )
    {
        // initialize the return value
        String pwDigest = "";

        try
        {
            // get the MD5 digest
            MessageDigest digest = MessageDigest.getInstance( MD5 );

            // append the password to the random string
            String passwordString = randomString + password;

            // get the digest in bytes and format it as hexidecimal then convert it to a string
            digest.update(passwordString.getBytes());
            byte[] bytes = digest.digest();
            BigInteger bi = new BigInteger(1, bytes);
            pwDigest = String.format("%0" + (bytes.length << 1) + "x", bi);
        }

        // couldn't find the MD5 algorithm
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            pwDigest = "NO SUCH ALGORITHM";
        }


        return pwDigest;

    }

    // sends a query to the projector requesting the information on the power status
    public static void CheckPower( InetAddress projIP, int pjLinkPort, String password, int row, int column )
    {

        // create the query object
        String query = CreateQuery( CLASS_NUMBER, POWER, (String)sPowerCommands.get(PowerCommands.Status) );

        PJLinkQueryThread pjQuery = new PJLinkQueryThread(CommandType.Power, query, password, projIP, pjLinkPort, row, column, 0 );
        pjQuery.start();

        return;
    }

    // sends a query requesting info on power after a specified time
    public static void CheckPower( InetAddress projIP, int pjLinkPort, String password, int row, int column, int waitTime )
    {

        String query = CreateQuery( CLASS_NUMBER, POWER, (String)sPowerCommands.get(PowerCommands.Status) );

        PJLinkQueryThread pjQuery = new PJLinkQueryThread( CommandType.Power, query, password, projIP, pjLinkPort, row, column, waitTime );
        pjQuery.start();

        return;
    }

    // sends a query to the projector requesting the information on the input status
    public static void CheckInput(  InetAddress projIP, int pjLinkPort, String password, int row, int column )
    {
        String query = CreateQuery( CLASS_NUMBER, INPUT, (String)sInputCommands.get(InputCommands.Status) );

        PJLinkQueryThread pjQuery = new PJLinkQueryThread(  CommandType.Input, query, password, projIP, pjLinkPort, row, column, 0 );
        pjQuery.start();

        return;
    }

    // sends a query requesting info on input after a specified time
    public static void CheckInput( InetAddress projIP, int pjLinkPort, String password, int row, int column, int waitTime )
    {
        String query = CreateQuery( CLASS_NUMBER, INPUT, (String)sInputCommands.get(InputCommands.Status) );

        PJLinkQueryThread pjQuery = new PJLinkQueryThread(  CommandType.Input, query, password, projIP, pjLinkPort, row, column, waitTime );
        pjQuery.start();

        return;
    }

    // sends a command to the projector to turn on, uses the row and column to update the table cell in the view
    public static void TurnOn( InetAddress projIP, int pjLinkPort, String password, int row, int column )
    {
        String query = CreateQuery( CLASS_NUMBER, POWER, (String)sPowerCommands.get(PowerCommands.On));
        PJLinkQueryThread pjQuery = new PJLinkQueryThread(CommandType.Input, query, password, projIP, pjLinkPort, row, column, 0 );
        pjQuery.start();

        // check the power status after WAIT_TIME
        CheckPower(  projIP, pjLinkPort, password, row, column, WAIT_TIME );
        return;
    }

    // sends a command to the projector to turn off, uses the row and column to update the table cell in the view
    public static void TurnOff( InetAddress projIP, int pjLinkPort, String password, int row, int column )
    {
        String query = CreateQuery( CLASS_NUMBER, POWER, (String)sPowerCommands.get(PowerCommands.Off));
        PJLinkQueryThread pjQuery = new PJLinkQueryThread(CommandType.Input, query, password, projIP, pjLinkPort, row, column, 0 );
        pjQuery.start();

        // check the power status after WAIT_TIME
        CheckPower(  projIP, pjLinkPort, password, row, column, WAIT_TIME );

        return;
    }

    public static String CreateQuery( int classNumber, String command, String parameter )
    {
        return HEADER_PREFIX + Integer.toString(classNumber) + command + " " + parameter + "\r";
    }

}
