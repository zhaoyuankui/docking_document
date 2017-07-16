package com.gumilicai.platform.entity;

/**
 * Created by gumi on 2016/5/9.
 */
public abstract class SaltInfo {
    //用于鉴权校验,该账户的8位长度密钥
    private  String salt;

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        return "SaltInfo{" +
                "salt='" + salt + '\'' +
                '}';
    }
}
