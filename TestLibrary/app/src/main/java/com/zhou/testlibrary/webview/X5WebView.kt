package com.zhou.testlibrary.webview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView

class X5WebView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attributeSet, defStyleAttr) {

    init {
        setBackgroundColor(85621)
        initWebViewSettings()
        this.getView().setClickable(true)
        this.getView().setOverScrollMode(View.OVER_SCROLL_ALWAYS)
    }

    private fun initWebViewSettings() {
        val webSettings:WebSettings  = this.settings
        webSettings.setAllowFileAccess(true)
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS)
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true)
        webSettings.setDisplayZoomControls(false)//隐藏原生的缩放控件
        webSettings.setUseWideViewPort(true)
        webSettings.setSupportMultipleWindows(false)
        webSettings.setLoadWithOverviewMode(true)
        webSettings.setAppCacheEnabled(true)
        webSettings.setDatabaseEnabled(true)
        webSettings.setDomStorageEnabled(true)
        webSettings.setJavaScriptEnabled(true)
        webSettings.setGeolocationEnabled(true)
        webSettings.setAppCacheMaxSize(Long.MAX_VALUE)
        webSettings.setAppCachePath(this.getContext().getDir("appcache", 0).getPath())
        webSettings.setDatabasePath(this.getContext().getDir("databases", 0).getPath())
        webSettings.setGeolocationDatabasePath(this.getContext().getDir("geolocation", 0).getPath())
        webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND)
        //this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension// settings 的设计
    }
}