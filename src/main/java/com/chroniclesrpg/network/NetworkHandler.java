package com.chroniclesrpg.network;

import com.chroniclesrpg.ChroniclesRpg;
import com.chroniclesrpg.data.RpgData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class NetworkHandler {
    private static int id = 0;

    private NetworkHandler() {}

    public static void register() {
        ChroniclesRpg.NETWORK.registerMessage(id++, SpendStatPacket.class,
                SpendStatPacket::encode, SpendStatPacket::decode, SpendStatPacket::handle);
        ChroniclesRpg.NETWORK.registerMessage(id++, UnlockSkillPacket.class,
                UnlockSkillPacket::encode, UnlockSkillPacket::decode, UnlockSkillPacket::handle);
    }

    public static void spendStat(int index) {
        ChroniclesRpg.NETWORK.sendToServer(new SpendStatPacket(index));
    }

    public static void unlockSkill(String skillId) {
        ChroniclesRpg.NETWORK.sendToServer(new UnlockSkillPacket(skillId));
    }

    public record SpendStatPacket(int index) {
        static void encode(SpendStatPacket msg, FriendlyByteBuf buf) { buf.writeInt(msg.index); }
        static SpendStatPacket decode(FriendlyByteBuf buf) { return new SpendStatPacket(buf.readInt()); }
        static void handle(SpendStatPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player != null) RpgData.get(player).spendStatPoint(msg.index);
            });
            ctx.get().setPacketHandled(true);
        }
    }

    public record UnlockSkillPacket(String skillId) {
        static void encode(UnlockSkillPacket msg, FriendlyByteBuf buf) { buf.writeUtf(msg.skillId, 64); }
        static UnlockSkillPacket decode(FriendlyByteBuf buf) { return new UnlockSkillPacket(buf.readUtf(64)); }
        static void handle(UnlockSkillPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player != null) {
                    RpgData data = RpgData.get(player);
                    if (com.chroniclesrpg.data.SkillCatalog.forClass(data.classId())
                            .stream().anyMatch(s -> s.id().equals(msg.skillId))) {
                        data.unlockSkill(msg.skillId);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
