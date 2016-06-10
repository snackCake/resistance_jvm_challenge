package com.nerdery.jvm.resistance.bots;

import com.nerdery.jvm.resistance.models.PatientOutcome;
import com.nerdery.jvm.resistance.models.Prescription;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
     * @param previousPrescriptions A collection of prescriptions from the previous round. Empty if this is the first round.
     * @return true if the patient should take antibiotics, false if they should not.
     */
    boolean prescribeAntibiotic(float patientTemperature, Collection<Prescription> previousPrescriptions);

    /**
     * @param patientTemperature The fahrenheit temperature of the patient
     * @param previousOutcomes A collection of patient outcomes from the previous round. Empty if this is the first round.
     * @return true if the patient should take antibiotics, false if they should not.
     */
    default boolean prescribeAntibioticHipaaDubious(float patientTemperature, Collection<PatientOutcome> previousOutcomes) {
        List<Prescription> prescriptions = previousOutcomes.stream()
                .map(PatientOutcome::getPrescription)
                .collect(Collectors.toList());
        return prescribeAntibiotic(patientTemperature, prescriptions);
    }

}
