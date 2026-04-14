package com.example.xptracker;

public enum XPSource {
	COMBAT("Combat", "Kampf", 0xFFFF5555),
	MINING("Mining", "Abbau", 0xFF55FFFF),
	SMELTING("Smelting", "Schmelzen", 0xFFFFAA00),
	TRADING("Trading", "Handel", 0xFF55FF55),
	FISHING("Fishing", "Angeln", 0xFF5555FF),
	BREEDING("Breeding", "Zucht", 0xFFFF55FF),
	OTHER("Other", "Sonstiges", 0xFFAAAAAA);

	private final String englishName;
	private final String germanName;
	private final int color;

	XPSource(String englishName, String germanName, int color) {
		this.englishName = englishName;
		this.germanName = germanName;
		this.color = color;
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
}
