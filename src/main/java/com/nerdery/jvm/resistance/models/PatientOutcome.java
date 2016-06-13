package com.nerdery.jvm.resistance.models;

import java.util.Objects;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class PatientOutcome {

    private Patient patient;
    private Prescription prescription;
    private Outcome outcome;

    public PatientOutcome(Patient patient, Prescription prescription, Outcome outcome) {
        this.patient = patient;
        this.prescription = prescription;
        this.outcome = outcome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientOutcome that = (PatientOutcome) o;
        return Objects.equals(patient, that.patient) &&
                Objects.equals(prescription, that.prescription) &&
                outcome == that.outcome;
    }

    @Override
    public int hashCode() {
        return Objects.hash(patient, prescription, outcome);
    }

    public Patient getPatient() {
        return patient;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    @Override
    public String toString() {
        return "PatientOutcome{" +
                "patient=" + patient +
                ", prescription=" + prescription +
                ", outcome=" + outcome +
                '}';
    }
}
