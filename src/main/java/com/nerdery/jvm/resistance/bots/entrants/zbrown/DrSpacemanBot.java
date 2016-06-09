package com.nerdery.jvm.resistance.bots.entrants.zbrown;

import com.nerdery.jvm.resistance.bots.DoctorBot;
import com.nerdery.jvm.resistance.models.Prescription;

import java.util.*;

/**
 * @author Zack Brown (zbrown@nerdery.com)
 */
public class DrSpacemanBot implements DoctorBot {

    private static final String USER_ID = "zbrown";

    private static final float NO_INFECTION_CHANCE_TEMP_CEILING = 100;
    private static final float LOW_INFECTION_CHANCE_TEMP_CEILING = 101;
    private static final float INDETERMINATE_INFECTION_CHANCE_TEMP_CEILING = 102;
    private static final float HIGH_INFECTION_CHANCE_TEMP_CEILING = 103;
    private static final float DEFINITE_INFECTION_CHANCE_TEMP_CEILING = Float.MAX_VALUE;

    private final Map<String, OtherDoctor> otherDoctors = new HashMap<>();
    private int roundNumber;

    @Override
    public String getUserId() {
        return USER_ID;
    }

    @Override
    public boolean prescribeAntibiotic(float patientTemperature, Collection<Prescription> previousPrescriptions) {
        roundNumber++;

        PrescriptionStrategy strategy = new StandardStrategy();

        if (otherDoctors.isEmpty()) {
            for (Prescription prescription : previousPrescriptions) {
                String userId = prescription.getUserId();

                if (USER_ID.equals(userId)) {
                    continue;
                }

                otherDoctors.put(userId, new OtherDoctor());
            }
        } else {
            for (Prescription prescription : previousPrescriptions) {
                if (USER_ID.equals(prescription.getUserId())) {
                    continue;
                }

                otherDoctors.get(prescription.getUserId()).addPrescription(prescription);
            }

            strategy = PrescriptionStrategyFactory.INSTANCE.getStrategy(otherDoctors.values());
        }

        return strategy.getPrescription(patientTemperature, roundNumber);
    }

    private class OtherDoctor {

        private List<Prescription> prescriptionHistory = new ArrayList<>();

        void addPrescription(Prescription prescription) {
            prescriptionHistory.add(prescription);
        }

        double getAveragePrescriptionRate(float temperatureCeilingNonInclusive) {
            return prescriptionHistory.stream().filter(p -> p.getTemperature() < temperatureCeilingNonInclusive).
                    mapToDouble(p -> p.isPrescribedAntibiotics() ? 1 : 0).average().orElse(0);
        }
    }

    private static class PrescriptionStrategyFactory {

        static final PrescriptionStrategyFactory INSTANCE = new PrescriptionStrategyFactory();

        private PrescriptionStrategyFactory() {
        }

        PrescriptionStrategy getStrategy(Collection<OtherDoctor> otherDoctors) {
            float aggression = 0;

            for (OtherDoctor doctor : otherDoctors) {
                aggression += getAggression(doctor);
            }

            float aggressionPercentage = aggression / 4;

            if (aggressionPercentage <= 0) {
                return new AggressiveStrategy();
            } else if (aggressionPercentage < .25) {
                return new ProgressivelyAggressiveStrategy();
            } else if (aggressionPercentage < .5) {
                return new StandardStrategy();
            } else if (aggressionPercentage < .75) {
                return new DefensiveStrategy();
            } else if (aggressionPercentage < .99) {
                return new SafeStrategy();
            } else {
                return new OverprescribeDefenseStrategy();
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
                    aggression += .5;
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

        static final int MIN_ROUND_LIMIT = 25;

        final PrimitiveIterator.OfInt randomNumberGenerator = new Random(System.currentTimeMillis()).ints(1, 101).iterator();
        int roundNumber;

        boolean getPrescription(float patientTemperature, int roundNumber) {
            this.roundNumber = roundNumber;

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

        boolean getDefiniteInfectionChancePrescription() {
            return true;
        }

        /**
         * Randomly decides whether or not to prescribe antibiotics based on a given percent chance.
         *
         * @param prescribeChance the percent chance that antibiotics will be prescribed
         * @return {@code true} if antibiotics are prescribed; {@code false} otherwise
         */
        boolean prescribeRandom(int prescribeChance) {
            return randomNumberGenerator.nextInt() <= prescribeChance;
        }
    }

    private static class SafeStrategy extends PrescriptionStrategy {

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
            return prescribeRandom(40);
        }
    }

    private static class DefensiveStrategy extends PrescriptionStrategy {

        @Override
        protected boolean getLowInfectionChancePrescription() {
            return roundNumber < MIN_ROUND_LIMIT * .8 && prescribeRandom(25);
        }

        @Override
        protected boolean getIndeterminateChancePrescription() {
            return roundNumber < MIN_ROUND_LIMIT * .8 ? prescribeRandom(35) : prescribeRandom(20);
        }

        @Override
        protected boolean getHighInfectionChancePrescription() {
            return roundNumber < MIN_ROUND_LIMIT * .8 ? prescribeRandom(60) : prescribeRandom(40);
        }
    }

    private static class StandardStrategy extends PrescriptionStrategy {

        @Override
        protected boolean getLowInfectionChancePrescription() {
            return prescribeRandom(25);
        }

        @Override
        protected boolean getIndeterminateChancePrescription() {
            return prescribeRandom(50);
        }

        @Override
        protected boolean getHighInfectionChancePrescription() {
            return prescribeRandom(75);
        }
    }

    private static class AggressiveStrategy extends PrescriptionStrategy {

        @Override
        protected boolean getNoInfectionChancePrescription() {
            return true;
        }

        @Override
        protected boolean getLowInfectionChancePrescription() {
            return true;
        }

        @Override
        protected boolean getIndeterminateChancePrescription() {
            return true;
        }

        @Override
        protected boolean getHighInfectionChancePrescription() {
            return true;
        }
    }

    private static class ProgressivelyAggressiveStrategy extends PrescriptionStrategy {

        @Override
        protected boolean getNoInfectionChancePrescription() {
            return roundNumber >= MIN_ROUND_LIMIT * .8;
        }

        @Override
        protected boolean getLowInfectionChancePrescription() {
            return roundNumber >= MIN_ROUND_LIMIT * .6;
        }

        @Override
        protected boolean getIndeterminateChancePrescription() {
            return roundNumber >= MIN_ROUND_LIMIT * .4;
        }

        @Override
        protected boolean getHighInfectionChancePrescription() {
            return roundNumber >= MIN_ROUND_LIMIT * .2;
        }
    }

    private static class OverprescribeDefenseStrategy extends PrescriptionStrategy {

        @Override
        protected boolean getNoInfectionChancePrescription() {
            return roundNumber >= MIN_ROUND_LIMIT * .2;
        }

        @Override
        protected boolean getLowInfectionChancePrescription() {
            return roundNumber >= MIN_ROUND_LIMIT * .2;
        }

        @Override
        protected boolean getIndeterminateChancePrescription() {
            return roundNumber >= MIN_ROUND_LIMIT * .2;
        }

        @Override
        protected boolean getHighInfectionChancePrescription() {
            return roundNumber >= MIN_ROUND_LIMIT * .2;
        }
    }
}
