package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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

import dao.GestorBD;
import modelo.Jugador;

public class NuevaPartida extends JFrame {
	
	private List<PanelJugador> panelesDeJuego = new ArrayList<>();
	private int vidasInicialesGlobales; // Para saber a cuántas vidas resetear
	
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
		setBounds(100, 100, 900, 600); 
		
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
			"Si tienes Sol Ring en turno 1 pagas la cena",
			
		};
		String fraseAleatoria = frasesInicio[new Random().nextInt(frasesInicio.length)];
		
		// Creamos un diálogo personalizado en lugar de un JOptionPane
		JDialog dialogMensaje = new JDialog(this, "¡Nueva Partida!", true);
		dialogMensaje.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel lblMensaje = new JLabel(fraseAleatoria, SwingConstants.CENTER);
		lblMensaje.setFont(new Font("Tahoma", Font.BOLD, 16));
		dialogMensaje.add(lblMensaje);
		dialogMensaje.setSize(350, 120);
		dialogMensaje.setLocationRelativeTo(this); // Centrado en la pantalla
		
		// Timer interno que cierra el mensaje a los 3000 ms (3 segundos)
		Timer timerMensaje = new Timer(3000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialogMensaje.dispose(); // Cierra el mensaje
			}
		});
		timerMensaje.setRepeats(false); // Solo se ejecuta una vez
		timerMensaje.start();
		dialogMensaje.setVisible(true); // Muestra el mensaje (bloquea hasta que se cierre)

		// ==========================================
		// 1. ZONA NORTE: Cronómetro, Turnos y Dados
		// ==========================================
		JPanel panelSuperior = new JPanel();
		panelSuperior.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Más espacio entre elementos

		// --- Cronómetro ---
		JLabel lblTextoCrono = new JLabel("Tiempo:");
		lblTextoCrono.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelSuperior.add(lblTextoCrono);

		lblCronometro = new JLabel("00:00");
		lblCronometro.setFont(new Font("Tahoma", Font.BOLD, 24));
		lblCronometro.setForeground(new Color(0, 100, 0));
		panelSuperior.add(lblCronometro);

		JButton btnPausarReanudar = new JButton("Pausar");
		btnPausarReanudar.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelSuperior.add(btnPausarReanudar);

		// --- Separador visual ---
		panelSuperior.add(new JLabel("|"));

		// --- Contador de Turnos ---
		JLabel lblTextoTurno = new JLabel("Turno:");
		lblTextoTurno.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelSuperior.add(lblTextoTurno);

		lblTurno = new JLabel("1");
		lblTurno.setFont(new Font("Tahoma", Font.BOLD, 24));
		lblTurno.setForeground(new Color(0, 0, 150)); // Azul para diferenciar
		panelSuperior.add(lblTurno);

		JButton btnSiguienteTurno = new JButton("Siguiente Turno >>");
		btnSiguienteTurno.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnSiguienteTurno.setBackground(new Color(255, 200, 0)); // Amarillo para resaltar
		panelSuperior.add(btnSiguienteTurno);

		// --- Separador visual ---
		panelSuperior.add(new JLabel("|"));

		// --- Botón de Dados ---
		JButton btnDados = new JButton(" Tirar Dados");
		btnDados.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelSuperior.add(btnDados);

		contentPane.add(panelSuperior, BorderLayout.NORTH);

		// ==========================================
		// 2. ZONA CENTRAL: La Mesa de Juego
		// ==========================================
		JPanel panelMesa = new JPanel();
		panelMesa.setLayout(new GridLayout(0, 2, 10, 10)); // 2 columnas, filas automáticas
		contentPane.add(panelMesa, BorderLayout.CENTER);

		List<PanelJugador> panelesDeJuego = new ArrayList<>(); // Lista que compartiremos

		for (Jugador j : jugadoresSeleccionados) {
		    // Le pasamos la lista (aunque esté vacía al principio, se llenará y todos la compartirán)
		    PanelJugador panel = new PanelJugador(j, vidasIniciales, jugadoresSeleccionados, panelesDeJuego);
		    
		    panelesDeJuego.add(panel); // La añadimos a la lista
		    panelMesa.add(panel);      // La añadimos a la mesa visual
		}

		// ==========================================
		// 3. ZONA SUR: Botones de control global
		// ==========================================
		JPanel panelInferior = new JPanel();
		panelInferior.setLayout(new GridLayout(1, 3, 10, 10));
		
		JButton btnVolver = new JButton("Volver al Menú");
		btnVolver.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelInferior.add(btnVolver);
		
		JButton btnReiniciar = new JButton("Reiniciar Partida");
		btnReiniciar.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelInferior.add(btnReiniciar);
		
		JButton btnFinalizar = new JButton("Finalizar Partida");
		btnFinalizar.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelInferior.add(btnFinalizar);
		
		contentPane.add(panelInferior, BorderLayout.SOUTH);

		// ==========================================
		// 4. LÓGICA DE LOS BOTONES
		// ==========================================
		
		// Lógica del Cronómetro (se actualiza cada 1 segundo)
		timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				segundosTranscurridos++;
				int minutos = segundosTranscurridos / 60;
				int segundos = segundosTranscurridos % 60;
				lblCronometro.setText(String.format("%02d:%02d", minutos, segundos));
			}
		});
		timer.start(); // Arrancamos el cronómetro al crear la ventana

		// Botón Pausar / Reanudar
		btnPausarReanudar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (timer.isRunning()) {
					timer.stop();
					btnPausarReanudar.setText("Reanudar");
					lblCronometro.setForeground(Color.RED); // Indicador visual de pausa
				} else {
					timer.start();
					btnPausarReanudar.setText("Pausar");
					lblCronometro.setForeground(new Color(0, 100, 0)); // Vuelve a verde
				}
			}
		});

		// Botón Volver
		btnVolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.stop(); // Detenemos el cronómetro al salir
				
				int opcion = JOptionPane.showConfirmDialog(null, 
					"¿Seguro que quieres salir? Se perderá el progreso.", "Confirmar", JOptionPane.YES_NO_OPTION);
				if (opcion == JOptionPane.YES_OPTION) {
					dispose();
					MenuPrincipal menu = new MenuPrincipal();
					menu.setVisible(true);
				} else {
					timer.start(); // Si cancela la salida, reanudamos el tiempo
				}
			}
		});
		
		   btnReiniciar.addActionListener(new ActionListener() {
		       public void actionPerformed(ActionEvent e) {
		           // 1. Resetear cronómetro
		           segundosTranscurridos = 0;
		           lblCronometro.setText("00:00");
		           if (!timer.isRunning()) {
		               timer.start();
		               btnPausarReanudar.setText("Pausar");
		               lblCronometro.setForeground(new Color(0, 100, 0));
		           }
		           
		           // 2. ¡Resetear todos los paneles de jugador!
		           for (PanelJugador panel : panelesDeJuego) {
		               panel.reiniciarPanel(vidasInicialesGlobales);
		           }
		           
		           JOptionPane.showMessageDialog(null, "Partida y cronómetro reiniciados a 0.");
		       }
		   });
		   
		// Lógica del botón "Siguiente Turno"
		   btnSiguienteTurno.addActionListener(new ActionListener() {
		       public void actionPerformed(ActionEvent e) {
		           turnoActual++;
		           lblTurno.setText(String.valueOf(turnoActual));
		           
		           // Efecto visual: parpadeo rápido para indicar cambio
		           lblTurno.setForeground(Color.RED);
		           Timer flashTimer = new Timer(300, new ActionListener() {
		               public void actionPerformed(ActionEvent e) {
		                   lblTurno.setForeground(new Color(0, 0, 150)); // Vuelve a azul
		               }
		           });
		           flashTimer.setRepeats(false);
		           flashTimer.start();
		       }
		   });

		   // Lógica del botón "Tirar Dados"
		   btnDados.addActionListener(new ActionListener() {
		       public void actionPerformed(ActionEvent e) {
		           // Opciones de dados comunes en Magic
		           String[] opciones = {"d6 (6 caras)", "d20 (20 caras)", "d100 (100 caras)"};
		           
		           String seleccion = (String) JOptionPane.showInputDialog(
		               NuevaPartida.this,
		               "¿Qué dado quieres tirar?",
		               "Lanzador de Dados",
		               JOptionPane.QUESTION_MESSAGE,
		               null,
		               opciones,
		               opciones[0]
		           );
		           
		           if (seleccion != null) {
		               int resultado = 0;
		               if (seleccion.contains("d6")) resultado = (int)(Math.random() * 6) + 1;
		               else if (seleccion.contains("d20")) resultado = (int)(Math.random() * 20) + 1;
		               else if (seleccion.contains("d100")) resultado = (int)(Math.random() * 100) + 1;
		               
		               JOptionPane.showMessageDialog(NuevaPartida.this, 
		                   "¡Has tirado un " + seleccion + "!\n\nResultado: " + resultado, 
		                   "Resultado del Dado", 
		                   JOptionPane.INFORMATION_MESSAGE);
		           }
		       }
		   });
		
			// Botón Finalizar
			btnFinalizar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					timer.stop();
					
					// 1. Preparar datos de la partida
					String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
					int duracion = segundosTranscurridos / 60; // Minutos
					
					// 2. Guardar la partida en la BD y obtener su ID
					int idPartida = GestorBD.guardarPartida(fechaActual, duracion, "Partida normal");
					
					if (idPartida != -1) {
						// 3. Preguntar quién ganó (usando los nombres para la interfaz)
						String[] nombresJugadores = new String[jugadoresEnPartida.size()];
						for (int i = 0; i < jugadoresEnPartida.size(); i++) {
							nombresJugadores[i] = jugadoresEnPartida.get(i).getNombre();
						}
						
						String ganadorNombre = (String) JOptionPane.showInputDialog(
							NuevaPartida.this, 
							"Selecciona el ganador de la partida:", 
							"Fin de Partida - Tiempo: " + lblCronometro.getText(), 
							JOptionPane.QUESTION_MESSAGE, 
							null, 
							nombresJugadores, 
							nombresJugadores[0]
						);
						
						if (ganadorNombre != null) {
							// 4. Guardar los resultados en la BD
							for (Jugador j : jugadoresEnPartida) {
								int posicion = 0;
								String tipo = "Derrota";
								
								if (j.getNombre().equals(ganadorNombre)) {
									posicion = 1;
									tipo = "Victoria";
								} else {
									// Para los demás, podríamos poner posición 2 por defecto o mejorar esto luego
									posicion = 2; 
								}
								
								// Guardamos en la BD
								GestorBD.guardarResultado(idPartida, j.getId(), posicion, tipo);
							}
							
							JOptionPane.showMessageDialog(NuevaPartida.this, 
								"¡Partida guardada en el Historial!\nGanador: " + ganadorNombre);
						}
					}
					
					dispose();
					MenuPrincipal menu = new MenuPrincipal();
					menu.setVisible(true);
				}
			});
	}
}