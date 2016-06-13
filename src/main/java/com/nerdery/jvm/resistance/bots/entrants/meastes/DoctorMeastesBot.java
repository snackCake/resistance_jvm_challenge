package com.nerdery.jvm.resistance.bots.entrants.meastes;

import com.nerdery.jvm.resistance.bots.DoctorBot;
import com.nerdery.jvm.resistance.models.Prescription;

import java.util.Collection;

/**
 * Simple bot that prescribes when patient is likely infected.
 *
 * @author Mike Eastes <meastes@nerdery.com>
 */
public class DoctorMeastesBot implements DoctorBot {
    @Override
    public String getUserId() {
        return "meastes";
    }

    @Override
    public boolean prescribeAntibiotic(float patientTemperature, Collection<Prescription> previousPrescriptions) {
        return patientTemperature >= 101;
    }
}
