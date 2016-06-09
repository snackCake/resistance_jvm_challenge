package com.nerdery.jvm.resistance.bots.entrants.hopper

import java.security.SecureRandom
import java.util
import java.util.Random

import com.nerdery.jvm.resistance.bots.DoctorBot
import com.nerdery.jvm.resistance.models.Prescription
import twitter4j.{StatusUpdate, TwitterFactory}

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * The Hopper bot. Uses conditional probabilities. Also violates
  * HIPAA by Tweeting about patients its seeing.
  *
  * Pros: Tries to use probabilities of outcomes when making decisions. Is a social butterfly knife.
  * Cons: Other doctors are illogical, and sometimes this bot is just unlucky. But those things don't matter,
  * it was going to get sued anyways for violating HIPAA.
  *
  * @author Stephen Hopper
  */
class HopperBayesianBot(tweetMuch: Boolean = true) extends DoctorBot {
  import HopperBayesianBot._
  private val twitter = TwitterFactory.getSingleton
  private val prescriptionMap = mutable.Map[String, ListBuffer[PrescriptionData]]()

  override def getUserId: String = "hopper"

  override def prescribeAntibiotic(patientTemperature: Float, previousPrescriptions: util.Collection[Prescription]) = {
    updatePrescriptions(previousPrescriptions.toList)
    val temp: PatientTemp = PatientTemp(patientTemperature)
    val probMoneyIfPrescribeAntibiotic: Float = probMoneyGivenAntibiotic(temp)
    val probMoneyIfPrescribeRest: Float = probMoneyGivenRest(temp)

    println(s"Patient temp: $patientTemperature")
    println(s"Probability of money if prescribe antibiotic: $probMoneyIfPrescribeAntibiotic")
    println(s"Probability of money if prescribe rest: $probMoneyIfPrescribeRest")

    /*val prescribeAntibioticMoneyFactor = probMoneyIfPrescribeAntibiotic * (MONEY_ANTIBIOTIC_BAD + MONEY_ANTIBIOTIC_GOOD)
    val prescribeRestMoneyFactor = probMoneyIfPrescribeRest * (MONEY_REST_BAD + MONEY_REST_GOOD)
    println(s"Prescribe antibiotic money factor: $prescribeAntibioticMoneyFactor")
    println(s"Prescribe rest money factor: $prescribeRestMoneyFactor")
    val prescribeDrugs = prescribeAntibioticMoneyFactor >= prescribeRestMoneyFactor*/
    val prescribeDrugs = probMoneyIfPrescribeAntibiotic > probMoneyIfPrescribeRest
    println(s"Prescribe antibiotics?: $prescribeDrugs")

    val status: StatusUpdate = buildStatus(patientTemperature, prescribeDrugs, probMoneyIfPrescribeAntibiotic, probMoneyIfPrescribeRest)

    if (tweetMuch) {
      try {
        Future {
          twitter.updateStatus(status)
        }
      } catch {
        case e: Exception => //I want there to be no disintegrations
          println(s"Something went wrong whilst tweeting: $e")
          e.printStackTrace()
      }
    }

    prescribeDrugs
  }

  private def buildStatus(temp: Float, prescribeDrugs: Boolean, chanceMoneyAnti: Float, chanceMoneyRest: Float): StatusUpdate = {
    val antiMoneyStr: String = format(chanceMoneyAnti * 100.0f)
    val restMoneyStr: String = format(chanceMoneyRest * 100.0f)
    val status: String = prescribeDrugs match {
      case true => s"Prescribed drugs to a patient with a fever of ${format(temp)}F as there's a $antiMoneyStr% chance I get $$$$$$ from this (vs $restMoneyStr%) #NerderyResistance"
      case false => s"Told some fool to rest up because their fever was ${format(temp)}F and there's a $restMoneyStr% chance I get $$$$$$ from this (vs $antiMoneyStr%) #NerderyResistance"
    }
    println(status)
    val update: StatusUpdate = new StatusUpdate(status)
    update.setPossiblySensitive(false)
    update
  }

  private def format(f: Float): String = "%.1f".format(f)

  private def updatePrescriptions(previousPrescriptions: List[Prescription]) {
    previousPrescriptions.filter(p => !"hopper".equals(p.getUserId)).foreach(p => {
      val prescriptionData = PrescriptionData.apply(p)
      prescriptionMap.get(p.getUserId) match {
        case Some(prescriptions) => prescriptions += prescriptionData
        case _ => {
          val prescriptions = ListBuffer[PrescriptionData](prescriptionData)
          prescriptionMap.put(p.getUserId, prescriptions)
        }
      }
    })
  }

  private def probMoneyGivenAntibiotic(temp: PatientTemp): Float = probInfected(temp) + (1.0f - probInfected(temp)) * probLuckyGivenAntibiotic(temp)

  private def probLuckyGivenAntibiotic(temp: PatientTemp): Float = 1.0f - probUnluckyGivenAntibiotic(temp)

  private def probUnluckyGivenAntibiotic(temp: PatientTemp): Float = {
    countRounds <= MIN_ROUNDS_FOR_DOC_PROB_CALC match {
      case true => DEFAULT_DOC_PROB_CALC
      case false => {
        val probs = prescriptionMap.map {
          case (docId, prescriptions) =>
            val overallProbPrescribe = probPrescribeAntibiotic(prescriptions.toList)
            val similarPrescriptionData = prescriptions.filter(_.temp == temp).toList
            similarPrescriptionData.size >= MIN_SIMILAR_PRESCRIPTION_PROB_CALC match {
              case true =>
                val similarPrescriptionProb = probPrescribeAntibiotic(similarPrescriptionData)
                WEIGHT_SIMILAR_PRESCRIPTION_PROB * similarPrescriptionProb + WEIGHT_OVERALL_PRESCRIPTION_PROB * overallProbPrescribe
              case false => overallProbPrescribe
            }
        }
        probs.foldLeft(1.0f)(_ * _)
      }
    }
  }

  private def probPrescribeAntibiotic(samples: List[PrescriptionData]): Float = samples.count(_.prescribedAntibiotics) / samples.size.toFloat

  private def countRounds: Int = prescriptionMap.map(entry => entry._2.size).reduceOption(_ max _).getOrElse(0)

  private def probMoneyGivenRest(temp: PatientTemp): Float = probInfected(temp) * probLuckyGivenRest(temp) + (1.0f - probInfected(temp))

  private def probLuckyGivenRest(temp: PatientTemp): Float = 1.0f - probUnluckyGivenRest(temp)

  private def probUnluckyGivenRest(temp: PatientTemp): Float = probInfected(temp) * Math.min(1.0f, randy + 0.5f)

  private def randy: Float = new SecureRandom().nextFloat

  private def probInfected(temp: PatientTemp): Float = temp.getProbInfected
}

object HopperBayesianBot {
  private val MIN_ROUNDS_FOR_DOC_PROB_CALC: Int = 9
  private val DEFAULT_DOC_PROB_CALC: Float = 0.9f
  private val MIN_SIMILAR_PRESCRIPTION_PROB_CALC: Int = 5
  private val WEIGHT_SIMILAR_PRESCRIPTION_PROB: Float = 0.9f
  private val WEIGHT_OVERALL_PRESCRIPTION_PROB: Float = 1 - WEIGHT_SIMILAR_PRESCRIPTION_PROB
  /*private val MONEY_ANTIBIOTIC_GOOD: Int = 3
  private val MONEY_ANTIBIOTIC_BAD: Int = -100
  private val MONEY_REST_GOOD: Int = 1
  private val MONEY_REST_BAD: Int = -10*/
}