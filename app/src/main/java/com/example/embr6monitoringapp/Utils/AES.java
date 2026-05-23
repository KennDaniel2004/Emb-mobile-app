package com.example.embr6monitoringapp.Utils;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AES {

    private static final String AES_SECRET         = "EMBR6App@SecureK32BytesKey!!2024";
    private static final String AES_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String ALGORITHM          = "AES";
    private static final int    IV_LENGTH          = 16;

    private AES() {}


    public static String encrypt(String plainText) {
        if (plainText == null) throw new IllegalArgumentException("plainText must not be null");

        try {
            byte[]    iv     = generateIv();
            SecretKey key    = buildKey();
            Cipher    cipher = Cipher.getInstance(AES_TRANSFORMATION);

            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

            byte[] encryptedBytes = cipher.doFinal(
                    plainText.getBytes(StandardCharsets.UTF_8)
            );

            String ivBase64         = Base64.encodeToString(iv,             Base64.NO_WRAP);
            String cipherTextBase64 = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);

            return ivBase64 + ":" + cipherTextBase64;

        } catch (Exception e) {
            throw new RuntimeException("AESUtils.encrypt failed: " + e.getMessage(), e);
        }
    }


    public static String decrypt(String encryptedText) {
        if (encryptedText == null) throw new IllegalArgumentException("encryptedText must not be null");

        String[] parts = encryptedText.split(":", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "AESUtils.decrypt: invalid format — expected 'BASE64_IV:BASE64_CIPHERTEXT'"
            );
        }

        try {
            byte[]    iv             = Base64.decode(parts[0], Base64.NO_WRAP);
            byte[]    encryptedBytes = Base64.decode(parts[1], Base64.NO_WRAP);
            SecretKey key            = buildKey();
            Cipher    cipher         = Cipher.getInstance(AES_TRANSFORMATION);

            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("AESUtils.decrypt failed: " + e.getMessage(), e);
        }
    }

    private static SecretKey buildKey() {
        byte[] keyBytes = AES_SECRET.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
    private static byte[] generateIv() {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
}