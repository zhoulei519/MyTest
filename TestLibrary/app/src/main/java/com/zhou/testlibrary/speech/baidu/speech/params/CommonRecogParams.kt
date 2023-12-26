package com.zhou.testlibrary.speech.baidu.speech.params

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import com.baidu.speech.asr.SpeechConstant
import com.zhou.testlibrary.R
import com.zhou.testlibrary.baidu.speecha.utils.FileUtil

/**
 * @author: zl
 * @date: 2023/12/19
 */
open class CommonRecogParams {
    private var samplePath: String? = null

    /**
     * 字符串格式的参数
     */
    var stringParams = ArrayList<String>()

    /**
     * int格式的参数
     */
    var intParams = ArrayList<String>()

    /**
     * bool格式的参数
     */
    var boolParams = ArrayList<String>()


    init {
        stringParams.addAll(
            listOf(
                SpeechConstant.VAD,
                SpeechConstant.IN_FILE
            )
        )
        intParams.addAll(
            listOf(
                SpeechConstant.PID,
                SpeechConstant.LMID,
                SpeechConstant.VAD_ENDPOINT_TIMEOUT
            )
        )
        boolParams.addAll(
            listOf(
                SpeechConstant.BDS_ASR_ENABLE_LONG_SPEECH,
                SpeechConstant.ACCEPT_AUDIO_DATA,
                SpeechConstant.ACCEPT_AUDIO_VOLUME
            )
        )
    }

    /**
     * 创建保存OUTFILE的临时目录. 仅用于OUTFILE参数。不使用demo中的OUTFILE参数可忽略此段
     *
     * @param context
     */
    fun initSamplePath(context: Context) {
        val sampleDir = "baiduASR"
        samplePath = Environment.getExternalStorageDirectory().toString() + "/" + sampleDir
        if (!FileUtil.makeDir(samplePath)) {
            samplePath = context.getExternalFilesDir(sampleDir)!!.absolutePath
            if (!FileUtil.makeDir(samplePath)) {
                throw RuntimeException("创建临时目录失败 :$samplePath")
            }
        }
    }

    fun fetch(sp: SharedPreferences): MutableMap<String?, Any?> {
        val map: MutableMap<String?, Any?> = HashMap()
        parseParamArr(sp, map)
        if (sp.getBoolean("_tips_sound", false)) { // 声音回调
            map[SpeechConstant.SOUND_START] = R.raw.bdspeech_recognition_start
            map[SpeechConstant.SOUND_END] = R.raw.bdspeech_speech_end
            map[SpeechConstant.SOUND_SUCCESS] = R.raw.bdspeech_recognition_success
            map[SpeechConstant.SOUND_ERROR] = R.raw.bdspeech_recognition_error
            map[SpeechConstant.SOUND_CANCEL] = R.raw.bdspeech_recognition_cancel
        }
        if (sp.getBoolean("_outfile", false)) { // 保存录音文件
            map[SpeechConstant.ACCEPT_AUDIO_DATA] = true // 目前必须开启此回掉才嫩保存音频
            map[SpeechConstant.OUT_FILE] = "$samplePath/outfile.pcm"
        }
        return map
    }

    /**
     * 根据 stringParams intParams boolParams中定义的参数名称，提取SharedPreferences相关字段
     *
     * @param sp
     * @param map
     */
    private fun parseParamArr(sp: SharedPreferences, map: MutableMap<String?, Any?>) {
        for (name in stringParams) {
            if (sp.contains(name)) {
                val tmp = sp.getString(name, "")!!.replace(",.*".toRegex(), "").trim { it <= ' ' }
                if (null != tmp && "" != tmp) {
                    map[name] = tmp
                }
            }
        }
        for (name in intParams) {
            if (sp.contains(name)) {
                val tmp = sp.getString(name, "")!!.replace(",.*".toRegex(), "").trim { it <= ' ' }
                if (null != tmp && "" != tmp) {
                    map[name] = tmp.toInt()
                }
            }
        }
        for (name in boolParams) {
            if (sp.contains(name)) {
                val res = sp.getBoolean(name, false)
                if (res || name == SpeechConstant.ACCEPT_AUDIO_VOLUME) {
                    map[name] = res
                }
            }
        }
    }
}