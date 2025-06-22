FROM openjdk:21-jdk-slim
COPY ./target/bot.jar bot.jar
ARG app_telegram_token
ENV APP_TELEGRAM_TOKEN=$app_telegram_token
ENTRYPOINT ["java","-jar","bot.jar"]
