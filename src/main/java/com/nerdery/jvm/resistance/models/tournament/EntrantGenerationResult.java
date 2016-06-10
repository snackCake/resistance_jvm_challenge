package com.nerdery.jvm.resistance.models.tournament;

import java.util.Objects;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class EntrantGenerationResult {
    private int score;
    private int malpracticeSuits;
    private int viralCures;
    private int bacterialCures;
    private int antibioticsPrescribed;
    private int patientsTreated;
    private boolean zombieApocalypseTriggered;

    public EntrantGenerationResult() {
    }

    private EntrantGenerationResult(int score,
                                    int malpracticeSuits,
                                    int viralCures,
                                    int bacterialCures,
                                    int antibioticsPrescribed,
                                    int patientsTreated,
                                    boolean zombieApocalypseTriggered) {
        this.score = score;
        this.malpracticeSuits = malpracticeSuits;
        this.viralCures = viralCures;
        this.bacterialCures = bacterialCures;
        this.antibioticsPrescribed = antibioticsPrescribed;
        this.patientsTreated = patientsTreated;
        this.zombieApocalypseTriggered = zombieApocalypseTriggered;
    }

    public int getScore() {
        return score;
    }

    public int getMalpracticeSuits() {
        return malpracticeSuits;
    }

    public int getViralCures() {
        return viralCures;
    }

    public int getBacterialCures() {
        return bacterialCures;
    }

    public int getAntibioticsPrescribed() {
        return antibioticsPrescribed;
    }

    public int getPatientsTreated() {
        return patientsTreated;
    }

    public boolean isZombieApocalypseTriggered() {
        return zombieApocalypseTriggered;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntrantGenerationResult that = (EntrantGenerationResult) o;
        return score == that.score &&
                malpracticeSuits == that.malpracticeSuits &&
                viralCures == that.viralCures &&
                bacterialCures == that.bacterialCures &&
                antibioticsPrescribed == that.antibioticsPrescribed &&
                patientsTreated == that.patientsTreated &&
                zombieApocalypseTriggered == that.zombieApocalypseTriggered;
    }

    @Override
    public int hashCode() {
        return Objects.hash(score, malpracticeSuits, viralCures, bacterialCures,
                antibioticsPrescribed, patientsTreated, zombieApocalypseTriggered);
    }

    @Override
    public String toString() {
        return "EntrantGenerationResult{" +
                "score=" + score +
                ", malpracticeSuits=" + malpracticeSuits +
                ", viralCures=" + viralCures +
                ", bacterialCures=" + bacterialCures +
                ", antibioticsPrescribed=" + antibioticsPrescribed +
                ", patientsTreated=" + patientsTreated +
                ", zombieApocalypseTriggered=" + zombieApocalypseTriggered +
                '}';
    }

    public EntrantGenerationResult addScore(int newPoints) {
        this.score += newPoints;
        return this;
    }

    public EntrantGenerationResult addMalpracticeSuits(int newMalpracticeSuits) {
        this.malpracticeSuits += newMalpracticeSuits;
        return this;
    }

    public EntrantGenerationResult addViralCures(int newViralCures) {
        this.viralCures += newViralCures;
        return this;
    }

    public EntrantGenerationResult addBacterialCures(int newBacterialCures) {
        this.bacterialCures += newBacterialCures;
        return this;
    }

    public EntrantGenerationResult addAntibioticsPrescribed(int newAntibioticsPrescribed) {
        this.antibioticsPrescribed += newAntibioticsPrescribed;
        return this;
    }

    public EntrantGenerationResult addPatientsTreated(int newPatientsTreated) {
        this.patientsTreated += newPatientsTreated;
        return this;
    }

    public EntrantGenerationResult zombieApocalypseTriggered(boolean zombieApocalypseTriggered) {
        this.zombieApocalypseTriggered = zombieApocalypseTriggered;
        return this;
    }
}
