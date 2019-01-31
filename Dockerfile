FROM oracle/graalvm-ce:1.0.0-rc11
EXPOSE 8080
EXPOSE 8081
ADD build/libs/*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]