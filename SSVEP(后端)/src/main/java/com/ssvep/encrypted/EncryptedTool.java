package com.ssvep.encrypted;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptedTool {
    //生成AES密钥
    public static byte[] generateAESKeys(){
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            return secretKey.getEncoded();
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
    }

    //生成初始化IV向量
    public static byte[] generateIV(){
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        return iv;
    }

    //使用主密钥加密AES密钥
    public static byte[] encryptAesKey(byte[] aesKey){
        try {
            String mainKey = System.getenv("MAIN_KEY");
            if (mainKey == null || mainKey.isEmpty()) {
                throw new IllegalArgumentException("MAIN_KEY is not set or empty.");
            }
            byte[] mainKeyBytes = Base64.getDecoder().decode(mainKey);
            SecretKeySpec mainSpec = new SecretKeySpec(mainKeyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE,mainSpec);
            return cipher.doFinal(aesKey);
        }catch(IllegalArgumentException e){
            System.err.println(e.getMessage());
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //使用主密钥解密AES密钥
    public static byte[] decryptAesKey(byte[] encryptedAesKey) {
        try {
            String mainKey = System.getenv("MAIN_KEY");
            if (mainKey == null || mainKey.isEmpty()) {
                throw new IllegalArgumentException("MAIN_KEY is not set or empty.");
            }
            byte[] mainKeyBytes = Base64.getDecoder().decode(mainKey);
            SecretKeySpec mainSpec = new SecretKeySpec(mainKeyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, mainSpec);
            return cipher.doFinal(encryptedAesKey);
        }catch(IllegalArgumentException e){
            System.err.println(e.getMessage());
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //使用AES密钥和IV向量加密数据
    public static byte[] encryptData(byte[] aesKey,byte[] iv,String data){
        try {
            if (data == null || data.isEmpty()) {
                throw new IllegalArgumentException("Data must not be null or empty");
            }
            if (aesKey == null || aesKey.length == 0) {
                throw new IllegalArgumentException("Key must not be null or empty");
            }
            SecretKeySpec aesSpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, aesSpec, ivSpec);
            return cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //使用AES密钥和IV向量解密数据
    public static String decryptData(byte[] aesKey,byte[] iv,byte[] data){
        if (aesKey == null || aesKey.length != 32) {
            throw new IllegalArgumentException("AES key must be a 16-byte array");
        }
        if (iv == null || iv.length != 16) {
            throw new IllegalArgumentException("IV must be a 16-byte array");
        }
        if (data == null || data.length == 0) {
            return "";
        }
        try {
            SecretKeySpec aesSpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, aesSpec, ivSpec);
            return new String(cipher.doFinal(data), StandardCharsets.UTF_8);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}