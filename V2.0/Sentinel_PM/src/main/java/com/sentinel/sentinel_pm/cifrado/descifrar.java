package com.sentinel.sentinel_pm.cifrado;

import com.sentinel.sentinel_pm.ConfigurationController;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;

public class descifrar {
    String key = ConfigurationController.passwdKey;

    public static void decryptFile(String inputFile, String outputFile) throws Exception {
        byte[] inputBytes = Files.readAllBytes(Paths.get(inputFile));
        byte[] outputBytes = decrypt(inputBytes, key);
        Files.write(Paths.get(outputFile), outputBytes);
    }

    public static byte[] decrypt(byte[] data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

}
