# build
FROM eclipse-temurin:17-jre as builder
LABEL stage=builder
WORKDIR application

ARG JAR_FILE

ADD ${JAR_FILE} ./application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# image
FROM eclipse-temurin:17-jre
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

RUN cp /opt/java/openjdk/lib/security/cacerts /application/kafka.client.truststore.jks

EXPOSE 8080
ENTRYPOINT ["java","-Xms256m","-Xmx512m", "org.springframework.boot.loader.JarLauncher"]
