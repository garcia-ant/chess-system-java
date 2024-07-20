package application;

import chess.chessPiece;

public class UI {

    public static void printBoard(chessPiece[][] pieces) {

        for (int i = 0; i < pieces.length; i++) {
            System.out.print((8 - i) + " ");
            for (int k = 0; k < pieces[i].length; k++) {
                printPiece(pieces[i][k]);
                System.out.print(" "); // Adiciona espaço entre as peças
            }
            System.out.println();
        }
        System.out.println(" a b c d e f g h");
    }

    public static void printPiece(chessPiece piece) {
        if (piece == null) {
            System.out.print("-");
        } else {
            System.out.print(piece);
        }
    }
}
