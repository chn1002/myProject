/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package avprojector.model;

import avprojector.model.AVProjector;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Long
 */
public class AVProjectorParser {

    public static ArrayList<AVProjector> Parse( String filename )
    {

        ArrayList<AVProjector> projList = new ArrayList<AVProjector>( );
        BufferedReader reader;

        try
        {

            reader = new BufferedReader( new FileReader( filename ) );
            String line = reader.readLine();
            
            while ( line != null )
            {
                String[] tokens = line.split("\t");
                
                projList.add( new AVProjector( tokens[0], tokens[1], tokens[2] ) );

                line = reader.readLine();
                
            }
            reader.close();
        }

        catch( FileNotFoundException e )
        {
            System.out.println( "File Not Found: " + e );
            System.exit( -1 );
        }

        catch (IOException e )
        {
            System.out.println( "IO Exception Occurred: " + e );
            System.exit( -1 );
        }

        catch( Exception e )
        {
            System.out.println( "Unknown Error: " + e );
            System.exit( -1 );
        }
        
        return projList;
    }

}
