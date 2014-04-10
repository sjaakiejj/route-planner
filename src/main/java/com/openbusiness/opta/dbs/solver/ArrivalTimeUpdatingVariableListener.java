package com.openbusiness.opta.dbs.solver;

import org.apache.commons.lang.ObjectUtils;
import org.optaplanner.core.impl.domain.variable.listener.PlanningVariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import com.openbusiness.opta.Destination;
import com.openbusiness.opta.StandStill;
import com.openbusiness.opta.dbs.DBSBranch;

public class ArrivalTimeUpdatingVariableListener implements PlanningVariableListener<Destination> {

    public void beforeEntityAdded(ScoreDirector scoreDirector, Destination customer) {
        // Do nothing
    }

    public void afterEntityAdded(ScoreDirector scoreDirector, Destination customer) {
        if (customer instanceof DBSBranch) {
            updateVehicle(scoreDirector, (DBSBranch) customer);
        }
    }

    public void beforeVariableChanged(ScoreDirector scoreDirector, Destination customer) {
        // Do nothing
    }

    public void afterVariableChanged(ScoreDirector scoreDirector, Destination customer) {
        if (customer instanceof DBSBranch) {
            updateVehicle(scoreDirector, (DBSBranch) customer);
        }
    }

    public void beforeEntityRemoved(ScoreDirector scoreDirector, Destination customer) {
        // Do nothing
    }

    public void afterEntityRemoved(ScoreDirector scoreDirector, Destination customer) {
        // Do nothing
    }

    protected void updateVehicle(ScoreDirector scoreDirector, DBSBranch sourceDeliveryOrder) {
        StandStill previousStandStill = sourceDeliveryOrder.getPreviousStandstill();
        Integer milliDepartureTime = (previousStandStill instanceof DBSBranch)
                ? ((DBSBranch) previousStandStill).getDepartureTime() : null;
        DBSBranch shadowDeliveryOrder = sourceDeliveryOrder;
        Integer milliArrivalTime = calculateMilliArrivalTime(shadowDeliveryOrder, milliDepartureTime);
        while (shadowDeliveryOrder != null && ObjectUtils.notEqual(shadowDeliveryOrder.getMilliArrivalTime(), milliArrivalTime)) {
            scoreDirector.beforeVariableChanged(shadowDeliveryOrder, "milliArrivalTime");
            shadowDeliveryOrder.setMilliArrivalTime(milliArrivalTime);
            scoreDirector.afterVariableChanged(shadowDeliveryOrder, "milliArrivalTime");
            milliDepartureTime = shadowDeliveryOrder.getDepartureTime();
            shadowDeliveryOrder = shadowDeliveryOrder.getNextDestination();
            milliArrivalTime = calculateMilliArrivalTime(shadowDeliveryOrder, milliDepartureTime);
        }
    }

    private Integer calculateMilliArrivalTime(DBSBranch customer, Integer previousMilliDepartureTime) {
        if (customer == null) {
            return null;
        }
        if (previousMilliDepartureTime == null) {
            // PreviousStandStill is the Vehicle, so we leave from the Depot at the best suitable time
            return (int)Math.max(customer.getMilliReadyTime(), customer.getDistanceToPrevOrder());
        }
        return (int)(previousMilliDepartureTime + customer.getDistanceToPrevOrder());
    }

}
