package com.zhou.common.network.retrofit

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitFactory {
    private var client: OkHttpClient? = null
    var instance: Retrofit? = null
    private const val HOST = "https://api.github.com"

    init {
        client = OkHttpClient.Builder()
            .connectTimeout(9, TimeUnit.SECONDS)
            .build()
        instance = Retrofit.Builder()
            .baseUrl(HOST)
            .client(client!!)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
            .build()
    }
}