# 🔌 RK硬件控制Flutter插件

[切换到英文版本](./README.en.md)

一个用于控制**瑞芯微RK系列**开发板（如RK3288、RK3399、RK3568）硬件级功能的Flutter插件。该插件允许从Flutter应用直接进行系统级控制（主板定制API）。

A Flutter plugin for controlling hardware-level functions of **Rockchip RK series** development boards (such as RK3288, RK3399, RK3568). This plugin allows system-level control (motherboard custom API) directly from Flutter applications.


---

## 📋 特性

- ✅ **电源控制**: 关机,重启
- 🧭 **状态栏与导航栏控制**: 设置状态栏与虚拟导航栏显示或隐藏
- 📡 **GOIP状态读取与设置**: 获取与设置GOIP模块状态
- 💡 **LED灯带控制**: 控制主板LED灯带状态
- ⚡ **继电器控制**: 控制外接继电器通断
- 🌞 **屏幕亮度**: 获取与设置屏幕亮度
- 📸 **截屏功能**: 获取当前屏幕截图
- 🛠️ **静默升级**: 在后台执行系统升级任务
- ⏰ **定时开关机**: 自动在指定时间开启或关闭设备
- 💡 **屏幕控制**: 管理屏幕状态（开/关）
- 🕒 **系统时间配置**: 设置或同步系统时间

---


## 🧩 支持设备

| 厂家  | 型号       | 备注          |
|-----|----------|-------------|
| 视美泰 | RK3288   | 他家主板基本都能适配到 |
|     | RK3568A  |             |
|     | RK3568S  |             |
|     | RK3568XM |             |
|     | RK3566R  |             |
|     | RK3566X  |             |
|     | RK3568HV |             |
|     | RK3568SC |             |
|     | RK3576E  |             |
|     | RK8953   | 高通方案        |
| 亿晟  | RK3288   | 他家主板基本都能适配到 |
|     | RK3568   |  |
|     | RK3566   |  |
| 华壹  | RK3288   | 暂未支持后续添加    |
| 金海创 | RK3288   | 暂未支持后续添加    |
| 郎国  | RK3288   | 暂未支持后续添加    |
| 乐瞬  | RK3288   | 暂未支持后续添加    |
| 西沃  | RK3288   | 暂未支持后续添加    |
| 天波  | RK3288   | 暂未支持后续添加    |
| 灰度  | RK3288   | 暂未支持后续添加    |

---

## 📦 安装

在`pubspec.yaml`中添加依赖：

```yaml
dependencies:
  rk_control_lib: ^1.0.0
```

---

## 🚀 快速开始

导入插件：

```dart
import 'package:rk_control_lib/rk_control_lib.dart';
```

##  插件可自动判断主板类型

### ✅ **电源控制**: 关机,重启

```dart
await RkHardwareControl.shutdown(); // 关机
await RkHardwareControl.reboot(); // 重启
```

### 🧭 **状态栏与导航栏控制**: 设置状态栏与虚拟导航栏显示或隐藏

```dart
await _rkControlLibPlugin.showStatusBar(value);
```

### 📡 **GOIP状态读取与设置**: 获取与设置GOIP模块状态

```dart

```

### 💡 **LED灯带控制**: 控制主板LED灯带状态

```dart

```

### ⚡ **继电器控制**: 控制外接继电器通断

```dart

```

### 🌞 **屏幕亮度**: 获取与设置屏幕亮度

```dart

```

### 📸 **截屏功能**: 获取当前屏幕截图

```dart

```

### 🛠️ **静默升级**: 在后台执行系统升级任务

```dart

```

### ⏰ **定时开关机**: 自动在指定时间开启或关闭设备

```dart

```

### 💡 **屏幕控制**: 管理屏幕状态（开/关）

```dart
await _rkControlLibPlugin.sleepScreen();
await _rkControlLibPlugin.wakeScreen();
```

### 🕒 **系统时间配置**: 设置或同步系统时间

```dart
await _rkControlLibPlugin.setSystemTime("2024-05-20 12:00:00");
```


## 📄 许可证

本项目基于[Apache License 2.0](./LICENSE)许可证。

---

## 🙋 支持

如有问题、功能请求或贡献，请创建[Issue](https://github.com/SHINING-TECH/flutter_rk_control_lib/issues)或提交[Pull Request](https://github.com/SHINING-TECH/flutter_rk_control_lib/pulls)。
