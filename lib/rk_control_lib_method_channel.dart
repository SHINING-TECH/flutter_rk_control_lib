import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'rk_control_lib_platform_interface.dart';

/// An implementation of [RkControlLibPlatform] that uses method channels.
class MethodChannelRkControlLib extends RkControlLibPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('rk_control_lib');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<void> showStatusBar(bool visible) async {
    await methodChannel.invokeMethod('showStatusBar', {'visible': visible});
  }

  @override
  Future<void> reboot() async {
    await methodChannel.invokeMethod('reboot');
  }

  @override
  Future<void> shutdown() async {
    await methodChannel.invokeMethod('shutdown');
  }

  @override
  Future<void> sleepScreen() async {
    await methodChannel.invokeMethod('sleepScreen');
  }

  @override
  Future<void> wakeScreen() async {
    await methodChannel.invokeMethod('wakeScreen');
  }

  @override
  Future<void> setSystemTime(String time) async {
    await methodChannel.invokeMethod('setSystemTime', {'time': time});
  }
}