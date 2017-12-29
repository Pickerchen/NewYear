package com.sen5.secure.launcher.utils;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
* 反射工具类
*/
public final class ReflectUtils {
        
        private ReflectUtils() {
                super();
        }

        public static boolean isString(Class<?> clazz) {
                return clazz == String.class;
        }
        
        public static boolean isInteger(Class<?> clazz) {
                return clazz == Integer.TYPE || clazz == Integer.class;
        }
        
        public static boolean isLong(Class<?> clazz) {
                return clazz == Long.TYPE || clazz == Long.class;
        }
        
        public static boolean isShort(Class<?> clazz) {
                return clazz == Short.TYPE || clazz == Short.class;
        }
        
        public static boolean isFloat(Class<?> clazz) {
                return clazz == Float.TYPE || clazz == Float.class;
        }
        
        public static boolean isDouble(Class<?> clazz) {
                return clazz == Double.TYPE || clazz == Double.class;
        }
        
        /**
         * 获取对象的私有变量
         */
        public static Object getField(Object instance, String name) throws SecurityException, NoSuchFieldException,
                IllegalArgumentException, IllegalAccessException {
                Object object = null;
                if (instance != null && !TextUtils.isEmpty(name)) {
                        Field field = instance.getClass().getDeclaredField(name);
                        if (field != null) {
                                field.setAccessible(true);
                                object = field.get(instance);
                        }
                }
                return object;
        }

        /**
         * 调用对象的私有函数
         */
        @SuppressWarnings("rawtypes")
        public static Object invokeMethod(Object instance, String name, Object[] objects) throws SecurityException,
                NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
                Object object = null;
                if (instance != null && !TextUtils.isEmpty(name)) {
                        Class[] clazzs = null;
                        if (objects != null) {
                                int length = objects.length;
                                clazzs = new Class[length];
                                for (int i = 0; i < length; i++) {
                                        if (objects != null) {
                                                clazzs[i] = objects.getClass();
                                        }
                                }
                        }
                        Method method = instance.getClass().getDeclaredMethod(name, clazzs);
                        if (method != null) {
                                method.setAccessible(true);
                                object = method.invoke(instance, objects);
                        }
                }
                return object;
        }

}