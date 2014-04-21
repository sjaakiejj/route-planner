/*
 * The main class and entry point for the Routing Engine
 * Note that any external dependencies are established in separate classes.
 * This is to maximise future compatibility and maintainance. 
 */

package com.openbusiness.app;

/*
 * External Dependencies
 */ 
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;
import java.lang.Thread;

import com.openbusiness.opta.*;
import com.openbusiness.gen.*;
import com.openbusiness.gen.dbs.*;
import com.openbusiness.exceptions.*;

import com.openbusiness.opta.Planner;
 
public class RoutingEngine extends Thread
{
  private Properties m_properties;
  private int m_number_vehicles;
  private int m_number_destinations;
  private boolean m_running;
  private DeliverySolution m_schedule;
  private Planner m_planner;
  private boolean m_read_orders_from_file;
  private boolean m_starting;
  
  private String m_exit_error;
  
  public RoutingEngine()
  {
     m_schedule = null;
     m_running = false;
     m_planner = new Planner();
     m_read_orders_from_file = true;
     m_exit_error = "";
     m_starting = false;
  }
  
  public boolean getStarting()
  {
     return m_starting;
  }
  
  public void setStarting(boolean starting)
  {
     m_starting = true;
  }
  
  public String getErrorMessage()
  { 
     return m_exit_error;
  }
  
  public boolean propertiesSet()
  {
     return m_properties != null;
  }
  
  public boolean running()
  {
     return m_running;
  }
 /*
  * Entry Point 
  */
  public void defaultSetup(List<String> args) throws UsageException
  {
    // Properties properties = null;
  
     if(args.contains("-json"))
        loadPropertiesFromFile("json",args.get(args.indexOf("-json")+1));
     else
        loadPropertiesFromFile("prop", "/tmp/config.prop");
	
     
     initialiseVariables();
     
  }
  
  public void apiSetup()
  {
     initialiseVariables();
     //DeliverySolution solution = start();
     m_running = false;
  }
  
  public void run()
  {
    List<Vehicle> vehicles;
    List<Destination> destinations = new ArrayList<Destination>();
    /*
     * Setup graphical interface
     */
    
    // Generate according to the mode (Test is static from a file)
    
    try{
       // Read from file or generate randomly
       if( (m_properties.getProperty("problem").equals("dbsmorning") 
    	   || m_properties.getProperty("problem").equals("dbsafternoon"))
    	   && m_read_orders_from_file )
       {
	 destinations = DBSBranchFactory.generateFromFile("dbs_branches.csv", m_properties);
       }
       else
	 destinations = DestinationFactory.generate(m_number_destinations, m_properties);
       
       // Start the algorithm
       System.out.println("NoVehicles: " + m_number_vehicles);
       System.out.println("Properties: " + m_properties);
       vehicles = VehicleFactory.generate(m_number_vehicles, m_properties);
       
       System.out.println(vehicles.size() + " vehicles generated");	
       m_running = true;
       m_schedule = m_planner.plan(vehicles,destinations, m_properties);
       m_running = false;
    } 
    catch(Exception e){
       // TODO: Throw proper exceptions here
       m_exit_error = "Exception in RoutingEngine.run: " + e;
    }
  }
  
  public void terminateEarly()
  {
     m_planner.terminate();
  }
  
  public String getBestSolutionJSON()
  {
     DeliverySolution solution = m_planner.getBestSolution();
     
     if(solution == null)
       return null;
     
     return solutionToJSON(solution,m_properties);
  }
  
  public void loadPropertiesFromString(String jsonString) throws JSONException
  {
     try{
       m_properties = JSONPropertyReader.load(jsonString);
     }
     catch(Exception e)
     {
       throw new JSONException(jsonString, e);
     }
  }
  
  public void loadPropertiesFromFile(String type, String fileName)
  			throws PropertyFileException
  {
    // Initialisation of Streaming Objects
    InputStream input = null;
   // Properties properties = null;
   
    m_properties = new Properties();
    if(type.equals("json"))
    {
      try{
        input = new FileInputStream(fileName);
	m_properties = JSONPropertyReader.load(input);
      }
      catch(IOException ex){
        throw new PropertyFileException(fileName, ex);
      }
      catch(Exception ex){
      	throw new PropertyFileException(fileName, ex);
      }
    }
    else
    {
       // Attempt to read configuration file and extract properties
       try{
	 // Open the file and load it as a properties-formatted file
	 input = new FileInputStream("/tmp/config.prop");
	 m_properties.load(input);
       }
       catch (IOException ex){
	 ex.printStackTrace();
       }
       finally{
	 if (input != null){
	   try{
	      input.close();
	   }
	   catch(IOException e){
	      // TODO: should throw application exception
	      e.printStackTrace();
	   }
	 }
       }
    }
    
   // return properties;
  }
  
  public void initialiseVariables()
  {

    // Read properties from the input data
    m_number_vehicles = Integer.parseInt( m_properties.getProperty("vehicles"));
    
    if( m_properties.getProperty("orders") == null )
      m_number_destinations = 30; // default to 30
    else
      m_number_destinations = Integer.parseInt( m_properties.getProperty("orders"));

    // Setup the restrictions on delivery orders
    if( m_properties.getProperty("problem").equals("standard") )
    {
         Location.init(Double.parseDouble(m_properties.getProperty("min_lat")),
      		       Double.parseDouble(m_properties.getProperty("max_lat")),
		       Double.parseDouble(m_properties.getProperty("min_lon")),
		       Double.parseDouble(m_properties.getProperty("max_lon"))
		      );    
    }
  }
  
  public static String solutionToJSON(DeliverySolution solution, Properties props)
  {
  
      // And output the object into JSON
      JSONOutputWriter outputWriter = new JSONOutputWriter();

      // Write scores, fuel usage
      outputWriter.writeData("hard_score", ""+solution.getHardScore());
      outputWriter.writeData("soft_score", ""+solution.getSoftScore());
      outputWriter.writeData("fuel_used", ""+solution.getFuelUsed());
      outputWriter.writeData("distance_travelled", ""+solution.getDistanceTravelled());
      outputWriter.writeDeliverySchedules( solution.getDeliverySchedulesAsArray(),
      					   props );
      return outputWriter.getString();
  }
  
  public static void printSolution(DeliverySolution solution, Properties props)
  {
      System.out.println( solutionToJSON(solution,props) );
  }
  
}
