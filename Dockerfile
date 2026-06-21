# ===== Run bosqichi: faqat jar bilan yengil image =====
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Yuklangan rasmlar uchun papka (volume bilan ulanadi)
RUN mkdir -p /app/uploads

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
