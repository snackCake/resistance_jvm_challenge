package com.nerdery.jvm.resistance.bots.entrants.hopper;

import com.nerdery.jvm.resistance.models.Prescription;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hopper on 6/6/16.
 */
public class HopperBayesianBotTest {

    private HopperBayesianBot bot = new HopperBayesianBot(false);
    private List<Prescription> emptyPrescriptions = Collections.emptyList();
    private List<Prescription> evilPrescriptions = Arrays.asList(
            new Prescription("a", true, 100.0f),
            new Prescription("a", true, 90.0f),
            new Prescription("b", true, 88.0f),
            new Prescription("b", true, 89.0f),
            new Prescription("c", true, 95.0f),
            new Prescription("c", true, 98.0f)
    );

    private List<Prescription> mostlyEvilLargeScalePrescriptions = Arrays.asList(
            new Prescription("a", true, 100.0f),
            new Prescription("a", true, 90.0f),
            new Prescription("a", true, 100.0f),
            new Prescription("a", true, 90.0f),
            new Prescription("a", true, 100.0f),
            new Prescription("a", true, 90.0f),
            new Prescription("a", true, 100.0f),
            new Prescription("a", true, 90.0f),
            new Prescription("a", true, 100.0f),
            new Prescription("a", true, 90.0f),
            new Prescription("b", true, 88.0f),
            new Prescription("b", true, 89.0f),
            new Prescription("b", true, 88.0f),
            new Prescription("b", true, 89.0f),
            new Prescription("b", true, 88.0f),
            new Prescription("b", true, 89.0f),
            new Prescription("b", true, 88.0f),
            new Prescription("b", true, 89.0f),
            new Prescription("b", true, 88.0f),
            new Prescription("b", true, 89.0f),
            new Prescription("c", true, 95.0f),
            new Prescription("c", true, 98.0f),
            new Prescription("c", false, 95.0f),
            new Prescription("c", false, 98.0f),
            new Prescription("c", false, 95.0f),
            new Prescription("c", false, 98.0f),
            new Prescription("c", false, 95.0f),
            new Prescription("c", false, 98.0f),
            new Prescription("c", false, 95.0f),
            new Prescription("c", false, 98.0f)
    );

    private static final float P0 = 90.0f;
    private static final float P25 = 100.0f;
    private static final float P50 = 101.0f;
    private static final float P75 = 102.0f;
    private static final float P100 = 103.0f;

    @Test
    public void testUserId() {
        assertEquals("hopper", bot.getUserId());
    }

    @Test
    public void test_00_RangeTemp() {
        assertFalse(bot.prescribeAntibiotic(P0, emptyPrescriptions));
    }

    @Test
    public void test_25_RangeTemp() {
        assertFalse(bot.prescribeAntibiotic(P25, emptyPrescriptions));
    }

    @Test
    public void test_50_RangeTemp() {
        assertFalse(bot.prescribeAntibiotic(P50, emptyPrescriptions));
    }

    @Test
    public void test_75_RangeTemp() {
        assertFalse(bot.prescribeAntibiotic(P75, emptyPrescriptions));
    }

    @Test
    public void test_100_RangeTemp() {
        assertTrue(bot.prescribeAntibiotic(P100, emptyPrescriptions));
    }

    @Test
    public void test_0_RangeTemp_EverybodyEvil() {
        assertFalse(bot.prescribeAntibiotic(P0, evilPrescriptions));
    }

    @Test
    public void test_25_RangeTemp_EverybodyEvil() {
        assertFalse(bot.prescribeAntibiotic(P25, evilPrescriptions));
    }

    @Test
    public void test_50_RangeTemp_EverybodyEvil() {
        assertFalse(bot.prescribeAntibiotic(P50, evilPrescriptions));
    }

    @Test
    public void test_75_RangeTemp_EverybodyEvil() {
        assertFalse(bot.prescribeAntibiotic(P75, evilPrescriptions));
    }

    @Test
    public void test_100_RangeTemp_EverybodyEvil() {
        assertTrue(bot.prescribeAntibiotic(P100, evilPrescriptions));
    }

    @Test
    public void test_0_RangeTemp_EverybodyMostlyEvil() {
        assertFalse(bot.prescribeAntibiotic(P0, mostlyEvilLargeScalePrescriptions));
    }

    @Test
    public void test_25_RangeTemp_EverybodyMostlyEvil() {
        assertFalse(bot.prescribeAntibiotic(P25, mostlyEvilLargeScalePrescriptions));
    }

    @Test
    public void test_50_RangeTemp_EverybodyMostlyEvil() {
        assertFalse(bot.prescribeAntibiotic(P50, mostlyEvilLargeScalePrescriptions));
    }

    @Test
    public void test_75_RangeTemp_EverybodyMostlyEvil() {
        assertTrue(bot.prescribeAntibiotic(P75, mostlyEvilLargeScalePrescriptions));
    }

    @Test
    public void test_100_RangeTemp_EverybodyMostlyEvil() {
        assertTrue(bot.prescribeAntibiotic(P100, mostlyEvilLargeScalePrescriptions));
    }
}
