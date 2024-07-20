package application;

import javax.swing.UIClientPropertyKey;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMath;

public class Program {

	public static void main(String[] args) {
		
		ChessMath chessMath =new ChessMath();
		
		UI.printBoard(chessMath.getPieces());
	
	}

}
