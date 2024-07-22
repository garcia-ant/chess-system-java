package chess_pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Bishop extends ChessPiece {

    public Bishop(Board board, Color color) {
        super(board, color);
    }

    private boolean canMove(Position position) {
        ChessPiece p = (ChessPiece) getBoard().piece(position);
        return p == null || p.getColor() != getColor();
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
        Position p = new Position(0, 0);

        // nw
        p.setValues(position.getRow() - 1, position.getColumn()-1);
        while (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
            
            if (getBoard().piece(p) != null && ((ChessPiece) getBoard().piece(p)).getColor() != getColor()) {
                break;
            }
            p.setValues(p.getRow()-1, p.getColumn() -1 );
        }

        // ne
        p.setValues(position.getRow() - 1, position.getColumn()+1);
        while (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
            if (getBoard().piece(p) != null && ((ChessPiece) getBoard().piece(p)).getColor() != getColor()) {
                break;
            }
            p.setValues(p.getRow() -1,p.getColumn() +1);
        }

        // se
        p.setValues(position.getRow() +1, position.getColumn() + 1);
        while (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
            if (getBoard().piece(p) != null && ((ChessPiece) getBoard().piece(p)).getColor() != getColor()) {
                break;
            }
            p.setValues(p.getRow() +1,p.getColumn() +1);
        }

        // sw
        p.setValues(position.getRow() +1 , position.getColumn() - 1);
        while (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
            if (getBoard().piece(p) != null && ((ChessPiece) getBoard().piece(p)).getColor() != getColor()) {
                break;
            }
            p.setValues(p.getRow() + 1,p.getColumn() -1);
        }

        return mat;
    }
    @Override
    public String toString() {
        return "B";
    }
}
