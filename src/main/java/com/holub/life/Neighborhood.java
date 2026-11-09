package com.holub.life;

import java.awt.Point;
import java.util.*;
import java.io.*;

/***
 * A group of {@link Cell} objects. Cells are grouped into neighborhoods
 * to make board updates more efficient. When a neighborhood is
 * quiescent (none of the Cells it contains are active), then it
 * ignores any requests to update itself.
 *
 * <h3>History</h3>
 * <p>11-29-04
 * Alexy Marinichev fixed the disapearing-glider problem by clearing
 * the active edges in transistion() rather then figureNextState().
 * The original call is commented out and the new line is marked 
 * with "(1)"
 *
 * @include /etc/license.txt
 */

public final class Neighborhood implements Cell
{
	/** Returns true only if none of the cells in the Neighborhood
	 *  changed state during the last transition.
	 */

	private boolean amActive = false;

	/** The actual grid of Cells contained within this neighborhood. */
	private final Cell[][] grid;

	/** The neighborhood is square, so gridSize is both the horizontal
	 *  and vertical size.
	 */
	private final int 	   gridSize;

	/** Create a new Neigborhood containing gridSize-by-gridSize
	 *  clones of the prototype. The Protype is deliberately
	 *  not put into the grid, so you can reuse it if you like.
	 */

	public Neighborhood(int gridSize, Cell prototype)
	{
		this.gridSize = gridSize;
 		this.grid = new Cell[gridSize][gridSize];

		for( int row = 0; row < gridSize; ++row )
			for( int column = 0; column < gridSize; ++column )
				grid[row][column] = prototype.create();
	}

	/** The "clone" method used to create copies of the current
	 *  neighborhood. This method is called from the containing
	 *  neighborhood's constructor. (The current neighborhood
	 *  is passed into the containing-neighborhood constructor
	 *  as the "prototype" argument.
	 */

	public Cell create()
	{	return new Neighborhood(gridSize, grid[0][0]);
	}

	/** Shows the direction of the cells along the edge of the block 
	 *  that will change  state in the next transition. For example,
	 *  if the upper-left corner has changed, then the current
	 *  Cell is disruptive in the NORTH, WEST, and NORTHWEST directions.
	 *  If this is the case, the neigboring
	 *  cells may need to be updated, even if they were previously
	 *  stable.
	 */
	public  Direction isDisruptiveTo(){ return activeEdges; }
	private	Direction activeEdges = new Direction( Direction.NONE );

	/** Figures the next state of the current neighborhood and the
	 *  contained neigborhoods (or cells). Does not transition to the
	 *  next state, however. Note that the neighboring cells are passed
	 *  in as arguments rather than being stored internally---an
	 *  example of the Flyweight pattern.
	 *
	 *  @see #transition
	 *  @param north		The neighbor to our north
	 *  @param south		The neighbor to our south
	 *  @param east			The neighbor to our east
	 *  @param west			The neighbor to our west
	 *  @param northeast	The neighbor to our northeast
	 *  @param northwest	The neighbor to our northwest
	 *  @param southeast	The neighbor to our southeast
	 *  @param southwest	The neighbor to our southwest
	 * 
	 *  @return true if this neighborhood (i.e. any of it's cells)
	 *  			 will change state in the next transition.
	 */

	public boolean figureNextState(	Cell north, 	Cell south,
									Cell east,		Cell west,
									Cell northeast, Cell northwest,
									Cell southeast, Cell southwest )
	{
		boolean	nothingHappened = true;

		// Is some ajacent neigborhood active on the edge
		// that ajoins me?

		if(		amActive
			||	north	 .isDisruptiveTo().the( Direction.SOUTH 	  )
			||	south	 .isDisruptiveTo().the( Direction.NORTH 	  )
			||	east	 .isDisruptiveTo().the( Direction.WEST  	  )
			||	west	 .isDisruptiveTo().the( Direction.EAST  	  )
			||	northeast.isDisruptiveTo().the( Direction.SOUTHWEST )
			||	northwest.isDisruptiveTo().the( Direction.SOUTHEAST )
			||	southeast.isDisruptiveTo().the( Direction.NORTHWEST )
			||	southwest.isDisruptiveTo().the( Direction.NORTHEAST )
		)
		{
			Cell	northCell,		southCell,
					eastCell,		westCell,
					northeastCell, northwestCell,
					southeastCell, southwestCell;

			// activeEdges.clear();
					
			for( int row = 0; row < gridSize; ++row )
			{	for( int column = 0; column < gridSize; ++column )
				{
					// Get the current cell's eight neighbors

					if(row == 0 )		//{=Neighborhood.get.neighbors}
					{	northwestCell = (column==0)
							? northwest.edge(gridSize-1,gridSize-1)
							: north.edge	(gridSize-1,column-1)
							;

						northCell=	north.edge(gridSize-1,column);

						northeastCell = (column == gridSize-1 )
							? northeast.edge (gridSize-1, 0)
							: north.edge	 (gridSize-1, column+1)
							;
					}
					else
					{	northwestCell  = (column == 0)
							? west.edge(row-1, gridSize-1)
							: grid[row-1][column-1]
							;

						northCell = grid[row-1][column];

						northeastCell = (column == gridSize-1)
							? east.edge(row-1, 0)
							: grid[row-1][column+1]
							;
					}

					westCell = (column == 0)
							? west.edge( row, gridSize-1)
							: grid[row][column-1]
							;

					eastCell = (column == gridSize-1)
							? east.edge(row, 0)
							: grid[row][column+1]
							;

					if(row == gridSize-1)
					{	southwestCell = ( column==0 )
							? southwest.edge(0,gridSize-1)
							: south.edge(0,column-1)
							;

						southCell = south.edge(0,column);

						southeastCell = (column == gridSize-1 )
							? southeast.edge(0,0)
							: south.edge(0, column+1)
							;
					}
					else
					{	southwestCell  = (column == 0)
							? west.edge(row+1, gridSize-1)
							: grid[row+1][column-1]
							;

						southCell = grid[row+1][column];

						southeastCell = (column == gridSize-1)
							? east.edge(row+1, 0)
							: grid[row+1][column+1]
							;
					}

					// Tell the cell to change its state. If
					// the cell changed (the figureNextState request
					// returned false), then mark the current block as
					// unstable. Also, if the unstable cell is on the
					// edge of the block modify activeEdges to
					//  indicate which edge or edges changed. 

					if( grid[row][column].figureNextState
						( northCell, 	 southCell,
						  eastCell,	  	 westCell,
						  northeastCell, northwestCell,
						  southeastCell, southwestCell
						)
					)
					{   nothingHappened = false;
					}
				}
			}
		}

		amActive = !nothingHappened;
		return amActive;
	}


	/** Transition the neighborhood to the previously-computed
	 *  state.
	 *  @return true if the transition actually changed anything.
	 *  @see #figureNextState
	 */
	public boolean transition()
	{	
		boolean someSubcellChangedState = false;

		activeEdges.clear();							/*(1)*/

		for( int row = 0; row < gridSize; ++row ) //{=transition.start}
			for( int column = 0; column < gridSize; ++column )
			{	if( grid[row][column].transition() )
				{	rememberThatCellAtEdgeChangedState(row, column);
					someSubcellChangedState = true;
				}								 //{=transition.end}
			}
		
		return someSubcellChangedState;
	}

	/** Modifies activeEdges to indicate whether the addition
	 *  of the cell at (row,column) makes an edge active.
	 */
	private void rememberThatCellAtEdgeChangedState(int row,int column)
	{
		if( row == 0 )
		{	activeEdges.add( Direction.NORTH );

			if(column==0)
				activeEdges.add( Direction.NORTHWEST );
			else if(column==gridSize-1)
				activeEdges.add( Direction.NORTHEAST );
		}
		else if( row == gridSize-1 )
		{	activeEdges.add( Direction.SOUTH );

			if(column==0)
				activeEdges.add( Direction.SOUTHWEST );
			else if(column==gridSize-1)
				activeEdges.add( Direction.SOUTHEAST );
		}

		if( column == 0 )
		{	activeEdges.add( Direction.WEST );
		}
		else if( column == gridSize-1 )
		{	activeEdges.add( Direction.EAST );
		}
		// else it's an internal cell. Do nothing.
	}

	public void accept(CellVisitor visitor, int x, int y)
	{
		int size = widthInCells();
		if (!visitor.enterNeighborhood(x, y, size))
			return;

		int stride = size / gridSize;
		for (int row = 0; row < gridSize; row++)
			for (int column = 0; column < gridSize; column++)
				grid[row][column].accept(visitor, x + column * stride, y + row * stride);

		visitor.leaveNeighborhood(x, y, size, amActive);
	}

	/** Return the edge cell in the indicated row and column.
	 */
	public Cell	edge(int row, int column)
	{	assert 	  (row	  == 0 || row	 == gridSize-1)
			   || (column == 0 || column == gridSize-1)
			   :  "central cell requested from edge()";

		return grid[row][column];
	}

	public boolean isAliveAt(int x, int y)
	{
		int stride = grid[0][0].widthInCells();
		return grid[y / stride][x / stride].isAliveAt(x % stride, y % stride);
	}

	public boolean setAliveAt(int x, int y, boolean alive)
	{
		int stride = grid[0][0].widthInCells();
		int row = y / stride;
		int column = x / stride;
		if (!grid[row][column].setAliveAt(x % stride, y % stride, alive))
			return false;

		amActive = true;
		rememberThatCellAtEdgeChangedState(row, column);
		return true;
	}

	public void setRule(LifeRule rule)
	{
		for (Cell[] row : grid)
			for (Cell cell : row)
				cell.setRule(rule);
	}

	void activateAll()
	{
		amActive = true;
		for (Cell[] row : grid)
			for (Cell cell : row)
				if (cell instanceof Neighborhood neighborhood)
					neighborhood.activateAll();
	}

	public boolean isAlive()
	{	return true;
	}

	public int widthInCells()
	{	return gridSize * grid[0][0].widthInCells();
	}

	public void clear()
	{	activeEdges.clear();

		for( int row = 0; row < gridSize; ++row )
			for( int column = 0; column < gridSize; ++column )
				grid[row][column].clear();

		amActive = false;
	}

	/** Cause subcells to add an annotation to the indicated
	 *  memento if they happen to be alive.
	 */

	public boolean transfer(Storable memento, Point corner,
														boolean load)
	{ 	int   subcellWidth	= grid[0][0].widthInCells();
		int   myWidth		= widthInCells();
		Point upperLeft	= new Point( corner );

		for( int row = 0; row < gridSize; ++row )
		{   for( int column = 0; column < gridSize; ++column )
			{	if(grid[row][column].transfer(memento,upperLeft,load))
					amActive = true;

				Direction d =
						grid[row][column].isDisruptiveTo();

				if( !d.equals( Direction.NONE ) )
					activeEdges.add(d);

				upperLeft.translate( subcellWidth, 0);
			}
			upperLeft.translate(-myWidth, subcellWidth );
		}
		return amActive;
	}

	public Storable createMemento()
	{	Memento m = emptyMemento();
		transfer(m, new Point(0,0), Cell.STORE);
		return m;
	}

	/**
	 * The NeighborhoodState stores the state of this neighborhood
	 * and all its sub-neighborhoods. For the moment, I'm storing
	 * state with serialization, but a future modification might
	 * rewrite load() and flush() to use XML.
	 */

	static Memento emptyMemento()
	{	return new NeighborhoodState();
	}

	private static class NeighborhoodState implements Cell.Memento
	{
		private Set<Point> liveCells = new LinkedHashSet<>();

		public void load(InputStream in) throws IOException
		{
			ObjectInputStream source = new ObjectInputStream(in);
			source.setObjectInputFilter(info -> {
				if (info.depth() > 3 || info.references() > 1_000_000)
					return ObjectInputFilter.Status.REJECTED;
				Class<?> type = info.serialClass();
				if (type == null)
					return ObjectInputFilter.Status.UNDECIDED;
				return type == LinkedList.class || type == Point.class
						? ObjectInputFilter.Status.ALLOWED
						: ObjectInputFilter.Status.REJECTED;
			});
			try
			{
				if (!(source.readObject() instanceof LinkedList<?> points))
					throw new IOException("Expected a list of live cells");
				Set<Point> loaded = new LinkedHashSet<>();
				for (Object entry : points)
				{
					if (!(entry instanceof Point point))
						throw new IOException("Invalid live cell");
					loaded.add(point);
				}
				liveCells = loaded;
			}
			catch (ClassNotFoundException e)
			{
				throw new IOException("Unable to read life file", e);
			}
		}

		public void flush(OutputStream out) throws IOException
		{
			ObjectOutputStream sink = new ObjectOutputStream(out);
			sink.writeObject(new LinkedList<>(liveCells));
			sink.flush();
		}

		public void markAsAlive(Point location)
		{	liveCells.add(new Point(location));
		}

		public boolean isAlive(Point location)
		{	return liveCells.contains(location);
		}
	}
}
