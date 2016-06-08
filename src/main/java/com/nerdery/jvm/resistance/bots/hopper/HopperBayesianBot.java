package com.nerdery.jvm.resistance.bots.hopper;

import com.nerdery.jvm.resistance.bots.DoctorBot;
import com.nerdery.jvm.resistance.models.Prescription;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.Authorization;

import java.io.IOException;
import java.io.InputStream;
import java.text.Format;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Hopper bot. Uses conditional probabilities.
 *
 * Pros: Tries to use probabilities of outcomes when making decisions.
 * Cons: Other doctors are illogical, and sometimes this bot is just unlucky.
 *
 * @author Stephen Hopper
 */
public class HopperBayesianBot implements DoctorBot {

    private static final int MIN_ROUNDS_FOR_DOC_PROB_CALC = 9;
    private static final float DEFAULT_DOC_PROB_CALC = 0.9f;
    private static final int MIN_SIMILAR_PRESCRIPTION_PROB_CALC = 5;
    private static final float WEIGHT_SIMILAR_PRESCRIPTION_PROB = 0.5f;
    private static final float WEIGHT_OVERALL_PRESCRIPTION_PROB = (1 - WEIGHT_SIMILAR_PRESCRIPTION_PROB);

    private static final int MONEY_ANTIBIOTIC_GOOD = 3;
    private static final int MONEY_ANTIBIOTIC_BAD = -100;
    private static final int MONEY_REST_GOOD = 1;
    private static final int MONEY_REST_BAD = -10;

    private static final Twitter twitter;

    static {
        twitter = TwitterFactory.getSingleton();
    }

    private Map<String, List<PrescriptionData>> prescriptionMap = new HashMap<>();

    @Override
    public String getUserId() {
        return "hopper";
    }

    @Override
    public boolean prescribeAntibiotic(float patientTemperature, Collection<Prescription> previousPrescriptions) {
        updatePrescriptions(previousPrescriptions);
        PatientTemp temp = PatientTemp.lookupTemp(patientTemperature);
        float probMoneyIfPrescribeAntibiotic = probMoneyGivenAntibiotic(temp);
        float probMoneyIfPrescribeRest = probMoneyGivenRest(temp);

        System.out.println("Patient temp: " + patientTemperature);
        System.out.println("Probability of money if prescribe antibiotic: " + probMoneyIfPrescribeAntibiotic);
        System.out.println("Probability of money if prescribe rest: " + probMoneyIfPrescribeRest);

//        float prescribeAntibioticMoneyFactor = probMoneyIfPrescribeAntibiotic * (MONEY_ANTIBIOTIC_BAD + MONEY_ANTIBIOTIC_GOOD);
//        float prescribeRestMoneyFactor = probMoneyIfPrescribeRest * (MONEY_REST_BAD + MONEY_REST_GOOD);
//        System.out.println("Prescribe antibiotic money factor: " + prescribeAntibioticMoneyFactor);
//        System.out.println("Prescribe rest money factor: " + prescribeRestMoneyFactor);
//
//        boolean prescribeDrugs = prescribeAntibioticMoneyFactor >= prescribeRestMoneyFactor;
        boolean prescribeDrugs = probMoneyIfPrescribeAntibiotic > probMoneyIfPrescribeRest;
        System.out.println("Prescribe antibiotics?: " + prescribeDrugs);

        StatusUpdate status = buildStatus(patientTemperature, prescribeDrugs, probMoneyIfPrescribeAntibiotic, probMoneyIfPrescribeRest);
        try {
            twitter.updateStatus(status);
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        return prescribeDrugs;
    }

    private StatusUpdate buildStatus(float temp, boolean prescribeDrugs, float chanceMoneyAnti, float chanceMoneyRest) {
        String status;
        String antiMoneyStr = format(chanceMoneyAnti * 100.0f);
        String restMoneyStr = format(chanceMoneyRest * 100.0f);
        if (prescribeDrugs) {
            status = "Prescribed drugs to a patient with a fever of " + format(temp) +
                    "F as there's a " + antiMoneyStr + "% chance I get $$$ from this (vs " + restMoneyStr + "%) #NerderyResistance";
        } else {
            status = "Told some fool to rest up because their fever was " + format(temp) +
                    "F and there's a " + restMoneyStr + "% chance I get $$$ from this (vs " + antiMoneyStr + "%) #NerderyResistance";
        }
        System.out.println(status);

        StatusUpdate update = new StatusUpdate(status);
        update.setPossiblySensitive(false); //hilarious
        return update;
    }

    private String format(float f) {
        return String.format("%.1f", f);
    }

    private void updatePrescriptions(Collection<Prescription> previousPrescriptions) {
        previousPrescriptions.
                stream().
                filter(x -> !"hopper".equals(x.getUserId())).
                forEach(x -> {
                    if (prescriptionMap.containsKey(x.getUserId())) {
                        prescriptionMap.get(x.getUserId()).add(PrescriptionData.fromPrescription(x));
                    } else {
                        List<PrescriptionData> prescriptions = new ArrayList<>();
                        prescriptions.add(PrescriptionData.fromPrescription(x));
                        prescriptionMap.put(x.getUserId(), prescriptions);
                    }
                });
    }

    private float probMoneyGivenAntibiotic(PatientTemp temp) {
        return probInfected(temp) + (1.0f - probInfected(temp)) * probLuckyGivenAntibiotic(temp);
    }

    private float probLuckyGivenAntibiotic(PatientTemp temp) {
        return 1.0f - probUnluckyGivenAntibiotic(temp);
    }

    private float probUnluckyGivenAntibiotic(PatientTemp temp) {
        if (countRounds() <= MIN_ROUNDS_FOR_DOC_PROB_CALC) {
            return DEFAULT_DOC_PROB_CALC;
        } else {
            return prescriptionMap.
                    entrySet().
                    stream().
                    map(entries -> {
                        List<PrescriptionData> prescriptions = entries.getValue();
                        float overallProbPrescribe = probPrescribeAntibiotic(prescriptions);
                        Stream<PrescriptionData> similarPrescriptionData = prescriptions.
                                stream().
                                filter(x -> x.getTemp() == temp);
                        if (similarPrescriptionData.count() >= MIN_SIMILAR_PRESCRIPTION_PROB_CALC) {
                            float similarPrescriptionProb = probPrescribeAntibiotic(similarPrescriptionData.collect(Collectors.toList()));
                            return WEIGHT_SIMILAR_PRESCRIPTION_PROB * similarPrescriptionProb + WEIGHT_OVERALL_PRESCRIPTION_PROB * overallProbPrescribe;
                        } else {
                            return overallProbPrescribe;
                        }
                    }).reduce(1.0f, (x, y) -> x * y);
        }
    }

    private float probPrescribeAntibiotic(List<PrescriptionData> samples) {
        return samples.stream().filter(PrescriptionData::isPrescribedAntibiotics).count() / (float) samples.size();
    }

    private int countRounds() {
        return prescriptionMap.
                entrySet().
                stream().
                map(x -> x.getValue().size()).
                max(Integer::compareTo).
                orElse(0);
    }

    //rest probability methods
    private float probMoneyGivenRest(PatientTemp temp) {
        return probInfected(temp) * probLuckyGivenRest(temp) + (1.0f - probInfected(temp));
    }

    private float probLuckyGivenRest(PatientTemp temp) {
        return 1.0f - probUnluckyGivenRest(temp);
    }

    private float probUnluckyGivenRest(PatientTemp temp) {
        return probInfected(temp) * randy();
    }

    private float randy() {
        return new Random().nextFloat();
    }

    //general probability methods
    private float probInfected(PatientTemp temp) {
        return temp.getProbInfected();
    }
}
