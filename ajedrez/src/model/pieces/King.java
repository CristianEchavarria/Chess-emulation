package model.pieces;

import model.Board;

public class King extends Piece {
    private Board board;

    public King(String color, Board board) {
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
        int dx = Math.abs(startX - endX);
        int dy = Math.abs(startY - endY);

        // Verifica que el movimiento esté dentro del rango del rey (1 casilla alrededor)
        if (dx > 1 || dy > 1) {
            return false;
        }

        // Verifica si la casilla de destino está ocupada por una pieza del mismo color
        Piece destinationPiece = board.getPieceAt(endX, endY);
        if (destinationPiece != null && destinationPiece.getColor().equals(this.color)) {
            return false;
        }

        // Simula el movimiento para verificar si el rey quedaría en jaque
        Piece originalEndPiece = board.getPieceAt(endX, endY);  // Guarda la pieza en la posición final si la hay
        board.setPieceAt(startX, startY, null);                 // Elimina temporalmente el rey de su posición actual
        board.setPieceAt(endX, endY, this);                     // Mueve el rey temporalmente a la nueva posición

        boolean inCheck = isInCheck(endX, endY);

        // Restaura el tablero a su estado original
        board.setPieceAt(startX, startY, this);
        board.setPieceAt(endX, endY, originalEndPiece);

        // Solo permite el movimiento si no deja al rey en jaque
        return !inCheck;
    }

    // Método auxiliar para verificar si el rey está en jaque en una posición dada
    public boolean isInCheck(int kingX, int kingY) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = board.getPieceAt(x, y);
                // Verifica si la pieza es del oponente y si amenaza la posición del rey
                if (piece != null && !piece.getColor().equals(this.getColor()) && piece.isValidMove(x, y, kingX, kingY)) {
                    return true;  // El rey estaría en jaque
                }
            }
        }
        return false;  // El rey no está en jaque en esta posición
    }
}

