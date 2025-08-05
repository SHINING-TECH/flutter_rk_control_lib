
import 'rk_control_lib_platform_interface.dart';

class RkControlLib {
  Future<String?> getPlatformVersion() {
    return RkControlLibPlatform.instance.getPlatformVersion();
  }

  Future<void> showStatusBar(bool visible) {
    return RkControlLibPlatform.instance.showStatusBar(visible);
  }

  Future<void> reboot() {
    return RkControlLibPlatform.instance.reboot();
  }

  Future<void> shutdown() {
    return RkControlLibPlatform.instance.shutdown();
  }

  Future<void> sleepScreen() {
    return RkControlLibPlatform.instance.sleepScreen();
  }

  Future<void> wakeScreen() {
    return RkControlLibPlatform.instance.wakeScreen();
  }

  Future<void> setSystemTime(String time) {
    return RkControlLibPlatform.instance.setSystemTime(time);
  }
}