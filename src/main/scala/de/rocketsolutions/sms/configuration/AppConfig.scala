package de.rocketsolutions.sms.configuration

import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

case class APIConf(username: String, password: String)
case class SMSServiceConf(appId: String, username: String, password: String, from: String)

class AppConfig(config: com.typesafe.config.Config) {
  val smsServiceConf: SMSServiceConf = config.as[SMSServiceConf]("sms-service")
  val apiConf: APIConf = config.as[APIConf]("api")
}