package vista;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MenuPrincipal extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
	    // 🟢 ACTIVAR MODO OSCURO MODERNO
	    try {
	        com.formdev.flatlaf.FlatDarkLaf.setup();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    EventQueue.invokeLater(new Runnable() {
	        public void run() {
	            try {
	                MenuPrincipal frame = new MenuPrincipal();
	                frame.setVisible(true);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    });
	}

	/**
	 * Create the frame.
	 */
	public MenuPrincipal() {
		setTitle("MTG Commander");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 692, 427);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(2, 2, 0, 0));
		
		// ==========================================
		// BOTÓN 1: NUEVA PARTIDA (Corregido)
		// ==========================================
		JButton btnNuevaPartida = new JButton("Nueva Partida");
		btnNuevaPartida.setFont(new Font("Tahoma", Font.PLAIN, 24));
		btnNuevaPartida.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// CORREGIDO: Ahora abrimos la pantalla de Configuración primero
				ConfigurarPartida configurar = new ConfigurarPartida();
				configurar.setVisible(true);
				dispose(); // Cerramos el menú principal
			}
		});
		contentPane.add(btnNuevaPartida);
		
		// ==========================================
		// BOTÓN 2: JUGADORES
		// ==========================================
		JButton btnJugadores = new JButton("Jugadores");
		btnJugadores.setFont(new Font("Tahoma", Font.PLAIN, 24));
		btnJugadores.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GestionJugadores ventanaJugadores = new GestionJugadores();
				ventanaJugadores.setVisible(true);
				dispose(); 
			}
		});
		contentPane.add(btnJugadores);
		
		// ==========================================
		// BOTÓN 3: RANKING
		// ==========================================
		JButton btnRanking = new JButton("Ranking");
		btnRanking.setFont(new Font("Tahoma", Font.PLAIN, 24));
		btnRanking.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Ranking ventanaRanking = new Ranking();
				ventanaRanking.setVisible(true);
				dispose();				
			}
		});
		contentPane.add(btnRanking);
		
		// ==========================================
		// BOTÓN 4: HISTORIAL
		// ==========================================
		JButton btnHistorial = new JButton("Historial");
		btnHistorial.setFont(new Font("Tahoma", Font.PLAIN, 24));
		btnHistorial.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Historial ventanaHistorial = new Historial();
				ventanaHistorial.setVisible(true);
				dispose();
			}
		});
		contentPane.add(btnHistorial);
	}
}