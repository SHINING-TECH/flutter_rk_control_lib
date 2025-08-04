import 'package:flutter_test/flutter_test.dart';
import 'package:rk_control_lib/rk_control_lib.dart';
import 'package:rk_control_lib/rk_control_lib_platform_interface.dart';
import 'package:rk_control_lib/rk_control_lib_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockRkControlLibPlatform
    with MockPlatformInterfaceMixin
    implements RkControlLibPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final RkControlLibPlatform initialPlatform = RkControlLibPlatform.instance;

  test('$MethodChannelRkControlLib is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelRkControlLib>());
  });

  test('getPlatformVersion', () async {
    RkControlLib rkControlLibPlugin = RkControlLib();
    MockRkControlLibPlatform fakePlatform = MockRkControlLibPlatform();
    RkControlLibPlatform.instance = fakePlatform;

    expect(await rkControlLibPlugin.getPlatformVersion(), '42');
  });
}
