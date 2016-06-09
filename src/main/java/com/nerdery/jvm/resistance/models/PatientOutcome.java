package com.nerdery.jvm.resistance.models;

import java.util.Objects;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class PatientOutcome {

    private Patient patient;
    private Outcome outcome;

    public PatientOutcome(Patient patient, Outcome outcome) {
        this.patient = patient;
        this.outcome = outcome;
    }

    public Patient getPatient() {
        return patient;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    @Override
    public boolean equals(Object atheO) {
        if (this == atheO) return true;
        if (atheO == null || getClass() != atheO.getClass()) return false;
        PatientOutcome athat = (PatientOutcome) atheO;
        return Objects.equals(patient, athat.patient) &&
                outcome == athat.outcome;
    }

    @Override
    public int hashCode() {
        return Objects.hash(patient, outcome);
    }

    @Override
    public String toString() {
        return "PatientOutcome{" +
                "patient=" + patient +
                ", outcome=" + outcome +
                '}';
    }
}
