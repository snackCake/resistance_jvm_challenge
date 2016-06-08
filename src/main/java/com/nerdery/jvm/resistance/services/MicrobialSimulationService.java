package com.nerdery.jvm.resistance.services;

import com.nerdery.jvm.resistance.models.Outcome;
import com.nerdery.jvm.resistance.models.Patient;
import com.nerdery.jvm.resistance.models.PatientOutcome;
import com.nerdery.jvm.resistance.models.Prescription;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class MicrobialSimulationService {

    public List<PatientOutcome> divineOutcomes(List<Patient> patients, List<Prescription> prescriptions) {

        // TODO: Test Cases!
        return IntStream.range(0, patients.size()).mapToObj(i -> {

            Patient patient = patients.get(i);
            Prescription prescription = prescriptions.get(i);

            Outcome outcome;
            if (!prescription.isPrescribedAntibiotics() && !patient.isBacterialInfection()) {
                outcome = Outcome.VIRAL_REST;
            } else if (!prescription.isPrescribedAntibiotics() && patient.isBacterialInfection()) {
                if (hasGoodLuck(patient.getTemperature())) {
                    outcome = Outcome.LUCKY_BACTERIAL_REST;
                } else {
                    outcome = Outcome.UNLUCKY_BACTERIAL_REST;
                }
            } else if (prescription.isPrescribedAntibiotics() && patient.isBacterialInfection()) {
                outcome = Outcome.BACTERIAL_ANTIBIOTICS;
            } else /* if (prescription.isPrescribedAntibiotics() && !patient.isBacterialInfection())*/ {
                if (prescriptions.stream()
                        .map(Prescription::isPrescribedAntibiotics)
                        .reduce(false, (combined, prescribed) ->  (combined || !prescribed))) {
                    outcome = Outcome.LUCKY_VIRAL_ANTIBIOTICS;
                } else {
                    outcome = Outcome.UNLUCKY_VIRAL_ANTIBIOTICS;
                }
            }
            return new PatientOutcome(patient, outcome);
        }).collect(Collectors.toList());
    }

    // TODO: Write this!
    private boolean hasGoodLuck(float temperature) {
        return false;
    }
}
