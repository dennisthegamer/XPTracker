package com.example.xptracker;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum XPSource {
	COMBAT("Combat", "Kampf", 0xFFFF5555, new ItemStack(Items.IRON_SWORD)),
	MINING("Mining", "Abbau", 0xFF55FFFF, new ItemStack(Items.IRON_PICKAXE)),
	SMELTING("Smelting", "Schmelzen", 0xFFFFAA00, new ItemStack(Items.FURNACE)),
	TRADING("Trading", "Handel", 0xFF55FF55, new ItemStack(Items.EMERALD)),
	FISHING("Fishing", "Angeln", 0xFF5555FF, new ItemStack(Items.FISHING_ROD)),
	BREEDING("Breeding", "Zucht", 0xFFFF55FF, new ItemStack(Items.WHEAT)),
	OTHER("Other", "Sonstiges", 0xFFAAAAAA, new ItemStack(Items.EXPERIENCE_BOTTLE));

	private final String englishName;
	private final String germanName;
	private final int color;
	private final ItemStack icon;

	XPSource(String englishName, String germanName, int color, ItemStack icon) {
		this.englishName = englishName;
		this.germanName = germanName;
		this.color = color;
		this.icon = icon;
	}

	public String getEnglishName() {
		return englishName;
	}

	public String getGermanName() {
		return germanName;
	}

	public int getColor() {
		return color;
	}

	public ItemStack getIcon() {
		return icon;
	}
}
