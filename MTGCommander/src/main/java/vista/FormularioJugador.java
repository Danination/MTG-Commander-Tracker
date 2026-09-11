package vista;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class FormularioJugador extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtNombre;
	private JTextField txtColor;
	private JButton btnGuardar;
	private JButton btnCancelar;
	
	// 1. CORREGIDO: Nombre en minúscula (convención Java)
	private modelo.Jugador jugadorAEditar;
	
	// Atributo para guardar la referencia al modelo de la lista
	private DefaultListModel<modelo.Jugador> modeloRecibido;

	/**
	 * Launch the application. (Solo para pruebas)
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// 2. CORREGIDO: Pasamos dos null porque el constructor ahora pide 2 parámetros
					FormularioJugador frame = new FormularioJugador(null, null);
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
	// 3. CORREGIDO: Añadido el segundo parámetro 'jugadorAEditar'
	public FormularioJugador(DefaultListModel<modelo.Jugador> modelo, modelo.Jugador jugadorAEditar) {
		// 1. Guardamos las referencias que nos envía la ventana anterior
		this.modeloRecibido = modelo;
		this.jugadorAEditar = jugadorAEditar; 
		
		// Título dinámico
		setTitle(jugadorAEditar == null ? "Nuevo Jugador" : "Editar Jugador");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
		setBounds(100, 100, 400, 200); 
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(3, 2, 10, 10)); 
		
		JLabel lblNombre = new JLabel("Nombre:");
		contentPane.add(lblNombre);
		
		txtNombre = new JTextField();
		contentPane.add(txtNombre);
		txtNombre.setColumns(10);
		
		JLabel lblColor = new JLabel("Color favorito:");
		contentPane.add(lblColor);
		
		txtColor = new JTextField();
		contentPane.add(txtColor);
		txtColor.setColumns(10);
		
		// Texto del botón dinámico
		btnGuardar = new JButton(jugadorAEditar == null ? "Guardar" : "Actualizar");
		contentPane.add(btnGuardar);
		
		btnCancelar = new JButton("Cancelar");
		contentPane.add(btnCancelar);
		
		// PRE-RELLENAR los datos si estamos en modo edición
		if (jugadorAEditar != null) {
			txtNombre.setText(jugadorAEditar.getNombre());
			txtColor.setText(jugadorAEditar.getColorFavorito());
		}

		// --- LÓGICA DE LOS BOTONES ---

		// Botón Cancelar
		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose(); 
			}
		});

		// Botón Guardar / Actualizar
		btnGuardar.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        String nombre = txtNombre.getText();
		        String color = txtColor.getText();
		        
		        if (!nombre.trim().isEmpty()) {
		            if (jugadorAEditar != null) {
		                // MODO EDICIÓN: Actualizamos el objeto existente
		                jugadorAEditar.setNombre(nombre);
		                jugadorAEditar.setColorFavorito(color);
		                
		                // TRUCO DE SWING: Forzamos a la lista a repintarse con el objeto actualizado
		                int indice = modeloRecibido.indexOf(jugadorAEditar);
		                modeloRecibido.setElementAt(jugadorAEditar, indice);
		            } else {
		                // MODO CREACIÓN: Creamos uno nuevo
		                int idAleatorio = (int) (Math.random() * 10000);
		                modelo.Jugador nuevoJugador = new modelo.Jugador(idAleatorio, nombre, color);
		                modeloRecibido.addElement(nuevoJugador);
		            }
		            dispose(); // Cerramos el formulario en ambos casos
		        } else {
		            JOptionPane.showMessageDialog(null, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
		        }
		    }
		});
	}
}