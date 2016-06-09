package com.nerdery.jvm.resistance.models;

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
}
