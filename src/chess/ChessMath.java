package chess;

import boardgame.Board;

public class ChessMath {
	
	private Board board;
	
	public ChessMath() {
		
		board = new Board(8,8);
	}
	
	public chessPiece[][] getPieces(){
		
		chessPiece[][] mat = new chessPiece[board.getRows()]
				[board.getColums()];
		for(int i=0;i<board.getRows();i++) {
			
			for(int k=0; k < board.getColums();k++) {
				mat[i][k] = (chessPiece) board.piece(i, k);
			}
		}
		return mat;
	}

}
