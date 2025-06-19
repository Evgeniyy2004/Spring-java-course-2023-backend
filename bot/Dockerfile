FROM openjdk:21
# Создание рабочей директории
ARG app_telegram_token
ENV APP_TELEGRAM_TOKEN=$app_telegram_token
COPY ./target/bot.jar /app/bot.jar
EXPOSE 8081

RUN echo "MY_VARIABLE=${APP_TELEGRAM_TOKEN}"
ENTRYPOINT ["java","-jar","/app/bot.jar"]
