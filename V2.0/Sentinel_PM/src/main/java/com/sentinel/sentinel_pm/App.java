package com.sentinel.sentinel_pm;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) {
        // Limpiar archivos de configuración problemáticos antes de iniciar
        limpiarArchivosConflictivos();
        
        // Iniciar la aplicación normalmente
        AppInitializer.main(args);
    }
    
    private static void limpiarArchivosConflictivos() {
        try {
            // Verificar si hay un archivo marcador de corrupción
            File corruptedMarker = new File("config.json.corrupted");
            if (corruptedMarker.exists()) {
                System.out.println("Detectado marcador de configuración corrupta. Limpiando archivos...");
                
                // Eliminar todos los archivos de configuración
                Files.deleteIfExists(Paths.get("config.json"));
                Files.deleteIfExists(Paths.get("config.json.bak"));
                Files.deleteIfExists(Paths.get("secret.key"));
                Files.deleteIfExists(Paths.get("secret.key.bak"));
                Files.deleteIfExists(Paths.get("config.json.corrupted"));
                
                System.out.println("Archivos de configuración eliminados correctamente.");
            }
        } catch (Exception e) {
            System.err.println("Error al limpiar archivos: " + e.getMessage());
        }
    }
}