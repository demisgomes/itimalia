#Build stage
FROM gradle:7-jdk8-jammy AS BUILD
WORKDIR /usr/app/
COPY . .
RUN gradle build -x test -x integrationTest -x unitTest -x koverMergedVerify -x koverMergedReport

# Package stage
FROM eclipse-temurin:11-alpine
ENV JAR_NAME=itimalia-1.0.0-SNAPSHOT.jar
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY --from=BUILD $APP_HOME .
EXPOSE 7000
ENTRYPOINT exec java -jar $APP_HOME/build/libs/$JAR_NAME