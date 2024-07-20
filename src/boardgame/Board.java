package boardgame;

public class Board {

    private int rows;
    private int columns;
    private Piece[][] pieces;

    public Board(int rows, int columns) {
    	if(rows < 1 || columns < 1) {
    		throw new BoardException("Error creating board: there must br at least 1 row and 1  column");	
    	}
        this.rows = rows;
        this.columns = columns;
        pieces = new Piece[rows][columns];
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public Piece piece(int row, int column) {
    	if(!PositionExists(row,column)) {
    		throw new BoardException("Position not on the board");
    	}
        return pieces[row][column];
    }

    public Piece piece(Position position) {
    	if(!PositionExists(position)) {
    		throw new BoardException("Position not on the board");
    	}
        return pieces[position.getRow()][position.getColumn()];
    }

    public void placePiece(Piece piece, Position position) {
    	if(thereISAPiece(position)) {
    		throw new BoardException("The is already a piece on position " + position);
    	}
    	pieces[position.getRow()][position.getColumn()] = piece;
        piece.position = position;
    }
    
    public boolean PositionExists(int row, int column) {
    	return row >= 0 && row < rows && column >= 0 && column < columns; 
    }
    
    public boolean PositionExists(Position position) {
    	return PositionExists(position.getRow(),position.getColumn());
    }
    public boolean thereISAPiece(Position position) {
    	if(!PositionExists(position)) {
    		throw new BoardException("Position not on the board");
    	}
    		return piece(position) != null;
    }
}
