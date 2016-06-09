package com.nerdery.jvm.resistance.bots.test;

import com.nerdery.jvm.resistance.models.Prescription;

import java.util.Collection;

/**
 * A bot that only prescribes antibiotics when there is no chance that the patient is actually infected.
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
public class BadBot extends TestBot {
    @Override
    public boolean prescribeAntibiotic(float patientTemperature, Collection<Prescription> previousPrescriptions) {
        return patientTemperature < 100.0f;
    }
}