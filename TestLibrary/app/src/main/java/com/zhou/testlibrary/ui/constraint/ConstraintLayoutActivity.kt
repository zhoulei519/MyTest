package com.zhou.testlibrary.ui.constraint

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import com.zhou.common.permission.requestPermission
import com.zhou.testlibrary.R
import com.zhou.testlibrary.base.BaseActivity

/**
 * @author: zl
 * @date: 2023/12/11
 */
class ConstraintLayoutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        requestPermission(Manifest.permission.CAMERA, onPermit = {
            Toast.makeText(this, "申请成功", Toast.LENGTH_SHORT).show()
        }, onDeny = { shouldShowCustomRequest ->
            Toast.makeText(this, "申请失败", Toast.LENGTH_SHORT).show()
            if (shouldShowCustomRequest) {
                Toast.makeText(this, "拒绝了并且不在询问", Toast.LENGTH_SHORT).show()
            }
        })
    }
}