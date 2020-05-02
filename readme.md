## Status
[![pipeline status](https://gitlab.rotate-it.be/tripled/poker/badges/master/pipeline.svg)](https://gitlab.rotate-it.be/tripled/poker/pipelines)
[![coverage report](https://gitlab.rotate-it.be/tripled/poker/badges/master/coverage.svg)](https://gitlab.rotate-it.be/tripled/poker/commits/master)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)
[![Slack Widget](https://img.shields.io/badge/Slack-Opentripled-blue.svg?style=flat-square)](https://tripled-io.slack.com/messages/opentripled)


## Functional

We assume most people are familiar with texas holdem, poker, here's an eventstorming overview of what it looks like:

![alt text](eventstorming.png "poker eventstorming")

There is also a video on how to play:
[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/GAoR9ji8D6A/0.jpg)](https://www.youtube.com/watch?v=GAoR9ji8D6A)


## Getting started

### Build & run the backend
* ./gradlew assemble check jacocoTestReport detekt
* ./gradlew build
* ./gradlew bootRun

##$ Build & run the front-end

* cd poker-ui-app
* npm run build of yarn build
* npm start of yarn start


### Use It 
* localhost:3000 for the hosted frontend
* localhost:8080/graphiql for direct graphiql api on the backend
* localhost:8080/api/businessMetric for businessMetric of the backend


## Technical Setup
### Technologies

* Gradle
* Kotlin
* Spring-boot
    * actuator
    * web
* Embedded mongo
* Graphql & graphiql
    * [See GraphQl with Spring boot](https://github.com/graphql-java-kickstart/graphql-spring-boot)
* [Prometheus](https://docs.spring.io/spring-metrics/docs/current/public/prometheus)
    * business & technical metrics
* [Grafana spring boot dashboard](https://grafana.com/dashboards/4701)


### Infrastructure
#### Runtime infra
![alt text](runtime infra.png "runtime infrastructure")

#### Build infra
![alt text](build infra.png "building high level overview")

## Install keycloak
```
helm upgrade --namespace keycloak --install keycloak \
--set keycloak.persistence.deployPostgres=true \
--set keycloak.persistence.dbVendor=postgres \
--set postgresql.persistence.enabled=true \
--set keycloak.ingress.enabled=true \
--set keycloak.ingress.hosts={keycloak.otd.rotate-it.be} codecentric/keycloak
```

## Added keycloak gatekeeper
```
helm upgrade --install --version 1.6.0 \
    --set discoveryURL=https://keycloak.otd.rotate-it.be/auth/realms/poker \
    --set upstreamURL=http://poker-svc.poker-265.svc.cluster.local:8080 \
    --set ClientID=poker-gatekeeper \
    --set ClientSecret=e1c1a7de-e94e-487d-9c12-b23b2eebf20d \
    --set ingress.enabled=true \
    --set scopes[0]=openid \
    --set ingress.hosts[0]=poker.otd.rotate-it.be \
    --set forwarding.enable=false \
    --set forwarding.username=echouser \
    --set forwarding.password=echopassword \
    secure-poker gabibbo97/keycloak-gatekeeper
```

In order to become another user, add http header:
```
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJYZUh4Q0xKUEN1MGl3RmZNQTZOMi1MdExLZ3ZKVmUxb3NyQkxqeWpKWkdrIn0.eyJqdGkiOiI0NzAxNjA0YS0zZjYwLTQwOGQtODg3ZS03NjMwOWUwYzA0NGIiLCJleHAiOjE1ODYzNDU5NDcsIm5iZiI6MCwiaWF0IjoxNTg2MzQ1NjQ3LCJpc3MiOiJodHRwczovL2tleWNsb2FrLm90ZC5yb3RhdGUtaXQuYmUvYXV0aC9yZWFsbXMvcG9rZXIiLCJhdWQiOiJsb2NhbHRlc3QiLCJzdWIiOiI1NTg4YTczNS0yODM1LTRkNmUtODgzMy00ZjIyMTdmNzU1YjIiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJsb2NhbHRlc3QiLCJhdXRoX3RpbWUiOjE1ODYzNDU2NDcsInNlc3Npb25fc3RhdGUiOiJkY2YwZWJlMi1kYmQ0LTQ5OTQtOWViYy01MzAwNTA4NmM0MjUiLCJhY3IiOiIxIiwic2NvcGUiOiJvcGVuaWQiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0In0.V64rkhXYStcT0-cps_Oc_o9lx1624ErcQxD-MnzQ4dVeU8nok0yb_Nv48KJJ1ofHJuCbuawQxscXn5V12deJa9apF5BJKlI52s2KQjsHy6uf1__spJdPzLT5wcf6V-jdEK_wRJxmDKTSjGCOSuChGzw_HkG1a61YTZ_k8OHHuxQrpxNXupZ5jwxJKk8nGQ8nJPrs4G1r2BdGXNQezoDEuLZm8PBvJZvojDjPMIBWU9bO4G5jBfJmjm3WHFQ3-QsF5UkiPODcT_zu9mummJywJuvz16YyauE6Bpu56PgnjnE5zcUhap4NA27a_CtE32sN-eJlH_6p1gHkt1sX4fnMfA
```