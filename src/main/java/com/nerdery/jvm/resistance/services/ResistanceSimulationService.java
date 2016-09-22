package com.nerdery.jvm.resistance.services;

import com.nerdery.jvm.resistance.bots.DoctorBot;
import com.nerdery.jvm.resistance.models.Outcome;
import com.nerdery.jvm.resistance.models.Patient;
import com.nerdery.jvm.resistance.models.PatientOutcome;
import com.nerdery.jvm.resistance.models.Prescription;
import com.nerdery.jvm.resistance.models.tournament.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class ResistanceSimulationService {

    private static final Logger logger = LoggerFactory.getLogger(ResistanceSimulationService.class);
    private static final String csvPattern = "resistance-{0,date}-{0,time}.csv";

    private MicrobialSimulationService microbialSimulation;
    private CSVPrinter csvPrinter;

    public ResistanceSimulationService(MicrobialSimulationService microbialSimulation) {
        this.microbialSimulation = microbialSimulation;
    }

    public void runTournament(Tournament tournament) {
        try (FileWriter csvOutput = new FileWriter(new File(MessageFormat.format(csvPattern, new Date())))) {
            initCsv(csvOutput);

            tournament.getGenerations()
                    .stream()
                    .map(this::runGeneration)
                    .forEach(this::updateEntrantsWithResults);

            csvPrinter.close();
            logger.info("Tournament entrants: {}", tournament.listScoredEntrants());
        } catch (IOException e) {
            logger.error("Error opening CSV file", e);
        }
    }

    private void updateEntrantsWithResults(List<Map<Entrant, PatientOutcome>> generationPatientOutcomes) {
        Map<Entrant, EntrantGenerationResult> entrantGenerationResults = new HashMap<>();
        generationPatientOutcomes.get(0)
                .keySet()
                .forEach(entrant -> entrantGenerationResults.put(entrant, new EntrantGenerationResult()));
        generationPatientOutcomes
                .forEach(dayPatientOutcomes ->
                        dayPatientOutcomes.keySet()
                                .forEach(entrant -> updateGenerationWithDay(entrantGenerationResults, dayPatientOutcomes, entrant)));
        entrantGenerationResults.entrySet()
                .forEach(entrantPair -> entrantPair.getKey().addGenerationResult(entrantPair.getValue()));
    }

    private void updateGenerationWithDay(Map<Entrant, EntrantGenerationResult> entrantGenerationResults,
                                         Map<Entrant, PatientOutcome> dayPatientOutcomes,
                                         Entrant entrant) {
        Outcome outcome = dayPatientOutcomes.get(entrant).getOutcome();
        EntrantGenerationResult result = entrantGenerationResults.get(entrant);
        result.addScore(outcome.getInsurancePayment()).addPatientsTreated(1);
        switch (outcome) {
            case VIRAL_REST:
                result.addViralCures(1);
                break;
            case LUCKY_BACTERIAL_REST:
                break;
            case UNLUCKY_BACTERIAL_REST:
                result.addMalpracticeSuits(1);
                break;
            case BACTERIAL_ANTIBIOTICS:
                result.addBacterialCures(1);
                break;
            case LUCKY_VIRAL_ANTIBIOTICS:
                break;
            case UNLUCKY_VIRAL_ANTIBIOTICS:
                result.zombieApocalypseTriggered(true);
                break;
        }
    }

    private List<Map<Entrant, PatientOutcome>> runGeneration(TownGeneration generation) {
        logger.info("Running generation: {}", generation);
        resetEntrants(generation.getEntrants());
        final List<PatientOutcome> previousDay = new ArrayList<>(generation.getEntrants().size());
        return generation.getDays()
                .stream()
                .map(day -> {
                    Map<Entrant, PatientOutcome> entrantPatientOutcomeMap = runAndScoreDay(previousDay, day);
                    entrantPatientOutcomeMap.entrySet().forEach(entrantPatientOutcome ->
                            logPrescriptionEvent(generation.getGenerationNumber(),
                                    day,
                                    entrantPatientOutcome.getValue().getPatient(),
                                    entrantPatientOutcome.getKey().getDoctorBot(),
                                    entrantPatientOutcome.getValue().getPrescription().isPrescribedAntibiotics(),
                                    entrantPatientOutcome.getValue().getOutcome(),
                                    entrantPatientOutcome.getValue().getPrescription().getContemplationTime()));
                    return entrantPatientOutcomeMap;
                })
                .collect(Collectors.toList());
    }

    private void resetEntrants(List<Entrant> entrants) {
        entrants.forEach(Entrant::resetDoctorBot);
    }

    private Map<Entrant, PatientOutcome> runAndScoreDay(List<PatientOutcome> previousDay, TownDay day) {
        if (previousDay.size() > 0 && previousDay.get(0).getOutcome() == Outcome.UNLUCKY_VIRAL_ANTIBIOTICS) {
            logger.info("Not running day, because the town has been wiped out: {}", day);
            return Collections.emptyMap();
        }

        logger.info("Running day: {}", day);
        List<Prescription> prescriptions = runDay(day, previousDay);

        List<PatientOutcome> outcomes = microbialSimulation.divineOutcomes(day.getPatients(), prescriptions);
        logger.info("On day #{} patients had the following outcomes: {}", day.getDayNumber(), outcomes);
        previousDay.clear();
        previousDay.addAll(outcomes);

        Map<Entrant, PatientOutcome> entrantOutcomes = new HashMap<>();
        IntStream.range(0, outcomes.size()).forEach(i -> entrantOutcomes.put(day.getDoctors().get(i), outcomes.get(i)));
        return entrantOutcomes;
    }

    private List<Prescription> runDay(TownDay day, Collection<PatientOutcome> previousDay) {
        return IntStream.range(0, day.getDoctors().size())
                .parallel()
                .mapToObj(i -> {
                    Patient patient = day.getPatients().get(i);
                    DoctorBot doctor = day.getDoctors().get(i).getDoctorBot();
                    return seePatient(previousDay, patient, doctor);
                }).collect(Collectors.toList());
    }

    private Prescription seePatient(Collection<PatientOutcome> previousDay, Patient patient, DoctorBot doctor) {
        boolean antibiotics;
        long startTime = System.currentTimeMillis();
        try {
            antibiotics = doctor.prescribeAntibioticHipaaDubious(patient.getTemperature(), previousDay);
        } catch (Exception e) {
            // Broad Exception catch here, because I can't trust that all bots are error free.
            logger.info("Doctor will prescribe rest.");
            logger.error("Doctor threw an error: " + doctor.getClass().getSimpleName(), e);
            antibiotics = false;
        }
        long runtime = System.currentTimeMillis() - startTime;
        return new Prescription(doctor.getUserId(), antibiotics, patient.getTemperature(), runtime);
    }


    private void initCsv(FileWriter csvOutput) throws IOException {
        csvPrinter = new CSVPrinter(csvOutput, CSVFormat.DEFAULT);
        try {
            csvPrinter.printRecord("Generation Number",
                    "Day Number",
                    "Doctor User ID",
                    "Doctor Class Name",
                    "Patient Temperature",
                    "Patient Bacterial Infection",
                    "Doctor Prescribed Antibiotics",
                    "Outcome",
                    "Income",
                    "Zombies",
                    "Runtime ms");
        } catch (IOException e) {
            logger.error("Failed to write CSV header", e);
        }
    }

    private void logPrescriptionEvent(int generationNumber,
                                      TownDay day,
                                      Patient patient,
                                      DoctorBot doctor,
                                      boolean antibiotics,
                                      Outcome outcome,
                                      long runtime) {
        try {
            csvPrinter.printRecord(((Integer)generationNumber).toString(),
                    ((Integer)day.getDayNumber()).toString(),
                    doctor.getUserId(),
                    doctor.getClass().getSimpleName(),
                    ((Float)patient.getTemperature()).toString(),
                    ((Boolean)patient.isBacterialInfection()).toString(),
                    ((Boolean)antibiotics).toString(),
                    outcome.name(),
                    ((Integer)outcome.getInsurancePayment()).toString(),
                    ((Boolean)outcome.isZombieApocalypse()).toString(),
                    ((Long)runtime).toString());
        } catch (IOException e) {
            logger.error("Failed to write CSV Record for doctor: " + doctor.getClass().getSimpleName(), e);
        }
    }
}
