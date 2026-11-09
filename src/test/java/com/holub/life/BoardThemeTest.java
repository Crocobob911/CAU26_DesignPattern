package com.holub.life;

import static org.junit.jupiter.api.Assertions.*;

import com.holub.ui.Colors;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** E8: themes (Abstract Factory) and renderer decorators. */
class BoardThemeTest {
    private static final int CELL = 4;

    /** A 64x64 board with a glider and a blinker, ticked once so some blocks are active. */
    private static LifeFacade sampleBoard() {
        LifeFacade board = new UniverseBuilder().build();
        int[][] cells = {{1, 0}, {2, 1}, {0, 2}, {1, 2}, {2, 2}, {30, 30}, {31, 30}, {32, 30}};
        for (int[] c : cells)
            board.setAlive(c[0], c[1], true);
        board.tick();
        return board;
    }

    private static BufferedImage draw(LifeFacade board, Color background, Function<Graphics, CellVisitor> renderer) {
        int pixels = board.width() * CELL;
        BufferedImage image = new BufferedImage(pixels + 1, pixels + 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(background);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        board.visitCells(renderer.apply(graphics));
        graphics.dispose();
        return image;
    }

    private static UniversePanel panel(LifeFacade board) {
        return new UniversePanel(new LifeMediator(board, () -> { }));
    }

    private static Color pixel(BufferedImage image, int x, int y) {
        return new Color(image.getRGB(x, y));
    }

    @Test
    @DisplayName("Classic 테마 + 격자선 + 블록 테두리 = 기존 CellRenderer와 픽셀 단위로 같은 그림")
    void classicThemeMatchesOriginalRenderer() {
        LifeFacade board = sampleBoard();
        UniversePanel panel = panel(board);

        BufferedImage expected = draw(board, Color.WHITE, g -> new OriginalRenderer(g, CELL));
        BufferedImage actual = draw(board, Color.WHITE, g -> panel.createRenderer(g, CELL));

        for (int y = 0; y < expected.getHeight(); y++)
            for (int x = 0; x < expected.getWidth(); x++)
                assertEquals(expected.getRGB(x, y), actual.getRGB(x, y), "pixel " + x + "," + y);
    }

    @Test
    @DisplayName("격자선을 끄면 셀 왼쪽 위 픽셀이 셀 색으로 채워지고, 켜면 격자선 색이 됨")
    void gridLinesCanBeTurnedOff() {
        LifeFacade board = new UniverseBuilder().build();
        UniversePanel panel = panel(board);
        // cell (3,3) is inside a block, so no block border touches its top-left pixel
        int corner = 3 * CELL;

        BufferedImage withGrid = draw(board, Color.WHITE, g -> panel.createRenderer(g, CELL));
        assertEquals(Colors.DARK_YELLOW, pixel(withGrid, corner, corner));

        panel.setShowGridLines(false);
        BufferedImage withoutGrid = draw(board, Color.WHITE, g -> panel.createRenderer(g, CELL));
        assertEquals(Colors.LIGHT_YELLOW, pixel(withoutGrid, corner, corner));
    }

    @Test
    @DisplayName("테마마다 살아 있는 셀·죽은 셀·배경 색이 서로 다르고, 한 테마 안에서 살아 있는 셀과 죽은 셀은 구분됨")
    void eachThemeDrawsItsOwnColors() {
        LifeFacade board = new UniverseBuilder().build();
        board.setAlive(5, 5, true);
        List<BoardTheme> themes = List.of(new ClassicTheme(), new DarkTheme(), new HighContrastTheme());
        int center = 5 * CELL + CELL / 2;
        int deadCenter = 6 * CELL + CELL / 2;

        List<Integer> liveColors = new ArrayList<>();
        List<Integer> backgrounds = new ArrayList<>();
        for (BoardTheme theme : themes) {
            BufferedImage image = draw(board, theme.background(), g -> theme.createCellPainter(g, CELL));
            assertNotEquals(image.getRGB(center, 5 * CELL + 1), image.getRGB(deadCenter, 5 * CELL + 1), theme.name());
            liveColors.add(image.getRGB(center, center));
            backgrounds.add(theme.background().getRGB());
        }
        assertEquals(3, Set.copyOf(liveColors).size());
        assertEquals(3, Set.copyOf(backgrounds).size());
        assertEquals(Color.RED, new Color(liveColors.get(0)));
    }

    @Test
    @DisplayName("테마를 바꾸면 패널 배경색도 같은 테마의 색으로 바뀜")
    void panelUsesThemeBackground() {
        UniversePanel panel = panel(new UniverseBuilder().build());
        assertEquals(Color.WHITE, panel.getBackground());
        BoardTheme dark = new DarkTheme();
        panel.setTheme(dark);
        assertEquals(dark.background(), panel.getBackground());
    }

    @Test
    @DisplayName("Decorator는 감싼 렌더러에 방문을 빠짐없이 전달하고, 감싼 렌더러가 건너뛴 블록은 그리지 않음")
    void decoratorsForwardEveryVisit() {
        LifeFacade board = new UniverseBuilder().blockSize(4).levels(2).build();
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.createGraphics();

        RecordingVisitor plain = new RecordingVisitor(Integer.MAX_VALUE);
        board.visitCells(plain);

        RecordingVisitor inner = new RecordingVisitor(Integer.MAX_VALUE);
        BoardTheme theme = new ClassicTheme();
        board.visitCells(theme.createBlockBorders(theme.createGridLines(inner, graphics, CELL), graphics, CELL));
        assertEquals(plain.calls, inner.calls);

        // 16x16 board = 16 blocks of 16 cells. inner refuses the first block (2nd enter):
        // its 16 cells must not be visited through the decorators either
        RecordingVisitor skipping = new RecordingVisitor(2);
        board.visitCells(theme.createBlockBorders(theme.createGridLines(skipping, graphics, CELL), graphics, CELL));
        assertEquals(256 - 16, skipping.residents);
        graphics.dispose();
    }

    /** Counts visits; refuses to enter the neighborhood with the given order number. */
    private static final class RecordingVisitor implements CellVisitor {
        private final int refuse;
        private int entered;
        private int residents;
        private final List<String> calls = new ArrayList<>();

        RecordingVisitor(int refuse) {
            this.refuse = refuse;
        }

        @Override
        public boolean enterNeighborhood(int x, int y, int width) {
            calls.add("enter " + x + "," + y + "," + width);
            return ++entered != refuse;
        }

        @Override
        public void visitResident(int x, int y, boolean alive) {
            residents++;
            calls.add("cell " + x + "," + y + "," + alive);
        }

        @Override
        public void leaveNeighborhood(int x, int y, int width, boolean active) {
            calls.add("leave " + x + "," + y + "," + width + "," + active);
        }
    }

    /** Copy of the renderer before E8 (commit 87c871c), kept as the reference picture. */
    private static final class OriginalRenderer implements CellVisitor {
        private final Graphics graphics;
        private final int cellSize;
        private final Rectangle clip;

        OriginalRenderer(Graphics graphics, int cellSize) {
            this.graphics = graphics;
            this.cellSize = cellSize;
            this.clip = graphics.getClipBounds();
        }

        @Override
        public boolean enterNeighborhood(int x, int y, int width) {
            int pixels = width * cellSize;
            return clip == null || clip.intersects(x * cellSize, y * cellSize, pixels + 1, pixels + 1);
        }

        @Override
        public void visitResident(int x, int y, boolean alive) {
            int left = x * cellSize;
            int top = y * cellSize;
            graphics.setColor(alive ? Color.RED : Colors.LIGHT_YELLOW);
            graphics.fillRect(left + 1, top + 1, cellSize - 1, cellSize - 1);
            graphics.setColor(Colors.DARK_YELLOW);
            graphics.drawLine(left, top, left, top + cellSize);
            graphics.drawLine(left, top, left + cellSize, top);
        }

        @Override
        public void leaveNeighborhood(int x, int y, int width, boolean active) {
            int left = x * cellSize;
            int top = y * cellSize;
            int pixels = width * cellSize;
            graphics.setColor(Colors.LIGHT_ORANGE);
            graphics.drawRect(left, top, pixels, pixels);
            if (active && pixels > 2) {
                graphics.setColor(Color.BLUE);
                graphics.drawRect(left + 1, top + 1, pixels - 2, pixels - 2);
            }
        }
    }
}
