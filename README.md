# 视频下载器 Android 应用

支持多平台视频下载的 Android 应用。

## 支持的视频平台

- ✅ TikTok / 抖音
- 🚧 小红书 (需要完善)
- 🚧 B站 (需要完善)
- 🚧 更多平台即将支持

## 功能特点

- 📥 支持多个主流视频平台
- 🚀 快速解析视频链接
- 💾 一键下载到本地
- 🎨 简洁美观的 Material Design 界面
- 📋 支持粘贴和分享链接

## 技术栈

- Kotlin
- Android Architecture Components
- OkHttp (网络请求)
- Coroutines (异步处理)
- Material Design Components

## 快速开始

### 前置要求

- Android Studio Arctic Fox 或更高版本
- Android SDK 21+
- Kotlin 1.8+

### 构建步骤

1. 克隆或下载本项目
2. 使用 Android Studio 打开项目
3. 等待 Gradle 同步完成
4. 点击 "Run" 按钮或执行 `./gradlew assembleDebug`

详细说明请查看 [BUILD.md](BUILD.md)

## 项目结构

```
app/
├── src/main/
│   ├── java/com/videodownloader/
│   │   ├── ui/                    # UI 相关
│   │   │   └── MainActivity.kt
│   │   ├── download/             # 下载逻辑
│   │   │   └── VideoDownloadManager.kt
│   │   └── VideoDownloaderApp.kt # Application 类
│   ├── res/                      # 资源文件
│   └── AndroidManifest.xml
└── build.gradle
```

## 重要说明

### 视频解析

由于各大平台的反爬虫机制，视频解析是本项目的技术难点：

1. **建议使用后端服务**
   - 部署自己的解析服务器
   - 或使用稳定的第三方解析 API

2. **可用的解析方案**
   - TikWM API (TikTok)
   - 自定义 WebView 解析
   - yt-dlp 部署为服务

### 法律声明

- 本应用仅供个人学习交流使用
- 请遵守各视频平台的使用条款
- 不得用于商业用途
- 下载的视频版权归原作者所有

## 贡献指南

欢迎提交 Issue 和 Pull Request！

## 许可证

MIT License

## 联系方式

如有问题或建议，欢迎联系。

---

**注意**：本应用使用了第三方解析服务，这些服务的稳定性无法保证。建议在生产环境中部署自己的解析服务。
