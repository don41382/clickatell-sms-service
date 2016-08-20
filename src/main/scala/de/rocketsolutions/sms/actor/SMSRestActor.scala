package de.rocketsolutions.sms.actor

import akka.actor.{Actor, ActorLogging}
import de.rocketsolutions.sms.configuration.APIConf
import de.rocketsolutions.sms.service.SMSService
import org.slf4j.LoggerFactory
import spray.routing.HttpService
import spray.routing.authentication.{BasicAuth, UserPass}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class SMSRestActor(val apiConf: APIConf, val smsService: SMSService) extends Actor with SMSRestRoute {

  def authUser(user: String, pass: String) =
    apiConf.username == user && apiConf.password == pass

  def actorRefFactory = context
  def receive  = runRoute(route)

}



trait SMSRestRoute extends HttpService {

  val log = LoggerFactory.getLogger(this.getClass)

  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
  val smsService: SMSService

  def authUser(user: String, pass: String): Boolean

  def check_auth(userPass: Option[UserPass]): Option[String] = for {
      up <- userPass
      if authUser(up.user, up.pass)
    } yield up.user

  def authenticator(userPass: Option[UserPass]): Future[Option[String]] = Future { check_auth(userPass) }

  val route =
      path("send") {
        parameter("receiver", "msg") { (receiver,msg) =>
          authenticate(BasicAuth(authenticator _, realm = "secure area")) { user =>
            log.info(s"sending sms to $receiver: '$msg' ...")

              onComplete(smsService.sendSMS(
                receiver = receiver,
                msg = msg
              )) {

                case Success(response) =>
                  complete("send!")

                case Failure(err) =>
                  log.error("could not process sms request", err)
                  failWith(err)

              }

          }
        }
      }
}
