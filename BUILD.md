# 视频下载器 - 构建说明

## 项目简介

这是一个支持多平台视频下载的 Android 应用，支持：
- ✅ TikTok / 抖音
- 🚧 小红书 (开发中)
- 🚧 B站 (开发中)
- 🚧 其他平台 (待添加)

## 项目结构

```
VideoDownloader/
├── app/
│   ├── src/main/
│   │   ├── java/com/videodownloader/
│   │   │   ├── ui/MainActivity.kt
│   │   │   ├── download/VideoDownloadManager.kt
│   │   │   └── VideoDownloaderApp.kt
│   │   ├── res/
│   │   │   ├── layout/activity_main.xml
│   │   │   ├── values/strings.xml
│   │   │   ├── values/colors.xml
│   │   │   ├── values/themes.xml
│   │   │   └── drawable/ic_download.xml
│   │   └── AndroidManifest.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
└── gradle.properties
```

## 构建 APK 的方法

### 方法一：使用 Android Studio（推荐）

1. **安装 Android Studio**
   - 下载地址：https://developer.android.com/studio
   - 安装时选择 "Standard" 安装类型

2. **打开项目**
   - 启动 Android Studio
   - 选择 "Open an Existing Project"
   - 选择 `VideoDownloader` 文件夹

3. **同步项目**
   - Android Studio 会自动下载 Gradle 和依赖
   - 等待同步完成（第一次可能需要较长时间）

4. **构建 APK**
   - 菜单栏：Build → Build Bundle(s) / APK(s) → Build APK(s)
   - 等待构建完成
   - APK 文件位置：`app/build/outputs/apk/debug/app-debug.apk`

5. **生成正式版 APK（可选）**
   - 菜单栏：Build → Generate Signed Bundle / APK
   - 按照向导创建签名密钥
   - 生成带有签名的 APK

### 方法二：使用命令行构建

1. **安装 Android SDK**
   - 下载 Android Command Line Tools
   - 配置环境变量 `ANDROID_HOME`

2. **安装 Gradle**
   - 下载地址：https://gradle.org/releases/
   - 或使用项目中的 Gradle Wrapper

3. **构建命令**
   ```bash
   # Windows
   gradlew.bat assembleDebug
   
   # Mac/Linux
   ./gradlew assembleDebug
   ```

4. **输出位置**
   - Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
   - Release APK: `app/build/outputs/apk/release/app-release.apk`

### 方法三：使用在线构建服务

如果不想在本地配置开发环境，可以使用在线构建服务：

1. **GitHub Actions**（免费）
   - 将代码推送到 GitHub
   - 配置 GitHub Actions 自动构建

2. **GitLab CI/CD**（免费）
   - 类似 GitHub Actions

3. **CodeMagic**（免费额度）
   - 专业的移动应用构建平台

## 重要提示

### ⚠️ 视频解析的限制

由于各大平台的反爬虫机制，直接在 Android 应用中解析视频链接非常困难：

1. **TikTok/抖音**
   - 可以使用第三方解析 API
   - 建议部署自己的解析服务

2. **小红书**
   - 需要登录才能获取视频
   - 建议使用 WebView + 拦截请求的方式

3. **B站**
   - 官方提供 API（需要申请）
   - 或使用第三方解析服务

### 🔧 改进建议

1. **使用后端服务**
   ```
   Android App → 你的后端服务器 → 视频解析 → 返回下载链接
   ```

2. **使用 WebView**
   - 在应用中嵌入 WebView
   - 加载第三方解析网站
   - 拦截视频请求获取真实地址

3. **使用开源项目**
   - yt-dlp (Python, 可打包为服务)
   - youtube-dl
   - 部署为后端 API

## 快速测试

如果你想快速测试应用功能，可以：

1. **修改解析逻辑**
   - 在 `VideoDownloadManager.kt` 中
   - 使用稳定的第三方解析 API
   - 或连接到你自己的解析服务器

2. **使用模拟数据**
   - 暂时返回测试视频链接
   - 验证下载功能是否正常工作

## 常见问题

### Q: 构建失败怎么办？
A: 
- 检查 Android SDK 是否正确安装
- 检查 Gradle 版本是否兼容
- 查看构建日志获取详细错误信息

### Q: 解析失败怎么办？
A:
- 第三方 API 可能不稳定，建议多试几个
- 部署自己的解析服务
- 使用后端代理

### Q: 如何添加更多平台支持？
A:
- 在 `VideoDownloadManager.kt` 的 `parseVideoInfo()` 方法中
- 添加新的平台判断逻辑
- 实现对应的解析方法

## 联系与贡献

如果你有改进建议或发现了 Bug，欢迎：
- 提交 Issue
- 提交 Pull Request
- 联系开发者

## 许可证

MIT License

---

**注意**：本应用仅供学习交流使用，请遵守各平台的使用条款和服务协议。
