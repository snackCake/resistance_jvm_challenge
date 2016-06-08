package com.nerdery.jvm.resistance.bots.test;

import com.nerdery.jvm.resistance.models.Prescription;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Random;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class RandoBot extends TestBot {

    private Random random;

    public RandoBot() {
        random = new SecureRandom();
    }

    @Override
    public boolean prescribeAntibiotic(float patientTemperature, Collection<Prescription> previousPrescriptions) {
        return random.nextBoolean();
    }
}
