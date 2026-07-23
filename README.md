# EXE-EDITOR
This repository contains a minimal Android app scaffold (`android-app/`) that demonstrates offline FFmpeg usage via FFmpegKit.

What I added:
- `android-app/` — Android Studio Gradle project (Kotlin) with FFmpegKit dependency.
- `.github/workflows/android-build.yml` — GitHub Actions workflow that builds a debug APK and creates a Release with the APK attached.

How to get the APK (you must push this repo to GitHub first):

1. Push the repository to your GitHub account under `rogghost266-art/EXE-EDITOR`.
2. GitHub Actions will run on push to `main` and build the APK.
3. The workflow creates a release `v0.1.0` and uploads the APK as `EXE-Editor-debug.apk`.

Notes and limitations:
- The FFmpegKit Maven coordinate used is `com.arthenica:ffmpeg-kit-full:4.5.LTS` — builds may require network access to download dependencies.
- This is a minimal starter — full beat-detection, auto-tune, and UI features are left as next steps due to complexity.

If you want I can: commit and push these changes to your GitHub repo and trigger the build (I will need permission to push). Otherwise follow the steps above to push from your side and the APK will appear in Releases.