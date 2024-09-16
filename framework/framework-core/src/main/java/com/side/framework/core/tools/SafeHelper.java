package com.side.framework.core.tools;


import com.side.framework.core.utils.ECCKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Set;

/**
 * 安全工具类，提供加解密
 * todo 线程安全性和性能未验证
 *
 * @author yxfl
 * @date 2024/04/04 10
 **/
@Slf4j
public class SafeHelper {
    private static final String defaultKey = "SafeHelper";

    private static final String defaultAlgorithm = "SHA3-384";

    /**
     * jdk内置支持摘要算法
     */
    private static final Set<String> jdkAlgorithms = Security.getAlgorithms("MessageDigest");

    /**
     * 公钥
     */
    private static volatile String defaultPublicKeyStr = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE1DME8A8pgg0OY5AOQ2IBxe5nQLiOwr7n6+XUQagpmkLKFgHhHZzU77J+EiDmqDIrJfnrM1TE5+/Uzh7HvakBbg==";

    /**
     * 私钥
     */
    private static volatile String defaultPrivateKeyStr = "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgMaLKWVkHOmDLXgacWbIhZIaZTaKDh84YjYdThY+F5ImgCgYIKoZIzj0DAQehRANCAATUMwTwDymCDQ5jkA5DYgHF7mdAuI7Cvufr5dRBqCmaQsoWAeEdnNTvsn4SIOaoMisl+eszVMTn79TOHse9qQFu";


    /**
     * 默认Cipher算法
     * 基于ECC的加密方案
     */
    private final static String defaultTransformation = "ECIES";

    /**
     * 默认Cipher构造函数
     */
    private final static String defaultProvider = "BC";

    /**
     * 默认密钥对算法
     */
    public final static String defaultKeyPairAlgorithm = "EC";

    /**
     * 默认密钥对构造函数
     */
    private final static String defaultKeyPairProvider = "BC";

    /**
     * 默认密钥对参数(ECC)
     * 超过256位的安全椭圆曲线（"sec" 表示 SEC，Secure Elliptic Curve；"p" 表示素数域；"256" 表示字段大小；"r1" 表示曲线的第一个实现）
     * SHA256withECDSA
     * secp256r1
     */
    private final static String defaultKeyPairParameter = "secp256r1";

    /**
     * 公钥type
     */
    public static final Integer PUBLIC_KEY = Cipher.PUBLIC_KEY;

    /**
     * 私钥type
     */
    public static final Integer PRIVATE_KEY = Cipher.PRIVATE_KEY;


    /**
     * 默认实例
     */
    private static SafeHelper defaultInstance;

    /**
     * 重新生成默认的keyPair
     * 等效安全强度：
     * RSA 密钥大小(bits)	ECC 密钥大小 (bits)
     * 1024	                160
     * 2048	                224
     * 3072	                256
     * 7680	                384
     * 15360	            521
     *
     * @return 密钥对
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair regenerateDefaultTypeKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(defaultKeyPairAlgorithm, defaultKeyPairProvider);
        generator.initialize(new ECGenParameterSpec(defaultKeyPairParameter));
        KeyPair keyPair = generator.generateKeyPair();
        return keyPair;
    }

    static {
        // 添加BouncyCastle作为安全提供商
        Security.addProvider(new BouncyCastleProvider());
        try {
            Cipher instance = Cipher.getInstance(defaultTransformation, defaultProvider);
            KeyPair defaultKeyPair = new KeyPair(
                    ECCKeyUtil.base64ToECPublicKey(defaultPublicKeyStr), ECCKeyUtil.base64ToECPrivateKey(defaultPrivateKeyStr));
            defaultInstance = new SafeHelper(defaultAlgorithm, instance, defaultKeyPair);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("SafeHelper Initialization failure!");
        }
    }

    public static SafeHelper getDefaultInstance() {
        return defaultInstance;
    }

    /**
     * 获取默认公钥字符串
     * @return
     */
    public static String getDefaultPublicKeyStr() {
        return Base64.getEncoder().encodeToString(getDefaultInstance().keyPair.getPublic().getEncoded());
    }

    /**
     * 重新设置默认密钥对
     *
     * @param publicKeyStr
     * @param privateKeyStr
     * @throws Exception
     */
    public static void reloadDefaultInstance(String publicKeyStr, String privateKeyStr) throws Exception {
        synchronized (SafeHelper.class) {
            try {
                if (publicKeyStr == null || privateKeyStr == null) {
                    KeyPair defaultKeyPair = new KeyPair(
                            ECCKeyUtil.base64ToECPublicKey(publicKeyStr), ECCKeyUtil.base64ToECPrivateKey(privateKeyStr));
                    SafeHelper newSageHelper = new SafeHelper(defaultAlgorithm, defaultInstance.cipher, defaultKeyPair);
                    defaultInstance = newSageHelper;
                }
            } catch (Exception e) {
                log.error("reloadDefaultInstance error", e.getMessage());
            }
        }
    }

    /**
     * 摘要器
     */
    private MessageDigest messageDigest;

    /**
     * 加解密器
     */
    private Cipher cipher;

    /**
     * 密钥对
     */
    private KeyPair keyPair;


    /**
     * @param algorithm
     * @param cipher
     * @throws NoSuchAlgorithmException
     */
    private SafeHelper(String algorithm, Cipher cipher, KeyPair keyPair) throws NoSuchAlgorithmException {
        this.messageDigest = MessageDigest.getInstance(algorithm);
        this.cipher = cipher;
        this.keyPair = keyPair;
    }


    public synchronized String digestUTF8ToBase64(String data) {
        return digestToBase64(data, StandardCharsets.UTF_8);
    }

    /**
     * 使用指定的字符集对指定数据进行摘要。
     *
     * @param data    需要摘要的数据。
     * @param charset 数据的字符集。
     * @return 摘要后的数据，如果摘要过程中发生异常，则返回null。
     */
    public synchronized String digestToBase64(String data, Charset charset) {
        byte[] digest = messageDigest.digest(data.getBytes(charset));
        return Base64.getEncoder().encodeToString(digest);
    }


    public synchronized String encryptUTF8(String data) {
        return encrypt(data, StandardCharsets.UTF_8);
    }

    /**
     * 使用密钥对中的公钥对指定数据进行加密。
     *
     * @param data    需要加密的数据。
     * @param charset 数据的字符集。
     * @return 加密后的数据，如果加密过程中发生异常，则返回null。
     */
    public synchronized String encrypt(String data, Charset charset) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            byte[] bytes = cipher.doFinal(data.getBytes(charset));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.error("encryptUTF8 fail! msg:{}", e.getMessage());
        }
        return null;
    }

    public synchronized String decryptUTF8(String data) {
        return decrypt(data, StandardCharsets.UTF_8);
    }

    /**
     * 使用密钥对中的私钥对指定数据进行解密。
     *
     * @param data    需要解密的数据。
     * @param charset 数据的字符集。
     * @return 解密后的数据，如果解密过程中发生异常，则返回null。
     */
    public synchronized String decrypt(String data, Charset charset) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(bytes, charset);
        } catch (Exception e) {
            log.error("encryptUTF8 fail! msg:{}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 密钥加密--用于在不安全的网络中传输密钥
     *
     * @param key
     * @return
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     */
    public synchronized String wrapUTF8(Key key) throws IllegalBlockSizeException, InvalidKeyException {
        preWrap(keyPair.getPublic(), Cipher.WRAP_MODE, cipher);
        byte[] bytes = cipher.wrap(key);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 密钥解密--用于在不安全的网络中传输密钥
     *
     * @param data
     * @param algorithm
     * @param type
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public synchronized Key unwrapUTF8(String data, String algorithm, int type) throws NoSuchAlgorithmException, InvalidKeyException {
        preWrap(keyPair.getPrivate(), Cipher.UNWRAP_MODE, cipher);
        Key key = cipher.unwrap(Base64.getDecoder().decode(data), algorithm, type);
        return key;
    }

    /**
     * @param key
     * @param mode
     * @param cipher
     * @throws InvalidKeyException
     */
    private synchronized static void preWrap(Key key, Integer mode, Cipher cipher) throws InvalidKeyException {
        //校验
        FuncHelper.preIfConsumer(mode,
                i -> !Arrays.asList(Cipher.WRAP_MODE, Cipher.UNWRAP_MODE).contains(i),
                i -> {
                    log.error("preWrap：不支持的mode参数：{}", i);
                    throw new IllegalArgumentException("不支持的mode参数");
                });
        if (key instanceof PublicKey && mode == Cipher.WRAP_MODE) {
            cipher.init(mode, (PublicKey) key);
            return;
        }
        if (key instanceof PrivateKey && mode == Cipher.UNWRAP_MODE) {
            cipher.init(mode, (PrivateKey) key);
            return;
        }

        throw new IllegalArgumentException("不支持的key类型");
    }

    /**
     * MD5加密
     *
     * @param data
     * @return
     */
    public static String md5UTF8(String data) {
        MD5Digest md5Digest = new MD5Digest();
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        md5Digest.update(bytes, 0, bytes.length);
        byte[] hash = new byte[md5Digest.getDigestSize()];
        md5Digest.doFinal(hash, 0);
        return Base64.getEncoder().encodeToString(hash);
    }
}
