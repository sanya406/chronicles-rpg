package com.chroniclesrpg.data;

import com.chroniclesrpg.ChroniclesRpg;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;

public class RpgData implements INBTSerializable<CompoundTag> {
    public static final Capability<RpgData> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static final String[] STATS = {"strength", "agility", "endurance", "intelligence", "will"};
    private int level = 1;
    private int xp = 0;
    private int skillPoints = 0;
    private String classId = "unknown";

    private final int[] stats = {0, 0, 0, 0, 0};
    private final Set<String> skills = new HashSet<>();

    public int level() { return level; }
    public int xp() { return xp; }
    public int skillPoints() { return skillPoints; }
    public String classId() { return classId; }
    public int stat(int index) { return index >= 0 && index < stats.length ? stats[index] : 0; }
    public boolean hasSkill(String id) { return skills.contains(id); }

    public void setClassId(String id) { classId = id == null ? "unknown" : id; }
    public int xpToNext() { return 100 + (level - 1) * 25; }

    public void addXp(Player player, int amount) {
        if (amount <= 0) return;
        xp += amount;
        while (xp >= xpToNext()) {
            xp -= xpToNext();
            level++;
            skillPoints++;
        }
        if (player instanceof ServerPlayer sp) sync(sp);
    }

    public boolean spendSkillPoint() {
        if (skillPoints <= 0) return false;
        skillPoints--;
        return true;
    }

    public boolean spendStatPoint(int index) {
        if (skillPoints <= 0 || index < 0 || index >= stats.length) return false;
        skillPoints--;
        stats[index]++;
        return true;
    }

    public boolean unlockSkill(String id) {
        if (id == null || skills.contains(id) || skillPoints <= 0) return false;
        skillPoints--;
        skills.add(id);
        return true;
    }

    public void sync(ServerPlayer player) {
        // Client GUI reads fresh values when opened. Network packets are used for changes.
    }

    @Override public CompoundTag serializeNBT() {
        CompoundTag t = new CompoundTag();
        t.putInt("Level", level);
        t.putInt("XP", xp);
        t.putInt("SkillPoints", skillPoints);
        t.putString("Class", classId);

        CompoundTag statTag = new CompoundTag();
        for (int i = 0; i < stats.length; i++) statTag.putInt(STATS[i], stats[i]);
        t.put("Stats", statTag);

        CompoundTag skillTag = new CompoundTag();
        for (String skill : skills) skillTag.putBoolean(skill, true);
        t.put("Skills", skillTag);
        return t;
    }

    @Override public void deserializeNBT(CompoundTag t) {
        level = Math.max(1, t.getInt("Level"));
        xp = Math.max(0, t.getInt("XP"));
        skillPoints = Math.max(0, t.getInt("SkillPoints"));
        classId = t.getString("Class");
        if (classId.isEmpty()) classId = "unknown";

        CompoundTag statTag = t.getCompound("Stats");
        for (int i = 0; i < stats.length; i++) stats[i] = Math.max(0, statTag.getInt(STATS[i]));

        skills.clear();
        CompoundTag skillTag = t.getCompound("Skills");
        for (String key : skillTag.getAllKeys()) if (skillTag.getBoolean(key)) skills.add(key);
    }

    public static RpgData get(Player p) {
        return p.getCapability(CAPABILITY).orElseThrow(() ->
                new IllegalStateException("Chronicles RPG capability missing"));
    }

    public static void copyFrom(Player oldPlayer, Player newPlayer) {
        oldPlayer.reviveCaps();
        oldPlayer.getCapability(CAPABILITY).ifPresent(oldData ->
                newPlayer.getCapability(CAPABILITY).ifPresent(newData ->
                        newData.deserializeNBT(oldData.serializeNBT())));
        oldPlayer.invalidateCaps();
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final RpgData data = new RpgData();
        private final LazyOptional<RpgData> optional = LazyOptional.of(() -> data);

        @Override public <T> LazyOptional<T> getCapability(Capability<T> cap, net.minecraft.core.Direction side) {
            return cap == CAPABILITY ? optional.cast() : LazyOptional.empty();
        }
        @Override public CompoundTag serializeNBT() { return data.serializeNBT(); }
        @Override public void deserializeNBT(CompoundTag tag) { data.deserializeNBT(tag); }
    }

    public static class Events {
        @SubscribeEvent
        public void attach(AttachCapabilitiesEvent<Player> e) {
            e.addCapability(new net.minecraft.resources.ResourceLocation(ChroniclesRpg.MODID, "rpg_data"),
                    new Provider());
        }

        @SubscribeEvent
        public void clone(PlayerEvent.Clone e) {
            RpgData.copyFrom(e.getOriginal(), e.getEntity());
        }
    }

    static {
        MinecraftForge.EVENT_BUS.register(new Events());
    }
}
