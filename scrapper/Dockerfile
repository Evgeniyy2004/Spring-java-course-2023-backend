FROM openjdk:21
# Создание рабочей директории
COPY ./target/commandservice.jar /app/commandservice.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/commandservice.jar"]
