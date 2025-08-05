package com.example.rk_control_lib.impl.smdt;

import android.app.smdt.SmdtManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;


import com.cn.kdzn_p2.manager.boardfeature.base.BoardFeatureManager;
import com.example.rk_control_lib.constant.TagConstant;
import com.example.rk_control_lib.listener.BoardControlListener;
import com.example.rk_control_lib.listener.SetSystemTimeListener;
import com.example.rk_control_lib.listener.TakeShotListener;
import com.example.rk_control_lib.util.ShellUtils;
import com.example.rk_control_lib.util.Util;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;


/**
 * author : vinda
 * date : 2021/8/18 18:24
 * description :
 */
public class BoardFeatureSmdtImpl extends BoardFeatureManager {
    String TAG = TagConstant.TAG_BOARD_FEATURE + "-Smdt";
    private SmdtManager smdt;

    public BoardFeatureSmdtImpl(Context context, String boardType, BoardControlListener boardControlListener) {
        setMContext(context);
        setMBoardType(boardType);
        setMBoardControlListener(boardControlListener);
        smdt = SmdtManager.create(getMContext());
    }

    @Override
    public void setStatusBarVisible(boolean isShow) {
        log(TAG, "setStatusBarVisible " + isShow);
        //smdt.smdtSetStatusBar(getMContext(), isShow);
        //smdt.setGestureBar(isShow);
        //2022-3-4修改为通用方法
        if (isShow) {
            execSuCmd1("wm overscan 0,0,0,0");
        } else {
            execSuCmd1("wm overscan 0,-60,0,-60");
        }
    }

    @Override
    public void setPowerOffOn(@NotNull String offTime, @NotNull String OnTime, boolean enable) {
        String data;
        if (enable) {
            data = "1";
        } else {
            data = "0";
        }
        String[] OpenTime = OnTime.split("-");
        String[] CloseTime = offTime.split("-");
        String optime = OpenTime[3] + ":" + OpenTime[4];
        String cltime = CloseTime[3] + ":" + CloseTime[4];
        smdt.smdtSetTimingSwitchMachine(cltime, optime, data);
        log(TAG, "设置定时开关机 视美泰 optime:" + optime + " cltime:" + cltime);
    }

    @Override
    public void closeDevice() {
        log(TAG, "closeDevice");
        smdt.shutDown();
    }

    @Override
    public void restartDevice() {
        log(TAG, "restartDevice");
        smdt.smdtReboot("reboot");
    }

    @Override
    public void closeScreen() {
        smdt.smdtSetLcdBackLight(0);
    }

    @Override
    public void awakeDevice() {
        smdt.smdtSetLcdBackLight(1);
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
            String[] mDate = timeResult.split(" ")[0].split("-");
            String[] mTime = timeResult.split(" ")[1].split(":");//恢复之前的时间
            smdt.setTime(getMContext(),
                    Integer.valueOf(mDate[0]),
                    Integer.valueOf(mDate[1]),
                    Integer.valueOf(mDate[2]),
                    Integer.valueOf(mTime[0]),
                    Integer.valueOf(mTime[1])
            );
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
            String[] mDate = modifyTime.split(" ")[0].split("-");
            String[] mTime = modifyTime.split(" ")[1].split(":");//恢复之前的时间
            smdt.setTime(getMContext(),
                    Integer.valueOf(mDate[0]),
                    Integer.valueOf(mDate[1]),
                    Integer.valueOf(mDate[2]),
                    Integer.valueOf(mTime[0]),
                    Integer.valueOf(mTime[1])
            );
        }
    }


    @Override
    public void writeWag(@NotNull byte[] byteArray) {

    }

    @Override
    public void writeGpio(int id, int value) {

    }

    @Override
    public void ledControl(int value) {
        boolean open = false;
        if (value == 1) {
            open = true;
        }
        //smdt.setLedLighted(SmdtManager.LED_WHITE, open);
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
       // smdt.setLedLighted(smdtLedColor, open);
        log(TAG, "colorLedControl color:" + smdtLedColor + " open:" + open);
//        String smdtLedColor = "";
//        if ("green".equals(color)) {
//            smdt.smdtSetGpioDirection(4,1,value);
//        } else if ("red".equals(color)) {
//            smdtLedColor = SmdtManager.LED_RED;
//            smdt.smdtSetUsbPower(1,3,value);
//        } else {
//            smdtLedColor = SmdtManager.LED_WHITE;
//            smdt.smdtSetControl(3,value);
//        }
//        log(TAG, "colorLedControl color:" + smdtLedColor + " open:" + value);
    }

    @Override
    public void relayControl(boolean isOpen) {
        int data = 0;
        if (isOpen) {
            data = 1;
        }
        smdt.setRelayIoValue(data);
    }

    @Override
    public void installApk(@NotNull String apkPath, @NotNull String packageName, @NotNull String apkFileName, @NotNull String savePath, @NotNull String provider) {
        log(TAG, "smdt3288xt 静默安装");
        SmdtManager smdt = SmdtManager.create(getMContext());
        smdt.smdtSilentInstall(apkPath, getMContext());
        startInstallApk(packageName);
    }

    @Override
    public void switchAdb(boolean isOpen) {

    }

    @Override
    public Boolean isScreenBright() {
        boolean isBright = smdt.smdtGetLcdLightStatus() == 1;
        log(TAG,"屏幕是否开启:"+isBright);
        return isBright;
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
        smdt.setBrightness(null,screenLight);
    }

    @Override
    public float getScreenBright() {
        return getScreenBrightByAndroidApi();
    }

    @Override
    public void takeScreenShot( String path,  String name, TakeShotListener takeShotListener) {
        String absolutePath = path + name;
        log(TAG, "设备截屏 absolutePath:" + absolutePath);
        if (ShellUtils.hasRootPrivilege()){
            log(TAG, "adb 截屏");
            takeShotByAdb(TAG,absolutePath,takeShotListener);
        }else {
            log(TAG, "api 截屏");
            smdt.smdtTakeScreenshot(path,name,getMContext());
            takeShotListener.takeShotListener(true,absolutePath);
        }

    }

    @Override
    public Bitmap takeScreenShot() {
        return smdt.smdtScreenShot(getMContext());
    }

    @Override
    public int readGpio(int id,int defaultReturnValue) {
        return defaultReturnValue;
    }


    @Override
    public Integer setLiveMonitorWithBoardApi( String packageName, long restartTime, boolean enableBroadCast) {
        return -1;
    }


    @Override
    public String getTotalRam() {
        return null;
    }


    @Override
    public String getAvalibleRam() {
        return null;
    }


    @Override
    public String getTotalSd() {
        return null;
    }


    @Override
    public String getAvalibleSd() {
        return null;
    }

    @Override
    public void rotateScreen(int degree) {

    }

    @Override
    public void setEthMode( String mode,  String ip,  String gateWay,  String mask,  String dns1,  String dns2) {

    }


    @Override
    public String getEthMode() {
        return null;
    }


    @Override
    public boolean getEthStatus() {
        return false;
    }


    @Override
    public String getEthNetGateWay() {
        return null;
    }

    @Override
    public String getEthIp() {
        return "";
    }

    @Override
    public String getEthNetMask() {
        return null;
    }


    @Override
    public String getEthDns1() {
        return null;
    }


    @Override
    public String getEthDns2() {
        return null;
    }

    @Override
    public void setEthStatus(boolean open) {

    }
}
