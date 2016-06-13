package com.nerdery.jvm.resistance.models.tournament;

import com.google.common.collect.Lists;
import org.apache.commons.math3.util.Combinations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class Tournament {
    public static final int MAXIMUM_DAYS = 200;
    public static final int MINIMUM_DAYS = 100;
    private List<Entrant> entrants;
    private List<TownGeneration> generations;

    private Tournament(List<Entrant> entrants, List<TownGeneration> generations) {
        this.entrants = entrants;
        this.generations = generations;
    }

    public static TournamentBuilder builder() {
        return new TournamentBuilder();
    }

    @Override
    public boolean equals(Object atheO) {
        if (this == atheO) return true;
        if (atheO == null || getClass() != atheO.getClass()) return false;
        Tournament athat = (Tournament) atheO;
        return Objects.equals(entrants, athat.entrants) &&
                Objects.equals(generations, athat.generations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entrants, generations);
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "entrants=" + entrants +
                ", generations=" + generations +
                ", currentGenerationIndex=" +
                '}';
    }

    public List<Entrant> listScoredEntrants() {
        return entrants
                .stream()
                .sorted((entrant1, entrant2) -> ((Integer) entrant2.getScore()).compareTo(entrant1.getScore()))
                .collect(Collectors.toList());
    }

    public List<TownGeneration> getGenerations() {
        return Collections.unmodifiableList(generations);
    }

    public static class TournamentBuilder {
        private static final Logger logger = LoggerFactory.getLogger(TournamentBuilder.class);

        private String entrantPackage;

        private TournamentBuilder() {
            entrantPackage = "";
        }

        public TournamentBuilder entrantPackage(String entrantPackage) {
            this.entrantPackage = entrantPackage;
            return this;
        }

        public Tournament build() {
            List<Entrant> entrants = Entrant.finder().searchPackage(entrantPackage).find();
            entrants.stream().forEach(entrant -> logger.info("Entrant loaded: {}", entrant));
            List<TownGeneration> generations = buildGenerations(entrants);
            logger.info("Found {} entrants, which means there will be {} generations.", entrants.size(), generations.size());
            return new Tournament(entrants, generations);
        }

        private List<TownGeneration> buildGenerations(List<Entrant> entrants) {
            Combinations combinations = new Combinations(entrants.size(), TownGeneration.DOCTORS_PER_TOWN);
            TownGeneration.TownGenerationBuilder builder = TownGeneration.builder();
            return Lists.newArrayList(combinations)
                    .stream()
                    .map(indexArray ->
                            Arrays.stream(indexArray)
                                    .boxed()
                                    .map(entrants::get).collect(Collectors.toList()))
                    .map(generationEntrants -> builder
                            .minimumDays(MINIMUM_DAYS)
                            .maximumDays(MAXIMUM_DAYS)
                            .entrants(generationEntrants)
                            .build())
                    .collect(Collectors.toList());
        }
    }
}
