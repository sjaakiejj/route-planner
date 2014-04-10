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

import com.openbusiness.opta.*;
import com.openbusiness.gen.*;
import com.openbusiness.exceptions.InvalidModeException;
import com.openbusiness.exceptions.UnrecognizedProblemException;

import com.openbusiness.opta.Planner;
 
public class RoutingEngine
{

 /*
  * Entry Point 
  */
  public static void main(String [] args)
  {
    /* 
     * Variable Declaration and Initialisation
     */

    // App Settings (Test or Demo)
    int mode = SoftwareMode.TEST;
    Properties properties = new Properties();
    
    // Initialisation of Streaming Objects
    InputStream input = null;
    
    // Declaration of Objects
  //  DeliveryOrder [] orders;
  //  DeliveryVehicle [] vehicles;
    List<Vehicle> vehicles;
    List<Destination> destinations;
    
    // Initialisation of Primitives
    int numberOfDestinations = 10;
    int numberOfVehicles = 4;
    
    
    /*
     * Configuration 
     */
    // TODO: Construct a map of input args
    // JSON
    if(args.length > 1 && args[0].equals("-json"))
    {
      try{
        input = new FileInputStream(args[1]);
	properties = JSONPropertyReader.load(input);
      }
      catch(IOException ex){
        
      }
      catch(InvalidModeException ex){
      }
      catch(Exception ex){
        System.out.println(ex);
      }
    }
    else
    {
       // Attempt to read configuration file and extract properties
       try{
	 // Open the file and load it as a properties-formatted file
	 input = new FileInputStream("/tmp/config.prop");
	 properties.load(input);
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
	      e.printStackTrace();
	   }
	 }
       }
    }
    
    
    // Use the properties
    try{
	 // Read properties from the input data
	 numberOfVehicles = Integer.parseInt( properties.getProperty("vehicles"));
	 numberOfDestinations = Integer.parseInt( properties.getProperty("orders"));

	 // Setup the restrictions on delivery orders
	 Location.init(Double.parseDouble(properties.getProperty("min_lat")),
      			    Double.parseDouble(properties.getProperty("max_lat")),
			    Double.parseDouble(properties.getProperty("min_lon")),
			    Double.parseDouble(properties.getProperty("max_lon"))
			   );

	 // Are we in test or demo mode?
	 if( properties.getProperty("mode").toUpperCase().equals("TEST") )
            mode = SoftwareMode.TEST;
	 else if( properties.getProperty("mode").toUpperCase().equals("DEMO") )
            mode = SoftwareMode.DEMO;
	 else
            throw new InvalidModeException(properties.getProperty("mode"));
    } catch(InvalidModeException ex) {
    }    
    
    /*
     * Setup graphical interface
     */
    
    // Generate according to the mode (Test is static from a file)
    destinations = DestinationFactory.generate(mode,numberOfDestinations,properties);
    vehicles = VehicleFactory.generate(mode,numberOfVehicles,properties);
    
    try{
      // Now pass control to OptaPlanner
      DeliverySolution solution = Planner.plan(vehicles,destinations,properties);

      // And output the object into JSON
      JSONOutputWriter outputWriter = new JSONOutputWriter();

      // Write scores, fuel usage
      outputWriter.writeData("hard_score", ""+solution.getHardScore());
      outputWriter.writeData("soft_score", ""+solution.getSoftScore());
      outputWriter.writeData("fuel_used", ""+solution.getFuelUsed());
      outputWriter.writeData("distance_travelled", ""+solution.getDistanceTravelled());
      outputWriter.writeDeliverySchedules( solution.getDeliverySchedulesAsArray(),
      					   properties );

      outputWriter.print(System.out);
    }
    catch(UnrecognizedProblemException e)
    {
      System.err.println(e);
    }
  }
  
}
