package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import dao.GestorBD;
import modelo.Jugador;

public class FormularioJugador extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtNombre;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JButton btnCambiarAvatar;
    private JLabel lblPreviewAvatar;
    
    private Jugador jugadorAEditar;
    private DefaultListModel<Jugador> modeloRecibido;
    private String rutaAvatarTemporal; // Para guardar la ruta temporalmente

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
        setSize(450, 400); // Tamaño más adecuado
        setLocationRelativeTo(null); // Centrado en pantalla
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(25, 25, 25, 25));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(15, 15));
        
        // ==========================================
        // 1. ZONA NORTE: Título
        // ==========================================
        JLabel lblTitulo = new JLabel(
            jugadorAEditar == null ? "➕ Añadir Jugador" : "✏️ Editar Jugador",
            SwingConstants.CENTER
        );
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(255, 215, 0)); // Dorado
        contentPane.add(lblTitulo, BorderLayout.NORTH);
        
        // ==========================================
        // 2. ZONA CENTRO: Formulario
        // ==========================================
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new GridLayout(0, 1, 15, 15));
        panelFormulario.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        // Campo Nombre
        JPanel panelNombre = new JPanel(new BorderLayout(10, 0));
        panelNombre.setOpaque(false);
        JLabel lblNombre = new JLabel("Nombre del jugador:");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelNombre.add(lblNombre, BorderLayout.NORTH);
        
        txtNombre = new JTextField();
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtNombre.setPreferredSize(new Dimension(0, 40));
        panelNombre.add(txtNombre, BorderLayout.CENTER);
        contentPane.add(panelNombre, BorderLayout.CENTER);
        
        // Panel del Avatar
        JPanel panelAvatar = new JPanel(new BorderLayout(15, 10));
        panelAvatar.setOpaque(false);
        panelAvatar.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 75)),
            "Foto de perfil (Avatar)",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 13),
            Color.GRAY
        ));
        
        lblPreviewAvatar = new JLabel("Sin imagen", SwingConstants.CENTER);
        lblPreviewAvatar.setPreferredSize(new Dimension(150, 150));
        lblPreviewAvatar.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        lblPreviewAvatar.setOpaque(true);
        lblPreviewAvatar.setBackground(new Color(30, 30, 35));
        lblPreviewAvatar.setForeground(Color.WHITE);
        lblPreviewAvatar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelAvatar.add(lblPreviewAvatar, BorderLayout.CENTER);
        
        btnCambiarAvatar = new JButton("📷 Seleccionar Foto");
        btnCambiarAvatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCambiarAvatar.setPreferredSize(new Dimension(0, 40));
        btnCambiarAvatar.setBackground(new Color(60, 60, 65));
        btnCambiarAvatar.setForeground(Color.WHITE);
        btnCambiarAvatar.setFocusPainted(false);
        btnCambiarAvatar.setBorderPainted(false);
        btnCambiarAvatar.setOpaque(true);
        btnCambiarAvatar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelAvatar.add(btnCambiarAvatar, BorderLayout.SOUTH);
        
        panelFormulario.add(panelNombre);
        panelFormulario.add(panelAvatar);
        contentPane.add(panelFormulario, BorderLayout.CENTER);
        
        // ==========================================
        // 3. ZONA SUR: Botones de Acción
        // ==========================================
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setOpaque(false);
        
        btnGuardar = new JButton(jugadorAEditar == null ? "💾 Guardar" : "💾 Actualizar");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnGuardar.setPreferredSize(new Dimension(150, 45));
        btnGuardar.setBackground(new Color(0, 150, 0)); // Verde
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setOpaque(true);
        btnGuardar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelBotones.add(btnGuardar);
        
        btnCancelar = new JButton("❌ Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCancelar.setPreferredSize(new Dimension(150, 45));
        btnCancelar.setBackground(new Color(150, 0, 0)); // Rojo
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setOpaque(true);
        btnCancelar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelBotones.add(btnCancelar);
        
        contentPane.add(panelBotones, BorderLayout.SOUTH);
        
        // ==========================================
        // 4. Cargar datos si es edición
        // ==========================================
        if (jugadorAEditar != null) {
            txtNombre.setText(jugadorAEditar.getNombre());
            if (jugadorAEditar.getAvatarPath() != null && !jugadorAEditar.getAvatarPath().isEmpty()) {
                cargarMiniaturaAvatar(jugadorAEditar.getAvatarPath());
            }
        }

        // ==========================================
        // 5. LÓGICA DE LOS BOTONES
        // ==========================================
        
        // Botón Cancelar
        btnCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Botón Cambiar Avatar
        btnCambiarAvatar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                seleccionarAvatar();
            }
        });

        // Botón Guardar / Actualizar
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nombre = txtNombre.getText().trim();
                
                if (!nombre.isEmpty()) {
                    if (jugadorAEditar != null) {
                        // MODO EDICIÓN
                        jugadorAEditar.setNombre(nombre);
                        if (rutaAvatarTemporal != null) {
                            jugadorAEditar.setAvatarPath(rutaAvatarTemporal);
                        }
                        
                        GestorBD.guardarJugador(jugadorAEditar);
                        
                        int indice = modeloRecibido.indexOf(jugadorAEditar);
                        modeloRecibido.setElementAt(jugadorAEditar, indice);
                        
                    } else {
                        // MODO CREACIÓN
                        Jugador nuevoJugador = new Jugador(0, nombre, ""); // Color vacío
                        if (rutaAvatarTemporal != null) {
                            nuevoJugador.setAvatarPath(rutaAvatarTemporal);
                        }
                        
                        GestorBD.guardarJugador(nuevoJugador);
                        modeloRecibido.addElement(nuevoJugador);
                    }
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    
    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================
    
    private void seleccionarAvatar() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar foto de perfil");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "jpeg", "png"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            
            try {
                // 1. Crear carpeta 'avatars' si no existe
                File carpetaAvatars = new File("avatars");
                if (!carpetaAvatars.exists()) {
                    carpetaAvatars.mkdir();
                }
                
                // 2. Definir el nombre del archivo
                String nombreJugador = txtNombre.getText().trim().isEmpty() ? "jugador" : txtNombre.getText().trim();
                String extension = archivoSeleccionado.getName().substring(archivoSeleccionado.getName().lastIndexOf("."));
                String nombreArchivo = nombreJugador.replace(" ", "_") + "_" + System.currentTimeMillis() + extension;
                
                // 3. Copiar el archivo a la carpeta avatars
                java.nio.file.Path origen = archivoSeleccionado.toPath();
                java.nio.file.Path destino = java.nio.file.Paths.get("avatars", nombreArchivo);
                java.nio.file.Files.copy(origen, destino, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                // 4. Guardar la ruta relativa
                rutaAvatarTemporal = "avatars/" + nombreArchivo;
                
                // 5. Mostrar la miniatura
                cargarMiniaturaAvatar(rutaAvatarTemporal);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar la imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cargarMiniaturaAvatar(String ruta) {
        try {
            File archivo = new File(ruta);
            if (archivo.exists()) {
                Image img = javax.imageio.ImageIO.read(archivo);
                javax.swing.ImageIcon icon = new javax.swing.ImageIcon(img.getScaledInstance(150, 150, Image.SCALE_SMOOTH));
                lblPreviewAvatar.setIcon(icon);
                lblPreviewAvatar.setText("");
                lblPreviewAvatar.setBorder(BorderFactory.createLineBorder(new Color(0, 150, 0), 2)); // Borde verde
            }
        } catch (Exception e) {
            lblPreviewAvatar.setText("Error al cargar");
        }
    }
}