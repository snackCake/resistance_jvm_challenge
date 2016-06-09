package com.nerdery.jvm.resistance.test

import com.nerdery.jvm.resistance.bots.test.BadBot
import com.nerdery.jvm.resistance.models.tournament.Entrant
import com.nerdery.jvm.resistance.models.tournament.TownGeneration
import spock.lang.Specification

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
class TownGenerationBuilderSpec extends Specification {

    def "Built Town Generation will have a valid number of days"() {
        given:
        def builder = TownGeneration.builder()
        def entrants = [new Entrant(new BadBot()), new Entrant(new BadBot()), new Entrant(new BadBot())]
        def minDays = 108
        def maxDays = 228

        when: "The minimum number of patients is between #minDays and #maxPatients"
        def generation = builder.entrants(entrants)
                .minimumDays(minDays)
                .maximumDays(maxDays)
                .build()

        then: "The number of days should be in the valid range (#minDays - #maxPatients)"
        println(generation)
        println("Day count: ${generation.days.size()}")
        generation.days.size() >= minDays && generation.days.size() <= maxDays
    }
}
