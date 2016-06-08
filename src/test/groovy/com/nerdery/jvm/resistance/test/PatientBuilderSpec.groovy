package com.nerdery.jvm.resistance.test

import com.nerdery.jvm.resistance.models.Patient
import spock.lang.Specification

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
class PatientBuilderSpec extends Specification {
  def "Built Patient temperature will be in the valid range"() {
    given:
    def builder = Patient.builder()
    def min = 5.0f
    def max = 10.0f

    when: "temp range between #min and #max and randomized"
    def patient = builder.minTemperature(min)
            .maxTemperature(max)
            .randomizeNeeded(true)
            .build()

    then: "temperature should be between #min and #max"
    println(patient)
    patient.temperature >= min && patient.temperature <= max
  }

  def "Unrandomized patients have the same parameters"() {
    given:
    def builder = Patient.builder()
    when: "Only the first patient is randomized"
    def patients = [builder.randomizeNeeded(true).build(),
                    builder.build(),
                    builder.build(),
                    builder.build(),
                    builder.build()]

    then: "subsequent patients should have the same temp and infection status"
    patients.find { patient -> println(patient); patient != patients[0] } == null
  }

  def "Randomized patients have different parameters"() {
    given:
    def builder = Patient.builder()
    when: "All patients are randomized"
    def patients = [builder.randomizeNeeded(true).build(),
                    builder.randomizeNeeded(true).build(),
                    builder.randomizeNeeded(true).build(),
                    builder.randomizeNeeded(true).build(),
                    builder.randomizeNeeded(true).build()]

    then: "subsequent patients should have the different temp and infection status"
    def differentPatients = patients.findAll { patient -> println(patient); patient != patients[0]}
    differentPatients != null && differentPatients.size == patients.size() - 1
  }
}
