FROM gradle:6.9.0-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM bellsoft/liberica-openjdk-centos:11

ENV B_TOKEN=token
ENV B_NAME=name
ENV DATABASE_URL=mongodb://localhost:27017/santa
ENV CA_CERT=cert

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/santa-bot.jar /app/santa-bot.jar

ENTRYPOINT ["java","-jar","/app/santa-bot.jar","--santa.bot.token=${B_TOKEN}", "--santa.bot.name=${B_NAME}","--spring.data.mongodb.uri=${DATABASE_URL}"]
