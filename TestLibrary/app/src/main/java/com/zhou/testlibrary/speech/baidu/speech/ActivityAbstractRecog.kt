package com.zhou.testlibrary.speech.baidu.speech

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.baidu.speech.asr.SpeechConstant
import com.google.gson.Gson
import com.zhou.testlibrary.R
import com.zhou.testlibrary.speech.baidu.speech.listener.IRecogListener
import com.zhou.testlibrary.speech.baidu.speech.listener.IStatus
import com.zhou.testlibrary.speech.baidu.speech.listener.MessageStatusRecogListener
import com.zhou.testlibrary.speech.baidu.speech.params.CommonRecogParams
import com.zhou.testlibrary.speech.baidu.speech.params.OnlineRecogParams
import com.zhou.testlibrary.speech.baidu.speech.recog.MyRecognizer
import com.zhou.testlibrary.speech.baidu.speech.utils.AuthUtil
import com.zhou.testlibrary.speech.baidu.speech.utils.AutoCheck
import com.zhou.testlibrary.speech.baidu.speech.utils.PCMPlayer
import com.zhou.testlibrary.speech.xunfei.LfasrSignature
import com.zhou.testlibrary.speech.xunfei.OrderResultData
import com.zhou.testlibrary.speech.xunfei.ResponseData
import com.zhou.testlibrary.utils.LogUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okio.BufferedSink
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.ByteBuffer
import java.security.SignatureException


/**
 * @author: zl
 * @date: 2023/12/19
 */
open class ActivityAbstractRecog : AppCompatActivity(), IStatus {
    private var handler: Handler? = null
    private var apiParams: CommonRecogParams = OnlineRecogParams()
    val tag = "ActivityAbstractRecon"

    private var myRecognizer: MyRecognizer? = null

    private var pcmPlayer: PCMPlayer? = null

    private lateinit var btn:Button
    private var status = STATUS_NONE


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common)
        initView()
        initPermission()
        handler = MyHandler(this)
        apiParams.initSamplePath(this)
        val listener: IRecogListener = MessageStatusRecogListener(handler)
        myRecognizer = MyRecognizer(this, listener)

//        pcmPlayer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            PCMPlayer()
//        } else {
//            TODO("VERSION.SDK_INT < M")
//        }

    }

    fun initView(){
        btn = findViewById(R.id.btn)
        btn.setOnClickListener {
            when (status) {
                STATUS_NONE -> {
                    start()
                    status = STATUS_WAITING_READY
                    updateBtnTextByStatus()
                }

                STATUS_WAITING_READY, STATUS_READY, STATUS_SPEAKING, STATUS_FINISHED, STATUS_RECOGNITION -> {
                    stop()
                    status = STATUS_STOPPED // 引擎识别中
                    updateBtnTextByStatus()
                }

                STATUS_LONG_SPEECH_FINISHED, STATUS_STOPPED -> {
                    cancel()
                    status = STATUS_NONE // 识别结束，回到初始状态
                    updateBtnTextByStatus()
                }

                else -> {}
            }
        }
    }

    private fun updateBtnTextByStatus() {
        when (status) {
            STATUS_NONE -> {
                btn.text = "开始录音"
                btn.isEnabled = true
            }

            STATUS_WAITING_READY, STATUS_READY, STATUS_SPEAKING, STATUS_RECOGNITION -> {
                btn.text = "停止录音"
                btn.isEnabled = true
            }

            STATUS_LONG_SPEECH_FINISHED, STATUS_STOPPED -> {
                btn.text = "取消整个识别过程"
                btn.isEnabled = true
            }

            else -> {}
        }
    }

    private fun handleMsg(msg: Message) {
        val currentTime = System.currentTimeMillis()
        val diffTime = currentTime - startTime
        LogUtil.d("耗时","识别耗时【" + diffTime + "ms】")
        Log.d(
            "Msg",
            msg.obj.toString()
        )
        when (msg.what) {
            STATUS_FINISHED -> {
                if (msg.arg2 == 1) {
//                    txtResult.setText(msg.obj.toString())
                }
                status = msg.what
                updateBtnTextByStatus()
            }

            STATUS_NONE, STATUS_READY, STATUS_SPEAKING, STATUS_RECOGNITION -> {
                status = msg.what
                updateBtnTextByStatus()
            }

            else -> {}
        }
    }

    var startTime = 0L
    /**
     * 开始录音，点击“开始”按钮后调用。
     * 基于DEMO集成2.1, 2.2 设置识别参数并发送开始事件
     */
    private fun start() {
//        var url = "http://10.52.70.59:8100/filestore/file/download/i/12938?extension=mp3&redirect=download"
//        DownloadAndConvertTask(cacheDir.path).execute(url)


//        val mp3filepath = getSDCardPathByEnvironment()+"/Download/20231215164543.mp3" //mp3文件路径
//        val pcmfilepath = getSDCardPathByEnvironment()+"/Download/20231215164543.pcm" //转化后的pcm路径
//        Mp3ToPcm.Mp3ToPcm(mp3filepath, pcmfilepath)

//        val audioPlayer = AudioPlayer(this, null,2,false)
//        audioPlayer.startPlayTaskAudio(getSDCardPathByEnvironment()+"/Download/outfile.pcm",ImageView(this),"0L")
//        audioPlayer.startPlayTaskAudio("http://10.41.243.185:8100/filestore/file/download/i/14827?extension=pcm&redirect=download",ImageView(this),"0L")
//        audioPlayer.startPlayTaskAudio("http://10.52.70.59:8100/filestore/file/download/i/12938?extension=mp3&redirect=download",ImageView(this),"0L")



//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            pcmPlayer?.playPcm("")
//        }
    }

    open fun identifyPcm(pcmPath: String){
        startTime = System.currentTimeMillis()
        // DEMO集成步骤2.1 拼接识别参数： 此处params可以打印出来，直接写到你的代码里去，最终的json一致即可。
        val params: Map<String?, Any?> = fetchParams(pcmPath)
        // params 也可以根据文档此处手动修改，参数会以json的格式在界面和logcat日志中打印
        Log.i(tag, "设置的start输入参数：$params")
        // params.put(SpeechConstant.NLU, "enable");
        // params.put(SpeechConstant.BDS_ASR_ENABLE_LONG_SPEECH, true);//长语音  优先级高于VAD_ENDPOINT_TIMEOUT
        // params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0); // 长语音

        // 复制此段可以自动检测常规错误
        AutoCheck(applicationContext, object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                if (msg.what == 100) {
                    val autoCheck: AutoCheck = msg.obj as AutoCheck
                    synchronized(autoCheck) {
                        val message: String =
                            autoCheck.obtainErrorMessage() // autoCheck.obtainAllMessage();
                        // 可以用下面一行替代，在logcat中查看代码
                        Log.d("TAG",message)
                    }
                }
            }
        }, false).checkAsr(params)


        myRecognizer!!.start(params)
    }

    private fun fetchParams(pcmPath:String): Map<String?, Any?> {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        //  上面的获取是为了生成下面的Map， 自己集成时可以忽略
        val params: MutableMap<String?, Any?> = apiParams.fetch(sp)
        params[SpeechConstant.APP_ID] = AuthUtil.getAppId() // 添加appId
        params[SpeechConstant.APP_KEY] = AuthUtil.getAk() // 添加apiKey
        params[SpeechConstant.SECRET] = AuthUtil.getSk() // 添加secretKey
        //  集成时不需要上面的代码，只需要params参数。
//        params[SpeechConstant.IN_FILE] = "res:///com/baidu/android/voicedemo/16k_test.pcm"
        params[SpeechConstant.IN_FILE] = pcmPath
        return params
    }

    open fun getSDCardPathByEnvironment(): String? {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            Environment.getExternalStorageDirectory().absolutePath
        } else ""
    }



    private fun stop() {
        myRecognizer!!.stop()
    }

    private fun cancel() {
        myRecognizer!!.cancel()
    }

    override fun onDestroy() {
        myRecognizer!!.release()
        Log.i(tag, "onDestory")
        super.onDestroy()
    }

    private class MyHandler(activity: ActivityAbstractRecog) : Handler(Looper.getMainLooper()) {
        private val mActivity: WeakReference<ActivityAbstractRecog>

        init {
            mActivity = WeakReference<ActivityAbstractRecog>(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity: ActivityAbstractRecog? = mActivity.get()
            if (activity != null) {
                mActivity.get()?.handleMsg(msg)
            }
        }
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private fun initPermission() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val toApplyList = ArrayList<String>()
        for (perm in permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
                    this,
                    perm
                )
            ) {
                toApplyList.add(perm)
                // 进入到这里代表没有权限.
            }
        }
        val tmpList = arrayOfNulls<String>(toApplyList.size)
        if (toApplyList.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123)
        }
    }



    @SuppressLint("StaticFieldLeak")
    inner class DownloadAndConvertTask constructor(cacheDirPath: String): AsyncTask<String?, Void?, Void?>() {
        //缓存路径
        var cacheDir = File(cacheDirPath)

        override fun doInBackground(vararg params: String?): Void? {
            if (params.isNotEmpty()) {
                val audioUrl = params[0]
                if (audioUrl != null) {
                    val path = downloadAudioFile(audioUrl)
                    val pcmPath = decodeAudioToPcm(path)
                    if(!pcmPath.isNullOrBlank()){
                        // TODO ... 这里拿到pcm文件路径
                        identifyPcm(pcmPath)
                    }
                }
            }
            return null
        }

        /**
         * 下載音频文件到本地
         * @param audioUrl 要下载的音频链接
         * @return 下载到本地的文件路径
         */
        private fun downloadAudioFile(audioUrl: String): String{
            startTime = System.currentTimeMillis()
            Log.d("downloadAndConvert", "audioUrl = "+audioUrl)

            var urlConnection: HttpURLConnection?
            var outputStream: FileOutputStream?

            // Open a connection to the audio file URL
            val url = URL(audioUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()

            // Create a temporary file to store the downloaded audio data
            val tempFile: File = File.createTempFile("temp_audio", ".mp3", cacheDir)

            outputStream = FileOutputStream(tempFile)

            // Write the downloaded audio data to the temporary file
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (urlConnection.inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.close()
            urlConnection.inputStream.close()
            urlConnection.disconnect()
            val currentTime = System.currentTimeMillis()
            val diffTime = currentTime - startTime
            LogUtil.d("耗时","下载耗时【" + diffTime + "ms】" + tempFile.absolutePath)
            return tempFile.absolutePath

        }
        /**
         * 音頻文件解码为pcm格式
         * @param inputAudioFile 要转码的音频文件路径
         * @return pcm文件路径
         */
        private fun decodeAudioToPcm(inputAudioFile: String): String? {
            startTime = System.currentTimeMillis()
            if(inputAudioFile.isEmpty()){
                return null
            }
            if(inputAudioFile.endsWith(".pcm")){
                return inputAudioFile
            }

            val mediaExtractor = MediaExtractor()
            var mediaCodec: MediaCodec? = null
            var outputStream: FileOutputStream? = null
            val tempPcmFile: File = File.createTempFile("temp_audio_pcm", ".pcm", cacheDir)
            try {
                // 设置数据源为输入音频文件
//                var mStrUrl = "http://10.52.70.59:8100/filestore/file/download/i/13045?extension=mp3&redirect=download"
//                val uri = Uri.parse(mStrUrl)
//                val headers: Map<String, String> = HashMap()
//                mediaExtractor.setDataSource(applicationContext,uri,headers)

                //本地数据
                mediaExtractor.setDataSource(inputAudioFile)

                // 获取音频轨道
                var audioTrackIndex = -1
                for (i in 0 until mediaExtractor.trackCount) {
                    val format: MediaFormat = mediaExtractor.getTrackFormat(i)
                    val mime = format.getString(MediaFormat.KEY_MIME)
                    if (mime!!.startsWith("audio/")) {
                        audioTrackIndex = i
                        break
                    }
                }
                if (audioTrackIndex == -1) {
                    return null
                }

                // 选择音频轨道
                mediaExtractor.selectTrack(audioTrackIndex)

                // 获取音频轨道的格式
                val inputFormat = mediaExtractor.getTrackFormat(audioTrackIndex)

                // 创建用于解码的 MediaCodec
                mediaCodec = MediaCodec.createDecoderByType(inputFormat.getString(MediaFormat.KEY_MIME)!!)
                mediaCodec.configure(inputFormat, null, null, 0)
                mediaCodec.start()

                // 开始解码并写入到 PCM 文件
                val codecInputBuffers = mediaCodec.inputBuffers
                val codecOutputBuffers = mediaCodec.outputBuffers
                val bufferInfo = MediaCodec.BufferInfo()

                // 输出 PCM 文件的路径
                val pcmOutputFile = File(tempPcmFile.absolutePath)
                outputStream = FileOutputStream(pcmOutputFile)

                var isDone = false
                while (!isDone) {
                    // 获取可用的输入缓冲区
                    val inputBufferIndex = mediaCodec.dequeueInputBuffer(10000)
                    if (inputBufferIndex >= 0) {
                        // 获取输入缓冲区
                        val inputBuffer = codecInputBuffers[inputBufferIndex]
                        // 读取音频数据到输入缓冲区
                        val sampleSize = mediaExtractor.readSampleData(inputBuffer, 0)
                        if (sampleSize < 0) {
                            // 如果没有更多数据，发送 EOS（End of Stream）标志
                            mediaCodec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                            isDone = true
                        } else {
                            // 将数据放入输入缓冲区，并前进到下一帧
                            mediaCodec.queueInputBuffer(inputBufferIndex, 0, sampleSize, mediaExtractor.sampleTime, 0 )
                            mediaExtractor.advance()
                        }
                    }

                    // 获取解码后的输出数据
                    val outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000)
                    if (outputBufferIndex >= 0) {
                        // 获取输出缓冲区
                        val outputBuffer = codecOutputBuffers[outputBufferIndex]
                        // 将 PCM 数据从输出缓冲区中读取到字节数组中
                        val chunk = ByteArray(bufferInfo.size)
                        outputBuffer.get(chunk)
                        outputBuffer.clear()

                        // 将 PCM 数据写入到输出文件
                        outputStream.write(chunk, 0, chunk.size)

                        // 释放输出缓冲区
                        mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
                    }
                }
            } catch (e: IOException) {
                return null
            } finally {
                try {
                    // 关闭资源
                    if (mediaCodec != null) {
                        mediaCodec.stop()
                        mediaCodec.release()
                    }
                    mediaExtractor.release()
                    outputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            val currentTime = System.currentTimeMillis()
            val diffTime = currentTime - startTime
            LogUtil.d("耗时","转换耗时【" + diffTime + "ms】")
            return tempPcmFile.absolutePath
        }

        private fun convertToPCM(inputAudioFile: String): String? {
            // 获取音频数据源
            val extractor = MediaExtractor()
            try {
                extractor.setDataSource(inputAudioFile)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            // 获取音频轨道
            var audioTrackIndex = -1
            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime!!.startsWith("audio/")) {
                    audioTrackIndex = i
                    break
                }
            }
            if (audioTrackIndex == -1) {
                return ""
            }

            // 选择音频轨道
            extractor.selectTrack(audioTrackIndex)

            // 获取音频格式
            val audioFormat = extractor.getTrackFormat(audioTrackIndex)
            val sampleRate = audioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            val channelCount = audioFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)

            // 创建PCM文件
            val pcmFile: File = File.createTempFile("temp_audio_pcm", ".pcm", cacheDir)
            if (pcmFile.exists()) {
                pcmFile.delete()
            }
            try {
                pcmFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            // 创建PCM文件输出流
            var outputStream: FileOutputStream? = null
            try {
                outputStream = FileOutputStream(pcmFile)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            // 读取音频数据并写入PCM文件
            val inputBuffer = ByteBuffer.allocate(1024 * 1024)
            val bufferInfo = MediaCodec.BufferInfo()
            var decoder: MediaCodec? = null
            try {
                decoder =
                    MediaCodec.createDecoderByType(audioFormat.getString(MediaFormat.KEY_MIME)!!)
                decoder.configure(audioFormat, null, null, 0)
                decoder.start()
                var isEOS = false
                while (!isEOS) {
                    val inputIndex = decoder.dequeueInputBuffer(10000)
                    if (inputIndex >= 0) {
                        val sampleSize = extractor.readSampleData(inputBuffer, 0)
                        if (sampleSize < 0) {
                            decoder.queueInputBuffer(
                                inputIndex,
                                0,
                                0,
                                0,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM
                            )
                            isEOS = true
                        } else {
                            val presentationTimeUs = extractor.sampleTime
                            decoder.queueInputBuffer(
                                inputIndex,
                                0,
                                sampleSize,
                                presentationTimeUs,
                                0
                            )
                            extractor.advance()
                        }
                    }
                    val outputIndex = decoder.dequeueOutputBuffer(bufferInfo, 10000)
                    if (outputIndex >= 0) {
                        val outputBuffer = decoder.getOutputBuffer(outputIndex)
                        val chunk = ByteArray(bufferInfo.size)
                        outputBuffer!![chunk]
                        outputBuffer.clear()
                        if (chunk.isNotEmpty()) {
                            outputStream!!.write(chunk)
                        }
                        decoder.releaseOutputBuffer(outputIndex, false)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                if (decoder != null) {
                    decoder.stop()
                    decoder.release()
                }
                extractor.release()
            }

            return pcmFile.absolutePath
        }

    }

}