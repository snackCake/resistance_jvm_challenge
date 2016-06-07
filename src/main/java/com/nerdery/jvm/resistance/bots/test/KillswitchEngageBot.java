package com.nerdery.jvm.resistance.bots.test;

import com.nerdery.jvm.resistance.bots.DoctorBot;
import com.nerdery.jvm.resistance.models.Prescription;

import java.util.Collection;

/**
 * A bot which never prescribes anything but rest until it finds malpractice at which point
 * its HP and attack power double and it enters rage mode.
 *
 * Pros: Punishes malpractice with a vengeance
 * Cons: Ironically, a really terrible doctor.
 *
 * Part 1: https://youtu.be/N2jqoUABDGI?t=5m23s
 * Part 2: https://www.youtube.com/watch?v=5keNxXQxQ5w
 *
 * @author Stephen Hopper
 */
public class KillswitchEngageBot implements DoctorBot {

    //start the bot in a peaceful state
    private boolean killswitchEngaged = false;

    @Override
    public String getUserId() {
        return "switchy";
    }

    @Override
    public boolean prescribeAntibiotic(float patientTemperature, Collection<Prescription> previousPrescriptions) {
        if (!killswitchEngaged) {
            //enter rage mode if the chance of infection was 25% or less
            //but the doctor prescribed antibiotics anyways
            if (previousPrescriptions.
                    stream().
                    anyMatch(x -> x.getTemperature() < 101.0f)) {
                System.out.println("Killswitch has been engaged!");
                killswitchEngaged = true;
            }
        }

        return killswitchEngaged;
    }
}
