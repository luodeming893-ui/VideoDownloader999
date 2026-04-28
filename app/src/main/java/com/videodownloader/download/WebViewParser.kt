package com.videodownloader.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.videodownloader.download.VideoInfo

class WebViewParser(private val callback: (VideoInfo?) -> Unit) {
    
    @SuppressLint("SetJavaScriptEnabled")
    fun createWebView(webView: WebView) {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
        }
        
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }
            
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }
            
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // 页面加载完成后，注入JavaScript来查找视频元素
                injectJavaScript(view)
            }
            
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): android.webkit.WebResourceResponse? {
                val url = request?.url?.toString() ?: ""
                
                // 拦截视频请求
                if (isVideoUrl(url)) {
                    // 找到了视频URL
                    val videoInfo = VideoInfo(
                        title = "webview_video_${System.currentTimeMillis()}",
                        videoUrl = url
                    )
                    callback(videoInfo)
                }
                
                return super.shouldInterceptRequest(view, request)
            }
        }
    }
    
    private fun injectJavaScript(webView: WebView?) {
        val jsCode = """
            javascript:(function() {
                // 查找video标签
                var videos = document.getElementsByTagName('video');
                for (var i = 0; i < videos.length; i++) {
                    var src = videos[i].getAttribute('src');
                    if (src) {
                        window.android.onVideoFound(src);
                    }
                }
                
                // 查找可能包含视频URL的属性
                var elements = document.querySelectorAll('[data-video-url], [data-src]');
                for (var j = 0; j < elements.length; j++) {
                    var url = elements[j].getAttribute('data-video-url') || 
                              elements[j].getAttribute('data-src');
                    if (url && (url.includes('.mp4') || url.includes('video'))) {
                        window.android.onVideoFound(url);
                    }
                }
            })();
        """.trimIndent()
        
        webView?.evaluateJavascript(jsCode, null)
    }
    
    private fun isVideoUrl(url: String): Boolean {
        val videoExtensions = listOf(".mp4", ".m3u8", ".avi", ".mov", ".flv", ".mkv")
        val videoKeywords = listOf("video", "play", "media", "download")
        
        return videoExtensions.any { url.contains(it) } ||
               videoKeywords.any { url.contains(it) && url.contains("http") }
    }
}
