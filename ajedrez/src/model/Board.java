package model;

import model.pieces.*;

public class Board {
    private Piece[][] board;

    public Board() {
        board = new Piece[8][8];
        initializeBoard();
    }

    private void initializeBoard() {
        // Colocar piezas iniciales blancas
        initializePieceRow(7, "white", new String[]{"Rook", "Knight", "Bishop", "Queen", "King", "Bishop", "Knight", "Rook"});
        initializePawnRow(6, "white");

        // Colocar piezas iniciales negras
        initializePieceRow(0, "black", new String[]{"Rook", "Knight", "Bishop", "Queen", "King", "Bishop", "Knight", "Rook"});
        initializePawnRow(1, "black");
    }

    public void resetBoard() {
        clearBoard();       // Limpia todas las piezas del tablero
        initializeBoard();  // Restaura las piezas a la posición inicial
    }

    private void clearBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = null; // Eliminar cualquier pieza en cada posición
            }
        }
    }

    private void initializePawnRow(int row, String color) {
        for (int i = 0; i < 8; i++) {
            board[row][i] = new Pawn(color, this);
        }
    }

    private void initializePieceRow(int row, String color, String[] pieceOrder) {
        for (int i = 0; i < pieceOrder.length; i++) {
            String pieceName = pieceOrder[i];
            switch (pieceName) {
                case "Rook" -> board[row][i] = new Rook(color, this);
                case "Knight" -> board[row][i] = new Knight(color, this);
                case "Bishop" -> board[row][i] = new Bishop(color, this);  // Se pasa el tablero como referencia
                case "Queen" -> board[row][i] = new Queen(color, this);
                case "King" -> board[row][i] = new King(color, this);
            }
        }
    }

    public Piece getPieceAt(int x, int y) {
        if (!isValidPosition(x, y)) {
            return null; // Devuelve null si la posición está fuera de los límites
        }
        return board[x][y];
    }

    // Método para establecer una pieza en una posición específica
    public void setPieceAt(int x, int y, Piece piece) {
        if (isValidPosition(x, y)) {
            board[x][y] = piece;
        }
    }

    public void movePiece(int startX, int startY, int endX, int endY) {
        board[endX][endY] = board[startX][startY];
        board[startX][startY] = null;
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    public boolean isOccupied(int x, int y) {
        if (!isValidPosition(x, y)) {
            return false; // Devuelve false si la posición está fuera de los límites
        }
        return board[x][y] != null;
    }

}

