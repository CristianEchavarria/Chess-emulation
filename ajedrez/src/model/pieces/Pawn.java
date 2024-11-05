package model.pieces;

import model.Board;

public class Pawn extends Piece {
    private boolean firstMove = true;
    private Board board;

    public Pawn(String color, Board board) {
        super(color);
        this.board = board;
    }

    public boolean isPromotionSquare(int x) {
        return (color.equals("white") && x == 0) || (color.equals("black") && x == 7);
    }

    @Override
    public boolean isValidMove(int startX, int startY, int endX, int endY) {
        int direction = color.equals("white") ? -1 : 1;

        // Movimiento hacia adelante sin captura
        if (startY == endY) {
            if (startX + direction == endX && board.getPieceAt(endX, endY) == null) {
                firstMove = false;
                return true;
            }
            // Primer movimiento: dos casillas hacia adelante sin captura
            if (firstMove && startX + 2 * direction == endX && board.getPieceAt(endX, endY) == null) {
                firstMove = false;
                return true;
            }
        }

        // Movimiento en diagonal para capturar
        if (Math.abs(startY - endY) == 1 && startX + direction == endX) {
            Piece targetPiece = board.getPieceAt(endX, endY);
            // Solo permite captura si hay una pieza de color opuesto en la posición diagonal
            if (targetPiece != null && !targetPiece.getColor().equals(this.color)) {
                return true;
            }
        }

        return false;
    }
}

