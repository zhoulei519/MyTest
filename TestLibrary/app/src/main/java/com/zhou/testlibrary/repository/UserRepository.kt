package com.zhou.testlibrary.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zhou.common.network.retrofit.RetrofitFactory
import com.zhou.testlibrary.bean.User
import com.zhou.testlibrary.network.api.AppApi
import com.zhou.testlibrary.utils.LogUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository private constructor() {
    private val userApi = RetrofitFactory.instance!!.create(AppApi::class.java)

    fun getUser(username: String?): LiveData<User?> {
        val user = MutableLiveData<User?>()

        userApi.queryUserByUsername(username)
            ?.enqueue(object : Callback<User?> {
                override fun onResponse(call: Call<User?>, response: Response<User?>) {
                    user.value = response.body()
                }

                override fun onFailure(call: Call<User?>, t: Throwable) {
                    t.printStackTrace()
                    LogUtil.d("请求失败！")
                }
            })
        return user
    }

    companion object {
        val instance = UserRepository()
    }
}