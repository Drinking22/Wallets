FROM openjdk:17-oracle

WORKDIR /app

COPY target/wallets-0.0.1-SNAPSHOT.jar app.jar

ENV CREATE_ON_STARTUP=true

CMD ["java", "-jar", "app.jar"]