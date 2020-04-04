FROM maven:3.6.3-jdk-11-slim as maven

ADD pom.xml ./

RUN mvn -B -T 1C clean dependency:go-offline

ADD src ./src

RUN mvn -T 1C clean install

FROM openjdk:11-jre-slim

RUN groupadd -r ironquest && useradd -r ironquest -g ironquest
USER ironquest:ironquest

WORKDIR /home/ironquest

COPY --from=maven target/*.jar ./

CMD ["/bin/sh", "-c", "java -jar /home/ironquest/*.jar --server.port=${PORT}"]
