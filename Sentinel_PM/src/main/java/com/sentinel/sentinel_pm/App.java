package com.sentinel.sentinel_pm;

/**
 * Punto de entrada principal para la aplicación.
 */
public class App {
    public static void main(String[] args) {
        try {
            // Lanzar directamente la aplicación JavaFX
            AppInitializer.main(args);
        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
            
            System.err.println("\n\n=== ERROR DE INICIALIZACIÓN ===");
            System.err.println("No se pudo iniciar Sentinel Password Manager.");
            System.err.println("Por favor, asegúrese de tener Java 21 o superior instalado.");
            System.err.println("Si el problema persiste, contacte al soporte técnico.");
        }
    }
}