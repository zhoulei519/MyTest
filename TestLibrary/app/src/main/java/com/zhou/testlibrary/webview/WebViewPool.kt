package com.zhou.testlibrary.webview

import com.zhou.testlibrary.app.MyApplication
import java.lang.Exception
import java.lang.RuntimeException
import android.view.ViewGroup

class WebViewPool private constructor(){
    private var webViewPool: MutableList<WebVieWrap> = ArrayList()
    private var maxSize = 2

    //Kotlin中的每个类都继承自Any，但Any不声明wait（），notify（）和notifyAll（），这意味着这些方法不能在Kotlin类上调用。 但是你仍然可以使用java.lang.Object的一个实例作为锁，并调用它的方法
    private val lock = Object()

    init {
        webViewPool = ArrayList()
        initView()
    }

    companion object {
        val instance: WebViewPool by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            WebViewPool()
        }
    }

    private fun initView() {
        for (i in 0 until maxSize) {
            val webView = X5WebView(MyApplication.getContext())
            val webVieWrap = WebVieWrap()
            webVieWrap.x5WebView = webView
            webViewPool.add(webVieWrap)
        }
    }

    /**
     * 获取webView
     */
    @Synchronized
    fun getWebView(): X5WebView? = synchronized(lock){
        if (webViewPool.size < maxSize) {
            return buildWebView()
        }
        var x5WebView: X5WebView? = checkWebView()
        if (x5WebView != null) {
            return x5WebView
        }
        //再次判断
        if (webViewPool.size < maxSize) {
            return buildWebView()
        }
        try {
            lock.wait((2 * 1000).toLong())
            x5WebView = getWebView()
            return x5WebView
        } catch (e: Exception) {
        }
        throw RuntimeException("webView池已满")
    }

    private fun checkWebView(): X5WebView? {
        for (i in webViewPool.indices.reversed()) {
            val webVieWrap = webViewPool[i]
            if (webVieWrap.inUse) {
                continue
            }
            val x5WebView = webVieWrap.x5WebView
            webVieWrap.inUse = true
            return x5WebView
        }
        return null
    }

    /**
     * 回收webView
     * @param webView
     */
    @Synchronized
    fun recycleWebView(webView: X5WebView) = synchronized(lock){
        for (i in webViewPool.indices) {
            val webVieWrap = webViewPool[i]
            val temp = webVieWrap.x5WebView
            if (webView == temp) {
                temp.stopLoading()
                temp.webChromeClient = null
                temp.webViewClient = null
                temp.clearHistory()
                //                temp.clearCache(true);
                temp.loadUrl("about:blank")
                temp.pauseTimers()
                webVieWrap.inUse = false
                break
            }
        }
        lock.notifyAll()
    }

    /**
     * 创建webView
     * @return
     */
    private fun buildWebView(): X5WebView? {
        val webView = X5WebView(MyApplication.getContext())
        val webVieWrap = WebVieWrap()
        webVieWrap.x5WebView = webView
        webViewPool.add(webVieWrap)
        return webView
    }


    /**
     * 销毁连接池
     */
    fun destroyPool() {
        try {
            if (webViewPool.size == 0) {
                return
            }
            for (webVieWrap in webViewPool) {
                val webView = webVieWrap.x5WebView
                webView!!.destroy()
            }
            webViewPool.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 回收webView ,解绑
     *
     * @param webView 需要被回收的webView
     */
    fun recycleWebView(view: ViewGroup?, webView: X5WebView?) {
        if (view != null && webView != null) {
            recycleWebView(webView)
            view.removeView(webView)
        }
    }

    /**
     * 设置webView池个数
     *
     * @param size webView池个数
     */
    fun setMaxPoolSize(size: Int) {
        maxSize = size
    }


    internal class WebVieWrap {
        var x5WebView: X5WebView? = null
        var inUse = false
    }
}