package fr.galaxyoyo.spigot.nbtapi;

import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.Set;

import static fr.galaxyoyo.spigot.nbtapi.ReflectionUtils.*;

@SuppressWarnings({"SameParameterValue", "unused"})
public class TagCompound extends HashMap<String, Object> {
    public static TagCompound fromNMS(Object o) {
        if (o == null)
            return null;
        Validate.isTrue(o.getClass().getSimpleName().equalsIgnoreCase("NBTTagCompound"), "Only a NBTTagCompound can be transformed into a TagCompound");
        TagCompound tag = new TagCompound();
        Set<String> keys = invokeNMSMethod("c", o);
        for (String key : keys) {
            Object base = invokeNMSMethod("get", o, new Class<?>[]{String.class}, key);
            Object data;
            switch (base.getClass().getSimpleName()) {
                case "NBTTagEnd":
                    data = null;
                    break;
                case "NBTTagCompound":
                    data = fromNMS(base);
                    break;
                case "NBTTagList":
                    data = TagList.fromNMS(base);
                    break;
                default:
                    data = getNMSField(base, "data");
                    break;
            }
            tag.put(key, data);
        }

        return tag;
    }

    public void setString(String key, String value) {
        put(key, value);
    }

    public void setInt(String key, int value) {
        put(key, value);
    }

    public void setByte(String key, byte value) {
        put(key, value);
    }

    public void setFloat(String key, float value) {
        put(key, value);
    }

    public void setDouble(String key, double value) {
        put(key, value);
    }

    public void setShort(String key, short value) {
        put(key, value);
    }

    public void setLong(String key, long value) {
        put(key, value);
    }

    public void setCompound(String key, TagCompound value) {
        put(key, value);
    }

    public void setList(String key, TagList value) {
        put(key, value);
    }

    public void setByteArray(String key, byte[] value) {
        put(key, value);
    }

    public void setIntArray(String key, int[] value) {
        put(key, value);
    }

    public void setEnd(String key) {
        put(key, null);
    }

    @Override
    public Object get(Object key) {
        return super.get(key);
    }

    public String getString(String key) {
        return (String) get(key);
    }

    public byte getByte(String key) {
        return (byte) get(key);
    }

    public int getInt(String key) {
        return (int) get(key);
    }

    public double getDouble(String key) {
        return (double) get(key);
    }

    public float getFloat(String key) {
        return (float) get(key);
    }

    public short getShort(String key) {
        return (short) get(key);
    }

    public long getLong(String key) {
        return (long) get(key);
    }

    public TagCompound getCompound(String key) {
        return (TagCompound) get(key);
    }

    public TagList getList(String key) {
        return (TagList) get(key);
    }

    public byte[] getByteArray(String key) {
        return (byte[]) get(key);
    }

    public int[] getIntArray(String key) {
        return (int[]) get(key);
    }

    public Object convertToNMS() {
        Object tag = newNMS("NBTTagCompound");
        for (Entry<String, Object> entry : entrySet()) {
            if (entry.getValue() == null)
                invokeNMSMethod("set", tag, new Class<?>[]{String.class, getNMSClass("NBTBase")}, entry.getKey(), newNMS("NBTTagEnd"));
            else if (entry.getValue() instanceof String)
                invokeNMSMethod("setString", tag, new Class<?>[]{String.class, String.class}, entry.getKey(), entry.getValue());
            else if (entry.getValue() instanceof Byte)
                invokeNMSMethod("setByte", tag, new Class<?>[]{String.class, byte.class}, entry.getKey(), entry.getValue());
            else if (entry.getValue() instanceof Short)
                invokeNMSMethod("setShort", tag, new Class<?>[]{String.class, short.class}, entry.getKey(), entry.getValue());
            else if (entry.getValue() instanceof Float)
                invokeNMSMethod("setFloat", tag, new Class<?>[]{String.class, float.class}, entry.getKey(), entry.getValue());
            else if (entry.getValue() instanceof Double)
                invokeNMSMethod("setDouble", tag, new Class<?>[]{String.class, double.class}, entry.getKey(), entry.getValue());
            else if (entry.getValue() instanceof Integer)
                invokeNMSMethod("setInt", tag, new Class<?>[]{String.class, int.class}, entry.getKey(), entry.getValue());
            else if (entry.getValue() instanceof Long)
                invokeNMSMethod("setLong", tag, new Class<?>[]{String.class, long.class}, entry.getKey(), entry.getValue());
            else if (entry.getValue() instanceof int[])
                invokeNMSMethod("setIntArray", tag, new Class<?>[]{String.class, int[].class}, entry.getKey(), entry.getValue());
            else if (entry.getValue() instanceof byte[])
                invokeNMSMethod("setByteArray", tag, new Class<?>[]{String.class, byte[].class}, entry.getKey(), entry.getValue());
            else if (entry.getValue() instanceof TagCompound)
                invokeNMSMethod("set", tag, new Class<?>[]{String.class, getNMSClass("NBTBase")}, entry.getKey(), ((TagCompound) entry.getValue()).convertToNMS());
            else if (entry.getValue() instanceof TagList)
                invokeNMSMethod("set", tag, new Class<?>[]{String.class, getNMSClass("NBTBase")}, entry.getKey(), ((TagList) entry.getValue()).convertToNMS());
        }
        return tag;
    }
}