package com.sentinel.sentinel_pm;

import java.util.Arrays;
import java.util.List;
import java.nio.file.*;
import java.io.File;

/**
 * Punto de entrada principal para la aplicación.
 */
public class App {
    public static void main(String[] args) {
        try {
            // Detectar el sistema operativo
            String osName = System.getProperty("os.name").toLowerCase();
            System.out.println("Sistema operativo detectado: " + osName);
            
            // Comprobar si estamos dentro de un JAR
            String protocol = App.class.getResource("App.class").getProtocol();
            boolean runningFromJar = "jar".equals(protocol);
            
            if (runningFromJar) {
                // Solo es necesario configurar el módulo JavaFX si se ejecuta desde un JAR
                System.out.println("Ejecutando desde JAR. Configurando módulos JavaFX...");
                
                // Ruta donde se encuentra el JAR actual
                String jarPath = App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
                
                // En Windows, la ruta puede comenzar con /, lo que causa problemas
                if (osName.contains("win") && jarPath.startsWith("/")) {
                    jarPath = jarPath.substring(1);
                }
                
                String jarDir = Paths.get(jarPath).getParent().toString();
                System.out.println("Directorio del JAR: " + jarDir);
                
                // Obtener la ruta de los módulos JavaFX específicos para cada sistema operativo
                List<String> jfxModules = Arrays.asList(
                    "javafx-graphics", "javafx-controls", "javafx-fxml", "javafx-base"
                );
                
                // Directorio donde deberían estar las bibliotecas nativas de JavaFX
                String jfxLibPath = "";
                
                if (osName.contains("mac") || osName.contains("darwin")) {
                    jfxLibPath = new File(jarDir, "lib").getPath();
                    System.out.println("Configurando JavaFX para macOS en: " + jfxLibPath);
                } else if (osName.contains("win")) {
                    jfxLibPath = new File(jarDir, "lib").getPath();
                    System.out.println("Configurando JavaFX para Windows en: " + jfxLibPath);
                } else if (osName.contains("nux") || osName.contains("nix")) {
                    jfxLibPath = new File(jarDir, "lib").getPath();
                    System.out.println("Configurando JavaFX para Linux en: " + jfxLibPath);
                } else {
                    System.out.println("Sistema operativo no reconocido específicamente: " + osName + ". Intentando configuración genérica.");
                    jfxLibPath = new File(jarDir, "lib").getPath();
                }
                
                // Establecer la propiedad para JavaFX
                if (!jfxLibPath.isEmpty()) {
                    System.setProperty("javafx.runtime.path", jfxLibPath);
                    System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + jfxLibPath);
                }
            }
            
            // Invocamos directamente el lanzamiento de la aplicación JavaFX
            AppInitializer.main(args);
        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
            
            try {
                // Si hay un error, intentar lanzar la aplicación JavaFX de todos modos
                AppInitializer.main(args);
            } catch (Exception e2) {
                System.err.println("Error fatal al iniciar la aplicación. Detalles: " + e2.getMessage());
                e2.printStackTrace();
                
                // Mostrar mensaje de error más amigable para el usuario
                System.err.println("\n\n=== ERROR DE INICIALIZACIÓN ===");
                System.err.println("No se pudo iniciar Sentinel Password Manager.");
                System.err.println("Por favor, asegúrese de tener Java 21 o superior instalado.");
                System.err.println("Si el problema persiste, contacte al soporte técnico.");
            }
        }
    }
}