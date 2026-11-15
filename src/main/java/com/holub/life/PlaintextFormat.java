package com.holub.life;

import java.io.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class PlaintextFormat extends TextFormat {


    @Override
    protected void readHeader(BufferedReader reader) throws IOException {
        reader.mark(4096);
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("!")) {
                reader.reset();
                break;
            }
            reader.mark(4096);
        }
    }

    @Override
    protected List<Point> readBody(BufferedReader reader) throws IOException {
        List<Point> liveCells = new ArrayList<>();
        String line;
        int y = 0;
        
        while ((line = reader.readLine()) != null) {
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c == 'O' || c == '*' || c == 'o') { 
                    liveCells.add(new Point(x, y));
                }
            }
            y++;
        }
        return liveCells;
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