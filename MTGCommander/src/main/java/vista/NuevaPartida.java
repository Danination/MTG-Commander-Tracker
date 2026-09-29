package vista;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import modelo.Jugador;

public class NuevaPartida extends JFrame {
	
	private List<PanelJugador> panelesDeJuego = new ArrayList<>();
	private int vidasInicialesGlobales; // Para saber a cuántas vidas resetear
	
	// 🟢 NUEVO: Lista para rastrear el orden en que los jugadores son eliminados
	private List<Jugador> ordenDeEliminacion = new ArrayList<>();
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	// Atributos del cronómetro
	private JLabel lblCronometro;
	private Timer timer;
	private int segundosTranscurridos = 0;
	private JLabel lblTurno;
	private int turnoActual = 1;
	
	private List<Jugador> jugadoresEnPartida;

	// Paleta de colores para los botones inferiores (igual que en el resto de la app)
	private static final Color COLOR_ROJO_BRILLANTE = new Color(220, 60, 60);
	private static final Color COLOR_ROJO_HOVER = new Color(235, 80, 80);
	private static final Color COLOR_GRIS_OSCURO = new Color(50, 50, 55);
	private static final Color COLOR_GRIS_OSCURO_HOVER = new Color(65, 65, 70);
	private static final Color COLOR_AVISO = new Color(160, 110, 40);
	private static final Color COLOR_AVISO_HOVER = new Color(180, 130, 55);

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					List<Jugador> prueba = new ArrayList<>();
					NuevaPartida frame = new NuevaPartida(prueba, 40);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Constructor modificado
	public NuevaPartida(List<Jugador> jugadoresSeleccionados, int vidasIniciales) {
	    this.vidasInicialesGlobales = vidasIniciales;
		this.jugadoresEnPartida = jugadoresSeleccionados;
		setTitle("Mesa de Juego - Commander");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1100, 750); // Un poco más grande para mejor visualización
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(10, 10));

		// ==========================================
		// 0. MENSAJE DE BIENVENIDA TEMPORAL (3 segundos)
		// ==========================================
		String[] frasesInicio = {
			"¡Que comience el baile! 🎲",
			"¡Prepara los dados y que gane el mejor! ⚔️",
			"Recuerda: el daño de comandante es acumulativo. 💀",
			"¡A por esa victoria o a morir en el intento! 🔥",
			"Que la suerte (y el mana) esté con vosotros. 🌟",
			"Si tienes Sol Ring en turno 1 pagas la cena 🍻",
			"Sois más malos que pegar a un padre"
		};
		String fraseAleatoria = frasesInicio[new Random().nextInt(frasesInicio.length)];
		
		JDialog dialogMensaje = new JDialog(this, "¡Nueva Partida!", true);
		dialogMensaje.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel lblMensaje = new JLabel(fraseAleatoria, SwingConstants.CENTER);
		// 🟢 "Segoe UI Emoji" para que los emojis de la frase se vean bien
		// en vez de cuadros vacíos.
		lblMensaje.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
		dialogMensaje.add(lblMensaje);
		dialogMensaje.setSize(400, 120);
		dialogMensaje.setLocationRelativeTo(this);
		
		Timer timerMensaje = new Timer(3000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialogMensaje.dispose();
			}
		});
		timerMensaje.setRepeats(false);
		timerMensaje.start();
		dialogMensaje.setVisible(true);

		// ==========================================
		// 1. ZONA NORTE: HUD de Control (Rediseño Premium)
		// ==========================================
		JPanel panelSuperior = new JPanel();
		panelSuperior.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 15));
		panelSuperior.setOpaque(true);
		panelSuperior.setBackground(new Color(40, 40, 45)); // Fondo más oscuro e integrado
		panelSuperior.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(70, 70, 75)),
			BorderFactory.createEmptyBorder(10, 20, 10, 20)
		));

		// --- CRONÓMETRO ---
		JPanel boxCrono = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(50, 50, 55));
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
				g2.setColor(new Color(100, 255, 100, 30));
				g2.setStroke(new BasicStroke(1.5f));
				g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		boxCrono.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 0));
		boxCrono.setPreferredSize(new Dimension(180, 55));
		boxCrono.setOpaque(false);
		
		JLabel lblIconoCrono = new JLabel("");
		lblIconoCrono.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 24));
		lblIconoCrono.setForeground(new Color(100, 255, 100));
		boxCrono.add(lblIconoCrono);
		
		lblCronometro = new JLabel("00:00");
		lblCronometro.setFont(new Font("Segoe UI", Font.BOLD, 28));
		lblCronometro.setForeground(new Color(100, 255, 100));
		boxCrono.add(lblCronometro);
		
		JButton btnPausar = new JButton("");
		btnPausar.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 16));
		btnPausar.setForeground(Color.WHITE);
		btnPausar.setBackground(new Color(60, 60, 65));
		btnPausar.setFocusPainted(false);
		btnPausar.setBorderPainted(false);
		btnPausar.setOpaque(true);
		btnPausar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		btnPausar.setToolTipText("Pausar/Reanudar cronómetro");
		btnPausar.setPreferredSize(new Dimension(40, 40));
		boxCrono.add(btnPausar);
		
		panelSuperior.add(boxCrono);

		// --- TURNOS ---
		JPanel boxTurno = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(50, 50, 55));
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
				g2.setColor(new Color(100, 200, 255, 30));
				g2.setStroke(new BasicStroke(1.5f));
				g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		boxTurno.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 0));
		boxTurno.setPreferredSize(new Dimension(140, 55));
		boxTurno.setOpaque(false);
		
		JLabel lblIconoTurno = new JLabel("↻");
		lblIconoTurno.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 24));
		lblIconoTurno.setForeground(new Color(100, 200, 255));
		boxTurno.add(lblIconoTurno);
		
		lblTurno = new JLabel("1");
		lblTurno.setFont(new Font("Segoe UI", Font.BOLD, 28));
		lblTurno.setForeground(new Color(100, 200, 255));
		boxTurno.add(lblTurno);
		
		JButton btnSiguienteTurno = new JButton("▶");
		btnSiguienteTurno.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 16));
		btnSiguienteTurno.setForeground(Color.WHITE);
		btnSiguienteTurno.setBackground(new Color(60, 60, 65));
		btnSiguienteTurno.setFocusPainted(false);
		btnSiguienteTurno.setBorderPainted(false);
		btnSiguienteTurno.setOpaque(true);
		btnSiguienteTurno.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		btnSiguienteTurno.setToolTipText("Siguiente turno");
		btnSiguienteTurno.setPreferredSize(new Dimension(40, 40));
		boxTurno.add(btnSiguienteTurno);
		
		panelSuperior.add(boxTurno);

		// --- DADO (único, usando PopupDado) ---
		JButton btnTirarDado = new JButton("🎲 Tirar Dado");
		btnTirarDado.setFont(new Font("Segoe UI", Font.BOLD, 15));
		btnTirarDado.setForeground(Color.WHITE);
		btnTirarDado.setBackground(COLOR_ROJO_BRILLANTE);
		btnTirarDado.setFocusPainted(false);
		btnTirarDado.setBorderPainted(false);
		btnTirarDado.setOpaque(true);
		btnTirarDado.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		btnTirarDado.setToolTipText("Tirar dado con animación");
		btnTirarDado.setPreferredSize(new Dimension(160, 50));
		btnTirarDado.addActionListener(e -> {
			PopupDado popupDado = new PopupDado((java.awt.Frame) SwingUtilities.getWindowAncestor(this));
			popupDado.setVisible(true);
		});
		panelSuperior.add(btnTirarDado);

		contentPane.add(panelSuperior, BorderLayout.NORTH);

		// ==========================================
		// 2. ZONA CENTRAL: La Mesa de Juego
		// ==========================================
		// 🟢 Columnas adaptables: ceil(√nºJugadores) reparte la rejilla
		// lo más cuadrada posible según cuántos jugadores haya (2 → lado
		// a lado, 4 → 2×2, 6 → 3×2, 9 → 3×3, etc.) en vez de forzar
		// siempre 2 columnas.
		int numJugadores = jugadoresSeleccionados.size();
		int columnasMesa = Math.max(1, (int) Math.ceil(Math.sqrt(numJugadores)));

		JPanel panelMesa = new JPanel();
		panelMesa.setLayout(new GridLayout(0, columnasMesa, 10, 10)); // filas automáticas
		contentPane.add(panelMesa, BorderLayout.CENTER);

		// 🟢 CORRECCIÓN: Usamos la lista global de la clase, no creamos una nueva local
		this.panelesDeJuego.clear(); 

		for (Jugador j : jugadoresSeleccionados) {
		    // 🟢 CORRECCIÓN: Pasamos los 5 parámetros, incluyendo ordenDeEliminacion
		    PanelJugador panel = new PanelJugador(j, vidasIniciales, jugadoresSeleccionados, this.panelesDeJuego, this.ordenDeEliminacion);
		    
		    this.panelesDeJuego.add(panel);
		    panelMesa.add(panel);
		}

		// ==========================================
		// 3. ZONA SUR: Botones de control global
		// ==========================================
		JPanel panelInferior = new JPanel();
		panelInferior.setLayout(new GridLayout(1, 3, 10, 10));
		// 🟢 Fondo oscuro a juego con el resto de la app (antes quedaba
		// con el gris claro por defecto de Swing).
		panelInferior.setOpaque(true);
		panelInferior.setBackground(new Color(45, 45, 48));
		panelInferior.setBorder(new EmptyBorder(10, 0, 0, 0));
		
		// 🟢 Botones redondeados, mismo componente que en PanelControlJugador
		// y FormularioJugador, en vez de JButton planos.
		JButton btnVolver = new BotonRedondeado("Volver al Menú", COLOR_GRIS_OSCURO, COLOR_GRIS_OSCURO_HOVER);
		btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnVolver.setForeground(Color.WHITE);
		btnVolver.setPreferredSize(new Dimension(0, 46));
		btnVolver.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		panelInferior.add(btnVolver);
		
		JButton btnReiniciar = new BotonRedondeado("Reiniciar Partida", COLOR_AVISO, COLOR_AVISO_HOVER);
		btnReiniciar.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnReiniciar.setForeground(Color.WHITE);
		btnReiniciar.setPreferredSize(new Dimension(0, 46));
		btnReiniciar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		panelInferior.add(btnReiniciar);
		
		JButton btnFinalizar = new BotonRedondeado("Finalizar Partida", COLOR_ROJO_BRILLANTE, COLOR_ROJO_HOVER);
		btnFinalizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnFinalizar.setForeground(Color.WHITE);
		btnFinalizar.setPreferredSize(new Dimension(0, 46));
		btnFinalizar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		panelInferior.add(btnFinalizar);
		
		contentPane.add(panelInferior, BorderLayout.SOUTH);

		// ==========================================
		// 4. LÓGICA DE LOS BOTONES
		// ==========================================
				
		// Lógica del Cronómetro
		timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				segundosTranscurridos++;
				int minutos = segundosTranscurridos / 60;
				int segundos = segundosTranscurridos % 60;
				lblCronometro.setText(String.format("%02d:%02d", minutos, segundos));
			}
		});
		timer.start();

		// Lógica del botón Pausar / Reanudar
		btnPausar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (timer.isRunning()) {
					timer.stop();
					btnPausar.setText("▶"); 
					lblCronometro.setForeground(Color.RED);
					lblIconoCrono.setForeground(Color.RED); // El icono también se pone rojo
				} else {
					timer.start();
					btnPausar.setText("⏸"); 
					lblCronometro.setForeground(new Color(100, 255, 100));
					lblIconoCrono.setForeground(new Color(180, 180, 180)); // Vuelve al gris
				}
			}
		});

		// Botón Volver
		btnVolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.stop();
				int opcion = JOptionPane.showConfirmDialog(null, 
					"¿Seguro que quieres salir? Se perderá el progreso.", "Confirmar", JOptionPane.YES_NO_OPTION);
				if (opcion == JOptionPane.YES_OPTION) {
					dispose();
					MenuPrincipal menu = new MenuPrincipal();
					menu.setVisible(true);
				} else {
					timer.start();
				}
			}
		});
		
		// Botón Reiniciar (Con confirmación de seguridad)
		btnReiniciar.addActionListener(new ActionListener() {
		       public void actionPerformed(ActionEvent e) {
		           int opcion = JOptionPane.showConfirmDialog(NuevaPartida.this,
		                   "¿Seguro que quieres reiniciar la partida?\nSe perderá todo el progreso actual.",
		                   "Confirmar Reinicio", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		           if (opcion == JOptionPane.YES_OPTION) {
		               // 1. Resetear cronómetro
		               segundosTranscurridos = 0;
		               lblCronometro.setText("00:00");
		               if (!timer.isRunning()) {
		                   timer.start();
		                   btnPausar.setText("⏸");
		                   lblCronometro.setForeground(new Color(100, 255, 100));
		               }
		               
		               // 2. Resetear todos los paneles de jugador
		               for (PanelJugador panel : panelesDeJuego) {
		                   panel.reiniciarPanel(vidasInicialesGlobales);
		               }

		               // 3. Limpiar la lista de eliminados para que las posiciones se reseteen
		               ordenDeEliminacion.clear();
		               
		               JOptionPane.showMessageDialog(NuevaPartida.this, 
		                   "Partida y cronómetro reiniciados a 0.", 
		                   "Partida Reiniciada", JOptionPane.INFORMATION_MESSAGE);
		           }
		       }
		});
		   
		// Lógica del botón "Siguiente Turno"
		btnSiguienteTurno.addActionListener(new ActionListener() {
		       public void actionPerformed(ActionEvent e) {
		           turnoActual++;
		           lblTurno.setText(String.valueOf(turnoActual));
		           
		           // Efecto visual: parpadeo en el número y el icono
		           lblTurno.setForeground(Color.RED);
		           lblIconoTurno.setForeground(Color.RED);
		           
		           Timer flashTimer = new Timer(300, new ActionListener() {
		               public void actionPerformed(ActionEvent e) {
		                   lblTurno.setForeground(new Color(100, 200, 255));
		                   lblIconoTurno.setForeground(new Color(180, 180, 180));
		               }
		           });
		           flashTimer.setRepeats(false);
		           flashTimer.start();
		       }
		});

		
		// Botón Finalizar
		btnFinalizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.stop();
				
				String fechaActual = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
				int duracion = segundosTranscurridos / 60;
				
				JPanel panelPosiciones = new JPanel(new java.awt.GridLayout(jugadoresEnPartida.size() + 1, 2, 10, 10));
				panelPosiciones.setBorder(javax.swing.BorderFactory.createTitledBorder("Asigna la posición final a cada jugador"));
				
				panelPosiciones.add(new JLabel("Jugador", javax.swing.SwingConstants.CENTER));
				panelPosiciones.add(new JLabel("Posición", javax.swing.SwingConstants.CENTER));
				
				Map<modelo.Jugador, javax.swing.JComboBox<Integer>> combosPosicion = new HashMap<>();
				
				for (modelo.Jugador j : jugadoresEnPartida) {
					panelPosiciones.add(new JLabel(j.getNombre(), javax.swing.SwingConstants.LEFT));
					
					javax.swing.JComboBox<Integer> combo = new javax.swing.JComboBox<>();
					for (int i = 1; i <= jugadoresEnPartida.size(); i++) {
						combo.addItem(i);
					}
					
					// 🟢 LÓGICA DE PRE-SELECCIÓN AUTOMÁTICA
					int posicionSugerida = 1;
					int indiceEliminacion = ordenDeEliminacion.indexOf(j);
					
					if (indiceEliminacion != -1) {
						posicionSugerida = jugadoresEnPartida.size() - indiceEliminacion;
					}
					combo.setSelectedItem(posicionSugerida);
					
					combosPosicion.put(j, combo);
					panelPosiciones.add(combo);
				}
				
				int opcion = JOptionPane.showConfirmDialog(
					NuevaPartida.this, panelPosiciones, 
					"Finalizar Partida - Tiempo: " + lblCronometro.getText(), 
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE
				);
				
				if (opcion == JOptionPane.OK_OPTION) {
					int idPartida = dao.GestorBD.guardarPartida(fechaActual, duracion, "Partida normal");
					
					if (idPartida != -1) {
						String ganador = "Desconocido";
						for (modelo.Jugador j : jugadoresEnPartida) {
							int posicion = (int) combosPosicion.get(j).getSelectedItem();
							String tipo = (posicion == 1) ? "Victoria" : "Derrota";
							
							if (posicion == 1) {
								ganador = j.getNombre();
							}
							dao.GestorBD.guardarResultado(idPartida, j.getId(), posicion, tipo);
						}
						
						JOptionPane.showMessageDialog(NuevaPartida.this, 
							"¡Partida guardada en el Historial!\n\n🏆 Ganador: " + ganador,
							"Partida Finalizada", JOptionPane.INFORMATION_MESSAGE);
					}
					
					dispose();
					MenuPrincipal menu = new MenuPrincipal();
					menu.setVisible(true);
				} else {
					timer.start();
				}
			}
		});
	}

	/**
	 * Botón con esquinas redondeadas, mismo componente que en
	 * PanelControlJugador y FormularioJugador, para que los botones de
	 * control global compartan el mismo lenguaje visual que el resto de
	 * la app.
	 */
	private static class BotonRedondeado extends JButton {
		private static final long serialVersionUID = 1L;
		private final Color colorBase;
		private final Color colorHover;
		private boolean hover = false;

		BotonRedondeado(String texto, Color colorBase, Color colorHover) {
			super(texto);
			this.colorBase = colorBase;
			this.colorHover = colorHover;
			setContentAreaFilled(false);
			setFocusPainted(false);
			setBorderPainted(false);
			setOpaque(false);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					hover = true;
					repaint();
				}
				@Override
				public void mouseExited(MouseEvent e) {
					hover = false;
					repaint();
				}
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(hover ? colorHover : colorBase);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
			g2.dispose();
			super.paintComponent(g);
		}
	}
}