package com.nerdery.jvm.resistance.models;

import java.util.Objects;

/**
 * A simple description of the prescription for a patient in the previous round.
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
public class Prescription {

    private String userId;
    private boolean prescribedAntibiotics;
    private float temperature;
    private long contemplationTime;

    public Prescription(String userId, boolean prescribedAntibiotics, float temperature, long contemplationTime) {
        this.userId = userId;
        this.prescribedAntibiotics = prescribedAntibiotics;
        this.temperature = temperature;
        this.contemplationTime = contemplationTime;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isPrescribedAntibiotics() {
        return prescribedAntibiotics;
    }

    public float getTemperature() {
        return temperature;
    }

    public long getContemplationTime() {
        return contemplationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prescription that = (Prescription) o;
        return prescribedAntibiotics == that.prescribedAntibiotics &&
                contemplationTime == that.contemplationTime &&
                Float.compare(that.temperature, temperature) == 0 &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, prescribedAntibiotics, temperature, contemplationTime);
    }

    @Override
    public String toString() {
        return "Prescription{" +
                "userId='" + userId + '\'' +
                ", prescribedAntibiotics=" + prescribedAntibiotics +
                ", temperature=" + temperature +
                ", contemplationTime=" + contemplationTime +
                '}';
    }
}
