package com.nerdery.jvm.resistance.bots.entrants.jklun;

import com.nerdery.jvm.resistance.bots.DoctorBot;
import com.nerdery.jvm.resistance.models.Prescription;

import java.util.Collection;

/**
 * Bot that simulates Dr. Leo Spaceman.
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
public class DoctorSpacemanBot implements DoctorBot {
    @Override
    public String getUserId() {
        return "jklun";
    }

    @Override
    public boolean prescribeAntibiotic(float patientTemperature, Collection<Prescription> previousPrescriptions) {
        // Prescribe antibiotics if the temperature is greater than 102 or everybody did last time, anyway.
        return patientTemperature >= 102.0f ||
                previousPrescriptions.stream()
                        .map(Prescription::isPrescribedAntibiotics)
                        .reduce(true, (prescription1, prescription2) -> prescription1 && prescription2);
    }
}
