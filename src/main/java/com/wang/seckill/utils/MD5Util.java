package com.wang.seckill.utils;

import org.springframework.stereotype.Component;
import org.apache.commons.codec.digest.DigestUtils;


@Component
public class MD5Util {


    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String salt="1a2b3c4d";

    public static String inputPassToFormPass(String inputPass){
        String s = salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(s);
    }

    public static String formPassToDBPass(String formPass, String salt){
        String s = salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(s);
    }

    public static String inputPassToDBPass(String inputPass, String salt){
        String s = inputPassToFormPass(inputPass);
        String DBPass = formPassToDBPass(s, salt);
        return DBPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPassToDBPass("18256224745", "hahahaha"));
    }

}
