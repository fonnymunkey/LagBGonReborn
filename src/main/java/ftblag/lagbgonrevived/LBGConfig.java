package ftblag.lagbgonrevived;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LBGConfig {

    public static Configuration cfg;

    public static List<String> entityBlacklist = new ArrayList<>();
    public static List<String> itemBlacklist = new ArrayList<>();
    public static int entityInterval, TPSForUnload, crowdLimit, perChunkSpawnLimit, unloadInterval;
    public static boolean automaticRemoval, policeCrowd, toggleItemBlacklist, toggleEntityBlacklist, effectsOnSP;

    public static void init(Configuration cfg) {
        LBGConfig.cfg = cfg;
    }

    public static void load() {
        cfg.load();
        entityBlacklist = new ArrayList<>(Arrays.asList(cfg.get(Configuration.CATEGORY_GENERAL, "EntityBlackList", new String[]{
                "minecraft:villager",
                "minecraft:pig",
                "minecraft:chicken",
                "minecraft:rabbit",
                "minecraft:sheep",
                "minecraft:cow",
                "minecraft:armor_stand",
                "minecraft:item_frame",
                "minecraft:horse",
                "minecraft:donkey",
                "minecraft:witch",
                "minecraft:wolf",
                "minecraft:painting",
                "minecraft:villager_golem",
                "minecraft:ender_dragon",
                "minecraft:ender_crystal",
                "minecraft:wither",
                "minecraft:snowman",
                "minecraft:shulker",
                "minecraft:skeleton_horse",
                "minecraft:polar_bear",
                "minecraft:zombie_horse",
                "minecraft:mooshroom",
                "minecraft:parrot",
                "minecraft:ocelot",
                "minecraft:llama",
                "minecraft:boat",
                "minecraft:minecart",
                "minecraft:chest_minecart",
                "minecraft:hopper_minecart",
                "net.minecraft.entity.projectile.EntityFishHook",
        }, "List of Entities not to destroy, use namespace:* to blacklist a whole mod.").getStringList()));
        itemBlacklist = new ArrayList<>(Arrays.asList(cfg.get(Configuration.CATEGORY_GENERAL, "ItemBlackList", new String[]{""}, "List of Items not to destroy, use namespace:* to blacklist a whole mod.").getStringList()));
        entityInterval = cfg.get(Configuration.CATEGORY_GENERAL, "EntityInterval", 15, "Interval between clearing entities in minutes. The interval is actually 1 minute longer, as it includes a 1 minute warning.").getInt();
        automaticRemoval = cfg.get(Configuration.CATEGORY_GENERAL, "AutomaticRemoval", true, "Automatically run clear entity and unload chunk checks").getBoolean();
        effectsOnSP = cfg.get(Configuration.CATEGORY_GENERAL, "EffectsOnSP", false, "Run all non-command effects even when not on a dedicated server").getBoolean();
        TPSForUnload = cfg.get(Configuration.CATEGORY_GENERAL, "TPSForUnload", 12, "If the server's main TPS drops below this number Lag'B'Gon will try to unload chunks to improve TPS").getInt();
        crowdLimit = cfg.get("Breeding", "CrowdLimit", 20, "If at least this amount of breedable animals are within five blocks, new babies will not spawn.").getInt();
        policeCrowd = cfg.get("Breeding", "PoliceCrowding", true, "Prevents overbreeding.").getBoolean();
        perChunkSpawnLimit = cfg.get(Configuration.CATEGORY_GENERAL, "PerChunkSpawnLimit", 100, "Maximum mobs spawnable per chunk. 0 disables.").getInt();
        toggleItemBlacklist = cfg.get(Configuration.CATEGORY_GENERAL, "ToggleItemBlacklist", true, "If false, treats the item blacklist as a whitelist").getBoolean();
        toggleEntityBlacklist = cfg.get(Configuration.CATEGORY_GENERAL, "ToggleEntityBlacklist", true, "If false, treats the entity blacklist as a whitelist").getBoolean();
        unloadInterval = cfg.get(Configuration.CATEGORY_GENERAL, "UnloadInterval", 15, "Interval between unloading chunks in minutes.").getInt();
        cfg.save();
    }

    public static void toggleAuto() {
        automaticRemoval = !automaticRemoval;
        save();
    }

    public static void toggleEffectSP() {
        effectsOnSP = !effectsOnSP;
        save();
    }

    public static void toggleItemBlacklist() {
        toggleItemBlacklist = !toggleItemBlacklist;
        save();
    }

    public static void toggleEntityBlacklist() {
        toggleEntityBlacklist = !toggleEntityBlacklist;
        save();
    }

    public static void changeMaxPerChunk(int newMax) {
        if(newMax < 0) newMax = 0;
        perChunkSpawnLimit = newMax;
        save();
    }

    public static void changeCrowdLimit(int newLimit) {
        if(newLimit < 0) newLimit = 0;
        crowdLimit = newLimit;
        save();
    }

    public static void changeEntityInterval(int newInterval) {
        if(newInterval < 1) newInterval = 1;
        entityInterval = newInterval;
        save();
    }

    public static void changeUnloadInterval(int newInterval) {
        if(newInterval < 1) newInterval = 1;
        unloadInterval = newInterval;
        save();
    }

    public static void changeTPSForUnload(int newTPS) {
        TPSForUnload = Math.min(Math.max(newTPS, 1), 15);
        save();
    }

    public static void togglePolice() {
        policeCrowd = !policeCrowd;
        save();
    }

    public static void toggleItem(String name) {
        if(itemBlacklist.contains(name)) itemBlacklist.remove(name);
        else itemBlacklist.add(name);
        save();
    }

    public static void toggleEntity(String name) {
        if(entityBlacklist.contains(name)) entityBlacklist.remove(name);
        else entityBlacklist.add(name);
        save();
    }

    public static boolean isBlacklisted(Item item, boolean strict) {
        ResourceLocation name = item.getRegistryName();
        return itemBlacklist.contains(name.toString()) || (!strict && itemBlacklist.contains(name.getNamespace() + ":*"));
    }

    public static boolean isBlacklisted(Entity entity, boolean strict) {
        if(entity == null) return false;
        ResourceLocation rl = EntityList.getKey(entity);
        if(rl != null) return entityBlacklist.contains(rl.toString()) || (!strict && entityBlacklist.contains(rl.getNamespace() + ":*"));
        else return entityBlacklist.contains(entity.getClass().getName());
    }

    public static boolean isBlacklisted(String name, boolean entity) {
        return entity ? entityBlacklist.contains(name) : itemBlacklist.contains(name);
    }

    public static void save() {
        cfg.get(Configuration.CATEGORY_GENERAL, "EntityBlackList", new String[]{
                "minecraft:villager",
                "minecraft:pig",
                "minecraft:chicken",
                "minecraft:rabbit",
                "minecraft:sheep",
                "minecraft:cow",
                "minecraft:armor_stand",
                "minecraft:item_frame",
                "minecraft:horse",
                "minecraft:donkey",
                "minecraft:witch",
                "minecraft:wolf",
                "minecraft:painting",
                "minecraft:villager_golem",
                "minecraft:ender_dragon",
                "minecraft:ender_crystal",
                "minecraft:wither",
                "minecraft:snowman",
                "minecraft:shulker",
                "minecraft:skeleton_horse",
                "minecraft:polar_bear",
                "minecraft:zombie_horse",
                "minecraft:mooshroom",
                "minecraft:parrot",
                "minecraft:ocelot",
                "minecraft:llama",
                "minecraft:boat",
                "minecraft:minecart",
                "minecraft:chest_minecart",
                "minecraft:hopper_minecart",
                "net.minecraft.entity.projectile.EntityFishHook",
        }, "List of Entities not to destroy, use namespace:* to blacklist a whole mod.").set(entityBlacklist.toArray(new String[0]));
        cfg.get(Configuration.CATEGORY_GENERAL, "ItemBlackList", new String[]{""}, "List of Items not to destroy, use namespace:* to blacklist a whole mod.").set(itemBlacklist.toArray(new String[0]));
        cfg.get(Configuration.CATEGORY_GENERAL, "EntityInterval", 15, "Interval between clearing entities in minutes. The interval is actually 1 minute longer, as it includes a 1 minute warning.").set(entityInterval);
        cfg.get(Configuration.CATEGORY_GENERAL, "AutomaticRemoval", true, "Automatically run clear entity and unload chunk checks").set(automaticRemoval);
        cfg.get(Configuration.CATEGORY_GENERAL, "EffectsOnSP", false, "Run all non-command effects even when not on a dedicated server").set(effectsOnSP);
        cfg.get(Configuration.CATEGORY_GENERAL, "TPSForUnload", 12, "If the server's main TPS drops below this number Lag'B'Gon will try to unload chunks to improve TPS").set(TPSForUnload);
        cfg.get("Breeding", "CrowdLimit", 20, "If at least this amount of breedable animals are within five blocks, new babies will not spawn.").set(crowdLimit);
        cfg.get("Breeding", "PoliceCrowding", true, "Prevents overbreeding.").set(policeCrowd);
        cfg.get(Configuration.CATEGORY_GENERAL, "PerChunkSpawnLimit", 100, "Maximum mobs spawnable per chunk. 0 disables.").set(perChunkSpawnLimit);
        cfg.get(Configuration.CATEGORY_GENERAL, "ToggleItemBlacklist", true, "If false, treats the item blacklist as a whitelist").set(toggleItemBlacklist);
        cfg.get(Configuration.CATEGORY_GENERAL, "ToggleEntityBlacklist", true, "If false, treats the entity blacklist as a whitelist").set(toggleEntityBlacklist);
        cfg.get(Configuration.CATEGORY_GENERAL, "UnloadInterval", 15, "Interval between unload chunks in minutes.").set(unloadInterval);
        cfg.save();
    }
}
