package com.example.rk_control_lib.impl.smdt;

import android.app.smdt.NetworkInfoData;
import android.app.smdt.SmdtManagerNew;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;


import com.cn.kdzn_p2.manager.boardfeature.base.BoardFeatureManager;
import com.example.rk_control_lib.constant.BoardTypeConstant;
import com.example.rk_control_lib.constant.TagConstant;
import com.example.rk_control_lib.listener.BoardControlListener;
import com.example.rk_control_lib.listener.SetSystemTimeListener;
import com.example.rk_control_lib.listener.TakeShotListener;
import com.example.rk_control_lib.util.DateUtils;
import com.example.rk_control_lib.util.ShellUtils;
import com.example.rk_control_lib.util.Util;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


/**
 * author : vinda
 * date : 2021/8/18 18:24
 * description :
 */
public class BoardFeatureSmdt3568Impl extends BoardFeatureManager {
    String TAG = TagConstant.TAG_BOARD_FEATURE + "-Smdt3568";
    String softWareVersion = "";
    int weekSetOpenCloseFailedResetCount = 0;
    String weekPowerOnOffSetFailedVersion = "rk3568_r-userdebug 11 RQ2A.210505.003 eng.smdt.20220922.120628 release-keys";

    public BoardFeatureSmdt3568Impl(Context context, String boardType, BoardControlListener boardControlListener) {
        setMContext(context);
        setMBoardType(boardType);
        setMBoardControlListener(boardControlListener);
        //修改代码,因为现场发现频繁触发lowmemkill,先修改这个参数试试
        String cmd = "echo 3 > proc/sys/vm/drop_caches";
        ShellUtils.CommandResult result = ShellUtils.execCmd(cmd, true);
        log(TAG, "echo 3 > proc/sys/vm/drop_cac 执行结果 result:" + result.result + "successMsg:" + result.successMsg + "errorMsg:" + result.errorMsg);
        //修改进程内oom_score_adj的值
        int pid = android.os.Process.myPid();
        String cmdChangeOomScoreAdj = "echo > proc/" + pid + "/oom_score_adj -1000";
        ShellUtils.CommandResult result1 = ShellUtils.execCmd(cmdChangeOomScoreAdj, true);
        log(TAG, "echo > proc/pid/oom_score_adj -1000 执行结果 result:" + result1.result + "successMsg:" + result1.successMsg + "errorMsg:" + result1.errorMsg);
        softWareVersion = SmdtManagerNew.getInstance(getMContext()).info_getSoftwareVersion();
        String apiVersion = SmdtManagerNew.getInstance(getMContext()).info_getApiVersion();
        log(TAG, "softWareVersion:" + softWareVersion + " apiVersion:" + apiVersion);
        this.switchAdb(true);
    }

    @Override
    public void setStatusBarVisible(boolean isShow) {
        log(TAG, "setStatusBarVisible " + isShow);
        SmdtManagerNew.getInstance(getMContext()).disp_setStatusBar(isShow);
        SmdtManagerNew.getInstance(getMContext()).disp_setNavigationBar(isShow);
        SmdtManagerNew.getInstance(getMContext()).disp_setStatusBarDrag(isShow);
        SmdtManagerNew.getInstance(getMContext()).disp_setGestureBar(isShow);
    }

    @Override
    public void setPowerOffOn(@NotNull String offTime, @NotNull String OnTime, boolean enable) {
        weekSetOpenCloseFailedResetCount = 0;
        log(TAG, "softWareVersion:" + softWareVersion);
        if (softWareVersion.contains("20240726")
                || softWareVersion.contains("20240618")
                || softWareVersion.contains("20240319")
                || softWareVersion.contains("20241022")
                || softWareVersion.contains("20241031")
                || softWareVersion.contains("2025")) {
            //新版本固件已经支持年月日设置定时开关机了
            log(TAG, "按照年月日设置开关机-API");

            //先清空之前按照星期执行的设置
            int[] week = new int[]{0, 0, 0, 0, 0, 0, 0};
            int resetWeekCode = SmdtManagerNew.getInstance(getMContext()).sys_setAutoPowerOnOff(
                    false,
                    week,
                    00,
                    00,
                    00,
                    00);
            int[] nowTimeConfig = SmdtManagerNew.getInstance(getMContext()).sys_getAutoPowerOnOffRepeat();
            log(TAG, "重置按星期定时开关机  resetWeekCode +" + resetWeekCode + " now week Config:" + Arrays.toString(nowTimeConfig));
            //500ms后再按照年月日设定一次
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        String[] OpenTime = OnTime.split("-");
                        String[] CloseTime = offTime.split("-");
                        String openTime = OpenTime[0] + "-" + OpenTime[1] + "-" + OpenTime[2] + " " + OpenTime[3] + ":" + OpenTime[4] + ":00";
                        String closeTime = CloseTime[0] + "-" + CloseTime[1] + "-" + CloseTime[2] + " " + CloseTime[3] + ":" + CloseTime[4] + ":00";
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date dateOnTime = null;
                        dateOnTime = sdf.parse(openTime);
                        Date dateOffTime = sdf.parse(closeTime);
                        long dateOnTimeTemp = dateOnTime.getTime();
                        long dateOffTimeTemp = dateOffTime.getTime();
                        log(TAG, "设置定时开关机 :" + dateOffTimeTemp + " dateOnTimeTemp:" + dateOnTimeTemp);
                        int resultCode = SmdtManagerNew.getInstance(getMContext()).sys_setAutoPowerOnOffTime(enable, dateOnTimeTemp, dateOffTimeTemp);
                        log(TAG, "设置定时开关机完成:" + resultCode);
                    } catch (ParseException e) {
                        log(TAG, "设置定时开关机异常:" + e);
                        throw new RuntimeException(e);
                    }
                }
            }, 500);

        } else if (softWareVersion.contains("20230621")
                || softWareVersion.contains("20231110")) {
            log(TAG, "按照年月日设置开关机-广播");
            //因为- v11.2.20230609.173302固件有bug，无法开机，后面更新了一版本，支持按照日期设置的
            String[] openTime = OnTime.split("-");
            String[] closeTime = offTime.split("-");
            int[] timeOnArray = {
                    Integer.valueOf(openTime[0]),
                    Integer.valueOf(openTime[1]),
                    Integer.valueOf(openTime[2]),
                    Integer.valueOf(openTime[3]),
                    Integer.valueOf(openTime[4])
            };
            int[] timeOffArray = {
                    Integer.valueOf(closeTime[0]),
                    Integer.valueOf(closeTime[1]),
                    Integer.valueOf(closeTime[2]),
                    Integer.valueOf(closeTime[3]),
                    Integer.valueOf(closeTime[4])
            };
            Intent intent = new Intent("android.intent.action.setpoweronoff");
            intent.putExtra("timeon", timeOnArray);
            intent.putExtra("timeoff", timeOffArray);
            intent.putExtra("enable", enable);
            getMContext().sendBroadcast(intent);
            log(TAG, "setPowerOffOn timeOffArray:" + timeOffArray.toString() + " timeOnArray:" + timeOnArray);
        } else {
            //------------------[天，一，二，三，四，五，六]
            //--------------------{0，1，2，3，4，5，6}
            int[] week = new int[]{0, 0, 0, 0, 0, 0, 0};
            if (enable) {
                //兼容跨天开手机
                //计算出今天是星期几
                int toDayWeekIndex = DateUtils.INSTANCE.getWeekOfDateIndexForSmdt();
                //计算出设置的关机时间是星期几
                int closeWeekIndex = Integer.parseInt(DateUtils.INSTANCE.dateToWeekForSmdt(offTime));
                //计算出设置的开机时间是星期几
                int onWeekIndex = Integer.parseInt(DateUtils.INSTANCE.dateToWeekForSmdt(OnTime));

                //关机时间生效的星期数
                if (toDayWeekIndex == closeWeekIndex) {
                    week[closeWeekIndex] = 1;
                } else {
                    long offTimeTemp = DateUtils.INSTANCE.getDateStringToDate(offTime, "yyyy-MM-dd");
                    //计算出关机时间距离现在多篇少天
                    String nowTimeStr = DateUtils.INSTANCE.getDateFormatString(System.currentTimeMillis(), "yyyy-MM-dd");
                    long nowTimeTemp = DateUtils.INSTANCE.getDateStringToDate(nowTimeStr, "yyyy-MM-dd");
                    long offDayAfterTodayCount = DateUtils.INSTANCE.calculateDaysBetweenTimestamps(nowTimeTemp, offTimeTemp);
                    log(TAG, "关机时间距离现在:" + offDayAfterTodayCount + "天");
                    long nextOffWeekIndex = toDayWeekIndex + offDayAfterTodayCount;
                    if (nextOffWeekIndex > 6) {
                        nextOffWeekIndex = nextOffWeekIndex % 7;
                    }
                    week[(int) nextOffWeekIndex] = 1;
                }
                //开机时间生效的星期数
                if (toDayWeekIndex == onWeekIndex) {
                    week[onWeekIndex] = 1;
                } else {
                    long onTimeTemp = DateUtils.INSTANCE.getDateStringToDate(OnTime, "yyyy-MM-dd");
                    //计算出关机时间距离现在多篇少天
                    String nowTimeStr = DateUtils.INSTANCE.getDateFormatString(System.currentTimeMillis(), "yyyy-MM-dd");
                    long nowTimeTemp = DateUtils.INSTANCE.getDateStringToDate(nowTimeStr, "yyyy-MM-dd");
                    long onDayAfterTodayCount = DateUtils.INSTANCE.calculateDaysBetweenTimestamps(nowTimeTemp, onTimeTemp);
                    log(TAG, "开机时间距离现在:" + onDayAfterTodayCount + "天");
                    long nextOnWeekIndex = toDayWeekIndex + onDayAfterTodayCount;
                    if (nextOnWeekIndex > 6) {
                        nextOnWeekIndex = nextOnWeekIndex % 7;
                    }
                    week[(int) nextOnWeekIndex] = 1;
                }
            }

            String[] OpenTime = OnTime.split("-");
            String[] CloseTime = offTime.split("-");
            String optime = OpenTime[3] + ":" + OpenTime[4];
            String cltime = CloseTime[3] + ":" + CloseTime[4];
            log(TAG, "设置定时开关机 星期数:" + Arrays.toString(week));

            int resultCode = SmdtManagerNew.getInstance(getMContext()).sys_setAutoPowerOnOff(
                    enable,
                    week,
                    Integer.valueOf(OpenTime[3]),
                    Integer.valueOf(OpenTime[4]),
                    Integer.valueOf(CloseTime[3]),
                    Integer.valueOf(CloseTime[4]));
            int[] nowTimeConfig = SmdtManagerNew.getInstance(getMContext()).sys_getAutoPowerOnOffRepeat();
            log(TAG, "设置定时开关机 视美泰 optime:" + optime + " cltime:" + cltime + " resultCode:" + resultCode + " nowTimeConfig:" + Arrays.toString(nowTimeConfig));
            if (resultCode != 0){
                log(TAG, "设置定时开关机失败");
                //只针对这个老固件有这个逻辑
                if (softWareVersion.equals(weekPowerOnOffSetFailedVersion)){
                    resetWeekOpenClose(enable,week,OpenTime,CloseTime);
                }

            } else {
                weekSetOpenCloseFailedResetCount = 0;
            }
        }
    }

    //按星期设置定时开关机失败重新设置逻辑
    private void resetWeekOpenClose(boolean enable, int[] week, String[] openTime, String[] closeTime) {
        weekSetOpenCloseFailedResetCount ++;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int resetCode = SmdtManagerNew.getInstance(getMContext()).sys_setAutoPowerOnOff(
                        enable,
                        week,
                        Integer.valueOf(openTime[3]),
                        Integer.valueOf(openTime[4]),
                        Integer.valueOf(closeTime[3]),
                        Integer.valueOf(closeTime[4]));
                int[] nowTimeConfig = SmdtManagerNew.getInstance(getMContext()).sys_getAutoPowerOnOffRepeat();
                log(TAG, "重新设置定时开关机  resetCode:" + resetCode + " nowTimeConfig:" + Arrays.toString(nowTimeConfig));
                if (resetCode !=0 && weekSetOpenCloseFailedResetCount < 3){
                    resetWeekOpenClose(enable,week,openTime,closeTime);
                } else  {
                    weekSetOpenCloseFailedResetCount = 0;
                }
            }
        },1000);

    }


    @Override
    public void closeDevice() {
        log(TAG, "closeDevice");
        SmdtManagerNew.getInstance(getMContext()).sys_setPowerOff();
    }

    @Override
    public void restartDevice() {
        log(TAG, "restartDevice");
        SmdtManagerNew.getInstance(getMContext()).sys_setReboot();
    }

    @Override
    public void closeScreen() {
        log(TAG, "息屏");
        if (getMBoardType().equals(BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3568SC)){
            SmdtManagerNew.getInstance(getMContext()).disp_setLcdBackLightEnable(1, false);
        } else {
            SmdtManagerNew.getInstance(getMContext()).disp_setLcdBackLightEnable(0, false);
        }

    }

    @Override
    public void awakeDevice() {
        log(TAG, "亮屏");
        if (getMBoardType().equals(BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3568SC)){
            SmdtManagerNew.getInstance(getMContext()).disp_setLcdBackLightEnable(1, true);
        } else {
            SmdtManagerNew.getInstance(getMContext()).disp_setLcdBackLightEnable(0, true);
        }

    }


    @Override
    public void setSystemTime(@NotNull String time, SetSystemTimeListener setSystemTimeListener) {
        if (time.contains("2020") && Util.getNowYearMonthDayStrTime().contains("2020")) {
            log(TAG, "设备当前时间已经2020,不再重复设置");
            setSystemTimeListener.setTimeSuccess();
            return;
        }
        try {
            //如果是yyyyMMdd.HHmmss格式需要转换成"yyyy-MM-dd HH:mm:ss"
            String timeResult = time;
            if (time.contains(".")) {
                log(TAG, "TimeSet 需要转换时间格式");
                //时间戳转换为yyyyMMdd.HHmmss格式时间
                long timeTemp = Util.dateToStamp(time);
                //判断出时间是2020-07-20 00:00:00格式
                timeResult = Util.getTime(timeTemp);
                log(TAG, "TimeSet 转换后格式:" + timeResult);
            }
            log(TAG, "TimeSet setSystemTime:" + time);
            setThisModifyTime(timeResult);
            long lSetTime = Util.dateToStampYMDHM(timeResult);
            SmdtManagerNew.getInstance(getMContext()).sys_setTime(lSetTime);
            startCheckModifyTimeSuccess(setSystemTimeListener, timeResult.split(" ")[0]);
        } catch (Exception e) {
            log(TAG, "TimeSet 修改设备时间失败" + e.getMessage());
            setSystemTimeListener.setTimeError();
        }

    }

    @Override
    public void reSetSystemTime() {
        if (!TextUtils.isEmpty(getThisModifyTime())) {
            String modifyTime = getThisModifyTime();
            log(TAG, "修改设备时间失败,继续修改:" + modifyTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = sdf.parse(modifyTime);
                long dateTime = date.getTime();
                SmdtManagerNew.getInstance(getMContext()).sys_setTime(dateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void writeWag(@NotNull byte[] byteArray) {

    }

    @Override
    public void writeGpio(int id, int value) {

    }

    @Override
    public int readGpio(int id, int defaultReturnValue) {
        return SmdtManagerNew.getInstance(getMContext()).sys_getGpioValue(1);
    }

    @Override
    public void ledControl(int value) {
        boolean open = false;
        if (value == 1) {
            open = true;
        }
        SmdtManagerNew.getInstance(getMContext()).dev_setLedLighted("LED_WHITE", open);
        log(TAG, "ledControl open:" + open);
    }

    @Override
    public void colorLedControl(@NotNull String color, int value) {
        String smdtLedColor = "";
        if ("green".equals(color)) {
            smdtLedColor = "LED_GREEN";
        } else if ("red".equals(color)) {
            smdtLedColor = "LED_RED";
        } else {
            smdtLedColor = "LED_WHITE";
        }
        boolean open = false;
        if (value == 1) {
            open = true;
        }
        SmdtManagerNew.getInstance(getMContext()).dev_setLedLighted(smdtLedColor, open);
        log(TAG, "colorLedControl color:" + smdtLedColor + " open:" + open);
    }

    @Override
    public void relayControl(boolean isOpen) {
        SmdtManagerNew.getInstance(getMContext()).custom_setRelayIoEnable(isOpen);
    }

    @Override
    public void installApk(@NotNull String apkPath, @NotNull String packageName, @NotNull String apkFileName, @NotNull String savePath, @NotNull String provider) {
        log(TAG, "smdt3288xt 静默安装");
        SmdtManagerNew.getInstance(getMContext()).sys_doSilentInstallApp(apkPath, new SmdtManagerNew.InstallCallback() {
            @Override
            public void onInstallFinished(String s, int i, String s1) throws RemoteException {
                log(TAG, "smdt3288xt 静默安装:" + s1);
            }
        });
    }

    @Override
    public void switchAdb(boolean isOpen) {
        SmdtManagerNew.getInstance(getMContext()).sys_setAdbDebug(0, true);
    }

    @Override
    public Boolean isScreenBright() {
        int nowBrightNess = 0;
        if (getMBoardType().equals(BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3568SC)){
            nowBrightNess = SmdtManagerNew.getInstance(getMContext()).disp_getLcdBackLight(1);
        } else {
            nowBrightNess = SmdtManagerNew.getInstance(getMContext()).disp_getLcdBackLight(0);
        }
        log(TAG, "屏幕是否开启:" + nowBrightNess);
        return nowBrightNess > 0;
    }

    public static String execSuCmd1(String cmd) {
        synchronized (cmd) {
            Process process = null;
            DataOutputStream os = null;
            DataInputStream is = null;
            try {
                process = Runtime.getRuntime().exec("su");
                os = new DataOutputStream(process.getOutputStream());
                os.writeBytes(cmd + " \n");
                os.writeBytes("exit\n");
                os.flush();
                int aa = process.waitFor();
                is = new DataInputStream(process.getInputStream());
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                String out = new String(buffer);
                try {
                    if (os != null) {
                        os.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                    process.destroy();
                } catch (Exception e) {
                }
                return out;
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (os != null) {
                        os.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                    process.destroy();
                } catch (Exception e1) {
                }
                return "";
            }
        }
    }

    @Override
    public boolean otaUpgrade(@NotNull String otaPath) {
        return false;
    }

    @Override
    public boolean startAppProcess(@NotNull String packageName) {
        return false;
    }

    @Override
    public void setScreenBright(float brightValue) {
        int maxLightValue = 255;
        int screenLight = (int) (maxLightValue * brightValue);
        if (getMBoardType().equals(BoardTypeConstant.BoardTypeSMDT.BOARD_TYPE_SMDT_3568SC)){
            SmdtManagerNew.getInstance(getMContext()).disp_setLcdBackLight(1, screenLight, 1, true);
        } else {
            SmdtManagerNew.getInstance(getMContext()).disp_setLcdBackLight(0, screenLight, 1, true);
        }

        log(TAG, "setScreenBright:" + screenLight);
    }



    @Override
    public void takeScreenShot(String path,String name, TakeShotListener takeShotListener) {
        String absolutePath = path + name;
        log(TAG, "设备截屏 absolutePath:" + absolutePath);
        if (ShellUtils.hasRootPrivilege()) {
            log(TAG, "adb 截屏");
            takeShotByAdb(TAG, absolutePath, takeShotListener);
        } else {
            log(TAG, "api 截屏");
            int apiResult = SmdtManagerNew.getInstance(getMContext()).disp_getScreenShot(absolutePath);
            if (apiResult == 0) {
                log(TAG, "截屏成功");
                takeShotListener.takeShotListener(true, absolutePath);
            } else {
                log(TAG, "截屏异常");
                takeShotListener.takeShotListener(false, absolutePath);
            }
        }

    }


    @Override
    public Bitmap takeScreenShot() {
        return SmdtManagerNew.getInstance(getMContext()).disp_getScreenShotBitmap();
    }

    @Override
    public float getScreenBright() {
        return getScreenBrightByAndroidApi();
    }


    @Override
    public Integer setLiveMonitorWithBoardApi(String packageName, long restartTime, boolean enableBroadCast) {
        log(TAG, "设置守护进程 packageName:" + packageName + " time:" + restartTime);
        int result = SmdtManagerNew.getInstance(getMContext()).sys_setDaemonsActivity(packageName, restartTime, enableBroadCast);
        String nowSysDeamonsActivity = SmdtManagerNew.getInstance(getMContext()).sys_getDaemonsActivity();
        log(TAG, "读取系统当前守护进程 nowSysDeamonsActivity:" + nowSysDeamonsActivity);
        return 0;
    }


    @Override
    public String getTotalRam() {
        return SmdtManagerNew.getInstance(getMContext()).info_getTotalMemory();
    }


    @Override
    public String getAvalibleRam() {
        return SmdtManagerNew.getInstance(getMContext()).info_getAvailMemory();
    }


    @Override
    public String getTotalSd() {
        return SmdtManagerNew.getInstance(getMContext()).info_getTotalStorage();
    }


    @Override
    public String getAvalibleSd() {
        return SmdtManagerNew.getInstance(getMContext()).info_getAvailStorage();
    }

    @Override
    public void rotateScreen(int degree) {
        SmdtManagerNew.getInstance(getMContext()).disp_setDisplayRotation(0, degree);
    }

    @Override
    public void setEthMode(String mode, String ip, String gateWay, String mask, String dns1, String dns2) {
        if (mode.equals("static")) {
            SmdtManagerNew.getInstance(getMContext()).net_setNetWorkModel("eth0", 1, ip, gateWay, mask, dns1, dns2);
        } else {
            SmdtManagerNew.getInstance(getMContext()).net_setNetWorkModel("eth0", 0, "", "", "", "", "");
        }
    }


    @Override
    public String getEthMode() {
        int mode = SmdtManagerNew.getInstance(getMContext()).net_getNetWorkModel("eth0");
        if (mode == 0) {
            return "dhcp";
        } else {
            return "static";
        }
    }

    @Override
    public void setEthStatus(boolean open) {
        SmdtManagerNew.getInstance(getMContext()).net_setNetWork("eth0", open);
    }

    @Override
    public boolean getEthStatus() {
        return SmdtManagerNew.getInstance(getMContext()).net_getNetWork("eth0") == 1;
    }



    @Override
    public String getEthIp() {
        NetworkInfoData networkInfoData = SmdtManagerNew.getInstance(getMContext()).net_getNetWorkInf("eth0");
        if (networkInfoData != null) {
            return networkInfoData.getIp();
        } else {
            return "";
        }
    }


    @Override
    public String getEthNetGateWay() {
        NetworkInfoData networkInfoData = SmdtManagerNew.getInstance(getMContext()).net_getNetWorkInf("eth0");
        if (networkInfoData != null) {
            return networkInfoData.getGateway();
        } else {
            return "";
        }
    }


    @Override
    public String getEthNetMask() {
        NetworkInfoData networkInfoData = SmdtManagerNew.getInstance(getMContext()).net_getNetWorkInf("eth0");
        if (networkInfoData != null) {
            return networkInfoData.getNetmask();
        } else {
            return "";
        }
    }


    @Override
    public String getEthDns1() {
        NetworkInfoData networkInfoData = SmdtManagerNew.getInstance(getMContext()).net_getNetWorkInf("eth0");
        if (networkInfoData != null) {
            return networkInfoData.getDns1();
        } else {
            return "";
        }
    }


    @Override
    public String getEthDns2() {
        NetworkInfoData networkInfoData = SmdtManagerNew.getInstance(getMContext()).net_getNetWorkInf("eth0");
        if (networkInfoData != null) {
            return networkInfoData.getDns2();
        } else {
            return "";
        }
    }

}
