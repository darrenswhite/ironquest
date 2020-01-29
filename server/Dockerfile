FROM maven:3.6.1-jdk-8-alpine as maven

ADD pom.xml ./

RUN mvn -B -T 1C clean dependency:go-offline

ADD src ./src

RUN mvn -T 1C clean install

FROM openjdk:8-jdk-alpine

RUN addgroup -S ironquest && adduser -S ironquest -G ironquest
USER ironquest:ironquest

WORKDIR /home/ironquest

COPY --from=maven target/ironquest.jar ./

CMD ["/bin/sh", "-c", "/usr/bin/java -jar /home/ironquest/ironquest.jar --server.port=${PORT}"]
