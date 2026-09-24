package vista;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatDarkLaf;
import com.google.gson.JsonObject;

import dao.GestorBD;
import modelo.Jugador;
import servicios.ScryfallAPI;

public class ConfigurarPartida extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JList<Jugador> listaJugadoresDisponibles;
	private JTextField txtVidas;
	
	// Colores rojo sangre
	private static final Color COLOR_SANGRE = new Color(139, 26, 26);
	private static final Color COLOR_SANGRE_CLARO = new Color(165, 42, 42);
	private static final Color COLOR_SANGRE_OSCURO = new Color(74, 14, 14);

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FlatDarkLaf.setup();
					ConfigurarPartida frame = new ConfigurarPartida();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ConfigurarPartida() {
		try {
			UIManager.setLookAndFeel(new FlatDarkLaf());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setTitle("Configurar Nueva Partida");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(750, 700);
		setLocationRelativeTo(null);
		setResizable(false);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(40, 40, 40, 40));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 30));
		
		// ==========================================
		// 1. ZONA NORTE: Título con estilo premium (CORREGIDO)
		// ==========================================
		JPanel panelNorte = new JPanel();
		panelNorte.setLayout(new BorderLayout(0, 10));
		panelNorte.setOpaque(false);
		panelNorte.setBorder(new EmptyBorder(0, 0, 20, 0));
		
		// Panel superior con título + línea decorativa
		JPanel panelTituloContainer = new JPanel();
		panelTituloContainer.setLayout(new BorderLayout());
		panelTituloContainer.setOpaque(false);
		
		JLabel lblTitulo = new JLabel("CONFIGURAR PARTIDA", SwingConstants.CENTER);
		lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
		lblTitulo.setForeground(new Color(220, 60, 60));
		panelTituloContainer.add(lblTitulo, BorderLayout.CENTER);
		
		// Línea decorativa debajo del título
		JPanel lineaDecorativa = new JPanel();
		lineaDecorativa.setPreferredSize(new Dimension(0, 3));
		lineaDecorativa.setBackground(new Color(150, 35, 35));
		panelTituloContainer.add(lineaDecorativa, BorderLayout.SOUTH);
		
		panelNorte.add(panelTituloContainer, BorderLayout.CENTER);
		
		JLabel lblSubtitulo = new JLabel("Selecciona los jugadores y sus comandantes", SwingConstants.CENTER);
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
				
				// Fondo con degradado sutil
				GradientPaint gradiente = new GradientPaint(
					0, 0, new Color(50, 50, 55),
					0, getHeight(), new Color(40, 40, 45)
				);
				g2.setPaint(gradiente);
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
				
				// Borde sutil
				g2.setColor(new Color(70, 70, 75));
				g2.setStroke(new BasicStroke(1.5f));
				g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
				
				g2.dispose();
				super.paintComponent(g);
			}
		};
		panelListaContainer.setOpaque(false);
		panelListaContainer.setBorder(new EmptyBorder(20, 20, 20, 20));
		
		DefaultListModel<Jugador> modeloLista = new DefaultListModel<>();
		List<Jugador> jugadoresBD = GestorBD.obtenerTodosLosJugadores();
		
		for (Jugador j : jugadoresBD) {
			modeloLista.addElement(j);
		}
		
		listaJugadoresDisponibles = new JList<>(modeloLista);
		listaJugadoresDisponibles.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		listaJugadoresDisponibles.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		listaJugadoresDisponibles.setFixedCellHeight(70);
		listaJugadoresDisponibles.setBackground(new Color(45, 45, 48));
		listaJugadoresDisponibles.setForeground(Color.WHITE);
		listaJugadoresDisponibles.setSelectionBackground(COLOR_SANGRE_CLARO);
		listaJugadoresDisponibles.setSelectionForeground(Color.WHITE);
		listaJugadoresDisponibles.setCellRenderer(new JugadorCellRenderer());
		
		JScrollPane scrollPane = new JScrollPane(listaJugadoresDisponibles);
		scrollPane.setBorder(null);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		panelListaContainer.add(scrollPane, BorderLayout.CENTER);
		
		contentPane.add(panelListaContainer, BorderLayout.CENTER);

		// ==========================================
		// 3. ZONA SUR: Tarjeta de vidas + Botones premium
		// ==========================================
		JPanel panelInferior = new JPanel();
		panelInferior.setLayout(new BorderLayout(0, 25));
		panelInferior.setOpaque(false);
		
		// Tarjeta de Vidas (redondeada)
		JPanel panelVidasContainer = new JPanel() {
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				GradientPaint gradiente = new GradientPaint(
					0, 0, new Color(45, 45, 50),
					0, getHeight(), new Color(35, 35, 40)
				);
				g2.setPaint(gradiente);
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
				
				g2.setColor(new Color(70, 70, 75));
				g2.setStroke(new BasicStroke(1f));
				g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 15, 15);
				
				g2.dispose();
				super.paintComponent(g);
			}
		};
		panelVidasContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
		panelVidasContainer.setOpaque(false);
		panelVidasContainer.setPreferredSize(new Dimension(0, 80));
		
		JLabel lblVidasTexto = new JLabel("Vidas iniciales:");
		lblVidasTexto.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblVidasTexto.setForeground(new Color(200, 200, 200));
		panelVidasContainer.add(lblVidasTexto);
		
		txtVidas = new JTextField("40", 6);
		txtVidas.setFont(new Font("Segoe UI", Font.BOLD, 24));
		txtVidas.setHorizontalAlignment(SwingConstants.CENTER);
		txtVidas.setBackground(new Color(30, 30, 35));
		txtVidas.setForeground(new Color(100, 255, 100));
		txtVidas.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(60, 60, 65), 2),
			BorderFactory.createEmptyBorder(8, 15, 8, 15)
		));
		panelVidasContainer.add(txtVidas);
		
		panelInferior.add(panelVidasContainer, BorderLayout.NORTH);

		// Panel de Botones
		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
		panelBotones.setOpaque(false);
		
		// Botón Volver (estilo discreto)
		JButton btnVolver = new JButton("← Volver");
		btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 15));
		btnVolver.setPreferredSize(new Dimension(140, 50));
		btnVolver.setBackground(new Color(50, 50, 55));
		btnVolver.setForeground(new Color(180, 180, 180));
		btnVolver.setFocusPainted(false);
		btnVolver.setBorderPainted(false);
		btnVolver.setOpaque(true);
		btnVolver.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		panelBotones.add(btnVolver);
		
		// Botón ¡A JUGAR! (épico con degradado)
		JButton btnJugar = new JButton("¡A JUGAR!") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				GradientPaint gradiente = new GradientPaint(
					0, 0, getBackground().brighter(),
					0, getHeight(), getBackground()
				);
				g2.setPaint(gradiente);
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
				
				// Borde brillante superior
				g2.setColor(new Color(255, 255, 255, 60));
				g2.setStroke(new BasicStroke(1.5f));
				g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 25, 25);
				
				g2.dispose();
				super.paintComponent(g);
			}
		};
		btnJugar.setFont(new Font("Segoe UI", Font.BOLD, 20));
		btnJugar.setPreferredSize(new Dimension(220, 55));
		btnJugar.setBackground(COLOR_SANGRE);
		btnJugar.setForeground(Color.WHITE);
		btnJugar.setFocusPainted(false);
		btnJugar.setBorderPainted(false);
		btnJugar.setOpaque(false);
		btnJugar.setContentAreaFilled(false);
		btnJugar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		
		// Efecto hover
		btnJugar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btnJugar.setBackground(COLOR_SANGRE_CLARO);
				btnJugar.repaint();
			}
			@Override
			public void mouseExited(MouseEvent e) {
				btnJugar.setBackground(COLOR_SANGRE);
				btnJugar.repaint();
			}
		});
		
		panelBotones.add(btnJugar);
		panelInferior.add(panelBotones, BorderLayout.SOUTH);
		contentPane.add(panelInferior, BorderLayout.SOUTH);

		// ==========================================
		// 4. LÓGICA DE LOS BOTONES
		// ==========================================
		btnVolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				MenuPrincipal menu = new MenuPrincipal();
				menu.setVisible(true);
			}
		});

		btnJugar.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        List<Jugador> seleccionados = listaJugadoresDisponibles.getSelectedValuesList();

		        if (seleccionados.size() < 2) {
		            JOptionPane.showMessageDialog(ConfigurarPartida.this, 
		                "Debes seleccionar al menos 2 jugadores para empezar la partida.", 
		                "Error", JOptionPane.ERROR_MESSAGE);
		            return;
		        }

		        int vidasIniciales = 40; 
		        try {
		            vidasIniciales = Integer.parseInt(txtVidas.getText().trim());
		            if (vidasIniciales <= 0) {
		                throw new NumberFormatException();
		            }
		        } catch (NumberFormatException ex) {
		            JOptionPane.showMessageDialog(ConfigurarPartida.this, 
		                "Las vidas iniciales deben ser un número mayor a 0.", 
		                "Error", JOptionPane.ERROR_MESSAGE);
		            return;
		        }

		        JOptionPane.showMessageDialog(ConfigurarPartida.this,
		            "Configurando " + seleccionados.size() + " jugadores...\nBuscando comandantes en Scryfall.",
		            "Preparando partida", JOptionPane.INFORMATION_MESSAGE);

		        for (Jugador j : seleccionados) {
		            String nombreComandante = JOptionPane.showInputDialog(
		                ConfigurarPartida.this,
		                "¿Cuál es el comandante de " + j.getNombre() + "?\n(Nombre en inglés, ej: 'Atraxa, Praetors' Voice')",
		                "Configurar Comandante - " + j.getNombre(),
		                JOptionPane.QUESTION_MESSAGE
		            );

		            if (nombreComandante == null || nombreComandante.trim().isEmpty()) {
		                nombreComandante = "Basic Land";
		            }

		            System.out.println("Buscando comandante: " + nombreComandante);
		            JsonObject carta = ScryfallAPI.buscarComandante(nombreComandante);

		            if (carta != null) {
		                j.setComandanteNombre(carta.get("name").getAsString());
		                j.setComandanteImagenUrl(ScryfallAPI.obtenerUrlImagen(carta));
		                System.out.println("✅ Comandante encontrado para " + j.getNombre() + ": " + j.getComandanteNombre());
		            } else {
		                j.setComandanteNombre(nombreComandante);
		                j.setComandanteImagenUrl(null);
		                System.out.println("⚠️ No se encontró el comandante.");
		            }
		        }

		        dispose();
		        NuevaPartida mesaDeJuego = new NuevaPartida(seleccionados, vidasIniciales);
		        mesaDeJuego.setVisible(true);
		    }
		});
	}
	
	// ==========================================
	// RENDERER PERSONALIZADO
	// ==========================================
	private class JugadorCellRenderer extends JPanel implements javax.swing.ListCellRenderer<Jugador> {
		
		private static final long serialVersionUID = 1L;
		private JLabel lblAvatar;
		private JLabel lblNombre;
		
		public JugadorCellRenderer() {
			setLayout(new BorderLayout(15, 0));
			setOpaque(true);
			setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
			
			lblAvatar = new JLabel();
			lblAvatar.setPreferredSize(new Dimension(55, 55));
			lblAvatar.setOpaque(false);
			lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
			add(lblAvatar, BorderLayout.WEST);
			
			lblNombre = new JLabel();
			lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 20));
			lblNombre.setForeground(Color.WHITE);
			add(lblNombre, BorderLayout.CENTER);
		}
		
		@Override
		public Component getListCellRendererComponent(JList<? extends Jugador> list, Jugador value,
				int index, boolean isSelected, boolean cellHasFocus) {
			
			if (value != null) {
				lblNombre.setText(value.getNombre());
				
				String avatarPath = value.getAvatarPath();
				if (avatarPath != null && !avatarPath.isEmpty()) {
					File archivoAvatar = new File(avatarPath);
					if (archivoAvatar.exists()) {
						try {
							Image img = javax.imageio.ImageIO.read(archivoAvatar);
							ImageIcon icon = new ImageIcon(img.getScaledInstance(55, 55, Image.SCALE_SMOOTH));
							lblAvatar.setIcon(icon);
							lblAvatar.setText("");
						} catch (Exception e) {
							lblAvatar.setIcon(null);
							lblAvatar.setText("?");
							lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 24));
							lblAvatar.setForeground(Color.GRAY);
						}
					} else {
						lblAvatar.setIcon(null);
						lblAvatar.setText("?");
						lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 24));
						lblAvatar.setForeground(Color.GRAY);
					}
				} else {
					lblAvatar.setIcon(null);
					lblAvatar.setText("?");
					lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 24));
					lblAvatar.setForeground(Color.GRAY);
				}
			}
			
			if (isSelected) {
				setBackground(COLOR_SANGRE_CLARO);
				lblNombre.setForeground(Color.WHITE);
			} else {
				setBackground(new Color(45, 45, 48));
				lblNombre.setForeground(Color.WHITE);
			}
			
			return this;
		}
	}
}