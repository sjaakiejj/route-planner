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

import com.openbusiness.gen.*;
import com.openbusiness.exceptions.InvalidModeException;

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
    DeliveryOrder [] orders;
    DeliveryVehicle [] vehicles;
    
    // Initialisation of Primitives
    int numberOfOrders = 10;
    int numberOfVehicles = 4;
    
    
    /*
     * Configuration 
     */
    
    // Attempt to read configuration file and extract properties
    try{
      // Open the file and load it as a properties-formatted file
      input = new FileInputStream("config.prop");
      properties.load(input);
	
      // Read properties from the input data
      numberOfVehicles = Integer.parseInt( properties.getProperty("vehicles"));
      numberOfOrders = Integer.parseInt( properties.getProperty("orders"));
      
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
    }
    catch (IOException ex){
      ex.printStackTrace();
    }
    catch (InvalidModeException ex)
    {
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
    
    /*
     * Setup graphical interface
     */
    
    
    // Generate according to the mode (Test is static from a file)
    orders = DeliveryOrderFactory.generate(mode,numberOfOrders);
    vehicles = DeliveryVehicleFactory.generate(mode,numberOfVehicles);
    
    // Now pass control to OptaPlanner
    List<DeliverySchedule> deliverySchedules = Planner.plan(vehicles,orders);
  }
  
}
