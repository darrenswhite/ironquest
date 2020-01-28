FROM maven:3.6.1-jdk-8-alpine

WORKDIR /chosenlegends/server

ADD entrypoint.sh ./
RUN set -ex && \
    chmod +x entrypoint.sh

ADD pom.xml ./
ADD src ./src

EXPOSE 80

ENTRYPOINT ["./entrypoint.sh"]
