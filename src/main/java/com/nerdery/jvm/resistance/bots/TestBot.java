package com.nerdery.jvm.resistance.bots;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public abstract class TestBot implements DoctorBot {
    @Override
    public String getUserId() {
        return "test";
    }
}
