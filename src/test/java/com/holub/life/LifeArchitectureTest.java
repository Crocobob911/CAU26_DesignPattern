package com.holub.life;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class LifeArchitectureTest {
    @Test
    void resizeKeepsFacadeListenersAndRule() {
        LifeFacade board = new UniverseBuilder().rule((alive, neighbors) -> !alive).build();
        var pauses = new AtomicInteger();
        var mediator = new LifeMediator(board, pauses::incrementAndGet);
        var events = new ArrayList<LifeFacade.Event>();
        board.addListener(events::add);
        mediator.toggle(0, 0);
        var old = board.createSnapshot();

        assertThrows(IllegalArgumentException.class,
                () -> mediator.resize(new UniverseBuilder().blockSize(16).levels(3)));
        assertEquals(0, pauses.get());
        assertTrue(board.isAlive(0, 0));

        mediator.resize(new UniverseBuilder().blockSize(3).levels(3).cellPixels(4));
        assertSame(board, mediator.facade());
        assertEquals(27, board.width());
        assertEquals(4, board.cellPixels());
        assertFalse(board.isAlive(0, 0));
        assertEquals(1, pauses.get());
        assertThrows(IllegalArgumentException.class, () -> board.restore(old));

        mediator.tick();
        assertTrue(board.isAlive(26, 26));
        assertEquals(List.of(
                new LifeFacade.Event(LifeFacade.Change.EDIT, 0),
                new LifeFacade.Event(LifeFacade.Change.RESIZE, 0),
                new LifeFacade.Event(LifeFacade.Change.TICK, 1)), events);
    }

    @Test
    void snapshotRestoresCellsAndGeneration() {
        LifeFacade board = new UniverseBuilder().build();
        board.tick();
        board.setAlive(7, 7, true);
        var snapshot = board.createSnapshot();
        board.clear();
        board.restore(snapshot);
        assertEquals(1, board.generation());
        assertTrue(board.isAlive(7, 7));
        assertFalse(board.isAlive(8, 7));
        board.tick();
        assertFalse(board.isAlive(7, 7));
    }

    @Test
    void legacyFileLoadsAndInvalidInputPreservesBoard() throws IOException {
        LifeFacade board = new UniverseBuilder().build();
        var data = new ByteArrayOutputStream();
        try (var output = new ObjectOutputStream(data)) {
            output.writeObject(new LinkedList<>(List.of(new Point(7, 7), new Point(8, 7), new Point(9, 7))));
        }
        board.load(new ByteArrayInputStream(data.toByteArray()));
        assertThrows(IOException.class, () -> board.load(new ByteArrayInputStream(new byte[0])));
        board.tick();
        assertTrue(board.isAlive(8, 6));
        assertTrue(board.isAlive(8, 7));
        assertTrue(board.isAlive(8, 8));
        assertFalse(board.isAlive(7, 7));
    }

    @Test
    void nestedBoardMatchesFullScanAfterEditsAndRuleChanges() {
        var random = new Random(443);
        for (int block : new int[] {2, 3, 4, 8}) {
            int levels = block == 8 ? 2 : 3;
            LifeFacade board = new UniverseBuilder().blockSize(block).levels(levels).build();
            int width = board.width();
            boolean[][] state = new boolean[width][width];
            LifeRule rule = LifeRule.CONWAY;
            for (int tick = 0; tick < 60; tick++) {
                if (tick == 20 || tick == 40) {
                    rule = tick == 20 ? (alive, count) -> count == 0 || count == 4 : LifeRule.CONWAY;
                    board.setRule(rule);
                }
                for (int edit = 0; edit < 8; edit++) {
                    int x = random.nextInt(width);
                    int y = random.nextInt(width);
                    board.toggle(x, y);
                    state[y][x] = !state[y][x];
                }

                state = advance(state, rule);
                board.tick();
                for (int y = 0; y < width; y++)
                    for (int x = 0; x < width; x++)
                        assertEquals(state[y][x], board.isAlive(x, y), "cell " + x + "," + y);
            }
        }
    }

    private static boolean[][] advance(boolean[][] state, LifeRule rule) {
        int width = state.length;
        boolean[][] next = new boolean[width][width];
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < width; x++) {
                int neighbors = 0;
                for (int yy = Math.max(0, y - 1); yy <= Math.min(y + 1, width - 1); yy++)
                    for (int xx = Math.max(0, x - 1); xx <= Math.min(x + 1, width - 1); xx++)
                        if ((xx != x || yy != y) && state[yy][xx])
                            neighbors++;
                next[y][x] = rule.nextState(state[y][x], neighbors);
            }
        }
        return next;
    }
}
