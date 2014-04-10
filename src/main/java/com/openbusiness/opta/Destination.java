package com.openbusiness.opta;
// OptaPlanner 
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import com.openbusiness.opta.solver.VrpOrderDifficultyComparator;
import com.openbusiness.opta.solver.VehicleUpdatingVariableListener;
import com.openbusiness.opta.dbs.solver.ArrivalTimeUpdatingVariableListener;

// OpenBusiness
//import com.openbusiness.gen.DeliveryOrder;
import com.openbusiness.gen.Location;

@PlanningEntity(difficultyComparatorClass = VrpOrderDifficultyComparator.class)
public class Destination implements StandStill
{
  // The immutable order object
  //private Destinati m_order;
  protected Destination m_next;
  protected StandStill m_prev;
  
  protected Vehicle m_deliveryVehicle;
  protected double m_volSize;
  protected double m_weight;
  protected Location m_location;
  
  public Destination()
  {
    m_next = null;
    m_prev = null;
  }
  
  public Destination(Location loc, double vol, double weight)
  {
   //m_order = order;
    m_next = null;
    m_prev = null;
    
    // Extract data from DeliveryOrder object to model constraints
    m_location = loc;
    m_volSize = vol; //order.getVolume();
    m_weight = weight; //order.getWeight();
  }
  
  /*
  public DeliveryOrder getWrappedDeliveryOrder()
  {
    return m_order;
  }*/
  
  // ******************************************************************
  // Graph methods
  // ******************************************************************
  
  
  @PlanningVariable(chained =true, 
  		valueRangeProviderRefs = {"vehicleRange", "orderRange"},
		variableListenerClasses = {VehicleUpdatingVariableListener.class,
 					   ArrivalTimeUpdatingVariableListener.class})
  public StandStill getPreviousStandstill()
  {
    return m_prev;
  }
  
  public void setPreviousStandstill(StandStill prev)
  {
    m_prev = prev;
  }
  
  public void setNextDestination(Destination next)
  {
    m_next = next;
  }
  
  public Destination getNextDestination()
  {
    return m_next;
  }
  
  public void setDeliveryVehicle(Vehicle vehicle)
  {
    m_deliveryVehicle = vehicle;
  }
  public Vehicle getDeliveryVehicle()
  {
    return m_deliveryVehicle;
  }
  
  public void setVolume(double vol)
  {
    m_volSize = vol;
  }
  
  public double getVolume()
  {
    return m_volSize;
  }
  
  public void setWeight(double weight)
  {
    m_weight = weight;
  }
  
  public double getWeight()
  {
    return m_weight;
  }
  
  
  public Location getLocation()
  {
    return m_location; //m_order.getLocation();
  }
  
  public double getDistanceToPrevOrder()
  {
    if(m_prev == null)
      return 0;
    return m_prev.getLocation().getDistance( getLocation() );
  }
  
  public double getDistanceTo(StandStill standstill)
  {
    return standstill.getLocation().getDistance( getLocation() );
  }
  
  @Override
  public String toString()
  {
    return "Order {Volume: " + getVolume() + ", Weight: " + getWeight() + ", Location: " + getLocation() + "}";
  }
}
