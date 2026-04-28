# 视频下载器 - 完整部署指南

## 🚀 快速生成 APK

### 方案一：使用 Android Studio（最简单）

#### 步骤：

1. **下载安装 Android Studio**
   - 官网：https://developer.android.com/studio
   - 选择 Windows 版本下载
   - 安装时选择 "Standard" 安装

2. **打开项目**
   ```
   启动 Android Studio
   → 选择 "Open"
   → 选择 VideoDownloader 文件夹
   → 等待 Gradle 同步（首次可能需要 10-30 分钟）
   ```

3. **构建 APK**
   ```
   菜单栏：Build
   → Build Bundle(s) / APK(s)
   → Build APK(s)
   → 等待构建完成
   ```

4. **获取 APK**
   - 构建完成后会弹出提示
   - 点击 "locate" 打开 APK 所在文件夹
   - APK 路径：`app/build/outputs/apk/debug/app-debug.apk`

### 方案二：使用命令行（无需安装 Android Studio）

#### 前置准备：

1. **安装 Java JDK**
   - 下载 JDK 11 或更高版本
   - 配置 JAVA_HOME 环境变量

2. **下载 Android SDK Command Line Tools**
   - 下载地址：https://developer.android.com/studio#command-line-tools-only
   - 解压到任意目录

3. **配置环境变量**
   ```
   ANDROID_HOME=C:\Android\Sdk
   PATH=%PATH%;%ANDROID_HOME%\cmdline-tools\latest\bin
   ```

4. **安装 SDK 组件**
   ```bash
   sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
   ```

5. **构建 APK**
   ```bash
   cd VideoDownloader
   gradlew.bat assembleDebug
   ```

### 方案三：使用 GitHub Actions（免费云端构建）

#### 步骤：

1. **创建 GitHub 仓库**
   - 登录 GitHub
   - 创建新仓库 `VideoDownloader`
   - 推送代码到仓库

2. **添加 GitHub Actions 配置**
   创建 `.github/workflows/build.yml`：

   ```yaml
   name: Build Android APK
   
   on:
     push:
       branches: [ main ]
     pull_request:
       branches: [ main ]
   
   jobs:
     build:
       runs-on: ubuntu-latest
       
       steps:
       - uses: actions/checkout@v3
       
       - name: Set up JDK 11
         uses: actions/setup-java@v3
         with:
           java-version: '11'
           distribution: 'temurin'
       
       - name: Grant execute permission for gradlew
         run: chmod +x gradlew
       
       - name: Build with Gradle
         run: ./gradlew assembleDebug
       
       - name: Upload APK
         uses: actions/upload-artifact@v3
         with:
           name: app-debug
           path: app/build/outputs/apk/debug/app-debug.apk
   ```

3. **触发构建**
   - 推送代码到 main 分支
   - 在 GitHub 仓库的 "Actions" 标签页查看构建进度
   - 构建完成后下载 APK

## 📱 安装到手机

### 开启未知来源安装

1. **Android 8.0+**
   ```
   设置 → 安全 → 未知来源应用
   → 选择你要安装的应用（如文件管理器）
   → 允许安装未知应用
   ```

2. **Android 7.0 及以下**
   ```
   设置 → 安全
   → 未知来源（勾选）
   ```

### 安装 APK

1. 将 APK 文件传输到手机
2. 使用文件管理器打开 APK
3. 点击安装
4. 等待安装完成

## ⚙️ 配置视频解析服务

### 当前问题

应用中的视频解析功能使用了第三方 API，这些 API 可能：
- 不稳定
- 有使用限制
- 随时可能失效

### 推荐解决方案

#### 方案 1：部署自己的解析服务

1. **使用 yt-dlp（推荐）**

   创建简单的后端服务：

   ```python
   # server.py
   from flask import Flask, request, jsonify
   import yt_dlp
   
   app = Flask(__name__)
   
   @app.route('/parse', methods=['POST'])
   def parse_video():
       url = request.json.get('url')
       
       ydl_opts = {
           'format': 'best',
           'quiet': True,
       }
       
       with yt_dlp.YoutubeDL(ydl_opts) as ydl:
           info = ydl.extract_info(url, download=False)
           return jsonify({
               'title': info.get('title'),
               'video_url': info.get('url')
           })
   
   if __name__ == '__main__':
       app.run(host='0.0.0.0', port=5000)
   ```

2. **部署到云服务器**
   - 阿里云、腾讯云、AWS 等
   - 获取公网 IP 或域名

3. **修改 Android 应用**
   在 `VideoDownloadManager.kt` 中：
   ```kotlin
   private suspend fun parseVideoInfo(url: String): VideoInfo {
       val apiUrl = "http://你的服务器IP:5000/parse"
       // ... 调用自己的API
   }
   ```

#### 方案 2：使用稳定的第三方服务

修改 `VideoDownloadManager.kt`，使用以下稳定的解析 API：

- **TikTok/抖音**：https://tikwm.com/api/
- **通用解析**：https://youtube-dl-api.herokuapp.com/

#### 方案 3：使用 WebView + 第三方解析网站

修改应用，嵌入 WebView 加载解析网站：
- https://snaptik.app/
- https://ssstik.io/
- https://www.tikwm.com/

## 🐛 常见问题

### Q1: 构建失败，提示 "SDK not found"

**解决**：
1. 确认 Android SDK 已正确安装
2. 检查 `local.properties` 文件是否存在
3. 如不存在，手动创建并添加：
   ```
   sdk.dir=C:\\Users\\你的用户名\\AppData\\Local\\Android\\Sdk
   ```

### Q2: Gradle 同步失败

**解决**：
1. 检查网络连接（可能需要科学上网）
2. 修改 `build.gradle` 中的仓库地址为国内镜像：
   ```groovy
   repositories {
       maven { url 'https://maven.aliyun.com/repository/google' }
       maven { url 'https://maven.aliyun.com/repository/public' }
       google()
       mavenCentral()
   }
   ```

### Q3: 解析视频失败

**解决**：
1. 检查网络连接
2. 尝试使用 VPN
3. 更换解析 API
4. 部署自己的解析服务

### Q4: 下载的视频无法播放

**解决**：
1. 确认视频格式受支持
2. 尝试使用其他播放器
3. 检查视频是否完整下载

## 📝 开发建议

### 改进方向

1. **添加更多平台支持**
   - YouTube
   - Instagram
   - Facebook
   - Twitter

2. **优化用户体验**
   - 添加下载历史
   - 批量下载
   - 视频格式转换

3. **提高解析成功率**
   - 使用多个解析 API 备份
   - 实现智能切换
   - 添加解析失败重试

## 🚫 法律声明

- 本应用仅供学习交流使用
- 请遵守各平台的服务条款
- 不得用于商业用途
- 下载的视频版权归原作者所有
- 使用本应用产生的任何法律后果由使用者自行承担

## 📧 联系方式

如有问题或建议，欢迎联系。

---

**祝使用愉快！🎉**
