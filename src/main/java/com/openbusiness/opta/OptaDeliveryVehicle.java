package com.openbusiness.opta;
/*
 * Maintain separation to maximise compatibility 
 * 
 */
// OptaPlanner 
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

// OpenBusiness
import com.openbusiness.gen.DeliveryVehicle;
import com.openbusiness.gen.Location;

public class OptaDeliveryVehicle implements StandStill
{
  // The immutable vehicle object
  private DeliveryVehicle m_vehicle;
  
  protected double m_volCapacity;
  protected double m_fuelCapacity;
  protected double m_fuelEfficiency;
  protected double m_weightCapacity;
  
  protected Depot m_depot;
  protected OptaDeliveryOrder m_nextOrder;
  
  public OptaDeliveryVehicle()
  {
  }
  
  public OptaDeliveryVehicle(DeliveryVehicle vehicle)
  {
    m_vehicle = vehicle;
    
    // Extract required data from the vehicle object
    // to model the constraints
    m_volCapacity = vehicle.getVolumeCapacity();
    m_fuelCapacity = vehicle.getFuelCapacity();
    m_fuelEfficiency = vehicle.getFuelEfficiency();
    m_weightCapacity = vehicle.getWeightCapacity();
  }
  
  public DeliveryVehicle getWrappedDeliveryVehicle()
  {
    return m_vehicle;
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
  
  public OptaDeliveryVehicle getDeliveryVehicle()
  {
    return this;
  }
  
  public OptaDeliveryOrder getNextDeliveryOrder()
  {
    return m_nextOrder;
  }
  
  public void setNextDeliveryOrder(OptaDeliveryOrder order)
  {
    m_nextOrder = order;
  }
  
  public String toString()
  {
    String id = "[tmpId]";
    String vehicleString = "Vehicle " + id + ": {Capacity: [Weight:" + getWeightCapacity()
    							+ ", Volume: " + getVolumeCapacity() 
    							+ ", Fuel: " + getFuelCapacity() 
							+ "], Efficiency: " + getFuelEfficiency()
							+ "}\n";
    OptaDeliveryOrder tmpOrder = getNextDeliveryOrder();
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
	 tmpOrder = tmpOrder.getNextDeliveryOrder();
      }
      vehicleString += "-------------------------------------------------\n";
      vehicleString += "Total Distance Travelled: " + totalDistance;
      vehicleString += "\nTotal Fuel Used: " + (totalDistance/getFuelEfficiency());
    }
    
    return vehicleString;
  }
}
