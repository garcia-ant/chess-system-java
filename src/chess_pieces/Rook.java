package chess_pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Rook extends ChessPiece {

    public Rook(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String toString() {
        return "R";
    }

    private boolean canMove(Position position) {
        ChessPiece p = (ChessPiece) getBoard().piece(position);
        return p == null || p.getColor() != getColor();
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
        Position p = new Position(0, 0);

        // Up
        p.setValues(position.getRow() - 1, position.getColumn());
        while (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
            if (getBoard().piece(p) != null && ((ChessPiece) getBoard().piece(p)).getColor() != getColor()) {
                break;
            }
            p.setRow(p.getRow() - 1);
        }

        // Down
        p.setValues(position.getRow() + 1, position.getColumn());
        while (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
            if (getBoard().piece(p) != null && ((ChessPiece) getBoard().piece(p)).getColor() != getColor()) {
                break;
            }
            p.setRow(p.getRow() + 1);
        }

        // Left
        p.setValues(position.getRow(), position.getColumn() - 1);
        while (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
            if (getBoard().piece(p) != null && ((ChessPiece) getBoard().piece(p)).getColor() != getColor()) {
                break;
            }
            p.setColumn(p.getColumn() - 1);
        }

        // Right
        p.setValues(position.getRow(), position.getColumn() + 1);
        while (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
            if (getBoard().piece(p) != null && ((ChessPiece) getBoard().piece(p)).getColor() != getColor()) {
                break;
            }
            p.setColumn(p.getColumn() + 1);
        }

        return mat;
    }
}
