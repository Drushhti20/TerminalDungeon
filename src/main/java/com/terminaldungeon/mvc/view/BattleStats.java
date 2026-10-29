package com.terminaldungeon.mvc.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Per-round numbers read from the battle messages, for the end-of-game summary.
// Stored by round number, so a round taken back with undo or load is replaced when it is replayed.
final class BattleStats {
    static final int DEALT = 0;
    static final int TAKEN = 1;
    static final int HEALED = 2;
    static final int HEALS = 3;

    private static final Pattern AMOUNT = Pattern.compile(" for (\\d+) ");

    private final Map<Integer, int[]> rounds = new HashMap<>();

    void record(int round, List<String> messages, String heroName) {
        int[] numbers = new int[4];
        String heroPrefix = heroName + " ";
        for (String message : messages) {
            int amount = amountIn(message);
            if (amount < 0) {
                continue;
            }
            boolean byHero = message.startsWith(heroPrefix);
            if (message.contains(" attacks ")) {
                numbers[byHero ? DEALT : TAKEN] += amount;
            } else if (message.contains(" heals ") && byHero) {
                numbers[HEALED] += amount;
                numbers[HEALS]++;
            }
        }
        rounds.put(round, numbers);
    }

    // totals of rounds 1..upToRound, the rounds that are part of the current game
    int[] totals(int upToRound) {
        int[] totals = new int[4];
        for (int round = 1; round <= upToRound; round++) {
            int[] numbers = rounds.get(round);
            if (numbers == null) {
                continue;
            }
            for (int i = 0; i < totals.length; i++) {
                totals[i] += numbers[i];
            }
        }
        return totals;
    }

    // the number in "... for 12 damage." or "... heals for 6 HP.", or -1 when there is none
    static int amountIn(String message) {
        Matcher matcher = AMOUNT.matcher(message);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : -1;
    }
}
