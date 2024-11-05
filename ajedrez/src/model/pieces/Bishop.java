package model.pieces;

import model.Board;

public class Bishop extends Piece {
    private Board board;

    public Bishop(String color, Board board) {
        super(color);
        this.board = board;
    }

    @Override
    public boolean isValidMove(int startX, int startY, int endX, int endY) {
        // Verifica que el movimiento sea en diagonal
        if (Math.abs(startX - endX) != Math.abs(startY - endY)) {
            return false;
        }

        // Determina la dirección del movimiento
        int stepX = (endX - startX) > 0 ? 1 : -1;
        int stepY = (endY - startY) > 0 ? 1 : -1;

        // Recorre las casillas intermedias
        int currentX = startX + stepX;
        int currentY = startY + stepY;
        while (currentX != endX && currentY != endY) {
            // Si hay una pieza en el camino, el movimiento no es válido
            if (board.isOccupied(currentX, currentY)) {
                return false;
            }
            currentX += stepX;
            currentY += stepY;
        }

        // Verifica la casilla final para evitar atacar piezas del mismo color
        Piece destinationPiece = board.getPieceAt(endX, endY);
        if (destinationPiece != null && destinationPiece.getColor().equals(this.color)) {
            return false;
        }

        // Movimiento válido si no hay piezas del mismo color en la casilla final
        return true;
    }
}
