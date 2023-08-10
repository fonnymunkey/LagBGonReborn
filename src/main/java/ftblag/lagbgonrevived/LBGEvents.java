package ftblag.lagbgonrevived;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

@Mod.EventBusSubscriber(modid = LagBGonRevived.MODID)
public class LBGEvents {

    public static long nextUnload;
    public static long nextClear;
    private static boolean warned = false;
    private static long tick = 0;

    @SubscribeEvent
    public static void onEntitySpawn(LivingSpawnEvent.CheckSpawn e) {
        if(e.getEntity() == null || e.getWorld().isRemote) return;
        if(!LBGConfig.effectsOnSP && FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getCurrentPlayerCount() <= 1) return;
        Entity ent = e.getEntity();
        if(ent.addedToChunk || !ent.isNonBoss() || ent.hasCustomName() || (ent instanceof EntityTameable && ((EntityTameable)ent).isTamed())) return;
        if(LBGConfig.perChunkSpawnLimit > 0 && ent instanceof EntityLiving) {
            BlockPos pos = ent.getPosition();
            if(e.getWorld().isBlockLoaded(pos)) {//Don't try to get and load the chunk if its trying to spawn something in an unloaded chunk for whatever reason
                Chunk chunk = e.getWorld().getChunk(pos);
                int count = 0;
                for(ClassInheritanceMultiMap<Entity> list : chunk.getEntityLists()) {
                    count += list.size();
                }
                if(count >= LBGConfig.perChunkSpawnLimit) {
                    e.setResult(Event.Result.DENY);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBabySpawn(BabyEntitySpawnEvent e) {
        if(e.getChild() == null || e.getChild().world.isRemote) return;
        if(!LBGConfig.effectsOnSP && FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getCurrentPlayerCount() <= 1) return;

        EntityAgeable ent = e.getChild();
        if(LBGConfig.policeCrowd) {
            if(LBGConfig.crowdLimit <= 1) {
                e.setCanceled(true);
                return;
            }
            int amt = ent.world.getEntitiesWithinAABB(EntityAgeable.class, new AxisAlignedBB(ent.getPosition()).grow(5)).size();
            if(amt > LBGConfig.crowdLimit) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent event) {
        if(event.phase == Phase.START || !LBGConfig.automaticRemoval) return;
        if(!LBGConfig.effectsOnSP && FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getCurrentPlayerCount() <= 1) return;
        tick++;
        if(tick%200 == 0) {//Lower checks to every 10 seconds
            LBGCommand.checkTPS();
            if(LBGEvents.nextClear <= 0) LBGEvents.nextClear = System.currentTimeMillis() + ((long)LBGConfig.entityInterval * 1000 * 60);
            else if(LBGEvents.nextClear < System.currentTimeMillis()) {
                if(warned) {
                    LBGCommand.doClear();
                    warned = false;
                }
                else {
                    LBGEvents.nextClear = System.currentTimeMillis() + 1000 * 60;
                    FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendMessage(new TextComponentString(TextFormatting.RED.toString() + TextFormatting.BOLD.toString() + "Lag'B'Gon will be clearing entities in 1 minute!"));
                    warned = true;
                }
            }
        }
    }
}
