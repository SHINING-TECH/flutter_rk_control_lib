package com.example.rk_control_lib

import android.content.Context
import android.graphics.Bitmap
import com.cn.kdzn_p2.manager.boardfeature.base.BoardFeatureManager
import com.example.rk_control_lib.constant.BoardTypeConstant
import com.example.rk_control_lib.constant.TagConstant
import com.example.rk_control_lib.impl.smdt.BoardFeatureSmdt3568Impl
import com.example.rk_control_lib.impl.ys.BoardFeatureYs3568Impl
import com.example.rk_control_lib.impl.ys.BoardFeatureYsImpl
import com.example.rk_control_lib.listener.BoardControlListener
import com.example.rk_control_lib.listener.SetSystemTimeListener
import com.example.rk_control_lib.listener.TakeShotListener
import com.example.rk_control_lib.util.BoardTypeCheckUtil
import com.example.rk_control_lib.util.ShellUtils

/**
 *  author : vinda
 *  date : 2021/6/4 8:30
 *  description :主板特定功能代理类
 */
class BoardFeatureProxy : BoardFeatureManager() {
    private var boardFeatureManager: BoardFeatureManager? = null


    companion object {
        @Volatile
        private var instance: BoardFeatureProxy? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: BoardFeatureProxy().also { instance = it }
            }
    }

    /**
     * 初始化 通过代码判断主板类型进行初始化
     */
    fun initImpl(context: Context) {
        initImpl(context, "")
    }

    /**
     * 初始化主板特定功能实现类
     * @param context 上下文
     * @param boardType 主板类型 如果传递空串则完全通过代码进行判断
     *
     */
    fun initImpl(context: Context, boardType: String): BoardFeatureProxy {
        if (boardType.isNullOrBlank()) {
            log(TagConstant.TAG_BOARD_FEATURE, "init -> use code check")
            mBoardType = BoardTypeCheckUtil.instance.checkBoardType(context, mBoardControlListener)
        } else {
            log(TagConstant.TAG_BOARD_FEATURE, "init -> use input param")
            mBoardType = boardType
        }
        mContext = context
        log(TagConstant.TAG_BOARD_FEATURE, "当前主板类型 $mBoardType")
        when (mBoardType) {
            BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3568M5,
            BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3568A,
            BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3568S,
            BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3568XM,
            BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3566R,
            BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3566X,
            BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3568HV,
            BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3568SC,
            BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3576E-> {
                boardFeatureManager = BoardFeatureSmdt3568Impl(mContext, mBoardType, mBoardControlListener)
                log(TagConstant.TAG_BOARD_FEATURE, "BoardFeatureSmdt3568Impl init")
            }
            //亿晟3568_r
            BoardTypeConstant.BoardTypeYS.BOARD_TYPE_RK3568_R -> {
                boardFeatureManager =
                    BoardFeatureYs3568Impl(mContext, mBoardType, mBoardControlListener)
                log(TagConstant.TAG_BOARD_FEATURE, "BoardFeatureYs3568Impl init")
            }
            else -> { //根据重构前的逻辑，else就认定为是亿晟主板
                boardFeatureManager =
                    BoardFeatureYsImpl(mContext, mBoardType, mBoardControlListener)
                log(TagConstant.TAG_BOARD_FEATURE, "未匹配到主板类型-默认亿晟类型")
            }
        }
        return this
    }

    /**
     * 设置监听
     */
    fun setBoardControlListener(boardControlListener: BoardControlListener): BoardFeatureProxy {
        mBoardControlListener = boardControlListener
        return this
    }

    /**
     * 状态栏隐藏显示
     */
    override fun setStatusBarVisible(isShow: Boolean) {
        log(TagConstant.TAG_BOARD_FEATURE, "boardType: $mBoardType setStatusBarVisible $isShow")
        boardFeatureManager?.setStatusBarVisible(isShow)
    }

    /**
     * 设置定时开关机
     */
    override fun setPowerOffOn(offTime: String, OnTime: String, enable: Boolean) {
        log(
            TagConstant.TAG_BOARD_FEATURE, "boardType: $mBoardType setPowerOffOn " +
                    "offTime $offTime / OnTime $OnTime / enable $enable"
        )
        boardFeatureManager?.setPowerOffOn(offTime, OnTime, enable)
    }

    /**
     * 关机
     */
    override fun closeDevice() {
        log(TagConstant.TAG_BOARD_FEATURE, "closeDevice")
        boardFeatureManager?.closeDevice()
    }

    /**
     * 重启
     */
    override fun restartDevice() {
        log(TagConstant.TAG_BOARD_FEATURE, "restartDevice")
        boardFeatureManager?.restartDevice()
    }

    /**
     * 熄屏
     */
    override fun closeScreen() {
        log(TagConstant.TAG_BOARD_FEATURE, "closeScreen")
        boardFeatureManager?.closeScreen()
    }

    /**
     * 唤醒
     */
    override fun awakeDevice() {
        log(TagConstant.TAG_BOARD_FEATURE, "awakeDevice")
        boardFeatureManager?.awakeDevice()
    }

    override fun setScreenBright(brightValue: Float) {
        log(TagConstant.TAG_BOARD_FEATURE, "setScreenBright")
        boardFeatureManager?.setScreenBright(brightValue)
    }

    override fun getScreenBright(): Float {
        boardFeatureManager?.let {
            return it?.getScreenBright()!!
        }
       return 1f
    }

    /**
     * 设置系统时间
     */
    override fun setSystemTime(time: String, setSystemTimeListener: SetSystemTimeListener) {
        log(TagConstant.TAG_BOARD_FEATURE, "TimeSet setSystemTime ${time}")
        boardFeatureManager?.setSystemTime(time, setSystemTimeListener)
    }

    override fun reSetSystemTime() {

    }

    /**
     * 释放
     */
    fun release() {
        mContext = null
        boardFeatureManager = null
    }

    /**
     * 写韦根
     */
    override fun writeWag(byteArray: ByteArray) {
        log(TagConstant.TAG_BOARD_FEATURE, "writeWag")
        boardFeatureManager?.writeWag(byteArray)
    }

    /**
     * gpio控制
     */
    override fun writeGpio(id: Int, value: Int) {
        log(TagConstant.TAG_BOARD_FEATURE, "writeGpio id:${id} value:${value}")
        boardFeatureManager?.writeGpio(id, value)
    }

    override fun readGpio(id: Int,defaultReturnValue:Int): Int {
        val result: Int = boardFeatureManager?.readGpio(id,defaultReturnValue)!!
        //log(TagConstant.TAG_BOARD_FEATURE, "readGpio id:${id} value:${result}")
        return result
    }

    override fun ledControl(value: Int) {
        log(TagConstant.TAG_BOARD_FEATURE, "ledControl value:${value}")
        boardFeatureManager?.ledControl(value)
    }

    override fun colorLedControl(color: String, value: Int) {
        log(TagConstant.TAG_BOARD_FEATURE, "colorLedControl color:${color} value:${value}")
        boardFeatureManager?.colorLedControl(color, value)
    }

    /**
     * 继电器控制
     */
    override fun relayControl(isOpen: Boolean) {
        log(TagConstant.TAG_BOARD_FEATURE, "relayControl isOpen:${isOpen}")
        boardFeatureManager?.relayControl(isOpen)
    }

    /**
     * 开关adb
     */
    override fun switchAdb(isOpen: Boolean) {
        log(TagConstant.TAG_BOARD_FEATURE, "switchAdb:${isOpen}")
        boardFeatureManager?.switchAdb(isOpen)
    }

    override fun startAppProcess(packageName: String): Boolean {
        boardFeatureManager?.let {
            return it.startAppProcess(packageName)
        }
        return false
    }

    override fun otaUpgrade(otaPath: String): Boolean {
        log(TagConstant.TAG_BOARD_FEATURE, "otaUpgrade:${otaPath}")
        boardFeatureManager?.let {
            return it.otaUpgrade(otaPath)
        }
        return false
    }

    override fun installApk(
        apkPath: String,
        packageName: String,
        apkFileName: String,
        savePath: String,
        providerName: String,
    ) {
        log(
            TagConstant.TAG_BOARD_FEATURE,
            "installApk:apkPath:${apkPath} packageName:${packageName} apkFileName:${apkFileName} savePath:${savePath} providerName:${providerName}"
        )
        boardFeatureManager?.let {
            it.installApk(apkPath, packageName, apkFileName, savePath, providerName)
        } ?: let {
            log(
                TagConstant.TAG_BOARD_FEATURE,
                "主板控制类初始化异常"
            )
        }
    }

    /**
     * 开始监听Gpio状态
     */
//    fun startListenerGpioValue(ioIndex: Int,defaultReturnValue: Int, gpioListener: GpioListener) {
//        log(TagConstant.TAG_BOARD_FEATURE,"开始gpio监听")
//        gpioLoopThread ?: let {
//            gpioLoopThread = GpioListenerLoopThread(ioIndex,defaultReturnValue, gpioListener,mBoardControlListener)
//        }
//        gpioLoopThread?.startLoop()
//    }


    override fun takeScreenShot(path: String, name: String, takeShotListener: TakeShotListener) {
        boardFeatureManager?.takeScreenShot(path, name, takeShotListener)
    }

    override fun takeScreenShot(): Bitmap? {
        return boardFeatureManager?.takeScreenShot()
    }

    override fun setLiveMonitorWithBoardApi(packageName: String, restartTime: Long, enableBroadCast: Boolean):Int? {
       return  boardFeatureManager?.setLiveMonitorWithBoardApi(packageName,restartTime,enableBroadCast)
    }

    override fun isScreenBright(): Boolean? {
        return boardFeatureManager?.isScreenBright()
    }


    fun deleteFileOrFolderByShell(absolutePath:String,callback:(Boolean) -> Unit){
        try {
            val cmd = "time rm -rf  $absolutePath"
            log(TagConstant.TAG_BOARD_FEATURE,  "执行删除指令:" + cmd)
            val mCommandResult: ShellUtils.CommandResult = ShellUtils.execCmd(cmd, true)
            log(TagConstant.TAG_BOARD_FEATURE, "执行结果 result:${mCommandResult.result} successMsg:${mCommandResult.successMsg} errorMsg:${mCommandResult.errorMsg}")
            if (mCommandResult.result == 0) {
                callback.invoke(true)
            } else {
                callback.invoke(false)
            }
        } catch (e: java.lang.Exception) {
            log(TagConstant.TAG_BOARD_FEATURE, "exception:" + e.message)
            callback.invoke(true)
        }
    }


    override fun getTotalRam(): String {
        boardFeatureManager?.let {
            return it.getTotalRam()
        }?:let {
            return ""
        }
    }

    override fun getAvalibleRam(): String {
        boardFeatureManager?.let {
            return it.getAvalibleRam()
        }?:let {
            return ""
        }
    }

    override fun getTotalSd(): String {
        boardFeatureManager?.let {
            return it.getTotalSd()
        }?:let {
            return ""
        }
    }

    override fun getAvalibleSd(): String {
        boardFeatureManager?.let {
            return it.getAvalibleSd()
        }?:let {
            return ""
        }
    }

    override fun rotateScreen(degree: Int) {
        boardFeatureManager?.rotateScreen(degree)
    }

    override fun setEthMode(mode:String,ip:String,gateWay:String,mask:String,dns1:String,dns2:String) {
        boardFeatureManager?.setEthMode(mode,ip,gateWay,mask, dns1, dns2)
    }

    override fun getEthMode(): String {
        boardFeatureManager?.let {
            return it.getEthMode()
        }?:let {
            return ""
        }
    }

    override fun setEthStatus(open:Boolean) {
        boardFeatureManager?.setEthStatus(open)
    }

    override fun getEthStatus(): Boolean {
        boardFeatureManager?.let {
            return it.getEthStatus()
        }?:let {
            return false
        }
    }

    override fun getEthIp(): String {
        boardFeatureManager?.let {
            return it.getEthIp()
        }?:let {
            return ""
        }
    }

    override fun getEthNetGateWay(): String {
        boardFeatureManager?.let {
            return it.getEthNetGateWay()
        }?:let {
            return ""
        }
    }

    override fun getEthNetMask(): String {
        boardFeatureManager?.let {
            return it.getEthNetMask()
        }?:let {
            return ""
        }
    }

    override fun getEthDns1(): String {
        boardFeatureManager?.let {
            return it.getEthDns1()
        }?:let {
            return ""
        }
    }

    override fun getEthDns2(): String {
        boardFeatureManager?.let {
            return it.getEthDns2()
        }?:let {
            return ""
        }
    }
}