package com.nerdery.jvm.resistance.bots;

import com.nerdery.jvm.resistance.models.Prescription;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class MalpracticeBot extends TestBot {

    @Override
    public boolean prescribeAntibiotic(float patientTemperature, Optional<Collection<Prescription>> previousPrescriptions) {
        return false;
    }
}
