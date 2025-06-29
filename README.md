# 🔌 RK Hardware Control Plugin for Flutter

A Flutter plugin for controlling hardware-level features on **Rockchip RK series** boards (e.g. RK3288, RK3399, RK3568). This plugin enables system-level control directly from Flutter apps, perfect for embedded devices and digital signage solutions.

---

## 📋 Features

- ✅ **Power Control**: Turn the device on or off
- ⏰ **Scheduled Power On/Off**: Automatically power the device on or off at a specified time
- 💡 **Screen Control**: Manage screen state (on/off)
- 🕒 **System Time Configuration**: Set or synchronize system time

---

## 🧩 Supported Devices

- ✅ RK3288
- ✅ RK3399
- ✅ RK3568
- ✅ Other Rockchip RK series boards  
> Requires Android OS (tested on Android 7.1+)

---

## 📦 Installation

Add the dependency in your `pubspec.yaml`:

```yaml
dependencies:
  rk_hardware_control: ^1.0.0
```

> Replace with the actual plugin name and version.

---

## 🚀 Getting Started

Import the plugin:

```dart
import 'package:rk_hardware_control/rk_hardware_control.dart';
```

### 🔌 Power Control

```dart
await RkHardwareControl.shutdown(); // Shutdown device
```

### ⏰ Scheduled Power On/Off

```dart
await RkHardwareControl.schedulePowerOn("08:00");
await RkHardwareControl.schedulePowerOff("22:00");
```

### 💡 Screen Control

```dart
await RkHardwareControl.turnScreenOff();
await RkHardwareControl.turnScreenOn();
```

### 🕒 System Time

```dart
await RkHardwareControl.setSystemTime("2025-06-29 15:30:00");
```

---

## 🛠️ Permissions

Ensure your app has **system-level permissions** and is **signed with the platform key**, as most of these controls require elevated access.

---

## 📄 License

This project is licensed under the [Apache License 2.0](./LICENSE).

---

## 🙋 Support

For issues, feature requests, or contributions, please open an [Issue](https://github.com/your-repo/issues) or submit a [Pull Request](https://github.com/your-repo/pulls).
