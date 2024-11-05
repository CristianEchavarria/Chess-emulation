package model.pieces;

import model.Board;

public class Rook extends Piece {
    private Board board;

    public Rook(String color, Board board) {
        super(color);
        this.board = board;
    }

    private boolean hasMoved = false;

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
    @Override
    public boolean isValidMove(int startX, int startY, int endX, int endY) {
        // Verifica que el movimiento sea en línea recta (horizontal o vertical)
        if (startX != endX && startY != endY) {
            return false;
        }

        // Determina la dirección del movimiento
        int stepX = Integer.compare(endX, startX); // 1 para derecha, -1 para izquierda, 0 si no se mueve en X
        int stepY = Integer.compare(endY, startY); // 1 para abajo, -1 para arriba, 0 si no se mueve en Y

        // Recorre las casillas intermedias
        int currentX = startX + stepX;
        int currentY = startY + stepY;
        while (currentX != endX || currentY != endY) {
            // Si hay una pieza en el camino, el movimiento no es válido
            if (board.isOccupied(currentX, currentY)) {
                return false;
            }
            currentX += stepX;
            currentY += stepY;
        }

        // Verifica si la casilla de destino tiene una pieza del mismo color
        Piece destinationPiece = board.getPieceAt(endX, endY);
        if (destinationPiece != null && destinationPiece.getColor().equals(this.color)) {
            return false;
        }

        // El movimiento es válido si no hay obstáculos y la casilla de destino no tiene una pieza del mismo color
        return true;
    }
}
