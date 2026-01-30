package com.gwangjin.callcenterwas.common.util;

import javax.crypto.Cipher;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class SecurityUtil {

    public static String encryptOAEP(char[] plainPin, String publicKeyStr) throws Exception {
        byte[] publicBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        // Convert char[] to byte[] safely
        java.nio.ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(plainPin));
        byte[] pinBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(pinBytes);

        try {
            byte[] encryptedBytes = cipher.doFinal(pinBytes);
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } finally {
            // Clean up temporary byte buffer
            Arrays.fill(pinBytes, (byte) 0);
        }
    }

    public static void wipe(char[] sensitiveData) {
        if (sensitiveData != null) {
            Arrays.fill(sensitiveData, '0');
        }
    }
}
