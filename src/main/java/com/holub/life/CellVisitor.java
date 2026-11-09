package com.holub.life;

/** A read-only traversal of the Composite in board coordinates. */
public interface CellVisitor {
    default boolean enterNeighborhood(int x, int y, int width) {
        return true;
    }

    void visitResident(int x, int y, boolean alive);

    default void leaveNeighborhood(int x, int y, int width, boolean active) {
    }
}
