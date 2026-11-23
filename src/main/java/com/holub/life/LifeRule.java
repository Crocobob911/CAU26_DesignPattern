package com.holub.life;

/** A rule must return the same result for the same state and neighbor count. */
@FunctionalInterface
public interface LifeRule {
    LifeRule CONWAY = BSRule.get("CONWAY");
    LifeRule HighLife = BSRule.get("HighLife");
    LifeRule Seeds = BSRule.get("Seeds");
    LifeRule DayAndNight = BSRule.get("Day&Night");

    boolean nextState(boolean alive, int neighbors);
}
