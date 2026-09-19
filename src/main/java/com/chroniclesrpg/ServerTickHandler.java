package com.chroniclesrpg;

import com.chroniclesrpg.data.ClassDetector;
import com.chroniclesrpg.data.RpgData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ServerTickHandler {
    private ServerTickHandler() {}

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        if (!(event.player instanceof ServerPlayer p)) return;
        if (p.tickCount % 40 != 0) return;
        RpgData data = RpgData.get(p);
        String detected = ClassDetector.detect(p);
        if (!detected.equals(data.classId())) data.setClassId(detected);
    }
}
