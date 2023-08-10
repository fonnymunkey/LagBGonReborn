package ftblag.lagbgonrevived;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = LagBGonRevived.MODID, name = "Lag'B'Gon Revived", version = LagBGonRevived.VERSION, acceptableRemoteVersions = "*")
public class LagBGonRevived {
    public static final String MODID = "lagbgonrevived";
    public static final String VERSION = "1.1.0";

    @Mod.Instance(MODID)
    public static LagBGonRevived instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LBGConfig.init(new Configuration(event.getSuggestedConfigurationFile()));
        LBGConfig.load();
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new LBGCommand());
    }

    public static void sendMsg(ICommandSender sender, String str) {
        sender.sendMessage(new TextComponentString(str));
    }
}