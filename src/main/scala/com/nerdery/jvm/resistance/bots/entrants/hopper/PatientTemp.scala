package com.nerdery.jvm.resistance.bots.entrants.hopper

/**
  * Enumeration type of thing for representing patient temperature categories.
  */
sealed trait PatientTemp {
  def getProbInfected: Float
  def temperature: Float
}

object PatientTemp {
  def apply(temperature: Float): PatientTemp = {
    temperature match {
      case t if t < 100.0f => P0(temperature)
      case t if t >= 100.0f && t < 101.0f => P25(temperature)
      case t if t >= 101.0f && t < 102.0f => P50(temperature)
      case t if t >= 102.0f && t < 103.0f => P75(temperature)
      case _ => P100(temperature)
    }
  }
}

case class P0(temperature: Float) extends PatientTemp {
  override def getProbInfected: Float = 0.0f
}

case class P25(temperature: Float) extends PatientTemp {
  override def getProbInfected: Float = 0.25f
}

case class P50(temperature: Float) extends PatientTemp {
  override def getProbInfected: Float = 0.50f
}

case class P75(temperature: Float) extends PatientTemp {
  override def getProbInfected: Float = 0.75f
}

case class P100(temperature: Float) extends PatientTemp {
  override def getProbInfected: Float = 1.0f
}