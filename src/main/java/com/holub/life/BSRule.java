package com.holub.life;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * E2 Strategy: a Life rule written in B/S notation, e.g. "B3/S23" (born with 3, survives with 2 or 3).
 * Also a registry of named rules; the Rule menu is built from {@link #all()}.
 */
public final class BSRule implements LifeRule {
	// Must stay above the constants below: static fields are initialized from top to bottom.
	private static final Map<String, BSRule> REGISTRY = new LinkedHashMap<>();

	public static final BSRule CONWAY = register("CONWAY", "B3/S23");
	public static final BSRule HIGH_LIFE = register("HighLife", "B36/S23");
	public static final BSRule SEEDS = register("Seeds", "B2/S");
	public static final BSRule DAY_AND_NIGHT = register("Day&Night", "B3678/S34678");

	private final String name;
	private final String notation;
	private final boolean[] birth = new boolean[9];
	private final boolean[] survive = new boolean[9];

	private BSRule(String name, String notation) {
		this.name = name;
		this.notation = notation;
		String[] parts = notation.split("/", -1);
		if (parts.length != 2 || !parts[0].startsWith("B") || !parts[1].startsWith("S"))
			throw new IllegalArgumentException("규칙은 B숫자/S숫자 형식이어야 합니다 (예: B3/S23): " + notation);
		fill(birth, parts[0]);
		fill(survive, parts[1]);
	}

	private static void fill(boolean[] table, String part) {
		for (int i = 1; i < part.length(); i++) {
			char digit = part.charAt(i);
			if (digit < '0' || digit > '8')
				throw new IllegalArgumentException("이웃 수는 0~8 사이 숫자여야 합니다: " + digit);
			table[digit - '0'] = true;
		}
	}

	/** Parse B/S notation without registering it. Throws IllegalArgumentException for bad notation. */
	public static BSRule parse(String notation) {
		String text = Objects.requireNonNull(notation).trim();
		return new BSRule(text, text);
	}

	/** Parse and register under a name; an invalid rule is not registered. */
	public static BSRule register(String name, String notation) {
		if (name == null || name.isBlank())
			throw new IllegalArgumentException("규칙 이름이 비어 있습니다.");
		BSRule rule = new BSRule(name.trim(), parse(notation).notation);
		REGISTRY.put(rule.name, rule);
		return rule;
	}

	public static BSRule get(String name) {
		return REGISTRY.get(name);
	}

	/** Registered rules in registration order. */
	public static List<BSRule> all() {
		return List.copyOf(REGISTRY.values());
	}

	public String name() {
		return name;
	}

	public String notation() {
		return notation;
	}

	@Override
	public boolean nextState(boolean alive, int neighbors) {
		return alive ? survive[neighbors] : birth[neighbors];
	}
}
