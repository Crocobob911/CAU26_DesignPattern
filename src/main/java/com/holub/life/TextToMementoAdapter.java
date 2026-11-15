package com.holub.life;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.awt.Point;
import java.util.List;


public class TextToMementoAdapter implements Cell.Memento {
    

    private final Cell.Memento internalMemento; 

    public TextToMementoAdapter(List<Point> parsedLiveCells) {
        this.internalMemento = Neighborhood.emptyMemento(); 
        for (Point p : parsedLiveCells) {
            internalMemento.markAsAlive(p);
        }
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
}