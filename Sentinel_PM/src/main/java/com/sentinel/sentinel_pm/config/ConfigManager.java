package com.sentinel.sentinel_pm.config;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sentinel.sentinel_pm.entidadesJson.passRuta;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ConfigManager {
    
    private static final String CONFIG_FILENAME = "config.json";
    private static final String CONFIG_BACKUP_FILENAME = "config.json.bak";
    private static final String CONFIG_CORRUPTED_MARKER = "config.json.corrupted";
    private static final String SENTINEL_SUBFOLDER = "sentinel"; // Subfolder name
    private static String configDirectory = "";
    
    /**
     * Inicializa el sistema de configuración y limpia archivos corruptos si es necesario
     */
    public static void inicializar() {
        try {
            // Verificar si hay un archivo marcador de corrupción en el directorio actual
            File corruptedMarker = new File(CONFIG_CORRUPTED_MARKER);
            if (corruptedMarker.exists()) {
                System.out.println("Detectado marcador de configuración corrupta. Limpiando archivos...");
                
                // Eliminar todos los archivos de configuración
                Files.deleteIfExists(Paths.get(getConfigFilePath()));
                Files.deleteIfExists(Paths.get(getBackupFilePath()));
                Files.deleteIfExists(Paths.get(CONFIG_CORRUPTED_MARKER));
                
                System.out.println("Archivos de configuración eliminados correctamente.");
            }
        } catch (Exception e) {
            System.err.println("Error al limpiar archivos: " + e.getMessage());
        }
    }
    
    /**
     * Establece el directorio donde se almacenarán los archivos de configuración
     * 
     * @param directory La ruta base del directorio elegido por el usuario
     */
    public static void setConfigDirectory(String directory) {
        if (directory != null && !directory.isEmpty()) {
            // Crear la estructura de carpetas: ruta_del_usuario/sentinel
            String sentinelPath = Paths.get(directory, SENTINEL_SUBFOLDER).toString();
            configDirectory = sentinelPath;
            
            // Asegurarse que el directorio existe
            File dir = new File(sentinelPath);
            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("Creada carpeta sentinel en: " + sentinelPath);
            }
        }
    }
    
    /**
     * Obtiene la ruta completa al archivo de configuración
     */
    private static String getConfigFilePath() {
        if (configDirectory != null && !configDirectory.isEmpty()) {
            return Paths.get(configDirectory, CONFIG_FILENAME).toString();
        } else {
            return CONFIG_FILENAME;
        }
    }
    
    /**
     * Obtiene la ruta completa al archivo de backup
     */
    private static String getBackupFilePath() {
        if (configDirectory != null && !configDirectory.isEmpty()) {
            return Paths.get(configDirectory, CONFIG_BACKUP_FILENAME).toString();
        } else {
            return CONFIG_BACKUP_FILENAME;
        }
    }
    
    /**
     * Guarda la configuración en un archivo JSON
     * 
     * @param password La contraseña para proteger la aplicación
     * @param rutaArchivo La ruta donde se guardan los archivos
     * @return true si se guardó correctamente, false en caso contrario
     */
    public static boolean guardarConfiguracion(String password, String rutaArchivo) {
        try {
            // Actualizar el directorio de configuración con la nueva ruta (creará subcarpeta sentinel)
            setConfigDirectory(rutaArchivo);
            
            String configPath = getConfigFilePath();
            String backupPath = getBackupFilePath();
            
            // Primero crear una copia de seguridad si existe el archivo
            File configFile = new File(configPath);
            if (configFile.exists()) {
                Files.copy(Paths.get(configPath), Paths.get(backupPath), StandardCopyOption.REPLACE_EXISTING);
            }
            
            // Crear el objeto para el JSON
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            
            // Guardar la ruta en texto plano
            rootNode.put("ruta", rutaArchivo);
            
            // Hashear la contraseña antes de guardarla (nunca guardar contraseñas en texto plano)
            String hashedPassword = hashPassword(password);
            rootNode.put("passwd", hashedPassword); // Corregido de "password" a "passwd" para mantener compatibilidad
            
            // Guardar en el archivo
            mapper.writerWithDefaultPrettyPrinter().writeValue(configFile, rootNode);
            
            // Verificar que se haya guardado correctamente
            if (!configFile.exists() || configFile.length() == 0) {
                mostrarAlerta(AlertType.ERROR, "Error al guardar", "No se pudo guardar la configuración");
                return false;
            }
            
            System.out.println("Configuración guardada en: " + configPath);
            return true;
        } catch (Exception e) {
            System.err.println("Error al guardar configuración: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Error", "Error al guardar la configuración: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Carga la configuración desde el archivo JSON
     * 
     * @return Un objeto passRuta con la configuración, o null si hay error
     */
    public static passRuta cargarConfiguracion() {
        try {
            // Primero intentar cargar desde el directorio actual (para compatibilidad con versiones anteriores)
            String defaultConfigPath = CONFIG_FILENAME;
            File defaultConfigFile = new File(defaultConfigPath);
            
            if (defaultConfigFile.exists()) {
                // Cargar configuración del archivo predeterminado
                passRuta config = cargarConfiguracionDesdeArchivo(defaultConfigPath);
                if (config != null) {
                    // Actualizar el directorio de configuración con la ruta cargada
                    setConfigDirectory(config.getRuta());
                }
                return config;
            }
            
            // Si no existe en la ubicación predeterminada, intentar buscar en el último directorio conocido
            if (configDirectory != null && !configDirectory.isEmpty()) {
                String configPath = getConfigFilePath();
                File configFile = new File(configPath);
                
                if (configFile.exists()) {
                    return cargarConfiguracionDesdeArchivo(configPath);
                }
                
                // Intentar usar el backup en la ruta configurada
                String backupPath = getBackupFilePath();
                File backupFile = new File(backupPath);
                
                if (backupFile.exists()) {
                    Files.copy(Paths.get(backupPath), Paths.get(configPath), StandardCopyOption.REPLACE_EXISTING);
                    return cargarConfiguracionDesdeArchivo(configPath);
                }
            }
            
            // Intentar buscar en una carpeta sentinel dentro de la ubicación predeterminada del usuario
            // (para compatibilidad con versiones anteriores en caso de que el usuario haya movido el archivo)
            File userHome = new File(System.getProperty("user.home"));
            if (userHome.exists()) {
                String homeConfigPath = Paths.get(userHome.getAbsolutePath(), SENTINEL_SUBFOLDER, CONFIG_FILENAME).toString();
                File homeConfigFile = new File(homeConfigPath);
                
                if (homeConfigFile.exists()) {
                    return cargarConfiguracionDesdeArchivo(homeConfigPath);
                }
            }
            
            return null;
            
        } catch (Exception e) {
            System.err.println("Error al cargar configuración: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Error", "Error al cargar la configuración: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Carga la configuración desde una ruta específica
     */
    private static passRuta cargarConfiguracionDesdeArchivo(String filePath) {
        try {
            // Leer el archivo
            String jsonContent = Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonContent);
            
            // Extraer los valores
            String rutaGuardada = rootNode.has("ruta") ? rootNode.get("ruta").asText() : "";
            // Buscar primero con el nombre "passwd", si no existe buscar con "password" para compatibilidad
            String passwordHash = rootNode.has("passwd") ? rootNode.get("passwd").asText() : 
                                 (rootNode.has("password") ? rootNode.get("password").asText() : "");
            
            // Crear y retornar el objeto
            return new passRuta(passwordHash, rutaGuardada);
            
        } catch (Exception e) {
            System.err.println("Error al cargar configuración desde archivo: " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Verifica si la contraseña proporcionada coincide con la almacenada
     * 
     * @param inputPassword La contraseña introducida por el usuario
     * @return true si la contraseña es correcta, false en caso contrario
     */
    public static boolean verificarPassword(String inputPassword) {
        try {
            passRuta config = cargarConfiguracion();
            if (config == null || config.getPasswd() == null || config.getPasswd().isEmpty()) {
                return false;
            }
            
            String storedHashedPassword = config.getPasswd();
            String hashedInput = hashPassword(inputPassword);
            
            return hashedInput.equals(storedHashedPassword);
            
        } catch (Exception e) {
            System.err.println("Error al verificar contraseña: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene la ruta guardada en la configuración
     * 
     * @return La ruta o una cadena vacía si hay error
     */
    public static String obtenerRuta() {
        try {
            passRuta config = cargarConfiguracion();
            if (config == null) {
                return "";
            }
            
            return config.getRuta();
            
        } catch (Exception e) {
            System.err.println("Error al obtener ruta: " + e.getMessage());
            return "";
        }
    }
    
    /**
     * Marca la configuración como corrupta para que se elimine en el próximo inicio
     */
    public static void marcarConfiguracionComoCorrupta() {
        try {
            new File(CONFIG_CORRUPTED_MARKER).createNewFile();
        } catch (Exception e) {
            System.err.println("Error al marcar configuración como corrupta: " + e.getMessage());
        }
    }
    
    /**
     * Hashea una contraseña usando SHA-256
     */
    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error al hashear contraseña: " + e.getMessage());
            return password; // En caso de error, usar la contraseña sin hashear (no es lo ideal)
        }
    }
    
    /**
     * Muestra un cuadro de diálogo de alerta de manera segura
     * Este método está diseñado para ser seguro tanto desde el hilo de la UI como desde otros hilos
     */
    private static void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        try {
            if (Platform.isFxApplicationThread()) {
                mostrarAlertaDirecta(tipo, titulo, mensaje);
            } else {
                // Si no estamos en el hilo de aplicación de JavaFX, debemos usar Platform.runLater
                Platform.runLater(() -> mostrarAlertaDirecta(tipo, titulo, mensaje));
            }
        } catch (Exception e) {
            // Si hay un error (por ejemplo, si JavaFX no está inicializado), simplemente registramos en la consola
            System.err.println("Error al mostrar alerta: " + e.getMessage());
            System.err.println(titulo + ": " + mensaje);
        }
    }
    
    private static void mostrarAlertaDirecta(AlertType tipo, String titulo, String mensaje) {
        try {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Error al mostrar alerta directamente: " + e.getMessage());
            System.err.println(titulo + ": " + mensaje);
        }
    }
}