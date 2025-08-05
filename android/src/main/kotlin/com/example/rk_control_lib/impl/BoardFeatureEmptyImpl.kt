package com.cn.kdzn_p2.manager.boardfeature.impl

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import com.cn.kdzn_p2.manager.boardfeature.base.BoardFeatureManager
import com.example.rk_control_lib.listener.BoardControlListener
import com.example.rk_control_lib.listener.SetSystemTimeListener
import com.example.rk_control_lib.listener.TakeShotListener
import com.example.rk_control_lib.util.ShellUtils

/**
 *  author : vinda
 *  date : 2021/6/4 9:12
 *  description :空实现
 */
class BoardFeatureEmptyImpl(context: Context?, boardType:String?,boardControlListener: BoardControlListener?) : BoardFeatureManager() {
    var TAG = "BoardFeatureEmptyImpl"

    init {
        mContext = context
        mBoardType = boardType
        mBoardControlListener = boardControlListener
    }

    override fun setStatusBarVisible(isShow: Boolean) {
        log(TAG, "setStatusBarVisible $isShow")
    }

    override fun setPowerOffOn(offTime: String, OnTime: String, enable: Boolean) {

    }

    override fun closeDevice() {

    }

    override fun restartDevice() {

    }

    override fun closeScreen() {

    }

    override fun awakeDevice() {

    }

    override fun setScreenBright(brightValue: Float) {

    }

    override fun getScreenBright(): Float {
        return 1f
    }

    override fun setSystemTime(time: String, setSystemTimeListener: SetSystemTimeListener) {
        setSystemTimeListener.setTimeError()
    }

    override fun reSetSystemTime() {

    }

    override fun writeWag(byteArray: ByteArray) {

    }

    override fun writeGpio(id: Int, value: Int) {

    }

    override fun readGpio(id: Int,defaultReturnValue:Int):Int {
        return defaultReturnValue
    }

    override fun ledControl(value: Int) {

    }
    override fun otaUpgrade(otaPath: String):Boolean {
        return false
    }

    override fun colorLedControl(color: String, value: Int) {

    }

    override fun relayControl(isOpen: Boolean) {

    }

    override fun installApk(
        apkPath: String,
        packageName: String,
        apkFileName: String,
        savePath: String,
        provider: String
    ) {
        installUseSystemApi(apkPath,packageName,apkFileName,savePath,provider)
        startInstallApk(packageName)
    }
    override fun switchAdb(isOpen: Boolean) {

    }

    override fun startAppProcess(packageName: String): Boolean {
        log(TAG, "拉起程序:${packageName}")
        return false
    }

    override fun isScreenBright(): Boolean {
        return true
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
            log(TAG,"root adb 截屏")
            takeShotByAdb(TAG,absolutePath,takeShotListener)
        }else{
            log(TAG,"截屏异常")
            takeShotListener.takeShotListener(false,absolutePath)
        }
    }


    override fun getTotalRam(): String {
        return ""
    }

    override fun getAvalibleRam(): String {
        return ""
    }

    override fun getTotalSd(): String {
        return ""
    }

    override fun getAvalibleSd(): String {
        return ""
    }

    override fun rotateScreen(degree: Int) {
    }

    override fun setEthMode(mode:String,ip:String,gateWay:String,mask:String,dns1:String,dns2:String) {

    }

    override fun getEthMode(): String {
        return ""
    }

    override fun setEthStatus(open:Boolean) {

    }

    override fun getEthStatus(): Boolean {
        return false
    }
    override fun getEthIp(): String {
        return ""
    }
    override fun getEthNetGateWay(): String {
        return ""
    }

    override fun getEthNetMask(): String {
        return ""
    }

    override fun getEthDns1(): String {
        return ""
    }

    override fun getEthDns2(): String {
        return ""
    }

}