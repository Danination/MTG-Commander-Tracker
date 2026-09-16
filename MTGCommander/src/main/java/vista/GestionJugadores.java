package vista;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import dao.GestorBD;
import modelo.Jugador;

public class GestionJugadores extends JFrame {

	private static final long serialVersionUID = 1L;
	
	// ==========================================
	// 1. ATRIBUTOS DE LA CLASE
	// ==========================================
	private JPanel contentPane;
	private DefaultListModel<Jugador> modeloJugadores;
	private JList<Jugador> listaJugadores;
	
	private JButton btnVolver;
	private JButton btnAñadir;
	private JButton btnEditar;
	private JButton btnEliminar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GestionJugadores frame = new GestionJugadores();
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
	public GestionJugadores() {
		
		// ==========================================
		// 2. CONFIGURACIÓN BÁSICA DE LA VENTANA
		// ==========================================
		setTitle("Gestión de Jugadores");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 350); 
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(10, 10)); 
		
		// ==========================================
		// 3. INICIALIZACIÓN DEL MODELO Y CARGA DESDE BD
		// ==========================================
		modeloJugadores = new DefaultListModel<>();
		
		// 🟢 NUEVO: Cargar jugadores desde la Base de Datos
		List<Jugador> jugadoresBD = GestorBD.obtenerTodosLosJugadores();
		for (Jugador j : jugadoresBD) {
			modeloJugadores.addElement(j);
		}
		System.out.println("📂 Cargados " + jugadoresBD.size() + " jugadores desde la BD.");
		
		// ==========================================
		// 4. CREACIÓN DE COMPONENTES VISUALES
		// ==========================================
		
		// --- Zona NORTH (Botón Volver) ---
		btnVolver = new JButton("Volver al Menú");
		btnVolver.setFont(new Font("Tahoma", Font.BOLD, 14));
		contentPane.add(btnVolver, BorderLayout.NORTH);
		
		// --- Zona CENTER (Lista de Jugadores) ---
		listaJugadores = new JList<>(modeloJugadores);
		listaJugadores.setFont(new Font("Tahoma", Font.PLAIN, 16));
		contentPane.add(listaJugadores, BorderLayout.CENTER);
		
		// --- Zona SOUTH (Panel de Botones de Acción) ---
		JPanel panelBotones = new JPanel();
		panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10)); 
		contentPane.add(panelBotones, BorderLayout.SOUTH);
		
		btnAñadir = new JButton("Añadir");
		btnAñadir.setFont(new Font("Tahoma", Font.PLAIN, 15));
		panelBotones.add(btnAñadir);
		
		btnEditar = new JButton("Editar");
		btnEditar.setFont(new Font("Tahoma", Font.PLAIN, 15));
		panelBotones.add(btnEditar);
		
		btnEliminar = new JButton("Eliminar");
		btnEliminar.setFont(new Font("Tahoma", Font.PLAIN, 15));
		panelBotones.add(btnEliminar);
		
		// ==========================================
		// 5. LÓGICA DE LOS BOTONES (ACTION LISTENERS)
		// ==========================================
		
		// Botón Volver
		btnVolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				MenuPrincipal menu = new MenuPrincipal();
				menu.setVisible(true);
			}
		});
		
		// Botón Añadir
		btnAñadir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FormularioJugador formulario = new FormularioJugador(modeloJugadores, null);
				formulario.setVisible(true);
			}
		});
		
		// Botón Editar
		btnEditar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Jugador seleccionado = listaJugadores.getSelectedValue();
				
				if (seleccionado == null) {
					JOptionPane.showMessageDialog(null, 
						"Por favor, selecciona un jugador de la lista para editar.", 
						"Aviso", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				FormularioJugador formulario = new FormularioJugador(modeloJugadores, seleccionado);
				formulario.setVisible(true);
			}
		});
		
		// Botón Eliminar
		btnEliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Jugador seleccionado = listaJugadores.getSelectedValue();
				
				if (seleccionado == null) {
					JOptionPane.showMessageDialog(null, 
						"Por favor, selecciona un jugador de la lista primero.", 
						"Aviso", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int opcion = JOptionPane.showConfirmDialog(null,
						"¿Estás seguro de que quieres eliminar a " + seleccionado.getNombre() + "?",
						"Confirmar eliminación",
						JOptionPane.YES_NO_OPTION);
				
				if (opcion == JOptionPane.YES_OPTION) {
					// 🟢 NUEVO: Eliminar de la Base de Datos
					GestorBD.eliminarJugador(seleccionado.getId());
					
					// Eliminar de la lista visual
					modeloJugadores.removeElement(seleccionado);
				}
			}
		});
	}
}