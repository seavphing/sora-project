FROM gradle:8-jdk17

EXPOSE 8083

WORKDIR /app

COPY . .

RUN gradle assemble

ENTRYPOINT ["java", "-jar","./build/libs/gift-code-service-0.0.1-SNAPSHOT.jar"]