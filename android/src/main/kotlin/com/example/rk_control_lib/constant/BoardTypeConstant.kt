package com.example.rk_control_lib.constant

/**
 *  author : vinda
 *  date : 2021/6/4 8:42
 *  description :主板型号常量表
 */
object BoardTypeConstant {
    //厂家 LG，Smdt，YS，LS，DH
    const val BOARD_TYPE_LG = "LG" //朗国
    const val BOARD_TYPE_Smdt = "Smdt" //视美泰
    const val BOARD_TYPE_YS = "YS" //亿昇
    const val BOARD_TYPE_LS = "LS" //乐瞬
    const val BOARD_TYPE_DH = "DH"

    //具体型号
    const val BOARD_TYPE_DH3288 = "BOARD_TYPE_DH3288"
    const val BOARD_TYPE_R1601015AAL = "R1601015AAL"
    const val BOARD_TYPE_R1601014AAL_KH0020 = "R1601014AAL_KH0020"
    const val BOARD_TYPE_RK3288 = "rk3288" //有的厂家不修改版本信息默认就是这个
    const val BOARD_TYPE_RK3288_A = "RK3288-A" //致善3288
    const val BOARD_TYPE_RK3288_R16 = "rk3288_r16" //未知型号
    const val BOARD_TYPE_RK3368 = "rk3368" //未知型号
    const val BOARD_TYPE_RK3368_BOX = "rk3368-box" //未知型号
    const val BOARD_TYPE_WTA83X = "WTA83X" //未知型号
    const val BOARD_TYPE_AD320_v = "AD320_v2" //致善320主板
    const val BOARD_TYPE_X2 = "X2" //吉为X2

    //消费机
    const val BOARD_TYPE_K2 = "K2" //K2设备
    const val BOARD_TYPE_X8 = "X8" //X8设备
    const val BOARD_TYPE_T1A_X1 = "T1A_X1" //未知设备
    const val BOARD_TYPE_T1A_X1S = "T1A_X1S" //未知设备
    const val BOARD_TYPE_TPS650 = "TPS650" //未知设备
    const val BOARD_TYPE_HUIDU_ANDROID7 = "HuiduAndroid7" //灰度主板-android7.1系统

    //手持机-资产盘点
    const val BOARD_TYPE_FG570 = "FG570"
    //手持机-寻更-智谷联
    const val BOARD_TYPE_3503M = "3503M"
    //手持机-消费-FDJ-770C5
    const val BOARD_TYPE_FDJ_770C5 = "FDJ-770C5"
    //清正3568
    const val BOARD_TYPE_QZ_F68V1_0 = "F68V1_0"

    const val BOARD_TYPE_UNIWIN3588 = "wabon-3588"

    const val BOARD_TYPE_JHC_3399 = "jhc-3399"
    const val BOARD_TYPE_JHC_3288 = "jhc-3288"
    const val BOARD_TYPE_JHC_3566_S = "rk3566_s"

    const val BOARD_TYPE_XC3288 = "xc-3288"

    const val BOARD_TYPE_DWH10 = "DWH10"

    //支付宝 系列
    object BoardTypeZfb{
        //海清FS支付宝门禁设备
        const val BOARD_TYPE_hqfs036 = "hqfs036"
        //海马1代
        const val BOARD_TYPE_FT1MINI = "T1D11"
        //海马2代
        const val BOARD_TYPE_FT2MINI = "FT2MINI"
    }



    // ZK 系列
    object BoardTypeZk {
        const val BOARD_TYPE_ZK_R322 = "ZK-R322" //未知型号待确认
        const val BOARD_TYPE_ZK_R322A = "ZK-R322A" //朗国主板
        const val BOARD_TYPE_ZK_R329 = "ZK-R329" //未知型号待确认
        const val BOARD_TYPE_ZK_R329A = "ZK-R329A" //未知型号待确认
    }

    //天波
    object BoardTypeTPS {
        const val BOARD_TYPE_TPS980P = "TPS980P"
        const val BOARD_TYPE_TPS465 = "TPS465"
        const val BOARD_TYPE_TPS467 = "TPS467"
        const val BOARD_TYPE_TPS550A = "TPS550A"
        const val BOARD_TYPE_TPS550 = "TPS550"
        const val BOARD_TYPE_TPS650 = "TPS650"
        const val BOARD_TYPE_TPS650T = "TPS650T"
        const val BOARD_TYPE_TPS530 = "TPS530"
        const val BOARD_TYPE_TPC50 = "C50"
    }

    //视美泰系列
    object BoardTypeSMDT {
        const val BOARD_TYPE_3288XT = "rk32_88XT" //视美泰门禁机
        const val BOARD_TYPE_3280 = "3280" //视美泰门禁机
        const val BOARD_TYPE_SMDT_3288 = "smdt3288" //视美泰通用标识
        const val BOARD_TYPE_AIOT_3288SF = "AIOT-3288SF" //视美泰双屏消费机
        const val BOARD_TYPE_SMDT_3568M5 = "RK3568-SM5" //视美泰3568-SM5
        const val BOARD_TYPE_SMDT_3568A = "SMDT_RK3568_A" //视美泰3568-R android11系统
        const val BOARD_TYPE_SMDT_3568S = "SMDT_RK3568_S" //视美泰3568-S android11系统
        const val BOARD_TYPE_SMDT_3568XM = "SMDT_RK3568_XM" //视美泰3568-XM android11系统
        const val BOARD_TYPE_SMDT_3566R = "SMDT_3566R" //视美泰3568-R
        const val BOARD_TYPE_SMDT_3566X = "SMDT_3566X" //视美泰3566X
        const val BOARD_TYPE_SMDT_YS_API = "SMDT_YS_API" //视美泰主板适配了YS主板协议
        const val BOARD_TYPE_SMDT_AIOT_8953 = "AIOT-8953" //视美泰高通方案,微信Voip主板
        const val BOARD_TYPE_SMDT_3568HV = "3568HV" //视美泰主板3568HV
        const val BOARD_TYPE_SMDT_3568SC = "3568SC" //视美泰主板3568SC-大华会议门牌
        const val BOARD_TYPE_SMDT_3576E = "3576E" //视美泰主板3568SC-大华会议门牌
    }

    //华壹设备
    object BoardTypeHy {
        const val BOARD_TYPE_HY = "HY" //华壹设备
        const val BOARD_TYPE_FDJ_765B = "FDJ-765B" //华壹设备,蓝色消费机
        const val BOARD_TYPE_HY_3568 = "HY-3568" //华壹设备,3568消费机
    }

    //乐瞬设备
    object BoardTypeLS {
        const val BOARD_TYPE_LSF8302WL005 = "LSF8302WL005" //乐舜3288
        const val BOARD_TYPE_LSF8102WL006 = "LSF8102WL006" //乐舜高通双屏消费机
        const val BOARD_TYPE_LSF8302WL006 = "LSF8302WL006" //乐舜横板电话机
        const val BOARD_TYPE_LSF8102WL200 = "LSF8102WL200" //乐舜横板双屏消费机-二维码扫描版本
        const val BOARD_TYPE_LSF8302WL601 = "LSF8302WL601" //乐舜电话班牌
        const val BOARD_TYPE_LSF8302WL602 = "LSF8302WL602"   //也是乐舜电话班牌-竖版
        const val BOARD_TYPE_LSF870 = "LSF870" //乐舜竖版双屏消费机
        const val BOARD_TYPE_LS60 = "leshun60" //乐舜竖版双屏消费机
        const val BOARD_TYPE_LSC1100 = "LSC1100" //乐舜支付宝班牌
        const val BOARD_TYPE_LSC1200 = "LSC1200" //乐舜班牌-中性版本
        const val BOARD_TYPE_LSF951 = "LSF951" //乐舜消费机-扫码版本
        const val BOARD_TYPE_LSF660 = "LSF660" //乐舜消费机-RK3568版本
    }

    object BoardTypeDH {
        const val BOARD_TYPE_DH_3288W = "rk3288-w"
        const val BOARD_TYPE_RK3568_R_DH = "rk3568_r_dh"

    }


    //亿晟设备
    object BoardTypeYS {
        const val BOARD_TYPE_RK3568_R = "rk3568_r" //亿晟Android10
    }

    const val BOARD_TYPE_YS_3288 = "YS_3288" //亿晟3288

    //海康电子班牌
    object BoardTypeHK {
        const val BOARD_TYPE_HK_D6122TH = "DS-D6122TH-B/C"
        const val BOARD_TYPE_HK_D6122TL = "DS-D6122TL-B/C"
    }

    //朗国电子班牌
    object BoardTypeLG {
        const val BOARD_TYPE_LG_K7 = "LGK7"
        const val BOARD_TYPE_LANGO_XM_R3288_L_FHD_7011 ="LANGO_XM_R3288_L_FHD_7011"//朗国班牌根据反射获取的build.prop内数值
        const val BOARD_TYPE_XINGMA_7004_XM_R3288_L_FHD ="XINGMA_7004_XM_R3288_L_FHD"//朗国班牌根据反射获取的build.prop内数值
        const val BOARD_TYPE_ZSKJ_033_XMR3288L_FHD ="ZSKJ_033_XMR3288L_FHD"//朗国班牌根据反射获取的build.prop内数值
        const val BOARD_TYPE_HONGHE_XM_R3568_D_FHD = "HONGHE_XM_R3568_D_FHD"  //鸿合设备朗国主板-3568
        const val BOARD_TYPE_XINGMA_0001_XMS2381D_2GB_LVDS = "XINGMA_0001_XMS2381D_2GB_LVDS"  //朗国主板 星宸 SSD2381方案
        const val BOARD_TYPE_XINGMA_7002_XM_R3288_L_FHD = "XINGMA_7002_XM_R3288_L_FHD"
    }

    //希沃电子班牌
    object BoardTypeSeeWo {
        const val BOARD_TYPE_SK03C_D = "SK03C-D"
        const val BOARD_TYPE_SK03B = "SK03A-B"
        const val BOARD_TYPE_SK07C_E = "SK07C-E"
    }

    //模拟器设备
    object BoardSimulateDevice {
        const val BOARD_TYPE_NX627J = "NX627J"
    }

}