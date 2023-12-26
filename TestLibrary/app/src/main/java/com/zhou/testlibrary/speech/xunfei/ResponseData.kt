package com.zhou.testlibrary.speech.xunfei

import com.google.gson.annotations.SerializedName

/**
 * @author: zl
 * @date: 2023/12/26
 */
data class ResponseData(
    val code: String,
    val descInfo: String,
    val content: ContentData
)

data class ContentData(
    val orderInfo: OrderInfo,
    val orderResult: String,
    val taskEstimateTime: Int,
)

data class OrderInfo(
    val orderId: String,
    val failType: Int,
    val status: Int,
    val originalDuration: Int,
    val realDuration: Int,
    val expireTime: Long
)

data class OrderResultData(
    @SerializedName("lattice")
    val lattice: List<Lattice>,

    @SerializedName("lattice2")
    val lattice2: List<Lattice2>
)

data class Lattice(
    @SerializedName("json_1best")
    val json1Best: Json1Best
)

data class Json1Best(
    val st: St
)

data class St(
    val sc: String,
    val pa: String,
    val rt: List<Rt>
)

data class Rt(
    val ws: List<Ws>
)

data class Ws(
    val cw: List<Cw>
)

data class Cw(
    val w: String,
    val wp: String,
    val wc: String
)

data class Lattice2(
    val lid: String,
    val end: String,
    val begin: String,
    @SerializedName("json_1best")
    val json1Best: Json1Best
)
