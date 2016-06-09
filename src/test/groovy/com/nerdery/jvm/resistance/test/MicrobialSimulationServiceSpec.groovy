package com.nerdery.jvm.resistance.test

import com.nerdery.jvm.resistance.services.MicrobialSimulationService
import spock.lang.Specification

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
class MicrobialSimulationServiceSpec extends Specification {
    def "Temperatures below 100 are always lucky"() {
        given:
        def service = new MicrobialSimulationService()

        when:
        def temp = 99.0f


        then: "#temp is lucky"
        service.hasGoodLuck(temp)
    }

    def "Temperatures above 103 are never lucky"() {
        given:
        def service = new MicrobialSimulationService()

        when:
        def temp = 104.0f

        then: "#temp is unlucky"
        !service.hasGoodLuck(temp)
    }

    def "Temperatures in the valid range might be lucky"() {
        given:
        def service = new MicrobialSimulationService()

        when:
        def temp = 102.5f

        then: "#temp could be lucky"
        def luck = service.hasGoodLuck(temp);
        println("temp: ${temp} luck: ${luck}")
        true
    }
}