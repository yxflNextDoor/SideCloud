package com.side.framework.core.utils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * ECC算法工具类
 *
 * @author yxfl
 * @date 2024/04/05 10
 **/
public class ECCKeyUtil {
    private ECCKeyUtil() {}

    /**
     * ECC密钥对算法
     */
    private final static String defaultKeyPairAlgorithm = "EC";

    public static PublicKey base64ToECPublicKey(String encodedPublicKeyBase64) throws Exception {

        // 解码Base64字符串

        byte[] publicKeyBytes = Base64.getDecoder().decode(encodedPublicKeyBase64);


        // 创建X509EncodedKeySpec，用于还原公钥

        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);


        // 创建KeyFactory实例并指定算法为ECDH或ECDSA

        KeyFactory kf = KeyFactory.getInstance(defaultKeyPairAlgorithm);


        // 生成ECPublicKey对象

        PublicKey publicKey = kf.generatePublic(publicKeySpec);


        return (ECPublicKey)publicKey;

    }


    public static PrivateKey base64ToECPrivateKey(String encodedPrivateKeyBase64) throws Exception {

        // 解码Base64字符串

        byte[] privateKeyBytes = Base64.getDecoder().decode(encodedPrivateKeyBase64);


        // 创建PKCS8EncodedKeySpec，用于还原私钥

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);


        // 创建KeyFactory实例并指定算法为ECDH或ECDSA

        KeyFactory kf = KeyFactory.getInstance(defaultKeyPairAlgorithm);


        // 生成ECPrivateKey对象

        PrivateKey privateKey = kf.generatePrivate(privateKeySpec);


        return (ECPrivateKey) privateKey;

    }


}
