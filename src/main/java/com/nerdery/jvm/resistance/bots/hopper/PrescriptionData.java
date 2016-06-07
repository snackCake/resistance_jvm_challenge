package com.nerdery.jvm.resistance.bots.hopper;

import com.nerdery.jvm.resistance.models.Prescription;

/**
 * A prettified version of the prescription object that simplifies decision making.
 */
class PrescriptionData {
    private boolean prescribedAntibiotics;
    private PatientTemp temp;

    PrescriptionData(boolean prescribedAntibiotics, PatientTemp temp) {
        this.prescribedAntibiotics = prescribedAntibiotics;
        this.temp = temp;
    }

    boolean isPrescribedAntibiotics() {
        return prescribedAntibiotics;
    }

    PatientTemp getTemp() {
        return temp;
    }

    static PrescriptionData fromPrescription(Prescription p) {
        return new PrescriptionData(p.isPrescribedAntibiotics(), PatientTemp.lookupTemp(p.getTemperature()));
    }
}
