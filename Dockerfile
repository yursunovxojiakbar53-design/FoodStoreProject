FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/*.jar foodstore.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "foodstore.jar"]