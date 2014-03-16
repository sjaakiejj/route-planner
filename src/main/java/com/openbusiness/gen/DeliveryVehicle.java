package com.openbusiness.gen;
/*
 * Object containing a single delivery vehicle,
 * with some helper methods
 */
 
public class DeliveryVehicle
{
  /*
   * Each vehicle has:  
   * - Fuel efficiency
   * - Volumetric size limit
   * - Weight limit
   * - Fuel capacity
   */
  
  // Fuel Efficiency in Kilometer per Litre of fuel 
  private double m_km_per_litre;
  
  // Volumetric Size Limit
  private double m_vol_capacity;
  
  // Weight Limit
  private double m_weight;
  
  // Fuel Capacity in Litres
  private double m_fuel_capacity;

  public DeliveryVehicle(double fuelEff, 
  			 double fuelCap, 
			 double volCap, 
			 double weight)
  {
    m_km_per_litre = fuelEff;
    m_fuel_capacity = fuelCap;
    
    m_vol_capacity = volCap;
    m_weight = weight;
  }
  
  /* Getters */
  public double getVolumeCapacity()
  {
    return m_vol_capacity;
  }
  
  public double getWeightCapacity()
  {
    return m_weight;
  }
  
  public double getFuelCapacity()
  {
    return m_fuel_capacity;
  }
  
  public double getFuelEfficiency()
  {
    return m_km_per_litre;
  }
}
