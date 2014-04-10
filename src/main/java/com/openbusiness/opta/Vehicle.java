package com.openbusiness.opta;
/*
 * Maintain separation to maximise compatibility 
 * 
 */
// OptaPlanner 
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

// OpenBusiness
//import com.openbusiness.gen.DeliveryVehicle;
import com.openbusiness.gen.Location;

public class Vehicle implements StandStill
{
  // The immutable vehicle object
  //private DeliveryVehicle m_vehicle;
  
  protected double m_volCapacity;
  protected double m_fuelCapacity;
  protected double m_fuelEfficiency;
  protected double m_weightCapacity;
  
  protected Depot m_depot;
  protected Destination m_nextDestination;
  
  public Vehicle()
  {
  }
  
  public Vehicle(double fuelEff, 
  			 double fuelCap, 
			 double volCap, 
			 double weight)
  {
    m_fuelEfficiency = fuelEff;
    m_fuelCapacity = fuelCap;
    m_volCapacity = volCap;
    m_weightCapacity = weight;
  }
  
  
  public void setDepot(Depot depot)
  {
    m_depot = depot;
  }
  
  public Location getLocation()
  {  
    return m_depot.getLocation();
  }
  
  public double getVolumeCapacity()
  {
    return m_volCapacity;
  }
  
  public double getWeightCapacity()
  {
    return m_weightCapacity;
  }
  
  public double getFuelEfficiency()
  {
    return m_fuelEfficiency;
  }
  
  public double getFuelCapacity()
  {
    return m_fuelCapacity;
  }
  
  public Vehicle getDeliveryVehicle()
  {
    return this;
  }
  
  public Destination getNextDestination()
  {
    return m_nextDestination;
  }
  
  public void setNextDestination(Destination dest)
  {
    m_nextDestination = dest;
  }
  
  public String toString()
  {
    String id = "[tmpId]";
    String vehicleString = "Vehicle " + id + ": {Capacity: [Weight:" + getWeightCapacity()
    							+ ", Volume: " + getVolumeCapacity() 
    							+ ", Fuel: " + getFuelCapacity() 
							+ "], Efficiency: " + getFuelEfficiency()
							+ "}\n";
    Destination tmpOrder = getNextDestination();
    if(tmpOrder == null)
      vehicleString += "[This vehicle has no deliveries to make]";
    else
    {
      double totalDistance = 0.0;
      while(tmpOrder != null)
      {
	 vehicleString += "   " + tmpOrder + "\n";
	 vehicleString += "       Distance prev order: " + tmpOrder.getDistanceToPrevOrder() + "\n";
	 totalDistance += tmpOrder.getDistanceToPrevOrder();
	 tmpOrder = tmpOrder.getNextDestination();
      }
      vehicleString += "-------------------------------------------------\n";
      vehicleString += "Total Distance Travelled: " + totalDistance;
      vehicleString += "\nTotal Fuel Used: " + (totalDistance/getFuelEfficiency());
    }
    
    return vehicleString;
  }
}
