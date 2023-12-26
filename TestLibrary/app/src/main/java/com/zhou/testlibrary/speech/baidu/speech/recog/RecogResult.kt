package com.zhou.testlibrary.speech.baidu.speech.recog

import org.json.JSONException
import org.json.JSONObject

/**
 * @author: zl
 * @date: 2023/12/19
 */
class RecogResult {
    private val ERROR_NONE = 0

    private var origalJson: String? = null
    private lateinit var resultsRecognition: Array<String?>
    private var origalResult: String? = null
    private var sn: String? = null // 日志id， 请求有问题请提问带上sn

    private var desc: String? = null
    private var resultType: String? = null
    private var error = -1
    private var subError = -1

    fun parseJson(jsonStr: String?): RecogResult? {
        val result = RecogResult()
        result.setOrigalJson(jsonStr)
        try {
            val json = JSONObject(jsonStr)
            val error = json.optInt("error")
            val subError = json.optInt("sub_error")
            result.setError(error)
            result.setDesc(json.optString("desc"))
            result.setResultType(json.optString("result_type"))
            result.setSubError(subError)
            if (error == ERROR_NONE) {
                result.setOrigalResult(json.getString("origin_result"))
                val arr = json.optJSONArray("results_recognition")
                if (arr != null) {
                    val size = arr.length()
                    val recogs = arrayOfNulls<String>(size)
                    for (i in 0 until size) {
                        recogs[i] = arr.getString(i)
                    }
                    result.setResultsRecognition(recogs)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return result
    }

    fun hasError(): Boolean {
        return error != ERROR_NONE
    }

    fun isFinalResult(): Boolean {
        return "final_result" == resultType
    }


    fun isPartialResult(): Boolean {
        return "partial_result" == resultType
    }

    fun isNluResult(): Boolean {
        return "nlu_result" == resultType
    }

    fun getOrigalJson(): String? {
        return origalJson
    }

    fun setOrigalJson(origalJson: String?) {
        this.origalJson = origalJson
    }

    fun getResultsRecognition(): Array<String?>? {
        return resultsRecognition
    }

    fun setResultsRecognition(resultsRecognition: Array<String?>) {
        this.resultsRecognition = resultsRecognition
    }

    fun getSn(): String? {
        return sn
    }

    fun setSn(sn: String?) {
        this.sn = sn
    }

    fun getError(): Int {
        return error
    }

    fun setError(error: Int) {
        this.error = error
    }

    fun getDesc(): String? {
        return desc
    }

    fun setDesc(desc: String?) {
        this.desc = desc
    }

    fun getOrigalResult(): String? {
        return origalResult
    }

    fun setOrigalResult(origalResult: String?) {
        this.origalResult = origalResult
    }

    fun getResultType(): String? {
        return resultType
    }

    fun setResultType(resultType: String?) {
        this.resultType = resultType
    }

    fun getSubError(): Int {
        return subError
    }

    fun setSubError(subError: Int) {
        this.subError = subError
    }
}