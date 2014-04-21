package com.openbusiness.gen;

/*
 * A factory for loading or generating delivery orders
 */
 
import com.openbusiness.app.SoftwareMode;
// Java
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Properties;

import com.openbusiness.opta.Destination;

public class DestinationFactory
{
  public static List<Destination> generate(int amount, Properties prop)
  {
    // Randomly generate within the restrictions
    Random rand = new Random();
    List<Destination> destinations = new ArrayList<Destination>();
    
    double volMin = Double.parseDouble(prop.getProperty("order_vol_lower_bound"));
    double volRange = Double.parseDouble(prop.getProperty("order_vol_range"));
    double weightMin = Double.parseDouble(prop.getProperty("order_weight_lower_bound"));
    double weightRange = Double.parseDouble(prop.getProperty("order_weight_range"));
    
    // Read the values one by one
    //while
    for(int i = 0; i < amount; i++) // Should use file 
    {
      double lat,lon,vol,weight;
      
      
      lat = Location.getMinLat() + (Location.getMaxLat() - Location.getMinLat())*rand.nextDouble();
      lon = Location.getMinLon() + (Location.getMaxLon() - Location.getMinLon())*rand.nextDouble();
      vol = volMin + volRange * rand.nextDouble();
      weight = weightMin + weightRange * rand.nextDouble();
      
      destinations.add(new Destination(new Location(lon,lat),
      					   vol,weight));
    }
    return destinations;
  }
}
