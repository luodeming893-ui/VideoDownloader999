package com.videodownloader.download

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

data class DownloadResult(
    val success: Boolean,
    val filePath: String = "",
    val error: String = "",
    val videoTitle: String = ""
)

class VideoDownloadManager(private val context: Context) {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()
    
    suspend fun parseAndDownload(url: String): DownloadResult {
        return try {
            // 清理URL
            val cleanUrl = cleanUrl(url)
            
            // 解析视频信息
            val videoInfo = parseVideoInfo(cleanUrl)
            
            if (videoInfo.videoUrl.isEmpty()) {
                return DownloadResult(false, error = "无法解析视频链接")
            }
            
            // 下载视频
            val safeTitle = videoInfo.title.take(50)
                .replace("[^a-zA-Z0-9\\u4e00-\\u9fa5\\s]".toRegex(), "_")
                .trim()
            val fileName = "$safeTitle.mp4"
            
            val savePath = downloadVideo(videoInfo.videoUrl, fileName)
            
            DownloadResult(true, filePath = savePath, videoTitle = videoInfo.title)
        } catch (e: Exception) {
            Log.e("VideoDownloader", "Download error", e)
            DownloadResult(false, error = e.message ?: "未知错误")
        }
    }
    
    private fun cleanUrl(url: String): String {
        var cleanUrl = url.trim()
        // 移除分享文本，只保留URL
        val urlPattern = Regex("https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+")
        val match = urlPattern.find(cleanUrl)
        return match?.value ?: cleanUrl
    }
    
    private suspend fun parseVideoInfo(url: String): VideoInfo {
        return when {
            url.contains("tiktok.com") || url.contains("douyin.com") -> parseTikTok(url)
            url.contains("xiaohongshu.com") || url.contains("xhslink.com") -> parseXiaoHongShu(url)
            url.contains("bilibili.com") || url.contains("b23.tv") -> parseBilibili(url)
            else -> throw IllegalArgumentException("不支持的平台链接。支持的平台：TikTok、抖音、小红书、B站")
        }
    }
    
    private suspend fun parseTikTok(url: String): VideoInfo {
        // 方法1: 使用Snaptik API (公开解析服务)
        return try {
            parseWithSnaptik(url)
        } catch (e: Exception) {
            Log.w("VideoDownloader", "Snaptik failed, trying alternative", e)
            // 方法2: 使用其他解析服务
            parseWithAlternativeAPI(url)
        }
    }
    
    private suspend fun parseWithSnaptik(url: String): VideoInfo {
        // Snaptik 风格的解析（实际需要使用他们的API）
        // 这里提供一个通用的解析框架
        
        // 方案：使用 web 解析服务
        val apiUrl = "https://api.tiktokv.com/aweme/v1/play/?url=$url"
        
        val request = Request.Builder()
            .url(apiUrl)
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .get()
            .build()
        
        val response = client.newCall(request).execute()
        
        if (response.isSuccessful) {
            // 如果能直接获取视频URL
            val videoUrl = response.request.url.toString()
            return VideoInfo("tiktok_video_${System.currentTimeMillis()}", videoUrl)
        }
        
        // 如果上面的方法不行，使用第三方解析服务
        return parseWithThirdPartyAPI(url)
    }
    
    private suspend fun parseWithThirdPartyAPI(url: String): VideoInfo {
        // 使用公开的第三方解析API
        // 注意：这些API可能不稳定，建议部署自己的解析服务
        
        val apis = listOf(
            "https://www.tikwm.com/api/",
            "https://api.ssstik.io/api/v2"
        )
        
        for (apiUrl in apis) {
            try {
                val result = parseWithAPI(apiUrl, url)
                if (result != null) {
                    return result
                }
            } catch (e: Exception) {
                Log.w("VideoDownloader", "API $apiUrl failed", e)
            }
        }
        
        throw Exception("所有解析服务均失败，请稍后重试")
    }
    
    private suspend fun parseWithAPI(apiUrl: String, videoUrl: String): VideoInfo? {
        // 实现具体的API调用逻辑
        // 这里需要根据具体API的文档来实现
        
        // 示例：使用 TikWM API
        if (apiUrl.contains("tikwm")) {
            val requestUrl = "$apiUrl?url=${java.net.URLEncoder.encode(videoUrl, "UTF-8")}"
            val request = Request.Builder()
                .url(requestUrl)
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return null
            
            val json = JSONObject(body)
            
            if (json.getInt("code") == 0) {
                val data = json.getJSONObject("data")
                val playUrl = data.getString("play")
                val title = data.optString("title", "tiktok_video")
                
                return VideoInfo(title, playUrl)
            }
        }
        
        return null
    }
    
    private suspend fun parseWithAlternativeAPI(url: String): VideoInfo {
        // 备用的解析方法
        throw Exception("TikTok解析失败，请尝试其他视频或稍后重试")
    }
    
    private suspend fun parseXiaoHongShu(url: String): VideoInfo {
        // 小红书解析相对复杂，通常需要：
        // 1. 模拟登录获取cookie
        // 2. 解析页面获取视频ID
        // 3. 使用API获取视频地址
        
        // 建议方案：
        // 1. 使用WebView加载页面并拦截请求
        // 2. 部署后端解析服务
        // 3. 使用第三方解析API
        
        throw Exception(
            "小红书视频下载需要特殊处理。\n" +
            "建议方案：\n" +
            "1. 复制视频链接\n" +
            "2. 使用电脑端工具下载\n" +
            "3. 等待应用更新支持"
        )
    }
    
    private suspend fun parseBilibili(url: String): VideoInfo {
        // B站解析
        // 可以使用的方式：
        // 1. 官方API（需要登录）
        // 2. 解析页面HTML
        // 3. 使用第三方解析服务
        
        // 简化的解析逻辑
        val bvPattern = Regex("BV[a-zA-Z0-9]+")
        val bvMatch = bvPattern.find(url)
        
        if (bvMatch != null) {
            val bvId = bvMatch.value
            return parseBilibiliByBV(bvId)
        }
        
        throw Exception(
            "B站视频解析。\n" +
            "支持格式：\n" +
            "1. https://www.bilibili.com/video/BVxxx\n" +
            "2. https://b23.tv/xxx"
        )
    }
    
    private suspend fun parseBilibiliByBV(bvId: String): VideoInfo {
        // 使用第三方B站解析API
        // 实际项目中应该部署自己的解析服务
        
        val apiUrl = "https://api.bilibili.com/x/web-interface/view?bvid=$bvId"
        
        val request = Request.Builder()
            .url(apiUrl)
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .get()
            .build()
        
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: throw Exception("无法获取视频信息")
        
        val json = JSONObject(body)
        
        if (json.getInt("code") == 0) {
            val data = json.getJSONObject("data")
            val title = data.getString("title")
            
            // 注意：这里只是获取了视频标题
            // 实际下载需要解析视频流地址，这需要使用B站的内部API
            
            throw Exception(
                "已识别视频：$title\n" +
                "但B站视频下载需要特殊处理，建议使用：\n" +
                "1. 官方客户端下载\n" +
                "2. 第三方下载工具"
            )
        }
        
        throw Exception("无法解析B站视频")
    }
    
    private suspend fun downloadVideo(videoUrl: String, fileName: String): String {
        val saveDir = getSaveDirectory()
        
        if (!saveDir.exists()) {
            saveDir.mkdirs()
        }
        
        val saveFile = File(saveDir, fileName)
        
        var retryCount = 0
        val maxRetries = 3
        
        while (retryCount < maxRetries) {
            try {
                val request = Request.Builder()
                    .url(videoUrl)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .get()
                    .build()
                
                val response = client.newCall(request).execute()
                
                if (!response.isSuccessful) {
                    throw Exception("HTTP ${response.code}")
                }
                
                val inputStream = response.body?.byteStream() 
                    ?: throw Exception("无法下载视频：空响应")
                
                FileOutputStream(saveFile).use { output ->
                    inputStream.use { input ->
                        val buffer = ByteArray(4096)
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                        }
                        output.flush()
                    }
                }
                
                return saveFile.absolutePath
                
            } catch (e: Exception) {
                retryCount++
                if (retryCount >= maxRetries) {
                    throw e
                }
                kotlinx.coroutines.delay(1000 * retryCount) // 重试前等待
            }
        }
        
        throw Exception("下载失败：超过最大重试次数")
    }
    
    private fun getSaveDirectory(): File {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ 使用应用私有目录
            File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "VideoDownloader")
        } else {
            // Android 9- 使用公共目录
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "VideoDownloader")
        }
    }
}

data class VideoInfo(
    val title: String,
    val videoUrl: String
)
