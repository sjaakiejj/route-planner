package com.openbusiness.opta.solver;

import org.optaplanner.core.impl.domain.variable.listener.PlanningVariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import com.openbusiness.opta.Vehicle;
import com.openbusiness.opta.Destination;
import com.openbusiness.opta.StandStill;

public class VehicleUpdatingVariableListener implements PlanningVariableListener<Destination> {

    public void beforeEntityAdded(ScoreDirector scoreDirector, Destination customer) {
        // Do nothing
    }

    public void afterEntityAdded(ScoreDirector scoreDirector, Destination customer) {
        updateVehicle(scoreDirector, customer);
    }

    public void beforeVariableChanged(ScoreDirector scoreDirector, Destination customer) {
        // Do nothing
    }

    public void afterVariableChanged(ScoreDirector scoreDirector, Destination customer) {
        updateVehicle(scoreDirector, customer);
    }

    public void beforeEntityRemoved(ScoreDirector scoreDirector, Destination customer) {
        // Do nothing
    }

    public void afterEntityRemoved(ScoreDirector scoreDirector, Destination customer) {
        // Do nothing
    }

    protected void updateVehicle(ScoreDirector scoreDirector, Destination sourceCustomer) {
        StandStill previousStandstill = sourceCustomer.getPreviousStandstill();
        Vehicle vehicle = previousStandstill == null ? null : previousStandstill.getDeliveryVehicle();
        Destination shadowCustomer = sourceCustomer;
        while (shadowCustomer != null && shadowCustomer.getDeliveryVehicle() != vehicle) {
            scoreDirector.beforeVariableChanged(shadowCustomer, "vehicle");
            shadowCustomer.setDeliveryVehicle(vehicle);
            scoreDirector.afterVariableChanged(shadowCustomer, "vehicle");
            shadowCustomer = shadowCustomer.getNextDestination();
        }
    }

}
