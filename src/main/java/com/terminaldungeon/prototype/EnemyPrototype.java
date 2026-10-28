package com.terminaldungeon.prototype;

// Prototype interface
// Every enemy template can produce an independent copy of itself
public interface EnemyPrototype {
    EnemyPrototype clone();
}
