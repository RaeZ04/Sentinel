package com.sentinel.sentinel_pm;

/**
 * Punto de entrada alternativo para la aplicación.
 * Este archivo se mantiene por compatibilidad, pero toda la lógica
 * de inicialización ahora está en AppInitializer.
 */
public class App {
    public static void main(String[] args) {
        // Delegar la inicialización al AppInitializer
        AppInitializer.main(args);
    }
}