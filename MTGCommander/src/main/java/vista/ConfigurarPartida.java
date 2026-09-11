package vista;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import modelo.GestorDatos;
import modelo.Jugador;

public class ConfigurarPartida extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JList<Jugador> listaJugadoresDisponibles;
	private JTextField txtVidas;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConfigurarPartida frame = new ConfigurarPartida();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ConfigurarPartida() {
		setTitle("Configurar Nueva Partida");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 400);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(10, 10));

		// ==========================================
		// 1. ZONA NORTE: Instrucciones
		// ==========================================
		JLabel lblInstrucciones = new JLabel("Selecciona los jugadores que participarán (mantén pulsado Ctrl para seleccionar varios):", JLabel.CENTER);
		lblInstrucciones.setFont(new Font("Tahoma", Font.BOLD, 14));
		contentPane.add(lblInstrucciones, BorderLayout.NORTH);

		// ==========================================
		// 2. ZONA CENTRO: Lista de jugadores (Selección Múltiple)
		// ==========================================
		// Usamos la lista GLOBAL que ya tiene a Dani, Mainez, Oscar, etc.
		listaJugadoresDisponibles = new JList<>(GestorDatos.modeloJugadoresGlobal);
		listaJugadoresDisponibles.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		// ¡TRUCO CLAVE! Permitir seleccionar más de un jugador a la vez
		listaJugadoresDisponibles.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		contentPane.add(listaJugadoresDisponibles, BorderLayout.CENTER);

		// ==========================================
		// 3. ZONA SUR: Vidas iniciales y Botones de Acción
		// ==========================================
		JPanel panelInferior = new JPanel();
		panelInferior.setLayout(new BorderLayout(10, 10));
		contentPane.add(panelInferior, BorderLayout.SOUTH);

		// Panel de Vidas
		JPanel panelVidas = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelVidas.add(new JLabel("Vidas iniciales:"));
		txtVidas = new JTextField("40", 5); // Valor por defecto 40
		txtVidas.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelVidas.add(txtVidas);
		panelInferior.add(panelVidas, BorderLayout.NORTH);

		// Panel de Botones
		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		
		JButton btnVolver = new JButton("Volver");
		btnVolver.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panelBotones.add(btnVolver);
		
		JButton btnJugar = new JButton("¡A JUGAR!");
		btnJugar.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelBotones.add(btnJugar);
		
		panelInferior.add(panelBotones, BorderLayout.SOUTH);

		// ==========================================
		// 4. LÓGICA DE LOS BOTONES
		// ==========================================
		
		// Botón Volver
		btnVolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				MenuPrincipal menu = new MenuPrincipal();
				menu.setVisible(true);
			}
		});

		// Botón ¡A JUGAR!
		btnJugar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 1. Obtenemos la lista de jugadores seleccionados
				List<Jugador> seleccionados = listaJugadoresDisponibles.getSelectedValuesList();

				// 2. Validación: Necesitamos al menos 2 jugadores
				if (seleccionados.size() < 2) {
					JOptionPane.showMessageDialog(null, 
						"Debes seleccionar al menos 2 jugadores para empezar la partida.", 
						"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				// 3. Validación: Comprobar que las vidas sean un número válido
				int vidasIniciales = 40; // Valor por defecto
				try {
					vidasIniciales = Integer.parseInt(txtVidas.getText().trim());
					if (vidasIniciales <= 0) {
						throw new NumberFormatException();
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, 
						"Las vidas iniciales deben ser un número mayor a 0.", 
						"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				// 4. ¡TODO CORRECTO! Cerramos esta ventana y abrimos la mesa de juego
				dispose();
				
				// (En el siguiente paso, modificaremos NuevaPartida para que acepte estos datos)
				NuevaPartida mesaDeJuego = new NuevaPartida(seleccionados, vidasIniciales);
				mesaDeJuego.setVisible(true);
			}
		});
	}
}