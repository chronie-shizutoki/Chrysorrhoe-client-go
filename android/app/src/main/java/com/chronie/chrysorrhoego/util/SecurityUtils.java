package com.chronie.chrysorrhoego.util;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * 安全工具类，提供数据加密、解密和安全存储功能
 */
public class SecurityUtils {
    private static final String TAG = "SecurityUtils";
    private static final String KEYSTORE_PROVIDER = "AndroidKeyStore";
    private static final String KEY_ALIAS = "ChrysorrhoeGOKey";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    /**
     * 加密敏感数据
     * @param context 应用上下文
     * @param data 要加密的数据
     * @return 加密后的数据（Base64编码）
     */
    public static String encryptData(Context context, String data) {
        try {
            // 获取或创建密钥
            SecretKey secretKey = getOrCreateSecretKey();
            
            // 初始化加密器
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            
            // 执行加密
            byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            // 组合IV和加密数据
            byte[] combinedData = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, combinedData, 0, iv.length);
            System.arraycopy(encryptedData, 0, combinedData, iv.length, encryptedData.length);
            
            // 返回Base64编码的结果
            return Base64.encodeToString(combinedData, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "Error encrypting data", e);
            return null;
        }
    }

    /**
     * 解密敏感数据
     * @param context 应用上下文
     * @param encryptedData 加密的数据（Base64编码）
     * @return 解密后的数据
     */
    public static String decryptData(Context context, String encryptedData) {
        try {
            // 解码Base64数据
            byte[] combinedData = Base64.decode(encryptedData, Base64.DEFAULT);
            
            // 分离IV和加密数据
            byte[] iv = Arrays.copyOfRange(combinedData, 0, GCM_IV_LENGTH);
            byte[] encryptedBytes = Arrays.copyOfRange(combinedData, GCM_IV_LENGTH, combinedData.length);
            
            // 获取密钥
            SecretKey secretKey = getSecretKey();
            if (secretKey == null) {
                return null;
            }
            
            // 初始化解密器
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            
            // 执行解密
            byte[] decryptedData = cipher.doFinal(encryptedBytes);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e(TAG, "Error decrypting data", e);
            return null;
        }
    }

    /**
     * 获取或创建密钥
     */
    private static SecretKey getOrCreateSecretKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
        keyStore.load(null);
        
        // 检查密钥是否已存在
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            // 创建新密钥
            KeyGenerator keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER);
            
            KeyGenParameterSpec keyGenParameterSpec;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setRandomizedEncryptionRequired(true)
                        .setUserAuthenticationRequired(false) // 可以根据需要设置为true
                        .build();
                
                keyGenerator.init(keyGenParameterSpec);
                return keyGenerator.generateKey();
            }
        }
        
        // 获取已存在的密钥
        KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(
                KEY_ALIAS, null);
        return secretKeyEntry.getSecretKey();
    }

    /**
     * 获取已存在的密钥
     */
    private static SecretKey getSecretKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
        keyStore.load(null);
        
        if (keyStore.containsAlias(KEY_ALIAS)) {
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(
                    KEY_ALIAS, null);
            return secretKeyEntry.getSecretKey();
        }
        return null;
    }

    /**
     * 生成安全的随机字符串，用于会话标识符等
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String generateSecureRandomString(int length) {
        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder result = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(charset.length());
            result.append(charset.charAt(index));
        }
        
        return result.toString();
    }

    /**
     * 清除敏感数据（从内存中擦除）
     * @param array 要清除的数组
     */
    public static void clearSensitiveData(byte[] array) {
        if (array != null) {
            Arrays.fill(array, (byte) 0);
        }
    }

    /**
     * 验证输入是否安全（防止SQL注入等）
     * @param input 用户输入
     * @return 是否安全
     */
    public static boolean isInputSafe(String input) {
        if (input == null || input.isEmpty()) {
            return true;
        }
        
        // 简单的SQL注入检测
        String[] dangerousPatterns = {
                "'", "\"", ";", "--", "/*", "*/", "OR 1=1", "DROP TABLE", "INSERT INTO",
                "DELETE FROM", "SELECT * FROM", "EXEC", "xp_"
        };
        
        for (String pattern : dangerousPatterns) {
            if (input.toUpperCase().contains(pattern)) {
                Log.w(TAG, "Potentially unsafe input detected: " + input);
                return false;
            }
        }
        
        return true;
    }
}