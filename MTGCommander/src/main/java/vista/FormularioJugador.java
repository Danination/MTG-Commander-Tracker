package vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

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
	private JButton btnCambiarAvatar;
	private JLabel lblPreviewAvatar; // Para ver una miniatura
	
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
		
        // ==========================================
        // ZONA DEL AVATAR
        // ==========================================
        JLabel lblAvatarTitulo = new JLabel("Foto de perfil (Avatar):");
        contentPane.add(lblAvatarTitulo);
        
        JPanel panelAvatar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelAvatar.setOpaque(false);
        
        lblPreviewAvatar = new JLabel("Sin imagen");
        lblPreviewAvatar.setPreferredSize(new Dimension(60, 60));
        lblPreviewAvatar.setBorder(javax.swing.BorderFactory.createLineBorder(Color.GRAY, 1));
        lblPreviewAvatar.setOpaque(true);
        lblPreviewAvatar.setBackground(Color.BLACK);
        lblPreviewAvatar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPreviewAvatar.setForeground(Color.WHITE);
        panelAvatar.add(lblPreviewAvatar);
        
        btnCambiarAvatar = new JButton("📷 Cambiar");
        btnCambiarAvatar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelAvatar.add(btnCambiarAvatar);
        
        contentPane.add(panelAvatar);
        
        // Si estamos editando y el jugador ya tiene un avatar, lo mostramos
        if (jugadorAEditar != null && jugadorAEditar.getAvatarPath() != null && !jugadorAEditar.getAvatarPath().isEmpty()) {
            cargarMiniaturaAvatar(jugadorAEditar.getAvatarPath());
        }

        // Lógica del botón Cambiar Avatar
        btnCambiarAvatar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Seleccionar foto de perfil");
                fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "jpeg", "png"));
                
                int result = fileChooser.showOpenDialog(FormularioJugador.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File archivoSeleccionado = fileChooser.getSelectedFile();
                    
                    try {
                        // 1. Crear carpeta 'avatars' si no existe
                        File carpetaAvatars = new File("avatars");
                        if (!carpetaAvatars.exists()) {
                            carpetaAvatars.mkdir();
                        }
                        
                        // 2. Definir el nombre del archivo (usamos el nombre del jugador para que sea único)
                        String nombreJugador = txtNombre.getText().trim().isEmpty() ? "jugador" : txtNombre.getText().trim();
                        String extension = archivoSeleccionado.getName().substring(archivoSeleccionado.getName().lastIndexOf("."));
                        String nombreArchivo = nombreJugador.replace(" ", "_") + extension;
                        
                        // 3. Copiar el archivo a la carpeta avatars
                        Path origen = archivoSeleccionado.toPath();
                        Path destino = Paths.get("avatars", nombreArchivo);
                        Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
                        
                        // 4. Guardar la ruta relativa en el jugador
                        String rutaRelativa = "avatars/" + nombreArchivo;
                        if (jugadorAEditar != null) {
                            jugadorAEditar.setAvatarPath(rutaRelativa);
                        } else {
                            // Si es nuevo, lo guardamos en una variable temporal hasta que se guarde el jugador
                            // (Lo manejaremos en el botón Guardar)
                        }
                        
                        // 5. Mostrar la miniatura
                        cargarMiniaturaAvatar(rutaRelativa);
                        JOptionPane.showMessageDialog(FormularioJugador.this, "Avatar actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(FormularioJugador.this, "Error al guardar la imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

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
	
    private void cargarMiniaturaAvatar(String ruta) {
        try {
            File archivo = new File(ruta);
            if (archivo.exists()) {
                java.awt.Image img = javax.imageio.ImageIO.read(archivo);
                javax.swing.ImageIcon icon = new javax.swing.ImageIcon(img.getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH));
                lblPreviewAvatar.setIcon(icon);
                lblPreviewAvatar.setText(""); // Quitar el texto "Sin imagen"
            }
        } catch (Exception e) {
            lblPreviewAvatar.setText("Error");
        }
    }
}