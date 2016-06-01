package com.nerdery.jvm.resistance.tournament;

import java.util.List;
import java.util.Objects;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class TownGeneration {
    private List<Entrant> generationEntrants;
    private List<TownDay> days;

    public TownGeneration(List<Entrant> generationEntrants, List<TownDay> days) {
        this.generationEntrants = generationEntrants;
        this.days = days;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TownGeneration that = (TownGeneration) o;
        return Objects.equals(generationEntrants, that.generationEntrants) &&
                Objects.equals(days, that.days);
    }

    @Override
    public int hashCode() {
        return Objects.hash(generationEntrants, days);
    }
}
