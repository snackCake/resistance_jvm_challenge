package com.nerdery.jvm.resistance.test

import com.nerdery.jvm.resistance.bots.test.BadBot
import com.nerdery.jvm.resistance.models.tournament.Entrant
import com.nerdery.jvm.resistance.models.tournament.TownDay
import spock.lang.Specification

/**
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
class TownDayBuilderSpec extends Specification {

    def "Built Town Day will have the correct number of patients and doctors"() {
        given:
        def builder = TownDay.builder()
        def entrants = [new Entrant(new BadBot()), new Entrant(new BadBot()), new Entrant(new BadBot())]

        when: "There are #entrants.size() entrants for the day"
        def day = builder.dayNumber(0).entrants(entrants).build()

        then: "There should also be #entrants.size() patients in the built day."
        println(day)
        day.patients.size() == entrants.size()
    }
}
