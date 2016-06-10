package com.nerdery.jvm.resistance.bots.test

import java.util

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.nerdery.jvm.resistance.bots.DoctorBot
import com.nerdery.jvm.resistance.models.Prescription

/**
  * Meryl Streep. She's doing her best to win that Golden Globe.
  */
class MerylStreepBot extends DoctorBot {
  override def getUserId: String = "MerylStreep"

  override def prescribeAntibiotic(patientTemperature: Float, dontCare: util.Collection[Prescription]): Boolean = patientTemperature >= 102.0f
}

private class IWantMyGoldenGlobeActor extends Actor with ActorLogging {
  import MerylStreepBot._
  override def receive = {
    case AllAloneInFrontOfTheNet(doctors) => {
      //Tell the candidate to engage iff Meryl is not in the match
      if (!doctors.contains("MerylStreep")) {
        sender() ! Engage
      } else {
        sender() ! PlayItCool
      }
    }
  }
}

object MerylStreepBot {
  val system = ActorSystem("ManchurianCandidate")
  val merylStreep = system.actorOf(Props[IWantMyGoldenGlobeActor], "meryl-streep")

  case class AllAloneInFrontOfTheNet(doctors: List[String])

  sealed trait Massage //everybody loves massages
  case object Engage extends Massage
  case object PlayItCool extends Massage
}
