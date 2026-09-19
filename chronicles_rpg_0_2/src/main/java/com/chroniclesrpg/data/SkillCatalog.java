package com.chroniclesrpg.data;

import java.util.List;
import java.util.Map;

public final class SkillCatalog {
    public record Skill(String id, String name, String description) {}

    private static final Map<String, List<Skill>> SKILLS = Map.of(
        "forgotten_knight", List.of(
            new Skill("knight_guard", "Стальная стойка", "Повышает защитную специализацию."),
            new Skill("knight_power", "Тяжёлый удар", "Усиливает боевую специализацию."),
            new Skill("knight_resolve", "Непоколебимость", "Усиливает стойкость на низком здоровье.")
        ),
        "titan", List.of(
            new Skill("titan_fortress", "Живая крепость", "Развивает выносливость."),
            new Skill("titan_crush", "Сокрушение", "Развивает силовую специализацию."),
            new Skill("titan_stability", "Стойкость", "Развивает сопротивление отрицательным эффектам.")
        ),
        "oni_slayer", List.of(
            new Skill("oni_mark", "Клеймо охотника", "Усиливает стиль охоты на отмеченных врагов."),
            new Skill("oni_step", "Стремительный шаг", "Развивает мобильность."),
            new Skill("oni_focus", "Концентрация", "Развивает точность и волю.")
        ),
        "bounty_hunter", List.of(
            new Skill("hunter_track", "Следопыт", "Развивает поиск целей."),
            new Skill("hunter_reward", "Трофей", "Развивает специализацию на добыче."),
            new Skill("hunter_finisher", "Добивание", "Развивает силу против ослабленных целей.")
        ),
        "exiled", List.of(
            new Skill("exiled_mana", "Глубокая мана", "Развивает магическую специализацию."),
            new Skill("exiled_dash", "Астральный рывок", "Развивает мобильность мага."),
            new Skill("exiled_will", "Сила воли", "Развивает устойчивость и контроль.")
        ),
        "rogue", List.of(
            new Skill("rogue_shadow", "Тень", "Развивает скрытность."),
            new Skill("rogue_speed", "Быстрые ноги", "Развивает ловкость."),
            new Skill("rogue_ambush", "Засада", "Развивает стиль внезапной атаки.")
        ),
        "vagabond", List.of(
            new Skill("vagabond_adapt", "Адаптация", "Универсальная ветка развития."),
            new Skill("vagabond_explorer", "Исследователь", "Развивает путешествия и исследование."),
            new Skill("vagabond_survivor", "Выживший", "Развивает универсальную стойкость.")
        )
    );

    public static List<Skill> forClass(String classId) {
        return SKILLS.getOrDefault(classId, List.of());
    }

    private SkillCatalog() {}
}
