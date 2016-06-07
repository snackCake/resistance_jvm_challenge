package com.nerdery.jvm.resistance.bots.hopper;

/**
 * Enum for representing patient temperature categories.
 */
enum PatientTemp {
    P0(0.0f), P25(0.25f), P50(0.50f), P75(0.75f), P100(1.0f);

    private float probInfected;

    PatientTemp(float probInfected) {
        this.probInfected = probInfected;
    }

    float getProbInfected() {
        return probInfected;
    }

    static PatientTemp lookupTemp(float temp) {
        if (temp < 100.0) return P0;
        else if (temp >= 100.0 && temp < 101.0) return P25;
        else if (temp >= 101.0 && temp < 102.0) return P50;
        else if (temp >= 102.0 && temp < 103.0) return P75;
        else return P100; //temp >= 103.0
    }
}
