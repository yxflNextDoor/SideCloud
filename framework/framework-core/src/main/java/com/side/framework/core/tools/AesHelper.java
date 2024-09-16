package com.side.framework.core.tools;

import com.side.framework.core.constants.CodeEnum;
import com.side.framework.core.exception.BasicException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author yxfl
 * @date 2024/04/07 22
 **/
@Slf4j
public class AesHelper {
    private AesHelper() {
    }

    // 默认的AES密钥长度
    private static final int KEY_SIZE_BITS = 256;

    private static final String ALGORITHM = "AES";

    private volatile static String secretKey = "1570c2c3a08a41fc9b1c73633b5967fd";

    private static Key generateKey(String secretKey) throws Exception {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        // 若密钥不足256位（或自定义的KEY_SIZE_BITS），则使用下面的代码进行补足
        keyBytes = Arrays.copyOf(keyBytes, KEY_SIZE_BITS / Byte.SIZE);
        return new SecretKeySpec(keyBytes, ALGORITHM);

    }


    /**
     * 加密方法
     */
    public static String encrypt(String plainText) {

        try {
            Key key = generateKey(secretKey);

            Cipher cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new BasicException(CodeEnum.HELPER_ERROR, e.getMessage());
        }

    }


    /**
     * 解密方法
     */
    public static String decrypt(String encryptedText) {
        try {
            Key key = generateKey(secretKey);

            Cipher cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);

            byte[] decryptedBytes = cipher.doFinal(decodedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new BasicException(CodeEnum.HELPER_ERROR, e.getMessage());
        }
    }

    /**
     * 尝试解密，失败返回原文
     *
     * @param encryptedText
     * @return
     */
    public static String tryDecrypt(String encryptedText) {
        try {
            return decrypt(encryptedText);
        } catch (Exception e) {
            log.info("tryDecrypt fail，msg:{}", e.getMessage());
        }
        return encryptedText;
    }

    /**
     * 设置密钥
     */
    public static void setSecretKey(String secretKey) {
        AesHelper.secretKey = secretKey;
    }
}
