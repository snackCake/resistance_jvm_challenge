package com.nerdery.jvm.resistance;

import com.nerdery.jvm.resistance.models.tournament.Tournament;
import com.nerdery.jvm.resistance.services.MicrobialSimulationService;
import com.nerdery.jvm.resistance.services.ResistanceSimulationService;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class TournamentRunner {

    public static final String DEFAULT_ENTRANT_PACKAGE = "com.nerdery.jvm.resistance.bots.entrants";
    public static final String TEST_ENTRANT_PACKAGE = "com.nerdery.jvm.resistance.bots.test";

    public static void main(String[] args) {
        String entrantPackage = args.length > 0  && "test".equals(args[0]) ? TEST_ENTRANT_PACKAGE : DEFAULT_ENTRANT_PACKAGE;
        Tournament tournament = Tournament.builder()
                .entrantPackage(entrantPackage)
                .build();
        MicrobialSimulationService microbialService = new MicrobialSimulationService();
        ResistanceSimulationService simulationService = new ResistanceSimulationService(microbialService);
        simulationService.runTournament(tournament);
    }
}
