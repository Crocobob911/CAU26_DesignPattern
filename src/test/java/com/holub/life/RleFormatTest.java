package com.holub.life;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.awt.Point;

public class RleFormatTest {

    @TempDir
    Path tempDir;

    @Test
    public void testLoadRleFormat() throws Exception {
        File tempFile = tempDir.resolve("glider.rle").toFile();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("#N Glider Test Pattern\n");
            writer.write("#C This is a comment that should be ignored\n");
            writer.write("x = 3, y = 3, rule = B3/S23\n");
            writer.write("bo$2bo$3o!\n");
        }

        FileFormat format = new RleFormat();


        Storable result = format.load(tempFile);


        assertTrue(result instanceof TextToMementoAdapter);
        TextToMementoAdapter adapter = (TextToMementoAdapter) result;

        assertEquals("Glider Test Pattern", adapter.getName(), "#N 헤더에서 이름을 추출해야 합니다.");
        
        assertEquals(3, adapter.getWidth(), "압축 해제된 최대 X 좌표 기반으로 너비가 3이어야 합니다.");
        assertEquals(3, adapter.getHeight(), "압축 해제된 최대 Y 좌표 기반으로 높이가 3이어야 합니다.");


        assertFalse(adapter.isAlive(new Point(0, 0)));
        assertTrue(adapter.isAlive(new Point(1, 0)));
        assertTrue(adapter.isAlive(new Point(2, 1)));
        assertTrue(adapter.isAlive(new Point(0, 2)));
        assertTrue(adapter.isAlive(new Point(1, 2)));
        assertTrue(adapter.isAlive(new Point(2, 2)));
    }

    @Test
    public void testSaveRleFormat() throws Exception {
        Neighborhood board = new Neighborhood(8, new Neighborhood(8, new Resident()));
        UniverseModel model = new UniverseModel(board, 2, 2, 2, 8, LifeRule.CONWAY);
        model.setAlive(0, 0, true);
        model.setAlive(1, 1, true);

        File tempFile = tempDir.resolve("saved.rle").toFile();
        FileFormat format = new RleFormat();

        format.save(tempFile, model);

        String content = Files.readString(tempFile.toPath());
        
        assertTrue(content.contains("#N Saved by Holub Life RLE Encoder"), "RLE 고유 헤더가 저장되어야 합니다.");
        assertTrue(content.contains("x = 2, y = 2, rule = B3/S23"), "x, y 사이즈와 rule 메타데이터가 저장되어야 합니다.");
        
        String compressedData = content.replaceAll("\\s+", ""); 
        
        assertTrue(compressedData.contains("o$bo!"), "생존(o)과 공백(b), 개행($), 종료(!) 문자가 최적화되어 정확히 압축 저장되어야 합니다.");
    }
}