package com.example.rk_control_lib.util;

import android.os.Build;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * shell命令工具类
 *
 * @author
 * @time 2019/1/24 19:48
 */
public class ShellUtils {

    private static final String LINE_SEP = System.getProperty("line.separator");

    private ShellUtils() {
        throw new UnsupportedOperationException("无法初始化！");
    }

    /**
     * 执行单条shell命令
     * @param command
     * @param isRooted
     * @return
     */
    public static CommandResult execCmd(final String command, final boolean isRooted) {
        return execCmd(new String[]{command}, isRooted, true);
    }

    /**
     * 执行多条shell命令
     * @param commands
     * @param isRooted
     * @return
     */
    public static CommandResult execCmd(final List<String> commands, final boolean isRooted) {
        return execCmd(commands == null ? null : commands.toArray(new String[]{}), isRooted, true);
    }

    /**
     * 执行多条shell命令
     * @param commands
     * @param isRooted
     * @return
     */
    public static CommandResult execCmd(final String[] commands, final boolean isRooted) {
        return execCmd(commands, isRooted, true);
    }

    /**
     * 执行shell命令核心方法
     * @param commands
     * @param isRooted
     * @param isNeedResultMsg
     * @return
     */
    private static CommandResult execCmd(final String[] commands, final boolean isRooted, final boolean isNeedResultMsg) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, "", "");
        }
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRooted ? "su" : "sh");
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                os.write(command.getBytes());
                os.writeBytes(LINE_SEP);
                os.flush();
            }
            os.writeBytes("exit" + LINE_SEP);
            os.flush();
            result = process.waitFor();
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), "UTF-8")
                );
                errorResult = new BufferedReader(
                        new InputStreamReader(process.getErrorStream(), "UTF-8")
                );
                String line;
                if ((line = successResult.readLine()) != null) {
                    successMsg.append(line);
                    while ((line = successResult.readLine()) != null) {
                        successMsg.append(LINE_SEP).append(line);
                    }
                }
                if ((line = errorResult.readLine()) != null) {
                    errorMsg.append(line);
                    while ((line = errorResult.readLine()) != null) {
                        errorMsg.append(LINE_SEP).append(line);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (successResult != null) {
                    successResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(
                result,
                successMsg == null ? "" : successMsg.toString(),
                errorMsg == null ? "" : errorMsg.toString()
        );
    }

    public static class CommandResult {
        public int result;
        public String successMsg;
        public String errorMsg;

        public CommandResult(final int result, final String successMsg, final String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }

        @Override
        public String toString() {
            return "result: " + result + "\n" +
                    "successMsg: " + successMsg + "\n" +
                    "errorMsg: " + errorMsg;
        }
    }

    private static final String[] rootRelatedDirs = new String[]{
            "/su", "/su/bin/su", "/sbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/data/local/su",
            "/system/xbin/su", "/system/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su",
            "/system/bin/cufsdosck", "/system/xbin/cufsdosck", "/system/bin/cufsmgr", "/system/xbin/cufsmgr",
            "/system/bin/cufaevdd", "/system/xbin/cufaevdd", "/system/bin/conbb", "/system/xbin/conbb"};

    public static boolean hasRootPrivilege() {
        boolean hasRootDir = false;
        String[] rootDirs;
        int dirCount = (rootDirs = rootRelatedDirs).length;
        for (int i = 0; i < dirCount; ++i) {
            String dir = rootDirs[i];
            if ((new File(dir)).exists()) {
                hasRootDir = true;
                break;
            }
        }
        return Build.TAGS != null && Build.TAGS.contains("test-keys") || hasRootDir;
    }
}
