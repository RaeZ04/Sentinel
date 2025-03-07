package com.sentinel.sentinel_pm.cifrado;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class descifrar {

          private static final String ALGORITHM = "AES";
          private static final String TRANSFORMATION = "AES";

          public static void decrypt(String key, String inputFile, String outputFile) throws Exception {
                    SecretKey secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
                    Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                    cipher.init(Cipher.DECRYPT_MODE, secretKey);

                    try (FileInputStream fis = new FileInputStream(inputFile);
                               CipherInputStream cis = new CipherInputStream(fis, cipher);
                               FileOutputStream fos = new FileOutputStream(outputFile)) {

                              byte[] buffer = new byte[1024];
                              int bytesRead;
                              while ((bytesRead = cis.read(buffer)) != -1) {
                                        fos.write(buffer, 0, bytesRead);
                              }
                    } catch (IOException e) {
                              throw new IOException("Error while decrypting the file", e);
                    }
          }

          public static void main(String[] args) {
                    if (args.length != 3) {
                              System.out.println("Usage: java Descifrar <key> <inputFile> <outputFile>");
                              return;
                    }

                    String key = args[0];
                    String inputFile = args[1];
                    String outputFile = args[2];

                    try {
                              decrypt(key, inputFile, outputFile);
                              System.out.println("File decrypted successfully");
                    } catch (Exception e) {
                              System.err.println("Error while decrypting the file: " + e.getMessage());
                    }
          }
}
