package com.nerdery.jvm.resistance.services;

import com.nerdery.jvm.resistance.bots.DoctorBot;
import com.nerdery.jvm.resistance.models.Patient;
import com.nerdery.jvm.resistance.models.Prescription;
import com.nerdery.jvm.resistance.models.tournament.Tournament;
import com.nerdery.jvm.resistance.models.tournament.TownDay;
import com.nerdery.jvm.resistance.models.tournament.TownGeneration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class ResistanceSimulationService {

    public void runTournament(Tournament tournament) {
        tournament.getGenerations()
                .stream()
                .forEach(this::runGeneration);
    }

    private void runGeneration(TownGeneration generation) {
        final Collection<Prescription> previousDay = new ArrayList<>(generation.getEntrants().size());
        generation.getDays()
                .stream()
                .forEach(day -> {
                    Collection<Prescription> prescriptions = runDay(day, previousDay);
                    previousDay.clear();
                    previousDay.addAll(prescriptions);
                });
    }

    private Collection<Prescription> runDay(TownDay day, Collection<Prescription> previousDay) {
        return IntStream.range(0, day.getDoctors().size())
                .parallel()
                .mapToObj(i -> {
                    Patient patient = day.getPatients().get(i);
                    DoctorBot doctor = day.getDoctors().get(i).getDoctorBot();
                    boolean antibiotics = doctor.prescribeAntibiotic(patient.getTemperature(), previousDay);
                    return new Prescription(doctor.getUserId(), antibiotics, patient.getTemperature());
                }).collect(Collectors.toList());
    }
}
