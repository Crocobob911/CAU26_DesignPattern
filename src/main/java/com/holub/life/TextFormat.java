package com.holub.life;
import java.io.*;
import java.awt.Point;
import java.util.List;

public abstract class TextFormat implements FileFormat {

    public static class ParsedData {
        public final List<Point> liveCells;
        public final int width;
        public final int height;

        public ParsedData(List<Point> liveCells, int width, int height) {
            this.liveCells = liveCells;
            this.width = width;
            this.height = height;
        }
    }
    
    @Override
    public final Storable load(File file) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String name = readHeader(reader);
            ParsedData data = readBody(reader);
            return new TextToMementoAdapter(data.liveCells, name, data.width, data.height);
        }
    }

    @Override
    public final void save(File file, UniverseModel model) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // UniverseModel의 전체 크기(가로/세로)를 가져오는 getter가 있다고 가정
            writeHeader(writer);
            writeBody(writer,(Cell.Memento) model.createMemento(), model.getWidth(), model.getHeight());
        }
    }

    protected abstract String readHeader(BufferedReader reader) throws IOException;
    protected abstract ParsedData readBody(BufferedReader reader) throws IOException;
    
    protected abstract void writeHeader(BufferedWriter writer) throws IOException;
    protected abstract void writeBody(BufferedWriter writer, Cell.Memento memento, int w, int h) throws IOException;
}