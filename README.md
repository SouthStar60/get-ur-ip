# Get Ur IP

![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android)
![minSdk](https://img.shields.io/badge/minSdk-24-5C6BC0)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)

English|[中文](README-zh_CN.md)

For this project, I personally only asked the AI questions; it was developed entirely by Doubao AI.

A minimal Android app that shows exactly three things:

| Function | Description |
| --- | --- |
| **Local IP** | Enumerates the device's network interfaces and lists the intranet IPv4 address (and global IPv6 if present), with the interface name, e.g. `192.168.1.10 (wlan0)` |
| **Public IPv4** | Queries echo services that only publish `A` records, forcing the request over the IPv4 stack |
| **Public IPv6** | Queries echo services that only publish `AAAA` records, forcing the request over the IPv6 stack |

Each card can be refreshed separately, or use **Refresh all**. Tap any address to copy it. Dark mode is supported.

## Features

- One screen, three cards: local IP, public IPv4, public IPv6
- Per-card refresh and one-tap refresh-all
- Tap an address to copy it to the clipboard
- Material 3 design, day/night theme
- No tracking, no ads, no personal data collection

## How it works

- **Local addresses** are read from `java.net.NetworkInterface.getNetworkInterfaces()`, iterating over interfaces that are up and skipping loopback / link-local addresses.
- **Public IPv4** is fetched, in order, from:
  - `https://ipv4.icanhazip.com`
  - `https://v4.ident.me`
  - `https://api.ipify.org`
- **Public IPv6** is fetched, in order, from:
  - `https://ipv6.icanhazip.com`
  - `https://v6.ident.me`
  - `https://api6.ipify.org`
- Requests use `HttpURLConnection` and run on `Dispatchers.IO` via Kotlin Coroutines, so the main thread is never blocked.
- If the current network has no IPv6 connectivity (common on some Wi-Fi / 4G networks), the IPv6 card shows a clear failure message instead of crashing.

## Requirements

- Android 7.0 or newer (minSdk 24), compile/target SDK 34
- JDK 17
- Gradle 8.7, Android Gradle Plugin 8.5, Kotlin 1.9

## Build

### Android Studio (recommended)

1. Open this directory in Android Studio (Hedgehog or newer).
2. Wait for the Gradle sync to finish.
3. Connect a device (USB debugging enabled) or start an emulator, then click Run.

### Command line

```bash
# Generate the Gradle wrapper if it is not present
gradle wrapper --gradle-version 8.7

# Build the debug APK
./gradlew assembleDebug

# Install on a connected device
./gradlew installDebug
```

Output APK: `app/build/outputs/apk/debug/app-debug.apk`

## Project structure

```
.
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml          # Declares INTERNET / network-state permissions
│       ├── java/com/example/iptools/
│       │   ├── MainActivity.kt          # UI, refresh actions, copy-to-clipboard
│       │   └── IpUtils.kt               # Local/public IP lookup logic
│       └── res/                         # Layouts, themes, icons
├── build.gradle.kts
├── settings.gradle.kts
├── gen_icons.py                         # Optional: regenerates launcher icon PNGs
└── LICENSE
```

## Permissions & Privacy

- `INTERNET` — calls the public echo services to discover the public IP.
- `ACCESS_NETWORK_STATE` — checks network connectivity.

The app does not collect, store, or upload any personal information.

## Third-Party Services, Libraries & Assets

**This project does not copy source code from any other project.** All Kotlin/Java code in this repository was written for this app. It nevertheless relies on the following third-party components:

### Runtime libraries (all licensed under the Apache License 2.0)

- [AndroidX](https://developer.android.com/jetpack/androidx) (`core-ktx`, `appcompat`)
- [Material Components for Android](https://github.com/material-components/material-components-android)
- [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines)

### Assets

- The refresh icon at `app/src/main/res/drawable/ic_refresh.xml` is adapted from [Material Icons](https://github.com/google/material-design-icons) by Google, licensed under the Apache License 2.0.
- The launcher globe icon (`ic_launcher_foreground.xml` and the generated PNGs) is original, simple geometry created for this project.

### Network services (external services, not bundled code)

The app sends a plain HTTPS `GET` to these free public IP-echo services and reads the returned address:

- [icanhazip.com](https://icanhazip.com/) (Cloudflare)
- [ident.me](https://ident.me/)
- [ipify.org](https://www.ipify.org/)

Please review and respect each service's terms of use. They are called over HTTPS, and no extra data is sent besides a standard request with a user-agent string.

## License

This project is licensed under the [MIT License](LICENSE). The third-party libraries and assets listed above remain under their respective licenses.
