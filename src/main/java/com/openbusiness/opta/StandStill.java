package com.openbusiness.opta;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import com.openbusiness.gen.Location;

@PlanningEntity
public interface StandStill {
  
  Location getLocation();
  Vehicle getDeliveryVehicle();
  
  @PlanningVariable(mappedBy = "previousStandstill")
  Destination getNextDestination();
  void setNextDestination(Destination order);
}
