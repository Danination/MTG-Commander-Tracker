package vista;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import dao.ConexionBD;

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

	// Método para leer las partidas desde la Base de Datos
	private void cargarHistorial() {
		modeloHistorial.clear();
		
		// Consulta SQL: Traemos la fecha, duración y el nombre del ganador (posición 1)
		String sql = "SELECT p.fecha, p.duracion_minutos, j.nombre " +
		             "FROM partidas p " +
		             "JOIN resultados r ON p.id = r.partida_id " +
		             "JOIN jugadores j ON r.jugador_id = j.id " +
		             "WHERE r.posicion = 1 " +
		             "ORDER BY p.id DESC"; // Las más recientes primero
		             
		try (Connection conn = ConexionBD.getConexion();
		     Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery(sql)) {
		     
		    while (rs.next()) {
		        String fecha = rs.getString("fecha");
		        int mins = rs.getInt("duracion_minutos");
		        String ganador = rs.getString("nombre");
		        
		        String texto = "🏆 Ganador: " + ganador + " | ⏱️ " + mins + " min | 📅 " + fecha;
		        modeloHistorial.addElement(texto);
		    }
		    
		    if (modeloHistorial.isEmpty()) {
		        modeloHistorial.addElement("Aún no hay partidas guardadas en la base de datos.");
		    }
		     
		} catch (SQLException e) {
		    System.err.println("Error al cargar historial: " + e.getMessage());
		    modeloHistorial.addElement("Error al leer la base de datos.");
		}
	}
}