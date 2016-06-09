package com.nerdery.jvm.resistance.bots.test;

import com.nerdery.jvm.resistance.models.Prescription;

import java.util.Collection;

/**
 * Bot that only prescribes if it is 100% positive that the patient has a bacterial infection.
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
public class PositiveBot extends TestBot {
    @Override
    public boolean prescribeAntibiotic(float patientTemperature, Collection<Prescription> previousPrescriptions) {
        return patientTemperature >= 103.0f;
    }
}