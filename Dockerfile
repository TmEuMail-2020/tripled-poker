FROM oracle/graalvm-ce:19.1.0
EXPOSE 8080
EXPOSE 8081
ADD main/build/libs/*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar", "-D", "spring.devtools.add-properties=false"]