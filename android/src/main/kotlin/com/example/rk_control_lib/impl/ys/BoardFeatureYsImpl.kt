package com.example.rk_control_lib.impl.ys

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.cn.kdzn_p2.manager.boardfeature.base.BoardFeatureManager
import com.example.rk_control_lib.constant.TagConstant
import com.example.rk_control_lib.listener.BoardControlListener
import com.example.rk_control_lib.listener.SetSystemTimeListener
import com.example.rk_control_lib.listener.TakeShotListener
import com.example.rk_control_lib.util.DateUtils
import com.example.rk_control_lib.util.ShellUtils
import com.example.rk_control_lib.util.Util
import com.ys.rkapi.MyManager
import startest.ys.com.poweronoff.PowerOnOffManager

/**
 *  author : vinda
 *  date : 2021/6/4 9:12
 *  description : 亿晟 主板功能实现类
 */
class BoardFeatureYsImpl(context: Context?, boardType: String?, boardControlListener: BoardControlListener?) : BoardFeatureManager() {
    var TAG = TagConstant.TAG_BOARD_FEATURE + "-Ys"
    var myManager: MyManager

    init {
        mContext = context
        mBoardType = boardType
        myManager = MyManager.getInstance(mContext)
        mBoardControlListener = boardControlListener
        MyManager.getInstance(mContext).bindAIDLService(mContext)
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
                powerOnOffManager.setPowerOnOff(timeonArray, timeoffArray) //校验当前开关机时间是否设置成功
                var msg = Message()
                msg.what = MSG_CHECK_POWER_SET
                msg.obj = Bundle().also {
                    it.putString("offTime", offTime)
                    it.putString("OnTime", OnTime)
                }
                powerOnoffCheckHandler.sendMessageDelayed(msg, 2000)
                log(TAG, "当前CPU名称: $mBoardType  设置定时开关机结束")
            } else {
                //有个现场发现取消定时开关机无效，所以采用设置时间方式实现
                //powerOnOffManager.clearPowerOnOffTime()
                val timeonArray = intArrayOf(2050,1, 1, 0, 0)
                val timeoffArray = intArrayOf(2050,1, 2, 0, 0)
                powerOnOffManager.setPowerOnOff(timeonArray, timeoffArray)
                log(TAG, "当前CPU名称: $mBoardType  清除定时开关机")
            }
        } catch (e: Exception) {
            log(TAG, "当前CPU名称: $mBoardType  设置定时开关机异常: $e")
        }
    }

    //检查定时开关机是否设置成功
    val MSG_CHECK_POWER_SET = 0x01
    var resetPowerOnOffTime = 0
    val powerOnoffCheckHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_CHECK_POWER_SET -> {
                    val bundle: Bundle = msg.obj as Bundle
                    val openTimeStr = bundle.getString("OnTime")
                    val offTimeStr = bundle.getString("offTime") //转换时间戳进行比对
                    val setOpenTimeTemp = DateUtils.getDateStringToDate(openTimeStr.toString(), "yyyy-MM-dd-HH-mm")
                    val setOffTimeTemp = DateUtils.getDateStringToDate(offTimeStr.toString(), "yyyy-MM-dd-HH-mm")

                    //获取当前系统内设定的开机时间
                    var nowDevicePowerOnTimeStr = PowerOnOffManager.getInstance(mContext).powerOnTime //获取当前系统内设定的关机时间
                    var nowDevicePowerOffTimeStr = PowerOnOffManager.getInstance(mContext).powerOffTime //转换时间戳进行比对
                    var nowDevicePowerOnTimeTemp = DateUtils.getDateStringToDate(nowDevicePowerOnTimeStr, "yyyyMMddHHmm")
                    var nowDevicePowerOffTimeTemp = DateUtils.getDateStringToDate(nowDevicePowerOffTimeStr, "yyyyMMddHHmm")

                    log(TAG,
                        "当前CPU名称: $mBoardType  " + "\n设置关机时间:${offTimeStr} 时间戳:${setOffTimeTemp}" + "\n设置开机时间:${openTimeStr} 时间戳:${setOpenTimeTemp}" + "\n系统关机时间:${nowDevicePowerOffTimeStr} 时间戳:${nowDevicePowerOffTimeTemp}" + "\n系统开机时间:${nowDevicePowerOnTimeStr} 时间戳:${nowDevicePowerOnTimeTemp}")
                    if (setOffTimeTemp == nowDevicePowerOffTimeTemp && setOpenTimeTemp == nowDevicePowerOnTimeTemp) {
                        log(TAG, "设定成功!!!!!!!")
                        resetPowerOnOffTime = 0
                    } else {
                        if (resetPowerOnOffTime < 5) {
                            resetPowerOnOffTime++
                            log(TAG, "设定失败重试-$resetPowerOnOffTime")
                            if (offTimeStr != null) {
                                if (openTimeStr != null) {
                                    setPowerOffOn(offTimeStr, openTimeStr, true)
                                }
                            }
                        } else {
                            log(TAG, "达到重试最大值!")
                            resetPowerOnOffTime = 0
                        }
                    }
                }
            }
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
        log(TAG, "息屏")
        MyManager.getInstance(mContext).turnOffBackLight()
    }

    override fun awakeDevice() {
        log(TAG, "亮屏")
        MyManager.getInstance(mContext).turnOnBackLight()
    }

    override fun setScreenBright(brightValue: Float) {
        log(TAG, "设置屏幕亮度:${brightValue}")
        object : Thread() {
            override fun run() {
                super.run()
                if (brightValue <= 1) {
                    try {
                        val maxLightValue = 100
                        val screenLight: Int = (maxLightValue * brightValue).toInt()
                        myManager.changeScreenLight(screenLight)
                    } catch (e: java.lang.Exception) {

                    }
                }
            }
        }.start()
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
        try { //如果是yyyyMMdd.HHmmss格式需要转换成"yyyy-MM-dd HH:mm:ss"
            var timeResult = time
            if (time.contains(".")) {
                log(TAG, "TimeSet 需要转换时间格式") //判断出时间是2020-07-20 00:00:00格式
                var timeTemp = Util.dateToStamp(time) //时间戳转换为yyyyMMdd.HHmmss格式时间
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
            MyManager.getInstance(mContext).setTime(Integer.valueOf(mDate[0]), Integer.valueOf(mDate[1]), Integer.valueOf(mDate[2]), Integer.valueOf(mTime[0]), Integer.valueOf(mTime[1]), second)
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
            MyManager.getInstance(mContext).setTime(Integer.valueOf(mDate[0]), Integer.valueOf(mDate[1]), Integer.valueOf(mDate[2]), Integer.valueOf(mTime[0]), Integer.valueOf(mTime[1]), second)
        }
    }


    override fun writeWag(byteArray: ByteArray) {
        log(TAG, "Wag控制 空实现")
    }

    override fun writeGpio(id: Int, value: Int) {
        log(TAG, "Gpio控制 空实现")
    }

    override fun readGpio(id: Int, defaultReturnValue: Int): Int {
        return defaultReturnValue
    }


    override fun ledControl(value: Int) {
        log(TAG, "Led灯控制 空实现")
    }

    override fun colorLedControl(color: String, value: Int) {
        log(TAG, "三色灯控制 color:${color} value:${value}")
        //RK3288Util.getInstance(mContext).lightControl(color, value)
    }

    override fun relayControl(isOpen: Boolean) {
        log(TAG, "继电器控制:${isOpen}")
        if (isOpen) {
            //RK3288Util.getInstance(mContext).openRelayPower()
        } else {
            //RK3288Util.getInstance(mContext).closeRelayPower()
        }
    }

    override fun installApk(apkPath: String, packageName: String, apkFileName: String, savePath: String, provider: String) { //        if (Build.MODEL.equals("rk3568_r")) {
        //            log(TAG, "YS3568_r设备升级:${apkPath}")
        //            MyManager.getInstance(mContext).silentInstallApk(apkPath, true)
        //        } else {
        //            log(TAG, "YS设备升级:${apkPath}")
        //            installUseSystemApi(apkPath, packageName, apkFileName, savePath, provider)
        //            startInstallApk(packageName)
        //        }
        //20230915 观察到部分YS设备，使用系统自带api升级无效，照道理用这个sdk api静默升级即可，先这样改，发现问题再改
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
        log(TAG, "屏幕是否开启:${MyManager.getInstance(mContext).isBacklightOn}")
        return MyManager.getInstance(mContext).isBacklightOn
    }

    override fun otaUpgrade(otaPath: String): Boolean {
        return false
    }

    override fun takeScreenShot(): Bitmap? {
        return null
    }

    override fun setLiveMonitorWithBoardApi(packageName: String, restartTime: Long, enableBroadCast: Boolean): Int? {
        return -1
    }

    override fun takeScreenShot(path: String, name: String, takeShotListener: TakeShotListener) {
        val absolutePath = path + name
        log(TAG, "截屏 absolutePath:${absolutePath}")
        if (ShellUtils.hasRootPrivilege()) {
            log(TAG, "截屏 use adb")
            takeShotByAdb(TAG, absolutePath, takeShotListener)
        } else {
            val apiTakeResult = MyManager.getInstance(mContext).takeScreenshot(absolutePath)
            if (apiTakeResult) {
                log(TAG, "截屏成功")
                takeShotListener.takeShotListener(true, absolutePath)
            } else {
                log(TAG, "截屏异常")
                takeShotListener.takeShotListener(false, absolutePath)
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