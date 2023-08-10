package ftblag.lagbgonrevived;

import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.*;

public class LBGCommand extends CommandBase {

    private static final String[] al = new String[]{"toggleitem", "toggleentity", "clear", "unload", "toggleauto", "togglededi", "settps", "itemblacklist", "entityblacklist", "togglepolice", "setbreedlimit", "maxperchunk"};

    @Override
    public String getName() {
        return "bgon";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/bgon : Shows help for using Lag'B'Gon Revived";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        if(args.length == 1) return getListOfStringsMatchingLastWord(args, al);
        else if(args.length == 2 && args[0].equals("toggleentity")) return getListOfStringsMatchingLastWord(args, EntityList.getEntityNameList());
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            LagBGonRevived.sendMsg(sender, "/bgon toggleitem: Toggles the blacklist status of held item.");
            LagBGonRevived.sendMsg(sender, "/bgon toggleitem <modid:name>: Toggles the blacklist status of the named item.");
            LagBGonRevived.sendMsg(sender, "/bgon toggleentity <modid:name>: Toggles the blacklist status of the named entity.");
            LagBGonRevived.sendMsg(sender, "/bgon clear: Clears all items/entities from the world not on blacklist.");
            LagBGonRevived.sendMsg(sender, "/bgon clear <minutes>: Sets the interval for automatic running of /bgon clear.");
            LagBGonRevived.sendMsg(sender, "/bgon unload: Unloads unused chunks.");
            LagBGonRevived.sendMsg(sender, "/bgon unload <minutes>: Sets the interval for automatic running of /bgon unload.");
            LagBGonRevived.sendMsg(sender, "/bgon toggleauto: Toggles automatic clearing of entities, and unloading of chunks.");
            LagBGonRevived.sendMsg(sender, "/bgon togglededi: Toggles all non-command effects when not on a dedicated server.");
            LagBGonRevived.sendMsg(sender, "/bgon settps <target tps>: Sets the maximum TPS for unloading chunks.");
            LagBGonRevived.sendMsg(sender, "/bgon itemblacklist: Switches between using blacklist and whitelist for items.");
            LagBGonRevived.sendMsg(sender, "/bgon entityblacklist: Switches between using blacklist and whitelist for entities.");
            LagBGonRevived.sendMsg(sender, "/bgon togglepolice: Toggles Breeding policing.");
            LagBGonRevived.sendMsg(sender, "/bgon setbreedlimit <amount>: Sets the limit for breeding.");
            LagBGonRevived.sendMsg(sender, "/bgon maxperchunk <amount>: Sets maximum entities to spawn per chunk.");
        }
        else if(args.length == 1) {
            switch(args[0]) {
                case "toggleitem":
                    if(!(sender instanceof EntityPlayer)) {
                        LagBGonRevived.sendMsg(sender, "Only for players!");
                        break;
                    }
                    EntityPlayer plr = (EntityPlayer)sender;
                    if(plr.getHeldItemMainhand().isEmpty()) {
                        LagBGonRevived.sendMsg(sender, "You must have an item held");
                        break;
                    }
                    Item item = plr.getHeldItemMainhand().getItem();
                    String name = item.getRegistryName().toString();
                    LBGConfig.toggleItem(name);
                    boolean added = LBGConfig.isBlacklisted(item, true);
                    LagBGonRevived.sendMsg(sender, name + (added ? " added to" : " removed from") + " blacklist.");
                    break;
                case "clear":
                    doClear();
                    break;
                case "unload":
                    unloadChunks();
                    break;
                case "toggleauto":
                    LBGConfig.toggleAuto();
                    LagBGonRevived.sendMsg(sender, "Automatic clearing " + (LBGConfig.automaticRemoval ? "en" : "dis") + "abled.");
                    break;
                case "togglededi":
                    LBGConfig.toggleEffectSP();
                    LagBGonRevived.sendMsg(sender, "Non-Dedicated Server effects " + (LBGConfig.effectsOnSP ? "en" : "dis") + "abled.");
                    break;
                case "itemblacklist":
                    LBGConfig.toggleItemBlacklist();
                    LagBGonRevived.sendMsg(sender, "Item " + (LBGConfig.toggleItemBlacklist ? "Black" : "White") + "list enabled.");
                    break;
                case "entityblacklist":
                    LBGConfig.toggleEntityBlacklist();
                    LagBGonRevived.sendMsg(sender, "Entity " + (LBGConfig.toggleEntityBlacklist ? "Black" : "White") + "list enabled.");
                    break;
                case "togglepolice":
                    LBGConfig.togglePolice();
                    LagBGonRevived.sendMsg(sender, "Breeding policing " + (LBGConfig.policeCrowd ? "en" : "dis") + "abled.");
                    break;
                default:
                    LagBGonRevived.sendMsg(sender, "Cmd not found!");
                    break;
            }
        }
        else if(args.length == 2) {
            switch(args[0]) {
                case "toggleitem":
                    LBGConfig.toggleItem(args[1]);
                    LagBGonRevived.sendMsg(sender, args[1] + " has been " + (LBGConfig.isBlacklisted(args[1], false) ? "added to" : "removed from") + " the item blacklist.");
                    break;
                case "toggleentity":
                    LBGConfig.toggleEntity(args[1]);
                    LagBGonRevived.sendMsg(sender, args[1] + " has been " + (LBGConfig.isBlacklisted(args[1], true) ? "added to" : "removed from") + " the entity blacklist.");
                    break;
                case "clear": {
                    int clearInter = parseIntElse(args[1], 0);
                    LBGConfig.changeEntityInterval(clearInter);
                    LBGEvents.nextClear = System.currentTimeMillis() + ((long)LBGConfig.entityInterval * 1000 * 60);
                    LagBGonRevived.sendMsg(sender, "Automatic entity clear interval set to: " + LBGConfig.entityInterval);
                    break;
                }
                case "unload": {
                    int unloadInter = parseIntElse(args[1], 0);
                    LBGConfig.changeUnloadInterval(unloadInter);
                    LBGEvents.nextUnload = System.currentTimeMillis() + ((long)LBGConfig.unloadInterval * 1000 * 60);
                    LagBGonRevived.sendMsg(sender, "Automatic chunk unload interval set to: " + LBGConfig.unloadInterval);
                    break;
                }
                case "settps":
                    int newTPS = parseIntElse(args[1], 0);
                    LBGConfig.changeTPSForUnload(newTPS);
                    LagBGonRevived.sendMsg(sender, "TPS threshold set to: " + LBGConfig.TPSForUnload);
                    break;
                case "setbreedlimit":
                    int limit = parseIntElse(args[1], 0);
                    LBGConfig.changeCrowdLimit(limit);
                    LagBGonRevived.sendMsg(sender, "Breeding limit set to: " + LBGConfig.crowdLimit);
                    break;
                case "maxperchunk":
                    int max = parseIntElse(args[1], 0);
                    LBGConfig.changeMaxPerChunk(max);
                    LagBGonRevived.sendMsg(sender, "Spawns per chunk limit set to: " + LBGConfig.perChunkSpawnLimit);
                    break;
                default:
                    LagBGonRevived.sendMsg(sender, "Cmd not found!");
                    break;
            }
        }
        else LagBGonRevived.sendMsg(sender, "Cmd not found!");
    }

    public static void doClear() {
        int itemsRemoved = 0;
        int entitiesRemoved = 0;
        int totalItems = 0;
        int totalEntities = 0;
        ArrayList<Entity> toRemove = new ArrayList<>();
        for(World world : DimensionManager.getWorlds()) {
            if(world == null || world.isRemote) continue;
            Iterator<Entity> iter = world.loadedEntityList.iterator();
            Entity obj;
            while(iter.hasNext()) {
                obj = iter.next();
                if(obj instanceof EntityItem) {
                    totalItems++;
                    if(LBGConfig.toggleItemBlacklist != LBGConfig.isBlacklisted(((EntityItem)obj).getItem().getItem(), false)) {
                        if(obj.ticksExisted < 1200 || obj.hasCustomName()) continue;
                        toRemove.add(obj);
                        itemsRemoved++;
                    }
                }
                else if(!(obj instanceof EntityPlayer)) {
                    totalEntities++;
                    if(LBGConfig.toggleEntityBlacklist != LBGConfig.isBlacklisted(obj, false)) {
                        if(obj.ticksExisted < 1200 || !obj.isNonBoss() || obj.hasCustomName() || (obj instanceof EntityTameable && ((EntityTameable)obj).isTamed())) continue;
                        toRemove.add(obj);
                        entitiesRemoved++;
                    }
                }
            }
            for(Entity e : toRemove) {
                e.setDead();
            }
            toRemove.clear();
        }
        LBGEvents.nextClear = System.currentTimeMillis() + ((long)LBGConfig.entityInterval * 1000 * 60);
        FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendMessage(new TextComponentString(TextFormatting.GREEN.toString() + "Lag'B'Gon has removed " + itemsRemoved + "/" + totalItems + " items and " + entitiesRemoved + "/" + totalEntities + " entities."));
    }

    private static int parseIntElse(String in, int def) {
        try { return Integer.parseInt(in); }
        catch(NumberFormatException e) { return def; }
    }

    private static long mean(long[] num) {
        long val = 0;
        for(long n : num) val += n;
        return val / num.length;
    }

    private static void unloadChunks() {
        List<ChunkPos> playerPos = Lists.newArrayList();
        int radius = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getViewDistance();
        for(EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
            for(int x = player.chunkCoordX - radius; x <= player.chunkCoordX + radius; x++) {
                for(int z = player.chunkCoordZ - radius; z <= player.chunkCoordZ + radius; z++) {
                    playerPos.add(new ChunkPos(x, z));
                }
            }
        }

        int chunksQueued = 0;
        for(WorldServer world : DimensionManager.getWorlds()) {
            ChunkProviderServer cPS = world.getChunkProvider();
            for(Chunk chunk : cPS.loadedChunks.values()) {
                ChunkPos chunkPos = chunk.getPos();
                if(!world.getPersistentChunks().containsKey(chunkPos)
                        && !playerPos.contains(chunkPos)
                        && world.provider.canDropChunk(chunkPos.x, chunkPos.z)) {
                    chunksQueued++;
                    cPS.queueUnload(chunk);
                }
            }
        }
        playerPos.clear();
        LBGEvents.nextUnload = System.currentTimeMillis() + ((long)LBGConfig.unloadInterval * 1000 * 60);
        FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendMessage(new TextComponentString(TextFormatting.GREEN.toString() + "Lag'B'Gon queued " + chunksQueued + " to be unloaded."));
    }

    public static void checkTPS() {
        if(LBGEvents.nextUnload <= 0) LBGEvents.nextUnload = System.currentTimeMillis() + ((long)LBGConfig.unloadInterval * 1000 * 60);
        else if(LBGEvents.nextUnload < System.currentTimeMillis()) {
            double meanTickTime = mean(FMLCommonHandler.instance().getMinecraftServerInstance().tickTimeArray) * 1.0E-6D;
            double meanTPS = Math.min(1000.0 / meanTickTime, 20);
            if(meanTPS < LBGConfig.TPSForUnload) {
                unloadChunks();
            }
        }
    }
}
