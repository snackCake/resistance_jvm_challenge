package com.nerdery.jvm.resistance.bots.entrants.jmullin

import java.util

import com.nerdery.jvm.resistance.bots.DoctorBot
import com.nerdery.jvm.resistance.models.Prescription

import scala.collection.JavaConversions._
import scala.language.postfixOps

/**
 * Major Sherman Cottle, M.D., Chief Medical Officer, Battlestar Galactica.
 *
 * @author Justin Mullin
 */
class DocCottleBot extends DoctorBot {
  import DocCottleBot._

  override def getUserId = "jmullin"

  /**
   * Store a profile on all the doctors we've seen.
   */
  private var profiles = Map[String, DoctorProfile]()

  override def prescribeAntibiotic(temperature: Float, previousPrescriptions: util.Collection[Prescription]) = {
    // "I find it absolutely amazing. You people went to all the trouble to appear human and didn't upgrade the plumbing!"
    previousPrescriptions foreach { prescription =>
      val profile = profiles.getOrElse(prescription.getUserId, DoctorProfile(prescription.getUserId))
      if(profile.doctorName != getUserId) {
        profiles = profiles + (prescription.getUserId -> profile.copy(prescriptionHistory = profile.prescriptionHistory :+ prescription))
      }
    }

    if(riskThresholdIsLow(temperature)) {
      if(profiles.values.exists(doctor => doctor.isCautious || doctor.isReckless)) {
        // "Jaw set nicely. You're done here."
        PrescribeAntibiotics
      } else {
        // "Freaking hypochondriac. One on every bloody ship."
        PrescribeRest
      }
    } else {
      // Is he gonna make it? "How should I know? I'm not a psychic."
      PrescribeAntibiotics
    }
  }

  /**
   * Determines if a given temperature should be assumed safe.
   *
   * @param temperature The patient's temperature.
   * @return
   */
  private def riskThresholdIsLow(temperature: Float) = {
    chanceOfBacterialInfection(temperature)*chanceOfHospitalization(temperature) < RiskThreshold
  }

  /**
   * Calculates the chance of a bacterial infection given a patient's temperature.
   *
   * @param temperature The patient's temperature.
   * @return The chance this patient's infection is bacterial and requires antibiotics.
   */
  private def chanceOfBacterialInfection(temperature: Float) = temperature match {
    case _ if temperature < 100.0f => 0.0f
    case _ if temperature < 101.0f => 0.25f
    case _ if temperature < 102.0f => 0.5f
    case _ if temperature < 103.0f => 0.75f
    case _ => 1.0f
  }

  /**
   * Calculates the chance of hospitalization assuming the patient has a bacterial infection.
   *
   * @param temperature The patient's temperature.
   * @return The chance a bacterial infection would send this patient to the hospital.
   */
  private def chanceOfHospitalization(temperature: Float) = math.pow((temperature - 100.0) / 3.0, 5.0)

  /**
   * Determines if a given prescription was reckless. A reckless prescription is one where
   * antibiotics are prescribed and the patient is very unlikely to have a bacterial infection.
   *
   * @param prescription The prescription to judge.
   * @return True if the prescription was reckless, false otherwise.
   */
  private def prescriptionCategorization(prescription: Prescription) = {
    val hospitalizationChance = chanceOfBacterialInfection(prescription.getTemperature)*chanceOfHospitalization(prescription.getTemperature)

    if(hospitalizationChance > 0 && hospitalizationChance <= RiskThreshold && prescription.isPrescribedAntibiotics) {
      Reckless
    } else if (hospitalizationChance >= RiskThreshold && !prescription.isPrescribedAntibiotics) {
      Cautious
    } else {
      Reasonable
    }
  }

  /**
   * Contains a recollection of a doctor's actions and perceived recklessness.
   *
   * @param doctorName The name of this doctor.
   * @param prescriptionHistory The prescriptions this doctor has made in the past.
   */
  private case class DoctorProfile(doctorName: String, prescriptionHistory: Seq[Prescription] = Nil) {
    def isReckless = recklessnessIndex >= RecklessnessThreshold
    def isCautious = recklessnessIndex <= CautiousnessThreshold

    def recklessnessIndex = prescriptionHistory.map(prescriptionCategorization).map {
      case Reckless => 1
      case Cautious => -1
      case Reasonable => 0
    }.sum

  }
}

object DocCottleBot {
  val PrescribeAntibiotics = true
  val PrescribeRest = false

  /**
   * Percentage chance of hospitalization at which the Doc must assume the worst.
   */
  val RiskThreshold = 1.0/3.0

  /**
   * Number of net reckless decisions a doctor must make to be considered reckless.
   */
  val RecklessnessThreshold = 4

  /**
   * Number of net cautious decisions a doctor must make to be considered cautious.
   */
  val CautiousnessThreshold = -2

  sealed trait PrescriptionCategorization
  case object Reckless extends PrescriptionCategorization
  case object Reasonable extends PrescriptionCategorization
  case object Cautious extends PrescriptionCategorization
}