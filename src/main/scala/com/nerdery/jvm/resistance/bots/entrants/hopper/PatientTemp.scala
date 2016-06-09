package com.nerdery.jvm.resistance.bots.entrants.hopper

/**
  * Enumeration type of thing for representing patient temperature categories.
  */
sealed trait PatientTemp {
  def getProbInfected: Float
}

object PatientTemp {
  def apply(temperature: Float): PatientTemp = {
    temperature match {
      case t if t < 100.0f => P0
      case t if t >= 100.0f && t < 101.0f => P25
      case t if t >= 101.0f && t < 102.0f => P50
      case t if t >= 102.0f && t < 103.0f => P75
      case _ => P100
    }
  }
}

case object P0 extends PatientTemp {
  override def getProbInfected: Float = 0.0f
}

case object P25 extends PatientTemp {
  override def getProbInfected: Float = 0.25f
}

case object P50 extends PatientTemp {
  override def getProbInfected: Float = 0.50f
}

case object P75 extends PatientTemp {
  override def getProbInfected: Float = 0.75f
}

case object P100 extends PatientTemp {
  override def getProbInfected: Float = 1.0f
}