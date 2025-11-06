package com.mycompany.app;

/**
 * Hello world with Docker environment support.
 */
public class App {

    public App() {}

    public static void main(String[] args) {
        // On lit la variable d'environnement APP_MESSAGE (si elle existe)
        String message = System.getenv("APP_MESSAGE");

        // Si aucune variable n'est fournie, on garde la valeur par défaut
        if (message == null || message.isBlank()) {
            message = "Hello World!";
        }

        System.out.println(message);
    }
}
