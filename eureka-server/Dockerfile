FROM gradle:8-jdk17

EXPOSE 8761

WORKDIR /app

COPY . .

RUN gradle assemble

ENTRYPOINT ["java", "-jar","./build/libs/eureka-server-0.0.1-SNAPSHOT.jar"]