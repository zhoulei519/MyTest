package com.zhou.common.utils.crash

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.format.DateFormat
import com.zhou.common.utils.LogUtil
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Date

class CrashHandler
/**
 * 保证只有一个 CrashHandler 实例
 */
private constructor() : Thread.UncaughtExceptionHandler {
    //系统默认的 UncaughtException 处理类
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null


    private var mContext : Context?=null

    /**
     * @throws
     * @Title: init
     * @Description: 初始化
     */
    fun init(context : Context?) {
        this.mContext = context
        //获取系统默认的 UncaughtException 处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        //设置该 CrashHandler 为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    /**
     * 当 UncaughtException 发生时会转入该函数来处理
     */
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        val crashInfo = StringBuilder()
        crashInfo.append("Crash time: ").append(DateFormat.format("yyyy-MM-dd HH:mm:ss", Date()).toString()).append('\n')
        crashInfo.append("Device manufacturer: ").append(Build.MANUFACTURER).append('\n')
        crashInfo.append("Device model: ").append(Build.MODEL).append('\n')
        crashInfo.append("Android version: ").append(Build.VERSION.RELEASE).append('\n')
        crashInfo.append('\n')
        crashInfo.append(getStackTraceString(ex))

        saveCrashLogToFile(crashInfo.toString())
        sendCrashLogToRemoteServer(crashInfo.toString())

        mDefaultHandler?.uncaughtException(thread, ex)
    }

    private fun getStackTraceString(ex: Throwable): String {
        val result = StringWriter()
        val printWriter = PrintWriter(result)
        ex.printStackTrace(printWriter)
        return result.toString()
    }

    private fun saveCrashLogToFile(crashInfo: String) {
        val logFile = File(mContext?.getExternalFilesDir(null), "crash_log.txt")
        try {
            val writer = FileWriter(logFile, true)
            writer.write(crashInfo)
            writer.flush()
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun sendCrashLogToRemoteServer(crashInfo: String) {
        // 在这里实现将崩溃日志发送到远程服务器的逻辑
        LogUtil.e(crashInfo)
    }

    companion object {
        /**
         * 获取 CrashHandler 实例 ,单例模式
         */
        //CrashHandler 实例
        @SuppressLint("StaticFieldLeak")
        val instance = CrashHandler()
    }


}