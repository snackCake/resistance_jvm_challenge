package com.nerdery.jvm.resistance.bots.entrants.hopper;

import com.nerdery.jvm.resistance.models.Prescription;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hopper on 6/6/16.
 */
public class HopperBayesianBotTest {

    private HopperBayesianBot bot = new HopperBayesianBot();
    private List<Prescription> emptyPrescriptions = Collections.emptyList();

    @Test
    public void testUserId() {
        assertEquals("hopper", bot.getUserId());
    }

    @Test
    public void test_00_RangeTemp() {
        assertFalse(bot.prescribeAntibiotic(90.0f, emptyPrescriptions));
    }

    @Test
    public void test_25_RangeTemp() {
        assertFalse(bot.prescribeAntibiotic(100.0f, emptyPrescriptions));
    }

    @Test
    public void test_50_RangeTemp() {
        assertFalse(bot.prescribeAntibiotic(101.0f, emptyPrescriptions));
    }

    @Test
    public void test_75_RangeTemp() {
        assertTrue(bot.prescribeAntibiotic(102.0f, emptyPrescriptions));
    }

    @Test
    public void test_100_RangeTemp() {
        assertTrue(bot.prescribeAntibiotic(103.0f, emptyPrescriptions));
    }
}
