package com.openbusiness.opta.solver;

import java.io.Serializable;
import java.util.Comparator;

import com.openbusiness.opta.Destination;

import org.apache.commons.lang.builder.CompareToBuilder;

public class VrpOrderDifficultyComparator implements Comparator<Destination>, Serializable {

    public int compare(Destination a, Destination b) {
        return new CompareToBuilder()
                // TODO experiment with (aLatitude - bLatitude) % 10
                .append(a.getLocation().getLat(), b.getLocation().getLat())
                .append(a.getLocation().getLon(), b.getLocation().getLon())
                .append(a.getWeight(), b.getWeight())
                .append(a.getVolume(), b.getVolume())
                .toComparison();
    }

}
