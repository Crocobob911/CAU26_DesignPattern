package com.holub.life;

/** A rule must return the same result for the same state and neighbor count. */
@FunctionalInterface
public interface LifeRule {
    LifeRule CONWAY = BSRule.CONWAY;

    boolean nextState(boolean alive, int neighbors);
}
