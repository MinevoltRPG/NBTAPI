package fr.galaxyoyo.spigot.nbtapi.test;

import fr.galaxyoyo.spigot.nbtapi.ItemStackUtils;
import fr.galaxyoyo.spigot.nbtapi.TagCompound;
import junit.framework.TestCase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.Main;
import org.bukkit.inventory.ItemStack;

public class NBTAPITest extends TestCase
{
	public void test() throws Exception
	{
		Main.main(new String[0]);

		while (Bukkit.getServer() == null)
			Thread.sleep(200);

		ItemStack stack = new ItemStack(Material.BREWING_STAND);
		assertNotNull(stack);
		System.out.println(stack);
		net.minecraft.server.v1_11_R1.ItemStack nmsStack = (net.minecraft.server.v1_11_R1.ItemStack) ItemStackUtils.asNMSCopy(stack);
		assertNotNull(nmsStack);
		System.out.println(nmsStack);
		System.out.println(nmsStack.getTag());
		TagCompound compound = ItemStackUtils.getTagCompound(stack);
		System.out.println(compound);
	}
}