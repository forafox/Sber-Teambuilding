FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built jar file
COPY build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

