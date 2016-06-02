package com.nerdery.jvm.resistance.bots.test;

import com.nerdery.jvm.resistance.models.Prescription;

import java.util.Collection;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class MalpracticeBot extends TestBot {

    @Override
    public boolean prescribeAntibiotic(float patientTemperature, Collection<Prescription> previousPrescriptions) {
        return false;
    }
}
