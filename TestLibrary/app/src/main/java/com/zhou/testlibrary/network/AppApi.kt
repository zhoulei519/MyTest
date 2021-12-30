package com.zhou.testlibrary.network

import com.zhou.testlibrary.bean.User
import retrofit2.Call

import retrofit2.http.GET
import retrofit2.http.Path


interface AppApi {
    @GET("/users/{username}")
    fun queryUserByUsername(@Path("username") username: String?): Call<User?>?
}