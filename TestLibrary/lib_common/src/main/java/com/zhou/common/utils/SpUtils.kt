package com.zhou.common.utils

import android.content.Context
import com.google.gson.Gson
import com.tencent.mmkv.MMKV

/**
 * MMKV使用封装
 *
 * @author zl
 * @since 8/28/20
 */
object SpUtils {

    /**
     * 初始化
     */
    fun initMMKV(context: Context): String? = MMKV.initialize(context)

    /**
     * 保存数据（简化）
     * 根据value类型自动匹配需要执行的方法
     */
    fun put(key: String, value: Any) =
        when (value) {
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            is Double -> putDouble(key, value)
            is String -> putString(key, value)
            is Boolean -> putBoolean(key, value)
            else -> false
        }

    private fun putString(key: String, value: String): Boolean? =
        MMKV.defaultMMKV()?.encode(key, value)

    fun getString(key: String, defValue: String = ""): String? =
        MMKV.defaultMMKV()?.decodeString(key, defValue)

    private fun putInt(key: String, value: Int): Boolean? = MMKV.defaultMMKV()?.encode(key, value)

    fun getInt(key: String, defValue: Int = 0): Int? = MMKV.defaultMMKV()?.decodeInt(key, defValue)

    private fun putLong(key: String, value: Long): Boolean? = MMKV.defaultMMKV()?.encode(key, value)

    fun getLong(key: String, defValue: Long = 0L): Long? = MMKV.defaultMMKV()?.decodeLong(key, defValue)

    private fun putDouble(key: String, value: Double): Boolean? =
        MMKV.defaultMMKV()?.encode(key, value)

    fun getDouble(key: String, defValue: Double = 0.0): Double? =
        MMKV.defaultMMKV()?.decodeDouble(key, defValue)

    private fun putFloat(key: String, value: Float): Boolean? =
        MMKV.defaultMMKV()?.encode(key, value)

    fun getFloat(key: String, defValue: Float = 0f): Float? =
        MMKV.defaultMMKV()?.decodeFloat(key, defValue)

    private fun putBoolean(key: String, value: Boolean): Boolean? =
        MMKV.defaultMMKV()?.encode(key, value)

    fun getBoolean(key: String, defValue: Boolean = false): Boolean? =
        MMKV.defaultMMKV()?.decodeBool(key, defValue)

    fun contains(key: String): Boolean? = MMKV.defaultMMKV()?.contains(key)

    fun <T> setArray(list: ArrayList<T>, name: String): Boolean? {
        val kv = MMKV.defaultMMKV()
        if (list == null || list.isEmpty()) { //清空
            kv!!.putInt(name + "size", 0)
            val size = kv.getInt(name + "size", 0)
            for (i in 0 until size) {
                if (kv.getString(name + i, null) != null) {
                    kv.remove(name + i)
                }
            }
        } else {
            kv!!.putInt(name + "size", list.size)
            if (list.size > 40) {
                list.removeAt(0) //只保留后20条记录
            }
            for (i in list.indices) {
                kv.remove(name + i)
                kv.remove(Gson().toJson(list[i])) //删除重复数据 先删后加
                kv.putString(name + i, Gson().toJson(list[i]))
            }
        }
        return kv.commit()
    }

    fun <T> getArray(name: String, bean: T): ArrayList<T> {
        val kv = MMKV.defaultMMKV()
        val list = ArrayList<T>()
        val size = kv!!.getInt(name + "size", 0)
        for (i in 0 until size) {
            if (kv.getString(name + i, null) != null) {
                try {
                    list.add(Gson().fromJson(kv.getString(name + i, null),bean!!::class.java) as T)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return list
    }
}