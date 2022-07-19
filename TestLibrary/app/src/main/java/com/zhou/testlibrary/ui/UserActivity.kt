package com.zhou.testlibrary.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.zhou.testlibrary.R
import com.zhou.testlibrary.base.BaseActivity
import com.zhou.testlibrary.bean.User
import com.zhou.testlibrary.utils.LogUtil
import com.zhou.testlibrary.viewmodel.UserModel

class UserActivity : BaseActivity() {
    private var userViewModel: UserModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        initData()
    }

    private fun initData() {
        userViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(UserModel::class.java)
        userViewModel!!.getUser("zhou")?.observe(this, Observer {
            it?.let { it1 -> updateUser(it1) }
        })
    }
    private fun updateUser(user:User){
        LogUtil.d("更新UI")
    }
}