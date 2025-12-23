package com.pear.shop;


import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.io.FileOutputStream;
import java.security.KeyPair;
import java.util.Base64;

public class KeyMaker {
    public static void main(String[] args) throws Exception {
        // 1. RSA 256 키 쌍 생성
        KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);

        // 2. privateKey 저장
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        String privateKeyStr = Base64.getEncoder().encodeToString(privateKeyBytes);

        try(FileOutputStream fos = new FileOutputStream("private.key")){
            fos.write(privateKeyStr.getBytes());
        }
        System.out.println("개인키 저장 완료: private.key");

        // 3. publicKey 저장
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        String publicKeyStr = Base64.getEncoder().encodeToString(publicKeyBytes);

        try(FileOutputStream fos = new FileOutputStream("public.key")){
            fos.write(publicKeyStr.getBytes());
        }
        System.out.println("공개키 저장 완료: public.key");
    }
}

