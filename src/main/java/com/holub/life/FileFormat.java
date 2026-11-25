package com.holub.life;
import java.io.File;

public interface FileFormat {
    Storable load(File file) throws Exception;
    void save(File file, UniverseModel model) throws Exception;

    /** Picks the format from the file extension, so Load, Store and the file stamp agree. */
    static FileFormat forFileName(String fileName) {
        if (fileName.endsWith(".life"))  return new LifeFormat();
        if (fileName.endsWith(".cells")) return new PlaintextFormat();
        if (fileName.endsWith(".rle"))   return new RleFormat();
        throw new IllegalArgumentException("지원하지 않는 파일 형식입니다: " + fileName);
    }
}