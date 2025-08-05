package com.example.rk_control_lib.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexFile;

/**
 * author : vinda
 * date : 2022/8/18 16:50
 * description :通过反射获取build.prop文件中一些隐藏的数值
 * function:
 */
public class SystemPropertiesProxy {
    /**
     * 根据给定Key获取值.
     * @return 如果不存在该key则返回空字符串
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    public static String get(Context context, String key) throws IllegalArgumentException {

        String ret= "";

        try{

            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[1];
            paramTypes[0]= String.class;

            Method get = SystemProperties.getMethod("get", paramTypes);

            //参数
            Object[] params= new Object[1];
            params[0]= new String(key);

            ret= (String) get.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= "";
            //TODO
        }

        return ret;

    }

    /**
     * 根据Key获取值.
     * @return 如果key不存在, 并且如果def不为空则返回def否则返回空字符串
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    public static String get(Context context, String key, String def) throws IllegalArgumentException {

        String ret= def;

        try{

            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= String.class;
            paramTypes[1]= String.class;

            Method get = SystemProperties.getMethod("get", paramTypes);

            //参数
            Object[] params= new Object[2];
            params[0]= new String(key);
            params[1]= new String(def);

            ret= (String) get.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= def;
            //TODO
        }

        return ret;

    }

    /**
     * 根据给定的key返回int类型值.
     * @param key 要查询的key
     * @param def 默认返回值
     * @return 返回一个int类型的值, 如果没有发现则返回默认值
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    public static Integer getInt(Context context, String key, int def) throws IllegalArgumentException {

        Integer ret= def;

        try{

            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= String.class;
            paramTypes[1]= int.class;

            Method getInt = SystemProperties.getMethod("getInt", paramTypes);

            //参数
            Object[] params= new Object[2];
            params[0]= new String(key);
            params[1]= new Integer(def);

            ret= (Integer) getInt.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= def;
            //TODO
        }

        return ret;

    }

    /**
     * 根据给定的key返回long类型值.
     * @param key 要查询的key
     * @param def 默认返回值
     * @return 返回一个long类型的值, 如果没有发现则返回默认值
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    public static Long getLong(Context context, String key, long def) throws IllegalArgumentException {

        Long ret= def;

        try{

            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties= cl.loadClass("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= String.class;
            paramTypes[1]= long.class;

            Method getLong = SystemProperties.getMethod("getLong", paramTypes);

            //参数
            Object[] params= new Object[2];
            params[0]= new String(key);
            params[1]= new Long(def);

            ret= (Long) getLong.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= def;
            //TODO
        }

        return ret;

    }

    /**
     * 根据给定的key返回boolean类型值.
     * 如果值为 'n', 'no', '0', 'false' or 'off' 返回false.
     * 如果值为'y', 'yes', '1', 'true' or 'on' 返回true.
     * 如果key不存在, 或者是其它的值, 则返回默认值.
     * @param key 要查询的key
     * @param def 默认返回值
     * @return 返回一个boolean类型的值, 如果没有发现则返回默认值
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    public static Boolean getBoolean(Context context, String key, boolean def) throws IllegalArgumentException {

        Boolean ret= def;

        try{

            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= String.class;
            paramTypes[1]= boolean.class;

            Method getBoolean = SystemProperties.getMethod("getBoolean", paramTypes);

            //参数
            Object[] params= new Object[2];
            params[0]= new String(key);
            params[1]= new Boolean(def);

            ret= (Boolean) getBoolean.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= def;
            //TODO
        }

        return ret;

    }

    /**
     * 根据给定的key和值设置属性, 该方法需要特定的权限才能操作.
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     * @throws IllegalArgumentException 如果value超过92个字符则抛出该异常
     */
    public static void set(Context context, String key, String val) throws IllegalArgumentException {

        try{

            @SuppressWarnings("unused")
            DexFile df = new DexFile(new File("/system_ext/priv-app/Settings/Settings.apk"));
            @SuppressWarnings("unused")
            ClassLoader cl = context.getClassLoader();
            Class SystemProperties11 = Class.forName("com/android/settings/DshSettings");
            @SuppressWarnings("rawtypes")
            Class SystemProperties = Class.forName("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= String.class;
            paramTypes[1]= String.class;

            Method set = SystemProperties.getMethod("set", paramTypes);

            //参数
            Object[] params= new Object[2];
            params[0]= new String(key);
            params[1]= new String(val);

            set.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            //TODO
            Log.d("","");
        }

    }


    public static void setProp(String key, String value) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("set", String.class, String.class);
            set.invoke(c, key, value );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void nothing11(Context cc, boolean open) {
        try {
            Context c = cc.createPackageContext("android.lamy.proxyserver", Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            Class clazz = c.getClassLoader().loadClass("android/lamy/proxyserver/router/system/Shell");
            Object object = clazz.newInstance();
            Log.w("", "method: " + clazz.getName());
            Method[] methods= clazz.getDeclaredMethods();
            for (Method meth : methods) {
                Log.w("", "field: " + meth.getName());
                if(meth.getName().equals("exec")) {
                    meth.setAccessible(true);
                    if (open){
                        meth.invoke(object,"setprop lamy.usb.adb.cfg peripheral");
                        meth.invoke(object,"setprop service.adb.tcp.port 5555");
                        meth.invoke(object,"setprop lamy.debug.adb.connect true");
                        meth.invoke(object,"setprop lamy.debug.adbpwd.enable 1");
                        meth.invoke(object,"setprop persist.adb.tls_server.enable 1");
                        meth.invoke(object,"start adbd");
                    } else {
                        meth.invoke(object,"setprop lamy.usb.adb.cfg ");
                        meth.invoke(object,"start adbd");
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    public static void shellDh11(Context cc, String cmd) {
        try {
            Context c = cc.createPackageContext("android.lamy.proxyserver", Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            Class clazz = c.getClassLoader().loadClass("android/lamy/proxyserver/router/system/Shell");
            Object object = clazz.newInstance();
            Log.w("", "method: " + clazz.getName());
            Method[] methods= clazz.getDeclaredMethods();
            for (Method meth : methods) {
                Log.w("", "field: " + meth.getName());
                if(meth.getName().equals("exec")) {
                    meth.setAccessible(true);
                    meth.invoke(object,"su");
                    meth.invoke(object,cmd);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
