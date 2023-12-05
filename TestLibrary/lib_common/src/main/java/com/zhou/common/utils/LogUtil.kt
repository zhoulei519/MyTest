package com.zhou.common.utils

import android.util.Log

object LogUtil {
    private var isDebug: Boolean = true
    private const val TAG: String = "ZHOU"

    fun setDebug(isDebug:Boolean){
        this.isDebug = isDebug
    }

    /**
     *包装log.d日志
     */
    fun d(msg: String) {
        if (isDebug) {
            Log.d(TAG, msg)
        }
    }
    fun d(tag: String,vararg msg:String){
        if (isDebug) {
            var msgS = ""
            for ((index,item) in msg.withIndex()){
                msgS += if(index == 0){
                    item
                }else {
                    (" $item")
                }
            }
            Log.d(tag, msgS)
        }
    }

    /**
     *包装log.e日志
     */
    fun e(msg: String) {
        if (isDebug) {
            Log.e(TAG, msg)
        }
    }
    fun e(tag: String,vararg msg:String){
        if (isDebug) {
            var msgS = "";
            for ((index,item) in msg.withIndex()){
                msgS += if(index == 0){
                    item
                }else {
                    (" $item")
                }
            }
            Log.e(tag, msgS)
        }
    }

    /**
     * v类型的log.v日志
     */
    fun v(msg: String) {
        if (isDebug) {
            Log.v(TAG, msg)
        }
    }
    fun v(tag: String,vararg msg:String){
        if (isDebug) {
            var msgS = "";
            for ((index,item) in msg.withIndex()){
                msgS += if(index == 0){
                    item
                }else {
                    (" $item")
                }
            }
            Log.v(tag, msgS)
        }
    }
}

