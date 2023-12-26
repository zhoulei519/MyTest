package com.zhou.testlibrary.speech.baidu.speech.params

import com.baidu.speech.asr.SpeechConstant

/**
 * @author: zl
 * @date: 2023/12/19
 */
class OnlineRecogParams : CommonRecogParams() {
    init {
        stringParams.addAll(
            mutableListOf(
                "_language",  // 用于生成PID参数
                "_model" // 用于生成PID参数
            )
        )
        intParams.addAll(listOf(SpeechConstant.PROP))
        boolParams.addAll(listOf(SpeechConstant.DISABLE_PUNCTUATION))
    }
}