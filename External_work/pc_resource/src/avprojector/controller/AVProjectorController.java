/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package avprojector.controller;

import avprojector.model.AVProjectorParser;
import avprojector.model.AVProjector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import pjLink.PJLinkC1;

/**
 *
 * @author Long
 */
public class AVProjectorController {

    public static boolean                   sDEBUG = true;
    private static long                     sCheckStatusTime = 300000;  // 5 minutes between projector pings
    private static Semaphore                sProjTableModelMutex;
    private static Timer                    sCheckAllTimer;
    private static boolean                  sInitialized = false;
    public static ArrayList<AVProjector>    sProjList;
    public static String                    sFilepath = "./projList.txt";
    public static int                       sColumnCount = AVProjectorTableModel.columnNames.length;
    public static AVProjectorTableModel     sProjTableModel;


    public static void Initialize()
    {
        if( !sInitialized )
        {
            sInitialized = true;
            PJLinkC1.IntializeCommands();
            Parse();
            sProjTableModelMutex = new Semaphore(1, true);
            sProjTableModel = new AVProjectorTableModel( GetModelData() );
            sCheckAllTimer = new Timer();
            sCheckAllTimer.scheduleAtFixedRate(new CheckProjectorStatusTask(), sCheckStatusTime, sCheckStatusTime);

            CheckAllStatus();
        }
    }

    public static void SetProjModelValueAt( String data, int row, int column )
    {
        try {
            sProjTableModelMutex.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(AVProjectorController.class.getName()).log(Level.SEVERE, null, ex);
            sProjTableModelMutex.release();
        }
        sProjTableModel.setValueAt(data, row, column);
        sProjTableModelMutex.release();
        return;
    }

    // parses a tab delimited file with ipAddress, hostname, projector name
    public static ArrayList<AVProjector> Parse( )
    {
        sProjList = AVProjectorParser.Parse( sFilepath );
        return sProjList;
    }

    public static Object[][] GetModelData()
    {
        Object[][] data = new Object[sProjList.size()][sColumnCount];
        for( int i = 0; i < sProjList.size(); i++ )
        {
            data[i][0] = sProjList.get(i).GetProjName();
            data[i][1] = "Unknown";
            data[i][2] = "Unknown";
        }

        return data;
    }

    public static void CheckAllStatus( )
    {

        for( int i = 0; i < sProjList.size(); i++ )
        {
            SetProjModelValueAt("Loading", i, 1);
            SetProjModelValueAt("Loading", i, 2);
            sProjList.get(i).CheckPower(i, 1);
            sProjList.get(i).CheckInput(i, 2);

        }

    }

    public static void TurnOnOff( int row, int column )
    {
        sProjList.get(row).TurnOnOff(row, column);
        return;
    }

    public static void GoToWebServer( int row, int column ) throws IOException
    {
        sProjList.get(row).GoToWebServer(row, column);
        return;
    }

}

final class CheckProjectorStatusTask extends TimerTask
{

    public CheckProjectorStatusTask()
    {

    }

    @Override
    public void run()
    {
        AVProjectorController.CheckAllStatus();
    }

}
