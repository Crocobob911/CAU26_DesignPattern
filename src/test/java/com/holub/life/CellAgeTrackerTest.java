package com.holub.life;

import static org.junit.jupiter.api.Assertions.*;

import com.holub.ui.Colors;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** E8: cell age (Observer on LifeFacade) and the age-color decorator. */
class CellAgeTrackerTest {
    private static LifeFacade board() {
        return new UniverseBuilder().build();   // 64 x 64
    }

    private static void block(LifeFacade board, int x, int y) {
        board.setAlive(x, y, true);
        board.setAlive(x + 1, y, true);
        board.setAlive(x, y + 1, true);
        board.setAlive(x + 1, y + 1, true);
    }

    @Test
    @DisplayName("정지 패턴(블록): 직접 찍으면 나이 1, 세대마다 1씩 늘어남")
    void blockGetsOlderEveryGeneration() {
        LifeFacade board = board();
        CellAgeTracker ages = new CellAgeTracker(board);
        block(board, 10, 10);
        assertEquals(1, ages.ageAt(10, 10));

        for (int i = 0; i < 5; i++)
            board.tick();
        assertEquals(6, ages.ageAt(10, 10));
        assertEquals(6, ages.ageAt(11, 11));
        assertEquals(0, ages.ageAt(12, 12));
    }

    @Test
    @DisplayName("블링커: 가운데 칸은 계속 나이가 늘고, 양 끝은 태어나면 1, 죽으면 0")
    void blinkerCenterAgesEndsRestart() {
        LifeFacade board = board();
        CellAgeTracker ages = new CellAgeTracker(board);
        board.setAlive(30, 30, true);
        board.setAlive(31, 30, true);
        board.setAlive(32, 30, true);

        board.tick();   // vertical
        assertEquals(2, ages.ageAt(31, 30));
        assertEquals(1, ages.ageAt(31, 29));
        assertEquals(1, ages.ageAt(31, 31));
        assertEquals(0, ages.ageAt(30, 30));

        board.tick();   // horizontal again
        assertEquals(3, ages.ageAt(31, 30));
        assertEquals(1, ages.ageAt(30, 30));
        assertEquals(0, ages.ageAt(31, 29));
    }

    @Test
    @DisplayName("편집: 켠 칸만 1, 끈 칸은 0, 나머지 칸의 나이는 그대로")
    void editKeepsOtherAges() {
        LifeFacade board = board();
        CellAgeTracker ages = new CellAgeTracker(board);
        block(board, 10, 10);
        board.tick();
        board.tick();
        assertEquals(3, ages.ageAt(10, 10));

        board.toggle(40, 40);
        assertEquals(1, ages.ageAt(40, 40));
        assertEquals(3, ages.ageAt(10, 10));

        board.toggle(40, 40);
        assertEquals(0, ages.ageAt(40, 40));
    }

    @Test
    @DisplayName("Clear·Load·Restore 후에는 기록이 없으니 살아 있는 칸은 모두 1부터 다시 시작")
    void wholeBoardChangesRestartAges() throws IOException {
        LifeFacade board = board();
        CellAgeTracker ages = new CellAgeTracker(board);
        block(board, 10, 10);
        LifeFacade.Snapshot snapshot = board.createSnapshot();
        ByteArrayOutputStream saved = new ByteArrayOutputStream();
        board.store(saved);
        board.tick();
        board.tick();
        assertEquals(3, ages.ageAt(10, 10));

        board.restore(snapshot);
        assertEquals(1, ages.ageAt(10, 10));

        board.tick();
        board.clear();
        assertEquals(0, ages.ageAt(10, 10));

        board.load(new ByteArrayInputStream(saved.toByteArray()));
        assertEquals(1, ages.ageAt(10, 10));
    }

    @Test
    @DisplayName("규칙만 바꾸면 보드는 그대로이므로 나이도 유지")
    void ruleChangeKeepsAges() {
        LifeFacade board = board();
        CellAgeTracker ages = new CellAgeTracker(board);
        block(board, 10, 10);
        board.tick();
        board.setRule(LifeRule.CONWAY);
        assertEquals(2, ages.ageAt(10, 10));
    }

    @Test
    @DisplayName("보드 크기를 바꿔도 같은 추적기가 새 크기로 계속 동작")
    void resizeFollowsNewBoard() {
        LifeFacade board = board();
        LifeMediator mediator = new LifeMediator(board, () -> { });
        CellAgeTracker ages = new CellAgeTracker(board);
        block(board, 10, 10);

        mediator.resize(new UniverseBuilder().blockSize(3).levels(3));   // 27 x 27, empty
        assertEquals(0, ages.ageAt(10, 10));
        assertThrows(IndexOutOfBoundsException.class, () -> ages.ageAt(30, 30));

        block(board, 20, 20);
        board.tick();
        assertEquals(2, ages.ageAt(21, 21));
    }

    @Test
    @DisplayName("detach 후에는 더 이상 보드 변화를 따라가지 않음")
    void detachStopsListening() {
        LifeFacade board = board();
        CellAgeTracker ages = new CellAgeTracker(board);
        block(board, 10, 10);
        ages.detach();
        board.tick();
        assertEquals(1, ages.ageAt(10, 10));
    }

    @Test
    @DisplayName("나이 색: 1세대는 young, FULL_AGE 이상은 old, 그 사이는 점점 old 쪽으로")
    void ageColorBlendsFromYoungToOld() {
        Color young = new Color(0, 0, 0);
        Color old = new Color(90, 180, 255);
        assertEquals(young, AgeColorDecorator.colorFor(1, young, old));
        assertEquals(old, AgeColorDecorator.colorFor(AgeColorDecorator.FULL_AGE, young, old));
        assertEquals(old, AgeColorDecorator.colorFor(AgeColorDecorator.FULL_AGE + 50, young, old));

        int previous = -1;
        for (int age = 1; age <= AgeColorDecorator.FULL_AGE; age++) {
            int red = AgeColorDecorator.colorFor(age, young, old).getRed();
            assertTrue(red > previous, "age " + age);
            previous = red;
        }
    }

    @Test
    @DisplayName("나이 색을 켜면 살아 있는 칸만 나이 색이 되고, 죽은 칸과 격자선은 그대로")
    void panelPaintsAgeColorUnderGridLines() {
        LifeFacade board = board();
        UniversePanel panel = new UniversePanel(new LifeMediator(board, () -> { }));
        block(board, 10, 10);
        for (int i = 0; i < 20; i++)
            board.tick();
        panel.setShowCellAge(true);

        int cell = 4;
        BufferedImage image = new BufferedImage(64 * cell + 1, 64 * cell + 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        board.visitCells(panel.createRenderer(graphics, cell));
        graphics.dispose();

        int centre = cell / 2;
        assertEquals(Colors.DARK_RED, new Color(image.getRGB(10 * cell + centre, 10 * cell + centre)));
        assertEquals(Colors.LIGHT_YELLOW, new Color(image.getRGB(13 * cell + centre, 13 * cell + centre)));
        assertEquals(Colors.DARK_YELLOW, new Color(image.getRGB(11 * cell, 11 * cell + centre)));
    }
}
