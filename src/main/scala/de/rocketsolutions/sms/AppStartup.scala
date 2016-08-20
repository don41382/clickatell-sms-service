package de.rocketsolutions.sms

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import de.rocketsolutions.sms.actor.SMSRestActor
import de.rocketsolutions.sms.configuration.AppConfig
import de.rocketsolutions.sms.service.SMSService

import scala.concurrent.duration._

object AppStartup extends App {

  implicit val system = ActorSystem("spray-actors")
  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
  implicit val timeout = Timeout(5.seconds)

  val appConf = new AppConfig(ConfigFactory.load())
  val smsService = new SMSService(appConf.smsServiceConf)

  val service = system.actorOf(Props(new SMSRestActor(appConf.apiConf, smsService)), "demo-service")

  IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = 8080)
}