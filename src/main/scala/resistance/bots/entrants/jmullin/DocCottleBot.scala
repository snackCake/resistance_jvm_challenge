package resistance.bots.entrants.jmullin

import java.util

import com.nerdery.jvm.resistance.bots.DoctorBot
import com.nerdery.jvm.resistance.models.Prescription

import scala.collection.JavaConversions._
import scala.language.postfixOps

/**
 * Major Sherman Cottle, M.D., Chief Medical Officer, Battlestar Galactica.
 */
class DocCottleBot extends DoctorBot {
  import DocCottleBot._

  override def getUserId = "jmullin"

  /**
   * Store a profile on all the doctor's we've seen.
   */
  private var profiles = Map[String, DoctorProfile]()

  override def prescribeAntibiotic(temperature: Float, previousPrescriptions: util.Collection[Prescription]) = {
    // Update our prescription history with the results from yesterday.
    previousPrescriptions foreach { prescription =>
      val profile = profiles.getOrElse(prescription.getUserId, DoctorProfile(prescription.getUserId))
      profiles = profiles +
        (prescription.getUserId -> profile.copy(prescriptionHistory = profile.prescriptionHistory :+ prescription))
    }

    if(profiles.contains("jklun") && temperature < 102.0f && previousPrescriptions.exists(!_.isPrescribedAntibiotics)) {
      // "I find it absolutely amazing. You people went to all the trouble to appear human and didn't upgrade the plumbing!"
      PrescribeAntibiotics
    } else if(looksViral(temperature)) {
      if(profiles.values.exists(_.isCautious)) {
        // "Freaking hypochondriac. One on every bloody ship."
        PrescribeAntibiotics
      } else {
        // Is he gonna make it? "How should I know? I'm not a psychic."
        PrescribeRest
      }
    } else {
      // "Jaw set nicely. You're done here."
      PrescribeAntibiotics
    }
  }

  /**
   * Determines if a given temperature should be assumed a viral infection.
   *
   * @param temperature The patient's temperature.
   * @return
   */
  private def looksViral(temperature: Float) = chanceInfected(temperature) < AntibioticsThreshold

  /**
   * Calculates the chance of a bacterial infection given a patient's temperature.
   *
   * @param temperature The patient's temperature.
   * @return The chance this patient's infection is bacterial and requires antibiotics.
   */
  private def chanceInfected(temperature: Float) = temperature match {
    case _ if temperature < 100.0f => 0.0f
    case _ if temperature < 101.0f => 0.25f
    case _ if temperature < 102.0f => 0.5f
    case _ if temperature < 103.0f => 0.75f
    case _ => 1.0f
  }

  /**
   * Determines if a given prescription was reckless. A reckless prescription is one where
   * antibiotics are prescribed and the patient is very unlikely to have a bacterial infection.
   *
   * @param prescription The prescription to judge.
   * @return True if the prescription was reckless, false otherwise.
   */
  private def isPrescriptionReckless(prescription: Prescription) = prescription.getTemperature < 100.75f && prescription.isPrescribedAntibiotics

  /**
   * Contains a recollection of a doctor's actions and perceived recklessness.
   *
   * @param doctorName The name of this doctor.
   * @param prescriptionHistory The prescriptions this doctor has made in the past.
   */
  private case class DoctorProfile(doctorName: String, prescriptionHistory: Seq[Prescription] = Nil) {
    def isReckless = recklessnessIndex > -2
    def isCautious = recklessnessIndex <= -2

    def recklessnessIndex = prescriptionHistory.map(isPrescriptionReckless).map {
      case true => 1
      case false => -1
    }.sum

  }
}

object DocCottleBot {
  val PrescribeAntibiotics = true
  val PrescribeRest = false

  /**
   * Percentage chance of bacterial infection at which the Doc must assume the worst.
   */
  val AntibioticsThreshold = 0.4
}