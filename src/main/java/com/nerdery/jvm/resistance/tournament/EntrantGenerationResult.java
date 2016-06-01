package com.nerdery.jvm.resistance.tournament;

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
    private boolean triggeredExctinction;

    public EntrantGenerationResult(int score,
                                   int malpracticeSuits,
                                   int viralCures,
                                   int bacterialCures,
                                   int antibioticsPrescribed,
                                   int patientsTreated,
                                   boolean triggeredExctinction) {
        this.score = score;
        this.malpracticeSuits = malpracticeSuits;
        this.viralCures = viralCures;
        this.bacterialCures = bacterialCures;
        this.antibioticsPrescribed = antibioticsPrescribed;
        this.patientsTreated = patientsTreated;
        this.triggeredExctinction = triggeredExctinction;
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

    public boolean isTriggeredExctinction() {
        return triggeredExctinction;
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
                triggeredExctinction == that.triggeredExctinction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(score, malpracticeSuits, viralCures, bacterialCures,
                antibioticsPrescribed, patientsTreated, triggeredExctinction);
    }
}
