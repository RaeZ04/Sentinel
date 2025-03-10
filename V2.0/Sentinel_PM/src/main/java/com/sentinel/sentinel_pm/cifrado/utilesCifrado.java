package com.sentinel.sentinel_pm.cifrado;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;

public class utilesCifrado {

    private static final String ALGORITHM = "AES";
    private static final String KEY_FILE = "secret.key";

    public static byte[] encryptFile(String filePath) throws Exception {
        SecretKeySpec secretKey = loadOrGenerateKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] inputBytes = Files.readAllBytes(Paths.get(filePath));
        byte[] outputBytes = cipher.doFinal(inputBytes);

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(outputBytes);
        }

        return outputBytes;
    }

    public static byte[] decryptFile(String filePath) throws Exception {
        SecretKeySpec secretKey = loadOrGenerateKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] inputBytes = Files.readAllBytes(Paths.get(filePath));
        return cipher.doFinal(inputBytes);
    }

    private static SecretKeySpec loadOrGenerateKey() throws Exception {
        File keyFile = new File(KEY_FILE);
        if (keyFile.exists()) {
            return loadKey();
        } else {
            return generateKey();
        }
    }

    private static SecretKeySpec generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        SecureRandom secureRandom = new SecureRandom();
        keyGen.init(128, secureRandom);
        SecretKey secretKey = keyGen.generateKey();
        byte[] encodedKey = secretKey.getEncoded();

        // Guardar la clave en un archivo
        try (FileOutputStream keyOutputStream = new FileOutputStream(KEY_FILE)) {
            keyOutputStream.write(encodedKey);
        }

        return new SecretKeySpec(encodedKey, ALGORITHM);
    }

    private static SecretKeySpec loadKey() throws Exception {
        byte[] encodedKey = Files.readAllBytes(Paths.get(KEY_FILE));
        return new SecretKeySpec(encodedKey, ALGORITHM);
    }

    public static String obtenerRutaJSON() {
        try {
            // Leer la ruta desde el archivo JSON
            String jsonFilePath = "config.json";
            File jsonFile = new File(jsonFilePath);

            if (jsonFile.exists()) {
                // Desencriptar JSON
                byte[] decryptedBytes = decryptFile(jsonFilePath);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(decryptedBytes);
                return jsonNode.get("ruta").asText();
            }

            return "";

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}