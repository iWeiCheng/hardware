package com.example.hardware;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SystemUtils {
    /**
     *   ANDROID_ID(恢复出厂+刷机会变) + 序列号(android 10会unknown/android 9需要设备权限)+品牌    +机型
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getUniqueIdentificationCode(Context context){
        String androidId =  Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String uniqueCode ;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            /** 需要权限 且仅适用9.0。 10.0后又不能获取了*/
            uniqueCode = androidId + Build.getSerial()+Build.BRAND+ Build.MODEL;
        }else{
            uniqueCode = androidId + Build.SERIAL+Build.BRAND+ Build.MODEL;
        }
      return toMD5(uniqueCode);
    }
 
    /**
     * MD5加密 格式一致
     */
    private static String toMD5(String text){                                                                                  
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] digest = messageDigest.digest(text.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            int digestInt = digest[i] & 0xff;
            //将10进制转化为较短的16进制
            String hexString = Integer.toHexString(digestInt);
            if (hexString.length() < 2) {
                sb.append(0);
            }
            sb.append(hexString);
        }
        return sb.toString().substring(8,24);
    }
}