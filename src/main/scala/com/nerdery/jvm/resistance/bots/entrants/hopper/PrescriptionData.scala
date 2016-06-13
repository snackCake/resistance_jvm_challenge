package com.nerdery.jvm.resistance.bots.entrants.hopper

import com.nerdery.jvm.resistance.models.Prescription

/**
  * A prettified version of the prescription object that simplifies decision making.
  */
case class PrescriptionData(prescribedAntibiotics: Boolean, temp: PatientTemp)

object PrescriptionData {
  def apply(p: Prescription): PrescriptionData = {
    PrescriptionData(
      prescribedAntibiotics = p.isPrescribedAntibiotics,
      temp = PatientTemp(p.getTemperature)
    )
  }
}
