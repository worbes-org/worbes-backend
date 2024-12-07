package com.worbes.auctionhousetracker.config;

import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.embeded.Language;
import com.worbes.auctionhousetracker.service.ItemClassService;
import org.springframework.boot.CommandLineRunner;

import java.util.Arrays;
import java.util.List;

public class ItemClassDataLoader implements CommandLineRunner {
    private final ItemClassService itemClassService;

    public ItemClassDataLoader(ItemClassService itemClassService) {
        this.itemClassService = itemClassService;
    }

    @Override
    public void run(String... args) {
        List<ItemClass> itemClasses = Arrays.asList(
                new ItemClass(0L, new Language(
                        "Consumable", "Consumible", "Consumível", "Verbrauchbares", "Consumable", "Consumible",
                        "Consommable", "Consumabili", "Расходуемые", "소비용품", "消耗品", "消耗品")),
                new ItemClass(1L, new Language(
                        "Container", "Contenedor", "Recipiente", "Behälter", "Container", "Recipiente",
                        "Conteneur", "Contenitori", "Контейнер", "가방", "容器", "容器")),
                new ItemClass(2L, new Language(
                        "Weapon", "Arma", "Arma", "Waffe", "Weapon", "Arma",
                        "Arme", "Arma", "Оружие", "무기", "武器", "武器")),
                new ItemClass(3L, new Language(
                        "Gem", "Gema", "Gema", "Edelstein", "Gem", "Gema",
                        "Gemme", "Gemme", "Самоцвет", "보석", "寶石", "宝石")),
                new ItemClass(4L, new Language(
                        "Armor", "Armadura", "Armadura", "Rüstung", "Armor", "Armadura",
                        "Armure", "Armature", "Доспехи", "방어구", "護甲", "护甲")),
                new ItemClass(5L, new Language(
                        "Reagent", "Componente", "Reagente", "Reagenz", "Reagent", "Componente",
                        "Composant", "Reagente", "Реагент", "재료", "施法材料", "材料")),
                new ItemClass(8L, new Language(
                        "Item Enhancement", "Mejora de objetos", "Aperfeiçoamento de Item", "Gegenstandsaufwertung", "Item Enhancement", "Mejora de objetos",
                        "Amélioration d’objet", "Potenziamenti oggetti", "Улучшение предмета", "아이템 강화", "物品附魔", "物品强化")),
                new ItemClass(9L, new Language(
                        "Recipe", "Receta", "Receita", "Rezept", "Recipe", "Receta",
                        "Recette", "Ricette", "Рецепт", "제조법", "配方", "配方")),
                new ItemClass(12L, new Language(
                        "Quest", "Misión", "Missão", "Quest", "Quest", "Misión",
                        "Quête", "Oggetti di missione", "Задание", "퀘스트", "任務", "任务")),
                new ItemClass(15L, new Language(
                        "Miscellaneous", "Miscelánea", "Diversos", "Verschiedenes", "Miscellaneous", "Miscelánea",
                        "Divers", "Varie", "Разное", "기타", "雜項", "杂项")),
                new ItemClass(16L, new Language(
                        "Glyph", "Glifo", "Glifo", "Glyphe", "Glyph", "Glifo",
                        "Glyphe", "Glifi", "Символ", "문양", "雕紋", "雕文")),
                new ItemClass(17L, new Language(
                        "Battle Pets", "Mascotas de duelo", "Mascotes de Batalha", "Kampfhaustiere", "Battle Pets", "Mascotas de duelo",
                        "Mascottes de combat", "Mascotte", "Боевые питомцы", "전투 애완동물", "戰寵", "战斗宠物")),
                new ItemClass(18L, new Language(
                        "WoW Token", "Ficha de WoW", "Ficha de WoW", "WoW-Marke", "WoW Token", "Ficha de WoW",
                        "Jeton WoW", "Gettone WoW", "Жетон WoW", "WoW 토큰", "魔獸代幣", "魔兽世界时光徽章")),
                new ItemClass(19L, new Language(
                        "Profession", "Profesión", "Profissão", "Beruf", "Profession", "Profesión",
                        "Métier", "Professione", "Профессия", "전문 기술", "專業技能", "专业技能"))
        );

        itemClasses.forEach(itemClassService::saveItemClass);
    }
}
