# clickatell-sms-service
Clickatell simple sms service, build with scala / spray / docker

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

## Run as docker

- create docker file
```
sbt docker
```
- run the docker & add the required parameter or use --env-file
```
docker run --env api_username=demo --env api_password=demo ....  sms-service/sms-service -p 8080:8080
```


