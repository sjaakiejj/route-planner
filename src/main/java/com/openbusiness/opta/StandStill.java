package com.openbusiness.opta;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import com.openbusiness.gen.Location;

@PlanningEntity
public interface StandStill {
  
  Location getLocation();
  OptaDeliveryVehicle getDeliveryVehicle();
  
  @PlanningVariable(mappedBy = "previousStandstill")
  OptaDeliveryOrder getNextDeliveryOrder();
  void setNextDeliveryOrder(OptaDeliveryOrder order);
}
