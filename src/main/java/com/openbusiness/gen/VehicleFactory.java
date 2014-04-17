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
  public static List<Vehicle> generate(int mode, int amount, Properties prop)
  {
    if(mode == SoftwareMode.TEST)
      return generateTestSet();
    else if(mode == SoftwareMode.DEMO)
      return generateRandomized(amount,prop);
    // TODO: Implement Simulation Mode
    else
      return null;
  }
  
  private static List<Vehicle> generateRandomized(int amount, Properties prop)
  {
    List<Vehicle> vehicles = new ArrayList<Vehicle>();
//    DeliveryVehicle [] vehicles = new DeliveryVehicle[amount];
    
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
  
  private static List<Vehicle> generateTestSet()
  {
    // TODO: Generate from file
    return null; // TODO: Implement
    /*
    DeliveryVehicle [] vehicles = new DeliveryVehicle[4];
    
    vehicles[0] = new DeliveryVehicle(17, 50, 7, 300);
    vehicles[1] = new DeliveryVehicle(12, 50, 10, 30);
    vehicles[2] = new DeliveryVehicle(5, 50, 20, 300);
    vehicles[3] = new DeliveryVehicle(3, 200, 25, 300);
    return vehicles; */
  }
}
