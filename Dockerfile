FROM gradle:6.9.0-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM bellsoft/liberica-openjdk-centos:11

EXPOSE 8099

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/santa-bot.jar /app/santa-bot.jar
ENV B_TOKEN=token
ENV B_NAME=name

ENTRYPOINT ["java","-jar","/app/santa-bot.jar","--santa.bot.token=${B_TOKEN}", "--santa.bot.name=${B_NAME}"]