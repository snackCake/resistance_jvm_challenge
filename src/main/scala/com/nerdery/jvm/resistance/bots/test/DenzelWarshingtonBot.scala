package com.nerdery.jvm.resistance.bots.test

import java.util

import akka.actor.{Actor, ActorLogging, Props}
import com.nerdery.jvm.resistance.bots.DoctorBot
import com.nerdery.jvm.resistance.bots.test.MerylStreepBot.{AllAloneInFrontOfTheNet, Engage, Massage, PlayItCool}
import com.nerdery.jvm.resistance.models.Prescription

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.collection.JavaConversions._
import akka.pattern.ask
import akka.util.Timeout

/**
  * Denzel Warshington. He thinks he's in complete control, but he's not.
  */
class DenzelWarshingtonBot extends DoctorBot {

  val self = MerylStreepBot.system.actorOf(Props[ManchurianCandidateActor])
  private implicit val timeout: Timeout = Timeout(1.second)

  override def getUserId: String = "DenzelWarshington"

  override def prescribeAntibiotic(patientTemperature: Float, previousPrescriptions: util.Collection[Prescription]): Boolean = {
    val users = previousPrescriptions.toList.map(_.getUserId)
    try {
      //ask Denzel's conscious about what he should do here
      Await.result((self ? AllAloneInFrontOfTheNet(users)).mapTo[Boolean], 1.second)
    } catch {
      case t: Throwable => false
    }
  }
}

private class ManchurianCandidateActor extends Actor with ActorLogging {
  var activated: Boolean = false
  private implicit val timeout: Timeout = Timeout(1.second)

  override def receive = {
    case a: AllAloneInFrontOfTheNet => {
      //Denzel's conscious is not his own. Meryl owns it. Let's give her a chance to provide her input.
      Await.result((context.actorSelection("/user/meryl-streep") ? a).mapTo[Massage], 1.second) match {
        case Engage => activated = true
        case PlayItCool => activated = false
      }
      sender() ! activated
    }
  }
}
