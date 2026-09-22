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
import javax.swing.ImageIcon;
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
    private String rutaAvatarTemporal;

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
        setSize(420, 450); 
        setLocationRelativeTo(null); 
        setResizable(false); // Evita que el usuario deforme la ventana
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(30, 30, 30, 30));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(20, 20));
        
        // ==========================================
        // 1. ZONA NORTE: Título limpio (Sin emojis que fallan)
        // ==========================================
        JLabel lblTitulo = new JLabel(
            jugadorAEditar == null ? "Añadir Jugador" : "Editar Jugador",
            SwingConstants.CENTER
        );
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(255, 255, 255)); // Blanco puro para mejor contraste
        contentPane.add(lblTitulo, BorderLayout.NORTH);
        
        // ==========================================
        // 2. ZONA CENTRO: Formulario
        // ==========================================
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new GridLayout(0, 1, 20, 0)); // Espacio vertical entre elementos
        panelCentral.setOpaque(false);
        
        // --- Campo Nombre ---
        JPanel panelNombre = new JPanel(new BorderLayout(0, 8));
        panelNombre.setOpaque(false);
        JLabel lblNombre = new JLabel("Nombre del jugador");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNombre.setForeground(new Color(180, 180, 180)); // Gris claro elegante
        panelNombre.add(lblNombre, BorderLayout.NORTH);
        
        txtNombre = new JTextField();
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtNombre.setPreferredSize(new Dimension(0, 45));
        // FlatLaf maneja bien los bordes, pero aseguramos un estilo limpio
        panelNombre.add(txtNombre, BorderLayout.CENTER);
        panelCentral.add(panelNombre);
        
        // --- Panel del Avatar (Rediseñado para no deformarse) ---
        JPanel panelAvatar = new JPanel(new BorderLayout(0, 10));
        panelAvatar.setOpaque(false);
        
        JLabel lblAvatarTitulo = new JLabel("Foto de perfil (Avatar)");
        lblAvatarTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAvatarTitulo.setForeground(new Color(180, 180, 180));
        lblAvatarTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelAvatar.add(lblAvatarTitulo, BorderLayout.NORTH);
        
        // Contenedor para centrar la imagen
        JPanel panelImagenContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panelImagenContainer.setOpaque(false);
        
        lblPreviewAvatar = new JLabel("Sin imagen");
        lblPreviewAvatar.setPreferredSize(new Dimension(120, 120)); // Cuadrado perfecto
        lblPreviewAvatar.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65), 2));
        lblPreviewAvatar.setOpaque(true);
        lblPreviewAvatar.setBackground(new Color(30, 30, 35));
        lblPreviewAvatar.setForeground(new Color(100, 100, 100));
        lblPreviewAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblPreviewAvatar.setVerticalAlignment(SwingConstants.CENTER);
        lblPreviewAvatar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelImagenContainer.add(lblPreviewAvatar);
        panelAvatar.add(panelImagenContainer, BorderLayout.CENTER);
        
        btnCambiarAvatar = new JButton("Seleccionar Foto");
        btnCambiarAvatar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCambiarAvatar.setPreferredSize(new Dimension(0, 35));
        btnCambiarAvatar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35)); // Se adapta al ancho
        btnCambiarAvatar.setBackground(new Color(50, 50, 55));
        btnCambiarAvatar.setForeground(Color.WHITE);
        btnCambiarAvatar.setFocusPainted(false);
        btnCambiarAvatar.setBorderPainted(false);
        btnCambiarAvatar.setOpaque(true);
        btnCambiarAvatar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelAvatar.add(btnCambiarAvatar, BorderLayout.SOUTH);
        
        panelCentral.add(panelAvatar);
        contentPane.add(panelCentral, BorderLayout.CENTER);
        
        // ==========================================
        // 3. ZONA SUR: Botones de Acción (Colores más sobrios)
        // ==========================================
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setOpaque(false);
        
        btnGuardar = new JButton(jugadorAEditar == null ? "Guardar" : "Actualizar");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setPreferredSize(new Dimension(140, 40));
        // Color azul elegante en lugar de verde chillón
        btnGuardar.setBackground(new Color(0, 120, 215)); 
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setOpaque(true);
        btnGuardar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelBotones.add(btnGuardar);
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setPreferredSize(new Dimension(140, 40));
        // Color gris oscuro en lugar de rojo chillón
        btnCancelar.setBackground(new Color(60, 60, 65)); 
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
        
        btnCancelar.addActionListener(e -> dispose());
        btnCambiarAvatar.addActionListener(e -> seleccionarAvatar());

        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nombre = txtNombre.getText().trim();
                
                if (!nombre.isEmpty()) {
                    if (jugadorAEditar != null) {
                        jugadorAEditar.setNombre(nombre);
                        if (rutaAvatarTemporal != null && !rutaAvatarTemporal.isEmpty()) {
                            jugadorAEditar.setAvatarPath(rutaAvatarTemporal);
                        }
                        GestorBD.guardarJugador(jugadorAEditar);
                        int indice = modeloRecibido.indexOf(jugadorAEditar);
                        if (indice >= 0) modeloRecibido.setElementAt(jugadorAEditar, indice);
                    } else {
                        Jugador nuevoJugador = new Jugador(0, nombre, "");
                        if (rutaAvatarTemporal != null && !rutaAvatarTemporal.isEmpty()) {
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
    
    private void seleccionarAvatar() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar foto de perfil");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "jpeg", "png"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            try {
                File carpetaAvatars = new File("avatars");
                if (!carpetaAvatars.exists()) carpetaAvatars.mkdir();
                
                String nombreJugador = txtNombre.getText().trim().isEmpty() ? "jugador" : txtNombre.getText().trim();
                String extension = archivoSeleccionado.getName().substring(archivoSeleccionado.getName().lastIndexOf("."));
                // Usamos timestamp para evitar sobrescribir si se cambia la foto
                String nombreArchivo = nombreJugador.replace(" ", "_") + "_" + System.currentTimeMillis() + extension;
                
                java.nio.file.Path origen = archivoSeleccionado.toPath();
                java.nio.file.Path destino = java.nio.file.Paths.get("avatars", nombreArchivo);
                java.nio.file.Files.copy(origen, destino, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                rutaAvatarTemporal = "avatars/" + nombreArchivo;
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
                // Escalamos manteniendo proporción dentro de 120x120
                ImageIcon icon = new ImageIcon(img.getScaledInstance(120, 120, Image.SCALE_SMOOTH));
                lblPreviewAvatar.setIcon(icon);
                lblPreviewAvatar.setText(""); // Quitar texto
                lblPreviewAvatar.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 2)); // Borde azul
            }
        } catch (Exception e) {
            lblPreviewAvatar.setText("Error");
        }
    }
}