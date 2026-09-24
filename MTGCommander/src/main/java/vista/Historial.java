package vista;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatDarkLaf;

import dao.ConexionBD;

public class Historial extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JList<String> listaHistorial;
	private DefaultListModel<String> modeloHistorial;
	
	// Colores rojo sangre
	private static final Color COLOR_SANGRE = new Color(139, 26, 26);
	private static final Color COLOR_SANGRE_CLARO = new Color(165, 42, 42);
	private static final Color COLOR_SANGRE_OSCURO = new Color(74, 14, 14);

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FlatDarkLaf.setup();
					Historial frame = new Historial();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Historial() {
		try {
			UIManager.setLookAndFeel(new FlatDarkLaf());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setTitle("Historial de Partidas");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(700, 600);
		setLocationRelativeTo(null);
		setResizable(false);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(40, 40, 40, 40));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 25));

		// ==========================================
		// 1. ZONA NORTE: Título en Rojo Sangre
		// ==========================================
		JPanel panelNorte = new JPanel();
		panelNorte.setLayout(new BorderLayout(0, 10));
		panelNorte.setOpaque(false);
		panelNorte.setBorder(new EmptyBorder(0, 0, 20, 0));
		
		JPanel panelTituloContainer = new JPanel();
		panelTituloContainer.setLayout(new BorderLayout());
		panelTituloContainer.setOpaque(false);
		
		JLabel lblTitulo = new JLabel("HISTORIAL DE PARTIDAS", SwingConstants.CENTER);
		lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
		lblTitulo.setForeground(COLOR_SANGRE);
		panelTituloContainer.add(lblTitulo, BorderLayout.CENTER);
		
		JPanel lineaDecorativa = new JPanel();
		lineaDecorativa.setPreferredSize(new Dimension(0, 3));
		lineaDecorativa.setBackground(new Color(150, 35, 35));
		panelTituloContainer.add(lineaDecorativa, BorderLayout.SOUTH);
		
		panelNorte.add(panelTituloContainer, BorderLayout.CENTER);
		
		JLabel lblSubtitulo = new JLabel("Registro de todas las partidas jugadas", SwingConstants.CENTER);
		lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblSubtitulo.setForeground(new Color(180, 180, 180));
		panelNorte.add(lblSubtitulo, BorderLayout.SOUTH);
		
		contentPane.add(panelNorte, BorderLayout.NORTH);

		// ==========================================
		// 2. ZONA CENTRO: Tarjeta redondeada con lista
		// ==========================================
		JPanel panelListaContainer = new JPanel(new BorderLayout()) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				GradientPaint gradiente = new GradientPaint(
					0, 0, new Color(50, 50, 55),
					0, getHeight(), new Color(40, 40, 45)
				);
				g2.setPaint(gradiente);
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
				
				g2.setColor(new Color(70, 70, 75));
				g2.setStroke(new BasicStroke(1.5f));
				g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
				
				g2.dispose();
				super.paintComponent(g);
			}
		};
		panelListaContainer.setOpaque(false);
		panelListaContainer.setBorder(new EmptyBorder(20, 20, 20, 20));
		
		modeloHistorial = new DefaultListModel<>();
		listaHistorial = new JList<>(modeloHistorial);
		listaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		listaHistorial.setBackground(new Color(45, 45, 48));
		listaHistorial.setForeground(new Color(200, 200, 200));
		listaHistorial.setSelectionBackground(COLOR_SANGRE_CLARO);
		listaHistorial.setSelectionForeground(Color.WHITE);
		listaHistorial.setFixedCellHeight(60);
		listaHistorial.setBorder(null);
		
		JScrollPane scrollPane = new JScrollPane(listaHistorial);
		scrollPane.setBorder(null);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		panelListaContainer.add(scrollPane, BorderLayout.CENTER);
		
		contentPane.add(panelListaContainer, BorderLayout.CENTER);
		
		// Cargar los datos
		cargarHistorial();

		// ==========================================
		// 3. ZONA SUR: Botones estilizados (INTERCAMBIADOS)
		// ==========================================
		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
		panelBotones.setOpaque(false);
		
		// 🟢 Botón Volver (AHORA A LA IZQUIERDA)
		JButton btnVolver = new JButton("← Volver");
		btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnVolver.setPreferredSize(new Dimension(140, 45));
		btnVolver.setBackground(new Color(50, 50, 55));
		btnVolver.setForeground(Color.WHITE);
		btnVolver.setFocusPainted(false);
		btnVolver.setBorderPainted(false);
		btnVolver.setOpaque(true);
		btnVolver.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		panelBotones.add(btnVolver);
		
		// 🟢 Botón Limpiar Historial (AHORA A LA DERECHA)
		JButton btnLimpiar = new JButton("Limpiar Historial");
		btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnLimpiar.setPreferredSize(new Dimension(200, 45));
		btnLimpiar.setBackground(COLOR_SANGRE_OSCURO);
		btnLimpiar.setForeground(new Color(200, 100, 100));
		btnLimpiar.setFocusPainted(false);
		btnLimpiar.setBorderPainted(false);
		btnLimpiar.setOpaque(true);
		btnLimpiar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		panelBotones.add(btnLimpiar);
		
		contentPane.add(panelBotones, BorderLayout.SOUTH);

		// ==========================================
		// 4. LÓGICA DE LOS BOTONES
		// ==========================================
		btnLimpiar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int opcion = JOptionPane.showConfirmDialog(Historial.this,
					"¿Estás seguro de que quieres BORRAR TODO el historial de partidas?\nEsta acción no se puede deshacer.",
					"Confirmar borrado", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				
				if (opcion == JOptionPane.YES_OPTION) {
					try (Connection conn = ConexionBD.getConexion();
					     Statement stmt = conn.createStatement()) {
					     
						stmt.execute("DELETE FROM resultados");
						stmt.execute("DELETE FROM partidas");
						
						JOptionPane.showMessageDialog(Historial.this, "Historial limpiado correctamente.");
						cargarHistorial();
						
					} catch (SQLException ex) {
						JOptionPane.showMessageDialog(Historial.this, "Error al limpiar: " + ex.getMessage());
					}
				}
			}
		});
		
		btnVolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				MenuPrincipal menu = new MenuPrincipal();
				menu.setVisible(true);
			}
		});
	}

	private void cargarHistorial() {
		modeloHistorial.clear();
		
		String sql = "SELECT p.fecha, p.duracion_minutos, j.nombre " +
		             "FROM partidas p " +
		             "JOIN resultados r ON p.id = r.partida_id " +
		             "JOIN jugadores j ON r.jugador_id = j.id " +
		             "WHERE r.posicion = 1 " +
		             "ORDER BY p.id DESC";
		             
		try (Connection conn = ConexionBD.getConexion();
		     Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery(sql)) {
		     		     
		    while (rs.next()) {
		        String fecha = rs.getString("fecha");
		        int mins = rs.getInt("duracion_minutos");
		        String ganador = rs.getString("nombre");
		        
		        // Formato más limpio sin emojis excesivos
		        String texto = "Ganador: " + ganador + "  |  Duración: " + mins + " min  |  Fecha: " + fecha;
		        modeloHistorial.addElement(texto);
		    }
		    
		    if (modeloHistorial.isEmpty()) {
		        // Añadimos varias líneas vacías para centrar visualmente
		        for (int i = 0; i < 3; i++) {
		            modeloHistorial.addElement("");
		        }
		        modeloHistorial.addElement("          📜 No hay partidas registradas aún          ");
		        modeloHistorial.addElement("          Juega algunas partidas para verlas aquí          ");
		        for (int i = 0; i < 3; i++) {
		            modeloHistorial.addElement("");
		        }
		    }
		     
		} catch (SQLException e) {
		    System.err.println("Error al cargar historial: " + e.getMessage());
		    modeloHistorial.addElement("Error al leer la base de datos.");
		}
	}
}