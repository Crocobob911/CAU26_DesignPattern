package com.holub.life;

import java.util.Objects;

/**
 * Decorator base for board renderers. Forwards every visit to the wrapped renderer,
 * so a subclass only overrides the step it adds drawing to.
 */
abstract class RendererDecorator implements CellVisitor {
    private final CellVisitor inner;

    RendererDecorator(CellVisitor inner) {
        this.inner = Objects.requireNonNull(inner);
    }

    @Override
    public boolean enterNeighborhood(int x, int y, int width) {
        return inner.enterNeighborhood(x, y, width);
    }

    @Override
    public void visitResident(int x, int y, boolean alive) {
        inner.visitResident(x, y, alive);
    }

    @Override
    public void leaveNeighborhood(int x, int y, int width, boolean active) {
        inner.leaveNeighborhood(x, y, width, active);
    }
}
