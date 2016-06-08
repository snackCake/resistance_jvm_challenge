package com.nerdery.jvm.resistance.models;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public enum Outcome {

    VIRAL_REST(1, false),
    LUCKY_BACTERIAL_REST(1, false),
    UNLUCKY_BACTERIAL_REST(-10, false),
    BACTERIAL_ANTIBIOTICS(3, false),
    LUCKY_VIRAL_ANTIBIOTICS(3, false),
    UNLUCKY_VIRAL_ANTIBIOTICS(0, true);


    private int insurancePayment;
    private boolean zombieApocalypse;

    Outcome(int insurancePayment, boolean zombieApocalypse) {
        this.insurancePayment = insurancePayment;
        this.zombieApocalypse = zombieApocalypse;
    }

    public int getInsurancePayment() {
        return insurancePayment;
    }

    public boolean isZombieApocalypse() {
        return zombieApocalypse;
    }
}
