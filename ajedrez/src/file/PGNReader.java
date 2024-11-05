package file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PGNReader {
    public List<String> loadMovesFromPGN(String filePath) {
        List<String> moves = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Ignorar líneas de metadatos (comienzan con "[")
                if (!line.startsWith("[")) {
                    // Separar movimientos por espacio y números de turno
                    String[] tokens = line.split("\\s+");
                    for (String token : tokens) {
                        if (!token.matches("\\d+\\.")) { // Ignorar los números de turno (por ejemplo, "1.", "2.", etc.)
                            moves.add(token.trim());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return moves;
    }
}
