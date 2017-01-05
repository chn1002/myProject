/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package avprojector.controller;


import avprojector.controller.AVProjectorController;
import avprojector.model.AVProjector;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Long
 */
public class AVProjectorTableModel extends AbstractTableModel {

    public static String[] columnNames                            = { "Projectors", "Power", "Input" };
    public static final String[] powerColumnValues          = { "Off", "On" };
    public static final String[] inputColumnValues          = { "Input A", "Video" };
    private ArrayList<AVProjector> projList;
    private Object[][] mData;
    private boolean DEBUG = false;

    public AVProjectorTableModel( )
    {
        AVProjectorController.Parse();
        mData = AVProjectorController.GetModelData();
    }

    public AVProjectorTableModel( Object[][] data )
    {
        mData = data;
    }

    public int getColumnCount()
    {
        return columnNames.length;
    }

    public int getRowCount()
    {
        return mData.length;
    }

    @Override
    public String getColumnName( int col )
    {
        return columnNames[col];
    }

    public Object getValueAt( int row, int col )
    {
        return mData[row][col];
    }

    @Override
    public Class getColumnClass( int c )
    {
        return getValueAt(0, c).getClass();
    }

    @Override
    public boolean isCellEditable( int row, int col )
    {
        /*
        // cannot change the name of the projector
        if( col == 1 )
        {
            return false;
        }
        */
        if( col == 2 && getValueAt( row, col ).toString().compareToIgnoreCase("Projector not on") != 0)
        {
            return true;
        }
        /*
        // can change the values of the power and input columns
        else
        {
            return true;
        }
         * */
        return false;
    }

    @Override
    public void setValueAt( Object value, int row, int col )
    {
        if (DEBUG)
        {
            System.out.println("Setting value at " + row + "," + col
                    + " to " + value
                    + " (an instance of "
                    + value.getClass() + ")");

        }

        mData[row][col] = value;
        fireTableCellUpdated( row, col );

        if (DEBUG)
        {
            System.out.println("New value of data:");
            printDebugData();
        }

    }

    private void printDebugData()
    {
        int numRows = getRowCount();
        int numCols = getColumnCount();

        for (int i = 0; i < numRows; i++ )
        {
            System.out.print("    row " + i + ":");
            for (int j = 0; j < numCols; j++ )
            {
                System.out.print( "  " + mData[i][j]);
            }
            System.out.println();
        }
        System.out.println("-------------------------------------");
    }
}
