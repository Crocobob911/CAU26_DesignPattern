package com.holub.life;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Point;
import java.util.Arrays;
import java.util.List;

public class TextToMementoAdapterTest {

    @Test
    public void testAdapterDataMapping() {
        // Given: 파싱된 더미 좌표와 메타데이터
        List<Point> parsedCells = Arrays.asList(new Point(0, 0), new Point(2, 2));
        String name = "Glider Test";
        int width = 3;
        int height = 3;

        // When: 어댑터 생성
        TextToMementoAdapter adapter = new TextToMementoAdapter(parsedCells, name, width, height);

        // Then 1: 메타데이터(PatternInfo) 검증
        assertEquals("Glider Test", adapter.getName());
        assertEquals(3, adapter.getWidth());
        assertEquals(3, adapter.getHeight());

        // Then 2: 좌표(Cell.Memento) 검증
        assertTrue(adapter.isAlive(new Point(0, 0)), "(0,0)은 살아있어야 합니다.");
        assertTrue(adapter.isAlive(new Point(2, 2)), "(2,2)는 살아있어야 합니다.");
        assertFalse(adapter.isAlive(new Point(1, 1)), "(1,1)은 죽어있어야 합니다.");
    }

    @Test
    public void testAdapterThrowsExceptionOnStreamMethods() {
        // Given
        TextToMementoAdapter adapter = new TextToMementoAdapter(Arrays.asList(), "Empty", 0, 0);

        // Then: byte stream 기반의 기존 load/flush 메서드는 Unsupported 예외를 던져야 함
        assertThrows(UnsupportedOperationException.class, () -> {
            adapter.load(null);
        });
        
        assertThrows(UnsupportedOperationException.class, () -> {
            adapter.flush(null);
        });
    }
}