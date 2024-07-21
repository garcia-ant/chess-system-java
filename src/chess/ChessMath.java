package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess_pieces.King;
import chess_pieces.Rook;

public class ChessMath {
    private int turn;
    private Color currentPlayer;
	private Board board;
	private boolean check;
	
	private List<Piece> piecesOnTheBoard ;
	private List<Piece> capturedPieces ;
    
	public ChessMath() {
        board = new Board(8, 8);
        turn =1;
        currentPlayer =Color.WHITE;
        piecesOnTheBoard = new ArrayList<>();
        capturedPieces = new ArrayList<>();
        initialSetup();
    }
    public int getTurn() {
    	return turn;
    }
    
    public Color getCurrentPlayer() {
    	return currentPlayer;
    }

    public boolean getCheck() {
    	return check;
    }
    
    
    
    
    public void setCurrentPlayer(Color currentPlayer) {
		this.currentPlayer = currentPlayer;
	}
	public ChessPiece[][] getPieces() {
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat;
    }

    public boolean[][] possibleMoves(ChessPosition position) {
        Position pos = position.toPosition();
        validateSourcePosition(pos);
        return board.piece(pos).possibleMoves();
    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Piece capturedPiece = makeMove(source, target);
        if(TestCheck(currentPlayer)) {
        	undMove(source, target, capturedPiece);
        	throw new ChessException("You can't put yourself in check");
        }
        check = (TestCheck(oppenent(currentPlayer)) ? true :false );
        
        nextTurn();
        return (ChessPiece) capturedPiece;
    }

    private Piece makeMove(Position source, Position target) {
    	 Piece p = board.removePiece(source);
         Piece capturedPiece = board.removePiece(target);
         board.placePiece(p, target);  
         
         if (capturedPiece != null) {
             piecesOnTheBoard.remove(capturedPiece);
             capturedPieces.add(capturedPiece);
         }
         
         return capturedPiece;
    }
    private void undMove(Position source, Position target, Piece capturedPiece) {
    	Piece p = board.removePiece(target);
    	board.placePiece(p, source);
    	
    	if(capturedPiece != null) {
    		board.placePiece(capturedPiece, target);
    		capturedPieces.remove(capturedPiece);
    		piecesOnTheBoard.add(capturedPiece);
    	}	
    }
    

    private void validateSourcePosition(Position position) {
        if (!board.thereIsAPiece(position)) {
            throw new ChessException("There is no piece on source position");
        }
        if(currentPlayer !=((ChessPiece)board.piece(position)).getColor() ) {
        	throw new ChessException("The chosen piece is not yours");	
        }
        if (!board.piece(position).isThereAnyPossibleMove()) { 
            throw new ChessException("There is no possible moves for the chosen piece");
        }
    }

    private void validateTargetPosition(Position source, Position target) {
        if (!board.piece(source).possibleMove(target)) {
            throw new ChessException("The chosen piece can't move to target position");
        }
    }

    private void nextTurn() {
    	turn ++;
    	currentPlayer = (currentPlayer==Color.WHITE) ? Color.BLACK : Color.WHITE;
    }
    
    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }
    
    private Color oppenent(Color color) {
    	return (color == Color.WHITE) ? color.BLACK : Color.WHITE;
    }
    
    private ChessPiece king(Color color) {
    	List<Piece> list = piecesOnTheBoard.stream().filter(x ->((ChessPiece)x).getColor() == color).collect(Collectors.toList());
    	for(Piece p : list) {
    	if(p instanceof King) {
    		
    		return (ChessPiece)p;
    	}	
    	}
    	throw  new IllegalStateException("There is no " + color + "King on the board");
    }
    
    private boolean TestCheck(Color color) {
    	Position kingPosition = king(color).getChessPosition().toPosition();
    	List<Piece> listOpponentPieces = piecesOnTheBoard.stream().filter(x ->((ChessPiece)x).getColor() == oppenent(color)).collect(Collectors.toList());
    	
    	for(Piece p :listOpponentPieces) {
    		boolean [][] mat = p.possibleMoves();
    		if(mat[kingPosition.getRow()][kingPosition.getColumn()]) {
    			return true;
    		}
    		
    	}
    	return false;
    }

    private void initialSetup() {
        // Here you should place the initial chess pieces on the board.
    	placeNewPiece('c', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new King(board, Color.WHITE));

        placeNewPiece('c', 7, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 8, new King(board, Color.BLACK));
        // Add all other pieces...
    }
}
