# 视频解析后端服务

使用 yt-dlp 提供视频解析 API 的 Python 后端服务。

## 功能特点

- ✅ 支持几乎所有可能的视频网站
- ✅ 提供 RESTful API
- ✅ 支持跨域请求
- ✅ 简单易用

## 支持的平台

yt-dlp 支持超过 1000+ 网站，包括：

- TikTok / 抖音
- 小红书
- B站 (Bilibili)
- YouTube
- Instagram
- Facebook
- Twitter
- 等等...

## 快速开始

### 1. 安装依赖

```bash
pip install -r requirements.txt
```

或使用 requirements.txt：

```bash
pip install Flask flask-cors yt-dlp
```

### 2. 启动服务

```bash
python app.py
```

服务将在 `http://0.0.0.0:5000` 启动。

### 3. 测试服务

```bash
# 检查服务状态
curl http://localhost:5000/info

# 解析视频
curl -X POST http://localhost:5000/parse \
  -H "Content-Type: application/json" \
  -d '{"url": "https://www.tiktok.com/@user/video/123456"}'
```

## API 文档

### POST /parse

解析视频链接，返回视频信息。

**请求体:**
```json
{
  "url": "视频链接"
}
```

**响应:**
```json
{
  "title": "视频标题",
  "video_url": "视频下载地址",
  "duration": 120,
  "thumbnail": "缩略图地址",
  "platform": "TikTok"
}
```

### POST /download

下载视频到服务器。

**请求体:**
```json
{
  "url": "视频链接"
}
```

**响应:**
```json
{
  "message": "下载成功",
  "filename": "视频文件名",
  "download_url": "/files/视频文件名"
}
```

## 部署到云服务器

### 使用 Gunicorn (推荐)

1. 安装 Gunicorn:
```bash
pip install gunicorn
```

2. 启动服务:
```bash
gunicorn -w 4 -b 0.0.0.0:5000 app:app
```

### 使用 Docker

创建 `Dockerfile`:

```dockerfile
FROM python:3.9-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app.py .

EXPOSE 5000

CMD ["python", "app.py"]
```

构建和运行:

```bash
docker build -t video-parser .
docker run -p 5000:5000 video-parser
```

### 部署到 Heroku

1. 创建 `Procfile`:
```
web: gunicorn app:app
```

2. 部署:
```bash
heroku create
git push heroku main
```

## 在 Android 应用中使用

修改 `VideoDownloadManager.kt`，将 API 地址指向你的后端服务：

```kotlin
private suspend fun parseVideoInfo(url: String): VideoInfo {
    val apiUrl = "http://你的服务器地址:5000/parse"
    
    val client = OkHttpClient()
    val requestBody = JSONObject().apply {
        put("url", url)
    }
    
    val request = Request.Builder()
        .url(apiUrl)
        .post(RequestBody.create("application/json".toMediaType(), requestBody.toString()))
        .build()
    
    val response = client.newCall(request).execute()
    val json = JSONObject(response.body?.string())
    
    return VideoInfo(
        title = json.getString("title"),
        videoUrl = json.getString("video_url")
    )
}
```

## 安全建议

1. **添加认证** - 为 API 添加密钥或 token 认证
2. **限制访问** - 使用防火墙或 Nginx 限制访问
3. **HTTPS** - 使用 SSL 证书加密传输
4. **速率限制** - 防止滥用

## 常见问题

### Q: yt-dlp 下载失败怎么办？

A: 更新 yt-dlp 到最新版本:
```bash
pip install --upgrade yt-dlp
```

### Q: 如何支持更多网站？

A: yt-dlp 已经支持 1000+ 网站。如果需要支持新的网站，可以提交 issue 或自行添加提取器。

### Q: 性能如何优化？

A:
- 使用 Gunicorn 多进程
- 添加 Redis 缓存
- 使用 CDN 加速

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！

---

**注意**: 本服务仅供学习交流使用，请遵守各视频平台的使用条款。
