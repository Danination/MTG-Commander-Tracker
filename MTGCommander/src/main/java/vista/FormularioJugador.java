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

import dao.GestorBD;
import modelo.Jugador;

public class FormularioJugador extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtNombre;
	private JTextField txtColor;
	private JButton btnGuardar;
	private JButton btnCancelar;
	private Jugador jugadorAEditar;
	
	private DefaultListModel<Jugador> modeloRecibido;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormularioJugador frame = new FormularioJugador(null, null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public FormularioJugador(DefaultListModel<Jugador> modelo, Jugador jugadorAEditar) {
		this.modeloRecibido = modelo;
		this.jugadorAEditar = jugadorAEditar; 
		
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
		        String nombre = txtNombre.getText().trim();
		        String color = txtColor.getText().trim();
		        
		        if (!nombre.isEmpty()) {
		            if (jugadorAEditar != null) {
		                // MODO EDICIÓN: Actualizamos el objeto y guardamos en BD
		                jugadorAEditar.setNombre(nombre);
		                jugadorAEditar.setColorFavorito(color);
		                
		                //  NUEVO: Guardar cambios en la Base de Datos (UPDATE)
		                GestorBD.guardarJugador(jugadorAEditar);
		                
		                // Actualizar en la lista visual
		                int indice = modeloRecibido.indexOf(jugadorAEditar);
		                modeloRecibido.setElementAt(jugadorAEditar, indice);
		            } else {
		                // MODO CREACIÓN: Creamos uno nuevo con ID 0
		                Jugador nuevoJugador = new Jugador(0, nombre, color);
		                
		                // 🟢 NUEVO: Guardar en la Base de Datos (INSERT)
		                // Esto asignará el ID real generado por la BD al objeto nuevoJugador
		                GestorBD.guardarJugador(nuevoJugador);
		                
		                // Añadir a la lista visual
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