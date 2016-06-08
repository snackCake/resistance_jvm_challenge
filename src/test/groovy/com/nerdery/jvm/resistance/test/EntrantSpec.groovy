package com.nerdery.jvm.resistance.test

import com.nerdery.jvm.resistance.tournament.Entrant
import spock.lang.Specification

/**
 * Test spec for Entrants and the logic for finding them.
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
class EntrantSpec extends Specification {
    def "Doctor Spaceman is real"() {
        given:
        def finder = Entrant.finder()
        def searchPackage = "com.nerdery.jvm.resistance.bots.entrants"

        when: "entrants searched in #searchPackage"
        def entrants = finder.searchPackage(searchPackage).find()

        then: "Doctor Spaceman should be in the list"
        println(entrants)
        entrants.find { entrant ->
            entrant.doctorBot.class.name == "com.nerdery.jvm.resistance.bots.entrants.jklun.DoctorSpacemanBot"
        } != null
    }

    def "Test Bot isn't found since it is abstract"() {
        given:
        def finder = Entrant.finder()
        def searchPackage = "com.nerdery.jvm.resistance.bots.test"

        when: "entrants searched in #searchPackage"
        def entrants = finder.searchPackage(searchPackage).find()

        then: "Test Bot should notbe in the list"
        println(entrants)
        entrants.find { entrant ->
            entrant.doctorBot.class.name == "com.nerdery.jvm.resistance.bots.test.TestBot"
        } == null
    }
}
