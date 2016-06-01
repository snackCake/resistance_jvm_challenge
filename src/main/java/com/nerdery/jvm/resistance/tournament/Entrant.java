package com.nerdery.jvm.resistance.tournament;

import com.nerdery.jvm.resistance.bots.DoctorBot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class Entrant {
    private DoctorBot doctorBot;
    private List<EntrantGenerationResult> results;

    public Entrant(DoctorBot doctorBot) {
        this.doctorBot = doctorBot;
        this.results = new ArrayList<>();
   }

    public DoctorBot getDoctorBot() {
        return doctorBot;
    }

    public int getScore() {
        return results.stream().mapToInt(EntrantGenerationResult::getScore).sum();
    }

    public int getExtinctionsCaused() {
        return (int) results.stream().filter(EntrantGenerationResult::isTriggeredExctinction).count();
    }

    public void addGenerationResult(EntrantGenerationResult result) {
        this.results.add(result);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entrant entrant = (Entrant) o;
        return Objects.equals(doctorBot, entrant.doctorBot) &&
                Objects.equals(results, entrant.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorBot, results);
    }
}

