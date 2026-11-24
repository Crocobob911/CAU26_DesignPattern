package com.holub.life;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.awt.Point;

public class PlaintextFormatTest {

    @TempDir
    Path tempDir;

    @Test
    public void testLoadPlaintextFormat() throws Exception {
        File tempFile = tempDir.resolve("test.cells").toFile();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("!Name: Blinker\n");
            writer.write("!Comment line\n");
            writer.write("...\n");
            writer.write("OOO\n");
            writer.write("...\n");
        }

        FileFormat format = new PlaintextFormat();
        Storable result = format.load(tempFile);

        assertTrue(result instanceof TextToMementoAdapter);
        TextToMementoAdapter adapter = (TextToMementoAdapter) result;

        assertEquals("Blinker", adapter.getName(), "이름을 파싱해야 합니다.");
        assertEquals(3, adapter.getWidth(), "가장 긴 줄의 길이를 너비로 잡아야 합니다.");
        assertEquals(3, adapter.getHeight(), "총 줄 수를 높이로 잡아야 합니다.");

        assertTrue(adapter.isAlive(new Point(0, 1)));
        assertTrue(adapter.isAlive(new Point(1, 1)));
        assertTrue(adapter.isAlive(new Point(2, 1)));
        assertFalse(adapter.isAlive(new Point(0, 0)));
    }

    @Test
    public void testSavePlaintextFormat() throws Exception {

        Neighborhood board = new Neighborhood(8, new Neighborhood(8, new Resident()));
        UniverseModel model = new UniverseModel(board, 8, 8, 2, 8, LifeRule.CONWAY);
        model.setAlive(1, 1, true);
        model.setAlive(2, 1, true);
        model.setAlive(3, 1, true);
        
        File tempFile = tempDir.resolve("saved.cells").toFile();
        FileFormat format = new PlaintextFormat();


        format.save(tempFile, model);


        String content = Files.readString(tempFile.toPath());
        assertTrue(content.contains("!Name: Saved Pattern from Holub Life"), "헤더가 작성되어야 합니다.");
        

        assertTrue(content.contains(".OOO...."), "보드 상태가 O와 .으로 직렬화되어야 합니다.");
    }
}