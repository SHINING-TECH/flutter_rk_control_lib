package com.example.rk_control_lib

import android.content.Context
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import android.util.Log
import com.example.rk_control_lib.listener.BoardControlListener
import com.example.rk_control_lib.listener.SetSystemTimeListener

/** RkControlLibPlugin */
class RkControlLibPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel

  private lateinit var context: Context

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    Log.d("RkControlLib", "!!!!onAttachedToEngine!!!!")
    // 获取 context
    context = flutterPluginBinding.applicationContext
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "rk_control_lib")
    channel.setMethodCallHandler(this)
    BoardFeatureProxy.getInstance().setBoardControlListener(object :BoardControlListener{
        override fun boardFeatureLogInfo(tag: String, logMsg: String) {
            Log.d(tag, logMsg)
        }

    }).initImpl(context, "")
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when (call.method) {
        "getPlatformVersion" -> {
            result.success("Android \${android.os.Build.VERSION.RELEASE}")
        }
        "showStatusBar" -> {
            val visible = call.argument<Boolean>("visible") ?: false
            Log.d("RkControlLib", "showStatusBar called : ${visible}")
            BoardFeatureProxy.getInstance().setStatusBarVisible(visible)
            result.success(null)
        }
        "reboot" -> {
            Log.d("RkControlLib", "reboot called")
            BoardFeatureProxy.getInstance().restartDevice()
            result.success(null)
        }
        "shutdown" -> {
            Log.d("RkControlLib", "shutdown called")
            BoardFeatureProxy.getInstance().closeDevice()
            result.success(null)
        }
        "sleepScreen" -> {
            Log.d("RkControlLib", "sleepScreen called")
            BoardFeatureProxy.getInstance().closeScreen()
            result.success(null)
        }
        "wakeScreen" -> {
            Log.d("RkControlLib", "wakeScreen called")
            BoardFeatureProxy.getInstance().awakeDevice()
            result.success(null)
        }
        "setSystemTime" -> {
            val time = call.argument<String>("time") ?: ""
            Log.d("RkControlLib", "setSystemTime called with time: \$time")
            BoardFeatureProxy.getInstance().setSystemTime(time,object :SetSystemTimeListener{
                override fun setTimeSuccess() {

                }

                override fun setTimeError() {

                }

            })
            result.success(null)
        }
        else -> {
            result.notImplemented()
        }
    }
}

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}