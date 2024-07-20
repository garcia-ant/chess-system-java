package boardgame;

public class Piece {

	protected Position postion;
	private Board board;
	
	public Piece(Position postion, Board board) {
		this.board = board;
		postion = null;
	}

	protected Board getBoard() {
		return board;
	}

}
