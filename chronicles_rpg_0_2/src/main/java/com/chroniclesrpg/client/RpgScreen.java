package com.chroniclesrpg.client;

import com.chroniclesrpg.data.RpgData;
import com.chroniclesrpg.data.SkillCatalog;
import com.chroniclesrpg.network.NetworkHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class RpgScreen extends Screen {
    private final List<SkillCatalog.Skill> skills;

    public RpgScreen() {
        super(Component.translatable("screen.chroniclesrpg.title"));
        this.skills = SkillCatalog.forClass("unknown");
    }

    private RpgData data() {
        return RpgData.get(minecraft.player);
    }

    @Override protected void init() {
        super.init();
        rebuildButtons();
    }

    private void rebuildButtons() {
        clearWidgets();
        if (minecraft == null || minecraft.player == null) return;

        RpgData data = data();
        int left = width / 2 - 190;
        int top = 38;

        for (int i = 0; i < 5; i++) {
            final int index = i;
            addRenderableWidget(Button.builder(
                    Component.literal("+"),
                    b -> {
                        NetworkHandler.spendStat(index);
                        minecraft.tell(this::rebuildButtons);
                    }).bounds(left + 170, top + i * 24, 20, 20).build());
        }

        List<SkillCatalog.Skill> classSkills = SkillCatalog.forClass(data.classId());
        for (int i = 0; i < classSkills.size(); i++) {
            SkillCatalog.Skill skill = classSkills.get(i);
            addRenderableWidget(Button.builder(
                    Component.literal(data.hasSkill(skill.id()) ? "Изучено" : "Изучить"),
                    b -> {
                        NetworkHandler.unlockSkill(skill.id());
                        minecraft.tell(this::rebuildButtons);
                    }).bounds(width / 2 + 25, 115 + i * 52, 90, 22).build());
        }
    }

    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        RpgData data = data();

        int left = width / 2 - 190;
        int top = 18;

        g.drawString(font, "CHRONICLES RPG", left, top, 0xFFFFFF);
        g.drawString(font, "Класс: " + className(data.classId()), left, top + 14, 0xB0B0FF);
        g.drawString(font, "Уровень: " + data.level(), left, top + 32, 0xFFFFFF);
        g.drawString(font, "XP: " + data.xp() + " / " + data.xpToNext(), left, top + 48, 0xFFFFFF);
        g.drawString(font, "Очки навыков: " + data.skillPoints(), left, top + 64, 0xFFD966);

        String[] names = {"Сила", "Ловкость", "Выносливость", "Интеллект", "Воля"};
        g.drawString(font, "ХАРАКТЕРИСТИКИ", left, 98, 0x8AB4F8);
        for (int i = 0; i < 5; i++)
            g.drawString(font, names[i] + ": " + data.stat(i), left, 118 + i * 24, 0xFFFFFF);

        g.drawString(font, "ВЕТКА КЛАССА", width / 2 + 25, 98, 0x8AB4F8);
        List<SkillCatalog.Skill> classSkills = SkillCatalog.forClass(data.classId());
        for (int i = 0; i < classSkills.size(); i++) {
            SkillCatalog.Skill s = classSkills.get(i);
            g.drawString(font, s.name(), width / 2 + 25, 101 + i * 52, 0xFFFFFF);
            g.drawString(font, s.description(), width / 2 + 25, 113 + i * 52, 0xAAAAAA);
        }

        super.render(g, mouseX, mouseY, partialTick);
    }

    private String className(String id) {
        return switch (id) {
            case "forgotten_knight" -> "Забытый рыцарь";
            case "vagabond" -> "Бродяга";
            case "titan" -> "Титан";
            case "oni_slayer" -> "Они-убийца";
            case "bounty_hunter" -> "Охотник за головами";
            case "exiled" -> "Сослан";
            case "rogue" -> "Негодяй";
            default -> "Не выбран";
        };
    }

    @Override public boolean isPauseScreen() { return false; }
}
