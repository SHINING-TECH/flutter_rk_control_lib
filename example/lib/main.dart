import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:rk_control_lib/rk_control_lib.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _rkControlLibPlugin = RkControlLib();
  bool _showStatusBarValue = false; // 添加Switch状态变量

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion =
          await _rkControlLibPlugin.getPlatformVersion() ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  // 更新方法以接受bool参数
  Future<void> _callShowStatusBar(bool value) async {
    try {
      await _rkControlLibPlugin.showStatusBar(value);
    } catch (e) {
      print('Error calling showStatusBar: \$e');
    }
  }

  Future<void> _callReboot() async {
    try {
      await _rkControlLibPlugin.reboot();
    } catch (e) {
      print('Error calling reboot: \$e');
    }
  }

  Future<void> _callShutdown() async {
    try {
      await _rkControlLibPlugin.shutdown();
    } catch (e) {
      print('Error calling shutdown: \$e');
    }
  }

  Future<void> _callSleepScreen() async {
    try {
      await _rkControlLibPlugin.sleepScreen();
    } catch (e) {
      print('Error calling sleepScreen: \$e');
    }
  }

  Future<void> _callWakeScreen() async {
    try {
      await _rkControlLibPlugin.wakeScreen();
    } catch (e) {
      print('Error calling wakeScreen: \$e');
    }
  }

  Future<void> _callSetSystemTime() async {
    try {
      await _rkControlLibPlugin.setSystemTime("2024-05-20 12:00:00");
    } catch (e) {
      print('Error calling setSystemTime: \$e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app', style: TextStyle(fontSize: 24)),
        ),
        body: SingleChildScrollView(
          child: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Text('Show Status Bar', style: TextStyle(fontSize: 18)),
                    Switch(
                      value: _showStatusBarValue,
                      onChanged: (value) {
                        setState(() {
                          _showStatusBarValue = value;
                        });
                        _callShowStatusBar(value);
                      },
                    ),
                  ],
                ),
                const SizedBox(height: 40),
                ElevatedButton(
                  onPressed: _callReboot,
                  child: const Text('Reboot'),
                  style: ElevatedButton.styleFrom(
                    minimumSize: Size(200, 60),
                    textStyle: TextStyle(fontSize: 18)
                  ),
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  onPressed: _callShutdown,
                  child: const Text('Shutdown'),
                  style: ElevatedButton.styleFrom(
                    minimumSize: Size(200, 60),
                    textStyle: TextStyle(fontSize: 18)
                  ),
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  onPressed: _callSleepScreen,
                  child: const Text('Sleep Screen'),
                  style: ElevatedButton.styleFrom(
                    minimumSize: Size(200, 60),
                    textStyle: TextStyle(fontSize: 18)
                  ),
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  onPressed: _callWakeScreen,
                  child: const Text('Wake Screen'),
                  style: ElevatedButton.styleFrom(
                    minimumSize: Size(200, 60),
                    textStyle: TextStyle(fontSize: 18)
                  ),
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  onPressed: _callSetSystemTime,
                  child: const Text('Set System Time'),
                  style: ElevatedButton.styleFrom(
                    minimumSize: Size(200, 60),
                    textStyle: TextStyle(fontSize: 18)
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}