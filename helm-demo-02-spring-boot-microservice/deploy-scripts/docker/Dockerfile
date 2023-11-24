FROM eclipse-temurin:17-jdk
VOLUME /tmp
ARG JAR_FILE=target/spring-boot-microservice.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
