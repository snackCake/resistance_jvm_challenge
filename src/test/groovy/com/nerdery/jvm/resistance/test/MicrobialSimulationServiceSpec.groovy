package com.nerdery.jvm.resistance.test

import com.nerdery.jvm.resistance.models.Outcome
import com.nerdery.jvm.resistance.models.Patient
import com.nerdery.jvm.resistance.models.Prescription
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

    def "Patients with clear-cut cases should get correct outcomes"() {
        given:
        def service = new MicrobialSimulationService()

        when:
        def patients = [new Patient(99.5f, false), new Patient(103.5f, true)]
        def prescriptions = [new Prescription("test", false, 99.5f), new Prescription("test", true, 103.5f)]

        then: "Correct diagnoses get the correct outcomes"
        println(patients)
        println(prescriptions)
        def outcomes = service.divineOutcomes(patients, prescriptions)
        outcomes[0].outcome == Outcome.VIRAL_REST && outcomes[1].outcome == Outcome.BACTERIAL_ANTIBIOTICS
    }

    def "Patients with wrong bacterial diagnoses should get acceptable outcomes"() {
        given:
        def service = new MicrobialSimulationService()

        when:
        def patients = [new Patient(102.5f, true)]
        def prescriptions = [new Prescription("test", false, 102.5f)]

        then: "Incorrect diagnoses get the correct outcomes"
        println(patients)
        println(prescriptions)
        def outcomes = service.divineOutcomes(patients, prescriptions)
        println(outcomes)
        outcomes[0].outcome == Outcome.LUCKY_BACTERIAL_REST || outcomes[0].outcome == Outcome.UNLUCKY_BACTERIAL_REST
    }

    def "Patients with wrong bacterial diagnoses should get the same outcomes, because luck is in the air"() {
        given:
        def service = new MicrobialSimulationService()

        when:
        def patients = [new Patient(102.5f, true), new Patient(102.5f, true), new Patient(102.5f, true), new Patient(102.5f, true),
                        new Patient(102.5f, true), new Patient(102.5f, true), new Patient(102.5f, true), new Patient(102.5f, true)]
        def prescriptions = [new Prescription("test", false, 102.5f), new Prescription("test", false, 102.5f),
                             new Prescription("test", false, 102.5f), new Prescription("test", false, 102.5f),
                             new Prescription("test", false, 102.5f), new Prescription("test", false, 102.5f),
                             new Prescription("test", false, 102.5f), new Prescription("test", false, 102.5f)]

        then: "Missed bacterial infections should all have the same outcome on the same day"
        println(patients)
        println(prescriptions)
        def outcomes = service.divineOutcomes(patients, prescriptions)
        println(outcomes)
        def identicalLuck = true
        outcomes.forEach { patientOutcome -> identicalLuck = identicalLuck && patientOutcome.outcome == outcomes[0].outcome}
        identicalLuck
    }

    def "Everybody prescribing antibiotics incorrectly triggers the zombie apocalypse"() {
        given:
        def service = new MicrobialSimulationService()

        when:
        def patients = [new Patient(101.5f, false),
                        new Patient(101.5f, false),
                        new Patient(101.5f, false),
                        new Patient(101.5f, false)]
        def prescriptions = [new Prescription("test", true, 101.5f),
                             new Prescription("test", true, 101.5f),
                             new Prescription("test", true, 101.5f),
                             new Prescription("test", true, 101.5f)]

        then: "#temp is unlucky"
        println(patients)
        println(prescriptions)
        def outcomes = service.divineOutcomes(patients, prescriptions)
        outcomes[0].outcome == Outcome.UNLUCKY_VIRAL_ANTIBIOTICS && outcomes[1].outcome == Outcome.UNLUCKY_VIRAL_ANTIBIOTICS &&
                outcomes[2].outcome == Outcome.UNLUCKY_VIRAL_ANTIBIOTICS && outcomes[3].outcome == Outcome.UNLUCKY_VIRAL_ANTIBIOTICS
    }
}