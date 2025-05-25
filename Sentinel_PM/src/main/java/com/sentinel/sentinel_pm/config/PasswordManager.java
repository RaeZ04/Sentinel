package com.sentinel.sentinel_pm.config;

/**
 * Clase para manejar la contraseña maestra en memoria durante la ejecución de la aplicación.
 * Esto permite tener acceso a la contraseña para operaciones de cifrado/descifrado
 * sin necesidad de pedirla constantemente al usuario.
 */
public class PasswordManager {
    
    private static String masterPassword = null;
    
    /**
     * Establece la contraseña maestra en memoria
     * 
     * @param password La contraseña maestra
     */
    public static void setMasterPassword(String password) {
        masterPassword = password;
    }
    
    /**
     * Obtiene la contraseña maestra actual
     * 
     * @return La contraseña maestra o null si no ha sido establecida
     */
    public static String getMasterPassword() {
        return masterPassword;
    }
    
    /**
     * Limpia la contraseña maestra de la memoria
     * Llamar a este método al cerrar la aplicación o en operaciones de logout
     */
    public static void clearPassword() {
        masterPassword = null;
    }
}
