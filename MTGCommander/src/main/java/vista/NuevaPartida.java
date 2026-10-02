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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import modelo.Jugador;

public class NuevaPartida extends JFrame {

	private List<PanelJugador> panelesDeJuego = new ArrayList<>();
	private int vidasInicialesGlobales; // Para saber a cuántas vidas resetear

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

	// Paleta de colores unificada con el resto de la app
	private static final Color COLOR_ROJO_BRILLANTE = new Color(220, 60, 60);
	private static final Color COLOR_ROJO_HOVER = new Color(235, 80, 80);
	private static final Color COLOR_GRIS_OSCURO = new Color(50, 50, 55);
	private static final Color COLOR_GRIS_OSCURO_HOVER = new Color(65, 65, 70);
	private static final Color COLOR_AVISO = new Color(160, 110, 40);
	private static final Color COLOR_AVISO_HOVER = new Color(180, 130, 55);
	private static final Color COLOR_VERDE_CRONO = new Color(100, 255, 100);
	private static final Color COLOR_AZUL_TURNO = new Color(100, 200, 255);
	private static final Color COLOR_MORADO_DADOS = new Color(186, 104, 200);
	private static final Color COLOR_FONDO_TARJETA_HUD = new Color(38, 38, 42);
	private static final Color COLOR_CAPTION = new Color(150, 150, 150);

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

	public NuevaPartida(List<Jugador> jugadoresSeleccionados, int vidasIniciales) {
	    this.vidasInicialesGlobales = vidasIniciales;
		this.jugadoresEnPartida = jugadoresSeleccionados;
		setTitle("Mesa de Juego - Commander");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1100, 750);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(10, 10));

		// ==========================================
		// 0. MENSAJE DE BIENVENIDA TEMPORAL (3 segundos)
		// ==========================================
		// 🟢 Sin emojis: en este equipo varios símbolos Unicode se
		// dibujan como cuadros vacíos incluso con la fuente "correcta",
		// así que se evita cualquier glifo especial en todo el archivo.
		String[] frasesInicio = {
			"¡Que comience el baile!",
			"¡Prepara los dados y que gane el mejor!",
			"Recuerda: el daño de comandante es acumulativo.",
			"¡A por esa victoria o a morir en el intento!",
			"Que la suerte (y el mana) esté con vosotros.",
			"Si tienes Sol Ring en turno 1 pagas la cena.",
			"Sois más malos que pegar a un padre."
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
		// 1. ZONA NORTE: HUD de Control — tarjetas redondeadas
		// ==========================================
		JPanel panelSuperior = new JPanel();
		panelSuperior.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
		panelSuperior.setOpaque(true);
		panelSuperior.setBackground(new Color(30, 30, 33));

		// --- CRONÓMETRO ---
		JPanel boxCrono = crearTarjetaHUD(COLOR_VERDE_CRONO);

		JLabel lblCaptionCrono = new JLabel("TIEMPO", SwingConstants.CENTER);
		lblCaptionCrono.setFont(new Font("Segoe UI", Font.BOLD, 10));
		lblCaptionCrono.setForeground(COLOR_CAPTION);
		boxCrono.add(lblCaptionCrono, BorderLayout.NORTH);

		JPanel filaCrono = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		filaCrono.setOpaque(false);

		lblCronometro = new JLabel("00:00");
		lblCronometro.setFont(new Font("Segoe UI", Font.BOLD, 30));
		lblCronometro.setForeground(COLOR_VERDE_CRONO);
		filaCrono.add(lblCronometro);

		JButton btnPausar = new BotonRedondeado("Pausar", COLOR_GRIS_OSCURO, COLOR_GRIS_OSCURO_HOVER);
		btnPausar.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnPausar.setForeground(Color.WHITE);
		btnPausar.setPreferredSize(new Dimension(90, 32));
		btnPausar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		btnPausar.setToolTipText("Pausar/Reanudar cronómetro");
		filaCrono.add(btnPausar);

		boxCrono.add(filaCrono, BorderLayout.CENTER);
		panelSuperior.add(boxCrono);

		// --- TURNOS ---
		JPanel boxTurno = crearTarjetaHUD(COLOR_AZUL_TURNO);

		JLabel lblCaptionTurno = new JLabel("TURNO", SwingConstants.CENTER);
		lblCaptionTurno.setFont(new Font("Segoe UI", Font.BOLD, 10));
		lblCaptionTurno.setForeground(COLOR_CAPTION);
		boxTurno.add(lblCaptionTurno, BorderLayout.NORTH);

		JPanel filaTurno = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		filaTurno.setOpaque(false);

		lblTurno = new JLabel("1");
		lblTurno.setFont(new Font("Segoe UI", Font.BOLD, 30));
		lblTurno.setForeground(COLOR_AZUL_TURNO);
		filaTurno.add(lblTurno);

		JButton btnSiguienteTurno = new BotonRedondeado("Siguiente", COLOR_GRIS_OSCURO, COLOR_GRIS_OSCURO_HOVER);
		btnSiguienteTurno.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnSiguienteTurno.setForeground(Color.WHITE);
		btnSiguienteTurno.setPreferredSize(new Dimension(90, 32));
		btnSiguienteTurno.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		btnSiguienteTurno.setToolTipText("Pasar al siguiente turno");
		filaTurno.add(btnSiguienteTurno);

		boxTurno.add(filaTurno, BorderLayout.CENTER);
		panelSuperior.add(boxTurno);

		// --- DADOS (unificado en un solo botón + menú) ---
		JPanel boxDados = crearTarjetaHUD(COLOR_MORADO_DADOS);

		JLabel lblCaptionDados = new JLabel("DADOS", SwingConstants.CENTER);
		lblCaptionDados.setFont(new Font("Segoe UI", Font.BOLD, 10));
		lblCaptionDados.setForeground(COLOR_CAPTION);
		boxDados.add(lblCaptionDados, BorderLayout.NORTH);

		JPanel filaDados = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		filaDados.setOpaque(false);

		JButton btnDados = new BotonRedondeado("Tirar Dado", COLOR_MORADO_DADOS.darker(), COLOR_MORADO_DADOS);
		btnDados.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnDados.setForeground(Color.WHITE);
		btnDados.setPreferredSize(new Dimension(150, 40));
		btnDados.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		btnDados.setToolTipText("Elegir y tirar un dado");
		filaDados.add(btnDados);

		boxDados.add(filaDados, BorderLayout.CENTER);
		panelSuperior.add(boxDados);

		contentPane.add(panelSuperior, BorderLayout.NORTH);

		// ==========================================
		// 2. ZONA CENTRAL: La Mesa de Juego
		// ==========================================
		// 🟢 Columnas adaptables: ceil(√nºJugadores) decide cuántas
		// columnas tiene una fila completa (2 → lado a lado, 4 → 2×2,
		// 6 → 3×2, etc.).
		int numJugadores = jugadoresSeleccionados.size();
		int columnasMesa = Math.max(1, (int) Math.ceil(Math.sqrt(numJugadores)));

		// 🟢 En vez de un único GridLayout fijo (que deja celdas vacías
		// cuando el número de jugadores no es múltiplo de las columnas,
		// como con 3 jugadores en una rejilla 2×2), construimos la mesa
		// fila a fila: cada fila usa GridLayout(1, nºEnEstaFila), así que
		// la última fila incompleta reparte el ANCHO COMPLETO entre los
		// jugadores que le queden, sin huecos.
		JPanel panelMesa = new JPanel();
		panelMesa.setLayout(new BoxLayout(panelMesa, BoxLayout.Y_AXIS));
		panelMesa.setOpaque(false);
		contentPane.add(panelMesa, BorderLayout.CENTER);

		this.panelesDeJuego.clear();

		int indiceJugador = 0;
		while (indiceJugador < numJugadores) {
			int enEstaFila = Math.min(columnasMesa, numJugadores - indiceJugador);

			JPanel fila = new JPanel(new GridLayout(1, enEstaFila, 10, 10));
			fila.setOpaque(false);

			for (int i = 0; i < enEstaFila; i++) {
				Jugador j = jugadoresSeleccionados.get(indiceJugador);
				PanelJugador panel = new PanelJugador(j, vidasIniciales, jugadoresSeleccionados, this.panelesDeJuego, this.ordenDeEliminacion);
				this.panelesDeJuego.add(panel);
				fila.add(panel);
				indiceJugador++;
			}

			panelMesa.add(fila);
			if (indiceJugador < numJugadores) {
				panelMesa.add(Box.createVerticalStrut(10));
			}
		}

		// ==========================================
		// 3. ZONA SUR: Botones de control global
		// ==========================================
		// 🟢 Igual que arriba las secciones del HUD van en tarjetas
		// redondeadas, aquí envolvemos los tres botones en una tarjeta
		// del mismo estilo (en vez de dejarlos sueltos sobre el fondo),
		// para que la ventana quede simétrica arriba y abajo.
		JPanel panelInferior = new JPanel(new BorderLayout());
		panelInferior.setOpaque(false);
		panelInferior.setBorder(new EmptyBorder(10, 0, 0, 0));

		JPanel tarjetaBotones = new JPanel(new GridLayout(1, 3, 15, 0)) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(COLOR_FONDO_TARJETA_HUD);
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		tarjetaBotones.setOpaque(false);
		tarjetaBotones.setBorder(new EmptyBorder(14, 14, 14, 14));

		JButton btnVolver = new BotonRedondeado("Volver al Menú", COLOR_GRIS_OSCURO, COLOR_GRIS_OSCURO_HOVER);
		btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnVolver.setForeground(Color.WHITE);
		btnVolver.setPreferredSize(new Dimension(0, 46));
		btnVolver.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		tarjetaBotones.add(btnVolver);

		JButton btnReiniciar = new BotonRedondeado("Reiniciar Partida", COLOR_AVISO, COLOR_AVISO_HOVER);
		btnReiniciar.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnReiniciar.setForeground(Color.WHITE);
		btnReiniciar.setPreferredSize(new Dimension(0, 46));
		btnReiniciar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		tarjetaBotones.add(btnReiniciar);

		JButton btnFinalizar = new BotonRedondeado("Finalizar Partida", COLOR_ROJO_BRILLANTE, COLOR_ROJO_HOVER);
		btnFinalizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnFinalizar.setForeground(Color.WHITE);
		btnFinalizar.setPreferredSize(new Dimension(0, 46));
		btnFinalizar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		tarjetaBotones.add(btnFinalizar);

		panelInferior.add(tarjetaBotones, BorderLayout.CENTER);
		contentPane.add(panelInferior, BorderLayout.SOUTH);

		// ==========================================
		// 4. LÓGICA DE LOS BOTONES
		// ==========================================

		// Cronómetro
		timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				segundosTranscurridos++;
				int minutos = segundosTranscurridos / 60;
				int segundos = segundosTranscurridos % 60;
				lblCronometro.setText(String.format("%02d:%02d", minutos, segundos));
			}
		});
		timer.start();

		// Pausar / Reanudar
		btnPausar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (timer.isRunning()) {
					timer.stop();
					btnPausar.setText("Reanudar");
					lblCronometro.setForeground(Color.RED);
				} else {
					timer.start();
					btnPausar.setText("Pausar");
					lblCronometro.setForeground(COLOR_VERDE_CRONO);
				}
			}
		});

		// Volver al menú
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

		// Reiniciar partida
		btnReiniciar.addActionListener(new ActionListener() {
		       public void actionPerformed(ActionEvent e) {
		           int opcion = JOptionPane.showConfirmDialog(NuevaPartida.this,
		                   "¿Seguro que quieres reiniciar la partida?\nSe perderá todo el progreso actual.",
		                   "Confirmar Reinicio", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		           if (opcion == JOptionPane.YES_OPTION) {
		               segundosTranscurridos = 0;
		               lblCronometro.setText("00:00");
		               if (!timer.isRunning()) {
		                   timer.start();
		                   btnPausar.setText("Pausar");
		                   lblCronometro.setForeground(COLOR_VERDE_CRONO);
		               }

		               for (PanelJugador panel : panelesDeJuego) {
		                   panel.reiniciarPanel(vidasInicialesGlobales);
		               }

		               ordenDeEliminacion.clear();

		               JOptionPane.showMessageDialog(NuevaPartida.this,
		                   "Partida y cronómetro reiniciados a 0.",
		                   "Partida Reiniciada", JOptionPane.INFORMATION_MESSAGE);
		           }
		       }
		});

		// Siguiente turno
		btnSiguienteTurno.addActionListener(new ActionListener() {
		       public void actionPerformed(ActionEvent e) {
		           turnoActual++;
		           lblTurno.setText(String.valueOf(turnoActual));

		           lblTurno.setForeground(Color.RED);

		           Timer flashTimer = new Timer(300, new ActionListener() {
		               public void actionPerformed(ActionEvent e) {
		                   lblTurno.setForeground(COLOR_AZUL_TURNO);
		               }
		           });
		           flashTimer.setRepeats(false);
		           flashTimer.start();
		       }
		});

		// Tirar Dado: un solo botón, despliega un menú para elegir el
		// tipo de dado, y cada opción lanza la tirada animada.
		btnDados.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPopupMenu menuDados = new JPopupMenu();
				int[] tiposDeDado = {4, 6, 8, 10, 12, 20, 100};
				for (int caras : tiposDeDado) {
					JMenuItem item = new JMenuItem("d" + caras);
					item.setFont(new Font("Segoe UI", Font.BOLD, 13));
					item.addActionListener(ev -> lanzarDado(caras));
					menuDados.add(item);
				}
				menuDados.show(btnDados, 0, btnDados.getHeight());
			}
		});

		// Finalizar partida
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
							"¡Partida guardada en el Historial!\n\nGanador: " + ganador,
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
	 * Tarjeta redondeada para una sección del HUD superior (Tiempo, Turno,
	 * Dados): fondo oscuro con una franja de color de acento en la parte
	 * superior. Layout BorderLayout para colocar una leyenda arriba
	 * (NORTH) y el contenido debajo (CENTER).
	 */
	private JPanel crearTarjetaHUD(Color colorAcento) {
		JPanel tarjeta = new JPanel(new BorderLayout(0, 4)) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(COLOR_FONDO_TARJETA_HUD);
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
				g2.setColor(colorAcento);
				g2.fillRoundRect(0, 0, getWidth() - 1, 5, 14, 14);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		tarjeta.setOpaque(false);
		tarjeta.setBorder(new EmptyBorder(10, 18, 10, 18));
		return tarjeta;
	}

	/**
	 * Muestra una tarjeta con el resultado de un dado de "caras" lados,
	 * con un pequeño efecto de "tirada" (números cambiando rápido antes
	 * de asentarse en el resultado final). Mismo patrón que el mensaje
	 * de bienvenida: un JDialog modal + un Timer.
	 */
	private void lanzarDado(int caras) {
		JDialog dialogDado = new JDialog(this, true);
		dialogDado.setUndecorated(true);
		dialogDado.setSize(220, 220);
		dialogDado.setLocationRelativeTo(this);

		JPanel panelDado = new JPanel(new BorderLayout(0, 10)) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(40, 40, 45));
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
				g2.setColor(COLOR_MORADO_DADOS);
				g2.setStroke(new BasicStroke(2f));
				g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		panelDado.setOpaque(false);
		panelDado.setBorder(new EmptyBorder(20, 10, 15, 10));

		JLabel lblTipoDado = new JLabel("d" + caras, SwingConstants.CENTER);
		lblTipoDado.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTipoDado.setForeground(COLOR_CAPTION);
		panelDado.add(lblTipoDado, BorderLayout.NORTH);

		JLabel lblResultado = new JLabel("?", SwingConstants.CENTER);
		lblResultado.setFont(new Font("Segoe UI", Font.BOLD, 72));
		lblResultado.setForeground(Color.WHITE);
		panelDado.add(lblResultado, BorderLayout.CENTER);

		dialogDado.setContentPane(panelDado);

		Random random = new Random();
		final int[] pasosRestantes = {14};

		Timer timerAnimacion = new Timer(60, null);
		timerAnimacion.addActionListener(ev -> {
			if (pasosRestantes[0] > 0) {
				lblResultado.setText(String.valueOf(random.nextInt(caras) + 1));
				pasosRestantes[0]--;
			} else {
				timerAnimacion.stop();
				int resultadoFinal = random.nextInt(caras) + 1;
				lblResultado.setText(String.valueOf(resultadoFinal));
				lblResultado.setForeground(COLOR_VERDE_CRONO);

				Timer timerCierre = new Timer(1400, cierre -> dialogDado.dispose());
				timerCierre.setRepeats(false);
				timerCierre.start();
			}
		});
		timerAnimacion.start();

		dialogDado.setVisible(true);
	}

	/**
	 * Botón con esquinas redondeadas, mismo componente que en
	 * PanelControlJugador y FormularioJugador.
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