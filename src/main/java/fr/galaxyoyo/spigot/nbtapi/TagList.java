package fr.galaxyoyo.spigot.nbtapi;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fr.galaxyoyo.spigot.nbtapi.ReflectionUtils.*;

public class TagList extends ArrayList<Object> {
    public TagList() {
    }

    public TagList(Object... objs) {
        Arrays.stream(objs).forEach(this::add);
    }

    public TagList(Iterable objs) {
        //noinspection unchecked
        objs.forEach(this::add);
    }

    public static TagList fromNMS(Object nbt) {
        if (nbt == null)
            return new TagList();
        Validate.isTrue(nbt.getClass().getSimpleName().equalsIgnoreCase("NBTTagList"), "Only a NBTTagList can be transformed into a TagList");
        TagList tag = new TagList();
        List<?> list = getNMSField(nbt, "list");
        for (Object base : list) {
            if (base == null)
                continue;
            Object data;
            switch (base.getClass().getSimpleName()) {
                case "NBTTagEnd":
                    data = null;
                    break;
                case "NBTTagCompound":
                    data = TagCompound.fromNMS(base);
                    break;
                case "NBTTagList":
                    data = fromNMS(base);
                    break;
                default:
                    data = getNMSField(base, "data");
                    break;
            }
            tag.add(data);
        }
        return tag;
    }

    public Object convertToNMS() {
        Object tag = newNMS("NBTTagList");
        for (Object o : this) {
            Object data;
            if (o instanceof Integer)
                data = newNMS("NBTTagInt", new Class<?>[]{int.class}, o);
            else if (o instanceof Byte)
                data = newNMS("NBTTagByte", new Class<?>[]{byte.class}, o);
            else if (o instanceof Double)
                data = newNMS("NBTTagDouble", new Class<?>[]{double.class}, o);
            else if (o instanceof Float)
                data = newNMS("NBTTagFloat", new Class<?>[]{float.class}, o);
            else if (o instanceof Long)
                data = newNMS("NBTTagLong", new Class<?>[]{long.class}, o);
            else if (o instanceof Short)
                data = newNMS("NBTTagShort", new Class<?>[]{short.class}, o);
            else if (o instanceof String)
                data = newNMS("NBTTagString", new Class<?>[]{String.class}, o);
            else if (o instanceof int[])
                data = newNMS("NBTTagIntArray", new Class<?>[]{int[].class}, o);
            else if (o instanceof byte[])
                data = newNMS("NBTTagByteArray", new Class<?>[]{byte[].class}, o);
            else if (o instanceof TagCompound)
                data = ((TagCompound) o).convertToNMS();
            else if (o instanceof TagList)
                data = ((TagList) o).convertToNMS();
            else
                data = newNMS("NBTTagEnd");
            invokeNMSMethod("add", tag, new Class<?>[]{getNMSClass("NBTBase")}, data);
        }
        return tag;
    }
}
