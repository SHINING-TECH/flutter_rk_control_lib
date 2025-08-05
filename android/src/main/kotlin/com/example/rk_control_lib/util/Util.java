package com.example.rk_control_lib.util;

import android.os.Environment;
import android.util.Log;


import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by CN on 2016/9/22.
 */
public class Util {
    private enum IsDHDevice {
        None, DHDevice, NoDHDevice
    }

    private static IsDHDevice mIsDHDevice = IsDHDevice.None;

    /**
     * 字符串数组转int数组
     * @param strArray
     * @return
     */
    public static int[] getIntArrayFromStringArray(String[] strArray){
        if(strArray == null){
            return new int[0];
        }
        int[] result = new int[strArray.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Integer.valueOf(strArray[i]);
        }
        return result;
    }

    /*
     * 将时间转换为时间戳
     */
    public static long dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd.HHmmss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        return ts;
    }

    /*
     * 将时间转换为时间戳
     */
    public static long dateToStampYMDHM(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        return ts;
    }

    /*
     * 将时间转换为时间戳
     */
    public static String dateToStampYMDHMForShell(String originalTime) throws ParseException {
        // 定义原始时间格式
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 定义目标时间格式
        // 注意：这里的小时使用的是12小时制（hh），而不是24小时制（HH）
        // 如果你想要24小时制，请确保你的输入时间也是24小时制的，并且在这里使用HH
        SimpleDateFormat targetFormat = new SimpleDateFormat("MMddHHmmyyyy.ss");

        try {
            // 解析原始时间字符串为Date对象
            Date date = originalFormat.parse(originalTime);

            // 格式化Date对象为目标时间字符串
            String formattedTime = targetFormat.format(date);
            return formattedTime;
        } catch (ParseException e) {
            // 处理解析异常
            e.printStackTrace();
        }
        return originalTime;
    }

    /*
     * 将时间转换为时间戳
     */
    public static long dateToStampYMDHMTrim(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        return ts;
    }

    /*
     * 将时间转换为时间戳
     */
    public static long dateToStampYMD(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime();
        return ts;
    }


    /*
     * 将时间转换为时间戳
     */
    public static String getNowYearMonthDayStrTime(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    /*
     * 将时间转换为时间戳
     */
    public static String getYYYYMMddHHmmsstrTime(long time){
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd.HHmmss");
        return format.format(date);
    }

    /*
     * 将时间转换为时间戳
     */
    public static String getTime(long time){
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static boolean getHYDevice(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hyzn/hyzn_upSystemPkg";
        File protect_file = new File(path);
        return protect_file.exists();
    }

    public static void setLowAdj() {
        //修改代码,因为现场发现频繁触发lowmemkill,先修改这个参数试试
        String cmd = "echo 3 > proc/sys/vm/drop_caches";
        ShellUtils.CommandResult result = ShellUtils.execCmd(cmd, true);
        //Log.d(TAG, "echo 3 > proc/sys/vm/drop_cac 执行结果 result:" + result.result + "successMsg:" + result.successMsg + "errorMsg:" + result.errorMsg);
        //修改进程内oom_score_adj的值
        int pid = android.os.Process.myPid();

        String cmdChangeOomScoreAdj = "echo > proc/" + pid + "/oom_score_adj -1000";
        ShellUtils.CommandResult result1 = ShellUtils.execCmd(cmdChangeOomScoreAdj, true);
        //Log.d(TAG, "echo > proc/pid/oom_score_adj -1000 执行结果 result:" + result1.result + "successMsg:" + result1.successMsg + "errorMsg:" + result1.errorMsg);

        String cmdChangeOomScoreAdj2 = "echo > proc/" + pid + "/oom_score 0";
        ShellUtils.CommandResult result2 = ShellUtils.execCmd(cmdChangeOomScoreAdj2, true);


        String cmdChangeOomScoreAdj3 = "echo > proc/" + pid + "/oom_adj -17";
        ShellUtils.CommandResult result3 = ShellUtils.execCmd(cmdChangeOomScoreAdj3, true);
    }
}