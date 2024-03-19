package com.zhou.common.utils

import android.content.Context
import java.lang.reflect.Method
import android.telephony.TelephonyManager
import java.lang.Exception


object TelephonyUtil {

    /**
     * 获取数据流量开关状态
     * - 有sim卡的情况下结果是正确的，无sim卡获取的状态不准确
     */
    fun getDataEnabled(context: Context): Any? {
        val telephonyService = context.getSystemService(Context.TELEPHONY_SERVICE)
        val getDataEnabled: Method = telephonyService.javaClass.getDeclaredMethod("getDataEnabled")
        return getDataEnabled.invoke(telephonyService)
    }

    /**
     * 检测手机卡状态是否正常
     */
    fun isSimCardReadyInner(context: Context?): Boolean {
        try {
            LogUtil.d("TimeValueData", "call isSimCardReadyInner")
            if (context == null) {
                return true
            }
            val telephonyManager: TelephonyManager = getTelephonyManager(context) ?: return true
            val simState = telephonyManager.simState
            return simState == TelephonyManager.SIM_STATE_READY
        } catch (e: Exception) {
            LogUtil.e("CommonUtils", "isSimCardReady: $e")
        }
        return true
    }
    private fun getTelephonyManager(context: Context): TelephonyManager? {
        val telephonyManager  = context.getSystemService(Context.TELEPHONY_SERVICE)
        return telephonyManager as TelephonyManager?
    }
}