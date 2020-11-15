package com.example.hardware

import android.content.Context
import android.os.Build
import android.os.Process.killProcess
import android.os.Process.myPid
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import java.util.*
import kotlin.concurrent.schedule

object DeviceUtil {

    /**
     * 退出 APP
     */
    fun exitApp() {
        Timer().schedule(500) {
            //获取PID，目前获取自己的也只有该API，否则从/proc中自己的枚举其他进程吧，不过要说明的是，结束其他进程不一定有权限，不然就乱套了。// 建议使用
            killProcess(myPid())
        }
    }

    /**
     * 获取设备唯一 ID
     * @param context 上下文
     * @return 设备唯一 ID
     */
    fun getUniqueId(context: Context): String {
        // 不选用需要权限的获取 ID 方式
        val data = getAndroidId(context) + getSerialNumber() + getUniquePsuedoId() + getUuid(context)
        return EncryptUtil.md5(data).toUpperCase(Locale.CHINA)
    }

    /**
     * 获取 UUID
     * @param context 上下文
     */
    fun getUuid(context: Context): String {
        // UUID 键
        val key = "key_uuid"
        // 获取 SharedPreferences
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        // 获取 UUID
        var uuid: String? = preferences.getString(key, "")
        // UUID 为空值
        if (uuid.isNullOrEmpty()) {
            // 创建新的 UUID
            uuid = UUID.randomUUID().toString()
            // 保存
            preferences?.edit()?.putString(key, uuid)?.apply()
        }
        Log.d("uuid", "uuid: $uuid")
        return uuid
    }

    /**
     * 获取 Android ID
     * @param context 上下文
     * @return androidId
     */
    private fun getAndroidId(context: Context): String =
            Settings.System.getString(context.contentResolver, Settings.System.ANDROID_ID)

    /**
     * 获取序列号
     * @return 序列号
     */
    private fun getSerialNumber(): String = Build.SERIAL

    /**
     * 伪 IMEI
     * @return 伪 IMEI
     */
    private fun getUniquePsuedoId(): String? =
            "35" +
                    Build.BOARD.length % 10 +
                    Build.BRAND.length % 10 +
                    Build.CPU_ABI.length % 10 +
                    Build.DEVICE.length % 10 +
                    Build.DISPLAY.length % 10 +
                    Build.HOST.length % 10 +
                    Build.ID.length % 10 +
                    Build.MANUFACTURER.length % 10 +
                    Build.MODEL.length % 10 +
                    Build.PRODUCT.length % 10 +
                    Build.TAGS.length % 10 +
                    Build.TYPE.length % 10 +
                    Build.USER.length % 10 //13 digits

}