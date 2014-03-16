package com.openbusiness.opta;

import org.optaplanner.core.impl.domain.variable.listener.PlanningVariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class VehicleUpdatingVariableListener implements PlanningVariableListener<OptaDeliveryOrder> {

    public void beforeEntityAdded(ScoreDirector scoreDirector, OptaDeliveryOrder customer) {
        // Do nothing
    }

    public void afterEntityAdded(ScoreDirector scoreDirector, OptaDeliveryOrder customer) {
        updateVehicle(scoreDirector, customer);
    }

    public void beforeVariableChanged(ScoreDirector scoreDirector, OptaDeliveryOrder customer) {
        // Do nothing
    }

    public void afterVariableChanged(ScoreDirector scoreDirector, OptaDeliveryOrder customer) {
        updateVehicle(scoreDirector, customer);
    }

    public void beforeEntityRemoved(ScoreDirector scoreDirector, OptaDeliveryOrder customer) {
        // Do nothing
    }

    public void afterEntityRemoved(ScoreDirector scoreDirector, OptaDeliveryOrder customer) {
        // Do nothing
    }

    protected void updateVehicle(ScoreDirector scoreDirector, OptaDeliveryOrder sourceCustomer) {
        StandStill previousStandstill = sourceCustomer.getPreviousStandstill();
        OptaDeliveryVehicle vehicle = previousStandstill == null ? null : previousStandstill.getDeliveryVehicle();
        OptaDeliveryOrder shadowCustomer = sourceCustomer;
        while (shadowCustomer != null && shadowCustomer.getDeliveryVehicle() != vehicle) {
            scoreDirector.beforeVariableChanged(shadowCustomer, "vehicle");
            shadowCustomer.setDeliveryVehicle(vehicle);
            scoreDirector.afterVariableChanged(shadowCustomer, "vehicle");
            shadowCustomer = shadowCustomer.getNextDeliveryOrder();
        }
    }

}
