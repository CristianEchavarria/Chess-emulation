package controller;


import file.PGNReader;
import model.Board;
import model.Move;
import model.pieces.*;
import view.ChessView;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.swing.*;

public class ChessController {
    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean whiteRookKingsideMoved = false;
    private boolean whiteRookQueensideMoved = false;
    private boolean blackRookKingsideMoved = false;
    private boolean blackRookQueensideMoved = false;
    private Board board;
    private ChessView view;
    private PGNReader pgnReader;
    private List<String> loadedMoves;
    private int moveIndex;
    private int startX = -1, startY = -1;
    private boolean isWhiteTurn;
    private List<Move> moveHistory = new ArrayList<>();
    private int currentMoveIndex = -1;  // Índice para manejar el avance y retroceso de movimientos
    private Stack<Move> redoStack = new Stack<>();    // Pila para movimientos "rehacer"


    public ChessController() {
        board = new Board();
        view = new ChessView();
        this.board = board;
        this.view = view;
        this.isWhiteTurn = true; // Comienza con el turno de las blancas
        pgnReader = new PGNReader();
        loadedMoves = new ArrayList<>();
        moveIndex = 0;
        initializeBoard();
        updateTurnStatus();
        setupControlButtons();

        view.addLoadGameButtonListener(e -> {
            String filePath = view.getPGNFilePath();
            if (filePath != null) {
                loadGameFromPGN(filePath);
            }
        });
    }

    private void initializeBoard() {
        view.addResetButtonListener(e -> resetGame());
        updateTurnLabel();
        initializePieceRow(7, "white", new String[]{"Rook", "Knight", "Bishop", "Queen", "King", "Bishop", "Knight", "Rook"});
        initializePawnRow(6, "white");

        initializePieceRow(0, "black", new String[]{"Rook", "Knight", "Bishop", "Queen", "King", "Bishop", "Knight", "Rook"});
        initializePawnRow(1, "black");

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                final int x = i;
                final int y = j;
                view.addButtonListener(i, j, e -> handleClick(x, y));

                if (board.getPieceAt(i, j) != null) {
                    String pieceName = board.getPieceAt(i, j).getClass().getSimpleName();
                    String color = board.getPieceAt(i, j).getColor();
                    view.updateButton(i, j, pieceName, color);
                }
            }
        }
    }

    // Método para cargar y reproducir movimientos desde un archivo PGN
    public void loadGameFromPGN(String filePath) {
        loadedMoves = pgnReader.loadMovesFromPGN(filePath);
        moveIndex = 0;
        replayNextMove();
    }

    // Método para reproducir el siguiente movimiento en el archivo PGN cargado
    private void replayNextMove() {
        if (moveIndex < loadedMoves.size()) {
            String move = loadedMoves.get(moveIndex);
            performMove(move);  // Método para traducir y realizar el movimiento en el tablero
            moveIndex++;
        }
    }
    private void performMove(String move) {
        // Implementación para interpretar el movimiento en notación algebraica y actualizar el tablero
    }
    private void updateTurnStatus() {
        String turnText = isWhiteTurn ? "Turno de Blancas" : "Turno de Negras";
        view.updateTurnLabel(turnText);
    }

    // Método para inicializar la fila de peones
    private void initializePawnRow(int row, String color) {
        for (int i = 0; i < 8; i++) {
            board.setPieceAt(row, i, color.equals("white") ? new model.pieces.Pawn("white", board) : new model.pieces.Pawn("black", board));
            view.updateButton(row, i, "pawn", color);
        }
    }

    // Método para inicializar una fila de piezas (torre, caballo, etc.)
    private void initializePieceRow(int row, String color, String[] pieceOrder) {
        for (int i = 0; i < pieceOrder.length; i++) {
            String pieceName = pieceOrder[i];
            switch (pieceName) {
                case "Rook" -> board.setPieceAt(row, i, new model.pieces.Rook(color, board));
                case "Knight" -> board.setPieceAt(row, i, new model.pieces.Knight(color, board));
                case "Bishop" -> board.setPieceAt(row, i, new model.pieces.Bishop(color, board));
                case "Queen" -> board.setPieceAt(row, i, new model.pieces.Queen(color, board));
                case "King" -> board.setPieceAt(row, i, new model.pieces.King(color, board));
            }
            view.updateButton(row, i, pieceName.toLowerCase(), color);
        }
    }

    // Método para verificar si el rey del jugador actual está en jaque
    private boolean isKingInCheck() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getPieceAt(i, j);
                // Encuentra el rey del jugador actual
                if (piece instanceof King && piece.getColor().equals(isWhiteTurn ? "white" : "black")) {
                    return ((King) piece).isInCheck(i, j);  // Utiliza isInCheck del rey
                }
            }
        }
        return false;
    }
    // Método para verificar si una pieza puede sacar al rey del jaque
    private boolean canPieceSaveKing(Piece piece, int startX, int startY) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (piece.isValidMove(startX, startY, x, y)) {
                    // Guarda el estado actual del tablero para revertir después
                    Piece originalPieceAtTarget = board.getPieceAt(x, y);

                    // Realiza el movimiento de prueba
                    board.movePiece(startX, startY, x, y);
                    boolean kingInCheck = isKingInCheck();

                    // Revierte el movimiento de prueba
                    board.setPieceAt(startX, startY, piece);
                    board.setPieceAt(x, y, originalPieceAtTarget);

                    // Si el movimiento saca al rey del jaque, entonces la pieza puede moverse
                    if (!kingInCheck) {
                        return true;
                    }
                }
            }
        }
        // Si ningún movimiento saca al rey del jaque, la pieza no puede moverse
        return false;
    }

    private void handleClick(int x, int y) {
        if (startX == -1 && startY == -1) {
            Piece selectedPiece = board.getPieceAt(x, y);

            if (selectedPiece != null &&
                    ((isWhiteTurn && selectedPiece.getColor().equals("white")) ||
                            (!isWhiteTurn && selectedPiece.getColor().equals("black")))) {

                if (isKingInCheck()) {
                    // Resalta las piezas que pueden salvar al rey del jaque
                    highlightSaviorPieces();

                    if (!(selectedPiece instanceof King) && !canPieceSaveKing(selectedPiece, x, y)) {
                        view.showMessage("El rey está en jaque. Solo puedes mover piezas que saquen al rey del jaque.");
                        return;
                    }
                } else {
                    // Si el rey no está en jaque, limpia cualquier resaltado
                    clearHighlights();
                }

                startX = x;
                startY = y;
            }
        } else {
            Piece pieceToMove = board.getPieceAt(startX, startY);

            // Verificar si el movimiento es un enroque
            if (pieceToMove instanceof King && Math.abs(startY - y) == 2) {
                if (y == 6 && canCastleKingside(pieceToMove.getColor())) {
                    performKingsideCastle(startX, startY, pieceToMove.getColor());
                } else if (y == 2 && canCastleQueenside(pieceToMove.getColor())) {
                    performQueensideCastle(startX, startY, pieceToMove.getColor());
                } else {
                    view.showMessage("El enroque no es posible.");
                }
                // Reiniciar las coordenadas después del enroque
                startX = -1;
                startY = -1;
                return;
            }

            if (pieceToMove.isValidMove(startX, startY, x, y)) {
                Piece movedPiece = pieceToMove;
                Piece capturedPiece = board.getPieceAt(x, y);

                board.movePiece(startX, startY, x, y);
                view.clearButton(startX, startY);

                String pieceName = movedPiece.getClass().getSimpleName();
                String color = movedPiece.getColor();
                view.updateButton(x, y, pieceName, color);

                Move move = new Move(startX, startY, x, y, movedPiece, capturedPiece);
                addMoveToHistory(move);

                // Verificar si la pieza movida es un peón y si ha llegado a la fila de promoción
                if (pieceToMove instanceof Pawn && ((Pawn) pieceToMove).isPromotionSquare(x)) {
                    // Llamar al método para promocionar el peón
                    promotePawn(x, y, pieceToMove.getColor());
                }

                // Paso 5: Desactivar enroque para el rey o la torre si se mueven
                if (movedPiece instanceof King) {
                    ((King) movedPiece).setHasMoved(true);  // Marcar que el rey ya se movió
                } else if (movedPiece instanceof Rook) {
                    ((Rook) movedPiece).setHasMoved(true);  // Marcar que la torre ya se movió
                }

                isWhiteTurn = !isWhiteTurn;
                updateTurnStatus();

                // Limpia los resaltados después de cada movimiento válido
                clearHighlights();

                if (isCheckmate()) {
                    view.showMessage("Jaque mate. " + (isWhiteTurn ? "Negras" : "Blancas") + " ganan.");
                    resetGame();
                }
            }

            startX = -1;
            startY = -1;
        }
    }


    private void promotePawn(int x, int y, String color) {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        String choice = (String) JOptionPane.showInputDialog(
                view, // Aquí simplemente pasamos `view` como el frame
                "Seleccione una pieza para la promoción:",
                "Promoción de Peón",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                "Queen"
        );

        Piece newPiece;
        switch (choice) {
            case "Rook" -> newPiece = new model.pieces.Rook(color, board);
            case "Bishop" -> newPiece = new model.pieces.Bishop(color, board);
            case "Knight" -> newPiece = new model.pieces.Knight(color, board);
            default -> newPiece = new model.pieces.Queen(color, board); // Por defecto es una Reina
        }

        // Actualiza el tablero y la vista
        board.setPieceAt(x, y, newPiece);
        view.updateButton(x, y, choice.toLowerCase(), color);
    }


    private void highlightSaviorPieces() {
        // Limpia cualquier resaltado previo
        clearHighlights();

        // Recorre el tablero buscando piezas que puedan salvar al rey
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = board.getPieceAt(x, y);
                if (piece != null && piece.getColor().equals(isWhiteTurn ? "white" : "black")) {
                    // Si la pieza puede sacar al rey del jaque, resáltala
                    if (canPieceSaveKing(piece, x, y)) {
                        view.highlightButton(x, y, new Color(144, 238, 144)); // Cambia a verde claro
                    }
                }
            }
        }
    }

    // Método auxiliar para limpiar los resaltados después de cada turno
    private void clearHighlights() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                view.resetButtonColor(x, y); // Método que restablece el color de fondo a su color predeterminado
            }
        }
    }

    private boolean isInCheck(int x, int y, String color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece opponentPiece = board.getPieceAt(i, j);
                // Si la pieza es del oponente y puede atacar la posición (x, y)
                if (opponentPiece != null && !opponentPiece.getColor().equals(color)) {
                    if (opponentPiece.isValidMove(i, j, x, y)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isInCheckPath(int startX, int startY, int endX, int endY, String color) {
        int stepY = startY < endY ? 1 : -1;
        for (int y = startY; y != endY + stepY; y += stepY) {
            if (isInCheck(startX, y, color)) {
                return true;
            }
        }
        return false;
    }


    private void performKingsideCastle(int startX, int startY, String color) {
        int endX = startX;
        int endY = startY + 2;
        board.movePiece(startX, startY, endX, endY);  // Mueve el rey
        board.movePiece(startX, startY + 3, startX, startY + 1);  // Mueve la torre

        view.updateButton(endX, endY, "king", color);
        view.updateButton(startX, startY + 1, "rook", color);
        view.clearButton(startX, startY);
        view.clearButton(startX, startY + 3);

        // Actualizar variables de enroque
        if (color.equals("white")) {
            whiteKingMoved = true;
            whiteRookKingsideMoved = true;
        } else {
            blackKingMoved = true;
            blackRookKingsideMoved = true;
        }

        isWhiteTurn = !isWhiteTurn;
        updateTurnStatus();
    }

    private void performQueensideCastle(int startX, int startY, String color) {
        int endX = startX;
        int endY = startY - 2;
        board.movePiece(startX, startY, endX, endY);  // Mueve el rey
        board.movePiece(startX, startY - 4, startX, startY - 1);  // Mueve la torre

        view.updateButton(endX, endY, "king", color);
        view.updateButton(startX, startY - 1, "rook", color);
        view.clearButton(startX, startY);
        view.clearButton(startX, startY - 4);

        // Actualizar variables de enroque
        if (color.equals("white")) {
            whiteKingMoved = true;
            whiteRookQueensideMoved = true;
        } else {
            blackKingMoved = true;
            blackRookQueensideMoved = true;
        }

        isWhiteTurn = !isWhiteTurn;
        updateTurnStatus();
    }

    private boolean canCastleKingside(String color) {
        if (color.equals("white")) {
            return !whiteKingMoved && !whiteRookKingsideMoved &&
                    !board.isOccupied(7, 5) && !board.isOccupied(7, 6) &&
                    !isInCheckPath(7, 4, 7, 6, color);
        } else {
            return !blackKingMoved && !blackRookKingsideMoved &&
                    !board.isOccupied(0, 5) && !board.isOccupied(0, 6) &&
                    !isInCheckPath(0, 4, 0, 6, color);
        }
    }

    private boolean canCastleQueenside(String color) {
        if (color.equals("white")) {
            return !whiteKingMoved && !whiteRookQueensideMoved &&
                    !board.isOccupied(7, 1) && !board.isOccupied(7, 2) && !board.isOccupied(7, 3) &&
                    !isInCheckPath(7, 4, 7, 2, color);
        } else {
            return !blackKingMoved && !blackRookQueensideMoved &&
                    !board.isOccupied(0, 1) && !board.isOccupied(0, 2) && !board.isOccupied(0, 3) &&
                    !isInCheckPath(0, 4, 0, 2, color);
        }
    }


    private void addMoveToHistory(Move move) {
        if (currentMoveIndex < moveHistory.size() - 1) {
            moveHistory = moveHistory.subList(0, currentMoveIndex + 1);
        }
        moveHistory.add(move);
        currentMoveIndex++;
    }
    public interface BoardDependentPiece {
        void setBoard(Board board);
    }

    public void undoMove() {
        // Verifica que currentMoveIndex esté dentro de los límites y que haya movimientos en el historial
        if (currentMoveIndex >= 0 && currentMoveIndex < moveHistory.size()) {
            Move lastMove = moveHistory.get(currentMoveIndex);

            // Reasigna el tablero a la pieza movida y la capturada si es necesario
            if (lastMove.getMovedPiece() != null && lastMove.getMovedPiece() instanceof BoardDependentPiece) {
                ((BoardDependentPiece) lastMove.getMovedPiece()).setBoard(board);
            }
            if (lastMove.getCapturedPiece() != null && lastMove.getCapturedPiece() instanceof BoardDependentPiece) {
                ((BoardDependentPiece) lastMove.getCapturedPiece()).setBoard(board);
            }

            // Restaura la posición original de la pieza movida
            board.setPieceAt(lastMove.getStartX(), lastMove.getStartY(), lastMove.getMovedPiece());

            // Restaura la pieza capturada en su posición anterior (o deja vacía si no había captura)
            board.setPieceAt(lastMove.getEndX(), lastMove.getEndY(), lastMove.getCapturedPiece());

            // Actualiza la vista
            view.updateButton(lastMove.getStartX(), lastMove.getStartY(), lastMove.getMovedPiece().getClass().getSimpleName(), lastMove.getMovedPiece().getColor());

            if (lastMove.getCapturedPiece() != null) {
                view.updateButton(lastMove.getEndX(), lastMove.getEndY(), lastMove.getCapturedPiece().getClass().getSimpleName(), lastMove.getCapturedPiece().getColor());
            } else {
                view.clearButton(lastMove.getEndX(), lastMove.getEndY());
            }

            // Decrementa el índice para retroceder en el historial de movimientos
            currentMoveIndex--;
            isWhiteTurn = !isWhiteTurn;
            updateTurnStatus();
        } else {

            view.showMessage("No hay movimientos para deshacer.");
        }
    }


    public void redoMove() {
        if (currentMoveIndex < moveHistory.size() - 1) {
            currentMoveIndex++;
            Move move = moveHistory.get(currentMoveIndex);

            board.movePiece(move.getStartX(), move.getStartY(), move.getEndX(), move.getEndY());
            view.clearButton(move.getStartX(), move.getStartY());
            view.updateButton(move.getEndX(), move.getEndY(), move.getMovedPiece().getClass().getSimpleName(), move.getMovedPiece().getColor());

            isWhiteTurn = !isWhiteTurn;
            updateTurnStatus();
        }
    }

    public void resetGame() {
        board.resetBoard();  // Restablece el estado interno del tablero
        view.updateTurnLabel("Turno de Blancas");  // Restablece el turno inicial a Blancas
        isWhiteTurn = true;  // Reinicia el turno a Blancas

        // Actualizar todos los botones de la vista con las imágenes de las piezas correctas
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getPieceAt(i, j);
                if (piece != null) {
                    String pieceName = piece.getClass().getSimpleName().toLowerCase();
                    String color = piece.getColor();
                    view.updateButton(i, j, pieceName, color);
                } else {
                    view.clearButton(i, j);  // Limpia el botón si no hay pieza
                }
            }
        }

        // Limpia el historial de movimientos para que los botones de deshacer/rehacer estén sincronizados
        moveHistory.clear();
        redoStack.clear();
    }
    private void updateTurnLabel() {
        view.updateTurnLabel("Turno: " + (isWhiteTurn ? "Blancas" : "Negras"));
    }

    private void setupControlButtons() {
        view.addUndoButtonListener(e -> undoMove());
        view.addRedoButtonListener(e -> redoMove());
        view.addResetButtonListener(e -> resetGame());
    }

    private boolean isCheckmate() {
        if (!isKingInCheck()) {
            return false; // No hay jaque, así que no es jaque mate
        }

        // Recorre todas las piezas del jugador actual
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getPieceAt(i, j);
                if (piece != null && piece.getColor().equals(isWhiteTurn ? "white" : "black")) {
                    // Intenta mover la pieza a cada posición posible en el tablero
                    for (int x = 0; x < 8; x++) {
                        for (int y = 0; y < 8; y++) {
                            if (piece.isValidMove(i, j, x, y)) {
                                // Guarda el estado actual del tablero
                                Piece originalPieceAtTarget = board.getPieceAt(x, y);

                                // Realiza el movimiento de prueba
                                board.movePiece(i, j, x, y);
                                boolean kingInCheck = isKingInCheck();

                                // Revierte el movimiento de prueba
                                board.setPieceAt(i, j, piece);
                                board.setPieceAt(x, y, originalPieceAtTarget);

                                // Si el rey no está en jaque después del movimiento, no es jaque mate
                                if (!kingInCheck) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        // Si ningún movimiento válido libera al rey, es jaque mate
        return true;
    }

    public void startGame() {
        SwingUtilities.invokeLater(() -> view.setVisible(true));
    }
}
