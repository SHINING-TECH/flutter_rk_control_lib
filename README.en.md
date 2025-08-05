# 🔌 RK Hardware Control Flutter Plugin [Switch to Chinese Version](./README.md)

A Flutter plugin for controlling hardware-level functions of **Rockchip RK series** development boards (such as RK3288, RK3399, RK3568). This plugin allows system-level control (motherboard custom API) directly from Flutter applications.

---

## 📋 Features
- ✅ **Power Control**: Shutdown, reboot
- 🧭 **Status Bar & Navigation Bar Control**: Show or hide status bar and virtual navigation bar
- 📡 **GOIP Status Reading & Setting**: Get and set GOIP module status
- 💡 **LED Strip Control**: Control motherboard LED strip status
- ⚡ **Relay Control**: Control external relay on/off
- 🌞 **Screen Brightness**: Get and set screen brightness
- 📸 **Screenshot Function**: Capture current screen
- 🛠️ **Silent Upgrade**: Perform system upgrade tasks in the background
- ⏰ **Scheduled Power On/Off**: Automatically power on or off the device at specified times
- 💡 **Screen Control**: Manage screen status (on/off)
- 🕒 **System Time Configuration**: Set or synchronize system time

---

## 🧩 Supported Devices
| Manufacturer | Model | Notes |
|--------------|-------|-------|
| Shimeitai | RK3288 | |
|             | RK3568 | |
| Yisheng | RK3288 | |
|             | RK3568 | |

---

## 📦 Installation
Add dependency in `pubspec.yaml`:
```yaml
dependencies:
  rk_control_lib: ^1.0.0
```

---

## 🚀 Quick Start
Import the plugin:
```dart
import 'package:rk_control_lib/rk_control_lib.dart';
```

### ✅ **Power Control**: Shutdown, Reboot
```dart
await RkHardwareControl.shutdown(); // Shutdown
await RkHardwareControl.reboot();   // Reboot
```

### 🧭 **Status Bar & Navigation Bar Control**: Show or hide status bar and virtual navigation bar
```dart
await _rkControlLibPlugin.showStatusBar(value);
```

### 📡 **GOIP Status Reading & Setting**: Get and set GOIP module status
```dart
```

### 💡 **LED Strip Control**: Control motherboard LED strip status
```dart
```

### ⚡ **Relay Control**: Control external relay on/off
```dart
```

### 🌞 **Screen Brightness**: Get and set screen brightness
```dart
```

### 📸 **Screenshot Function**: Capture current screen
```dart
```

### 🛠️ **Silent Upgrade**: Perform system upgrade tasks in the background
```dart
```

### ⏰ **Scheduled Power On/Off**: Automatically power on or off the device at specified times
```dart
```

### 💡 **Screen Control**: Manage screen status (on/off)
```dart
await _rkControlLibPlugin.sleepScreen();
await _rkControlLibPlugin.wakeScreen();
```

### 🕒 **System Time Configuration**: Set or synchronize system time
```dart
await _rkControlLibPlugin.setSystemTime("2024-05-20 12:00:00");
```

## 📄 License
This project is licensed under the [Apache License 2.0](./LICENSE).

---

## 🙋 Support
For issues, feature requests, or contributions, please create an [Issue](https://github.com/SHINING-TECH/flutter_rk_control_lib/issues) or submit a [Pull Request](https://github.com/SHINING-TECH/flutter_rk_control_lib/pulls).