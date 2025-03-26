FROM gradle:8-jdk17

EXPOSE 8085

WORKDIR /app

COPY . .

RUN gradle assemble

ENTRYPOINT ["java", "-jar","./build/libs/exchange-service-0.0.1-SNAPSHOT.jar"]