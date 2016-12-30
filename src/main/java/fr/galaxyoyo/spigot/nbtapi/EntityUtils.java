package fr.galaxyoyo.spigot.nbtapi;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Entity;

import java.util.Arrays;
import java.util.Map;

import static fr.galaxyoyo.spigot.nbtapi.ReflectionUtils.*;

public class EntityUtils {
	/**
	 * Get the transformed TagCompound from the entity, with auto-updating
	 */
    public static TagCompound getTagCompound(Entity e) {
    	return getTagCompound(e, true);
	}

	/**
	 * Get the transformed TagCompound from the entity. If the parameter update equals true, every modification will be automatically updated. Don't use this if
	 * you want to do multiple modifications in one use, because it can generate some lags
	 */
    public static TagCompound getTagCompound(Entity e, boolean update) {
        Object nmsEntity = invokeBukkitMethod("getHandle", e);
        Object nbt = newNMS("NBTTagCompound");
        invokeNMSMethod("b", nmsEntity, new Class<?>[]{getNMSClass("NBTTagCompound")}, nbt);
        return TagCompound.fromNMS(nbt, update ? e : null);
    }

	/**
	 * Update the NBTTagCompound of the targetted entity
	 */
	public static void setTagCompound(Entity e, TagCompound tag) {
        Object nmsEntity = invokeBukkitMethod("getHandle", e);
        Object nbt = tag.convertToNMS();
        invokeNMSMethod("a", nmsEntity, new Class<?>[]{getNMSClass("NBTTagCompound")}, nbt);
    }

    /**
     * Get all base attributes of target entity. Don't take about modifiers.
     * @param e The entity to get attributes
     * @return Entity's attributes
     */
    public static Map<EntityAttributes, Double> getAllAttributes(Entity e) {
        Map<EntityAttributes, Double> map = Maps.newHashMap();
        TagCompound tag = getTagCompound(e);
        TagList attributesTag = tag.getList("Attributes");
        if (attributesTag == null)
            attributesTag = new TagList();
        for (Object o : attributesTag) {
            TagCompound attributeTag = (TagCompound) o;
            EntityAttributes attributes = EntityAttributes.getByName(attributeTag.getString("Name"));
            double value = attributeTag.getDouble("Base");
            map.put(attributes, value);
        }
        return map;
    }

	/**
	 * Get the specified attribute of targetted entity
	 */
    public static double getAttribute(Entity e, EntityAttributes attributes)
    {
        return getAllAttributes(e).getOrDefault(attributes, attributes.getDefaultValue());
    }

	/**
	 * Set all attributes for one entity
	 */
	public static void setAllAttributes(Entity e, Map<EntityAttributes, Double> allAttributes)
    {
        TagCompound tag = getTagCompound(e);
        TagList attributesTag = new TagList();
        for (Map.Entry<EntityAttributes, Double> entry : allAttributes.entrySet())
        {
            Validate.inclusiveBetween(entry.getKey().getMinimum(), entry.getKey().getMaximum(), entry.getValue(), entry.getKey().getAttributeName()
                    + " attribute must be inclusive between " + entry.getKey().getMinimum() + " and " + entry.getKey().getMaximum());
            TagCompound attributeTag = new TagCompound();
            attributeTag.setString("Name", entry.getKey().getAttributeName());
            attributeTag.setDouble("Base", entry.getValue());
            attributesTag.add(attributeTag);
        }
        tag.setList("Attributes", attributesTag);
        setTagCompound(e, tag);
    }

	/**
	 * Set one attribute for one targetted entity
	 */
    public static void setAttribute(Entity e, EntityAttributes attributes, double value)
    {
        Map<EntityAttributes, Double> map = getAllAttributes(e);
        map.put(attributes, value);
        setAllAttributes(e, map);
    }

	/**
	 * All entity attributes (update: 1.11)
	 */
	public enum EntityAttributes {
        /**
         * The maximum health of this mob (in half-hearts); determines the highest health they may be healed to.
         */
        MAX_HEALTH("generic.maxHealth", 20.0D, 0.0D, Double.MAX_VALUE),
        /**
         * The range in blocks within which a mob with this attribute will target players or other mobs to track. Exiting this range will cause the mob to cease following the player/mob. Actual value used by most mobs is 16; for zombies it is 40.
         */
        FOLLOW_RANGE("generic.followRange", 32.0D, 0.0D, 2048.0D),
        /**
         * The chance to resist knockback from attacks, explosions, and projectiles. 1.0 is 100% chance for resistance.
         */
        KNOCKBACK_RESISTANCE("generic.knockbackResistance", 0.0D, 0.0D, 1.0D),
        /**
         * Speed of movement in some unknown metric. The mob's maximum speed in blocks/second is a bit over 43 times this value, but can be affected by various conditions.
         */
        MOVEMENT_SPEED("generic.movementSpeed", 0.699999988079071D, 0.0D, Double.MAX_VALUE),
        /**
         * Damage dealt by attacks, in half-hearts.
         */
        ATTACK_DAMAGE("generic.attackDamage", 2.0D, 0.0D, Double.MAX_VALUE),
        /**
         * Armor defense points.<br>
         * 1.9+ only.
         */
        ARMOR("generic.armor", 0.0D, 0.0D, 30.0D),
        /**
         * Armor toughness.<br>
         * 1.9+ only.
         */
        ARMOR_TOUGHNESS("generic.armorToughness", 0.0D, 0.0D, 20.0D),
        /**
         * Determines speed at which attack strength recharges. Value is the number of full-strength attacks per second.<br>
         * for players only.<br>
         * 1.9+ only.
         */
        ATTACK_SPEED("generic.attackSpeed", 4.0D, 0.0D, 1024.0D),
        /**
         * Affects the results of loot tables using the <strong>quality</strong> or <strong>bonus_rolls</strong> tag (e.g. when opening chests or chest minecarts, fishing, and killing mobs).<br>
         * for players only.<br>
         * 1.9+ only.
         */
        LUCK("generic.luck", 0.0D, -1024.0D, 1024.0D),
        /**
         * Horse jump strength in some unknown metric.<br>
         * for horses only.
         */
        JUMP_STRENGTH("horse.jumpStrength", 0.69999999999999996D, 0.0D, 2.0D),
        /**
         * Chance that a zombie will spawn another zombie when attacked.<br>
         * for zombies only.
         */
        SPAWN_REINFORCEMENTS("zombie.spawnReinforcements", 0.0D, 0.0D, 1.0D);

        private static final Map<String, EntityAttributes> BY_NAME = Maps.newHashMap();
        private final String name;
        private final double defaultValue;
        private final double minimum;
        private final double maximum;

        EntityAttributes(String name, double defaultValue, double minimum, double maximum) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.minimum = minimum;
            this.maximum = maximum;
        }

        public static EntityAttributes getByName(String name) {
            if (BY_NAME.isEmpty())
                Arrays.stream(values()).forEach(attributes -> BY_NAME.put(attributes.getAttributeName(), attributes));
            return BY_NAME.get(name);
        }

        public String getAttributeName() {
            return name;
        }

        public double getDefaultValue() {
            return defaultValue;
        }

        public double getMinimum() {
            return minimum;
        }

        public double getMaximum() {
            return maximum;
        }
    }
}
