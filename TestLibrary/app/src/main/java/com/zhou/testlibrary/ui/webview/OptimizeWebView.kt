package com.zhou.testlibrary.ui.webview

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.zhou.testlibrary.base.BaseActivity
import com.zhou.testlibrary.R

class OptimizeWebView : BaseActivity(){
    private var clear = false
    private var webview: X5WebView? = null
    private var layout:LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        layout = findViewById(R.id.root)
        webview = WebViewPool.instance.getWebView()
        layout?.addView(webview)

        //webview setting
        webview?.fitsSystemWindows = true
        /* if SDK version is greater of 19 then activate hardware acceleration
        otherwise activate software acceleration  */
        webview?.setLayerType(View.LAYER_TYPE_HARDWARE,null)
        webview?.loadUrl("file:///android_asset/test.html")
        // Set web view client
        webview?.setWebViewClient(object : WebViewClient() {
            override fun doUpdateVisitedHistory(webView: WebView, s: String, b: Boolean) {
                //处理历史视图问题
                if (clear) {
                    webView.clearHistory()
                    clear = false
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        WebViewPool.instance.recycleWebView(layout,webview)
        webview = null
    }

}