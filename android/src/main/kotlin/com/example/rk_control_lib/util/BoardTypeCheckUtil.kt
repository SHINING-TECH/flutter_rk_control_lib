package com.example.rk_control_lib.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import com.example.rk_control_lib.constant.BoardTypeConstant
import com.example.rk_control_lib.listener.BoardControlListener

/**
 * author : vinda
 * date : 2023/3/3 11:22
 *
 * description :判断当前主板，主要方式(多种方式结合判断) ！！！需要主意判断逻辑顺序！！！
 * 1.根据 Build.MODEL                     大部分厂商会修改这个值
 * 2.读取build.proc中的值                  小部分厂商会在这个文件中进行标识
 * 3.根据主板厂商提供的SDk                  小部分厂商提供的sdk可以判断出
 * 4.判断西戎内注册的广播接收者              通过广播实现api控制的主板可以判断出来
 * 5.根据主板是否已经安装了自带apk           通常主板出厂默认自带一些程序用来配合API实现
 * 6.反射主板Framework类，看是否抛出异常     通常主板Framework会添加一些类用来配合API实现
 *
 * function:
 * 亿昇主板:
 *  主板内置apk  com.ys.ys_receiver com.ys.checknet
 * 大华主板:
 *  主板内置apk  com.lamy.lamyconfigmanager  com.lamy.lamylogserver   android.lamy.server
 * 华壹主板:
 *  系统pm内能够传到定义了广播 com.hyzn.sdk.switchNavBar
 * 视美泰主板
 *  主板内置apk  com.smdt.test.basic com.smdt.settings_gpio com.smdt.RecordLogforSettings
 */
class BoardTypeCheckUtil {
    var mBoardControlListener: BoardControlListener? = null
    var hkDeviceCheckError = false //HK设备校验失败后不再重复校验，因为第二次调用会触发SDK内死循环，导致ANR

    //亿昇主板系统APK
    object YSBoardSysApk {
        const val COM_YS_YS_RECEIVER = "com.ys.ys_receiver"
        const val COM_YS_CHECKNET = "com.ys.checknet"
    }

    //大华主板系统APK
    object DHBoardSysApk {
        const val COM_LAMY_LAMYCONFIGMANAGER = "com.lamy.lamyconfigmanager"
        const val COM_LAMY_LAMYLOGSERVER = "com.lamy.lamylogserver"
        const val COM_LAMY_SERVER = "android.lamy.server"
    }

    //tips:这个apk为瑞芯微官方测试工具(7.1),用它进行判断主板不准确
    object HYBoardSysApk {
        const val COM_DEVICETEST = "com.DeviceTest"
    }

    //视美泰主板系统APK
    object SMDTBoardSysApk {
        const val COM_SMDT_TEST_BASIC = "com.smdt.test.basic"
        const val COM_SMDT_SETTINGS_GPIO = "com.smdt.settings_gpio"
        const val COM_SMDT_RECORDLOGFORSETTINGS = "com.smdt.RecordLogforSettings"
    }

    //朗国主板系统APK
    object LgBoardSysApk {
        const val COM_LG_OTA = "com.lango.ota"
        const val COM_LG_XBH_LANGOFCT = "com.xbh.langofct"
    }


    companion object {
        val instance: BoardTypeCheckUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { BoardTypeCheckUtil() }
    }

    fun checkBoardType(mContext: Context, boardControlListener: BoardControlListener?): String {
        mBoardControlListener = boardControlListener
        log("判断当前主板类型")
        val buildModel = Build.MODEL //主板型号-对应系统设置->关于设备->型号
        val buildBoard =  SystemPropertiesProxy.get(mContext, "ro.build.board")
        val customerCode = SystemPropertiesProxy.get(mContext, "ro.product.customer.model") //部分主板编译固件时会修改这个值,目前发现有此参数的主板如下
        val buildUser = SystemPropertiesProxy.get(mContext, "ro.build.user") //编译固件的User,也可以用于主板判断
        val product = SystemPropertiesProxy.get(mContext, "ro.product.devname") //编译固件的User,也可以用于主板判断
        val roBootConsole = SystemPropertiesProxy.get(mContext, "ro.boot.console") //主板调试串口
        log("\nbuildModel:${buildModel}\ncustomerCode:${customerCode}\nbuildUser:${buildUser}\nroBootConsole:${roBootConsole} product:${product}")

        //---------------------------Step1 根据customerCode判断
        if (!customerCode.isNullOrBlank()) {
            when (customerCode) {
                BoardTypeConstant.BoardTypeLG.BOARD_TYPE_LANGO_XM_R3288_L_FHD_7011 -> {
                    log("BOARD_TYPE_LANGO_XM_R3288_L_FHD_7011 朗国星马班牌")
                    return BoardTypeConstant.BoardTypeLG.BOARD_TYPE_LANGO_XM_R3288_L_FHD_7011
                }
                BoardTypeConstant.BoardTypeLG.BOARD_TYPE_XINGMA_7004_XM_R3288_L_FHD -> {
                    log("BOARD_TYPE_XINGMA_7004_XM_R3288_L_FHD 朗国星马班牌")
                    return BoardTypeConstant.BoardTypeLG.BOARD_TYPE_XINGMA_7004_XM_R3288_L_FHD
                }
                BoardTypeConstant.BoardTypeLG.BOARD_TYPE_XINGMA_0001_XMS2381D_2GB_LVDS ->{
                    log("BOARD_TYPE_XINGMA_0001_XMS2381D_2GB_LVDS 朗国星马班牌")
                    return BoardTypeConstant.BoardTypeLG.BOARD_TYPE_XINGMA_0001_XMS2381D_2GB_LVDS
                }
                BoardTypeConstant.BoardTypeLG.BOARD_TYPE_ZSKJ_033_XMR3288L_FHD ->{
                    log("BOARD_TYPE_ZSKJ_033_XMR3288L_FHD 朗国星马班牌")
                    return BoardTypeConstant.BoardTypeLG.BOARD_TYPE_ZSKJ_033_XMR3288L_FHD
                }
                BoardTypeConstant.BoardTypeLG.BOARD_TYPE_XINGMA_7002_XM_R3288_L_FHD ->{
                    log("BOARD_TYPE_XINGMA_7002_XM_R3288_L_FHD 朗国星马班牌")
                    return BoardTypeConstant.BoardTypeLG.BOARD_TYPE_XINGMA_7002_XM_R3288_L_FHD
                }
                else ->{
                    if (customerCode.contains("HONGHE") && customerCode.contains("XM_R3568_D_FHD")){
                        return BoardTypeConstant.BoardTypeLG.BOARD_TYPE_HONGHE_XM_R3568_D_FHD
                    }
                }
            }
        }
        //如果设备自带了朗国系统ota apk，但是customerCode不是以上两种，则判定为是朗国自用的主板（非与星马合作的版本）
        if (checkApplication(mContext, LgBoardSysApk.COM_LG_OTA)) {
            log("朗国自用主板-对应之前K7")
            return BoardTypeConstant.BOARD_TYPE_LG
        }

        //framwork中含有灰度主板sdk路径
        if (checkClassIsOk("cn.huidu.android.HuiduSupport")){
            log("灰度设备")
            return BoardTypeConstant.BOARD_TYPE_HUIDU_ANDROID7
        }

        if (checkApplication(mContext, "cn.huidu.testapp")) {
            log("灰度设备-3568")
            return BoardTypeConstant.BOARD_TYPE_HUIDU_ANDROID7
        }

        //---------------------------Step3 HY消费机-目前根据编译人员进行判断-目前是黄国涛(hgt),tips:这个以后可能会变化!
        //华壹主板-
        if (checkHasBroadCastReceiver(mContext,"com.hyzn.sdk.switchNavBar")) {
            if (Build.VERSION.SDK_INT == 30) {
                log("HY-3568消费设备")
                return BoardTypeConstant.BoardTypeHy.BOARD_TYPE_HY_3568
            } else {
                log("HY消费设备")
                return BoardTypeConstant.BoardTypeHy.BOARD_TYPE_HY
            }

        } else {
            log("非HY消费设备")
        }

        //---------------------------Step4 Smdt设备自带测试程序判断 com.smdt.test.basic
        //视美泰主板
        if (checkApplication(mContext, SMDTBoardSysApk.COM_SMDT_TEST_BASIC)) {
            log("check com.smdt.test.basic success => SMDT device")
            //smdt适配了ys的协议
            if (checkApplication(mContext, "com.ys.ys_receiver") || checkApplication(mContext, "com.yishengkj.achieve")) {
                log("SMDT设备-YS API版本")
                return BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_YS_API
            }
            if (Build.VERSION.SDK_INT == 25) {
                log("SMDT 7.1")
                //如果是7.1设备-使用smdt自带AIP
                return BoardTypeConstant.BOARD_TYPE_Smdt
            }
            if (buildModel.equals("rk3568_r") && Build.VERSION.SDK_INT == 30) {
                log("SMDT 3568r android 11")
                //如果是3568r并且是android，使用smdt new api
                return BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3568A
            }
            if (buildModel.equals("rk3566_r") && Build.VERSION.SDK_INT == 30) {
                log("SMDT 3566r android 11")
                return BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3566R
            }
            if (buildModel.equals("3566X") && Build.VERSION.SDK_INT == 30) {
                log("SMDT 3566X android 11")
                return BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3566X
            }
            if (buildModel.equals("3568XM") && Build.VERSION.SDK_INT == 30){
                log("SMDT 3568XM android 11")
                return BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3568XM
            }
            if (buildModel.equals("3568S") && Build.VERSION.SDK_INT == 30){
                log("SMDT 3568S android 11")
                return BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3568S
            }
            if (buildModel.equals("3568A") && Build.VERSION.SDK_INT == 30){
                log("SMDT 3568A android 11")
                return BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3568A
            }
            if (buildModel.equals("3568HV") && Build.VERSION.SDK_INT == 30){
                log("SMDT 3568HV android 11")
                return BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3568HV
            }
            if (buildModel.equals("3568SC") && Build.VERSION.SDK_INT == 30){
                log("SMDT 3568SC android 11")
                return BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3568SC
            }
            if (buildModel.equals("3576E")||buildModel.equals("3576SE")){
                log("SMDT 3576E android 14")
                return BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3576E
            }
        } else {
            log("非SMDT设备")
        }

        //天波智能结算台设备
        if (buildModel.equals("C50")){
            log("TP C50")
            return BoardTypeConstant.BoardTypeTPS.BOARD_TYPE_TPC50
        }

        //智能结算台uniwin 3588
        if (buildModel.equals("wabon-3588")){
            log("wabon-3588")
            return BoardTypeConstant.BOARD_TYPE_UNIWIN3588
        }
        //---------------------------Step5 反射判断海康设备


        //---------------------------Step6 根据builduser判断
        if(buildUser.equals("jhc") || checkHasBroadCastReceiver(mContext,"com.jhc.narStatusbar")){
            if (buildModel.contains("3399")){
                //jhc3399设备
                return BoardTypeConstant.BOARD_TYPE_JHC_3399
            }
        }
        if (buildModel.equals("rk3399-Android10")){
            return BoardTypeConstant.BOARD_TYPE_JHC_3399
        }
        if (product.equals("JHC-881")){
            //jhc3288设备
            return BoardTypeConstant.BOARD_TYPE_JHC_3288
        }
        //向成主板(api仿YS)  xcs1
        if(buildBoard.equals("xc3288ic")){
            log("device is XC 3288!")
            return BoardTypeConstant.BOARD_TYPE_XC3288
        } else {
            log("device is not XC 3288!")
        }

        //直接根据Build.MODEL判断出来的主板
        return buildModel
    }


    /**
     * 判断系统Framwork中是否有指定的类（部分主板为了实现api会在FrameWork加一层中间件）
     */
    private fun checkClassIsOk(classPath:String): Boolean {
        var result = true
        try {
            Class.forName(classPath)
        } catch (e: ClassNotFoundException) {
            result = false
        }
        return result
    }

    /**
     * 判断应用是否存在
     * @param packageName
     * @return
     */
    private fun checkApplication(context: Context, packageName: String): Boolean {
        return try {
            val info = context.packageManager.getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * 检查系统中是否有注册了特定的广播
     * @param castReceiveName 广播定义
     */
    private fun checkHasBroadCastReceiver(context: Context,castReceiveName:String):Boolean{
        val pm: PackageManager = context.getPackageManager()
        val broadcastIntent = Intent(castReceiveName)
        val matches: List<ResolveInfo> = pm.queryBroadcastReceivers(broadcastIntent, 0)
        log("${castReceiveName} size = ${matches.size}")
        return matches.size > 0
    }

    private fun log(logInfo: String) {
        mBoardControlListener?.let { it.boardFeatureLogInfo("BoardTypeCheckUtil", logInfo) }
    }

}