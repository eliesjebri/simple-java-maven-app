# Étape 1 : exécution avec une image légère
FROM eclipse-temurin:21-jre-alpine

# Copie du JAR compilé depuis le stage Jenkins
COPY target/my-app-1.0-SNAPSHOT.jar /app/my-app.jar

# Définit le répertoire de travail
WORKDIR /app

# Définit la commande de démarrage
ENTRYPOINT ["java", "-jar", "my-app.jar"]
