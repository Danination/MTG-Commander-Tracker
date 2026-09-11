package modelo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

public class GestorDatos {
	
	public static List<Partida> historialPartidas = new ArrayList<>();
	
	// Esta es nuestra "pizarra blanca". Es pública y estática para que todas las ventanas la usen.
	public static DefaultListModel<Jugador> modeloJugadoresGlobal = new DefaultListModel<>();
	
	// Método para cargar datos de prueba al iniciar la app (solo la primera vez)
	public static void cargarDatosIniciales() {
		// Solo añadimos si la lista está vacía para no duplicar al reiniciar
		if (modeloJugadoresGlobal.isEmpty()) {
			modeloJugadoresGlobal.addElement(new Jugador(1, "Dani", "Rojo"));
			modeloJugadoresGlobal.addElement(new Jugador(2, "Mainez", "Azul"));
		}
	}
}