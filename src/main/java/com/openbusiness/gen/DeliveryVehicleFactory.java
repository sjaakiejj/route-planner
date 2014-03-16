package com.openbusiness.gen;
/*
 * A factory for loading or generating delivery vehicles
 */
 
import com.openbusiness.app.SoftwareMode;
 
import java.util.Random;
 
public class DeliveryVehicleFactory
{
  public static DeliveryVehicle[] generate(int mode, int amount)
  {
    if(mode == SoftwareMode.TEST)
      return generateTestSet();
    else if(mode == SoftwareMode.DEMO)
      return generateRandomized(amount);
    // TODO: Implement Simulation Mode
    else
      return null;
  }
  
  private static DeliveryVehicle[] generateRandomized(int amount)
  {
    DeliveryVehicle [] vehicles = new DeliveryVehicle[amount];
    
    Random rand = new Random();
    
    // TODO: These should be taken from the config file instead
    double fuelEfRange = 17;   double fuelEfMin = 5;
    double fuelCapRange = 200; double fuelCapMin = 100;
    double weightRange = 3000;  double weightMin = 100;
    double volRange = 3000;     double volMin = 150;
    
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
