FROM azul/zulu-openjdk:17-jre
ARG ELASTIC_APM_AGENT_VERSION=1.15.0
RUN curl https://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/${ELASTIC_APM_AGEN_VERSION}/elastic-apm-agent-${ELASTIC_APM_AGEN_VERSION}.jar -o /elastic-apm-agent.jar

EXPOSE 8080
EXPOSE 8081

ADD main/build/libs/*.jar /app.jar
# "-javaagent:/elastic-apm-agent.jar",
ENTRYPOINT [ "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar" ]
