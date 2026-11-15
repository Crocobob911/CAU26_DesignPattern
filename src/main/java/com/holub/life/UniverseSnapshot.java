package com.holub.life;

public class UniverseSnapshot {
    private final Storable state;
    private final long generation;

    public UniverseSnapshot(Storable state, long generation) {
        this.state = state;
        this.generation = generation;
    }

    public Storable getState() { return state; }
    public long getGeneration() { return generation; }
}