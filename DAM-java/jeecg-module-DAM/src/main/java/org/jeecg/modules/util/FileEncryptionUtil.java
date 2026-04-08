package org.jeecg.modules.util;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * 文件加密解密工具类，支持MultipartFile类型
 */
public class FileEncryptionUtil {

    // 加密算法类型
    private static final String ALGORITHM = "AES";
    // 加密模式和填充方式
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /**
     * 加密文件
     *
     * @param file      待加密的文件
     * @param secretKey 加密密钥(长度建议16/24/32位)
     * @return 加密后的文件
     * @throws Exception 加密过程中可能抛出的异常
     */
    public static MultipartFile encryptFile(MultipartFile file, String secretKey) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 获取文件字节数组
        byte[] fileBytes = file.getBytes();

        // 加密字节数组
        byte[] encryptedBytes = encrypt(fileBytes, secretKey);

        // 将加密后的字节数组转换为MultipartFile
        return new MockMultipartFile(
                file.getName(),
                file.getOriginalFilename(),
                file.getContentType(),
                new ByteArrayInputStream(encryptedBytes)
        );
    }

    /**
     * 解密文件
     *
     * @param encryptedFile 加密后的文件
     * @param secretKey     解密密钥(需与加密密钥相同)
     * @return 解密后的原始文件
     * @throws Exception 解密过程中可能抛出的异常
     */
    public static MultipartFile decryptFile(MultipartFile encryptedFile, String secretKey) throws Exception {
        if (encryptedFile.isEmpty()) {
            throw new IllegalArgumentException("加密文件不能为空");
        }

        // 获取加密文件的字节数组
        byte[] encryptedBytes = encryptedFile.getBytes();

        // 解密字节数组
        byte[] decryptedBytes = decrypt(encryptedBytes, secretKey);

        // 将解密后的字节数组转换为MultipartFile
        return new MockMultipartFile(
                encryptedFile.getName(),
                encryptedFile.getOriginalFilename(),
                encryptedFile.getContentType(),
                new ByteArrayInputStream(decryptedBytes)
        );
    }

    /**
     * 字节数组加密
     *
     * @param data      待加密数据
     * @param secretKey 密钥
     * @return 加密后的数据
     * @throws Exception 加密异常
     */
    private static byte[] encrypt(byte[] data, String secretKey) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(generateKey(secretKey), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(data);
    }

    /**
     * 字节数组解密
     *
     * @param encryptedData 加密后的数据
     * @param secretKey     密钥
     * @return 解密后的数据
     * @throws Exception 解密异常
     */
    private static byte[] decrypt(byte[] encryptedData, String secretKey) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(generateKey(secretKey), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(encryptedData);
    }

    /**
     * 生成符合AES要求的密钥
     * 自动将任意长度密钥转换为128位(16字节)密钥
     *
     * @param secretKey 原始密钥
     * @return 处理后的密钥
     * @throws Exception 异常
     */
    private static byte[] generateKey(String secretKey) throws Exception {
        // 使用SHA-256哈希算法处理密钥，确保密钥长度符合要求
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(secretKey.getBytes("UTF-8"));

        // 截取前16字节作为AES-128密钥
        byte[] key = new byte[16];
        System.arraycopy(keyBytes, 0, key, 0, 16);
        return key;
    }

    /**
     * 生成随机密钥(16位)
     *
     * @return 随机密钥
     */
    public static String generateRandomKey() {
        byte[] key = new byte[16];
        new java.security.SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
}
