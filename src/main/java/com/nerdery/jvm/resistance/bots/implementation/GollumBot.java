package com.nerdery.jvm.resistance.bots.implementation;

import com.nerdery.jvm.resistance.bots.DoctorBot;
import com.nerdery.jvm.resistance.models.Prescription;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GollumBot implements DoctorBot {

    @Override
    public String getUserId() {
        return "myoung";
    }

    @Override
    public boolean prescribeAntibiotic(float patientTemperature, Collection<Prescription> previousPrescriptions) {
        List<Prescription> antiBioticPrescriptions = previousPrescriptions.stream()
                .filter(Prescription::isPrescribedAntibiotics)
                .collect(Collectors.toList());

        if (antiBioticPrescriptions.size() >= 2) {
            // You're using too much of the precious!
            return false;
        }
        if (patientTemperature >= 103f || patientTemperature < 100f) {
            // I'm sure they'll be fine. Or dead. That's fine. Everything's fine. More precious for me.
            return false;
        } else {
            return true;
        }
    }
}