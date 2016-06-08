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
    private boolean triggeredExtinction;

    public EntrantGenerationResult(int score,
                                   int malpracticeSuits,
                                   int viralCures,
                                   int bacterialCures,
                                   int antibioticsPrescribed,
                                   int patientsTreated,
                                   boolean triggeredExtinction) {
        this.score = score;
        this.malpracticeSuits = malpracticeSuits;
        this.viralCures = viralCures;
        this.bacterialCures = bacterialCures;
        this.antibioticsPrescribed = antibioticsPrescribed;
        this.patientsTreated = patientsTreated;
        this.triggeredExtinction = triggeredExtinction;
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

    public boolean isTriggeredExtinction() {
        return triggeredExtinction;
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
                triggeredExtinction == that.triggeredExtinction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(score, malpracticeSuits, viralCures, bacterialCures,
                antibioticsPrescribed, patientsTreated, triggeredExtinction);
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
                ", triggeredExtinction=" + triggeredExtinction +
                '}';
    }
}
