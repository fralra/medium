import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import oracle.jdbc.*;


/* Compile:
 * $ORACLE_HOME/jdk/bin/javac -cp $ORACLE_HOME/jdbc/lib/ojdbc8.jar LapStats.java
 * 
 * Run 
 * $ORACLE_HOME/jdk/bin/java -cp $ORACLE_HOME/jdbc/lib/ojdbc8.jar:. LapStats
 * iterate time ms: 835
 * EU lap (seconds) 3.3328314E7, races: 348790
 * noEU lap (seconds) 1.8739626E7, races: 194422
 * Total races: 543212.
 * 
 * loadjava -u f1data/Formula1Dxxxxx1@localhost:1521/pdb1 -v -r -t LapStats.java
 */

public class LapStats{

    private static final String jdbcClassName="oracle.jdbc.driver.OracleDriver";
    private static final String jdbcURL="jdbc:oracle:thin:@localhost:1521/pdb1";
    private static final String user="f1data";
    private static final String password="Formula1Dxxxxx1";	

    public static void main(String[] args) {
    	LapStats test = new LapStats();
        try{
            Class.forName(jdbcClassName);
            Connection conn = test.initConnection();
            LapStats.lapStats(conn);
            test.stopConnection(conn);
        }catch(SQLException ex){
            ex.printStackTrace();
        }catch(ClassNotFoundException c){
            c.printStackTrace();
        }
    }	
	
    private Connection initConnection(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(jdbcURL, user, password);
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return connection;
    }

    private void stopConnection(Connection conn){
        try{
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    } 


    public static void lapStats () throws SQLException 
    {
        Connection conn = DriverManager.getConnection("jdbc:default:connection:");
        conn.setAutoCommit(true);
        lapStats(conn);
    }

    public static void lapStats (Connection conn) throws SQLException 
    {
      String sql = "select r.NAME as circuit_name, l.raceid, l.driverid, d.DRIVERREF, d.nationality, l.LAP, l.MILLISECONDS "
       + " from laptimes l " 
       + " join races r on l.raceid = r.raceid " 
       + " join drivers d on l.driverid = d.driverid";
  
      
      try
      {
        
        PreparedStatement pstmt = conn.prepareStatement(sql,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
        
        long t1 = System.currentTimeMillis();
        ResultSet rset = pstmt.executeQuery();
        rset.setFetchSize(10000);
        
        int v_counter = 0;
        int v_counter_EU = 0;
        int v_counter_noEU = 0;
        float v_lap_sum_EU = 0;
        float v_lap_sum_noEU = 0;

        
        while (rset.next())
        {
            
         String n = rset.getString(5);
         float l = rset.getFloat(7);

         
         switch(n) {
          case "British":
          case "German":
          case "Finnish": 
          case "French": 
          case "Spanish": 
          case "Italian": 
          case "Dutch":
          case "Polish":
              v_counter_EU = v_counter_EU+1;
              v_lap_sum_EU=v_lap_sum_EU + l/1000;
              break;
          default:
              v_counter_noEU = v_counter_noEU+1;
              v_lap_sum_noEU=v_lap_sum_noEU + l/1000; 
         
         }
         v_counter=v_counter+1;
    
        }
        rset.close();
  
        System.out.println( "EU lap (seconds) " + v_lap_sum_EU + ", races: "+ v_counter_EU );
        System.out.println( "noEU lap (seconds) " + v_lap_sum_noEU + ", races: "+ v_counter_noEU );
        System.out.println( "Total races: " + v_counter + ".");
  
        
        pstmt.close();
        System.out.println("iterate time ms: " + (System.currentTimeMillis()-t1));
        
      }
      catch (SQLException e)
      {
        System.err.println(e.getMessage());
      }
    }

}

