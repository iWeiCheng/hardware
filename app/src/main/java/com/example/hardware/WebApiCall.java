package com.example.hardware;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebApiCall implements Runnable {
    public static final int REQUEST_SUCCESS = 200;
    public static final int REQUEST_SUCCESS_IMEI = 202;
    public static final int REQUEST_FAIL = 201;

    public static final int SubmitHardwareInfo = 3;
    public static final int GetAndSetHardwareInfo = 2;
    public static final int GetSelfRealIMEI = 1;
    private static OkHttpClient client;
    private int requestType;
    private Handler requestHandler;
    private JsonObject hardwareRoot;
    private String  PhoneNum="";

    public WebApiCall(int requestType, JsonObject hardwareRoot, Handler handler, String phoneNum) {
        this.requestType = requestType;
        this.requestHandler = handler;
        this.hardwareRoot = hardwareRoot;
        this.PhoneNum = phoneNum;

    }
    public WebApiCall(int requestType, JsonObject hardwareRoot, Handler handler) {
        this.requestType = requestType;
        this.requestHandler = handler;
        this.hardwareRoot = hardwareRoot;
    }

    public void run() {
        String url = "";
        switch (requestType) {
            case SubmitHardwareInfo:
                submitHardwareInfo(hardwareRoot);
                break;
            case GetAndSetHardwareInfo:
                set_build_param();
                break;
            case GetSelfRealIMEI:
                getSelfRealIMEI();
                break;
            default:
                return;
        }
    }

    public void getSelfRealIMEI() {
        String url = "http://127.0.0.1:9999/cmd?getImei=123";
        Message msg = requestHandler.obtainMessage();
        msg.arg1 = requestType;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Call call = client.newCall(request);
            Response response = call.execute();
            if (!response.isSuccessful()) {
                msg.what = REQUEST_FAIL;
            } else {
                msg.what = REQUEST_SUCCESS_IMEI;
                msg.obj = response.body().string();
            }
        } catch (IOException ex) {
            msg.what = REQUEST_FAIL;
        } finally {
            msg.sendToTarget();
        }
    }

    public static OkHttpClient getClient() {
        if(null == client)
        {
            client = new OkHttpClient().newBuilder().build();
        }
        return client;
    }

    //提交硬件信息到服务端
    public void submitHardwareInfo(JsonObject hardwareRoot) {
        String url = "http://terminalapi.hao5xin.com:8099/api/OutSite/AddHardWareInfo?phoneNumber="+PhoneNum;       //服务端接收的信息
        Message msg = requestHandler.obtainMessage();
        msg.arg1 = requestType;
        try {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody requestBody = RequestBody.create(mediaType, hardwareRoot.toString());
            Request request = new Request.Builder().url(url).method("POST", requestBody).build();
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            if (!response.isSuccessful()) {
                Log.i("TAG", "submitHardwareInfo: "+response.code());
                msg.what = REQUEST_FAIL;
            } else {
                msg.what = REQUEST_SUCCESS;
                msg.obj = hardwareRoot.toString();
            }
            response.close();
        } catch (IOException ex) {
            msg.what = REQUEST_FAIL;
           ex.printStackTrace();
        } finally {
            msg.sendToTarget();
        }
    }

    /**
     * 同步请求
     */
    public static String request2String(String url) {
        Request request = new Request.Builder()
                .url(url)
//                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();
        return sendRequest2String(request);
    }

    @SuppressWarnings("all")
    private static String sendRequest2String(Request request) {
        Response response;
        try {
            response = getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                String strRespon = response.body().string();
                return strRespon;
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }
        return null;
    }

    //根据参数设置本地硬件信息
    public void set_build_param() {
        Message msg = requestHandler.obtainMessage();
        msg.arg1 = requestType;
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            //表单数据参数填入
            String url = "http://127.0.0.1:9999/build_param";
            HashMap<String,String> paramsMap=new HashMap<>();
            JsonObject buildInfo = hardwareRoot.getAsJsonObject("buildInfo");

            Set<Map.Entry<String, JsonElement>> level1 = buildInfo.entrySet();
            for(Map.Entry<String, JsonElement>level1Entry:level1){
                if(level1Entry.getValue().isJsonNull()) continue;
                if(level1Entry.getKey().toLowerCase()=="ro.build.date.utc" && level1Entry.getValue().getAsString().length()>10){
                    long  builddate  = level1Entry.getValue().getAsLong()/1000;
                    hardwareRoot.getAsJsonObject("buildInfo").addProperty("ro.build.date.utc",builddate);
                }
                Log.i("hardwareRoot_Get", level1Entry.getKey()+": "+level1Entry.getValue().getAsString());
                paramsMap.put(level1Entry.getKey(),level1Entry.getValue().getAsString());
            }

            FormBody.Builder builder = new FormBody.Builder();
            for (String key : paramsMap.keySet()) {
                //追加表单信息
                builder.add(key, paramsMap.get(key));
                builder.add("ro.product.board", "测试看看");
            }
            RequestBody body = builder.build();
            Request request = new Request.Builder().url(url).post(body).build();
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            if (!response.isSuccessful()) {
                msg.what = REQUEST_FAIL;
            } else {
                msg.what = REQUEST_SUCCESS;
                msg.obj = response.body().string();
            }

            //表单数据参数填入
            String urlOther = "http://127.0.0.1:9999/other_param";
            HashMap<String,String> paramsMapurlOther=new HashMap<>();
            JsonObject buildInfourlOther = hardwareRoot.getAsJsonObject("otherInfo");

            Set<Map.Entry<String, JsonElement>> level2 = buildInfourlOther.entrySet();
            for(Map.Entry<String, JsonElement>level1Entry:level2){
                if(level1Entry.getValue().isJsonNull()) continue;
                if(level1Entry.getValue().isJsonArray()){
                    Log.i("hardwareRoot_Other", level1Entry.getKey()+": "+level1Entry.getValue().getAsJsonArray().toString());
                    paramsMapurlOther.put(level1Entry.getKey(),level1Entry.getValue().getAsJsonArray().toString());
                }
                else{
                    Log.i("hardwareRoot_Other", level1Entry.getKey()+": "+level1Entry.getValue().getAsString());
                    paramsMapurlOther.put(level1Entry.getKey(),level1Entry.getValue().getAsString());
                }
            }

            FormBody.Builder builderOther = new FormBody.Builder();
            for (String key : paramsMapurlOther.keySet()) {
                //追加表单信息
                builderOther.add(key, paramsMapurlOther.get(key));
                builder.add("zdx.tm.SIM_OPERATOR", "测试的");

            }
            RequestBody bodyOther = builderOther.build();
            Request requestOther = new Request.Builder().url(urlOther).post(bodyOther).build();
            Call callOther = okHttpClient.newCall(requestOther);
            Response responseOther = callOther.execute();
            if (!responseOther.isSuccessful()) {
                msg.what = REQUEST_FAIL;
            } else {
                msg.what = REQUEST_SUCCESS;
                msg.obj = responseOther.body().string();
            }
        } catch (Exception ex) {
            msg.what = REQUEST_FAIL;
        } finally {
            msg.sendToTarget();
        }
    }
}