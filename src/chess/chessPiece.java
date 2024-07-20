package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;

public class chessPiece extends Piece {
	
	private Color color;

	public chessPiece(Position postion, Board board, Color color) {
		super(postion, board);
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	

}
