package com.nerdery.jvm.resistance.bots;

import com.nerdery.jvm.resistance.models.Prescription;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * Jon Huff's bot for the JVM Resistance challenge.
 */
public class DoctorHuffBot implements DoctorBot {
    private DocHistory docHistoryA = new DocHistory();
    private DocHistory docHistoryB = new DocHistory();
    private DocHistory docHistoryC = new DocHistory();

    public DoctorHuffBot() {    }

    @Override
    public String getUserId() {
        return "johuff";
    }

    /**
     * @param patientTemperature    The fahrenheit temperature of the patient
     * @param previousPrescriptions An optional collection of prescriptions from the previous round. None if this is the first round.
     * @return true if the patient should take antibiotics, false if they should not.
     */
    @Override
    public boolean prescribeAntibiotic(float patientTemperature, Optional<Collection<Prescription>> previousPrescriptions) {
        if (previousPrescriptions.isPresent()) {
            addPrescriptionsToDocHistories(previousPrescriptions.get());
        }

        float docAForecast = docHistoryA.getAntibioticPercentage();
        float docBForecast = docHistoryB.getAntibioticPercentage();
        float docCForecast = docHistoryC.getAntibioticPercentage();

        System.out.println("[" + docHistoryA.getUserId() +": "+docAForecast+"][" + docHistoryB.getUserId() +": "+docBForecast+"][" + docHistoryC.getUserId() +": "+docCForecast+"]");

        // ---[ 0% chance ]-----------------------------------------------------------------------------------------------------------------
        if (patientTemperature < 100.0) {
            // 0% chance
            return false;
        }
        // ---[ 25% chance ]----------------------------------------------------------------------------------------------------------------
        else if (patientTemperature < 101.0) {
            // Go ahead and return false.  Keeping separate from 0% in case I want to change it later.
            return false;
        }
        // ---[ 50% chance ]----------------------------------------------------------------------------------------------------------------
        // If there's a 50% chance of bacterial infection, and a greater than 90% chance that the other doctors will prescribe
        // antibiotics, prescribe rest.  Hopefully this will prevent a zombie breakout.
        else if (patientTemperature < 102.0) {
            if (docAForecast >= .90 &&
                    docBForecast >= .90 &&
                    docCForecast >= .90) {
                return false;
            } else {
                return true;
            }

        }
        // ---[ 75% chance ]----------------------------------------------------------------------------------------------------------------
        // If there's a 75% chance of bacterial infection, and a greater than 50% chance that the other doctors will prescribe
        // antibiotics, prescribe rest.  Hopefully this will prevent a zombie breakout.
        else if (patientTemperature < 103.0) {
            if (docAForecast >= .50 &&
                    docBForecast >= .50 &&
                    docCForecast >= .50) {
                return false;
            } else {
                return true;
            }
        }
        // ---[ 100% chance ]---------------------------------------------------------------------------------------------------------------
        else {
            return true;
        }
    }

    private void addPrescriptionsToDocHistories(Collection<Prescription> previousPrescriptions) {
        if (!previousPrescriptions.isEmpty() && docHistoryA.getUserId() == null ) {

            System.out.println("Initializing histories...");
            docHistoryA.setUserId(((Prescription)previousPrescriptions.toArray()[1]).getUserId());
            docHistoryB.setUserId(((Prescription)previousPrescriptions.toArray()[2]).getUserId());
            docHistoryC.setUserId(((Prescription)previousPrescriptions.toArray()[3]).getUserId());

            System.out.println("A: " + docHistoryA.getUserId());
            System.out.println("B: " + docHistoryB.getUserId());
            System.out.println("C: " + docHistoryC.getUserId());
        }

        for (Prescription script : previousPrescriptions) {

            // I'm only interested in the ambiguous cases, so assume that every doctor is going to prescribe Rest for temps < 100° and
            // Antibiotics for temps >= 103°.
            if(script.getTemperature() < 100.0 || script.getTemperature() >= 103.0) {
                return;
            }

            String userId = script.getUserId();
            DocHistory docHistory = null;

            if (userId.equalsIgnoreCase(docHistoryA.getUserId())) {
                docHistory = docHistoryA;
            } else if (userId.equalsIgnoreCase(docHistoryB.getUserId())) {
                docHistory = docHistoryB;
            } else if (userId.equalsIgnoreCase(docHistoryC.getUserId())) {
                docHistory = docHistoryC;
            }

            if (docHistory != null) {
                docHistory.addPrescription(script);
            }
        }
    }

    private class DocHistory {
        private String userId;
        private Collection<Prescription> previousPrescriptions = new ArrayList<Prescription>();
        private int antibioticCount = 0;
        private int restCount = 0;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String theUserId) {
            userId = theUserId;
        }

        public void addPrescription(Prescription script) {
            previousPrescriptions.add(script);
            if (script.isPrescribedAntibiotics()) {
                antibioticCount++;
            } else {
                restCount++;
            }
        }

        public float getAntibioticPercentage() {
            return getPercentage(antibioticCount, previousPrescriptions.size());
        }
        public float getRestPercentage() {
            return getPercentage(restCount, previousPrescriptions.size());
        }

        private float getPercentage(int x, int total) {
            if (previousPrescriptions.size() == 0) {
                return 0.0f;
            } else {
                return (float) (x / total);
            }
        }
    }
}
