FROM oracle/graalvm-ce:19.1.0
ARG ELASTIC_APM_AGEN_VERSION=1.12.0
RUN curl https://repo1.maven.org:443/fromsearch?filepath=co/elastic/apm/elastic-apm-agent/${ELASTIC_APM_AGEN_VERSION}/elastic-apm-agent-${ELASTIC_APM_AGEN_VERSION}.jar \
    -o /elastic-apm-agent.jar

EXPOSE 8080
EXPOSE 8081

ADD main/build/libs/*.jar /app.jar
ENTRYPOINT [ "java", "-javaagent:/elastic-apm-agent.jar", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar" ]