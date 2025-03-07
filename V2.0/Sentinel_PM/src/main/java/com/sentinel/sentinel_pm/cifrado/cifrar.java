package com.sentinel.sentinel_pm.cifrado;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;


public class cifrar {

          private static final String ALGORITHM = "AES";

          public static void encryptFile(String inputFilePath, String outputFilePath, String key) throws Exception {
                    SecretKeySpec secretKey = generateKey(key);
                    Cipher cipher = Cipher.getInstance(ALGORITHM);
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey);

                    byte[] inputBytes = Files.readAllBytes(Paths.get(inputFilePath));
                    byte[] outputBytes = cipher.doFinal(inputBytes);

                    try (FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {
                              outputStream.write(outputBytes);
                    }
          }

          private static SecretKeySpec generateKey(String key) throws Exception {
                    byte[] keyBytes = key.getBytes("UTF-8");
                    KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
                    SecureRandom secureRandom = new SecureRandom(keyBytes);
                    keyGen.init(128, secureRandom);
                    SecretKey secretKey = keyGen.generateKey();
                    return new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);
          }

          public static void main(String[] args) {
                    try {
                              String key = "1234567890123456"; // Clave de ejemplo (debe ser de 16 bytes para AES-128)
                              encryptFile("ruta/al/archivo/original.txt", "ruta/al/archivo/encriptado.txt", key);
                              System.out.println("Archivo encriptado exitosamente.");
                    } catch (Exception e) {
                              e.printStackTrace();
                    }
          }
}
