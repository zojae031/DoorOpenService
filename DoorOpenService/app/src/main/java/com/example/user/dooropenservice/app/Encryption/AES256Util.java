package com.example.user.dooropenservice.app.Encryption;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES256Util {
    // commans-codec-1.9.jar 추가해야함
    private String iv;
    private Key keySpec;

    public AES256Util(String key) throws UnsupportedEncodingException {
        this.iv = key.substring(0, 16);   // 0~15 index의

        byte[] keyBytes = new byte[16];   // 16 bytes 배열로 만듦
        byte[] b = key.getBytes("UTF-8");   // 문자열을 bytes로 인코드 해준다
        // 만약 getBytes()의 인자로 캐릭터셋을 넘기지 않으면 사용자 플랫폼의 기본 charset으로 인코딩된다.
        int len = b.length;
        if(len > keyBytes.length)// 키값이 16보다 크다면 key값을 16으로로 만들어줌 (키값의 길이는 16이어야함)
            len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);//지정된 위치로부터 지정된 소스 배열의 배열을, 전송처 배열의 지정된 위치에 카피합니다.
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        // 지정된 바이트 배열로부터 비밀 키(개인 키)를 구축한다.
        // 지정된 바이트가 실제로 지정된 알고리즘의 암호 키를 지정하는지 여부는 확인하지 않는다.
        this.keySpec = keySpec;
    }

    // 암호화
    public String aesEncode(String str) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException{
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // 블록암호화를 진행하기 위해서는 패딩기법이 필요히다.
        // 데이터를 특정크기로 맞추기 위해서, 특정크기보다 부족한 부분의 공간을 의미없는 문자들로 채워서 비트수를 맞추는 것이다. (행렬채움)
        c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
        // 키와 알고리즘 파라미터 세트를 사용해,이 암호를 초기화한다.
        // 암호는 opmode의 값에 따라 암호화에 대해 초기화된다.
        byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
        // 데이터를 암호화작업을 마친다 doFinal은 암호화 하거나 복호화(해독)한다 여기선 암호화
        String enStr = new String(Base64.encodeBase64(encrypted));
        // bytes로 된걸  base64로 인코딩 해서 String 객체 생성

        return enStr;   // 암호화된 값 리턴
    }

    //복호화
    public String aesDecode(String str) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes("UTF-8")));

        byte[] byteStr = Base64.decodeBase64(str.getBytes());
        // 데이터를 암호화작업을 마친다 doFinal은 암호화 하거나 복호화(해독)한다 여기선 복호화
        return new String(c.doFinal(byteStr),"UTF-8");   // 복호화된 값 리턴
    }
}