package com.zhou.testlibrary.speech.baidu.speech.listener

import android.util.Log
import com.zhou.testlibrary.speech.baidu.speech.recog.RecogResult

/**
 * @author: zl
 * @date: 2023/12/19
 */
open class StatusRecogListener : IRecogListener, IStatus {
    private val TAG = "StatusRecogListener"

    /**
     * 识别的引擎当前的状态
     */
    protected var status = STATUS_NONE

    override fun onAsrReady() {
        status = STATUS_READY
    }

    override fun onAsrBegin() {
        status = STATUS_SPEAKING
    }

    override fun onAsrEnd() {
        status = STATUS_RECOGNITION
    }

    override fun onAsrPartialResult(results: Array<String?>?, recogResult: RecogResult?) {}

    override fun onAsrFinalResult(results: Array<String?>?, recogResult: RecogResult?) {
        status = STATUS_FINISHED
    }

    override fun onAsrFinish(recogResult: RecogResult?) {
        status = STATUS_FINISHED
    }


    override fun onAsrFinishError(
        errorCode: Int, subErrorCode: Int, descMessage: String?,
        recogResult: RecogResult?
    ) {
        status = STATUS_FINISHED
    }

    override fun onAsrLongFinish() {
        status = STATUS_LONG_SPEECH_FINISHED
    }

    override fun onAsrVolume(volumePercent: Int, volume: Int) {
        Log.i(TAG, "音量百分比$volumePercent ; 音量$volume")
    }

    override fun onAsrAudio(data: ByteArray?, offset: Int, length: Int) {
        var data = data
        if (offset != 0 || data?.size != length) {
            val actualData = ByteArray(length)
            System.arraycopy(data, 0, actualData, 0, length)
            data = actualData
        }
        Log.i(TAG, "音频数据回调, length:" + data.size)
    }

    override fun onAsrExit() {
        status = STATUS_NONE
    }

    override fun onAsrOnlineNluResult(nluResult: String?) {
        status = STATUS_FINISHED
    }

    override fun onOfflineLoaded() {}

    override fun onOfflineUnLoaded() {}
}