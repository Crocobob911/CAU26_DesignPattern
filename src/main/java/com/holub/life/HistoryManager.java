package com.holub.life;
import java.util.Stack;

public class HistoryManager {
    private static final Stack<UniverseSnapshot> undoStack = new Stack<>();
    private static final Stack<UniverseSnapshot> redoStack = new Stack<>();
    private static boolean ignoreSave = false; 

    public static void setIgnoreSave(boolean ignore) {
        ignoreSave = ignore;
    }

    public static void saveState(UniverseModel model) {
        if (ignoreSave) return;
        UniverseSnapshot snapshot = new UniverseSnapshot(model.createMemento(), model.generation());
        undoStack.push(snapshot);
        redoStack.clear(); 
    }

    public static boolean canUndo() { return !undoStack.isEmpty(); }
    public static boolean canRedo() { return !redoStack.isEmpty(); }

    public static UniverseSnapshot popUndo() { return undoStack.pop(); }
    public static UniverseSnapshot popRedo() { return redoStack.pop(); }
    
    public static void pushUndo(UniverseSnapshot snapshot) { undoStack.push(snapshot); }
    public static void pushRedo(UniverseSnapshot snapshot) { redoStack.push(snapshot); }
}