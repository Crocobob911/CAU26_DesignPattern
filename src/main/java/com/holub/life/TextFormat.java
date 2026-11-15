package com.holub.life;
import java.io.*;
import java.awt.Point;
import java.util.List;

public abstract class TextFormat implements FileFormat {
    
    @Override
    public final Storable load(File file) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            readHeader(reader);
            List<Point> liveCells = readBody(reader);
            return new TextToMementoAdapter(liveCells);
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

    protected abstract void readHeader(BufferedReader reader) throws IOException;
    protected abstract List<Point> readBody(BufferedReader reader) throws IOException;
    
    protected abstract void writeHeader(BufferedWriter writer) throws IOException;
    protected abstract void writeBody(BufferedWriter writer, Cell.Memento memento, int w, int h) throws IOException;
}