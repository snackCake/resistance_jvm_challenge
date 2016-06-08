package com.nerdery.jvm.resistance.models;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

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

    public static PatientBuilder builder() {
        return new PatientBuilder();
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientTemperature, bacterialInfection);
    }

    public static class PatientBuilder {

        public static final float DEFAULT_MIN_TEMPERATURE = 98.0f;
        public static final float DEFAULT_MAX_TEMPERATURE = 104.0f;

        private float maxTemperature;
        private float minTemperature;
        private float patientTemperature;
        private boolean bacterialInfection;
        private boolean randomizeNeeded;
        private Random randomGenerator;

        private PatientBuilder() {
            randomizeNeeded = true;
            minTemperature = DEFAULT_MIN_TEMPERATURE;
            maxTemperature = DEFAULT_MAX_TEMPERATURE;
            patientTemperature = (minTemperature + maxTemperature) / 2.0f;
            bacterialInfection = false;
            randomGenerator = new SecureRandom();
        }

        public Patient build() {
            randomizePatientParameters();
            return new Patient(patientTemperature, bacterialInfection);
        }

        private void randomizePatientParameters() {
            if (randomizeNeeded) {
                patientTemperature = minTemperature + (maxTemperature - minTemperature) * randomGenerator.nextFloat();
                bacterialInfection = randomGenerator.nextBoolean();
                randomizeNeeded = false;
            }
        }

        public PatientBuilder maxTemperature(float maxTemperature) {
            this.maxTemperature = maxTemperature;
            return this;
        }

        public PatientBuilder minTemperature(float minTemperature) {
            this.minTemperature = minTemperature;
            return this;
        }

        public PatientBuilder randomizeNeeded(boolean randomizeNeeded) {
            this.randomizeNeeded = randomizeNeeded;
            return this;
        }
    }
}
