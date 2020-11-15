package com.example.hardware;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.TELEPHONY_SERVICE;


public class Hardware {

    //获取本机硬件信息
    @SuppressLint("NewApi")
    public static JsonObject getObjHardwareInfo(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        JsonObject hardwareRoot = new JsonObject();
        try {
            JsonObject buildInfo = new JsonObject();
            hardwareRoot.add("buildInfo", buildInfo);
            buildInfo.addProperty("ro.product.board", Build.BOARD);
            buildInfo.addProperty("ro.bootloader", Build.BOOTLOADER);
            buildInfo.addProperty("ro.product.brand", Build.BRAND);
            buildInfo.addProperty("ro.product.cpu.abilist32", Build.CPU_ABI);
            buildInfo.addProperty("ro.product.cpu.abilist64", Build.CPU_ABI2);
            //buildInfo.addProperty("ro.product.cpu.SUPPORTED_ABIS", Build.SUPPORTED_ABIS);
            buildInfo.addProperty("ro.product.device", Build.DEVICE);
            buildInfo.addProperty("ro.build.display.id", Build.DISPLAY);
            buildInfo.addProperty("gsm.version.baseband", Build.RADIO);
            buildInfo.addProperty("gsm.version.baseband", Build.getRadioVersion());
            buildInfo.addProperty("ro.build.fingerprint", Build.FINGERPRINT);
            buildInfo.addProperty("ro.hardware", Build.HARDWARE);
            buildInfo.addProperty("ro.build.host", Build.HOST);
            buildInfo.addProperty("ro.build.id", Build.ID);
            buildInfo.addProperty("ro.product.manufacturer", Build.MANUFACTURER);
            buildInfo.addProperty("ro.product.model", Build.MODEL);
            buildInfo.addProperty("ro.serialno", Build.SERIAL);
            buildInfo.addProperty("ro.product.name", Build.PRODUCT);
            buildInfo.addProperty("ro.build.tags", Build.TAGS);
            buildInfo.addProperty("ro.build.date.utc", Build.TIME);
            buildInfo.addProperty("ro.build.type", Build.TYPE);
            buildInfo.addProperty("ro.build.user", Build.USER);
            buildInfo.addProperty("ro.build.version.incremental", Build.VERSION.INCREMENTAL);
            buildInfo.addProperty("ro.build.version.release", Build.VERSION.RELEASE);
            buildInfo.addProperty("ro.build.version.base_os", Build.VERSION.BASE_OS);
            buildInfo.addProperty("ro.build.version.security_patch", Build.VERSION.SECURITY_PATCH);
            buildInfo.addProperty("ro.build.version.sdk.str", Build.VERSION.SDK);
            buildInfo.addProperty("ro.build.version.sdk", Build.VERSION.SDK_INT);
            buildInfo.addProperty("ro.build.version.preview_sdk", Build.VERSION.PREVIEW_SDK_INT);
            buildInfo.addProperty("ro.build.version.codename", Build.VERSION.CODENAME);
            buildInfo.addProperty("ro.debuggable", 0);
            buildInfo.addProperty("net.hostname", getHostName(null));

            TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            int slotCount = mTelephony.getPhoneCount();

            JsonObject otherInfo = new JsonObject();
            hardwareRoot.add("otherInfo", otherInfo);

            //zdx.tm.DEVICE_ID
            try {
                otherInfo.addProperty("zdx.tm.DEVICE_ID", mTelephony.getDeviceId());
                for (int slotId = 0; slotId < slotCount; slotId++) {               //zdx.tm.DEVICE_ID%slotId
                    try {
                        otherInfo.addProperty("zdx.tm.DEVICE_ID" + slotId, mTelephony.getDeviceId(slotId));
                    } catch (Exception e) {
                        otherInfo.addProperty("zdx.tm.DEVICE_ID" + slotId, "null");
                    }
                }
            } catch (Exception e) {

            }

            //zdx.tm.IMEI
            try {
                Method method = mTelephony.getClass().getMethod("getImei", int.class);
                for (int slotId = 0; slotId < slotCount; slotId++) {               //zdx.tm.IMEI%slotId
                    try {
                        String imei = (String) method.invoke(mTelephony, slotId);
                        otherInfo.addProperty("zdx.tm.IMEI" + slotId, imei);
                    } catch (Exception e) {
                        otherInfo.addProperty("zdx.tm.IMEI" + slotId, "null");
                    }

                }
            } catch (Exception e) {
            }

            //zdx.tm.DEVICE_SOFT_VER
            try {
                Method method = mTelephony.getClass().getMethod("getDeviceSoftwareVersion", int.class);
                for (int slotId = 0; slotId < slotCount; slotId++) {
                    try {
                        String SoftwareVersion = (String) method.invoke(mTelephony, slotId);
                        otherInfo.addProperty("zdx.tm.DEVICE_SOFT_VER" + slotId, SoftwareVersion);
                    } catch (Exception e) {
                        otherInfo.addProperty("zdx.tm.DEVICE_SOFT_VER" + slotId, "null");
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //zdx.tm.NAI
            try {
                String nai0 = mTelephony.getNai();
                otherInfo.addProperty("zdx.tm.NAI0", nai0);
                Method method = mTelephony.getClass().getMethod("getNai", int.class);
                for (int slotId = 1; slotId < slotCount; slotId++) {
                    try {
                        String nai = (String) method.invoke(mTelephony, slotId);
                        otherInfo.addProperty("zdx.tm.NAI" + slotId, nai);
                    } catch (Exception e) {
                        otherInfo.addProperty("zdx.tm.NAI" + slotId, "null");
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //zdx.tm.GSM_PSC
            try {
                CellLocation cell = mTelephony.getCellLocation();
                if (cell != null) {
                    GsmCellLocation gsmCellLocation = (GsmCellLocation) cell;
                    otherInfo.addProperty("zdx.tm.GSM_LAC", String.valueOf(gsmCellLocation.getLac()));
                    otherInfo.addProperty("zdx.tm.GSM_CID", String.valueOf(gsmCellLocation.getCid()));
                    otherInfo.addProperty("zdx.tm.GSM_PSC", String.valueOf(gsmCellLocation.getPsc()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.GSM_LAC", "null");
                otherInfo.addProperty("zdx.tm.GSM_CID", "null");
                otherInfo.addProperty("zdx.tm.GSM_PSC", "null");
            }

            //zdx.tm.CURRENT_PHONE_TYPE_FOR_SLOT
            try {
                Method method = mTelephony.getClass().getMethod("getCurrentPhoneType", int.class);
                for (int slotId = 0; slotId < slotCount; slotId++) {
                    try {
                        Object imei = method.invoke(mTelephony, slotId);
                        otherInfo.addProperty("zdx.tm.CURRENT_PHONE_TYPE_FOR_SLOT" + slotId, imei.toString());
                    } catch (Exception e) {
                        otherInfo.addProperty("zdx.tm.CURRENT_PHONE_TYPE_FOR_SLOT" + slotId, "null");
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //zdx.tm.PHONE_TYPE
            otherInfo.addProperty("zdx.tm.PHONE_TYPE", mTelephony.getPhoneType());
//            JSONArray simList = SimUtils.getAllSimInfo(mTelephony);
            //zdx.tm.NETWORK_OPERATOR_NAME
            try {
                SubscriptionManager mSubscriptionManager = SubscriptionManager.from(context);
                List<SubscriptionInfo> mSubInfoList = mSubscriptionManager.getActiveSubscriptionInfoList();
                for (SubscriptionInfo info : mSubInfoList) {
                    Log.i("log", info.toString());
                    otherInfo.addProperty("zdx.tm.NETWORK_OPERATOR_NAME" + info.getSimSlotIndex(), info.getDisplayName().toString());
                    otherInfo.addProperty("zdx.tm.SIM_COUNTRY_ISO_FOR_PHONE" + info.getSimSlotIndex(), info.getCountryIso());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


//            //todo  这里有问题，应该是按卡来获取而不是卡槽  待修正
//            for (int slotId = 0; slotId < slotCount; slotId++) {
//                otherInfo.addProperty("zdx.tm.NETWORK_OPERATOR_NAME" + slotId, mTelephony.getNetworkOperator());
//            }

            otherInfo.addProperty("zdx.tm.NETWORK_OPERATOR_FOR_PHONE0", mTelephony.getNetworkOperator());
            otherInfo.addProperty("zdx.tm.IS_NETWORK_ROAMING", mTelephony.isNetworkRoaming());
            otherInfo.addProperty("zdx.tm.NETWORK_COUNTRY_ISO", mTelephony.getNetworkCountryIso());
            otherInfo.addProperty("zdx.tm.NETWORK_TYPE", mTelephony.getNetworkType());
            otherInfo.addProperty("zdx.tm.DATA_NETWORK_TYPE", mTelephony.getDataNetworkType());
            otherInfo.addProperty("zdx.tm.VOICE_NETWORK_TYPE", mTelephony.getVoiceNetworkType());


            try {
                Method method = mTelephony.getClass().getMethod("getNetworkTypeName");
                String resutl = (String) method.invoke(mTelephony);
                otherInfo.addProperty("zdx.tm.NETWORK_TYPE_NAME", resutl);
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.NETWORK_TYPE_NAME", "null");

            }

            //SIM_STATE
            //ToDo  获取不到  这里还需要排查
            try {
                Method method = mTelephony.getClass().getMethod("getSimState", int.class);
                for (int slotId = 0; slotId < slotCount; slotId++) {
                    try {
                        otherInfo.addProperty("zdx.tm.SIM_STATE" + slotId, mTelephony.getSimState());

                    } catch (Exception e) {
                        otherInfo.addProperty("zdx.tm.SIM_STATE" + slotId, "null");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            otherInfo.addProperty("zdx.tm.SIM_OPERATOR", mTelephony.getSimOperator());

            //SIM_OPERATOR_NUMERIC0
            try {
                Method method = mTelephony.getClass().getMethod("getSimOperatorNumeric");
                String numberic = (String) method.invoke(mTelephony);
                otherInfo.addProperty("zdx.tm.SIM_OPERATOR_NUMERIC0", numberic);
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.SIM_OPERATOR_NUMERIC0", "null");

            }

            otherInfo.addProperty("zdx.tm.SIM_OPERATOR_NAME", mTelephony.getSimOperatorName());

//            //zdx.tm.SIM_COUNTRY_ISO_FOR_PHONE0
//            //ToDo
//            try {
//                Method method = mTelephony.getClass().getMethod("getSimCountryIsoForPhone", int.class);
//                String result = (String) method.invoke(mTelephony, 0);
//                otherInfo.addProperty("zdx.tm.SIM_COUNTRY_ISO_FOR_PHONE0", result);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            otherInfo.addProperty("zdx.tm.SIM_COUNTRY_ISO", mTelephony.getSimCountryIso());
            otherInfo.addProperty("zdx.tm.SimSerialNumber", mTelephony.getSimSerialNumber());

            //zdx.tm.LTE_ON_CDMA_MODE
            try {
                otherInfo.add("zdx.tm.LTE_ON_CDMA_MODE", null);
                Method method = mTelephony.getClass().getMethod("getLteOnCdmaMode");
                Object numberic = method.invoke(mTelephony);
                otherInfo.addProperty("zdx.tm.LTE_ON_CDMA_MODE", numberic.toString());
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.LTE_ON_CDMA_MODE", "null");

            }

            otherInfo.addProperty("zdx.tm.SUBSCRIBER_ID", mTelephony.getSubscriberId());
            otherInfo.addProperty("zdx.tm.GROUP_ID_LEVEL1", mTelephony.getGroupIdLevel1());
            otherInfo.addProperty("zdx.tm.Line1Number", mTelephony.getLine1Number());

            ///zdx.tm.MSISDN
            try {
                Method method = mTelephony.getClass().getMethod("getLine1AlphaTag");
                String result = (String) method.invoke(mTelephony);
                otherInfo.addProperty("zdx.tm.LINE1_ALPHA_TAG", result);
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.LINE1_ALPHA_TAG", "null");

            }

            try {
                Method method1 = mTelephony.getClass().getMethod("getMsisdn");
                String result = (String) method1.invoke(mTelephony);
                otherInfo.addProperty("zdx.tm.MSISDN", result);
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.MSISDN", "null");

            }

            ///todo
            try {
                Method method2 = mTelephony.getClass().getMethod("getIsimImpi");
                String result = (String) method2.invoke(mTelephony);
                otherInfo.addProperty("zdx.tm.ISIM_IMPI", result);
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.ISIM_IMPI", "null");

            }

            //todo java.lang.SecurityException: getIsimIst: Neither user 10850 nor current process has android.permission.READ_PRIVILEGED_PHONE_STATE.
            try {
                Method method3 = mTelephony.getClass().getMethod("getIsimDomain");
                String result = (String) method3.invoke(mTelephony);
                otherInfo.addProperty("zdx.tm.ISIM_DOMAIN", result);
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.ISIM_DOMAIN", "null");

            }


            try {
                Method method4 = mTelephony.getClass().getMethod("getSimCount");
                int resultInt = (int) method4.invoke(mTelephony);
                otherInfo.addProperty("zdx.tm.SIM_COUNT", resultInt);
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.SIM_COUNT", "null");

            }

            //todo  java.lang.SecurityException: getIsimIst: Neither user 10850 nor current process has android.permission.READ_PRIVILEGED_PHONE_STATE.
            try {
                Method method5 = mTelephony.getClass().getMethod("getIsimIst");
                String result = (String) method5.invoke(mTelephony);
                otherInfo.addProperty("zdx.tm.ISIM_IST", result);
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.ISIM_IST", "null");

            }
            //todo  java.lang.SecurityException: getCdmaMdn
            try {
                Method method6 = mTelephony.getClass().getMethod("getCdmaMdn");
                String result = (String) method6.invoke(mTelephony);
                otherInfo.addProperty("zdx.tm.CDMA_MDN", result);
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.CDMA_MDN", "null");

            }

            Gson gson = new Gson();
            String json = gson.toJson(mTelephony.getAllCellInfo());
            otherInfo.addProperty("zdx.tm.ALL_CELL_INFO", json);
            otherInfo.addProperty("zdx.tm.HAS_ICCCARD", mTelephony.hasIccCard());

            try {
                //todo  UnsupportedAppUsage mTelephony.getSlotIndex()无法访问  尝试使用反射  报错java.lang.NoSuchMethodException: getSlotIndex []
                Method method = mTelephony.getClass().getMethod("getSlotIndex");
                int slotIndex = (int) method.invoke(mTelephony);
                Method method2 = mTelephony.getClass().getMethod("hasIccCard", int.class);
                boolean hasIccCard = (boolean) method2.invoke(mTelephony, slotIndex);
                otherInfo.addProperty("zdx.tm.HAS_ICCCARD" + slotIndex, hasIccCard);
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.HAS_ICCCARD%slotIndex", "null");

            }


            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo NetworkInfo = connectivityManager.getActiveNetworkInfo();
            otherInfo.addProperty("zdx.net.NETWORK_TYPE", NetworkInfo.getType());
            otherInfo.addProperty("zdx.net.NETWORK_SUB_TYPE", NetworkInfo.getSubtype());
            otherInfo.addProperty("zdx.net.NETWORK_TYPE_NAME", NetworkInfo.getTypeName());
            otherInfo.addProperty("zdx.net.NETWORK_SUB_TYPE_NAME", NetworkInfo.getSubtypeName());
            otherInfo.addProperty("zdx.net.NETWORK_EXTRA_INFO", NetworkInfo.getExtraInfo());

            WifiManager WifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo WifiInfo = WifiManager.getConnectionInfo();
            otherInfo.addProperty("zdx.net.WIFI_SSID", WifiInfo.getSSID());
            otherInfo.addProperty("zdx.net.WIFI_BSSID", WifiInfo.getBSSID());
            otherInfo.addProperty("zdx.net.WIFI_RSSI", WifiInfo.getRssi());
            otherInfo.addProperty("zdx.net.WIFI_MAC_ADDR", WifiInfo.getMacAddress());
            otherInfo.addProperty("zdx.net.WIFI_LINK_SPEED", WifiInfo.getLinkSpeed());
            otherInfo.addProperty("zdx.net.WIFI_FREQUENCY", WifiInfo.getFrequency());
            otherInfo.addProperty("zdx.net.WIFI_NETWORK_ID", WifiInfo.getNetworkId());
            otherInfo.addProperty("zdx.net.WIFI_IP_ADDRESS", WifiInfo.getIpAddress());
            otherInfo.addProperty("zdx.net.WIFI_STATE", WifiManager.getWifiState());


            //3.把list或对象转化为json
            Gson gson2 = new Gson();
            otherInfo.addProperty("zdx.net.WIFI_LIST", gson2.toJson(WifiManager.getScanResults()));

            Display Display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point p = new Point();
            Display.getSize(p);
            otherInfo.addProperty("zdx.dis.SIZE_X", p.x);
            otherInfo.addProperty("zdx.dis.SIZE_Y", p.y);
            otherInfo.addProperty("zdx.dis.ROTATION", Display.getRotation());
            try {
                // todo  java.lang.NoSuchMethodException: getMaximumSizeDimension []
                Method method6 = mTelephony.getClass().getMethod("getMaximumSizeDimension");
                int result = (int) method6.invoke(Display);
                otherInfo.addProperty("zdx.tm.MAX_SIZE_DIM", result);
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.MAX_SIZE_DIM", "null");

            }
            otherInfo.addProperty("zdx.dis.NAME", Display.getName());

            /// todo  这里引起崩溃  废除
            otherInfo.addProperty("zdx.dis.ID", "null");
            otherInfo.addProperty("zdx.set.ANDROID_ID", Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID));
            otherInfo.addProperty("zds.java.vm.version", System.getProperty("java.vm.version"));
            otherInfo.addProperty("zds.http.agent", System.getProperty("http.agent"));

//            try {

//                Enumeration<NetworkInterface> e = java.net.NetworkInterface.getNetworkInterfaces();
//                NetworkInterface NetworkInterface = e.nextElement();
//                byte[] mac = NetworkInterface.getHardwareAddress();
            otherInfo.addProperty("zds.net.HARDWARE_ADDR.wlan0", getMACAddress("wlan0"));
            otherInfo.addProperty("zds.net.HARDWARE_ADDR.eth0", getMACAddress("eth0"));

//            } catch (SocketException ex) {
//                ex.printStackTrace();
//            }
            try {
                otherInfo.addProperty("zdx.web.UA", new WebView(context).getSettings().getUserAgentString());
            }catch (Exception e){
                otherInfo.addProperty("zdx.web.UA","null");
            }

            try {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                List<String> providerList = locationManager.getProviders(true);
                String locationProvider = LocationManager.GPS_PROVIDER;
                if (providerList != null && providerList.contains(LocationManager.GPS_PROVIDER)) {
                    //如果是Network
                    locationProvider = LocationManager.GPS_PROVIDER;
                } else if (providerList != null && providerList.contains(LocationManager.NETWORK_PROVIDER)) {
                    locationProvider = LocationManager.NETWORK_PROVIDER;

                }
                Location location = locationManager.getLastKnownLocation(locationProvider);
                if (location != null) {
                    otherInfo.addProperty("zdx.loc.LAT", location.getLatitude());
                    otherInfo.addProperty("zdx.loc.LONG", location.getLongitude());
                    Toast.makeText(context, location.getAltitude() + "===", Toast.LENGTH_LONG).show();
                } else {
                    otherInfo.addProperty("zdx.loc.LAT", "null");
                    otherInfo.addProperty("zdx.loc.LONG", "null");
                }

            } catch (Exception e) {
                otherInfo.addProperty("zdx.loc.LAT", "null");
                otherInfo.addProperty("zdx.loc.LONG", "null");
            }

            BluetoothAdapter bluetoothAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter!=null) {
                otherInfo.addProperty("zdx.bt.BLUETOOTH_ADDR", bluetoothAdapter.getAddress());
                otherInfo.addProperty("zdx.bt.BLUETOOTH_NAME", bluetoothAdapter.getName());
            }


            otherInfo.addProperty("zdx.set.KERNEL_VER", getFormattedKernelVersion());

            PackageManager pm = context.getPackageManager();
            List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
            otherInfo.addProperty("zdx.pm.INSTALLED_PACK", new Gson().toJson(packageInfos));

            otherInfo.addProperty("zdx.debug.IS_DEBUGGER", android.os.Debug.isDebuggerConnected());

            try {
                SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
                Sensor gSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
                if (gSensor != null) {
                    otherInfo.addProperty("zdx.sensor.VENDOR9", gSensor.getVendor());
                    otherInfo.addProperty("zdx.sensor.VERSION9", gSensor.getVersion());
                    otherInfo.addProperty("zdx.sensor.TYPE9", gSensor.getType());
                    otherInfo.addProperty("zdx.sensor.MAX_RANGE9", gSensor.getMaximumRange());
                    otherInfo.addProperty("zdx.sensor.RESOLUTION9", gSensor.getResolution());
                    otherInfo.addProperty("zdx.sensor.POWER9", gSensor.getPower());
                    otherInfo.addProperty("zdx.sensor.MAX_DEALY9", gSensor.getMaxDelay());
                    otherInfo.addProperty("zdx.sensor.MIN_DELAY9", gSensor.getMinDelay());
                } else {
                    otherInfo.addProperty("zdx.sensor.VENDOR9", "null");
                    otherInfo.addProperty("zdx.sensor.VERSION9", "null");
                    otherInfo.addProperty("zdx.sensor.TYPE9", "null");
                    otherInfo.addProperty("zdx.sensor.MAX_RANGE9", "null");
                    otherInfo.addProperty("zdx.sensor.RESOLUTION9", "null");
                    otherInfo.addProperty("zdx.sensor.POWER9", "null");
                    otherInfo.addProperty("zdx.sensor.MAX_DEALY9", "null");
                    otherInfo.addProperty("zdx.sensor.MIN_DELAY9", "null");
                }

                Sensor mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                if (mSensor != null) {
                    otherInfo.addProperty("zdx.sensor.NAME1", mSensor.getName());
                    otherInfo.addProperty("zdx.sensor.VENDOR1", mSensor.getVendor());
                    otherInfo.addProperty("zdx.sensor.VERSION1", mSensor.getVersion());
                    otherInfo.addProperty("zdx.sensor.TYPE1", mSensor.getType());
                    otherInfo.addProperty("zdx.sensor.MAX_RANGE1", mSensor.getMaximumRange());
                    otherInfo.addProperty("zdx.sensor.RESOLUTION1", mSensor.getResolution());
                    otherInfo.addProperty("zdx.sensor.POWER1", mSensor.getPower());
                    otherInfo.addProperty("zdx.sensor.MAX_DEALY1", mSensor.getMaxDelay());
                    otherInfo.addProperty("zdx.sensor.MIN_DELAY1", mSensor.getMinDelay());
                } else {
                    otherInfo.addProperty("zdx.sensor.VENDOR1", "null");
                    otherInfo.addProperty("zdx.sensor.VERSION1", "null");
                    otherInfo.addProperty("zdx.sensor.TYPE1", "null");
                    otherInfo.addProperty("zdx.sensor.MAX_RANGE1", "null");
                    otherInfo.addProperty("zdx.sensor.RESOLUTION1", "null");
                    otherInfo.addProperty("zdx.sensor.POWER1", "null");
                    otherInfo.addProperty("zdx.sensor.MAX_DEALY1", "null");
                    otherInfo.addProperty("zdx.sensor.MIN_DELAY1", "null");
                }


                Sensor oSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

                if (oSensor != null) {
                    otherInfo.addProperty("zdx.sensor.NAME3", oSensor.getName());
                    otherInfo.addProperty("zdx.sensor.VENDOR3", oSensor.getVendor());
                    otherInfo.addProperty("zdx.sensor.VERSION3", oSensor.getVersion());
                    otherInfo.addProperty("zdx.sensor.TYPE3", oSensor.getType());
                    otherInfo.addProperty("zdx.sensor.MAX_RANGE3", oSensor.getMaximumRange());
                    otherInfo.addProperty("zdx.sensor.RESOLUTION3", oSensor.getResolution());
                    otherInfo.addProperty("zdx.sensor.POWER3", oSensor.getPower());
                    otherInfo.addProperty("zdx.sensor.MAX_DEALY3", oSensor.getMaxDelay());
                    otherInfo.addProperty("zdx.sensor.MIN_DELAY3", oSensor.getMinDelay());
                } else {
                    otherInfo.addProperty("zdx.sensor.VENDOR3", "null");
                    otherInfo.addProperty("zdx.sensor.VERSION3", "null");
                    otherInfo.addProperty("zdx.sensor.TYPE3", "null");
                    otherInfo.addProperty("zdx.sensor.MAX_RANGE3", "null");
                    otherInfo.addProperty("zdx.sensor.RESOLUTION3", "null");
                    otherInfo.addProperty("zdx.sensor.POWER3", "null");
                    otherInfo.addProperty("zdx.sensor.MAX_DEALY3", "null");
                    otherInfo.addProperty("zdx.sensor.MIN_DELAY3", "null");
                }

                Sensor gySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                if (gySensor != null) {
                    otherInfo.addProperty("zdx.sensor.NAME4", gySensor.getName());
                    otherInfo.addProperty("zdx.sensor.VENDOR4", gySensor.getVendor());
                    otherInfo.addProperty("zdx.sensor.VERSION4", gySensor.getVersion());
                    otherInfo.addProperty("zdx.sensor.TYPE4", gySensor.getType());
                    otherInfo.addProperty("zdx.sensor.MAX_RANGE4", gySensor.getMaximumRange());
                    otherInfo.addProperty("zdx.sensor.RESOLUTION4", gySensor.getResolution());
                    otherInfo.addProperty("zdx.sensor.POWER4", gySensor.getPower());
                    otherInfo.addProperty("zdx.sensor.MAX_DEALY4", gySensor.getMaxDelay());
                    otherInfo.addProperty("zdx.sensor.MIN_DELAY4", gySensor.getMinDelay());
                } else {
                    otherInfo.addProperty("zdx.sensor.VENDOR4", "null");
                    otherInfo.addProperty("zdx.sensor.VERSION4", "null");
                    otherInfo.addProperty("zdx.sensor.TYPE4", "null");
                    otherInfo.addProperty("zdx.sensor.MAX_RANGE4", "null");
                    otherInfo.addProperty("zdx.sensor.RESOLUTION4", "null");
                    otherInfo.addProperty("zdx.sensor.POWER4", "null");
                    otherInfo.addProperty("zdx.sensor.MAX_DEALY4", "null");
                    otherInfo.addProperty("zdx.sensor.MIN_DELAY4", "null");
                }

            } catch (Exception e) {
                otherInfo.addProperty("zdx.sensor.VENDOR9", "null");
                otherInfo.addProperty("zdx.sensor.VERSION9", "null");
                otherInfo.addProperty("zdx.sensor.TYPE9", "null");
                otherInfo.addProperty("zdx.sensor.MAX_RANGE9", "null");
                otherInfo.addProperty("zdx.sensor.RESOLUTION9", "null");
                otherInfo.addProperty("zdx.sensor.POWER9", "null");
                otherInfo.addProperty("zdx.sensor.MAX_DEALY9", "null");
                otherInfo.addProperty("zdx.sensor.MIN_DELAY9", "null");
                otherInfo.addProperty("zdx.sensor.VENDOR1", "null");
                otherInfo.addProperty("zdx.sensor.VERSION1", "null");
                otherInfo.addProperty("zdx.sensor.TYPE1", "null");
                otherInfo.addProperty("zdx.sensor.MAX_RANGE1", "null");
                otherInfo.addProperty("zdx.sensor.RESOLUTION1", "null");
                otherInfo.addProperty("zdx.sensor.POWER1", "null");
                otherInfo.addProperty("zdx.sensor.MAX_DEALY1", "null");
                otherInfo.addProperty("zdx.sensor.MIN_DELAY1", "null");
                otherInfo.addProperty("zdx.sensor.VENDOR3", "null");
                otherInfo.addProperty("zdx.sensor.VERSION3", "null");
                otherInfo.addProperty("zdx.sensor.TYPE3", "null");
                otherInfo.addProperty("zdx.sensor.MAX_RANGE3", "null");
                otherInfo.addProperty("zdx.sensor.RESOLUTION3", "null");
                otherInfo.addProperty("zdx.sensor.POWER3", "null");
                otherInfo.addProperty("zdx.sensor.MAX_DEALY3", "null");
                otherInfo.addProperty("zdx.sensor.MIN_DELAY3", "null");
                otherInfo.addProperty("zdx.sensor.VENDOR4", "null");
                otherInfo.addProperty("zdx.sensor.VERSION4", "null");
                otherInfo.addProperty("zdx.sensor.TYPE4", "null");
                otherInfo.addProperty("zdx.sensor.MAX_RANGE4", "null");
                otherInfo.addProperty("zdx.sensor.RESOLUTION4", "null");
                otherInfo.addProperty("zdx.sensor.POWER4", "null");
                otherInfo.addProperty("zdx.sensor.MAX_DEALY4", "null");
                otherInfo.addProperty("zdx.sensor.MIN_DELAY4", "null");
            }

            try {
                ///todo  No virtual method getSignalStrength()Landroid/telephony/SignalStrengt  崩溃
                otherInfo.addProperty("zdx.signal.IS_GSM", mTelephony.getSignalStrength().isGsm());
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.signal.IS_GSM", "null");
            }
            //todo 这4个无法获取到boolean的值
            otherInfo.addProperty("zdx.signal.FAKE_ENABLE", true);
            otherInfo.addProperty("zdx.signal.cdma.FAKE_ENABLE", true);
            otherInfo.addProperty("zdx.signal.wcdma.FAKE_ENABLE", true);
            otherInfo.addProperty("zdx.traffic.mobile.FAKE_ENABLE", true);
            List<PathBean> list = new ArrayList();
            list.add(new PathBean("/sdcard/cpuinf", "/proc/cpuinfo"));
            list.add(new PathBean("/sdcard/meminfo", "/proc/meminfo"));
            String replace = new Gson().toJson(list);
            otherInfo.addProperty("zdx.file.REPLACE", replace);


//            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
//            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
//            otherInfo.addProperty("zdx.loc.GEOCODER_GET_LOC", Geocoder.getFromLocation());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return hardwareRoot;
    }

    //getHostName
    public static String getHostName(String defValue) {
        try {
            Method getString = Build.class.getDeclaredMethod("getString", String.class);
            getString.setAccessible(true);
            return getString.invoke(null, "net.hostname").toString();
        } catch (Exception ex) {
            return defValue;
        }
    }

    public static String getIMEI(Context context, int slotId) {
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            Method method = manager.getClass().getMethod("getImei", int.class);
            String imei = (String) method.invoke(manager, slotId);
            return imei;
        } catch (Exception e) {
            return "";
        }
    }


    public static String getIMEI(Context context) {
        String imei = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                imei = "deviceId:" + tm.getDeviceId();
            } else {
                Method method = tm.getClass().getMethod("getImei");
                imei = (String) method.invoke(tm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }


    static class PathBean {
        public String srcPath;
        public String desPath;

        public PathBean(String srcPath, String desPath) {
            this.srcPath = srcPath;
            this.desPath = desPath;
        }
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    private static final String FILENAME_PROC_VERSION = "/proc/version";

    public static String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }

    //内核版本
    public static String getFormattedKernelVersion() {
        String procVersionStr;

        try {
            procVersionStr = readLine(FILENAME_PROC_VERSION);
            final String PROC_VERSION_REGEX =
                    "\\w+\\s+" + /* ignore: Linux */
                            "\\w+\\s+" + /* ignore: version */
                            "([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
                            "\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /* group 2: (xxxxxx@xxxxx.constant) */
                            "\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+" + /* ignore: (gcc ..) */
                            "([^\\s]+)\\s+" + /* group 3: #26 */
                            "(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
                            "(.+)"; /* group 4: date */

            Pattern p = Pattern.compile(PROC_VERSION_REGEX);
            Matcher m = p.matcher(procVersionStr);

            if (!m.matches()) {
                return "Unavailable";
            } else if (m.groupCount() < 4) {
                return "Unavailable";
            } else {
                return (new StringBuilder(m.group(1)).append("\n").append(
                        m.group(2)).append(" ").append(m.group(3)).append("\n")
                        .append(m.group(4))).toString();
            }
        } catch (IOException e) {
            return "Unavailable";
        }
    }
}
