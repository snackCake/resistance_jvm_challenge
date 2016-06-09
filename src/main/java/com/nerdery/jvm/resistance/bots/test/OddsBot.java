package com.nerdery.jvm.resistance.bots.test;

import com.nerdery.jvm.resistance.models.Prescription;

import java.util.Collection;

/**
 * Bot that that prescribes antibiotics if the odds are even or better.
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
public class OddsBot extends TestBot {
    @Override
    public boolean prescribeAntibiotic(float patientTemperature, Collection<Prescription> previousPrescriptions) {
        return patientTemperature > 101.0f;
    }
}
