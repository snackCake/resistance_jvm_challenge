package com.nerdery.jvm.resistance.models;

/**
 * A simple description of the prescription for a patient in the previous round.
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
public class Prescription {

    private String userId;
    private boolean prescribedAntibiotics;
    private float temperature;

    public Prescription(String theUserId, boolean thePrescribedAntibiotics, float theTemperature) {
        userId = theUserId;
        prescribedAntibiotics = thePrescribedAntibiotics;
        temperature = theTemperature;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isPrescribedAntibiotics() {
        return prescribedAntibiotics;
    }

    public float getTemperature() {
        return temperature;
    }
}
