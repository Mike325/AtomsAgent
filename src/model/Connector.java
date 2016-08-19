package model;

import java.sql.*;


import persistance.DebugMode;

/*
 * 
 *
 *
 */
public class Connector 
{
    Connection connection;

    static final int TEST = 1;
    static final String PRODSCHEME ="atomsdb";
    static final String TESTSCHEME ="atomstest";

    public static String schema = TESTSCHEME;
    
    public static void setSchema(int value)
    {
    	schema = (value == 0) ? PRODSCHEME: TESTSCHEME;
    	
    	DebugMode.printDebugMessage("Apuntando al schema " + schema + ((value == 0)? " en Produccion" : " en Test" ));
    	
    }

    /*
     *
     */
    public Connection CreateConnection()
    {
        try
        {
            Class.forName("com.ibm.db2.jcc.DB2Driver");

            String url      = "jdbc:db2://169.54.229.15:50000/S0447899:clientRerouteAlternateServerName=169.55.248.232;clientRerouteAlternatePortNumber=50000;";
            String user     = "ininjfpv";
            String password = "nh1xy6imjjoe";
            connection      = DriverManager.getConnection(url, user, password); 
        }
        catch(ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
            connection = null;
        }

        return connection;
    }

    /*
     * 
     *
     *
     */
    public boolean CloseConnection( Connection connection )
    {
        boolean status = false;

        try
        {
            connection.close();
            status = true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return status;
    }

}
