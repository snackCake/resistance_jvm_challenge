package com.nerdery.jvm.resistance.test

import com.nerdery.jvm.resistance.TournamentRunner
import com.nerdery.jvm.resistance.models.tournament.Tournament
import spock.lang.Specification

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
class TournamentBuilderSpec extends Specification {

    def "Built tournament will have the correct number of generations"() {
        given:
        def builder = Tournament.builder()
        def correctEntrantCount = 7

        when: "The entrant count is at the assumed #correctEntrantCount"
        def tournament = builder.entrantPackage(TournamentRunner.TEST_ENTRANT_PACKAGE).build()
        def correctGenerationCount = 35

        then: "There will be #correctGenerationCount generations, to allow for all combinations"
        println(tournament)
        tournament.listScoredEntrants().size() == correctEntrantCount
        tournament.generations.size() == correctGenerationCount
    }
}
