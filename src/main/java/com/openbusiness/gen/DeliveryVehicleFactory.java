package com.openbusiness.gen;
/*
 * A factory for loading or generating delivery vehicles
 */
 
import com.openbusiness.app.SoftwareMode;
 
import java.util.Random;
import java.util.Properties;
 
public class DeliveryVehicleFactory
{
  public static DeliveryVehicle[] generate(int mode, int amount, Properties prop)
  {
    if(mode == SoftwareMode.TEST)
      return generateTestSet();
    else if(mode == SoftwareMode.DEMO)
      return generateRandomized(amount,prop);
    // TODO: Implement Simulation Mode
    else
      return null;
  }
  
  private static DeliveryVehicle[] generateRandomized(int amount, Properties prop)
  {
    DeliveryVehicle [] vehicles = new DeliveryVehicle[amount];
    
    Random rand = new Random();
    
    // TODO: These should be taken from the config file instead
    double fuelEfRange = Double.parseDouble(prop.getProperty("fuel_efficiency_range"));   
    double fuelEfMin = Double.parseDouble(prop.getProperty("fuel_efficiency_lower_bound"));
    
    double fuelCapRange = Double.parseDouble(prop.getProperty("fuel_capacity_range")); 
    double fuelCapMin = Double.parseDouble(prop.getProperty("fuel_capacity_lower_bound"));
    
    double weightRange = Double.parseDouble(prop.getProperty("weight_capacity_range"));  
    double weightMin = Double.parseDouble(prop.getProperty("weight_capacity_lower_bound"));
    
    double volRange = Double.parseDouble(prop.getProperty("volume_capacity_range"));     
    double volMin = Double.parseDouble(prop.getProperty("volume_capacity_lower_bound"));
    
    for(int i = 0; i < amount; i++)
    {
      vehicles[i] = 
          new DeliveryVehicle(fuelEfMin + fuelEfRange * rand.nextDouble(), // Fuel efficiency
      			      fuelCapMin + fuelCapRange * rand.nextDouble(), // Fuel capacity
			      volMin + volRange * rand.nextDouble(), // Volume Capacity
			      weightMin + weightRange * rand.nextDouble() // Weight Capacity
			      );
    }
    
    return vehicles;
  }
  
  private static DeliveryVehicle[] generateTestSet()
  {
    // TODO: Generate from file
    DeliveryVehicle [] vehicles = new DeliveryVehicle[4];
    
    vehicles[0] = new DeliveryVehicle(17, 50, 7, 300);
    vehicles[1] = new DeliveryVehicle(12, 50, 10, 30);
    vehicles[2] = new DeliveryVehicle(5, 50, 20, 300);
    vehicles[3] = new DeliveryVehicle(3, 200, 25, 300);
    return vehicles;
  }
}
