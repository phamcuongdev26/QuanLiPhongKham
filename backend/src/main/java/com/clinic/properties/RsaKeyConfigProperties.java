package com.clinic.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@Getter
public class RsaKeyConfigProperties {

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    public RsaKeyConfigProperties(
            @Value("${app.jwt.public-key-path:classpath:keys/public.pem}") Resource publicKeyResource,
            @Value("${app.jwt.private-key-path:classpath:keys/private.pem}") Resource privateKeyResource
    ) throws Exception {
        if (publicKeyResource.exists() && privateKeyResource.exists()) {
            this.publicKey = parsePublicKey(new String(publicKeyResource.getInputStream().readAllBytes()));
            this.privateKey = parsePrivateKey(new String(privateKeyResource.getInputStream().readAllBytes()));
            return;
        }

        KeyPair keyPair = generateDevelopmentKeyPair();
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
    }

    private KeyPair generateDevelopmentKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    private RSAPublicKey parsePublicKey(String pem) throws Exception {
        String clean = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        return (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(clean)));
    }

    private RSAPrivateKey parsePrivateKey(String pem) throws Exception {
        String clean = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(clean)));
    }
}
