package com.holub.life;

import com.holub.ui.MenuSite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.Iterator;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JViewport;
import javax.swing.SpinnerNumberModel;

/** Swing input, menus and rendering for the board. */
final class UniversePanel extends JPanel {
    /** Themes offered in the View > Theme menu. The first one is the default. */
    static final List<BoardTheme> THEMES = List.of(new ClassicTheme(), new DarkTheme(), new HighContrastTheme());
    static final List<String> STAMPS = List.of("default", "glider", "toad", "block", "blink", "LWSS");
    
    private static final String GRID_LINES = "Grid Lines";
    private static final String CELL_AGE = "Cell Age Colors";
    private final FileCommands fileCommands;
    private final ClockCommands clockCommands;
    private final GridCommands gridCommands;
    private final StampCommands stampCommands;
    private final RuleCommands ruleCommands;
    private final LifeMediator mediator;
    private final LifeFacade facade;
    private BoardTheme theme = THEMES.get(0);
    private boolean showGridLines = true;
    private boolean showCellAge = false;
    private final CellAgeTracker cellAges;

    UniversePanel(LifeMediator mediator) {
        this(mediator, new FileCommands(mediator.facade().model()),
                new ClockCommands(mediator.facade().model()), new GridCommands(mediator.facade().model()),
                		new StampCommands(mediator), new RuleCommands(mediator.facade().model()));
    }

    UniversePanel(LifeMediator mediator, FileCommands fileCommands,
            ClockCommands clockCommands, GridCommands gridCommands,
            StampCommands stampCommands, RuleCommands ruleCommands) {
    	
        this.stampCommands = stampCommands;
    	this.fileCommands = fileCommands;
        this.clockCommands = clockCommands;
        this.gridCommands = gridCommands;
        this.ruleCommands = ruleCommands;
        this.mediator = mediator;
        this.facade = mediator.facade();
        this.cellAges = new CellAgeTracker(facade);
        setBackground(theme.background());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                int size = cellSize();
                int x = event.getX() / size;
                int y = event.getY() / size;
                if (event.getX() >= 0 && event.getY() >= 0 && x < facade.width() && y < facade.width())
                	stampCommands.stampAct(facade.model(), x, y).execute();
                	//mediator.toggle(x, y);
                	
            }
        });

        facade.addListener(event -> {
            if (event.change() == LifeFacade.Change.RESIZE) {
                revalidate();
                if (getParent() instanceof JViewport viewport)
                    viewport.setViewPosition(new Point());
            }
            repaint();
        });
    }

    void installMenus() {
    	Iterator<String> ruleIter = BSRule.getKeys();
        MenuSite.addLine(this, "Grid", "Clear", event -> mediator.clear());
        MenuSite.addLine(this, "Grid", "Load", event -> fileCommands.getLoad().execute());
        MenuSite.addLine(this, "Grid", "Store", event -> fileCommands.getSave().execute());
        MenuSite.addLine(this, "Grid", "Size...", event -> chooseSize());
        MenuSite.addLine(this, "Grid", "Exit", event -> System.exit(0));
        MenuSite.addLine(this, "Grid", "Undo", event -> gridCommands.getUndo().execute());
        MenuSite.addLine(this, "Grid", "Redo", event -> gridCommands.getRedo().execute());
        MenuSite.addLine(this, "Go", "Halt", event -> clockCommands.getHalt().execute());
        MenuSite.addLine(this, "Go", "Tick (Single Step)", event -> clockCommands.getTick().execute());
        MenuSite.addLine(this, "Go", "Agonizing", event -> clockCommands.runAgonizing().execute());
        MenuSite.addLine(this, "Go", "Slow", event -> clockCommands.runSlow().execute());
        MenuSite.addLine(this, "Go", "Medium", event -> clockCommands.runMedium().execute());
        MenuSite.addLine(this, "Go", "Fast", event -> clockCommands.runFast().execute());
        
        for(String stamp : STAMPS) {
        	MenuSite.addLine(this, "Stamp", stamp, event ->{
        		stampCommands.setStamp(stamp).execute();
        		refreshViewMenuLabels();
        	});
        } //STAMP Menus
        
        while(ruleIter.hasNext()) {
        	String item = ruleIter.next();
        	MenuSite.addLine(this, "Rule", item, event -> {
        		ruleCommands.setRule(item).execute();
        		refreshViewMenuLabels();
        	});
        }
        MenuSite.addLine(this,  "Rule",  "add rule", event -> addRules());
        //Rule Menus
        
        installViewMenus();
        
    }
    

    /**
     * View menu for E8. These only change how this panel draws, so they call the panel directly.
     * Nothing else (clock, model, other panels) has to react, so they do not go through the Mediator.
     */
    private void installViewMenus() {
        for (BoardTheme each : THEMES)
            MenuSite.addLine(this, "View:Theme", each.name(), event -> {
                setTheme(each);
                refreshViewMenuLabels();
            });
        MenuSite.addLine(this, "View", "-", null);
        MenuSite.addLine(this, "View", GRID_LINES, event -> {
            setShowGridLines(!showGridLines);
            refreshViewMenuLabels();
        });
        MenuSite.addLine(this, "View", CELL_AGE, event -> {
            setShowCellAge(!showCellAge);
            refreshViewMenuLabels();
        });
        refreshViewMenuLabels();
    }

    /** Shows the current choice in the menu text, e.g. "• Dark" and "Grid Lines: On". */
    private void refreshViewMenuLabels() {
    	Iterator<String> ruleIter = BSRule.getKeys();
    	while(ruleIter.hasNext()) {
    		String curr = ruleIter.next(); 
    		setMenuText("Rule", curr, choiceLabel(curr, BSRule.get(curr) == facade.model().rule()));
    	}
    	for (String stamp : STAMPS)
    		setMenuText("Stamp", stamp, choiceLabel(stamp, stamp == stampCommands.currStr()));
    	
        for (BoardTheme each : THEMES)
            setMenuText("View:Theme", each.name(), choiceLabel(each.name(), each == theme));
        
        setMenuText("View", GRID_LINES, toggleLabel(GRID_LINES, showGridLines));
        setMenuText("View", CELL_AGE, toggleLabel(CELL_AGE, showCellAge));
    }

    private void setMenuText(String menu, String name, String text) {
        JMenuItem item = MenuSite.getMyMenuItem(this, menu, name);
        if (item != null)
            item.setText(text);
    }

    static String choiceLabel(String name, boolean selected) {
        return selected ? "\u2022 " + name : name;
    }

    static String toggleLabel(String name, boolean on) {
        return name + (on ? ": On" : ": Off");
    }

    @Override
    public Dimension getPreferredSize() {
        int pixels = facade.width() * facade.cellPixels();
        return new Dimension(pixels, pixels);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        facade.visitCells(createRenderer(graphics, cellSize()));
    }

    /** Builds the decorator chain from the current theme and display options. */
    CellVisitor createRenderer(Graphics graphics, int cellSize) {
        CellVisitor renderer = theme.createCellPainter(graphics, cellSize);
        if (showCellAge)
            renderer = theme.createAgeColors(renderer, graphics, cellSize, cellAges);
        if (showGridLines)
            renderer = theme.createGridLines(renderer, graphics, cellSize);
        return theme.createBlockBorders(renderer, graphics, cellSize);
    }

    BoardTheme theme() {
        return theme;
    }

    boolean showsGridLines() {
        return showGridLines;
    }

    boolean showsCellAge() {
        return showCellAge;
    }

    void setTheme(BoardTheme theme) {
        this.theme = Objects.requireNonNull(theme);
        setBackground(theme.background());
        repaint();
    }

    void setShowCellAge(boolean show) {
        showCellAge = show;
        repaint();
    }

    void setShowGridLines(boolean show) {
        showGridLines = show;
        repaint();
    }

    private int cellSize() {
        return Math.max(1, Math.min(getWidth(), getHeight()) / facade.width());
    }

    private void chooseSize() {
        JSpinner block = new JSpinner(new SpinnerNumberModel(facade.blockSize(), 2, UniverseBuilder.MAX_WIDTH, 1));
        JSpinner levels = new JSpinner(new SpinnerNumberModel(facade.levels(), 1, 8, 1));
        JSpinner pixels = new JSpinner(new SpinnerNumberModel(facade.cellPixels(), 2, 32, 1));
        JPanel settings = new JPanel(new GridLayout(0, 2, 8, 4));
        settings.add(new JLabel("Block width"));
        settings.add(block);
        settings.add(new JLabel("Nesting levels"));
        settings.add(levels);
        settings.add(new JLabel("Pixels per cell"));
        settings.add(pixels);

        Object[] message = {"Current board: " + facade.width() + " × " + facade.width(),
                "New width = block width raised to the number of levels.",
                settings, "Applying these settings starts an empty board."};

        while (JOptionPane.showConfirmDialog(this, message, "Grid Size",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                block.commitEdit();
                levels.commitEdit();
                pixels.commitEdit();
                mediator.resize(new UniverseBuilder()
                        .blockSize((Integer) block.getValue())
                        .levels((Integer) levels.getValue())
                        .cellPixels((Integer) pixels.getValue()));
                return;
            } catch (ParseException exception) {
                showSizeError("Enter a valid number in each field.");
            } catch (IllegalArgumentException exception) {
                showSizeError(exception.getMessage());
            }
        }
    }
    
    private void addRules() {
    	JTextField ruleName = new JTextField("CONWAY", 16);
    	JTextField ruleDesc = new JTextField("B36/S23", 16);
    	JPanel panel = new JPanel(new GridLayout(2, 2, 8, 4));

    	panel.add(new JLabel("Rule Name"));
    	panel.add(ruleName);

    	panel.add(new JLabel("Rule"));
    	panel.add(ruleDesc);
    	
    	while(JOptionPane.showConfirmDialog(
    		    this,
    		    panel,
    		    "Add Rule",
    		    JOptionPane.OK_CANCEL_OPTION,
    		    JOptionPane.PLAIN_MESSAGE
    		) == JOptionPane.OK_OPTION) {
    		try{
    			String rName = ruleName.getText();
	    		String rDesc = ruleDesc.getText();
	    		
	    		if(BSRule.get(rName) != null) {
	    			showSizeError("Rule "+rName+" already Exist.");
	    		}
	    		else {
	    			ruleCommands.addRule(rName, rDesc).execute();
	    			JMenuItem add = MenuSite.getMyMenuItem(this,  "Rule", "add rule");
	    			
	    			JPopupMenu menu = (JPopupMenu) add.getParent();
	    			
	    			menu.remove(add);
	    			
	    			MenuSite.addLine(this, "Rule", rName, event -> {
	    				ruleCommands.setRule(rName).execute();
	    				refreshViewMenuLabels();
	    			});
	    			menu.add(add);
	    			menu.revalidate();
	    			menu.repaint();
	    			return;
	    		}
    		}catch(Exception e){
    			showSizeError(e.getMessage());
    		}
    	}
    }

    
    private void showSizeError(String message) {
        JOptionPane.showMessageDialog(this, message, "The Game of Life", JOptionPane.ERROR_MESSAGE);
    }
}
