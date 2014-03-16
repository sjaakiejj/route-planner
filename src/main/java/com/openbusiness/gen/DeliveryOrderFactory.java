package com.openbusiness.gen;

/*
 * A factory for loading or generating delivery orders
 */
 
import com.openbusiness.app.SoftwareMode;
// Java
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class DeliveryOrderFactory
{
  public static DeliveryOrder[] generate(int mode, int amount)
  {
    if(mode == SoftwareMode.TEST)
      return generateTestSet();
    else if(mode == SoftwareMode.DEMO)
      return generateRandomized(amount);
    else
      return null;
  }
  
  private static DeliveryOrder[] generateRandomized(int amount)
  {
    // Randomly generate within the restrictions
    Random rand = new Random();
    List<DeliveryOrder> deliveryOrders = new ArrayList<DeliveryOrder>();
    
    double volMin = 5;
    double volRange = 25;
    double weightMin = 5;
    double weightRange = 25;
    
    // Read the values one by one
    //while
    for(int i = 0; i < amount; i++) // Should use file 
    {
      double lat,lon,vol,weight;
      
      
      lat = Location.getMinLat() + (Location.getMaxLat() - Location.getMinLat())*rand.nextDouble();
      lon = Location.getMinLon() + (Location.getMaxLon() - Location.getMinLon())*rand.nextDouble();
      vol = volMin + volRange * rand.nextDouble();
      weight = weightMin + weightRange * rand.nextDouble();
      
      deliveryOrders.add(new DeliveryOrder(new Location(lon,lat),
      					   vol,weight));
    }
    
    return deliveryOrders.toArray(new DeliveryOrder[deliveryOrders.size()]);
  }
  
  private static DeliveryOrder[] generateTestSet()
  {
   // String testFile = SoftwareSettings.get("testFile");
    
    // Open the file
    
    //
    List<DeliveryOrder> deliveryOrders = new ArrayList<DeliveryOrder>();
    
    // Read the values one by one
    //while
    for(int i = 0; i < 10; i++) // Should use file 
    {
      double lat,lon,vol,weight;
      
      lat = lon = vol = weight = i; 
      lat = Location.getMinLat() + (double)i / 10.0;
      lon = Location.getMinLon() - (double)i / 10.0;
      deliveryOrders.add(new DeliveryOrder(new Location(lon,lat),
      					   vol,weight));
    }
    
    return deliveryOrders.toArray(new DeliveryOrder[deliveryOrders.size()]);
  }
}
