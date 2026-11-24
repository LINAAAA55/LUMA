# LUMA — Wake Up to Your Rhythm 

LUMA is a modern, feature-rich sleep planning, tracking, and independent alarm clock Android application designed with an elegant nighttime aesthetic inspired by the moon, stars, and peaceful sleep.

---

## Key Features

### 1. The Science of Sleep (Educational Dashboard)
- **Permanent Top Section**: Features an interactive educational card explaining why sleep cycles matter.
- **Sleep Stages**: Breaks down N1 (light sleep), N2 (stable light), N3 (deep sleep), and REM sleep.
- **Key Insights**: Highlights memory consolidation, physical recovery, brain health, and consistency goals.

### 2. Smart Sleep Calculator
- **Mode A (When to Wake Up)**: Input your bedtime and estimated sleep latency to calculate optimal wake-up times based on natural sleep cycles.
- **Mode B (When to Sleep)**: Input your desired wake-up time to calculate ideal bedtimes.
- **Direct Alarm Integration**: Every suggested time includes a **"Set Alarm"** button that instantly pre-fills the alarm creation dialog.

### 3. Real Android Alarm Clock & Foreground Service
- **Independent Alarm System**: Create alarms for any time without needing the sleep calculator.
- **Full-Screen Ringing Experience**: When an alarm triggers, it launches a dedicated full-screen alarm activity over the lock screen with **STOP** and **SNOOZE** (5-minute interval) buttons.
- **Continuous Ringtone & Vibration**: Plays the default system alarm sound in an infinite loop (`MediaPlayer`) with repeating vibrations until the user actively interacts.
- **Background Reliability**: Powered by Android `AlarmManager` and Foreground Services (`AlarmService`), ensuring alarms ring even when the app is minimized, closed, or the phone screen is locked.
- **Reboot Persistence**: Automatically reschedules active alarms after device reboots (`BOOT_COMPLETED`).

### 4. Sleep Tracker & Insights
- **Log Sleep Sessions**: Record bedtimes, wake-up times, duration, quality ratings (⭐ 1-5), and personal notes.
- **Weekly Overview**: Monitor average sleep duration and sleep consistency.

### 5. Local-First Privacy
- **100% Offline**: All sleep sessions, alarms, and preferences are stored securely on your device using a local SQLite database (`DatabaseHelper`). No cloud sync, accounts, or internet permissions required.

---

## Visual Identity & Color Palette

- **Midnight Navy** (`#101426`) — Primary background
- **Deep Blue** (`#1B223B`) — Cards & containers
- **Soft Lavender** (`#A99BFF`) — Primary accent
- **Light Blue** (`#8DC9E8`) — Secondary accent
- **Warm Cream** (`#F2E9D8`) — Highlights & moon
- **App Icon**: Custom vector pixel-art sleeping cat on a crescent moon.

---

##  Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose & Material Design 3
- **Local Storage**: SQLite (`SQLiteOpenHelper`)
- **Alarm Scheduling**: Android `AlarmManager`, Foreground Services, BroadcastReceivers
- **Audio & Haptics**: `MediaPlayer` & `Vibrator`

---

## Getting Started & Testing

1. Clone the repository:
   ```bash
   git clone https://github.com/LINAAAA55/LUMA.git
   ```
2. Open the project in **Android Studio**.
3. Build and run on an Android emulator or physical device (`API 24+`).
