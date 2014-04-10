package com.openbusiness.gen;

import java.util.List;
import java.util.ArrayList;

import com.openbusiness.opta.Destination;
import com.openbusiness.opta.Vehicle;

// This class can alternatively store the data in
// a map structure, with each vehicle being associated with a list of orders
// internally. The sample's description was unclear as to which of these two
// approaches is preferred. 
public class DeliverySchedule
{
  private List<Destination> m_orderList;
  private Vehicle m_assignedVehicle;
  
  private double m_total_volume;
  private double m_total_weight;
  private double m_total_distance;
  private double m_fuel_used;
  
  public DeliverySchedule(Vehicle vehicle)
  {
    m_assignedVehicle = vehicle;
    m_orderList = new ArrayList<Destination>();
  }
  
  public void close()
  {
    _calcVariables();
  }
  
  public void addOrder(Destination order)
  {
    m_orderList.add(order);
  }
  
  public Vehicle getDeliveryVehicle()
  {
    return m_assignedVehicle;
  }
  
  public List<Destination> getOrderList()
  {
    return m_orderList;
  }
  
  public double getVolumeUsed()
  {
    return m_total_volume;
  }
  
  public double getWeightUsed()
  {
    return m_total_weight;
  }
  
  public double getDistTravelled()
  {
    return m_total_distance;
  }
  
  public double getFuelUsed()
  {
    return m_fuel_used;
  }
  
  private void _calcVariables()
  {
  
    m_total_distance = 0;
    m_total_weight = 0;
    m_total_volume = 0;
    
    for(int i = 0; i < m_orderList.size(); i++)
    {
      double dist = 0;
      if(i == 0)
      {    
        double dlon = Location.getMinLon() + 
                        (Location.getMaxLon() - Location.getMinLon()) / 2; 
        double dlat = Location.getMinLat() + 
                        (Location.getMaxLat() - Location.getMinLat()) / 2; 
        dist = m_orderList.get(i).getLocation().getDistance( new Location( dlon,dlat ) ); 
	// TODO: Should be from correct depot position
      }
      
      else
      {
        dist = m_orderList.get(i-1).getLocation().getDistance( m_orderList.get(i).getLocation());
      }
      
      m_total_weight   += m_orderList.get(i).getWeight();
      m_total_volume   += m_orderList.get(i).getVolume();
      m_total_distance += dist;
    }
    
    m_fuel_used = m_total_distance / m_assignedVehicle.getFuelEfficiency();
  }
}
