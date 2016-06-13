package com.nerdery.jvm.resistance.services;

import com.nerdery.jvm.resistance.bots.DoctorBot;
import com.nerdery.jvm.resistance.models.Outcome;
import com.nerdery.jvm.resistance.models.Patient;
import com.nerdery.jvm.resistance.models.PatientOutcome;
import com.nerdery.jvm.resistance.models.Prescription;
import com.nerdery.jvm.resistance.models.tournament.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class ResistanceSimulationService {

    private MicrobialSimulationService microbialSimulation;

    public ResistanceSimulationService(MicrobialSimulationService microbialSimulation) {
        this.microbialSimulation = microbialSimulation;
    }

    public void runTournament(Tournament tournament) {
        tournament.getGenerations()
                .stream()
                .map(this::runGeneration)
                .forEach(this::updateEntrantsWithResults);
        System.err.println(tournament.listScoredEntrants());
    }

    private void updateEntrantsWithResults(List<Map<Entrant, PatientOutcome>> generationPatientOutcomes) {
        Map<Entrant, EntrantGenerationResult> entrantGenerationResults = new HashMap<>();
        generationPatientOutcomes.get(0)
                .keySet()
                .stream()
                .forEach(entrant -> entrantGenerationResults.put(entrant, new EntrantGenerationResult()));
        generationPatientOutcomes.stream()
                .forEach(dayPatientOutcomes ->
                        dayPatientOutcomes.keySet()
                                .stream()
                                .forEach(entrant -> updateGenerationWithDay(entrantGenerationResults, dayPatientOutcomes, entrant)));
        entrantGenerationResults.entrySet()
                .stream()
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
        System.err.println("Running generation: " + generation);
        final List<PatientOutcome> previousDay = new ArrayList<>(generation.getEntrants().size());
        return generation.getDays()
                .stream()
                .map(day -> runAndScoreDay(previousDay, day))
                .collect(Collectors.toList());
    }

    private Map<Entrant, PatientOutcome> runAndScoreDay(List<PatientOutcome> previousDay, TownDay day) {
        if (previousDay.size() > 0 && previousDay.get(0).getOutcome() == Outcome.UNLUCKY_VIRAL_ANTIBIOTICS) {
            System.err.println("Not running day, because the town has been wiped out: " + day);
            return Collections.emptyMap();
        }

        System.err.println("Running day: " + day);
        List<Prescription> prescriptions = runDay(day, previousDay);

        List<PatientOutcome> outcomes = microbialSimulation.divineOutcomes(day.getPatients(), prescriptions);
        System.err.println("On day #" + day.getDayNumber() + " patients had the following outcomes: " + outcomes);
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
                    boolean antibiotics;
                    try {
                        antibiotics = doctor.prescribeAntibioticHipaaDubious(patient.getTemperature(), previousDay);
                    } catch (Exception e) {
                        // Broad Exception catch here, because I can't trust that all bots are error free.
                        e.printStackTrace();
                        antibiotics = false;
                    }
                    return new Prescription(doctor.getUserId(), antibiotics, patient.getTemperature());
                }).collect(Collectors.toList());
    }
}
