package com.openbusiness.opta;

// OptaPlanner
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty
;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.value.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.core.impl.solution.Solution;

// OpenBusiness
import com.openbusiness.gen.DeliveryOrder;
import com.openbusiness.gen.Location;

// Java
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

@PlanningSolution
public class RoutingPlannerSolution implements Solution<HardSoftScore>
{
  private List<OptaDeliveryVehicle> m_vehicleList;
  private List<OptaDeliveryOrder>   m_orderList;
  private List<Location> 	    m_locationList;
  private List<Depot>		    m_depotList;
  
  private HardSoftScore m_score;
  
  public RoutingPlannerSolution()
  {
    m_depotList = new ArrayList<Depot>();
  }
  
  @PlanningEntityCollectionProperty
  @ValueRangeProvider(id = "vehicleRange")
  public List<OptaDeliveryVehicle> getVehicleList(){
    return m_vehicleList;
  }
  
  public void setVehicleList(List<OptaDeliveryVehicle> vehicleList)
  {
    m_vehicleList = vehicleList;
  }
  
  @PlanningEntityCollectionProperty
  @ValueRangeProvider(id = "orderRange")
  public List<OptaDeliveryOrder> getOrderList()
  {
    return m_orderList;
  }
  
  public void setOrderList(List<OptaDeliveryOrder> orderList)
  {
    m_orderList = orderList;
  }
  
  public void setLocationList(List<Location> locationList)
  {
    m_locationList = locationList;
  }
  
  public void generateDepot()
  {
    // only generate a depot if we have none
    if(m_depotList.size() != 0)
      return;
      
    double lon = Location.getMinLon() + 
    			(Location.getMaxLon() - Location.getMinLon()) / 2; 
    double lat = Location.getMinLat() + 
    			(Location.getMaxLat() - Location.getMinLat()) / 2; 
			
    Depot dpt = new Depot();
    dpt.setLocation(new Location(lon,lat));
    
    m_depotList.add(dpt);
    
    for(OptaDeliveryVehicle v : m_vehicleList)
      v.setDepot(dpt);
  }
  
  public void setScore(HardSoftScore score)
  {
    m_score = score;
    System.out.print("Current Score: " + score + "\r"); // TODO: REmove this line
  }
  
  public HardSoftScore getScore()
  {
    return m_score;
  }
  
  // **************************************************************************
  // Complex Methods 
  // **************************************************************************
  
  public Collection<? extends Object> getProblemFacts() {
    List<Object> facts = new ArrayList<Object>();
    facts.addAll(m_locationList);
    facts.addAll(m_depotList);
    // Do not add the planning entities because that will be done automatically
    return facts;
  }
  

}
