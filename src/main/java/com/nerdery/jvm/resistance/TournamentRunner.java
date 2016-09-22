package com.nerdery.jvm.resistance;

import com.nerdery.jvm.resistance.bots.DoctorBot;
import com.nerdery.jvm.resistance.models.tournament.Entrant;
import com.nerdery.jvm.resistance.models.tournament.Tournament;
import com.nerdery.jvm.resistance.services.MicrobialSimulationService;
import com.nerdery.jvm.resistance.services.ResistanceSimulationService;

import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class TournamentRunner {

    public static final String DEFAULT_ENTRANT_PACKAGE = "com.nerdery.jvm.resistance.bots.entrants";
    public static final String TEST_ENTRANT_PACKAGE = "com.nerdery.jvm.resistance.bots.test";
    public static final String ALL_ENTRANT_PACKAGE = "com.nerdery.jvm.resistance.bots";

    public static void main(String[] args) {
        String entrantPackage;
        if (args.length > 0  && "test".equals(args[0])) {
            entrantPackage = TEST_ENTRANT_PACKAGE;
        } else if (args.length > 0 && "all".equals(args[0])) {
            entrantPackage = ALL_ENTRANT_PACKAGE;
        } else {
            entrantPackage = DEFAULT_ENTRANT_PACKAGE;
        }
        Tournament tournament = Tournament.builder()
                .entrantPackage(entrantPackage)
                .build();
        MicrobialSimulationService microbialService = new MicrobialSimulationService();
        ResistanceSimulationService simulationService = new ResistanceSimulationService(microbialService);
        simulationService.runTournament(tournament);
        printReport(tournament);
    }

    private static void printReport(Tournament tournament) {
        List<Entrant> entrants = tournament.listScoredEntrants();
        System.out.println("Resistance Tournament Results");
        IntStream.range(0, entrants.size()).forEach(i -> {
            Entrant entrant = entrants.get(i);
            DoctorBot doctorBot = entrant.getDoctorBot();
            System.out.println((i + 1) + ". " + doctorBot.getUserId() + " - " + doctorBot.getClass().getSimpleName() + ": $" + entrant.getScore());
        });
        tournament.listScoredEntrants().forEach(entrant -> System.out.println(""));
    }
}
