package com.nerdery.jvm.resistance.tournament;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class Tournament {
    private List<Entrant> entrants;
    private List<TownGeneration> generations;
    private int currentGenerationIndex;

    public Tournament(List<Entrant> entrants, List<TownGeneration> generations) {
        this.entrants = entrants;
        this.generations = generations;
        this.currentGenerationIndex = 0;
    }

    public List<Entrant> listScoredEntrants() {
        return entrants
                .stream()
                .sorted((entrant1, entrant2) -> ((Integer)entrant1.getScore()).compareTo(entrant2.getScore()))
                .collect(Collectors.toList());
    }

    public TownGeneration getCurrentGeneration() {
        return generations.get(currentGenerationIndex);
    }

}
