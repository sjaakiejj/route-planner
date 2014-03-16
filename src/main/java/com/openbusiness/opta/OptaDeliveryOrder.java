package com.openbusiness.opta;
/*
 * Maintain separation to maximise compatibility 
 * 
 */
 
// OptaPlanner 
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;


// OpenBusiness
import com.openbusiness.gen.DeliveryOrder;
import com.openbusiness.gen.Location;

@PlanningEntity(difficultyComparatorClass = VrpOrderDifficultyComparator.class)
public class OptaDeliveryOrder implements StandStill
{
  // The immutable order object
  private DeliveryOrder m_order;
  protected OptaDeliveryOrder m_next;
  protected StandStill m_prev;
  
  protected OptaDeliveryVehicle m_deliveryVehicle;
  protected double m_volSize;
  protected double m_weight;
  
  public OptaDeliveryOrder()
  {
    m_next = null;
    m_prev = null;
  }
  
  public OptaDeliveryOrder(DeliveryOrder order)
  {
    m_order = order;
    m_next = null;
    m_prev = null;
    
    // Extract data from DeliveryOrder object to model constraints
    m_volSize = order.getVolume();
    m_weight = order.getWeight();
  }
  
  
  public DeliveryOrder getWrappedDeliveryOrder()
  {
    return m_order;
  }
  
  // ******************************************************************
  // Graph methods
  // ******************************************************************
  
  
  @PlanningVariable(chained =true, 
  		valueRangeProviderRefs = {"vehicleRange", "orderRange"},
		variableListenerClasses = {VehicleUpdatingVariableListener.class})
  public StandStill getPreviousStandstill()
  {
    return m_prev;
  }
  public void setPreviousStandstill(StandStill prev)
  {
    m_prev = prev;
  }
  
  public void setNextDeliveryOrder(OptaDeliveryOrder next)
  {
    m_next = next;
  }
  
  public OptaDeliveryOrder getNextDeliveryOrder()
  {
    return m_next;
  }
  
  public void setDeliveryVehicle(OptaDeliveryVehicle vehicle)
  {
    m_deliveryVehicle = vehicle;
  }
  public OptaDeliveryVehicle getDeliveryVehicle()
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
    return m_order.getLocation();
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
