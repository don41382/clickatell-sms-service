# clickatell-sms-service
Clickatell simple sms service, build with scala / spray

## How to run

- create conf/application.prod.conf
```
include "application"

api {
  username = ???
  password = ???
}

// this are the clickatell api informations
sms-service {
  from = ???
  appId = ???
  username = ???
  password = ???
}
```
- run
```
sbt run -Dconfig.resource=application.test.conf 
```
- send a SMS
```
http://localhost:8080/send?receiver=0049401234&msg=Hello!
````


