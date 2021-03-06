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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebView;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.TELEPHONY_SERVICE;


public class Hardware {
    static String nullStr = null;

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
            } catch (Exception e) {
                otherInfo.addProperty("zdx.tm.DEVICE_ID", nullStr);
            }
            for (int slotId = 0; slotId < slotCount; slotId++) {               //zdx.tm.DEVICE_ID%slotId
                try {
                    otherInfo.addProperty("zdx.tm.DEVICE_ID" + slotId, mTelephony.getDeviceId(slotId));
                } catch (Exception e) {
                    otherInfo.addProperty("zdx.tm.DEVICE_ID" + slotId, nullStr);
                }
            }

            //zdx.tm.IMEI
            try {
                Method method = mTelephony.getClass().getMethod("getImei", int.class);
                for (int slotId = 0; slotId < slotCount; slotId++) {               //zdx.tm.IMEI%slotId
                    try {
                        String imei = (String) method.invoke(mTelephony, slotId);
                        otherInfo.addProperty("zdx.tm.IMEI" + slotId, imei);
                    } catch (Exception e) {
                        otherInfo.addProperty("zdx.tm.IMEI" + slotId, nullStr);
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
                        otherInfo.addProperty("zdx.tm.DEVICE_SOFT_VER" + slotId, nullStr);
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
                        otherInfo.addProperty("zdx.tm.NAI" + slotId, nullStr);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //zdx.tm.GSM_PSC
            try {
                CellLocation cell = mTelephony.getCellLocation();
                if (cell != null) {
                    if (cell instanceof GsmCellLocation) {
                        GsmCellLocation gsmCellLocation = (GsmCellLocation) cell;
                        otherInfo.addProperty("zdx.tm.GSM_LAC", String.valueOf(gsmCellLocation.getLac()));
                        otherInfo.addProperty("zdx.tm.GSM_CID", String.valueOf(gsmCellLocation.getCid()));
                        otherInfo.addProperty("zdx.tm.GSM_PSC", String.valueOf(gsmCellLocation.getPsc()));
                    } else if (cell instanceof CdmaCellLocation) {
                        CdmaCellLocation cellLocation = (CdmaCellLocation) cell;
                        otherInfo.addProperty("zdx.tm.CDMA_BASE_STATION_ID", String.valueOf(cellLocation.getBaseStationId()));
                        otherInfo.addProperty("zdx.tm.CDMA_BASE_STATION_LAT", String.valueOf(cellLocation.getBaseStationLatitude()));
                        otherInfo.addProperty("zdx.tm.CDMA_BASE_STATION_LONG", String.valueOf(cellLocation.getBaseStationLongitude()));
                        otherInfo.addProperty("zdx.tm.CDMA_SYSTEM_ID", String.valueOf(cellLocation.getSystemId()));
                        otherInfo.addProperty("zdx.tm.CDMA_NETWORK_ID", String.valueOf(cellLocation.getNetworkId()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Method method = mTelephony.getClass().getMethod("getCurrentPhoneType");
                Object phoneType = method.invoke(mTelephony);
                otherInfo.addProperty("zdx.tm.CURRENT_PHONE_TYPE0", phoneType.toString());
            } catch (Exception e) {
                otherInfo.addProperty("zdx.tm.CURRENT_PHONE_TYPE0", nullStr);
            }

            try {
                String subscription = mTelephony.getSubscriberId();
                Method method = mTelephony.getClass().getMethod("getCurrentPhoneType", int.class);
                Object phoneType = method.invoke(mTelephony, Integer.parseInt(subscription));
                otherInfo.addProperty("zdx.tm.CURRENT_PHONE_TYPE" + subscription, phoneType.toString());
            } catch (Exception e) {
//                otherInfo.addProperty("zdx.tm.CURRENT_PHONE_TYPE" + 0, nullStr);
            }


            //zdx.tm.CURRENT_PHONE_TYPE_FOR_SLOT
            try {
                for (int slotId = 0; slotId < slotCount; slotId++) {
                    try {
                        Method method = mTelephony.getClass().getMethod("getCurrentPhoneTypeForSlot", int.class);
                        Object imei = method.invoke(mTelephony, slotId);
                        otherInfo.addProperty("zdx.tm.CURRENT_PHONE_TYPE_FOR_SLOT" + slotId, imei.toString());
                    } catch (Exception e) {
                        otherInfo.addProperty("zdx.tm.CURRENT_PHONE_TYPE_FOR_SLOT" + slotId, nullStr);
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
                    try {
                        otherInfo.addProperty("zdx.tm.NETWORK_OPERATOR_NAME" + info.getSimSlotIndex(), info.getDisplayName().toString());
                        otherInfo.addProperty("zdx.tm.SIM_COUNTRY_ISO_FOR_PHONE" + info.getSimSlotIndex(), info.getCountryIso());
                    } catch (Exception e) {
                        e.printStackTrace();
                        otherInfo.addProperty("zdx.tm.NETWORK_OPERATOR_NAME" + info.getSimSlotIndex(), nullStr);
                        otherInfo.addProperty("zdx.tm.SIM_COUNTRY_ISO_FOR_PHONE" + info.getSimSlotIndex(), nullStr);
                    }
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
                otherInfo.addProperty("zdx.tm.NETWORK_TYPE_NAME", nullStr);

            }

            //SIM_STATE
            //ToDo  获取不到  这里还需要排查
            try {
                Method method = mTelephony.getClass().getMethod("getSimState", int.class);
                for (int slotId = 0; slotId < slotCount; slotId++) {
                    try {
                        otherInfo.addProperty("zdx.tm.SIM_STATE" + slotId, mTelephony.getSimState());

                    } catch (Exception e) {
                        otherInfo.addProperty("zdx.tm.SIM_STATE" + slotId, nullStr);
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
                otherInfo.addProperty("zdx.tm.SIM_OPERATOR_NUMERIC0", nullStr);

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
            try {
                otherInfo.addProperty("zdx.tm.SimSerialNumber", mTelephony.getSimSerialNumber());
            } catch (Exception e) {
                otherInfo.addProperty("zdx.tm.SimSerialNumber", nullStr);
            }

            //zdx.tm.LTE_ON_CDMA_MODE
            try {
                otherInfo.add("zdx.tm.LTE_ON_CDMA_MODE", null);
                Method method = mTelephony.getClass().getMethod("getLteOnCdmaMode");
                Object numberic = method.invoke(mTelephony);
                otherInfo.addProperty("zdx.tm.LTE_ON_CDMA_MODE", numberic.toString());
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.LTE_ON_CDMA_MODE", nullStr);
            }
            try {
                otherInfo.addProperty("zdx.tm.SUBSCRIBER_ID", mTelephony.getSubscriberId());
            } catch (Exception e) {
                otherInfo.addProperty("zdx.tm.SUBSCRIBER_ID", nullStr);
            }
            try {
                otherInfo.addProperty("zdx.tm.GROUP_ID_LEVEL1", mTelephony.getGroupIdLevel1());
            } catch (Exception e) {
                otherInfo.addProperty("zdx.tm.GROUP_ID_LEVEL1", nullStr);
            }
            try {
                otherInfo.addProperty("zdx.tm.Line1Number", mTelephony.getLine1Number());
            } catch (Exception e) {
                otherInfo.addProperty("zdx.tm.Line1Number", nullStr);
            }

            ///zdx.tm.MSISDN
            try {
                Method method = mTelephony.getClass().getMethod("getLine1AlphaTag");
                String result = (String) method.invoke(mTelephony);
                otherInfo.addProperty("zdx.tm.LINE1_ALPHA_TAG", result);
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.LINE1_ALPHA_TAG", nullStr);

            }

            try {
                Method method1 = mTelephony.getClass().getMethod("getMsisdn");
                String result = (String) method1.invoke(mTelephony);
                otherInfo.addProperty("zdx.tm.MSISDN", result);
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.MSISDN", nullStr);

            }

            ///todo
            try {
                Method method2 = mTelephony.getClass().getMethod("getIsimImpi");
                String result = (String) method2.invoke(mTelephony);
                otherInfo.addProperty("zdx.tm.ISIM_IMPI", result);
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.ISIM_IMPI", nullStr);

            }

            //todo java.lang.SecurityException: getIsimIst: Neither user 10850 nor current process has android.permission.READ_PRIVILEGED_PHONE_STATE.
            try {
                Method method3 = mTelephony.getClass().getMethod("getIsimDomain");
                String result = (String) method3.invoke(mTelephony);
                otherInfo.addProperty("zdx.tm.ISIM_DOMAIN", result);
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.ISIM_DOMAIN", nullStr);

            }


            try {
                Method method4 = mTelephony.getClass().getMethod("getSimCount");
                int resultInt = (int) method4.invoke(mTelephony);
                otherInfo.addProperty("zdx.tm.SIM_COUNT", resultInt);
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.SIM_COUNT", nullStr);

            }

            //todo  java.lang.SecurityException: getIsimIst: Neither user 10850 nor current process has android.permission.READ_PRIVILEGED_PHONE_STATE.
            try {
                Method method5 = mTelephony.getClass().getMethod("getIsimIst");
                String result = (String) method5.invoke(mTelephony);
                otherInfo.addProperty("zdx.tm.ISIM_IST", result);
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.ISIM_IST", nullStr);

            }
            //todo  java.lang.SecurityException: getCdmaMdn
            try {
                Method method6 = mTelephony.getClass().getMethod("getCdmaMdn");
                String result = (String) method6.invoke(mTelephony);
                otherInfo.addProperty("zdx.tm.CDMA_MDN", result);
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.tm.CDMA_MDN", nullStr);

            }

            Gson gson = new Gson();
            List<CellInfo> cellInfos = mTelephony.getAllCellInfo();
            List<Object> cellInfoBeans = new ArrayList<>();
            if (cellInfos != null) {
                for (CellInfo cellInfo : cellInfos) {
                    if (cellInfo instanceof CellInfoLte) {
                        //4G
                        CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                        CellInfoBean3 bean = new CellInfoBean3();
                        bean.setMType(3);
                        CellInfoBean3.CellIdentityBean cellIdentityBean = new CellInfoBean3.CellIdentityBean();
                        cellIdentityBean.setMCi(cellInfoLte.getCellIdentity().getCi());
                        cellIdentityBean.setMMcc(cellInfoLte.getCellIdentity().getMcc());
                        cellIdentityBean.setMEarfcn(cellInfoLte.getCellIdentity().getEarfcn());
                        cellIdentityBean.setMMnc(cellInfoLte.getCellIdentity().getMnc());
                        cellIdentityBean.setMPci(cellInfoLte.getCellIdentity().getPci());
                        cellIdentityBean.setMTac(cellInfoLte.getCellIdentity().getTac());
                        CellInfoBean3.CellSignalStrengthBean strengthBean = new CellInfoBean3.CellSignalStrengthBean();
                        CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                        strengthBean.setCqi(cellSignalStrengthLte.getCqi());
                        strengthBean.setRsrp(cellSignalStrengthLte.getRsrp());
                        strengthBean.setRsrq(cellSignalStrengthLte.getRsrq());
                        strengthBean.setRssnr(cellSignalStrengthLte.getRssnr());
                        strengthBean.setSs(cellSignalStrengthLte.getRssi());
                        strengthBean.setTa(cellSignalStrengthLte.getTimingAdvance());
                        bean.setCellIdentity(cellIdentityBean);
                        bean.setCellSignalStrength(strengthBean);
                        cellInfoBeans.add(bean);
                    } else if (cellInfo instanceof CellInfoGsm) {
                        //3G
                        CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
                        CellInfoBean1 bean = new CellInfoBean1();
                        bean.setMType(1);
                        CellInfoBean1.CellIdentityBean cellIdentityBean = new CellInfoBean1.CellIdentityBean();
                        cellIdentityBean.setMLac(cellInfoGsm.getCellIdentity().getLac());
                        cellIdentityBean.setMMcc(cellInfoGsm.getCellIdentity().getMcc());
                        cellIdentityBean.setMBsic(cellInfoGsm.getCellIdentity().getBsic());
                        cellIdentityBean.setMMnc(cellInfoGsm.getCellIdentity().getMnc());
                        cellIdentityBean.setMCid(cellInfoGsm.getCellIdentity().getCid());
                        cellIdentityBean.setMLac(cellInfoGsm.getCellIdentity().getLac());
                        CellInfoBean1.CellSignalStrengthBean strengthBean = new CellInfoBean1.CellSignalStrengthBean();
                        CellSignalStrengthGsm cellSignalStrengthGSM = cellInfoGsm.getCellSignalStrength();
                        strengthBean.setBer(cellSignalStrengthGSM.getBitErrorRate());
                        strengthBean.setMTa(cellSignalStrengthGSM.getTimingAdvance());
                        strengthBean.setSs(cellSignalStrengthGSM.getRssi());
                        bean.setCellIdentity(cellIdentityBean);
                        bean.setCellSignalStrength(strengthBean);
                        cellInfoBeans.add(bean);
                    } else if (cellInfo instanceof CellInfoCdma) {
                        //cdma
                        CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfo;
                        CellInfoBean2 bean = new CellInfoBean2();
                        bean.setMType(2);
                        CellInfoBean2.CellIdentityBean cellIdentityBean = new CellInfoBean2.CellIdentityBean();
                        cellIdentityBean.setMBasestationId(cellInfoCdma.getCellIdentity().getBasestationId());
                        cellIdentityBean.setMLatitude(cellInfoCdma.getCellIdentity().getLatitude());
                        cellIdentityBean.setMLongitude(cellInfoCdma.getCellIdentity().getLongitude());
                        cellIdentityBean.setMNetworkId(cellInfoCdma.getCellIdentity().getNetworkId());
                        cellIdentityBean.setMSystemId(cellInfoCdma.getCellIdentity().getSystemId());
                        CellInfoBean2.CellSignalStrengthBean strengthBean = new CellInfoBean2.CellSignalStrengthBean();
                        CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                        strengthBean.setCdmaDbm(cellSignalStrengthCdma.getCdmaDbm());
                        strengthBean.setCdmaEcio(cellSignalStrengthCdma.getEvdoEcio());
                        strengthBean.setEvdoDbm(cellSignalStrengthCdma.getEvdoDbm());
                        strengthBean.setEvdoSnr(cellSignalStrengthCdma.getEvdoSnr());
                        bean.setCellIdentity(cellIdentityBean);
                        bean.setCellSignalStrength(strengthBean);
                        cellInfoBeans.add(bean);
                    } else if (cellInfo instanceof CellInfoWcdma) {
                        //3G
                        CellInfoWcdma info = (CellInfoWcdma) cellInfo;
                        CellInfoBean4 bean = new CellInfoBean4();
                        bean.setMType(4);
                        CellInfoBean4.CellIdentityBean identityBean = new CellInfoBean4.CellIdentityBean();
                        identityBean.setMCid(info.getCellIdentity().getCid());
                        identityBean.setMLac(info.getCellIdentity().getLac());
                        identityBean.setMMcc(info.getCellIdentity().getMcc());
                        identityBean.setMMnc(info.getCellIdentity().getMnc());
                        identityBean.setMPsc(info.getCellIdentity().getPsc());
                        identityBean.setMUarfcn(info.getCellIdentity().getUarfcn());
                        CellInfoBean4.CellSignalStrengthBean strengthBean = new CellInfoBean4.CellSignalStrengthBean();
                        CellSignalStrengthWcdma strengthWcdma = info.getCellSignalStrength();
//                        strengthBean.setBer(strengthWcdma.getBitErrorRate());
//                        strengthBean.setSs(strengthWcdma.getRssi());
                        bean.setCellIdentity(identityBean);
                        bean.setCellSignalStrength(strengthBean);
                        cellInfoBeans.add(bean);
                    }
                }
            }
            String json = gson.toJson(cellInfoBeans);
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
                otherInfo.addProperty("zdx.tm.HAS_ICCCARD%slotIndex", nullStr);

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


            List<ScanResult> scanResults = WifiManager.getScanResults();
            List<WifiBean> wifiBeans = new ArrayList<>();
            for (ScanResult result : scanResults) {
                wifiBeans.add(new WifiBean(result.SSID, result.BSSID, result.capabilities, result.level, result.frequency));
            }
            //3.把list或对象转化为json
            Gson gson2 = new Gson();
            otherInfo.addProperty("zdx.net.WIFI_LIST", gson2.toJson(wifiBeans));

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
                otherInfo.addProperty("zdx.tm.MAX_SIZE_DIM", nullStr);

            }
            otherInfo.addProperty("zdx.dis.NAME", Display.getName());

            /// todo  这里引起崩溃  废除
            otherInfo.addProperty("zdx.dis.ID", nullStr);
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
            } catch (Exception e) {
                otherInfo.addProperty("zdx.web.UA", nullStr);
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
                    setLocationStr(context, otherInfo, location);
                } else {
                    otherInfo.addProperty("zdx.loc.LAT", nullStr);
                    otherInfo.addProperty("zdx.loc.LONG", nullStr);
                }

            } catch (Exception e) {
                otherInfo.addProperty("zdx.loc.LAT", nullStr);
                otherInfo.addProperty("zdx.loc.LONG", nullStr);
            }

            BluetoothAdapter bluetoothAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null) {
                otherInfo.addProperty("zdx.bt.BLUETOOTH_ADDR", bluetoothAdapter.getAddress());
                otherInfo.addProperty("zdx.bt.BLUETOOTH_NAME", bluetoothAdapter.getName());
            }


            otherInfo.addProperty("zdx.set.KERNEL_VER", getFormattedKernelVersion());

            PackageManager pm = context.getPackageManager();
            List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
            List<PackageInfoBean> packageInfoBeans = new ArrayList<>();
            for (PackageInfo info : packageInfos) {
                packageInfoBeans.add(new PackageInfoBean(getApplicationNameByPackageName(context, info.packageName), info.packageName));
            }
            otherInfo.addProperty("zdx.pm.INSTALLED_PACK", new Gson().toJson(packageInfoBeans));

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
                    otherInfo.addProperty("zdx.sensor.VENDOR9", nullStr);
                    otherInfo.addProperty("zdx.sensor.VERSION9", nullStr);
                    otherInfo.addProperty("zdx.sensor.TYPE9", nullStr);
                    otherInfo.addProperty("zdx.sensor.MAX_RANGE9", nullStr);
                    otherInfo.addProperty("zdx.sensor.RESOLUTION9", nullStr);
                    otherInfo.addProperty("zdx.sensor.POWER9", nullStr);
                    otherInfo.addProperty("zdx.sensor.MAX_DEALY9", nullStr);
                    otherInfo.addProperty("zdx.sensor.MIN_DELAY9", nullStr);
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
                    otherInfo.addProperty("zdx.sensor.VENDOR1", nullStr);
                    otherInfo.addProperty("zdx.sensor.VERSION1", nullStr);
                    otherInfo.addProperty("zdx.sensor.TYPE1", nullStr);
                    otherInfo.addProperty("zdx.sensor.MAX_RANGE1", nullStr);
                    otherInfo.addProperty("zdx.sensor.RESOLUTION1", nullStr);
                    otherInfo.addProperty("zdx.sensor.POWER1", nullStr);
                    otherInfo.addProperty("zdx.sensor.MAX_DEALY1", nullStr);
                    otherInfo.addProperty("zdx.sensor.MIN_DELAY1", nullStr);
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
                    otherInfo.addProperty("zdx.sensor.VENDOR3", nullStr);
                    otherInfo.addProperty("zdx.sensor.VERSION3", nullStr);
                    otherInfo.addProperty("zdx.sensor.TYPE3", nullStr);
                    otherInfo.addProperty("zdx.sensor.MAX_RANGE3", nullStr);
                    otherInfo.addProperty("zdx.sensor.RESOLUTION3", nullStr);
                    otherInfo.addProperty("zdx.sensor.POWER3", nullStr);
                    otherInfo.addProperty("zdx.sensor.MAX_DEALY3", nullStr);
                    otherInfo.addProperty("zdx.sensor.MIN_DELAY3", nullStr);
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
                    otherInfo.addProperty("zdx.sensor.VENDOR4", nullStr);
                    otherInfo.addProperty("zdx.sensor.VERSION4", nullStr);
                    otherInfo.addProperty("zdx.sensor.TYPE4", nullStr);
                    otherInfo.addProperty("zdx.sensor.MAX_RANGE4", nullStr);
                    otherInfo.addProperty("zdx.sensor.RESOLUTION4", nullStr);
                    otherInfo.addProperty("zdx.sensor.POWER4", nullStr);
                    otherInfo.addProperty("zdx.sensor.MAX_DEALY4", nullStr);
                    otherInfo.addProperty("zdx.sensor.MIN_DELAY4", nullStr);
                }

            } catch (Exception e) {
                otherInfo.addProperty("zdx.sensor.VENDOR9", nullStr);
                otherInfo.addProperty("zdx.sensor.VERSION9", nullStr);
                otherInfo.addProperty("zdx.sensor.TYPE9", nullStr);
                otherInfo.addProperty("zdx.sensor.MAX_RANGE9", nullStr);
                otherInfo.addProperty("zdx.sensor.RESOLUTION9", nullStr);
                otherInfo.addProperty("zdx.sensor.POWER9", nullStr);
                otherInfo.addProperty("zdx.sensor.MAX_DEALY9", nullStr);
                otherInfo.addProperty("zdx.sensor.MIN_DELAY9", nullStr);
                otherInfo.addProperty("zdx.sensor.VENDOR1", nullStr);
                otherInfo.addProperty("zdx.sensor.VERSION1", nullStr);
                otherInfo.addProperty("zdx.sensor.TYPE1", nullStr);
                otherInfo.addProperty("zdx.sensor.MAX_RANGE1", nullStr);
                otherInfo.addProperty("zdx.sensor.RESOLUTION1", nullStr);
                otherInfo.addProperty("zdx.sensor.POWER1", nullStr);
                otherInfo.addProperty("zdx.sensor.MAX_DEALY1", nullStr);
                otherInfo.addProperty("zdx.sensor.MIN_DELAY1", nullStr);
                otherInfo.addProperty("zdx.sensor.VENDOR3", nullStr);
                otherInfo.addProperty("zdx.sensor.VERSION3", nullStr);
                otherInfo.addProperty("zdx.sensor.TYPE3", nullStr);
                otherInfo.addProperty("zdx.sensor.MAX_RANGE3", nullStr);
                otherInfo.addProperty("zdx.sensor.RESOLUTION3", nullStr);
                otherInfo.addProperty("zdx.sensor.POWER3", nullStr);
                otherInfo.addProperty("zdx.sensor.MAX_DEALY3", nullStr);
                otherInfo.addProperty("zdx.sensor.MIN_DELAY3", nullStr);
                otherInfo.addProperty("zdx.sensor.VENDOR4", nullStr);
                otherInfo.addProperty("zdx.sensor.VERSION4", nullStr);
                otherInfo.addProperty("zdx.sensor.TYPE4", nullStr);
                otherInfo.addProperty("zdx.sensor.MAX_RANGE4", nullStr);
                otherInfo.addProperty("zdx.sensor.RESOLUTION4", nullStr);
                otherInfo.addProperty("zdx.sensor.POWER4", nullStr);
                otherInfo.addProperty("zdx.sensor.MAX_DEALY4", nullStr);
                otherInfo.addProperty("zdx.sensor.MIN_DELAY4", nullStr);
            }

            try {
                ///todo  No virtual method getSignalStrength()Landroid/telephony/SignalStrengt  崩溃
                otherInfo.addProperty("zdx.signal.IS_GSM", mTelephony.getSignalStrength().isGsm());
            } catch (Exception e) {
                e.printStackTrace();
                otherInfo.addProperty("zdx.signal.IS_GSM", nullStr);
            }
            //todo 这4个无法获取到boolean的值
            otherInfo.addProperty("zdx.signal.FAKE_ENABLE", true);
            otherInfo.addProperty("zdx.signal.cdma.FAKE_ENABLE", true);
            otherInfo.addProperty("zdx.signal.wcdma.FAKE_ENABLE", true);
            otherInfo.addProperty("zdx.traffic.mobile.FAKE_ENABLE", true);
//            List<PathBean> list = new ArrayList();
//            list.add(new PathBean("/sdcard/cpuinf", "/proc/cpuinfo"));
//            list.add(new PathBean("/sdcard/meminfo", "/proc/meminfo"));
//            String replace = new Gson().toJson(list);
//            otherInfo.addProperty("zdx.file.REPLACE", replace);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return hardwareRoot;
    }

    private static void setLocationStr(Context context, JsonObject otherInfo, Location location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
            List<LocationBean> locationBeans = new ArrayList<>();
            for (Address address : addresses) {
                LocationBean locationBean = new LocationBean();
                locationBean.setMAdminArea(address.getAdminArea());
                locationBean.setMCountryCode(address.getCountryCode());
                locationBean.setMCountryName(address.getCountryName());
                locationBean.setMFeatureName(address.getFeatureName());
                locationBean.setMLatitude(address.getLatitude() + "");
                locationBean.setMLocality(address.getLocality());
                locationBean.setMLongitude(address.getLongitude() + "");
                LocationBean.MLocaleBean localeBean = new LocationBean.MLocaleBean();
                localeBean.setCountry(address.getLocale().getCountry());
                localeBean.setLang(address.getLocale().getLanguage());
                locationBean.setMLocale(localeBean);
                locationBean.setMMaxAddressLineIndex(address.getMaxAddressLineIndex());
                locationBean.setMPhone(address.getPhone());
                locationBean.setMPostalCode(address.getPostalCode());
                locationBean.setMPremises(address.getPremises());
                locationBean.setMSubAdminArea(address.getSubAdminArea());
                locationBean.setMSubLocality(address.getSubLocality());
                locationBean.setMSubThoroughfare(address.getSubThoroughfare());
                locationBean.setMThoroughfare(address.getThoroughfare());
                locationBean.setMUrl(address.getUrl());
                locationBeans.add(locationBean);
            }
            String locationStr = new Gson().toJson(locationBeans);
            otherInfo.addProperty("zdx.loc.GEOCODER_GET_LOC", locationStr);
        } catch (IOException e) {
            e.printStackTrace();
            otherInfo.addProperty("zdx.loc.GEOCODER_GET_LOC", nullStr);
        }
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


    public static String getApplicationNameByPackageName(Context context, String packageName) {

        PackageManager pm = context.getPackageManager();
        String Name;
        try {
            Name = pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            Name = "";
        }
        return Name
                ;
    }


    /**
     * Created by danjj on 2020/11/18 0018.
     */
    public static class CellInfoBean1 {

        /**
         * mType : 1
         * CellIdentity : {"mMcc":460,"mMnc":0,"mLac":9763,"mCid":4852,"mArfcn":49,"mBsic":4}
         * CellSignalStrength : {"ss":15,"ber":99,"mTa":2147483647}
         */

        private int mType;
        private CellIdentityBean CellIdentity;
        private CellSignalStrengthBean CellSignalStrength;

        public CellInfoBean1() {
        }

        public int getMType() {
            return mType;
        }

        public void setMType(int mType) {
            this.mType = mType;
        }

        public CellIdentityBean getCellIdentity() {
            return CellIdentity;
        }

        public void setCellIdentity(CellIdentityBean CellIdentity) {
            this.CellIdentity = CellIdentity;
        }

        public CellSignalStrengthBean getCellSignalStrength() {
            return CellSignalStrength;
        }

        public void setCellSignalStrength(CellSignalStrengthBean CellSignalStrength) {
            this.CellSignalStrength = CellSignalStrength;
        }

        public static class CellIdentityBean {
            /**
             * mMcc : 460
             * mMnc : 0
             * mLac : 9763
             * mCid : 4852
             * mArfcn : 49
             * mBsic : 4
             */

            private int mMcc;
            private int mMnc;
            private int mLac;
            private int mCid;
            private int mArfcn;
            private int mBsic;

            public int getMMcc() {
                return mMcc;
            }

            public void setMMcc(int mMcc) {
                this.mMcc = mMcc;
            }

            public int getMMnc() {
                return mMnc;
            }

            public void setMMnc(int mMnc) {
                this.mMnc = mMnc;
            }

            public int getMLac() {
                return mLac;
            }

            public void setMLac(int mLac) {
                this.mLac = mLac;
            }

            public int getMCid() {
                return mCid;
            }

            public void setMCid(int mCid) {
                this.mCid = mCid;
            }

            public int getMArfcn() {
                return mArfcn;
            }

            public void setMArfcn(int mArfcn) {
                this.mArfcn = mArfcn;
            }

            public int getMBsic() {
                return mBsic;
            }

            public void setMBsic(int mBsic) {
                this.mBsic = mBsic;
            }
        }

        public static class CellSignalStrengthBean {
            /**
             * ss : 15
             * ber : 99
             * mTa : 2147483647
             */

            private int ss;
            private int ber;
            private int mTa;

            public int getSs() {
                return ss;
            }

            public void setSs(int ss) {
                this.ss = ss;
            }

            public int getBer() {
                return ber;
            }

            public void setBer(int ber) {
                this.ber = ber;
            }

            public int getMTa() {
                return mTa;
            }

            public void setMTa(int mTa) {
                this.mTa = mTa;
            }
        }
    }

    public static class CellInfoBean2 {

        /**
         * mType : 2
         * CellIdentity : {"mNetworkId":2,"mSystemId":13844,"mBasestationId":11281,"mLongitude":2147483647,"mLatitude":2147483647}
         * CellSignalStrength : {"cdmaDbm":-86,"cdmaEcio":-70,"evdoDbm":2147483647,"evdoEcio":2147483647,"evdoSnr":2147483647}
         */

        private int mType;
        private CellIdentityBean CellIdentity;
        private CellSignalStrengthBean CellSignalStrength;

        public CellInfoBean2(int mType, CellIdentityBean cellIdentity, CellSignalStrengthBean cellSignalStrength) {
            this.mType = mType;
            CellIdentity = cellIdentity;
            CellSignalStrength = cellSignalStrength;
        }

        public CellInfoBean2() {
        }

        public int getMType() {
            return mType;
        }

        public void setMType(int mType) {
            this.mType = mType;
        }

        public CellIdentityBean getCellIdentity() {
            return CellIdentity;
        }

        public void setCellIdentity(CellIdentityBean CellIdentity) {
            this.CellIdentity = CellIdentity;
        }

        public CellSignalStrengthBean getCellSignalStrength() {
            return CellSignalStrength;
        }

        public void setCellSignalStrength(CellSignalStrengthBean CellSignalStrength) {
            this.CellSignalStrength = CellSignalStrength;
        }

        public static class CellIdentityBean {
            public CellIdentityBean(int mNetworkId, int mSystemId, int mBasestationId, int mLongitude, int mLatitude) {
                this.mNetworkId = mNetworkId;
                this.mSystemId = mSystemId;
                this.mBasestationId = mBasestationId;
                this.mLongitude = mLongitude;
                this.mLatitude = mLatitude;
            }

            public CellIdentityBean() {
            }

            /**
             * mNetworkId : 2
             * mSystemId : 13844
             * mBasestationId : 11281
             * mLongitude : 2147483647
             * mLatitude : 2147483647
             */

            private int mNetworkId;
            private int mSystemId;
            private int mBasestationId;
            private int mLongitude;
            private int mLatitude;

            public int getMNetworkId() {
                return mNetworkId;
            }

            public void setMNetworkId(int mNetworkId) {
                this.mNetworkId = mNetworkId;
            }

            public int getMSystemId() {
                return mSystemId;
            }

            public void setMSystemId(int mSystemId) {
                this.mSystemId = mSystemId;
            }

            public int getMBasestationId() {
                return mBasestationId;
            }

            public void setMBasestationId(int mBasestationId) {
                this.mBasestationId = mBasestationId;
            }

            public int getMLongitude() {
                return mLongitude;
            }

            public void setMLongitude(int mLongitude) {
                this.mLongitude = mLongitude;
            }

            public int getMLatitude() {
                return mLatitude;
            }

            public void setMLatitude(int mLatitude) {
                this.mLatitude = mLatitude;
            }
        }

        public static class CellSignalStrengthBean {
            public CellSignalStrengthBean(int cdmaDbm, int cdmaEcio, int evdoDbm, int evdoEcio, int evdoSnr) {
                this.cdmaDbm = cdmaDbm;
                this.cdmaEcio = cdmaEcio;
                this.evdoDbm = evdoDbm;
                this.evdoEcio = evdoEcio;
                this.evdoSnr = evdoSnr;
            }

            public CellSignalStrengthBean() {
            }

            /**
             * cdmaDbm : -86
             * cdmaEcio : -70
             * evdoDbm : 2147483647
             * evdoEcio : 2147483647
             * evdoSnr : 2147483647
             */

            private int cdmaDbm;
            private int cdmaEcio;
            private int evdoDbm;
            private int evdoEcio;
            private int evdoSnr;

            public int getCdmaDbm() {
                return cdmaDbm;
            }

            public void setCdmaDbm(int cdmaDbm) {
                this.cdmaDbm = cdmaDbm;
            }

            public int getCdmaEcio() {
                return cdmaEcio;
            }

            public void setCdmaEcio(int cdmaEcio) {
                this.cdmaEcio = cdmaEcio;
            }

            public int getEvdoDbm() {
                return evdoDbm;
            }

            public void setEvdoDbm(int evdoDbm) {
                this.evdoDbm = evdoDbm;
            }

            public int getEvdoEcio() {
                return evdoEcio;
            }

            public void setEvdoEcio(int evdoEcio) {
                this.evdoEcio = evdoEcio;
            }

            public int getEvdoSnr() {
                return evdoSnr;
            }

            public void setEvdoSnr(int evdoSnr) {
                this.evdoSnr = evdoSnr;
            }
        }
    }


    public static class CellInfoBean3 {


        /**
         * mType : 3
         * CellIdentity : {"mMcc":460,"mMnc":11,"mCi":124946454,"mPci":2,"mTac":30496,"mEarfcn":2452}
         * CellSignalStrength : {"ss":21,"rsrp":-95,"rsrq":-8,"rssnr":2147483647,"cqi":2147483647,"ta":2147483647}
         */

        private int mType;
        private CellIdentityBean CellIdentity;
        private CellSignalStrengthBean CellSignalStrength;

        public int getMType() {
            return mType;
        }

        public void setMType(int mType) {
            this.mType = mType;
        }

        public CellIdentityBean getCellIdentity() {
            return CellIdentity;
        }

        public void setCellIdentity(CellIdentityBean CellIdentity) {
            this.CellIdentity = CellIdentity;
        }

        public CellSignalStrengthBean getCellSignalStrength() {
            return CellSignalStrength;
        }

        public void setCellSignalStrength(CellSignalStrengthBean CellSignalStrength) {
            this.CellSignalStrength = CellSignalStrength;
        }

        public static class CellIdentityBean {
            /**
             * mMcc : 460
             * mMnc : 11
             * mCi : 124946454
             * mPci : 2
             * mTac : 30496
             * mEarfcn : 2452
             */

            private int mMcc;
            private int mMnc;
            private int mCi;
            private int mPci;
            private int mTac;
            private int mEarfcn;

            public int getMMcc() {
                return mMcc;
            }

            public void setMMcc(int mMcc) {
                this.mMcc = mMcc;
            }

            public int getMMnc() {
                return mMnc;
            }

            public void setMMnc(int mMnc) {
                this.mMnc = mMnc;
            }

            public int getMCi() {
                return mCi;
            }

            public void setMCi(int mCi) {
                this.mCi = mCi;
            }

            public int getMPci() {
                return mPci;
            }

            public void setMPci(int mPci) {
                this.mPci = mPci;
            }

            public int getMTac() {
                return mTac;
            }

            public void setMTac(int mTac) {
                this.mTac = mTac;
            }

            public int getMEarfcn() {
                return mEarfcn;
            }

            public void setMEarfcn(int mEarfcn) {
                this.mEarfcn = mEarfcn;
            }
        }

        public static class CellSignalStrengthBean {
            /**
             * ss : 21
             * rsrp : -95
             * rsrq : -8
             * rssnr : 2147483647
             * cqi : 2147483647
             * ta : 2147483647
             */

            private int ss;
            private int rsrp;
            private int rsrq;
            private int rssnr;
            private int cqi;
            private int ta;

            public int getSs() {
                return ss;
            }

            public void setSs(int ss) {
                this.ss = ss;
            }

            public int getRsrp() {
                return rsrp;
            }

            public void setRsrp(int rsrp) {
                this.rsrp = rsrp;
            }

            public int getRsrq() {
                return rsrq;
            }

            public void setRsrq(int rsrq) {
                this.rsrq = rsrq;
            }

            public int getRssnr() {
                return rssnr;
            }

            public void setRssnr(int rssnr) {
                this.rssnr = rssnr;
            }

            public int getCqi() {
                return cqi;
            }

            public void setCqi(int cqi) {
                this.cqi = cqi;
            }

            public int getTa() {
                return ta;
            }

            public void setTa(int ta) {
                this.ta = ta;
            }
        }
    }

    public static class CellInfoBean4 {

        /**
         * mType : 4
         * CellIdentity : {"mMcc":460,"mMnc":1,"mLac":42303,"mCid":24128969,"mPsc":265,"mUarfcn":10663}
         * CellSignalStrength : {"ss":4,"ber":99}
         */

        private int mType;
        private CellIdentityBean CellIdentity;
        private CellSignalStrengthBean CellSignalStrength;

        public int getMType() {
            return mType;
        }

        public void setMType(int mType) {
            this.mType = mType;
        }

        public CellIdentityBean getCellIdentity() {
            return CellIdentity;
        }

        public void setCellIdentity(CellIdentityBean CellIdentity) {
            this.CellIdentity = CellIdentity;
        }

        public CellSignalStrengthBean getCellSignalStrength() {
            return CellSignalStrength;
        }

        public void setCellSignalStrength(CellSignalStrengthBean CellSignalStrength) {
            this.CellSignalStrength = CellSignalStrength;
        }

        public static class CellIdentityBean {
            /**
             * mMcc : 460
             * mMnc : 1
             * mLac : 42303
             * mCid : 24128969
             * mPsc : 265
             * mUarfcn : 10663
             */

            private int mMcc;
            private int mMnc;
            private int mLac;
            private int mCid;
            private int mPsc;
            private int mUarfcn;

            public int getMMcc() {
                return mMcc;
            }

            public void setMMcc(int mMcc) {
                this.mMcc = mMcc;
            }

            public int getMMnc() {
                return mMnc;
            }

            public void setMMnc(int mMnc) {
                this.mMnc = mMnc;
            }

            public int getMLac() {
                return mLac;
            }

            public void setMLac(int mLac) {
                this.mLac = mLac;
            }

            public int getMCid() {
                return mCid;
            }

            public void setMCid(int mCid) {
                this.mCid = mCid;
            }

            public int getMPsc() {
                return mPsc;
            }

            public void setMPsc(int mPsc) {
                this.mPsc = mPsc;
            }

            public int getMUarfcn() {
                return mUarfcn;
            }

            public void setMUarfcn(int mUarfcn) {
                this.mUarfcn = mUarfcn;
            }
        }

        public static class CellSignalStrengthBean {
            /**
             * ss : 4
             * ber : 99
             */

            private int ss;
            private int ber;

            public int getSs() {
                return ss;
            }

            public void setSs(int ss) {
                this.ss = ss;
            }

            public int getBer() {
                return ber;
            }

            public void setBer(int ber) {
                this.ber = ber;
            }
        }
    }

    public static class LocationBean {

        /**
         * mLocale : {"lang":"zh","country":"CN"}
         * mFeatureName :
         * mMaxAddressLineIndex : 1
         * mAdminArea :
         * mSubAdminArea :
         * mLocality : 广州
         * mSubLocality :
         * mThoroughfare :
         * mSubThoroughfare :
         * mPremises :
         * mPostalCode :
         * mCountryCode :
         * mCountryName : 中国
         * mLatitude :
         * mLongitude :
         * mPhone :
         * mUrl :
         */

        private LocationBean.MLocaleBean mLocale;
        private String mFeatureName;
        private int mMaxAddressLineIndex;
        private String mAdminArea;
        private String mSubAdminArea;
        private String mLocality;
        private String mSubLocality;
        private String mThoroughfare;
        private String mSubThoroughfare;
        private String mPremises;
        private String mPostalCode;
        private String mCountryCode;
        private String mCountryName;
        private String mLatitude;
        private String mLongitude;
        private String mPhone;
        private String mUrl;

        public LocationBean.MLocaleBean getMLocale() {
            return mLocale;
        }

        public void setMLocale(LocationBean.MLocaleBean mLocale) {
            this.mLocale = mLocale;
        }

        public String getMFeatureName() {
            return mFeatureName;
        }

        public void setMFeatureName(String mFeatureName) {
            this.mFeatureName = mFeatureName;
        }

        public int getMMaxAddressLineIndex() {
            return mMaxAddressLineIndex;
        }

        public void setMMaxAddressLineIndex(int mMaxAddressLineIndex) {
            this.mMaxAddressLineIndex = mMaxAddressLineIndex;
        }

        public String getMAdminArea() {
            return mAdminArea;
        }

        public void setMAdminArea(String mAdminArea) {
            this.mAdminArea = mAdminArea;
        }

        public String getMSubAdminArea() {
            return mSubAdminArea;
        }

        public void setMSubAdminArea(String mSubAdminArea) {
            this.mSubAdminArea = mSubAdminArea;
        }

        public String getMLocality() {
            return mLocality;
        }

        public void setMLocality(String mLocality) {
            this.mLocality = mLocality;
        }

        public String getMSubLocality() {
            return mSubLocality;
        }

        public void setMSubLocality(String mSubLocality) {
            this.mSubLocality = mSubLocality;
        }

        public String getMThoroughfare() {
            return mThoroughfare;
        }

        public void setMThoroughfare(String mThoroughfare) {
            this.mThoroughfare = mThoroughfare;
        }

        public String getMSubThoroughfare() {
            return mSubThoroughfare;
        }

        public void setMSubThoroughfare(String mSubThoroughfare) {
            this.mSubThoroughfare = mSubThoroughfare;
        }

        public String getMPremises() {
            return mPremises;
        }

        public void setMPremises(String mPremises) {
            this.mPremises = mPremises;
        }

        public String getMPostalCode() {
            return mPostalCode;
        }

        public void setMPostalCode(String mPostalCode) {
            this.mPostalCode = mPostalCode;
        }

        public String getMCountryCode() {
            return mCountryCode;
        }

        public void setMCountryCode(String mCountryCode) {
            this.mCountryCode = mCountryCode;
        }

        public String getMCountryName() {
            return mCountryName;
        }

        public void setMCountryName(String mCountryName) {
            this.mCountryName = mCountryName;
        }

        public String getMLatitude() {
            return mLatitude;
        }

        public void setMLatitude(String mLatitude) {
            this.mLatitude = mLatitude;
        }

        public String getMLongitude() {
            return mLongitude;
        }

        public void setMLongitude(String mLongitude) {
            this.mLongitude = mLongitude;
        }

        public String getMPhone() {
            return mPhone;
        }

        public void setMPhone(String mPhone) {
            this.mPhone = mPhone;
        }

        public String getMUrl() {
            return mUrl;
        }

        public void setMUrl(String mUrl) {
            this.mUrl = mUrl;
        }

        public static class MLocaleBean {
            /**
             * lang : zh
             * country : CN
             */

            private String lang;
            private String country;

            public String getLang() {
                return lang;
            }

            public void setLang(String lang) {
                this.lang = lang;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }
        }
    }


    public static class WifiBean {

        public WifiBean(String ssid, String bssid, String cap, int level, int freq) {
            this.ssid = ssid;
            this.bssid = bssid;
            this.cap = cap;
            this.level = level;
            this.freq = freq;
        }

        /**
         * ssid : HUAWEI-Pro2_5G
         * bssid : e4:19:c1:e1:92:94
         * cap : [WPA2-PSK-CCMP][WPS][ESS]
         * level : -46
         * freq : 5765
         */

        private String ssid;
        private String bssid;
        private String cap;
        private int level;
        private int freq;

        public String getSsid() {
            return ssid;
        }

        public void setSsid(String ssid) {
            this.ssid = ssid;
        }

        public String getBssid() {
            return bssid;
        }

        public void setBssid(String bssid) {
            this.bssid = bssid;
        }

        public String getCap() {
            return cap;
        }

        public void setCap(String cap) {
            this.cap = cap;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getFreq() {
            return freq;
        }

        public void setFreq(int freq) {
            this.freq = freq;
        }
    }

    public static class PackageInfoBean {

        public String appName;
        public String packageName;

        public PackageInfoBean(String appName, String packageName) {
            this.appName = appName;
            this.packageName = packageName;
        }
    }

}
