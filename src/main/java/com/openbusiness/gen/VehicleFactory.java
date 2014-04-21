package com.openbusiness.gen;
/*
 * A factory for loading or generating delivery vehicles
 */
 
import com.openbusiness.app.SoftwareMode;
 
import java.util.Random;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;
 
import com.openbusiness.opta.Vehicle; 
 
public class VehicleFactory
{
  public static List<Vehicle> generate(int amount, Properties prop)
  {
    List<Vehicle> vehicles = new ArrayList<Vehicle>();
    
    Random rand = new Random();
    
    double fuelEfRange;
    double fuelEfMin;
    
    if( prop.getProperty("problem").equals("standard") )
    { 
      fuelEfRange = Double.parseDouble(prop.getProperty("fuel_efficiency_range"));   
      fuelEfMin   = Double.parseDouble(prop.getProperty("fuel_efficiency_lower_bound"));
    }
    else
    {
      fuelEfRange = 2;
      fuelEfMin   = 0;
    }
    
    double fuelCapRange = Double.parseDouble(prop.getProperty("fuel_capacity_range")); 
    double fuelCapMin = Double.parseDouble(prop.getProperty("fuel_capacity_lower_bound"));
    
    double weightRange = Double.parseDouble(prop.getProperty("weight_capacity_range"));  
    double weightMin = Double.parseDouble(prop.getProperty("weight_capacity_lower_bound"));
    
    double volRange = Double.parseDouble(prop.getProperty("volume_capacity_range"));     
    double volMin = Double.parseDouble(prop.getProperty("volume_capacity_lower_bound"));
    
    for(int i = 0; i < amount; i++)
    {
      vehicles.add( 
          new Vehicle(fuelEfMin + fuelEfRange * rand.nextDouble(), // Fuel efficiency
      			      fuelCapMin + fuelCapRange * rand.nextDouble(), // Fuel capacity
			      volMin + volRange * rand.nextDouble(), // Volume Capacity
			      weightMin + weightRange * rand.nextDouble() // Weight Capacity
			      ));
    }
    
    return vehicles;
  } 
}
