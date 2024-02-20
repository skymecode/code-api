FROM openjdk:8
VOLUME /tmp
ADD kl-backend-0.0.1.jar /app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]