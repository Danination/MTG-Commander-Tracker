package vista;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import modelo.GestorDatos;
import modelo.Partida;
import modelo.ResultadoJugador;

public class Historial extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JList<String> listaHistorial;
	private DefaultListModel<String> modeloHistorial;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Historial frame = new Historial();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Historial() {
		setTitle("Historial de Partidas");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 400);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(10, 10));

		// ==========================================
		// 1. ZONA CENTRO: Lista de Partidas
		// ==========================================
		modeloHistorial = new DefaultListModel<>();
		listaHistorial = new JList<>(modeloHistorial);
		listaHistorial.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(listaHistorial, BorderLayout.CENTER);

		// Cargar los datos desde el GestorDatos
		cargarHistorial();

		// ==========================================
		// 2. ZONA SUR: Botón Volver
		// ==========================================
		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
		contentPane.add(panelBotones, BorderLayout.SOUTH);

		JButton btnVolver = new JButton("Volver al Menú");
		btnVolver.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelBotones.add(btnVolver);

		// ==========================================
		// 3. LÓGICA DEL BOTÓN
		// ==========================================
		btnVolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				MenuPrincipal menu = new MenuPrincipal();
				menu.setVisible(true);
			}
		});
	}

	// Método auxiliar para leer las partidas y ponerlas en la lista
	private void cargarHistorial() {
		// Limpiamos la lista por si acaso
		modeloHistorial.clear();
		
		// Comprobamos si hay partidas guardadas
		if (GestorDatos.historialPartidas.isEmpty()) {
			modeloHistorial.addElement("Aún no hay partidas jugadas. ¡A jugar!");
			return;
		}

		// Recorremos la lista global de partidas
		for (Partida p : GestorDatos.historialPartidas) {
			// Buscamos quién fue el ganador (el que tiene posicionFinal == 1)
			ResultadoJugador ganador = p.obtenerGanador();
			String nombreGanador = (ganador != null) ? ganador.getJugador().getNombre() : "Desconocido";
			
			// Formateamos la fecha para que se vea bonita (dd/MM/yyyy HH:mm)
			String fechaFormateada = p.getFecha().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
			
			// Creamos el texto que se verá en la lista
			String textoLinea = "🏆 Ganador: " + nombreGanador + " | ⏱️ Tiempo: " + p.getDuracionMinutos() + " min | 📅 " + fechaFormateada;
			
			// Lo añadimos a nuestro modelo
			modeloHistorial.addElement(textoLinea);
		}
	}
}