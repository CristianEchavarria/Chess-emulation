package model.pieces;

import model.Board;

public class Knight extends Piece {
    private Board board;

    public Knight(String color, Board board) {
        super(color);
        this.board = board;
    }

    @Override
    public boolean isValidMove(int startX, int startY, int endX, int endY) {
        int dx = Math.abs(startX - endX);
        int dy = Math.abs(startY - endY);

        // Verifica si el movimiento tiene la forma de "L"
        if ((dx == 2 && dy == 1) || (dx == 1 && dy == 2)) {
            // Verifica si la casilla de destino está ocupada por una pieza del mismo color
            Piece destinationPiece = board.getPieceAt(endX, endY);
            if (destinationPiece != null && destinationPiece.getColor().equals(this.color)) {
                return false;
            }
            return true;
        }

        return false;
    }
}
