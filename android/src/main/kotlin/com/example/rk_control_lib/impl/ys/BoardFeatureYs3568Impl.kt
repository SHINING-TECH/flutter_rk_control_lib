package com.example.rk_control_lib.impl.ys

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.cn.kdzn_p2.manager.boardfeature.base.BoardFeatureManager
import com.example.rk_control_lib.constant.TagConstant
import com.example.rk_control_lib.listener.BoardControlListener
import com.example.rk_control_lib.listener.SetSystemTimeListener
import com.example.rk_control_lib.listener.TakeShotListener
import com.example.rk_control_lib.util.ShellUtils
import com.example.rk_control_lib.util.Util
import com.ys.rkapi.GPIOManager
import com.ys.rkapi.MyManager
import startest.ys.com.poweronoff.PowerOnOffManager

/**
 *  author : vinda
 *  date : 2021/6/4 9:12
 *  description : 亿晟3568 主板功能实现类
 */
class BoardFeatureYs3568Impl(context: Context?, boardType: String?, boardControlListener: BoardControlListener?) : BoardFeatureManager() {
    var TAG = TagConstant.TAG_BOARD_FEATURE + "-Ys3568"
    var myManager: MyManager
    var gpioManager: GPIOManager? = null

    init {
        mContext = context
        mBoardType = boardType
        myManager = MyManager.getInstance(mContext)
        myManager?.bindAIDLService(mContext)
        mBoardControlListener = boardControlListener
    }

    override fun setStatusBarVisible(isShow: Boolean) {
        log(TAG, "setStatusBarVisible $isShow")
        if (isShow) {
            myManager?.hideNavBar(false)
            myManager?.setSlideShowNavBar(true)
            myManager?.setSlideShowNotificationBar(true)
        } else {
            myManager?.hideNavBar(true)
            myManager?.setSlideShowNavBar(false)
            myManager?.setSlideShowNotificationBar(false)
        }

    }

    override fun setPowerOffOn(offTime: String, OnTime: String, enable: Boolean) {
        try {
            val powerOnOffManager = PowerOnOffManager.getInstance(mContext)
            if (enable) {
                log(TAG, "PowerOnOffManager当前CPU名称: " + mBoardType + "设置的开机时间: " + OnTime + "   设置的关机时间: " + offTime + "  是否开关: " + enable)
                val OpenTime: Array<String> = OnTime.split("-").toTypedArray()
                val CloseTime: Array<String> = offTime.split("-").toTypedArray()
                val timeonArray = intArrayOf(Integer.valueOf(OpenTime[0]), Integer.valueOf(OpenTime[1]), Integer.valueOf(OpenTime[2]), Integer.valueOf(OpenTime[3]), Integer.valueOf(OpenTime[4]))
                val timeoffArray = intArrayOf(Integer.valueOf(CloseTime[0]), Integer.valueOf(CloseTime[1]), Integer.valueOf(CloseTime[2]), Integer.valueOf(CloseTime[3]), Integer.valueOf(CloseTime[4]))
                powerOnOffManager.setPowerOnOff(timeonArray, timeoffArray)
                log(TAG, "当前CPU名称: $mBoardType  设置定时开关机")
            } else {
                powerOnOffManager.clearPowerOnOffTime()
                log(TAG, "当前CPU名称: $mBoardType  清除定时开关机")
            }
        } catch (e: Exception) {
            log(TAG, "当前CPU名称: $mBoardType  设置定时开关机异常: $e")
        }
    }

    override fun closeDevice() {
        val intent = Intent("android.intent.action.shutdown")
        mContext?.sendBroadcast(intent)
    }

    override fun restartDevice() {
        MyManager.getInstance(mContext).reboot()
    }

    override fun closeScreen() {
        log(TAG,"亮屏:${myManager?.turnOnBackLight()}")
    }

    override fun awakeDevice() {
       log(TAG,"息屏:${myManager?.turnOnBackLight()}")
    }

    override fun setScreenBright(brightValue: Float) {
        val maxLightValue = 255
        val screenLight: Int = (maxLightValue * brightValue).toInt()
        myManager?.changeScreenLight(screenLight)
    }

    override fun getScreenBright(): Float {
        return getScreenBrightByAndroidApi()
    }

    override fun setSystemTime(time: String, setSystemTimeListener: SetSystemTimeListener) {
        if (time.contains("2020") && Util.getNowYearMonthDayStrTime().contains("2020")) {
            log(TAG, "设备当前时间已经是2020年,不再重复设置")
            setSystemTimeListener.setTimeSuccess()
            return
        }
        try {
            //如果是yyyyMMdd.HHmmss格式需要转换成"yyyy-MM-dd HH:mm:ss"
            var timeResult = time
            if (time.contains(".")) {
                log(TAG, "TimeSet 需要转换时间格式")
                //判断出时间是2020-07-20 00:00:00格式
                var timeTemp = Util.dateToStamp(time)
                //时间戳转换为yyyyMMdd.HHmmss格式时间
                timeResult = Util.getTime(timeTemp)
                log(TAG, "TimeSet 转换后格式${timeResult}")
            }
            log(TAG, "TimeSet setSystemTime:${timeResult}")
            thisModifyTime = timeResult
            val mDate = timeResult.split(" ").toTypedArray()[0].split("-").toTypedArray()
            val mTime = timeResult.split(" ").toTypedArray()[1].split(":").toTypedArray() //恢复之前的时间
            var second = 0
            if (mTime.size > 2) {
                second = Integer.valueOf(mTime[2])
            }
            log(TAG, "TimeSet 年月日  " + mDate[0] + "-" + mDate[1] + "-" + mDate[2])
            log(TAG, "TimeSet 时分秒 " + mTime[0] + ":" + mTime[1] + ":" + second)
            MyManager.getInstance(mContext).switchAutoTime(false)
            MyManager.getInstance(mContext).setTime(
                Integer.valueOf(mDate[0]),
                Integer.valueOf(mDate[1]),
                Integer.valueOf(mDate[2]),
                Integer.valueOf(mTime[0]),
                Integer.valueOf(mTime[1]),
                second
            )
            startCheckModifyTimeSuccess(setSystemTimeListener, timeResult.split(" ").get(0))
        } catch (e: java.lang.Exception) {
            log(TAG, "修改设备时间失败 ${e.message}")
            setSystemTimeListener.setTimeError()
        }
    }

    override fun reSetSystemTime() {
        if (!thisModifyTime.isNullOrBlank()) {
            val mDate = thisModifyTime.split(" ").toTypedArray()[0].split("-").toTypedArray()
            val mTime = thisModifyTime.split(" ").toTypedArray()[1].split(":").toTypedArray() //恢复之前的时间
            var second = 0
            if (mTime.size > 2) {
                second = Integer.valueOf(mTime[2])
            }
            log(TAG, "修改设备时间失败,继续修改 年月日  " + mDate[0] + "-" + mDate[1] + "-" + mDate[2] + " 时分秒" + mTime[0] + ":" + mTime[1] + ":" + second)
            MyManager.getInstance(mContext).setTime(
                Integer.valueOf(mDate[0]),
                Integer.valueOf(mDate[1]),
                Integer.valueOf(mDate[2]),
                Integer.valueOf(mTime[0]),
                Integer.valueOf(mTime[1]),
                second
            )
        }
    }


    override fun writeWag(byteArray: ByteArray) {
        log(TAG,"Wag 控制-空实现")
    }

    override fun writeGpio(id: Int, value: Int) {
        log(TAG,"Gpio 控制-空实现")
    }

    override fun readGpio(id: Int,defaultReturnValue:Int):Int {
        return defaultReturnValue
    }

    override fun ledControl(value: Int) {
        log(TAG,"Led 控制-空实现")
    }

    override fun colorLedControl(color: String, value: Int) {
        log(TAG,"三色灯控制 color:${color} value:${value}")
        gpioManager ?: let {
            gpioManager = myManager?.getGpioManager()
        }
        if (color.equals("green", ignoreCase = true) || color.equals("LED_GREEN", ignoreCase = true)) {
            if (value == 1) {
                gpioManager?.pullUpGreenLight()
            } else {
                gpioManager?.pullDownGreenLight()
            }
        } else if (color.equals("red", ignoreCase = true) || color.equals("LED_RED", ignoreCase = true)) {
            if (value == 1) {
                gpioManager?.pullUpRedLight()
            } else {
                gpioManager?.pullDownRedLight()
            }
        } else {
            if (value == 1) {
                gpioManager?.pullUpWhiteLight()
            } else {
                gpioManager?.pullDownWhiteLight()
            }
        }
    }

    override fun relayControl(isOpen: Boolean) {
        log(TAG,"继电器控制 isOpen:${isOpen}")
        gpioManager ?: let {
            gpioManager = myManager?.getGpioManager()
        }
        if (isOpen) {
            gpioManager?.pullUpRelay()
        } else {
            gpioManager?.pullDownRelay()
        }

    }

    override fun installApk(apkPath: String, packageName: String, apkFileName: String, savePath: String, provider: String) {
        log(TAG,"静默安装apk:${apkPath}")
        MyManager.getInstance(mContext).silentInstallApk(apkPath, true)
    }

    override fun screenShot(activity: Activity, filePath: String) {
        MyManager.getInstance(activity).takeScreenshot(filePath)
    }

    override fun switchAdb(isOpen: Boolean) {
        log(TAG, "设置adb开关 ${isOpen}")
        MyManager.getInstance(mContext).setADBOpen(isOpen)
    }

    override fun startAppProcess(packageName: String): Boolean {
        log(TAG, "拉起程序:${packageName}")
        return false
    }

    override fun isScreenBright(): Boolean {
        log(TAG, "屏幕是否开启:${myManager!!.isBacklightOn}")
        return myManager!!.isBacklightOn
    }

    override fun otaUpgrade(otaPath: String):Boolean {
        return false
    }

    override fun takeScreenShot(): Bitmap? {
        return null
    }

    override fun setLiveMonitorWithBoardApi(packageName: String, restartTime: Long, enableBroadCast: Boolean): Int? {
        return -1
    }


    override fun takeScreenShot(path: String, name: String,takeShotListener: TakeShotListener) {
        val absolutePath = path + name
        log(TAG,"截屏 absolutePath:${absolutePath}")
        if (ShellUtils.hasRootPrivilege()){
            log(TAG,"截屏 use adb")
            takeShotByAdb(TAG,absolutePath,takeShotListener)
        }else{
            log(TAG,"截屏 use api")
            val filePath = path + name
            val apiTakeResult = MyManager.getInstance(mContext).takeScreenshot(filePath)
            if (apiTakeResult){
                log(TAG,"截屏成功")
                takeShotListener.takeShotListener(true,absolutePath)
            }else{
                log(TAG,"截屏异常")
                takeShotListener.takeShotListener(false,absolutePath)
            }
        }
    }


    override fun getTotalRam(): String {
        return myManager.runningMemory
    }

    override fun getAvalibleRam(): String {
        return ""
    }

    override fun getTotalSd(): String {
        return myManager.internalStorageMemory
    }

    override fun getAvalibleSd(): String {
        return ""
    }

    override fun rotateScreen(degree: Int) {
        myManager.rotateScreen(mContext,degree.toString())
    }

    override fun setEthMode(mode:String,ip:String,gateWay:String,mask:String,dns1:String,dns2:String) {
        if (mode == "static"){
            myManager.setStaticEthIPAddress(ip,gateWay,mask, dns1, dns2)
        } else {
            myManager.setDhcpIpAddress(mContext)
        }
    }

    override fun getEthMode(): String {
        return myManager.ethMode
    }

    override fun setEthStatus(open:Boolean) {
        myManager.ethEnabled(open)
    }

    override fun getEthStatus(): Boolean {
        return myManager.ethStatus
    }
    override fun getEthIp(): String {
        if (myManager.ethMode.equals("StaticIp")){
            return myManager.staticEthIPAddress
        } else {
            return myManager.dhcpIpAddress
        }
    }
    override fun getEthNetGateWay(): String {
        return myManager.gateway
    }

    override fun getEthNetMask(): String {
        return myManager.netMask
    }

    override fun getEthDns1(): String {
        return myManager.ethDns1
    }

    override fun getEthDns2(): String {
        return myManager.ethDns2
    }

}