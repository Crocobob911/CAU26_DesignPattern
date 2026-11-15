package com.holub.life;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class LifeFormat implements FileFormat {

    @Override
    public Storable load(File file) throws Exception {
        try (FileInputStream in = new FileInputStream(file)) {
            Storable memento = Neighborhood.emptyMemento();
            memento.load(in); 
            return memento;
        }
    }
    
    @Override
    public void save(File file, UniverseModel model) throws Exception {
        try (FileOutputStream out = new FileOutputStream(file)) {
            Storable memento = model.createMemento();
            memento.flush(out);
        }
    }

}
