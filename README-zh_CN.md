# Get Ur IP

![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android)
![minSdk](https://img.shields.io/badge/minSdk-24-5C6BC0)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)

[English](README.md)|中文

此项目本人仅向ai提出问题，完全由豆包ai开发。

一个极简的 Android 应用，只做三件事：

| 功能 | 说明 |
| --- | --- |
| **内网 IP** | 遍历本机网卡，列出内网 IPv4 地址（若有全局 IPv6 也会列出），并标注网卡名，如 `192.168.1.10 (wlan0)` |
| **公网 IPv4** | 访问仅发布 `A` 记录的回显服务，强制请求走 IPv4 协议栈 |
| **公网 IPv6** | 访问仅发布 `AAAA` 记录的回显服务，强制请求走 IPv6 协议栈 |

每个卡片可单独刷新，也可一键「全部刷新」；点击任意地址即可复制；支持深色模式。

## 功能特性

- 一屏三卡片：内网 IP、公网 IPv4、公网 IPv6
- 每个卡片可单独刷新，也可一键全部刷新
- 点击地址即可复制到剪贴板
- Material 3 设计，支持浅色 / 深色主题
- 无追踪、无广告、不收集个人数据

## 实现原理

- **内网地址**：通过 `java.net.NetworkInterface.getNetworkInterfaces()` 获取所有处于启用（UP）状态的网卡，跳过回环和链路本地地址。
- **公网 IPv4** 依次请求：
  - `https://ipv4.icanhazip.com`
  - `https://v4.ident.me`
  - `https://api.ipify.org`
- **公网 IPv6** 依次请求：
  - `https://ipv6.icanhazip.com`
  - `https://v6.ident.me`
  - `https://api6.ipify.org`
- 网络请求使用 `HttpURLConnection`，通过 Kotlin 协程在 `Dispatchers.IO` 上执行，不会阻塞主线程。
- 若当前网络没有 IPv6（部分 Wi-Fi / 4G 网络常见），IPv6 卡片会显示明确的失败提示，而不是闪退。

## 运行环境

- Android 7.0 及以上（minSdk 24），compile/target SDK 34
- JDK 17
- Gradle 8.7、Android Gradle Plugin 8.5、Kotlin 1.9

## 构建方式

### Android Studio（推荐）

1. 用 Android Studio（Hedgehog 或更新版本）打开本目录；
2. 等待 Gradle 同步完成；
3. 连接手机（开启 USB 调试）或启动模拟器，点击运行。

### 命令行

```bash
# 如果没有 Gradle Wrapper，先生成
gradle wrapper --gradle-version 8.7

# 构建 debug APK
./gradlew assembleDebug

# 安装到已连接的设备
./gradlew installDebug
```

产物路径：`app/build/outputs/apk/debug/app-debug.apk`

## 项目结构

```
.
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml          # 声明网络等权限
│       ├── java/com/example/iptools/
│       │   ├── MainActivity.kt          # 界面、刷新、复制逻辑
│       │   └── IpUtils.kt               # 内网 / 公网 IP 获取核心逻辑
│       └── res/                         # 布局、主题、图标
├── build.gradle.kts
├── settings.gradle.kts
├── gen_icons.py                         # 可选：重新生成启动图标 PNG
└── LICENSE
```

## 权限与隐私

- `INTERNET`：访问公共回显服务以获取公网 IP；
- `ACCESS_NETWORK_STATE`：检测网络连接状态。

本应用不收集、不存储、不上传任何个人信息。

## 第三方服务、库与资产说明

**本项目没有复制任何其他项目的源代码**，仓库中的 Kotlin/Java 代码均为本应用原创编写。但项目依赖或使用了以下第三方内容：

### 运行时依赖库（均为 Apache License 2.0 许可）

- [AndroidX](https://developer.android.com/jetpack/androidx)（`core-ktx`、`appcompat`）
- [Material Components for Android](https://github.com/material-components/material-components-android)
- [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines)

### 资产

- 刷新图标 `app/src/main/res/drawable/ic_refresh.xml` 改编自 Google 的 [Material Icons](https://github.com/google/material-design-icons)，采用 Apache License 2.0 许可。
- 启动器地球图标（`ic_launcher_foreground.xml` 及生成的 PNG）是为本项目创作的原创简单几何图形。

### 网络服务（外部服务，并非打包进应用的代码）

应用会向以下免费的公共 IP 回显服务发送一次普通的 HTTPS `GET` 请求，并读取返回的地址：

- [icanhazip.com](https://icanhazip.com/)（Cloudflare）
- [ident.me](https://ident.me/)
- [ipify.org](https://www.ipify.org/)

请查阅并尊重各服务的使用条款。请求均通过 HTTPS 发送，除带 User-Agent 的标准请求外不会发送其他数据。

## 许可证

本项目基于 [MIT 许可证](LICENSE) 开源。上述第三方库与资产仍遵循其各自的许可证。
