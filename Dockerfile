# ===== Build bosqichi: jar ni Maven bilan yig'amiz =====
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Avval pom.xml — bog'liqliklar cache bo'lishi uchun
COPY pom.xml .
RUN mvn -B -q dependency:go-offline || true

# Manba kod va to'liq paketlash (testlarsiz)
COPY src ./src
RUN mvn -B -DskipTests clean package

# ===== Run bosqichi: faqat jar bilan yengil image =====
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Yuklangan rasmlar uchun papka (volume bilan ulanadi)
RUN mkdir -p /app/uploads

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
