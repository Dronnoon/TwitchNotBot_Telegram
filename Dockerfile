FROM eclipse-temurin:23-jdk-alpine
WORKDIR /app
COPY target/Bot_Twitch_Notification_With_Telegram-1.0-SNAPSHOT.jar /app/TwitchNotificationBot.jar
ENTRYPOINT ["java", "-jar", "TwitchNotificationBot.jar"]