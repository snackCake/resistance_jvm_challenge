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
    private int generationNumber;

    private TownGeneration(List<Entrant> entrants, List<TownDay> days, int generationNumber) {
        this.entrants = entrants;
        this.days = days;
        this.generationNumber = generationNumber;
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

    public int getGenerationNumber() {
        return generationNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TownGeneration that = (TownGeneration) o;
        return Objects.equals(entrants, that.entrants) &&
                Objects.equals(days, that.days) &&
                generationNumber == that.generationNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(entrants, days, generationNumber);
    }

    @Override
    public String toString() {
        return "TownGeneration{" +
                "entrants=" + entrants +
                ", days=" + days +
                ", generationNumber=" + generationNumber +
                '}';
    }

    public static class TownGenerationBuilder {

        private List<Entrant> entrants;
        private int maximumDays;
        private int minimumDays;
        private int generationNumber;

        private TownGenerationBuilder() {
            entrants = Collections.emptyList();
            minimumDays = 1;
            maximumDays = 1;
            generationNumber = 0;
        }

        public TownGenerationBuilder entrants(List<Entrant> entrants) {
            this.entrants = entrants;
            return this;
        }

        public TownGenerationBuilder maximumDays(int maximumPatients) {
            this.maximumDays = maximumPatients;
            return this;
        }

        public TownGenerationBuilder minimumDays(int minimumPatients) {
            this.minimumDays = minimumPatients;
            return this;
        }

        public TownGeneration build() {
            List<TownDay> days = buildDays(entrants, minimumDays, maximumDays);
            return new TownGeneration(entrants, days, generationNumber++);
        }

        private List<TownDay> buildDays(List<Entrant> entrants, int minimumPatients, int maximumPatients) {
            int daySpread = maximumPatients - minimumPatients;
            SecureRandom random = new SecureRandom();
            int patientCount = minimumPatients + (Math.abs(random.nextInt()) % daySpread);
            TownDay.TownDayBuilder townDayBuilder = TownDay.builder();
            townDayBuilder.entrants(entrants);
            return IntStream.range(0, patientCount)
                    .mapToObj(i -> townDayBuilder.dayNumber(i).build())
                    .collect(Collectors.toList());
        }
    }
}
