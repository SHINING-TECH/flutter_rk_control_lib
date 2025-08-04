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
}
