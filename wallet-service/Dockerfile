FROM gradle:8-jdk17

EXPOSE 8082

WORKDIR /app

COPY . .

RUN gradle assemble

ENTRYPOINT ["java", "-jar","./build/libs/wallet-service-0.0.1-SNAPSHOT.jar"]