package com.holub.life;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.awt.Point;
import java.util.List;
import java.util.ArrayList;


public class TextToMementoAdapter implements Cell.Memento, PatternInfo {

    

    private final Cell.Memento internalMemento; 
    private final String name;
    private final int width;
    private final int height;

    public TextToMementoAdapter(List<Point> parsedLiveCells, String name, int width, int height) {
        this.internalMemento = Neighborhood.emptyMemento(); 
        this.relativePoints = new ArrayList<Point>();
        
        for (Point p : parsedLiveCells) {
        	relativePoints.add(p);
        	internalMemento.markAsAlive(p);
        }
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public List<Point> pointList(){
    	return relativePoints;
    }
    
    @Override
    public void markAsAlive(Point location) {
        internalMemento.markAsAlive(location);
    }

    @Override
    public boolean isAlive(Point location) {
        return internalMemento.isAlive(location);
    }


    @Override
    public void load(InputStream in) throws IOException {
        throw new UnsupportedOperationException("Text adapter does not use byte streams.");
    }

    @Override
    public void flush(OutputStream out) throws IOException {
        throw new UnsupportedOperationException("Text adapter does not use byte streams.");
    }

    @Override public String getName() { return name; }
    @Override public int getWidth() { return width; }
    @Override public int getHeight() { return height; }
}