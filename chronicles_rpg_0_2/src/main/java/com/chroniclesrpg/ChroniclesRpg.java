package com.chroniclesrpg;

import com.chroniclesrpg.client.ClientEvents;
import com.chroniclesrpg.command.RpgCommands;
import com.chroniclesrpg.data.RpgData;
import com.chroniclesrpg.network.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkRegistry;

@Mod(ChroniclesRpg.MODID)
public class ChroniclesRpg {
    public static final String MODID = "chroniclesrpg";
    public static final String PROTOCOL = "1";

    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals
    );

    public ChroniclesRpg() {
        NetworkHandler.register();
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
        MinecraftForge.EVENT_BUS.register(ServerTickHandler.class);
    }

    public static class CommonEvents {
        @SubscribeEvent
        public void registerCommands(RegisterCommandsEvent event) {
            RpgCommands.register(event.getDispatcher());
        }

        @SubscribeEvent
        public void clone(PlayerEvent.Clone event) {
            RpgData.copyFrom(event.getOriginal(), event.getEntity());
        }
    }
}
