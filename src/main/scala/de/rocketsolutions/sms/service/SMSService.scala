package de.rocketsolutions.sms.service

import java.net.URLEncoder

import de.rocketsolutions.sms.configuration.SMSServiceConf
import de.rocketsolutions.sms.service.SMSService._

import scala.concurrent.{ExecutionContext, Future}
import scalaj.http.Http

case class SMSService(conf: SMSServiceConf)(implicit ec: ExecutionContext) {

  def sendSMS(receiver: String, msg: String): Future[SMSResponse] = {
    Future {
      val url = s"http://api.clickatell.com/http/sendmsg?user=${conf.username}&password=${conf.password}&api_id=${conf.appId}&to=$receiver&from=${conf.from}&text=${URLEncoder.encode(msg,"UTF-8")}"
      Http(url).asString match {
        case response if (response.is2xx) =>
          SMSOk(receiver)
        case response if (response.is4xx) =>
          SMSAuthorizationFailure(receiver)
        case response =>
          SMSError(receiver, s"unexpected response code '${response.code}'")
      }
    }
  }

}

object SMSService {

  sealed trait SMSResponse {
    def receiver: String
  }
  case class SMSOk(receiver: String) extends SMSResponse
  case class SMSAuthorizationFailure(receiver: String) extends SMSResponse
  case class SMSError(receiver: String, msg: String) extends SMSResponse
}