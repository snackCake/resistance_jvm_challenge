package com.nerdery.jvm.resistance.test

import com.nerdery.jvm.resistance.TournamentRunner
import com.nerdery.jvm.resistance.models.tournament.Entrant
import com.nerdery.jvm.resistance.models.tournament.EntrantGenerationResult
import spock.lang.Specification

/**
 * Test spec for Entrants and the logic for finding them.
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
class EntrantSpec extends Specification {
    def "Doctor Nick says Hi Everybody!"() {
        given:
        def finder = Entrant.finder()
        def searchPackage = TournamentRunner.DEFAULT_ENTRANT_PACKAGE

        when: "entrants searched in #searchPackage"
        def entrants = finder.searchPackage(searchPackage).find()

        then: "Doctor Nick should be in the list"
        println(entrants)
        entrants.find { entrant ->
            entrant.doctorBot.class.name == TournamentRunner.DEFAULT_ENTRANT_PACKAGE + ".jklun.DoctorNickBot"
        } != null
    }

    def "Test Bot isn't found since it is abstract"() {
        given:
        def finder = Entrant.finder()
        def searchPackage = TournamentRunner.TEST_ENTRANT_PACKAGE

        when: "entrants searched in #searchPackage"
        def entrants = finder.searchPackage(searchPackage).find()

        then: "Test Bot should not be in the list"
        println(entrants)
        entrants.find { entrant ->
            entrant.doctorBot.class.name == TournamentRunner.TEST_ENTRANT_PACKAGE + ".TestBot"
        } == null
    }

    def "Resistance Bot should always prescribe antibiotics"() {
        given:
        def finder = Entrant.finder()
        def searchPackage = TournamentRunner.TEST_ENTRANT_PACKAGE

        when: "entrants searched in #searchPackage"
        def entrants = finder.searchPackage(searchPackage).find()

        then: "Resistance bot should return true (prescribe antibiotics)"
        println(entrants)
        entrants.find { entrant ->
            entrant.doctorBot.class.name == TournamentRunner.TEST_ENTRANT_PACKAGE + ".ResistanceBot"
        }.doctorBot.prescribeAntibiotic(2.0f, Collections.emptyList())
    }

    def "Malpractice Bot should never prescribe antibiotics"() {
        given:
        def finder = Entrant.finder()
        def searchPackage = TournamentRunner.TEST_ENTRANT_PACKAGE

        when: "entrants searched in #searchPackage"
        def entrants = finder.searchPackage(searchPackage).find()

        then: "Malpractice bot should return false (prescribe rest)"
        println(entrants)
        !entrants.find { entrant ->
            entrant.doctorBot.class.name == TournamentRunner.TEST_ENTRANT_PACKAGE + ".MalpracticeBot"
        }.doctorBot.prescribeAntibiotic(200.0f, Collections.emptyList())
    }

    def "An entrant should compute its score properly"() {
        given:
        def finder = Entrant.finder()
        def searchPackage = TournamentRunner.TEST_ENTRANT_PACKAGE

        when: "using MalpracticeBot, but it shouldn't matter"
        def entrant = finder.searchPackage(searchPackage)
                .find()
                .find { entrant ->
                    entrant.doctorBot.class.name == TournamentRunner.TEST_ENTRANT_PACKAGE + ".MalpracticeBot"
                }
        // Note: generation numbers aren't consistent
        entrant.addGenerationResult(new EntrantGenerationResult(5, 5, 5, 5, 5, 5, false))
        entrant.addGenerationResult(new EntrantGenerationResult(50, 5, 5, 5, 5, 5, false))
        entrant.addGenerationResult(new EntrantGenerationResult(-30, 5, 5, 5, 5, 5, false))
        entrant.addGenerationResult(new EntrantGenerationResult(0, 5, 5, 5, 5, 5, false))
        entrant.addGenerationResult(new EntrantGenerationResult(-2, 5, 5, 5, 5, 5, false))
        entrant.addGenerationResult(new EntrantGenerationResult(17, 5, 5, 5, 5, 5, false))
        def correctScore = 40;

        then: "the score should be #correctScore"
        println(entrant)
        entrant.score == correctScore
    }

    def "An entrant should know how many extinctions it caused"() {
        given:
        def finder = Entrant.finder()
        def searchPackage = TournamentRunner.TEST_ENTRANT_PACKAGE

        when: "using MalpracticeBot, but it shouldn't matter"
        def entrant = finder.searchPackage(searchPackage)
                .find()
                .find { entrant ->
                    entrant.doctorBot.class.name == TournamentRunner.TEST_ENTRANT_PACKAGE + ".ResistanceBot"
                }
        // Note: generation numbers aren't consistent
        entrant.addGenerationResult(new EntrantGenerationResult(5, 5, 5, 5, 5, 5, false))
        entrant.addGenerationResult(new EntrantGenerationResult(50, 5, 5, 5, 5, 5, true))
        entrant.addGenerationResult(new EntrantGenerationResult(-30, 5, 5, 5, 5, 5, true))
        entrant.addGenerationResult(new EntrantGenerationResult(0, 5, 5, 5, 5, 5, true))
        entrant.addGenerationResult(new EntrantGenerationResult(-2, 5, 5, 5, 5, 5, false))
        entrant.addGenerationResult(new EntrantGenerationResult(17, 5, 5, 5, 5, 5, true))
        def correctExctinctions = 4;

        then: "the extinctions should be #correctExctinctions"
        println(entrant)
        entrant.extinctionsCaused == correctExctinctions
    }

}
