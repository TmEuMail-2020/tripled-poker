FROM java:8
EXPOSE 8080
EXPOSE 8081
ADD build/libs/*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]