package com.zhou.testlibrary.speech.baidu.speech.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * @author: zl
 * @date: 2023/12/22
 */
public class LibFileUtils {

    //选择文件
    public static void pickFile(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(intent, 1);
    }

    public static String handlePickFileResult(Context context, int requestCode, int resultCode, Intent intent) {
        String path = "";
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = intent.getData();
            path = PathUtils.getPath(context, uri);
        }
        return path;
    }
}
