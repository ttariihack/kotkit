# KotKit Basic
jos jos jos jis 

**AI-powered Android client for automated TikTok video posting.**

This is the **mobile client app** that works with KotKit's backend AI service. The app sends screenshots to the backend, receives intelligent actions (tap, swipe, type), and executes them via Android Accessibility Service. All AI/VLM processing happens on the server - the app contains **zero AI models** locally.

**Why open source?** Transparency and trust. You can verify that the app only:
- Accesses TikTok (no other apps)
- Sends screenshots to backend API for analysis
- Does NOT collect personal data, passwords, or contacts

For individual creators and content publishers.

---

## 🔍 Проверьте код сами / Verify the Code

**Не доверяйте - проверьте!** Don't trust - verify!

Вставьте ссылку в любой AI (Claude, ChatGPT, DeepSeek):
```
https://github.com/pashechello/kotkit
```

Спросите / Ask:
```
Проанализируй этот Android проект. Как работает Accessibility Service и к каким приложениям он имеет доступ? Какие разрешения запрашивает приложение? Какие данные отправляются на сервер?
```
```
Analyze this Android app. How does the Accessibility Service work and which apps can it access? What permissions does the app request? What data is sent to the server?
```

AI прочитает весь код и скажет что там. / AI will read all code and tell you what's there.

---

## Features

### Personal Mode (Creator Mode)
Default mode for content creators who want to automate their own content posting:

- **Automated Video Posting** - Schedule and publish videos to TikTok automatically
- **Smart Scheduler** - AI-powered optimal posting time suggestions based on audience activity
- **Video Queue** - Manage multiple videos with drag-and-drop reordering
- **AI Caption Generation** - Generate engaging captions and hashtags via backend API
- **Publishing History** - Track all published videos with analytics
- **Localization** - Russian and English languages supported

### Worker Mode (Network Mode)
**Зарабатывайте на своём TikTok аккаунте!**

KotKit Network соединяет бренды (рекламодателей) с владельцами TikTok аккаунтов. Бренды платят за публикацию своих видео на вашем аккаунте.

**Как это работает:**
1. Вы включаете Worker Mode в приложении
2. Получаете задачи от брендов (видео + описание)
3. Приложение автоматически публикует видео в ваш TikTok
4. Через 24 часа проверяется что видео не удалено
5. Вы получаете оплату за каждый успешный пост

**Возможности:**
- **Task Assignment** - Receive posting tasks from advertisers
- **Automated Publishing** - Download and publish videos automatically
- **Reward System** - Earn USD for each successful publication
- **Payout Options** - Withdraw via cryptocurrency, bank cards, or local payment systems (СБП, карты)
- **Anti-Fraud Protection** - 24-hour verification ensures fair payment
- **Resume Downloads** - Network interruption recovery for large video files

## Requirements

- Android 7.0 (API 24) or higher
- TikTok app installed
- Accessibility Service permission enabled

## Screen Unlock

KotKit uses **only Accessibility Service** for screen unlock — no ADB, no root, no special setup required.

| Lock Type | How It Works |
|-----------|--------------|
| Swipe only | Accessibility Service swipes to unlock |
| PIN | Reads PIN pad from UI tree, taps each digit |
| Password | *Coming soon* |

### How PIN unlock works

1. User saves PIN in the app (stored encrypted via Android Keystore)
2. When posting time comes, app wakes the screen
3. Accessibility Service swipes up to show PIN pad
4. Reads PIN button coordinates from `rootInActiveWindow` (UI tree)
5. Uses `dispatchGesture()` to tap each digit
6. Phone unlocked → TikTok opens → video posted

**Key insight**: Android's TalkBack (for visually impaired users) works on lockscreen. Our Accessibility Service uses the same APIs, so it works too — even on MIUI/HyperOS where shell commands are blocked.

### Why no ADB?

Previous versions used ADB/Wireless Debugging for PIN entry. We removed it because:
- Complex setup (Developer Options, pairing codes)
- Breaks after reboot
- Doesn't work on some devices (MIUI blocks shell input)
- Accessibility approach is simpler and more reliable

## Installation

### From Releases
1. Download the latest APK from [Releases](https://github.com/pashechello/kotkit/releases)
2. Install APK on your device (enable "Install from unknown sources" if needed)
3. Open the app and follow the setup wizard
4. Enable Accessibility Service in Android Settings

### Build from Source
```bash
# Clone the repository
git clone https://github.com/pashechello/kotkit.git
cd kotkit

# Configure local.properties (optional, for release signing)
cat > local.properties << EOF
sdk.dir=/path/to/your/Android/sdk
RELEASE_STORE_FILE=path/to/your/keystore.jks
RELEASE_STORE_PASSWORD=your_store_password
RELEASE_KEY_ALIAS=your_key_alias
RELEASE_KEY_PASSWORD=your_key_password
EOF

# Build debug APK
./gradlew assembleDebug

# APK will be in app/build/outputs/apk/debug/
```

## How It Works

**Client-Server Architecture:**

```
┌─────────────────────────────────┐
│   📱 Your Android Device         │
│                                  │
│  ┌───────────────────────────┐  │
│  │  KotKit Basic (this app)  │  │
│  │                           │  │
│  │  1. Capture screenshot    │──┼──┐
│  │  2. Send to backend API   │  │  │  HTTPS + JWT
│  │  3. Receive action        │◄─┼──┘  (api.kotkit.pro)
│  │  4. Execute via           │  │
│  │     AccessibilityService  │  │
│  └───────────────────────────┘  │
│                                  │
│  ┌───────────────────────────┐  │
│  │     TikTok App            │  │
│  │  (automated by above)     │  │
│  └───────────────────────────┘  │
└─────────────────────────────────┘
                │
                │ Screenshot (JPEG, 720x1440)
                │ UI Tree (accessibility nodes)
                ▼
┌─────────────────────────────────┐
│  ☁️  KotKit Backend (closed)    │
│                                  │
│  - Vision-Language Model (VLM)  │
│  - AI decision making            │
│  - Action planning               │
│                                  │
│  Returns: {action: "tap",        │
│            x: 540, y: 960}       │
└─────────────────────────────────┘
```

**Publishing Flow:**

1. **Unlock Screen** - Automatically unlocks the device if needed
2. **Launch TikTok** - Opens TikTok via share intent with the video
3. **AI-Guided Navigation** - Backend VLM analyzes screenshots and provides actions
4. **Add Caption** - Enters the caption and hashtags
5. **Publish** - Taps the publish button and verifies success
6. **Extract Link** - Copies the published video URL for tracking

**What stays on device:**
- Video files (in your gallery)
- Posting history (SQLite database)
- Encrypted credentials (Android Keystore)

**What goes to backend:**
- Screenshots of TikTok UI (for AI analysis)
- UI accessibility tree (button coordinates)
- Task context (caption, video filename)

**No AI models on device** - all intelligence is server-side. This keeps the app small, fast, and allows us to improve the AI without requiring app updates.

## Architecture

```
kotkit-basic/
├── app/
│   └── src/main/kotlin/com/kotkit/basic/
│       ├── agent/              # PostingAgent - Core posting logic
│       │   ├── PostingAgent.kt
│       │   ├── AgentState.kt
│       │   └── ActionHandler.kt
│       ├── executor/           # ActionExecutor - UI automation
│       │   ├── accessibility/  # Accessibility Service
│       │   ├── screen/         # Screen unlock, wake lock
│       │   ├── screenshot/     # Screenshot capture
│       │   └── humanizer/      # Human-like action timing
│       ├── scheduler/          # SmartScheduler (Personal Mode)
│       │   ├── SmartScheduler.kt
│       │   ├── PostWorker.kt
│       │   └── SchedulerReceiver.kt
│       ├── network/            # Network workers (Worker Mode)
│       │   ├── NetworkWorkerService.kt
│       │   ├── NetworkTaskExecutor.kt
│       │   ├── VideoDownloader.kt
│       │   └── HeartbeatWorker.kt
│       ├── data/
│       │   ├── local/          # Room database
│       │   ├── remote/api/     # Retrofit API client
│       │   └── repository/     # Data repositories
│       ├── di/                 # Hilt DI modules
│       ├── security/           # SSL pinning, integrity checks
│       └── ui/
│           ├── screens/        # Compose UI screens
│           ├── components/     # Reusable components
│           └── navigation/     # Navigation graph
└── README.md
```

## Technology Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **DI**: Hilt (Dagger)
- **Database**: Room
- **Network**: Retrofit + OkHttp with SSL pinning
- **Background**: WorkManager + Foreground Services
- **Async**: Coroutines + Flow
- **Security**: Android Keystore (encrypted PIN storage), Conscrypt (TLS)

## API Integration

The app communicates with the KotKit backend API for:

- **Authentication** - JWT-based auth with token refresh
- **AI Analysis** - Screenshot analysis and action planning
- **Task Management** - Worker mode task assignment and tracking
- **Configuration** - Remote config and feature flags
- **Analytics** - Usage tracking and error reporting

All AI/ML processing is server-side. The mobile app contains no local AI models.

## Security

**Full security documentation: [SECURITY.md](SECURITY.md)**

Key points:
- **Accessibility Service is TikTok-only** - enforced at Android OS level via `packageNames` attribute
- **3-layer package restriction** - XML manifest + runtime constant + event filtering
- **Server cannot bypass restrictions** - it can only send action commands, not change which apps are accessible
- **PIN encrypted with AES-256-GCM** - stored locally, never transmitted to server
- **SSL certificate pinning** - only communicates with `api.kotkit.pro`
- **No dangerous permissions** - no contacts, SMS, camera, microphone, location

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

Apache License 2.0 - see [LICENSE](LICENSE)

## Support

- Documentation: [docs.kotkit.pro](https://docs.kotkit.pro)
- Issues: [GitHub Issues](https://github.com/pashechello/kotkit/issues)
- Website: [kotkit.pro](https://kotkit.pro)
