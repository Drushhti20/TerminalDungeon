package com.terminaldungeon.prototype;

import java.util.HashMap;
import java.util.Map;

// Prototype Registry
// Stores the prepared Goblin and Orc templates and returns independent copies of them
// Stats are tuned against Member 2's damage and healing rules so the hero can lose:
// attacking every turn loses, healing at the right time wins.
// The Orc carries 2 healing potions; Goblins and Skeletons carry none.
public class EnemyRegistry {
    private Map<String, EnemyPrototype> prototypes = new HashMap<>();

    public EnemyRegistry() {
        registerPrototype("Goblin", new Enemy("Goblin", 40, 17, 3));
        registerPrototype("Skeleton", new Enemy("Skeleton", 50, 15, 4));
        registerPrototype("Orc", new Enemy("Orc", 100, 22, 6, 2));
    }

    public void registerPrototype(String key, EnemyPrototype prototype) {
        prototypes.put(key, prototype);
    }

    // always copies the stored template, never a previously injured enemy
    public Enemy cloneEnemy(String key) {
        EnemyPrototype prototype = prototypes.get(key);
        if (prototype == null) {
            throw new IllegalArgumentException("No enemy template registered for: " + key);
        }
        return (Enemy) prototype.clone();
    }
}
