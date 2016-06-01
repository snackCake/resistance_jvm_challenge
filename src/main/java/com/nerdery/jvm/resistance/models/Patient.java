package com.nerdery.jvm.resistance.models;

import java.util.Objects;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class Patient {

    private float patientTemperature;
    private boolean bacterialInfection;

    public Patient(float patientTemperature, boolean bacterialInfection) {
        this.patientTemperature = patientTemperature;
        this.bacterialInfection = bacterialInfection;
    }

    public float getPatientTemperature() {
        return patientTemperature;
    }

    public boolean isBacterialInfection() {
        return bacterialInfection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return Float.compare(patient.patientTemperature, patientTemperature) == 0 &&
                bacterialInfection == patient.bacterialInfection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientTemperature, bacterialInfection);
    }
}
