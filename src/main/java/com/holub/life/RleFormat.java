package com.holub.life;

import java.io.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class RleFormat extends TextFormat {
    private static final java.util.regex.Pattern SIZE =
            java.util.regex.Pattern.compile("x\\s*=\\s*(\\d+)\\s*,\\s*y\\s*=\\s*(\\d+)");

    // Filled while reading one file, checked in validate().
    private int declaredWidth = -1;
    private int declaredHeight = -1;
    private boolean terminated;
    private char invalidTag;

    @Override
    protected String readHeader(BufferedReader reader) throws IOException {
        declaredWidth = declaredHeight = -1;
        terminated = false;
        invalidTag = 0;
        reader.mark(4096);
        String line;
        String patternName = "Unknown RLE Pattern"; // 기본 이름
        
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            
            // #N 으로 시작하면 패턴 이름을 추출합니다.
            if (line.startsWith("#N")) {
                patternName = line.substring(2).trim();
                reader.mark(4096);
                continue;
            }
            // 다른 주석(#)은 건너뜁니다.
            if (line.startsWith("#")) {
                reader.mark(4096);
                continue;
            }
            // x = ... 형태의 메타데이터 줄을 만나면 헤더가 끝난 것입니다.
            if (line.startsWith("x")) {
                java.util.regex.Matcher size = SIZE.matcher(line);
                if (size.find()) {
                    declaredWidth = Integer.parseInt(size.group(1));
                    declaredHeight = Integer.parseInt(size.group(2));
                }
                break;
            }
        }
        return patternName;
    }

    @Override
    protected ParsedData readBody(BufferedReader reader) throws IOException {
        List<Point> liveCells = new ArrayList<>();
        StringBuilder compressedData = new StringBuilder();
        String line;
        
        // 1. 본문 데이터 압축 문자열 잇기
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#") || line.startsWith("x")) continue;
            compressedData.append(line.trim());
            if (line.contains("!")) break; 
        }

        // 2. 압축 해제 및 바운딩 박스(최대 가로/세로) 계산
        int x = 0, y = 0, count = 0;
        int maxX = 0, maxY = 0; // 넓이/높이 측정을 위한 변수 추가
        
        for (int i = 0; i < compressedData.length(); i++) {
            char c = compressedData.charAt(i);
            
            if (Character.isDigit(c)) {
                count = count * 10 + (c - '0'); 
            } else {
                int run = (count == 0) ? 1 : count;
                count = 0; 
                
                if (c == 'b') {
                    x += run;
                } else if (c == 'o') { 
                    for (int j = 0; j < run; j++) {
                        liveCells.add(new Point(x, y));
                        // 최대 도달 좌표 갱신
                        maxX = Math.max(maxX, x);
                        maxY = Math.max(maxY, y);
                        x++;
                    }
                } else if (c == '$') {
                    y += run;
                    x = 0; 
                } else if (c == '!') {
                    terminated = true;
                    break;
                } else if (!Character.isWhitespace(c) && invalidTag == 0) {
                    invalidTag = c;
                }
            }
        }
        

        return new ParsedData(liveCells, maxX + 1, maxY + 1);
    }

    /** Header → body → here: the body must end with '!', use only b/o/$ tags and fit the x/y size line. */
    @Override
    protected void validate(ParsedData data) throws IOException {
        super.validate(data);
        if (invalidTag != 0)
            throw new IOException("RLE에서 쓸 수 없는 문자입니다: " + invalidTag);
        if (!terminated)
            throw new IOException("RLE 본문 끝에 '!'가 없습니다.");
        if (declaredWidth >= 0 && !data.liveCells.isEmpty()
                && (data.width > declaredWidth || data.height > declaredHeight))
            throw new IOException("패턴이 헤더 크기(x = " + declaredWidth + ", y = " + declaredHeight + ")를 벗어납니다.");
    }


    @Override
    protected void writeHeader(BufferedWriter writer) throws IOException {
        writer.write("#N Saved by Holub Life RLE Encoder");
        writer.newLine();
    }

    @Override
    protected void writeBody(BufferedWriter writer, Cell.Memento memento, int w, int h) throws IOException {
        writer.write("x = " + w + ", y = " + h + ", rule = B3/S23");
        writer.newLine();

        int lineLength = 0; 

        for (int y = 0; y < h; y++) {
            int runCount = 0;
            char currentState = ' ';
            
            for (int x = 0; x < w; x++) {
                char cellState = memento.isAlive(new Point(x, y)) ? 'o' : 'b';
                
                if (currentState == ' ') {
                    currentState = cellState;
                    runCount = 1;
                } else if (currentState == cellState) {
                    runCount++;
                } else {
                    lineLength = writeRun(writer, runCount, currentState, lineLength);
                    currentState = cellState;
                    runCount = 1;
                }
            }
            
            if (currentState == 'o' && runCount > 0) {
                lineLength = writeRun(writer, runCount, currentState, lineLength);
            }
            if (y == h - 1) {
                writer.write("!");
            } else {
                writer.write("$");
                lineLength++;
                if (lineLength > 65) { 
                    writer.newLine();
                    lineLength = 0; 
                }
            }
        }
        writer.newLine();
    }

    private int writeRun(BufferedWriter writer, int count, char state, int lineLength) throws IOException {
        String runStr = (count > 1 ? String.valueOf(count) : "") + state;
        writer.write(runStr);
        int newLength = lineLength + runStr.length();
        if (newLength > 65) {
            writer.newLine();
            return 0;
        }
        return newLength;
    }
}