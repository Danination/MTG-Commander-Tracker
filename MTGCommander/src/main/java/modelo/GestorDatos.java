package modelo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

public class GestorDatos {
	
	public static List<Partida> historialPartidas = new ArrayList<>();
	
	// Esta es nuestra "pizarra blanca". Es pública y estática para que todas las ventanas la usen.
	public static DefaultListModel<Jugador> modeloJugadoresGlobal = new DefaultListModel<>();
	
}