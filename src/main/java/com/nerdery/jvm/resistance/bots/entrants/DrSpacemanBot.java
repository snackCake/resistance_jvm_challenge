package com.nerdery.jvm.resistance.bots.entrants;

import com.nerdery.jvm.resistance.bots.DoctorBot;
import com.nerdery.jvm.resistance.models.Prescription;

import java.util.*;

/**
 * @author Zack Brown (zbrown@nerdery.com)
 */
public class DrSpacemanBot implements DoctorBot {

    private static final String USER_ID = "zrbrown";

    private static final float NO_INFECTION_CHANCE_TEMP_CEILING = 100;
    private static final float LOW_INFECTION_CHANCE_TEMP_CEILING = 101;
    private static final float INDETERMINATE_INFECTION_CHANCE_TEMP_CEILING = 102;
    private static final float HIGH_INFECTION_CHANCE_TEMP_CEILING = 103;
    private static final float DEFINITE_INFECTION_CHANCE_TEMP_CEILING = Float.MAX_VALUE;

    private final Random randomNumberGenerator = new Random(System.currentTimeMillis());
    private final Map<String, OtherDoctor> otherDoctors = new HashMap<>();

    @Override
    public String getUserId() {
        return USER_ID;
    }

    @Override
    public boolean prescribeAntibiotic(float patientTemperature, Collection<Prescription> previousPrescriptions) {
        if (otherDoctors.isEmpty()) {
            for (Prescription prescription : previousPrescriptions) {
                String userId = prescription.getUserId();

                if (USER_ID.equals(userId)) {
                    continue;
                }

                OtherDoctor otherDoctor = new OtherDoctor(userId);
                otherDoctors.put(userId, otherDoctor);
            }
        }

        for (Prescription prescription : previousPrescriptions) {
            if (USER_ID.equals(prescription.getUserId())) {
                continue;
            }

            otherDoctors.get(prescription.getUserId()).addPrescription(prescription);
        }

        PrescriptionStrategy strategy = PrescriptionStrategyFactory.INSTANCE.getStrategy(otherDoctors.values());
        return strategy.getPrescription(patientTemperature);
    }

    private class OtherDoctor {

        private final String userId;
        private List<Prescription> prescriptionHistory = new ArrayList<>();

        public OtherDoctor(String userId) {
            this.userId = userId;
        }

        public void addPrescription(Prescription prescription) {
            prescriptionHistory.add(prescription);
        }

        public double getAveragePrescriptionRate(float temperatureCeilingNonInclusive) {
            return prescriptionHistory.stream().filter(p -> p.getTemperature() < temperatureCeilingNonInclusive).
                    mapToDouble(p -> p.isPrescribedAntibiotics() ? 1 : 0).average().orElse(0);
        }
    }

    private static class PrescriptionStrategyFactory {

        public static final PrescriptionStrategyFactory INSTANCE = new PrescriptionStrategyFactory();

        private static final float MAX_PRESCRIPTION_ADD_AGGRESSION = 1;

        private PrescriptionStrategyFactory() {
        }

        public PrescriptionStrategy getStrategy(Collection<OtherDoctor> otherDoctors) {
            float aggression = 0;

            for (OtherDoctor doctor : otherDoctors) {
                aggression += getAggression(doctor);
            }

            float aggressionPercentage = aggression / (MAX_PRESCRIPTION_ADD_AGGRESSION * 4);

            if (aggressionPercentage <= 0) {
                return new StandardStrategy();
            } else if (aggressionPercentage < .25) {
                return new StandardStrategy();
            } else if (aggressionPercentage < .5) {
                return new StandardStrategy();
            } else if (aggressionPercentage < .75) {
                return new StandardStrategy();
            } else if (aggressionPercentage < .99) {
                return new StandardStrategy();
            } else {
                return new StandardStrategy();
            }
        }

        private float getAggression(OtherDoctor doctor) {
            double noInfectionChancePrescriptionRate = doctor.getAveragePrescriptionRate(NO_INFECTION_CHANCE_TEMP_CEILING);
            double lowInfectionChancePrescriptionRate = doctor.getAveragePrescriptionRate(LOW_INFECTION_CHANCE_TEMP_CEILING);
            double indeterminateInfectionChancePrescriptionRate = doctor.getAveragePrescriptionRate(INDETERMINATE_INFECTION_CHANCE_TEMP_CEILING);
            double highInfectionChancePrescriptionRate = doctor.getAveragePrescriptionRate(HIGH_INFECTION_CHANCE_TEMP_CEILING);
            double definiteInfectionChancePrescriptionRate = doctor.getAveragePrescriptionRate(DEFINITE_INFECTION_CHANCE_TEMP_CEILING);

            float aggression = 0;

            if (noInfectionChancePrescriptionRate > 0) {
                if (noInfectionChancePrescriptionRate == 1) {
                    aggression += 1;
                } else {
                    aggression += .75;
                }
            }

            if (lowInfectionChancePrescriptionRate > .25) {
                if (lowInfectionChancePrescriptionRate == 1) {
                    aggression += 1;
                } else {
                    aggression += .6;
                }
            } else if (lowInfectionChancePrescriptionRate == 0) {
                aggression -= .25;
            }

            if (indeterminateInfectionChancePrescriptionRate > .5) {
                if (indeterminateInfectionChancePrescriptionRate == 1) {
                    aggression += 1;
                } else {
                    aggression += .5;
                }
            } else {
                aggression -= .5;
            }

            if (highInfectionChancePrescriptionRate > .75) {
                if (highInfectionChancePrescriptionRate == 1) {
                    aggression += 1;
                }
            } else if (highInfectionChancePrescriptionRate > .5) {
                aggression -= .25;
            } else if (highInfectionChancePrescriptionRate > .25) {
                aggression -= .5;
            } else {
                aggression -= 1;
            }

            if (definiteInfectionChancePrescriptionRate < 1) {
                aggression -= 2;
            }

            return aggression > 0 ? aggression : 0;
        }
    }

    private static abstract class PrescriptionStrategy {
        public boolean getPrescription(float patientTemperature) {
            if (patientTemperature < NO_INFECTION_CHANCE_TEMP_CEILING) {
                return getNoInfectionChancePrescription();
            } else if (patientTemperature < LOW_INFECTION_CHANCE_TEMP_CEILING) {
                return getLowInfectionChancePrescription();
            } else if (patientTemperature < INDETERMINATE_INFECTION_CHANCE_TEMP_CEILING) {
                return getIndeterminateChancePrescription();
            } else if (patientTemperature < HIGH_INFECTION_CHANCE_TEMP_CEILING) {
                return getHighInfectionChancePrescription();
            } else {
                return getDefiniteInfectionChancePrescription();
            }
        }

        protected boolean getNoInfectionChancePrescription() {
            return false;
        }

        protected abstract boolean getLowInfectionChancePrescription();

        protected abstract boolean getIndeterminateChancePrescription();

        protected abstract boolean getHighInfectionChancePrescription();

        protected boolean getDefiniteInfectionChancePrescription() {
            return true;
        }
    }

    private static class StandardStrategy extends PrescriptionStrategy {

        @Override
        protected boolean getLowInfectionChancePrescription() {
            return false;
        }

        @Override
        protected boolean getIndeterminateChancePrescription() {
            return false;
        }

        @Override
        protected boolean getHighInfectionChancePrescription() {
            return true;
        }
    }
}
