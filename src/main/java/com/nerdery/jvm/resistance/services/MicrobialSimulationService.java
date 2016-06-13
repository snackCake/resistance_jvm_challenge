package com.nerdery.jvm.resistance.services;

import com.nerdery.jvm.resistance.models.Outcome;
import com.nerdery.jvm.resistance.models.Patient;
import com.nerdery.jvm.resistance.models.PatientOutcome;
import com.nerdery.jvm.resistance.models.Prescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class MicrobialSimulationService {

    private static final Logger logger = LoggerFactory.getLogger(MicrobialSimulationService.class);
    private Random randomGenerator;
    private double dailyLuck;


    public MicrobialSimulationService() {
        randomGenerator = new SecureRandom();
    }

    public List<PatientOutcome> divineOutcomes(List<Patient> patients, List<Prescription> prescriptions) {
        dailyLuck = randomGenerator.nextDouble();
        return IntStream.range(0, patients.size()).mapToObj(i -> {
            Patient patient = patients.get(i);
            Prescription prescription = prescriptions.get(i);
            Outcome outcome = decideOutcome(prescriptions, patient, prescription);
            return new PatientOutcome(patient, prescription, outcome);
        }).collect(Collectors.toList());
    }

    private Outcome decideOutcome(List<Prescription> prescriptions, Patient patient, Prescription prescription) {
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

        logger.info("Patient [ {} ] got outcome: {}", patient, outcome);
        return outcome;
    }

    private boolean hasGoodLuck(float temperature) {
        float infectionSpan = Patient.GUARANTEED_INFECTION_TEMPERATURE - Patient.NO_INFECTION_TEMPERATURE;
        double temperatureLuckThreshold = Math.pow(((double)(temperature - Patient.NO_INFECTION_TEMPERATURE) / infectionSpan), 5.0);
        return dailyLuck > temperatureLuckThreshold;
    }
}
