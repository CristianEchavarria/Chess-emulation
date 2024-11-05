package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class ChessView extends JFrame {
    private JButton loadGameButton;
    private JFileChooser fileChooser;
    private final JButton[][] boardButtons;
    private final Map<String, ImageIcon> pieceIcons;
    private final int buttonSize = 60; // Tamaño de los botones

    // Componentes adicionales
    private final JLabel turnLabel;
    private final JButton nextMoveButton;
    private final JButton prevMoveButton;
    private final JButton resetButton;

    public ChessView() {
        setTitle("Chess Game");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel superior con opciones de juego
        JPanel topPanel = new JPanel();
        loadGameButton = new JButton("Cargar Juego");
        fileChooser = new JFileChooser();

        topPanel.add(loadGameButton);
        add(topPanel, BorderLayout.NORTH);

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(8, 8));
        boardButtons = new JButton[8][8];
        pieceIcons = new HashMap<>();

        // Cargar las imágenes
        loadPieceIcons();

        // Crear botones para cada casilla
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardButtons[i][j] = new JButton();
                boardButtons[i][j].setPreferredSize(new Dimension(buttonSize, buttonSize));
                boardButtons[i][j].setOpaque(true);
                boardButtons[i][j].setBorderPainted(false);

                // Colores de tablero (blanco y gris)
                if ((i + j) % 2 == 0) {
                    boardButtons[i][j].setBackground(Color.WHITE);
                } else {
                    boardButtons[i][j].setBackground(Color.GRAY);
                }
                boardPanel.add(boardButtons[i][j]);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        // Panel de control para botones y turno
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        // Indicador de turno
        turnLabel = new JLabel("Turno: Blancas");
        controlPanel.add(turnLabel);

        // Botón para avanzar un movimiento
        nextMoveButton = new JButton("Avanzar Movimiento");
        controlPanel.add(nextMoveButton);

        // Botón para retroceder un movimiento
        prevMoveButton = new JButton("Retroceder Movimiento");
        controlPanel.add(prevMoveButton);

        // Botón para reiniciar el juego
        resetButton = new JButton("Reiniciar");
        controlPanel.add(resetButton);

        add(controlPanel, BorderLayout.SOUTH); // Añadir panel de control en la parte inferior

        setVisible(true);
    }

    private void loadPieceIcons() {
        // Carga las imágenes de las piezas y las almacena en el mapa pieceIcons
        pieceIcons.put("pawn_white", loadImage("pawn_white.png"));
        pieceIcons.put("pawn_black", loadImage("pawn_black.png"));
        pieceIcons.put("rook_white", loadImage("rook_white.png"));
        pieceIcons.put("rook_black", loadImage("rook_black.png"));
        pieceIcons.put("knight_white", loadImage("knight_white.png"));
        pieceIcons.put("knight_black", loadImage("knight_black.png"));
        pieceIcons.put("bishop_white", loadImage("bishop_white.png"));
        pieceIcons.put("bishop_black", loadImage("bishop_black.png"));
        pieceIcons.put("queen_white", loadImage("queen_white.png"));
        pieceIcons.put("queen_black", loadImage("queen_black.png"));
        pieceIcons.put("king_white", loadImage("king_white.png"));
        pieceIcons.put("king_black", loadImage("king_black.png"));
    }

    private ImageIcon loadImage(String fileName) {
        java.net.URL imageUrl = getClass().getResource("/Recursos/" + fileName);
        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);

            // Escalar la imagen al tamaño de la casilla
            Image scaledImage = icon.getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } else {
            System.err.println("No se pudo cargar la imagen: " + fileName);
            return null;
        }
    }

    public void resetBoard() {
        // Limpiar todas las piezas del tablero
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                clearButton(i, j);
            }
        }

        // Colocar las piezas en su posición original
        // Blancas
        updateButton(0, 0, "rook", "white");
        updateButton(0, 1, "knight", "white");
        updateButton(0, 2, "bishop", "white");
        updateButton(0, 3, "queen", "white");
        updateButton(0, 4, "king", "white");
        updateButton(0, 5, "bishop", "white");
        updateButton(0, 6, "knight", "white");
        updateButton(0, 7, "rook", "white");
        for (int j = 0; j < 8; j++) {
            updateButton(1, j, "pawn", "white");
        }

        // Negras
        updateButton(7, 0, "rook", "black");
        updateButton(7, 1, "knight", "black");
        updateButton(7, 2, "bishop", "black");
        updateButton(7, 3, "queen", "black");
        updateButton(7, 4, "king", "black");
        updateButton(7, 5, "bishop", "black");
        updateButton(7, 6, "knight", "black");
        updateButton(7, 7, "rook", "black");
        for (int j = 0; j < 8; j++) {
            updateButton(6, j, "pawn", "black");
        }
    }

    public void highlightButton(int x, int y, Color color) {
        boardButtons[x][y].setBackground(color);
    }

    // Método para mostrar el cuadro de diálogo de promoción
    public String showPromotionDialog(String color) {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Selecciona la pieza para la promoción:",
                "Promoción de Peón",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        // Retornar la pieza elegida en minúsculas para facilitar su uso en el controlador
        return choice >= 0 ? options[choice].toLowerCase() : "queen"; // Por defecto, promover a reina si se cancela
    }

    public void resetButtonColor(int x, int y) {
        // Restaurar el color original de la casilla (blanco o gris)
        if ((x + y) % 2 == 0) {
            boardButtons[x][y].setBackground(Color.WHITE);
        } else {
            boardButtons[x][y].setBackground(Color.GRAY);
        }
    }

    public void addLoadGameButtonListener(ActionListener listener) {
        loadGameButton.addActionListener(listener);
    }

    public String getPGNFilePath() {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }
    // Listeners de botones
    public void addButtonListener(int x, int y, ActionListener listener) {
        boardButtons[x][y].addActionListener(listener);
    }

    public void updateButton(int x, int y, String pieceName, String color) {
        String key = pieceName.toLowerCase() + "_" + color.toLowerCase();
        boardButtons[x][y].setIcon(pieceIcons.get(key));
    }

    public void clearButton(int x, int y) {
        boardButtons[x][y].setIcon(null);
    }

    public void updateTurnLabel(String text) {
        turnLabel.setText(text);
    }

    public void addUndoButtonListener(ActionListener listener) {
        prevMoveButton.addActionListener(listener);
    }

    public void addRedoButtonListener(ActionListener listener) {
        nextMoveButton.addActionListener(listener);
    }

    public void addResetButtonListener(ActionListener listener) {
        resetButton.addActionListener(listener);
    }

    // Método para mostrar un mensaje emergente al usuario
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}
