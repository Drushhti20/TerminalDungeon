package com.terminaldungeon.prototype;

import java.util.HashMap;
import java.util.Map;

// Prototype Registry
// Stores the prepared Goblin and Orc templates and returns independent copies of them
// Stat values are placeholders, to be agreed with Member 2's damage and healing rules
public class EnemyRegistry {
    private Map<String, EnemyPrototype> prototypes = new HashMap<>();

    public EnemyRegistry() {
        registerPrototype("Goblin", new Enemy("Goblin", 40, 8, 3));
        registerPrototype("Orc", new Enemy("Orc", 70, 12, 6));
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
