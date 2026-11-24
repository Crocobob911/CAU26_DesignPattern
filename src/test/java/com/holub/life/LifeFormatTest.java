package com.holub.life;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.nio.file.Path;
import java.awt.Point;

public class LifeFormatTest {

    @TempDir
    Path tempDir;

    @Test
    public void testLifeFormatRoundTrip() throws Exception {
        Neighborhood board = new Neighborhood(8, new Neighborhood(8, new Resident()));
        UniverseModel model = new UniverseModel(board, 64, 8, 2, 8, LifeRule.CONWAY);
        model.setAlive(5, 5, true);
        model.setAlive(6, 5, true);

        File tempFile = tempDir.resolve("test.life").toFile();
        FileFormat format = new LifeFormat();

        format.save(tempFile, model);
        assertTrue(tempFile.exists() && tempFile.length() > 0, "바이트 파일이 정상적으로 쓰여야 합니다.");

        Storable loadedMemento = format.load(tempFile);

        assertTrue(loadedMemento instanceof Cell.Memento);
        Cell.Memento memento = (Cell.Memento) loadedMemento;
        
        assertTrue(memento.isAlive(new Point(5, 5)), "(5,5)가 복원되어야 합니다.");
        assertTrue(memento.isAlive(new Point(6, 5)), "(6,5)가 복원되어야 합니다.");
        assertFalse(memento.isAlive(new Point(0, 0)), "기존에 빈 공간은 계속 비어있어야 합니다.");
    }
}