package com.zhou.testlibrary.speech.baidu.speech.listener

import android.os.Handler
import android.os.Message
import android.util.Log
import com.baidu.speech.asr.SpeechConstant
import com.zhou.testlibrary.speech.baidu.speech.recog.RecogResult

/**
 * @author: zl
 * @date: 2023/12/19
 */
class MessageStatusRecogListener(var handler: Handler?) : StatusRecogListener() {

    private var speechEndTime: Long = 0

    private val needTime = true

    private val TAG = "MesStatusRecogListener"

    override fun onAsrReady() {
        super.onAsrReady()
        speechEndTime = 0
        sendStatusMessage(SpeechConstant.CALLBACK_EVENT_WAKEUP_READY, "引擎就绪，可以开始说话。")
    }

    override fun onAsrBegin() {
        super.onAsrBegin()
        sendStatusMessage(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN, "检测到用户说话")
    }

    override fun onAsrEnd() {
        super.onAsrEnd()
        speechEndTime = System.currentTimeMillis()
        sendMessage("【asr.end事件】检测到用户说话结束")
    }

    override fun onAsrPartialResult(results: Array<String?>?, recogResult: RecogResult?) {
        sendStatusMessage(
            SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL,
            "临时识别结果，结果是“" + (results?.get(0) ?: "") + "”；原始json：" + recogResult?.getOrigalJson()
        )
        super.onAsrPartialResult(results, recogResult)
    }

    override fun onAsrFinalResult(results: Array<String?>?, recogResult: RecogResult?) {
        super.onAsrFinalResult(results, recogResult)
        var message = "识别结束，结果是”" + (results?.get(0) ?: "") + "”"
        sendStatusMessage(
            SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL,
            message + "；原始json：" + recogResult?.getOrigalJson()
        )
        if (speechEndTime > 0) {
            val currentTime = System.currentTimeMillis()
            val diffTime = currentTime - speechEndTime
            message += "；说话结束到识别结束耗时【" + diffTime + "ms】" + currentTime
        }
        speechEndTime = 0
        sendMessage(message, status, true)
    }

    override fun onAsrFinishError(
        errorCode: Int, subErrorCode: Int, descMessage: String?,
        recogResult: RecogResult?
    ) {
        super.onAsrFinishError(errorCode, subErrorCode, descMessage, recogResult)
        var message =
            "【asr.finish事件】识别错误, 错误码：$errorCode ,$subErrorCode ; $descMessage"
        sendStatusMessage(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL, message)
        if (speechEndTime > 0) {
            val diffTime = System.currentTimeMillis() - speechEndTime
            message += "。说话结束到识别结束耗时【" + diffTime + "ms】"
        }
        speechEndTime = 0
        sendMessage(message, status, true)
        speechEndTime = 0
    }

    override fun onAsrOnlineNluResult(nluResult: String?) {
        super.onAsrOnlineNluResult(nluResult)
        if (!nluResult?.isEmpty()!!) {
            sendStatusMessage(
                SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL,
                "原始语义识别结果json：$nluResult"
            )
        }
    }

    override fun onAsrFinish(recogResult: RecogResult?) {
        super.onAsrFinish(recogResult)
        sendStatusMessage(
            SpeechConstant.CALLBACK_EVENT_ASR_FINISH,
            "识别一段话结束。如果是长语音的情况会继续识别下段话。"
        )
    }

    /**
     * 长语音识别结束
     */
    override fun onAsrLongFinish() {
        super.onAsrLongFinish()
        sendStatusMessage(SpeechConstant.CALLBACK_EVENT_ASR_LONG_SPEECH, "长语音识别结束。")
    }


    /**
     * 使用离线命令词时，有该回调说明离线语法资源加载成功
     */
    override fun onOfflineLoaded() {
        sendStatusMessage(
            SpeechConstant.CALLBACK_EVENT_ASR_LOADED,
            "离线资源加载成功。没有此回调可能离线语法功能不能使用。"
        )
    }

    /**
     * 使用离线命令词时，有该回调说明离线语法资源加载成功
     */
    override fun onOfflineUnLoaded() {
        sendStatusMessage(SpeechConstant.CALLBACK_EVENT_ASR_UNLOADED, "离线资源卸载成功。")
    }

    override fun onAsrExit() {
        super.onAsrExit()
        sendStatusMessage(SpeechConstant.CALLBACK_EVENT_ASR_EXIT, "识别引擎结束并空闲中")
    }

    private fun sendStatusMessage(eventName: String, message: String) {
        var message = message
        message = "[$eventName]$message"
        sendMessage(message, status)
    }

    private fun sendMessage(message: String) {
        sendMessage(message, WHAT_MESSAGE_STATUS)
    }

    private fun sendMessage(message: String, what: Int) {
        sendMessage(message, what, false)
    }


    private fun sendMessage(message: String, what: Int, highlight: Boolean) {
        var message = message
        if (needTime && what != STATUS_FINISHED) {
            message += "  ;time=" + System.currentTimeMillis()
        }
        if (handler == null) {
            Log.i(TAG, message)
            return
        }
        val msg = Message.obtain()
        msg.what = what
        msg.arg1 = status
        if (highlight) {
            msg.arg2 = 1
        }
        msg.obj = """
            $message
            
            """.trimIndent()
        handler!!.sendMessage(msg)
    }
}