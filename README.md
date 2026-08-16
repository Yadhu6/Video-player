# Pocket Player

A local-video Android app written in Kotlin.

## Features
- Reads phone videos using Android MediaStore
- Runtime video permission handling
- Video thumbnails, filename, duration and file size
- Media3 / ExoPlayer playback
- Built-in play/pause and seek controls
- Next/previous video through a playlist
- Fullscreen immersive playback and device rotation
- Android 8.0+ (minSdk 26), target API 36

## Build
1. Open this folder in a recent Android Studio.
2. Let Android Studio sync Gradle.
3. If the project reports the Gradle wrapper JAR is missing, choose Android Studio's **Gradle > Wrapper** repair/generation option or run `gradle wrapper` once with Gradle 8.13 installed.
4. Build > Build APK(s).
5. Install the generated debug APK on your phone and grant video access.

The project uses Media3 1.11.0.
