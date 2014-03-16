package com.openbusiness.opta;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang.builder.CompareToBuilder;

public class VrpOrderDifficultyComparator implements Comparator<OptaDeliveryOrder>, Serializable {

    public int compare(OptaDeliveryOrder a, OptaDeliveryOrder b) {
        return new CompareToBuilder()
                // TODO experiment with (aLatitude - bLatitude) % 10
                .append(a.getLocation().getLat(), b.getLocation().getLat())
                .append(a.getLocation().getLon(), b.getLocation().getLon())
                .append(a.getWeight(), b.getWeight())
                .append(a.getVolume(), b.getVolume())
                .toComparison();
    }

}
