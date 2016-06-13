package com.nerdery.jvm.resistance.bots.entrants.aolarte;

import com.nerdery.jvm.resistance.bots.DoctorBot;
import com.nerdery.jvm.resistance.models.Prescription;

import java.util.Collection;
import java.util.Optional;


public class AndresBot implements DoctorBot {
    @Override
    public String getUserId() {
        return "aolarte";
    }

    @Override
    public boolean prescribeAntibiotic(float patientTemperature, Collection<Prescription> previousPrescriptions) {
        if (patientTemperature>calculateCutoffTemp(previousPrescriptions)) {
            return true;
        }
        return false;
    }

    float calculateCutoffTemp(Collection<Prescription> previousPrescriptions) {
        final float baseline=97;

        float ret=baseline;
        final float max=103;
        float malpracticeThreshold=0.75F;
        float adjustment=2;
        if (previousPrescriptions.size()>0) {
            long yes=previousPrescriptions.stream().filter(p->(p.getTemperature()>baseline&&p.getTemperature()<max)).filter(Prescription::isPrescribedAntibiotics).count();
            long total=previousPrescriptions.stream().filter(p->(p.getTemperature()>baseline&&p.getTemperature()<max)).count();
            if (total!=0) {
               float dangerPercent = yes / total;
                if (dangerPercent > malpracticeThreshold) {
                    float adjustmentPercent = (dangerPercent - malpracticeThreshold) / (1 - malpracticeThreshold);
                    ret = ret + (adjustment * adjustmentPercent);
                }
            }
        }
        return ret;
    }
}
