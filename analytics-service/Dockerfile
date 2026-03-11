# 1. Start with a lightweight Java 21 environment
FROM eclipse-temurin:21-jre-alpine

# 2. Set the working directory inside the container
WORKDIR /app

# 3. Copy the compiled .jar file from your target folder into the container
COPY target/*.jar app.jar

# 4. Tell Docker what command to run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]