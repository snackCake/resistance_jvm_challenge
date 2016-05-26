package com.nerdery.jvm.resistance.bots;

import com.nerdery.jvm.resistance.models.Prescription;

import java.util.Collection;
import java.util.Optional;

/**
 * DoctorBot defines the interface that must be implemented by bot entries to the tournament.
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
public interface DoctorBot {

    /**
     * @return Your Nerdery User ID
     */
    String getUserId();

    /**
     * @param patientTemperature The fahrenheit temperature of the patient
     * @param previousPrescriptions An optional collection of prescriptions from the previous round. None if this is the first round.
     * @return true if the patient should take antibiotics, false if they should not.
     */
    boolean prescribeAntibiotic(float patientTemperature, Optional<Collection<Prescription>> previousPrescriptions);
}
