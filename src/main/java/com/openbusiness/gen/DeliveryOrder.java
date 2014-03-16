package com.openbusiness.gen;
/*
 * Object containing a single delivery order,
 * with some helper methods
 */
 
public class DeliveryOrder
{
  /* Each order contains: 
   * - Destination location (lat,lon)
   * - Volumetric size
   * - Weight of package.
   */
   
  // Destination 
  private double m_lat;
  private double m_lon;
  
  // Volumetric Size
  private double m_vol;
  
  // Weight
  private double m_weight;
  
  private Location m_loc;
  
  public DeliveryOrder(Location loc, double vol, double weight)
  {
    m_loc    = loc;
    m_vol    = vol;
    m_weight = weight;
  }
  
  /* Getters */
  public double getVolume()
  {
    return m_vol;
  }
    
  public double getWeight()
  {
    return m_weight;
  }
  
  public Location getLocation()
  {
    return m_loc;
  }
    
}
