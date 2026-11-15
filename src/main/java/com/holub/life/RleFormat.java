package com.holub.life;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

// 미사용으로 인한 클래스 골조만 남겨 둠
public class RleFormat extends TextFormat {
    @Override protected void readHeader(BufferedReader reader) throws IOException { }
    @Override protected List<Point> readBody(BufferedReader reader) throws IOException { return new ArrayList<>(); }
    
    @Override protected void writeHeader(BufferedWriter writer) throws IOException {  }
    @Override protected void writeBody(BufferedWriter writer, Cell.Memento memento, int w, int h) throws IOException {}
}