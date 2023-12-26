package com.zhou.testlibrary.speech.baidu.speech.listener

import com.zhou.testlibrary.speech.baidu.speech.recog.RecogResult

/**
 * @author: zl
 * @date: 2023/12/19
 */
interface IRecogListener {

    /**
     * CALLBACK_EVENT_ASR_READY
     * ASR_START 输入事件调用后，引擎准备完毕
     */
    fun onAsrReady()

    /**
     * CALLBACK_EVENT_ASR_BEGIN
     * onAsrReady后检查到用户开始说话
     */
    fun onAsrBegin()

    /**
     * CALLBACK_EVENT_ASR_END
     * 检查到用户开始说话停止，或者ASR_STOP 输入事件调用后，
     */
    fun onAsrEnd()

    /**
     * CALLBACK_EVENT_ASR_PARTIAL resultType=partial_result
     * onAsrBegin 后 随着用户的说话，返回的临时结果
     *
     * @param results     可能返回多个结果，请取第一个结果
     * @param recogResult 完整的结果
     */
    fun onAsrPartialResult(results: Array<String?>?, recogResult: RecogResult?)

    /**
     * 语音的在线语义结果
     *
     * CALLBACK_EVENT_ASR_PARTIAL resultType=nlu_result
     * @param nluResult
     */
    fun onAsrOnlineNluResult(nluResult: String?)

    /**
     * 不开启长语音仅回调一次，长语音的每一句话都会回调一次
     * CALLBACK_EVENT_ASR_PARTIAL resultType=final_result
     * 最终的识别结果
     *
     * @param results     可能返回多个结果，请取第一个结果
     * @param recogResult 完整的结果
     */
    fun onAsrFinalResult(results: Array<String?>?, recogResult: RecogResult?)

    /**
     * CALLBACK_EVENT_ASR_FINISH
     * @param recogResult 结束识别
     */
    fun onAsrFinish(recogResult: RecogResult?)

    /**
     * CALLBACK_EVENT_ASR_FINISH error!=0
     *
     * @param errorCode
     * @param subErrorCode
     * @param descMessage
     * @param recogResult
     */
    fun onAsrFinishError(
        errorCode: Int, subErrorCode: Int, descMessage: String?,
        recogResult: RecogResult?
    )

    /**
     * 长语音识别结束
     */
    fun onAsrLongFinish()

    /**
     * CALLBACK_EVENT_ASR_VOLUME
     * 音量回调
     *
     * @param volumePercent 音量的相对值，百分比，0-100
     * @param volume 音量绝对值
     */
    fun onAsrVolume(volumePercent: Int, volume: Int)

    /**
     * CALLBACK_EVENT_ASR_AUDIO
     * @param data pcm格式，16bits 16000采样率
     *
     * @param offset
     * @param length
     */
    fun onAsrAudio(data: ByteArray?, offset: Int, length: Int)

    /**
     * CALLBACK_EVENT_ASR_EXIT
     * 引擎完成整个识别，空闲中
     */
    fun onAsrExit()

    /**
     * CALLBACK_EVENT_ASR_LOADED
     * 离线命令词资源加载成功
     */
    fun onOfflineLoaded()

    /**
     * CALLBACK_EVENT_ASR_UNLOADED
     * 离线命令词资源释放成功
     */
    fun onOfflineUnLoaded()
}