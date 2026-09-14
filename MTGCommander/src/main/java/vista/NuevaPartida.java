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

import modelo.GestorDatos;
import modelo.Jugador;
import modelo.Partida;
import modelo.ResultadoJugador;

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
					prueba.add(new Jugador(1, "Dani", "Rojo"));
					prueba.add(new Jugador(2, "Mainez", "Verde"));
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

		   for (Jugador j : jugadoresSeleccionados) {
		       PanelJugador panel = new PanelJugador(j, vidasIniciales);
		       panelesDeJuego.add(panel);
		       panelMesa.add(panel);
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
		        timer.stop(); // 1. Detenemos el cronómetro
		        
		        // 2. Creamos un array con los nombres de los jugadores para mostrarlos en el diálogo
		        String[] nombresJugadores = new String[jugadoresEnPartida.size()];
		        for (int i = 0; i < jugadoresEnPartida.size(); i++) {
		            nombresJugadores[i] = jugadoresEnPartida.get(i).getNombre();
		        }
		        
		        // 3. Mostramos un diálogo preguntando quién ha ganado
		        String ganadorNombre = (String) JOptionPane.showInputDialog(
		            NuevaPartida.this, 
		            "Selecciona el ganador de la partida:", 
		            "Fin de Partida - Tiempo: " + lblCronometro.getText(), 
		            JOptionPane.QUESTION_MESSAGE, 
		            null, 
		            nombresJugadores, 
		            nombresJugadores[0] // El primero de la lista aparece seleccionado por defecto
		        );
		        
		        // 4. Si el usuario cancela o cierra la ventana, no hacemos nada
		        if (ganadorNombre == null) {
		            timer.start(); // Si cancela, reanudamos el cronómetro por si acaso
		            return;
		        }
		        
		        // 5. ¡Guardar la Partida en el Historial Global!
		        // Buscamos el objeto Jugador completo basándonos en el nombre seleccionado
		        Jugador jugadorGanador = null;
		        for (Jugador j : jugadoresEnPartida) {
		            if (j.getNombre().equals(ganadorNombre)) {
		                jugadorGanador = j;
		                break;
		            }
		        }
		        
		        // Creamos el Resultado del ganador (Posición 1)
		        ResultadoJugador resultadoGanador = new ResultadoJugador(jugadorGanador, null, 1, "Victoria");
		        // Nota: null en comandante porque aún no hemos implementado la selección de mazos.
		        
		        // Creamos la Partida
		        Partida nuevaPartida = new Partida(
		            GestorDatos.historialPartidas.size() + 1, // ID autoincremental simple
		            java.time.LocalDateTime.now(),            // Fecha y hora actual
		            segundosTranscurridos / 60,               // Duración en minutos (aprox)
		            "Partida normal"                          // Notas por defecto
		        );
		        
		        // Añadimos el resultado a la partida
		        nuevaPartida.añadirResultado(resultadoGanador);
		        
		        // ¡La guardamos en la pizarra global!
		        GestorDatos.historialPartidas.add(nuevaPartida);
		        
		        // 6. Avisamos al usuario y volvemos al menú
		        JOptionPane.showMessageDialog(NuevaPartida.this, 
		            "¡Partida guardada en el Historial!\nGanador: " + ganadorNombre + "\nTiempo: " + lblCronometro.getText());
		            
		        dispose();
		        MenuPrincipal menu = new MenuPrincipal();
		        menu.setVisible(true);
		    }
		});
	}
}