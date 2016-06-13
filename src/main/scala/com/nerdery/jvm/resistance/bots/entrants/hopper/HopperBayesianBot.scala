package com.nerdery.jvm.resistance.bots.entrants.hopper

import java.security.SecureRandom
import java.util

import akka.actor._
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.nerdery.jvm.resistance.bots.DoctorBot
import com.nerdery.jvm.resistance.models.Prescription
import twitter4j.{StatusUpdate, TwitterFactory}

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * The Hopper bot. Uses conditional probabilities. Also violates
  * HIPAA by Tweeting about patients its seeing.
  *
  * Pros: Tries to use probabilities of outcomes when making decisions. Is a social butterfly knife.
  * Cons: Other doctors are illogical, and sometimes this bot is just unlucky. But those things don't matter,
  * it was going to get sued anyways for violating HIPAA.
  *
  * Note: Twitter functionality assumes there's a twitter4j.properties file on the root of the classpath.
  *
  * @author Stephen Hopper (stephenmhopper@gmail.com)
  */
class HopperBayesianBot(tweetMuch: Boolean = true) extends DoctorBot {
  import HopperBayesianBot._
  private val prescriptionMap = mutable.Map[String, ListBuffer[PrescriptionData]]()
  private val randy = new SecureRandom()

  //Default constructor. Disables Twitter functionality.
  def this() {
    this(false)
  }

  override def getUserId: String = "hopper"

  override def prescribeAntibiotic(patientTemperature: Float, previousPrescriptions: util.Collection[Prescription]) = {
    updatePrescriptions(previousPrescriptions.toList)
    val temp: PatientTemp = PatientTemp(patientTemperature)
    val probMoneyIfPrescribeAntibiotic: Float = probMoneyGivenAntibiotic(temp)
    val probMoneyIfPrescribeRest: Float = probMoneyGivenRest(temp)

    println(s"Patient temp: $patientTemperature")
    println(s"Probability of money if prescribe antibiotic: $probMoneyIfPrescribeAntibiotic")
    println(s"Probability of money if prescribe rest: $probMoneyIfPrescribeRest")

    //do the comparison. If it's within 5%, then we'll just go with antibiotics 'cause we get moar $$$
    val prescribeDrugs = probMoneyIfPrescribeAntibiotic > (probMoneyIfPrescribeRest - 0.05f)
    println(s"Prescribe antibiotics?: $prescribeDrugs")

    val status: StatusUpdate = buildStatus(patientTemperature, prescribeDrugs, probMoneyIfPrescribeAntibiotic, probMoneyIfPrescribeRest)
    val otherDocStatus = buildOtherDocStatusMaybe(previousPrescriptions.toList)
    if (tweetMuch) {
      try {
        twitterActor ! status
        otherDocStatus.foreach(s => twitterActor ! s)
      } catch {
        case t: Throwable =>
          println(s"Something went wrong whilst tweeting: $t")
          t.printStackTrace()
      }
    }

    prescribeDrugs
  }

  private def buildOtherDocStatusMaybe(previousPrescriptions: List[Prescription]): Option[StatusUpdate] = {
    if (previousPrescriptions.nonEmpty) {
      val otherDocs = previousPrescriptions.filter(_.getUserId != "hopper")
      val otherDoc = otherDocs(randy.nextInt(otherDocs.size))
      val handle = s"@${otherDoc.getUserId.substring(0,Math.min(49, otherDoc.getUserId.length))}"
      val temp = PatientTemp(otherDoc.getTemperature)
      val tempStr = s"${format(temp.temperature)}F"
      val statusOpt: Option[String] = (PatientTemp(otherDoc.getTemperature), otherDoc.isPrescribedAntibiotics) match {
        case (t, true) if t.getProbInfected <= 0.25 => Option(s"OMG ROFL $handle prescribed drugs to a patient with a temp of $tempStr $HASHTAG")
        case (t, false) if t.getProbInfected <= 0.25 => Option(s"$handle prescribed rest to a patient with a temp of $tempStr. Such dull Much boring $HASHTAG")
        case (t, false) if t.getProbInfected > 0.5 => Option(s"Lolz $handle prescribed rest to a patient with a temp of $tempStr $HASHTAG")
        case _ => None
      }
      statusOpt.map { status =>
        println(status)
        val update = new StatusUpdate(status)
        update.setPossiblySensitive(false) //hilarious
        update
      }
    } else {
      None
    }
  }

  private def buildStatus(temp: Float, prescribeDrugs: Boolean, chanceMoneyAnti: Float, chanceMoneyRest: Float): StatusUpdate = {
    val antiMoneyStr: String = format(chanceMoneyAnti * 100.0f)
    val restMoneyStr: String = format(chanceMoneyRest * 100.0f)
    val status: String = prescribeDrugs match {
      case true => s"Prescribed drugs to a patient with a fever of ${format(temp)}F as there's a $antiMoneyStr% chance I get $$$$$$ from this (vs $restMoneyStr%) $HASHTAG"
      case false => s"Told some fool to rest up because their fever was ${format(temp)}F and there's a $restMoneyStr% chance I get $$$$$$ from this (vs $antiMoneyStr%) $HASHTAG"
    }
    println(status)
    val update: StatusUpdate = new StatusUpdate(status)
    update.setPossiblySensitive(false) //hilarious
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

  private def probUnluckyGivenRest(temp: PatientTemp): Float = math.pow((temp.temperature - 100.0) / 3.0, 5.0).toFloat / 2.0f

  private def probInfected(temp: PatientTemp): Float = temp.getProbInfected
}

object HopperBayesianBot {
  private val MIN_ROUNDS_FOR_DOC_PROB_CALC: Int = 9
  private val DEFAULT_DOC_PROB_CALC: Float = 0.9f
  private val MIN_SIMILAR_PRESCRIPTION_PROB_CALC: Int = 5
  private val WEIGHT_SIMILAR_PRESCRIPTION_PROB: Float = 0.9f
  private val WEIGHT_OVERALL_PRESCRIPTION_PROB: Float = 1 - WEIGHT_SIMILAR_PRESCRIPTION_PROB
  private val HASHTAG = "#NerderyResistance"

  //sure, we could just use futures, but why not over-engineer and just use Akka?
  private lazy val system = ActorSystem("HopperBayes")
  private lazy val twitterActor = system.actorOf(Props(classOf[TweetMuch]), "tweet-much")

  class TweetMuch extends Actor with ActorLogging {
    def receive = {
      case status: StatusUpdate =>
        log.info("Sending status update to twitter")
        TwitterFactory.getSingleton.updateStatus(status)
    }
  }
}