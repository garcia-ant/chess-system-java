package application;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import chess.ChessException;
import chess.ChessMath;
import chess.ChessPiece;
import chess.ChessPosition;

public class Program {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        ChessMath chessMatch = new ChessMath();
        List<ChessPiece> captured = new ArrayList<>();
        
        while (!chessMatch.getCheckMate()) {
           try {
                UI.clearScreen();
                UI.printMatch(chessMatch,captured);
                System.out.println();
                System.out.print("Source: ");
                ChessPosition source = UI.readChessPosition(sc);
                boolean[][] possibleMoves = chessMatch.possibleMoves(source);
                UI.clearScreen();
                UI.printBoard(chessMatch.getPieces(), possibleMoves);
                System.out.println();
                System.out.print("Target: ");
                ChessPosition target = UI.readChessPosition(sc);
                
                ChessPiece capturedPiece = chessMatch.performChessMove(source, target);
                if(captured != null) {
                	captured.add(capturedPiece);
                }
                if(chessMatch.getPromoted() != null) {
                	System.out.print("Enter piece for promtion (B/N/R/Q): ");
                	String type = sc.next().toUpperCase();
                	while(!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
                		System.out.print("Invaled value! Enter piece for promtion (B/N/R/Q): ");
                    	type = sc.next().toUpperCase();	
                	}
                	chessMatch.replacePromotedPeice(type);
                }
                
           } catch (ChessException e) {
                System.out.println(e.getMessage());
                sc.nextLine();
            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
                sc.nextLine();
            }
        }
        UI.clearScreen();
        UI.printMatch(chessMatch, captured);
    }
}
