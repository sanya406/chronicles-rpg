package com.chroniclesrpg.client;

import com.chroniclesrpg.ChroniclesRpg;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = ChroniclesRpg.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
public final class ClientEvents {
    public static final KeyMapping OPEN_STATS = new KeyMapping(
            "key.chroniclesrpg.open_stats",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.categories.chroniclesrpg"
    );

    private ClientEvents() {}

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_STATS);
    }

    @Mod.EventBusSubscriber(modid = ChroniclesRpg.MODID, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
    public static class GameEvents {
        @SubscribeEvent
      TickEvent.ClientTickEvent event
            if (OPEN_STATS.consumeClick() && net.minecraft.client.Minecraft.getInstance().screen == null) {
                net.minecraft.client.Minecraft.getInstance().setScreen(new RpgScreen());
            }
        }
    }
}
