package com.zhou.testlibrary.speech.baidu.speech.listener

/**
 * @author: zl
 * @date: 2023/12/19
 */
interface IStatus {
    val STATUS_NONE: Int
        get() = 2

    val STATUS_READY: Int
        get() = 3
    val STATUS_SPEAKING: Int
        get() = 4
    val STATUS_RECOGNITION: Int
        get() = 5

    val STATUS_FINISHED: Int
        get() = 6
    val STATUS_LONG_SPEECH_FINISHED: Int
        get() = 7
    val STATUS_STOPPED: Int
        get() = 10

    val STATUS_WAITING_READY: Int
        get() = 8001
    val WHAT_MESSAGE_STATUS: Int
        get() = 9001

    val STATUS_WAKEUP_SUCCESS: Int
        get() = 7001
    val STATUS_WAKEUP_EXIT: Int
        get() = 7003
}