package com.sentinel.sentinel_pm.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utilidades de cifrado para Sentinel Password Manager.
 * Esta clase proporciona métodos para cifrar y descifrar datos.
 */
public class CryptoUtils {
    
    /**
     * Cifra un texto usando SHA-256 y lo codifica en Base64
     * 
     * @param text Texto a cifrar
     * @return El texto cifrado en formato Base64, o el texto original si ocurre un error
     */
    public static String encrypt(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error al cifrar texto: " + e.getMessage());
            return text; // En caso de error, devolver el texto original
        }
    }
    
    /**
     * Cifra un texto usando un algoritmo reversible (XOR)
     * Nota: Este método es seguro solo si la clave es robusta y se mantiene en secreto
     * 
     * @param text Texto a cifrar
     * @param key Clave para cifrar (se usa la contraseña maestra)
     * @return Texto cifrado en formato Base64
     */
    public static String encryptReversible(String text, String key) {
        if (text == null || key == null) {
            return text;
        }
        
        try {
            // Generar una clave derivada de la contraseña usando SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyHash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
            
            // XOR del texto con la clave hash (circular)
            byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
            byte[] result = new byte[textBytes.length];
            
            for (int i = 0; i < textBytes.length; i++) {
                result[i] = (byte) (textBytes[i] ^ keyHash[i % keyHash.length]);
            }
            
            // Codificar en Base64 para almacenamiento seguro
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            System.err.println("Error al cifrar texto reversible: " + e.getMessage());
            return text;
        }
    }
    
    /**
     * Descifra un texto que fue cifrado con encryptReversible
     * 
     * @param encryptedText Texto cifrado en formato Base64
     * @param key Clave para descifrar (debe ser la misma que se usó para cifrar)
     * @return Texto descifrado original
     */
    public static String decryptReversible(String encryptedText, String key) {
        if (encryptedText == null || key == null) {
            return encryptedText;
        }
        
        try {
            // Decodificar el texto cifrado de Base64
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            
            // Generar la misma clave derivada
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyHash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
            
            // XOR inverso (es simétrico)
            byte[] result = new byte[encryptedBytes.length];
            for (int i = 0; i < encryptedBytes.length; i++) {
                result[i] = (byte) (encryptedBytes[i] ^ keyHash[i % keyHash.length]);
            }
            
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Error al descifrar texto: " + e.getMessage());
            return encryptedText; // En caso de error devolver el texto cifrado
        }
    }
}
