# ArrMatey — Claude Context

## What this repo is

Fork of [owenlejeune/ArrMatey](https://github.com/owenlejeune/ArrMatey) at **https://github.com/DavidSobral/ArrMatey**.

ArrMatey is a **Kotlin Multiplatform (KMP)** app — it has a shared Kotlin core and an iOS SwiftUI app. The iOS app lives in `iosApp/`.

## Goal

Build an **unsigned IPA for sideloading** (AltStore, Sideloadly, TrollStore) via GitHub Actions, without needing an Apple Developer account or certificates. The IPA is published directly to GitHub Releases.

## Project structure

```
/                          KMP root — Gradle project
  shared/                  Kotlin Multiplatform shared module
  composeApp/              Android app
  iosApp/
    iosApp.xcworkspace     Open this in Xcode (not .xcodeproj)
    iosApp.xcodeproj/
    iosApp/
      Assets.xcassets/     App icon is AppIcon.appiconset/AppIcon.png (1024x1024)
      iOSApp.swift         App entry point + View extensions
    iosApp.xcworkspace/xcshareddata/swiftpm/Package.resolved
.github/workflows/
  android.yml              Android CI (pre-existing)
  ios.yml                  iOS CI (added by us)
```

## GitHub Actions — iOS CI (`ios.yml`)

Trigger: **manual** (`workflow_dispatch`) from the Actions tab.

Key design decisions:
- Runs on `macos-latest` (Xcode 16.4, Swift 6.1)
- JDK 17 set up before Xcode build so Gradle can compile the shared KMP framework (triggered automatically by the Xcode build phase `./gradlew :shared:embedAndSignAppleFrameworkForXcode`)
- Code signing fully disabled — sideloading tools re-sign at install time
- IPA packaged manually: `zip -r ArrMatey.ipa Payload/` (standard IPA format)
- Published to GitHub Releases under tag `automated-ios` (prerelease)
- Job needs `permissions: contents: write` to create releases

## Fixes applied vs upstream (all in this fork)

| File | Change | Reason |
|---|---|---|
| `.github/workflows/ios.yml` | Added `-destination 'generic/platform=iOS'` | Without it xcodebuild picked macOS destination |
| `iosApp/iosApp.xcodeproj/project.pbxproj` | Pinned MarkdownView to `exactVersion 2.5.2` | 2.6.x uses `isolated deinit` (SE-0371) which requires Swift 6.2; Xcode 16.4 ships with Swift 6.1 |
| `iosApp/iosApp.xcworkspace/.../Package.resolved` | Pinned MarkdownView revision to 2.5.2 | Matches the pbxproj pin |
| `iosApp/iosApp/iOSApp.swift` | Stubbed `glassCompatibleButtonStyle()` to return `self` | `.glassProminent` is an iOS 26 API not yet in the Xcode 16.4 SDK |
| `iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/` | Added 1024x1024 AppIcon.png + Contents.json | App had no appiconset; now uses custom icon |

## Known constraints

- **MarkdownView pinned to 2.5.2** — do NOT bump to 2.6.x until CI runs on Xcode with Swift 6.2 (likely Xcode 17 / WWDC 2026). The `exactVersion` constraint in `project.pbxproj` prevents accidental resolution.
- **`.glassProminent` stubbed** — `glassCompatibleButtonStyle()` in `iOSApp.swift` returns `self` instead of applying the iOS 26 glass button style. Restore when building with Xcode 17+ and iOS 26 SDK.
- **No code signing** — the IPA is unsigned. It must be sideloaded via AltStore, Sideloadly, or TrollStore. It cannot be installed directly.

## Running locally

```bash
# Build the KMP shared framework first (needed before opening Xcode)
./gradlew :shared:assembleDebug

# Then open the Xcode workspace (NOT .xcodeproj)
open iosApp/iosApp.xcworkspace
```

## App icon

Custom icon added at `iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/AppIcon.png` (1024x1024, cropped from the original photo at `/Users/david.sobral/Downloads/ArrMateyIcon.png`).
