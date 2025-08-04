
import 'rk_control_lib_platform_interface.dart';

class RkControlLib {
  Future<String?> getPlatformVersion() {
    return RkControlLibPlatform.instance.getPlatformVersion();
  }
}
