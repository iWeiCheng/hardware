package com.example.hardware;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.google.common.io.Files;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HardInfoUtil {
    public static boolean createNewHardwareFile() {
//        File file = new File(Environment.getExternalStorageDirectory() + "/DevInfoAll.dat");
//        if (file.exists()) {
//
//            return true;
//        }
        String strResult = WebApiCall.request2String("http://hdinfo.opsdns.cn:8081/Web_HardwareInfo.asmx/Interface_Query?parameters_json=&jkid=01H00A01");
        if (TextUtils.isEmpty(strResult)) {
            return false;
        }
        try {
            JSONObject jsonObject = new JSONObject(strResult);
            int code = jsonObject.optInt("code");
            if (code == 1) {
                if (strResult.contains("\\\\\\\"")) {
                    strResult = strResult.replace("\\\\\\\"", "\\\"");
                   Log.i("tag","修复DevInfoAll硬件信息中的三个反斜杠.");
                }
                return writeDevInfoAll(strResult);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("tag","转换硬件信息失败11！");
        }
        return false;

    }

    /**
     * 写入DevInfoAll.dat
     *
     * @param content
     * @return
     */
    public static boolean writeDevInfoAll(String content) {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/DevInfoAll.dat");
            if (!file.exists()) {
                file.createNewFile();
            }
            Files.write(content.getBytes(), file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}