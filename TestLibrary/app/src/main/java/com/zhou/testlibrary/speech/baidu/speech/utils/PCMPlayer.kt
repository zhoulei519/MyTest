package com.zhou.testlibrary.speech.baidu.speech.utils

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileInputStream

/**
 * @author: zl
 * @date: 2023/12/22
 */
@RequiresApi(Build.VERSION_CODES.M)
class PCMPlayer {
    private var mAudioTrack: AudioTrack? = null
    private var mEnd = false

    init {
        createAudioTrack()
    }

    /**
     * channel 暂只支持单声道和双声道
     * deepness 暂只支持8位和16位
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun createAudioTrack(sampleRate: Int = 44100, channel: Int = 2, deepness: Int = 16) {
        mAudioTrack?.apply {
            destroy()
        }

        mEnd = false
        val format = AudioFormat.Builder()
            .setChannelMask(if (channel == 2) AudioFormat.CHANNEL_OUT_STEREO else AudioFormat.CHANNEL_IN_DEFAULT)
            .setSampleRate(sampleRate)
            .setEncoding(if (deepness == 16) AudioFormat.ENCODING_PCM_16BIT else AudioFormat.ENCODING_PCM_8BIT)
            .build()

        mAudioTrack = AudioTrack.Builder()
            .setAudioFormat(format)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .setBufferSizeInBytes(File(getSDCardPathByEnvironment()+"/Download/16k_test.pcm").length().toInt())
            .build()
    }

    open fun getSDCardPathByEnvironment(): String? {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            Environment.getExternalStorageDirectory().absolutePath
        } else ""
    }

    /**
     * 可跳过头部 from 个字节，用于播放 WAV 格式音频
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun playPcm(pcmFilePath: String, from: Int) {
        if (mAudioTrack == null) {
            throw IllegalStateException("AudioTrack not found, forget createAudioTrack ?")
        }
        mAudioTrack?.apply {
            play()
            val inputStream = FileInputStream(File(pcmFilePath))
            val buffer = ByteArray(bufferSizeInFrames)
            var len = 0
            Observable.just(inputStream)
                .subscribeOn(Schedulers.io())
                .subscribe {
                    it.use { input ->
                        var indexFrom = from
                        while ((input.read(buffer, 0, buffer.size).apply { len = this }) > 0 && !mEnd) {
                            write(buffer, indexFrom, len - indexFrom)
                            indexFrom = 0
                        }
                    }
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun playPcm(pcmFilePath: String) {
        playPcm(getSDCardPathByEnvironment()+"/Download/16k_test.pcm", 0)
    }

    fun destroy() {
        mAudioTrack?.apply {
            mEnd = true
            pause()
            flush()
            release()
        }
    }
}