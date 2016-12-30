package fr.galaxyoyo.spigot.nbtapi;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static fr.galaxyoyo.spigot.nbtapi.ReflectionUtils.*;

public class ItemStackUtils
{
	public static String toId(Material material)
	{
		Object registry = getNMSStaticField("Item", "REGISTRY");
		Object item = invokeBukkitStaticMethod("util.CraftMagicNumbers", "getItem", new Class<?>[]{Material.class}, material);
		Object mcKey = invokeNMSMethod(ReflectionUtils.getServerVersion().contains("1_8") ? "c" : "b", registry, new Class<?>[]{Object.class}, item);
		return "minecraft:" + (mcKey == null ? "null" : invokeNMSMethod("a", mcKey));
	}

	public static Material toMaterial(String id)
	{
		Object registry = getNMSStaticField("Item", "REGISTRY");
		Object mcKey = newNMS("MinecraftKey", new Class<?>[]{String.class}, id);
		Object item = invokeNMSMethod("get", registry, new Class<?>[]{Object.class}, mcKey);
		return (Material) invokeBukkitStaticMethod("util.CraftMagicNumbers", "getMaterial", new Class<?>[]{getNMSClass("Item")}, item);
	}

	public static TagCompound getAllStackCompound(ItemStack stack)
	{
		Object nbt = newNMS("NBTTagCompound");
		invokeNMSMethod("save", asNMSCopy(stack), new Class<?>[]{nbt.getClass()}, nbt);
		return TagCompound.fromNMS(nbt);
	}

	public static ItemStack fromTagCompound(TagCompound tag)
	{
		Object nms = invokeNMSStaticMethod("ItemStack", "createStack", new Class<?>[]{getNMSClass("NBTTagCompound")}, tag.convertToNMS());
		return asBukkitCopy(nms);
	}

	public static Object asNMSCopy(ItemStack stack)
	{
		return invokeBukkitStaticMethod("inventory.CraftItemStack", "asNMSCopy", new Class<?>[]{ItemStack.class}, stack);
	}

	public static ItemStack asBukkitCopy(Object object)
	{
		Validate.isTrue(object.getClass() == getNMSClass("ItemStack"), "Parameter must be a Notch ItemStack");
		return invokeBukkitStaticMethod("inventory.CraftItemStack", "asBukkitCopy", new Class<?>[]{getNMSClass("ItemStack")}, object);
	}

	public static List<Material> getCanDestroy(ItemStack stack)
	{
		TagCompound tag = getTagCompound(stack);
		TagList list = tag.getList("CanDestroy");
		if (list == null)
			return Lists.newArrayList();
		return toMaterials(list.stream().map((o) -> o.toString().substring(10)).collect(Collectors.toList()).toArray(new String[list.size()]));
	}

	public static TagCompound getTagCompound(ItemStack stack)
	{
		return TagCompound.fromNMS(invokeNMSMethod("getTag", asNMSCopy(stack)));
	}

	public static List<Material> toMaterials(String... ids)
	{
		return Arrays.stream(ids).map(ItemStackUtils::toMaterial).collect(Collectors.toList());
	}

	public static void setCanDestroy(ItemStack stack, Material... blocks)
	{
		List<String> ids = toIds(blocks);
		TagCompound tag = getTagCompound(stack);
		if (tag == null)
			tag = new TagCompound();
		tag.setList("CanDestroy", new TagList(ids));
		setTagCompound(stack, tag);
	}

	public static List<String> toIds(Material... materials)
	{
		return Arrays.stream(materials).map(ItemStackUtils::toId).collect(Collectors.toList());
	}

	public static void setTagCompound(ItemStack stack, TagCompound tag)
	{
		Object copy = asNMSCopy(stack);
		invokeNMSMethod("setTag", copy, new Class<?>[]{ReflectionUtils.getNMSClass("NBTTagCompound")}, tag.convertToNMS());
		stack.setItemMeta(invokeBukkitStaticMethod("inventory.CraftItemStack", "getItemMeta", new Class<?>[]{getNMSClass("ItemStack")}, copy));
	}

	public static List<Material> getCanPlaceOn(ItemStack stack)
	{
		TagCompound tag = getTagCompound(stack);
		TagList list = tag.getList("CanPlaceOn");
		if (list == null)
			return Lists.newArrayList();
		return toMaterials(list.stream().map((o) -> o.toString().substring(10)).collect(Collectors.toList()).toArray(new String[list.size()]));
	}

	public static void setCanPlaceOn(ItemStack stack, Material... blocks)
	{
		List<String> ids = toIds(blocks);
		TagCompound tag = getTagCompound(stack);
		if (tag == null)
			tag = new TagCompound();
		tag.setList("CanPlaceOn", new TagList(ids));
		setTagCompound(stack, tag);
	}
}
