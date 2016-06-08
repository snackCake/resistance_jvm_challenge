package com.nerdery.jvm.resistance.models.tournament;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class TownGeneration {

    public static final int DOCTORS_PER_TOWN = 4;

    private List<Entrant> entrants;
    private List<TownDay> days;

    private TownGeneration(List<Entrant> entrants, List<TownDay> days) {
        this.entrants = entrants;
        this.days = days;
    }

    public static TownGenerationBuilder builder() {
        return new TownGenerationBuilder();
    }

    public List<TownDay> getDays() {
        return days;
    }

    public List<Entrant> getEntrants() {
        return entrants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TownGeneration that = (TownGeneration) o;
        return Objects.equals(entrants, that.entrants) &&
                Objects.equals(days, that.days);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entrants, days);
    }

    @Override
    public String toString() {
        return "TownGeneration{" +
                "entrants=" + entrants +
                ", days=" + days +
                '}';
    }

    public static class TownGenerationBuilder {

        private List<Entrant> entrants;
        private int maximumPatients;
        private int minimumPatients;

        private TownGenerationBuilder() {
            entrants = Collections.emptyList();
            minimumPatients = DOCTORS_PER_TOWN;
            maximumPatients = DOCTORS_PER_TOWN;
        }

        public TownGenerationBuilder entrants(List<Entrant> entrants) {
            this.entrants = entrants;
            return this;
        }

        public TownGenerationBuilder maximumPatients(int maximumPatients) {
            this.maximumPatients = maximumPatients;
            return this;
        }

        public TownGenerationBuilder minimumPatients(int minimumPatients) {
            this.minimumPatients = minimumPatients;
            return this;
        }

        public TownGeneration build() {
            List<TownDay> days = buildDays(entrants, minimumPatients, maximumPatients);
            return new TownGeneration(entrants, days);
        }

        private List<TownDay> buildDays(List<Entrant> entrants, int minimumPatients, int maximumPatients) {
            int patientSpread = maximumPatients - minimumPatients;
            SecureRandom random = new SecureRandom();
            int patientCount = minimumPatients + (random.nextInt() % patientSpread);
            patientCount = patientCount + (patientCount % entrants.size());
            TownDay.TownDayBuilder townDayBuilder = TownDay.builder();
            townDayBuilder.entrants(entrants);
            return IntStream.range(0, patientCount)
                    .mapToObj(i -> townDayBuilder.dayNumber(i).build())
                    .collect(Collectors.toList());
        }
    }
}
