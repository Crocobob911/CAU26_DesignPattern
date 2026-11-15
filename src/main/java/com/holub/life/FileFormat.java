package com.holub.life;
import java.io.File;

public interface FileFormat {
    Storable load(File file) throws Exception;
    void save(File file, UniverseModel model) throws Exception;
}