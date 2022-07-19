package com.zhou.testlibrary.viewmodel

import androidx.lifecycle.ViewModel
import com.zhou.testlibrary.bean.User

import androidx.lifecycle.LiveData

import com.zhou.testlibrary.repository.UserRepository





class UserModel : ViewModel() {
    private val userRepository: UserRepository = UserRepository.instance
    private var user: LiveData<User?>? = null

    fun getUser(username: String?): LiveData<User?>? {
        if (null == user) user = userRepository.getUser(username)
        return user
    }

    fun setUserName(userName: String){
        user?.value?.name = userName
    }
}