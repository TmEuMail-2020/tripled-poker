## Status
[![pipeline status](https://gitlab.rotate-it.be/tripled/poker/badges/master/pipeline.svg)](https://gitlab.rotate-it.be/tripled/poker/commits/master)
[![coverage report](https://gitlab.rotate-it.be/tripled/poker/badges/master/coverage.svg)](https://gitlab.rotate-it.be/tripled/poker/commits/master)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)
[![Slack Widget](https://img.shields.io/badge/Slack-Opentripled-blue.svg?style=flat-square)](https://tripled-io.slack.com/messages/opentripled)


## Functional

We assume most people are familiar with texas holdem, poker, here's an eventstorming overview of what it looks like:

![alt text](eventstorming.png "poker eventstorming")

There is also a video on how to play:
[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/GAoR9ji8D6A/0.jpg)](https://www.youtube.com/watch?v=GAoR9ji8D6A)

## Initial setup

https://start.spring.io
* gradle, kotlin, spring-boot
* web, embedded mongo, actuator

https://github.com/graphql-java-kickstart/graphql-spring-boot
* graphql & graphiql

https://docs.spring.io/spring-metrics/docs/current/public/prometheus
* business & technical metrics
* grafana spring boot dashboard: https://grafana.com/dashboards/4701

## getting started

### build it
* ./gradlew build
* ./gradlew bootRun

### Serve to 
* localhost:8080/graphiql
* localhost:8080/api/businessMetric