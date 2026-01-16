package com.wslight.utils;

import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.function.Function;

/**
 * Root 权限检测工具类 在运行时检测设备是否具有 root 权限
 */
public class RootChecker {

    private static final String TAG = "RootChecker";

    /**
     * 检查设备是否具有 root 权限
     */
    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    /**
     * 方法1：检查 su 命令是否存在
     */
    private static boolean checkRootMethod1() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return in.readLine() != null;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * 方法2：尝试执行 su 命令
     */
    private static boolean checkRootMethod2() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            return exitValue == 0;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * 方法3：检查系统属性
     */
    private static boolean checkRootMethod3() {
        String buildTags = Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    /**
     * 检查是否可以使用 evdev 捕获 需要 root 权限且设备支持 evdev
     */
    public static boolean canUseEvdevCapture() {
        if (!isDeviceRooted()) {
            Log.d(TAG, "设备未 root，无法使用 evdev 捕获");
            return false;
        }

        // 检查 evdev_reader 是否存在
        boolean hasEvdev = false;
        try {
            File inputDir = new File("/dev/input");
            if (inputDir.exists() && inputDir.isDirectory()) {
                File[] eventFiles = inputDir.listFiles((dir, name) -> name.startsWith("event"));
                hasEvdev = eventFiles != null && eventFiles.length > 0;
            }
        } catch (SecurityException e) {
            Log.d(TAG, "权限不足，无法访问 /dev/input", e);
            return false;
        } catch (Exception e) {
            Log.d(TAG, "检查evdev时发生错误", e);
            return false;
        }

        if (hasEvdev) {
            Log.d(TAG, "设备支持 evdev 捕获");
            return true;
        } else {
            Log.d(TAG, "设备不支持 evdev 捕获");
            return false;
        }
    }

    /**
     * 安全执行 root 命令
     *
     * @param command 要执行的命令
     * @return 命令输出，如果执行失败返回 null
     */
    public static String executeRootCommand(String command) {
        if (!isDeviceRooted()) {
            Log.w(TAG, "尝试在未 root 设备上执行 root 命令: " + command);
            return null;
        }

        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // 写入命令
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();

            // 读取输出
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();
            return output.toString();

        } catch (Throwable t) {
            Log.e(TAG, "执行 root 命令失败: " + command, t);
            return null;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}
