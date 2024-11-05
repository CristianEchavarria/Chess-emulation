package model;

import model.pieces.Piece;

// Clase auxiliar Move para almacenar los detalles de cada movimiento
public class Move {
    private int startX, startY, endX, endY;
    private Piece movedPiece;
    private Piece capturedPiece;

    public Move(int startX, int startY, int endX, int endY, Piece movedPiece, Piece capturedPiece) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
    }

    // Getters para acceder a los datos de movimiento
    public int getStartX() { return startX; }
    public int getStartY() { return startY; }
    public int getEndX() { return endX; }
    public int getEndY() { return endY; }
    public Piece getMovedPiece() { return movedPiece; }
    public Piece getCapturedPiece() { return capturedPiece; }
}
