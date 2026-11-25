package com.holub.life;
import java.util.Stack;

public class HistoryManager {
    /** Undo/Redo can each go back at most this many steps; the oldest snapshot is dropped first. */
    public static final int MAX_HISTORY = 100;

    private static final Stack<UniverseSnapshot> undoStack = new Stack<>();
    private static final Stack<UniverseSnapshot> redoStack = new Stack<>();
    private static boolean ignoreSave = false; 

    public static void setIgnoreSave(boolean ignore) {
        ignoreSave = ignore;
    }

    public static boolean isIgnoringSave() {
        return ignoreSave;
    }

    public static void saveState(UniverseModel model) {
        if (ignoreSave) return;
        UniverseSnapshot snapshot = new UniverseSnapshot(model.createMemento(), model.generation());
        pushUndo(snapshot);
        redoStack.clear(); 
    }

    public static void clearHistory() {
        undoStack.clear();
        redoStack.clear();
    }

    public static boolean canUndo() { return !undoStack.isEmpty(); }
    public static boolean canRedo() { return !redoStack.isEmpty(); }

    public static UniverseSnapshot popUndo() { return undoStack.pop(); }
    public static UniverseSnapshot popRedo() { return redoStack.pop(); }
    
    public static void pushUndo(UniverseSnapshot snapshot) { push(undoStack, snapshot); }
    public static void pushRedo(UniverseSnapshot snapshot) { push(redoStack, snapshot); }

    private static void push(Stack<UniverseSnapshot> stack, UniverseSnapshot snapshot) {
        stack.push(snapshot);
        while (stack.size() > MAX_HISTORY)
            stack.remove(0);
    }
}