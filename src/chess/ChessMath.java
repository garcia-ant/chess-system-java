package chess;

import java.nio.channels.IllegalSelectorException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.plaf.ColorUIResource;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess_pieces.Bishop;
import chess_pieces.King;
import chess_pieces.Knigth;
import chess_pieces.Pawn;
import chess_pieces.Queen;
import chess_pieces.Rook;

public class ChessMath {
	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;

	private List<Piece> piecesOnTheBoard;
	private List<Piece> capturedPieces;

	public ChessMath() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
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

	public boolean getCheckMate() {
		return checkMate;
	}

	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;

	}

	public ChessPiece getPromoted() {
		return promoted;

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
		
		if (TestCheck(currentPlayer)) {
			undMove(source, target, capturedPiece);
			throw new ChessException("You can't put yourself in check");
		}

		ChessPiece movePiece = (ChessPiece) board.piece(target);
		
		//#special move promotion
		promoted = null;
		if(movePiece instanceof Pawn) {
			if((movePiece.getColor() == Color.WHITE && target.getRow() == 0) || (movePiece.getColor() == Color.BLACK && target.getRow() == 7) ) {
				promoted = (ChessPiece)board.piece(target);
				promoted = replacePromotedPeice("Q");
			}
		}

		check = (TestCheck(opponent(currentPlayer)) ? true : false);

		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		} else {
			nextTurn();
		}
		// Special move en passant
		if (movePiece instanceof Pawn
				&& (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
			enPassantVulnerable = movePiece;
		} else {
			enPassantVulnerable = null;
		}

		return (ChessPiece) capturedPiece;
	}
	public ChessPiece replacePromotedPeice(String type) {
		if(promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted");
		}
		if(!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
			throw new InvalidParameterException("Invalid type for promotion");
		}
		Position pos = promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesOnTheBoard.remove(p);
		
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnTheBoard.add(newPiece);
		
		return newPiece;
	}
	
	private ChessPiece newPiece(String type, Color color) {
	    if (type.equals("B")) return new Bishop(board, color);
	    if (type.equals("N")) return new Knigth(board, color);
	    if (type.equals("Q")) return new Queen(board, color);
	    return new Rook(board, color);
	}


	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece) board.removePiece(source);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(p, target);

		if (capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}

		// #SpecialMove castling Kingside rook
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece) board.removePiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}

		// #SpecialMove castling Queenside rook
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece) board.removePiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}

		// Specialmove en passant
		if (p instanceof Pawn) {
			if (source.getColumn() != target.getColumn() && capturedPiece == null) {
				Position pawPosition;
				if (p.getColor() == Color.WHITE) {
					pawPosition = new Position(target.getRow() + 1, target.getColumn());
				} else {
					pawPosition = new Position(target.getRow() - 1, target.getColumn());
				}
				capturedPiece = board.removePiece(pawPosition);
				capturedPieces.add(capturedPiece);
				piecesOnTheBoard.remove(capturedPiece);
			}

		}

		return capturedPiece;
	}

	private void undMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece) board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, source);

		if (capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece) board.removePiece(targetT);
			board.placePiece(rook, sourceT);
			rook.decreaseMoveCount();
		}

		// #SpecialMove castling Queenside rook
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece) board.removePiece(targetT);
			board.placePiece(rook, sourceT);
			rook.increaseMoveCount();
		}
		// Specialmove en passant
		if (p instanceof Pawn) {
			if (source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable) {
				ChessPiece pawn = (ChessPiece) board.removePiece(target);

				Position pawPosition;

				if (p.getColor() == Color.WHITE) {
					pawPosition = new Position(3, target.getRow());
				} else {
					pawPosition = new Position(4, target.getRow());
				}
				board.placePiece(pawn, pawPosition);
			}

		}

	}

	private void validateSourcePosition(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position");
		}
		if (currentPlayer != ((ChessPiece) board.piece(position)).getColor()) {
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
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}

	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private ChessPiece king(Color color) {
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece) p;
			}
		}
		throw new IllegalStateException("There is no " + color + " King on the board");
	}

	private boolean TestCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> listOpponentPieces = piecesOnTheBoard.stream()
				.filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());

		for (Piece p : listOpponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}

	public boolean testCheckMate(Color color) {
		if (!TestCheck(color)) {
			return false;
		}
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());
		for (Piece p : list) {
			boolean[][] mat = p.possibleMoves();
			for (int i = 0; i < board.getRows(); i++) {
				for (int j = 0; j < board.getColumns(); j++) {
					if (mat[i][j]) {
						Position source = ((ChessPiece) p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = TestCheck(color);
						undMove(source, target, capturedPiece);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void initialSetup() {
		// Setup the initial pieces on the board

		// Torres brancas
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('b', 1, new Knigth(board, Color.WHITE));
		placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('d', 1, new Queen(board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(board, Color.WHITE));
		placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('g', 1, new Knigth(board, Color.WHITE));
		// Rei branco
		placeNewPiece('e', 1, new King(board, Color.WHITE, this));

		// Peões brancos
		for (char i = 'a'; i <= 'h'; i++) {
			placeNewPiece(i, 2, new Pawn(board, Color.WHITE, this));
		}

		// Torres pretas
		placeNewPiece('a', 8, new Rook(board, Color.BLACK));
		placeNewPiece('b', 8, new Knigth(board, Color.BLACK));
		placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('d', 8, new Queen(board, Color.BLACK));
		placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('h', 8, new Rook(board, Color.BLACK));
		placeNewPiece('g', 8, new Knigth(board, Color.BLACK));

		// Rei preto
		placeNewPiece('e', 8, new King(board, Color.BLACK, this));

		// Peões pretos
		for (char i = 'a'; i <= 'h'; i++) {
			placeNewPiece(i, 7, new Pawn(board, Color.BLACK, this));
		}
	}
}
