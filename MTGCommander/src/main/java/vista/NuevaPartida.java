package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
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
		lblMensaje.setFont(new Font("Segoe UI", Font.BOLD, 16));
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
		// 1. ZONA NORTE: HUD de Control (Rediseño Moderno)
		// ==========================================
		JPanel panelSuperior = new JPanel();
		panelSuperior.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 12));
		panelSuperior.setOpaque(true);
		panelSuperior.setBackground(new Color(45, 45, 48)); // Fondo oscuro integrado

		// --- CRONÓMETRO ---
		JPanel boxCrono = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		boxCrono.setOpaque(false);
		
		JLabel lblIconoCrono = new JLabel("⏱");
		lblIconoCrono.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 22));
		lblIconoCrono.setForeground(new Color(180, 180, 180));
		boxCrono.add(lblIconoCrono);
		
		lblCronometro = new JLabel("00:00");
		lblCronometro.setFont(new Font("Segoe UI", Font.BOLD, 32));
		lblCronometro.setForeground(new Color(100, 255, 100));
		boxCrono.add(lblCronometro);
		
		JButton btnPausar = new JButton("⏸");
		btnPausar.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18));
		btnPausar.setForeground(Color.WHITE);
		btnPausar.setBackground(new Color(60, 60, 65));
		btnPausar.setFocusPainted(false);
		btnPausar.setBorderPainted(false);
		btnPausar.setOpaque(true);
		btnPausar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		btnPausar.setToolTipText("Pausar/Reanudar cronómetro");
		boxCrono.add(btnPausar);
		
		panelSuperior.add(boxCrono);

		// Separador vertical elegante
		JLabel separador1 = new JLabel("│");
		separador1.setFont(new Font("Segoe UI", Font.PLAIN, 24));
		separador1.setForeground(new Color(80, 80, 85));
		panelSuperior.add(separador1);

		// --- TURNOS ---
		JPanel boxTurno = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		boxTurno.setOpaque(false);
		
		JLabel lblIconoTurno = new JLabel("⟳");
		lblIconoTurno.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 22));
		lblIconoTurno.setForeground(new Color(180, 180, 180));
		boxTurno.add(lblIconoTurno);
		
		lblTurno = new JLabel("1");
		lblTurno.setFont(new Font("Segoe UI", Font.BOLD, 32));
		lblTurno.setForeground(new Color(100, 200, 255));
		boxTurno.add(lblTurno);
		
		JButton btnSiguienteTurno = new JButton("▶");
		btnSiguienteTurno.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18));
		btnSiguienteTurno.setForeground(Color.WHITE);
		btnSiguienteTurno.setBackground(new Color(60, 60, 65));
		btnSiguienteTurno.setFocusPainted(false);
		btnSiguienteTurno.setBorderPainted(false);
		btnSiguienteTurno.setOpaque(true);
		btnSiguienteTurno.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		btnSiguienteTurno.setToolTipText("Siguiente turno");
		boxTurno.add(btnSiguienteTurno);
		
		panelSuperior.add(boxTurno);

		// Separador vertical elegante
		JLabel separador2 = new JLabel("│");
		separador2.setFont(new Font("Segoe UI", Font.PLAIN, 24));
		separador2.setForeground(new Color(80, 80, 85));
		panelSuperior.add(separador2);

		// --- DADOS ---
		JButton btnDados = new JButton(" Tirar Dado");
		btnDados.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnDados.setForeground(Color.WHITE);
		btnDados.setBackground(new Color(70, 70, 75));
		btnDados.setFocusPainted(false);
		btnDados.setBorderPainted(false);
		btnDados.setOpaque(true);
		btnDados.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		btnDados.setToolTipText("Tirar dados virtuales");
		panelSuperior.add(btnDados);

		contentPane.add(panelSuperior, BorderLayout.NORTH);

		// ==========================================
		// 2. ZONA CENTRAL: La Mesa de Juego
		// ==========================================
		JPanel panelMesa = new JPanel();
		panelMesa.setLayout(new GridLayout(0, 2, 10, 10)); // 2 columnas, filas automáticas
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
		
		JButton btnVolver = new JButton("Volver al Menú");
		btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 14));
		panelInferior.add(btnVolver);
		
		JButton btnReiniciar = new JButton("Reiniciar Partida");
		btnReiniciar.setFont(new Font("Segoe UI", Font.BOLD, 14));
		panelInferior.add(btnReiniciar);
		
		JButton btnFinalizar = new JButton("Finalizar Partida");
		btnFinalizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
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

		// Lógica del botón "Tirar Dados"
		btnDados.addActionListener(new ActionListener() {
		       public void actionPerformed(ActionEvent e) {
		           String[] opciones = {"d6 (6 caras)", "d20 (20 caras)", "d100 (100 caras)"};
		           String seleccion = (String) JOptionPane.showInputDialog(
		               NuevaPartida.this, "¿Qué dado quieres tirar?", "Lanzador de Dados",
		               JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]
		           );
		           
		           if (seleccion != null) {
		               int resultado = 0;
		               if (seleccion.contains("d6")) resultado = (int)(Math.random() * 6) + 1;
		               else if (seleccion.contains("d20")) resultado = (int)(Math.random() * 20) + 1;
		               else if (seleccion.contains("d100")) resultado = (int)(Math.random() * 100) + 1;
		               
		               JOptionPane.showMessageDialog(NuevaPartida.this, 
		                   "¡Has tirado un " + seleccion + "!\n\nResultado: " + resultado, 
		                   "Resultado del Dado", JOptionPane.INFORMATION_MESSAGE);
		           }
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
}