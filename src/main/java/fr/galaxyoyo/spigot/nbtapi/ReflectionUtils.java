package fr.galaxyoyo.spigot.nbtapi;

import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class ReflectionUtils {
    private static final String SERVER_VERSION = Bukkit.getServer().getClass().getName().split("\\.")[3];

    static {
        if (!Pattern.matches("1\\.(8|9|10|11)", SERVER_VERSION))
		{
			System.out.println("[NBTAPI] ***********************************************************************************************************");
			System.out.println("[NBTAPI] Warning: You're running Bukkit " + SERVER_VERSION + ". Known supported versions are 1.8, 1.9, 1.10 and 1.11.");
			System.out.println("[NBTAPI] The API may not work.");
			System.out.println("[NBTAPI] Please don't leave any error message.");
			System.out.println("[NBTAPI] ***********************************************************************************************************");
		}
    }

    public static <T> T invokeNMSStaticMethod(String className, String method, Class<?>[] parameterClasses, Object... params) {
        return invokeNMSMethod(className, method, null, parameterClasses, (Object[]) params);
    }

    public static <T> T invokeNMSMethod(String className, String method, Object invoker, Class<?>[] parameterClasses, Object... params) {
        Validate.isTrue(parameterClasses.length == params.length, "Parameter array's length must be equal to the params array's length");
        try {
            Class<?> clazz = getNMSClass(className);
            Method m = clazz.getDeclaredMethod(method, parameterClasses);
            m.setAccessible(true);
            //noinspection unchecked
            return (T) m.invoke(invoker, (Object[]) params);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T invokeNMSMethod(String method, Object invoker, Class<?>[] parameterClasses, Object... params) {
        return invokeNMSMethod(invoker.getClass().getSimpleName(), method, invoker, parameterClasses, (Object[]) params);
    }

    public static <T> T invokeNMSMethod(String method, Object invoker) {
        return invokeNMSMethod(method, invoker, new Class<?>[0]);
    }

    public static Object newNMS(String className) {
        return newNMS(className, new Class<?>[0]);
    }

    public static Object newNMS(String className, Class<?>[] parameterClasses, Object... params) {
        try {
            Class<?> clazz = getNMSClass(className);
            Constructor<?> constructor = clazz.getDeclaredConstructor(parameterClasses);
            constructor.setAccessible(true);
            return constructor.newInstance(params);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static <T> T getNMSField(Object owner, String fieldName) {
        return getNMSField(owner.getClass().getSimpleName(), owner, fieldName);
    }

    public static <T> T getNMSField(String className, Object owner, String fieldName) {
        try {
            Class<?> clazz = getNMSClass(className);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            //noinspection unchecked
            return (T) field.get(owner);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static <T> T getNMSStaticField(String className, String fieldName) {
        return getNMSField(className, null, fieldName);
    }

    public static Class<?> getNMSClass(String className) {
        try {
            return Class.forName("net.minecraft.server." + SERVER_VERSION + "." + className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    public static <T> T invokeBukkitStaticMethod(String className, String method, Class<?>[] parameterClasses, Object... params) {
        return invokeBukkitMethod(className, method, null, parameterClasses, (Object[]) params);
    }

    public static <T> T invokeBukkitMethod(String className, String method, Object invoker, Class<?>[] parameterClasses, Object... params) {
        Validate.isTrue(parameterClasses.length == params.length, "Parameter array's length must be equal to the params array's length");
        try {
            Class<?> clazz = getBukkitClass(className);
            Method m = clazz.getDeclaredMethod(method, parameterClasses);
            m.setAccessible(true);
            //noinspection unchecked
            return (T) m.invoke(invoker, (Object[]) params);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T invokeBukkitMethod(String method, Object invoker, Class<?>[] parameterClasses, Object... params) {
        return invokeBukkitMethod(invoker.getClass().getName().replace("org.bukkit.craftbukkit." + SERVER_VERSION + ".", ""), method, invoker,
                parameterClasses, (Object[]) params);
    }

    public static <T> T invokeBukkitMethod(String method, Object invoker) {
        return invokeBukkitMethod(method, invoker, new Class<?>[0]);
    }

    public static Object newBukkit(String className) {
        return newBukkit(className, new Class<?>[0]);
    }

    public static Object newBukkit(String className, Class<?>[] parameterClasses, Object... params) {
        try {
            Class<?> clazz = getBukkitClass(className);
            Constructor<?> constructor = clazz.getConstructor(parameterClasses);
            constructor.setAccessible(true);
            return constructor.newInstance(params);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static <T> T getBukkitField(Object owner, String fieldName) {
        return getBukkitField(owner.getClass().getName().replace("org.bukkit.craftbukkit." + SERVER_VERSION + ".", ""), owner, fieldName);
    }

    public static <T> T getBukkitField(String className, Object owner, String fieldName) {
        try {
            Class<?> clazz = getBukkitClass(className);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            //noinspection unchecked
            return (T) field.get(owner);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static <T> T getBukkitStaticField(String className, String fieldName) {
        return getBukkitField(className, null, fieldName);
    }

    public static Class<?> getBukkitClass(String className) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + SERVER_VERSION + "." + className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    public static String getServerVersion() {
        return SERVER_VERSION;
    }
}
