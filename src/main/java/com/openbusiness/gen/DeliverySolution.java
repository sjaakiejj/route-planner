package com.openbusiness.gen;

import java.util.List;

public class DeliverySolution
{
   private int m_hard_score;
   private int m_soft_score;
   private double m_fuel_used;
   private double m_distance_travelled;
   private List<DeliverySchedule> m_schedules;

   public DeliverySolution(int hardScore, int softScore,
			   List<DeliverySchedule> schedules)
   {
      m_hard_score = hardScore;
      m_soft_score = softScore;
      
      m_schedules = schedules;
      
      // Depot Location
      double lon = Location.getMinLon() + 
                        (Location.getMaxLon() - Location.getMinLon()) / 2; 
      double lat = Location.getMinLat() + 
                        (Location.getMaxLat() - Location.getMinLat()) / 2; 

      
      Location depot = new Location(lon,lat);
     
      for(DeliverySchedule schedule : schedules)
      {
	double totalDistance = 0.0;
	
	List<DeliveryOrder> orderList = schedule.getOrderList();
	
	for(int i = 0; i < orderList.size(); i++)
	{
	   if( i == 0 )
	   {
	     // Distance to depot
	     totalDistance += orderList.get(i).getLocation().getDistance(depot);
	   }
	   else
	   {
	     totalDistance += orderList.get(i).getLocation().getDistance(
	     			orderList.get(i-1).getLocation());
	   }
	   
	}
	
	m_fuel_used += totalDistance / schedule.getDeliveryVehicle().getFuelEfficiency();
	m_distance_travelled += totalDistance;
      }
      
   }
   
   public int getHardScore()
   {
     return m_hard_score;
   }
   
   public int getSoftScore()
   {
     return m_soft_score;
   }
   
   public double getFuelUsed()
   {
     return m_fuel_used;
   }
   
   public double getDistanceTravelled()
   { 
     return m_distance_travelled;
   }
   
   public List<DeliverySchedule> getDeliverySchedules()
   {
     return m_schedules;
   }
   public DeliverySchedule [] getDeliverySchedulesAsArray()
   {
     return m_schedules.toArray(new DeliverySchedule[m_schedules.size()]);
   }
   
}
