Chess Game Application

Descripción
Juego de ajedrez en Java con interfaz gráfica, donde puedes jugar un nuevo juego o cargar una partida desde un archivo PGN. Incluye funcionalidades para reiniciar el juego, deshacer y rehacer movimientos.

Requisitos
Java JDK 8 o superior

Ejecución
Compilar y Ejecutar: Abre la clase principal ChessController y ejecútala desde el IDE o línea de comandos:
javac -d . src/*.java
java ChessController
Interfaz Inicial: La aplicación te permite iniciar un Nuevo Juego o Cargar una Partida PGN.


Funcionalidades
Nuevo Juego: Comienza una partida de ajedrez con las piezas en sus posiciones iniciales y turnos alternando entre Blancas y Negras.

Cargar Partida desde PGN: Carga un archivo .pgn con movimientos de una partida previa.

Deshacer/Rehacer Movimiento: Deshace o rehace el último movimiento realizado en la partida.

Reiniciar Partida: Restaura el tablero al estado inicial.

Notas
Para cargar un archivo PGN, asegúrate de que esté en el formato correcto, conteniendo únicamente movimientos válidos de ajedrez.
¡Disfruta de tu juego de ajedrez!