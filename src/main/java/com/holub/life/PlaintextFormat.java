package com.holub.life;

import java.io.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class PlaintextFormat extends TextFormat {


    @Override
    protected String readHeader(BufferedReader reader) throws IOException {
        reader.mark(4096);
        String line;
        String patternName = "Unknown Pattern"; // 기본값
        
        while ((line = reader.readLine()) != null) {
            // !Name: 으로 시작하는 줄이 있으면 이름을 캐치합니다.
            if (line.startsWith("!Name:")) {
                patternName = line.substring(6).trim();
            } else if (!line.startsWith("!")) {
                reader.reset();
                break;
            }
            reader.mark(4096);
        }
        return patternName;
    }

    @Override
    protected ParsedData readBody(BufferedReader reader) throws IOException {
        List<Point> liveCells = new ArrayList<>();
        String line;
        int y = 0;
        int maxWidth = 0;
        
        while ((line = reader.readLine()) != null) {
            // 가장 긴 텍스트 줄을 가로 너비(width)로 갱신
            if (line.length() > maxWidth) {
                maxWidth = line.length();
            }
            
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c == 'O' || c == '*' || c == 'o') { 
                    liveCells.add(new Point(x, y));
                }
            }
            y++; // 줄 수가 곧 세로 높이(height)
        }
        // 좌표 덩어리와 함께 너비, 높이를 반환합니다.
        return new ParsedData(liveCells, maxWidth, y);
    }

    @Override
    protected void writeHeader(BufferedWriter writer) throws IOException {
        writer.write("!Name: Saved Pattern from Holub Life");
        writer.newLine();
    }

    @Override
    protected void writeBody(BufferedWriter writer, Cell.Memento memento, int w, int h) throws IOException {
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (memento.isAlive(new Point(x, y))) {
                    writer.write("O");
                } else {
                    writer.write(".");
                }
            }
            writer.newLine();
        }
    }
}