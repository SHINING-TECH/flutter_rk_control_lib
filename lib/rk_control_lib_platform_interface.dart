import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'rk_control_lib_method_channel.dart';

abstract class RkControlLibPlatform extends PlatformInterface {
  /// Constructs a RkControlLibPlatform.
  RkControlLibPlatform() : super(token: _token);

  static final Object _token = Object();

  static RkControlLibPlatform _instance = MethodChannelRkControlLib();

  /// The default instance of [RkControlLibPlatform] to use.
  ///
  /// Defaults to [MethodChannelRkControlLib].
  static RkControlLibPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [RkControlLibPlatform] when
  /// they register themselves.
  static set instance(RkControlLibPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
