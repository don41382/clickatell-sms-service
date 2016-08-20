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
sbt run -Dconfig.resource=application.prod.conf 
```
- send a SMS (49 for germany in the example)
```
http://localhost:8080/send?receiver=49401234&msg=Hello!
````


