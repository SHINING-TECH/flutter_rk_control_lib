package com.cn.kdzn_p2.manager.boardfeature.base

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.View
import com.example.rk_control_lib.constant.TagConstant
import com.example.rk_control_lib.listener.BoardControlListener
import com.example.rk_control_lib.listener.SetSystemTimeListener
import com.example.rk_control_lib.listener.TakeShotListener
import com.example.rk_control_lib.util.ShellUtils
import com.example.rk_control_lib.util.Util
import java.io.File
import java.io.FileOutputStream


/**
 *  author : vinda
 *  date : 2021/6/4 8:29
 *  description :主板特定功能基类
 */
abstract class BoardFeatureManager {
	var mContext: Context? = null
	var mBoardType: String? = ""
	var mBoardControlListener: BoardControlListener? = null


	private val MSG_CHECK_TIME = 0x01 //校验时间是否修改成功

	private val startCheckTimeInterval = 500L //第一次校验时间是否修改成功间隔时间
	private val checkTimeInterval = 2000L //校验时间是否修改成功间隔时间

	var thisModifyTime = "" //最近一次修改的时间，如果修改失败，使用这个记录的时间继续进行修改

	private var mLastModifyTime = "" //最近一次修改的时间,只保留年月日,用于校验是否修改成功使用

	private var mSetSystemTimeListener: SetSystemTimeListener? = null

	private var modifyTimeFailedCount = 0 //未生效次数大于20认为修改时间失败

	private val mHandler: Handler = object : Handler() {
		override fun dispatchMessage(msg: Message) {
			super.dispatchMessage(msg)
			if (msg.what == MSG_CHECK_TIME) {
				val nowTimeYMD = Util.getNowYearMonthDayStrTime()
				if (mLastModifyTime.equals("2020-07-20")) {
					log(TagConstant.TAG_BOARD_FEATURE, "init time check ,now time:$nowTimeYMD")
				} else {
					log(TagConstant.TAG_BOARD_FEATURE, "this modify time:${mLastModifyTime} now time:$nowTimeYMD")
				}
				var mLastModifyTimetem = Util.dateToStampYMD(mLastModifyTime)
				var nowTimeYMDTimetem = Util.dateToStampYMD(nowTimeYMD)
				log(TagConstant.TAG_BOARD_FEATURE, "this reset time:${mLastModifyTimetem} now dev time:$nowTimeYMDTimetem")
				if (mLastModifyTimetem == nowTimeYMDTimetem) {
					log(TagConstant.TAG_BOARD_FEATURE, "time reset success")
					mSetSystemTimeListener?.setTimeSuccess()
				} else {
					log(TagConstant.TAG_BOARD_FEATURE, "reset time failed,go on & wait")
					modifyTimeFailedCount++
					if (modifyTimeFailedCount < 20) {
						reSetSystemTime()
						sendEmptyMessageDelayed(MSG_CHECK_TIME, checkTimeInterval)
					} else {
						log(TagConstant.TAG_BOARD_FEATURE, "time reset failed. modifyTimeFailedCount:${modifyTimeFailedCount}")
						mSetSystemTimeListener?.setTimeError()
					}
				}
			}
		}
	}

	//开始校验当前设备时间是否修改成功
	fun startCheckModifyTimeSuccess(setSystemTimeListener: SetSystemTimeListener, lastModifyTime: String) {
		log(TagConstant.TAG_BOARD_FEATURE, "目标时间:${lastModifyTime}")
		modifyTimeFailedCount = 0
		mSetSystemTimeListener = setSystemTimeListener
		mLastModifyTime = lastModifyTime
		if (mHandler.hasMessages(MSG_CHECK_TIME)) {
			log(TagConstant.TAG_BOARD_FEATURE, "移除 MSG_CHECK_TIME")
			mHandler.removeMessages(MSG_CHECK_TIME)
		}
		log(TagConstant.TAG_BOARD_FEATURE, "开始检验时间修改结果")
		mHandler.sendEmptyMessageDelayed(MSG_CHECK_TIME, startCheckTimeInterval)
	}

	/**
	 * 设置状态栏 可见&不可见
	 * @param isShow
	 */
	abstract fun setStatusBarVisible(isShow: Boolean)

	/**
	 *设置定时开关机
	 */
	abstract fun setPowerOffOn(offTime: String, OnTime: String, enable: Boolean)

	/**
	 *关闭机器
	 */
	abstract fun closeDevice()

	/**
	 *重启机器
	 */
	abstract fun restartDevice()

	/**
	 *熄灭屏幕
	 */
	abstract fun closeScreen()

	/**
	 *唤醒屏幕
	 */
	abstract fun awakeDevice()


	/**
	 *设置屏幕亮度
	 * 0-1
	 */
	abstract fun setScreenBright(brightValue: Float)

	fun setScreenBrightByShell(brightValue: Float){
		log(TagConstant.TAG_BOARD_FEATURE, "设置屏幕亮度 by shell:${brightValue}")
		if (brightValue <= 1) {
			val maxLightValue = 255
			val screenLight: Int = (maxLightValue * brightValue).toInt()
			val cmd = "settings put system screen_brightness ${screenLight}"
			Log.d(TagConstant.TAG_BOARD_FEATURE, "执行设置屏幕亮度:" + cmd)
			val mCommandResult: ShellUtils.CommandResult = ShellUtils.execCmd(cmd, true)
			Log.d(TagConstant.TAG_BOARD_FEATURE, "执行结果 result:${mCommandResult.result} successMsg:${mCommandResult.successMsg} errorMsg:${mCommandResult.errorMsg}")

		}
	}

	/**
	 * 获取屏幕亮度
	 * 返回 0-1
	 */
	abstract fun getScreenBright():Float

	fun getScreenBrightByAndroidApi():Float{
		try {
			val nowBright =Settings.System.getInt(mContext?.contentResolver, Settings.System.SCREEN_BRIGHTNESS).toFloat()
			val floatValue = nowBright / 255f // 将输入值除以255，得到范围在0到1之间的浮点数
			return  String.format("%.1f", floatValue).toFloat() // 保留一位小数点后的浮点数值
		} catch (e: Settings.SettingNotFoundException) {
			e.printStackTrace()
		}
		return 0.0f
	}

	/**
	 *修改系统时间
	 */
	abstract fun setSystemTime(time: String, setSystemTimeListener: SetSystemTimeListener)

	/**
	 *修改系统时间失败,重复修改系统时间
	 */
	abstract fun reSetSystemTime()

	/**
	 *写韦根信号
	 */
	abstract fun writeWag(byteArray: ByteArray)

	/**
	 *Gpio控制-写
	 */
	abstract fun writeGpio(id: Int, value: Int)

	/**
	 * Gpio控制-读
	 * @param id IO编号
	 * @param defaultCLoseValue 默认返回的值
	 */
	abstract fun readGpio(id: Int,defaultReturnValue:Int):Int

	/**
	 *补光灯控制
	 */
	abstract fun ledControl(value: Int)

	/**
	 *彩色灯带控制
	 */
	abstract fun colorLedControl(color: String, value: Int)

	/**
	 *继电器控制
	 */
	abstract fun relayControl(isOpen: Boolean)

	/**
	 * 打开或关闭设备adb
	 */
	abstract fun switchAdb(isOpen: Boolean)

	/**
	 * 启动其它app进程
	 */
	abstract fun startAppProcess(packageName: String): Boolean

	/**
	 * ota升级系统
	 */
	abstract fun otaUpgrade(otaPath: String): Boolean

	/**
	 *静默安装
	 * @param apkPath 软件绝对路径
	 * @param packageName 包名
	 * @param apkFileName apk文件名字
	 * @param savePath apk文件保存在哪个文件夹下面
	 */
	abstract fun installApk(apkPath: String, packageName: String, apkFileName: String, savePath: String, provider: String)

	/**
	 * 截取屏幕
	 * @return 本地图片路径
	 */
	abstract fun takeScreenShot(path: String, name: String, takeShotListener: TakeShotListener)

	/**
	 * 截取屏幕
	 * @return 截屏Bitmap
	 */
	abstract fun takeScreenShot(): Bitmap?

	abstract fun setLiveMonitorWithBoardApi(packageName:String,restartTime:Long,enableBroadCast:Boolean):Int?

	/**
	 *  普通系统自带安装升级方式
	 */
	fun installUseSystemApi(apkPath: String, packageName: String, apkFileName: String, savePath: String, provider: String) {
		var file = File(apkPath)
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
			log(TagConstant.TAG_BOARD_FEATURE, "通过Intent安装APK文件")
			val intent = Intent(Intent.ACTION_VIEW).also {
				it.setDataAndType(Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive")
				it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
			}
			mContext?.startActivity(intent)
		} else {
//			log(TagConstant.TAG_BOARD_FEATURE, "installapk by android api:${file.path}  provider:${provider}")
//			val intent = Intent(Intent.ACTION_VIEW)
//			val apkUri: Uri = FileProvider.getUriForFile(mContext!!, provider, file)
//			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//			intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
//			intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//			mContext?.startActivity(intent)
		}
	}

	/**
	 * 升级之后启动
	 */
	var mPackageName = ""
	fun startInstallApk(packageName: String) {
		mPackageName = packageName
		mHandler.postDelayed(runnable, 10000)
	}

	//之前的旧逻辑存疑,先留着
	var runnable = Runnable {
		if (mPackageName.equals("com.kade.watchdog")) {
			if (!IsApp("com.kade.watchdog")) {
				var iintent = mContext!!.packageManager.getLaunchIntentForPackage("com.kade.watchdog")
				iintent?.let {
					log(TagConstant.TAG_BOARD_FEATURE, "安装新版本")
					it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
					mContext?.startActivity(it)
				} ?: let {
					log(TagConstant.TAG_BOARD_FEATURE, "iintent ==null 安装失败")
				}
			}
		} else if (mPackageName.equals("com.kdzn.qqth")) {
			var iintent = mContext!!.packageManager.getLaunchIntentForPackage("com.kdzn.qqth")
			iintent?.let {
				log(TagConstant.TAG_BOARD_FEATURE, "安装新版本")
				it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
				mContext?.startActivity(it)
			} ?: let {
				log(TagConstant.TAG_BOARD_FEATURE, "iintent ==null 安装失败")
			}
		} else {
			if (!IsApp("com.cn.kdzn_p2") || "TPS530".equals(Build.MODEL.toString(), ignoreCase = true)) {
				var iintent = mContext!!.packageManager.getLaunchIntentForPackage("com.cn.kdzn_p2")
				iintent?.let {
					log(TagConstant.TAG_BOARD_FEATURE, "安装新版本")
					it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
					mContext?.startActivity(it)
				} ?: let {
					log(TagConstant.TAG_BOARD_FEATURE, "iintent ==null 安装失败")
				}
			}
		}
	}

	open fun IsApp(strAppName: String): Boolean {
		val manager = mContext!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
		var isSafe = false
		val infos = manager.runningAppProcesses
		for (info in infos) {
			if (strAppName == info.processName) {
				isSafe = true
				continue
			}
		}
		return isSafe
	}

	open fun screenShot(activity: Activity, filePath: String) {
		// 获取屏幕
		val dView: View = activity.getWindow().getDecorView()
		dView.setDrawingCacheEnabled(true)
		dView.buildDrawingCache()
		val bmp: Bitmap = dView.getDrawingCache()
		if (bmp != null) {
			try {
				// 获取内置SD卡路径
				val sdCardPath: String = Environment.getExternalStorageDirectory().getPath()
				// 图片文件路径
				val file = File(filePath)
				val os = FileOutputStream(file)
				bmp.compress(Bitmap.CompressFormat.PNG, 100, os)
				os.flush()
				os.close()
			} catch (e: Exception) {
				log(TagConstant.TAG_BOARD_FEATURE, "获取截图失败:" + e.message)
			}
		}
	}

	/**
	 * 判断当前屏幕是否是开启状态
	 */
	abstract fun isScreenBright(): Boolean?

	fun takeShotByAdb(TAG: String, absolutePath: String, takeShotListener: TakeShotListener) {
		object : Thread() {
			override fun run() {
				//需要系统签名才可执行以下程序
				try {
					val cmd = "screencap -p ${absolutePath}"
					Log.d(TAG, "截屏执行指令:" + cmd)
					val mCommandResult: ShellUtils.CommandResult = ShellUtils.execCmd(cmd, true)
					Log.d(TAG, "执行结果 result:${mCommandResult.result} successMsg:${mCommandResult.successMsg} errorMsg:${mCommandResult.errorMsg}")
					if (mCommandResult.result == 0) {
						takeShotListener.takeShotListener(true, absolutePath)
					} else {
						takeShotListener.takeShotListener(false, absolutePath)
					}
				} catch (e: java.lang.Exception) {
					Log.d(TAG, "exception:" + e.message)
					takeShotListener.takeShotListener(false, absolutePath)
				}
			}
		}.start()
	}

	//获取总RAM
	abstract fun getTotalRam():String

	//获取可用RAM
	abstract fun getAvalibleRam():String

	//获取总磁盘
	abstract fun getTotalSd():String

	//获取可用磁盘
	abstract fun getAvalibleSd():String

	//旋转屏幕
	abstract fun rotateScreen(degree:Int)

	//设置以太网连接模式 静态或者动态
	abstract fun setEthMode(mode:String,ip:String,gateWay:String,mask:String,dns1:String,dns2:String)

	//获取以太网连接模式 静态或者动态
	abstract fun getEthMode():String

	//设置以太网开关
	abstract fun setEthStatus(open:Boolean)
	//获取以太网开关是否打开
	abstract fun getEthStatus():Boolean

	//获取以太IP
	abstract fun getEthIp():String
	//获取以太网网关
	abstract fun getEthNetGateWay():String
	//获取以太网开关子网掩码
	abstract fun getEthNetMask():String
	//获取以太网DNS1
	abstract fun getEthDns1():String
	//获取以太网DNS2
	abstract fun getEthDns2():String

//	abstract fun enableWatchDog(enable: Boolean):Int
//
//	abstract fun feedWatchDog(enable: Boolean):Int
	/**
	 * 日志打印
	 */
	fun log(tag: String, msg: String) {
		mBoardControlListener?.let {
			it.boardFeatureLogInfo(tag, msg)
		}
	}

}