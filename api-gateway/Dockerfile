FROM gradle:8-jdk17

EXPOSE 8080

WORKDIR /app

COPY . .

RUN gradle assemble

ENTRYPOINT ["java", "-jar","./build/libs/api-gateway-0.0.1-SNAPSHOT.jar"]