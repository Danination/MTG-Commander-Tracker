package vista;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.FlatDarkLaf;

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

    // Colores unificados
    private static final Color COLOR_ROJO_BRILLANTE = new Color(220, 60, 60);
    private static final Color COLOR_GRIS_OSCURO = new Color(50, 50, 55);
    private static final Color COLOR_GRIS_MEDIO = new Color(70, 70, 75);

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    FlatDarkLaf.setup();
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
        
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle(jugadorAEditar == null ? "Añadir Jugador" : "Editar Jugador");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 620); // 🟢 Un poco más grande
        setLocationRelativeTo(null); 
        setResizable(false);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(35, 35, 35, 35));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 25));
        
        // ==========================================
        // 1. ZONA NORTE: Título rojo + Línea decorativa
        // ==========================================
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setOpaque(false);
        panelNorte.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel lblTitulo = new JLabel(jugadorAEditar == null ? "AÑADIR JUGADOR" : "EDITAR JUGADOR", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 32)); // 🟢 Más grande
        lblTitulo.setForeground(COLOR_ROJO_BRILLANTE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNorte.add(lblTitulo);
        
        panelNorte.add(Box.createVerticalStrut(10));
        
        JPanel lineaDecorativa = new JPanel();
        lineaDecorativa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        lineaDecorativa.setBackground(new Color(150, 35, 35));
        lineaDecorativa.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNorte.add(lineaDecorativa);
        
        contentPane.add(panelNorte, BorderLayout.NORTH);
        
        // ==========================================
        // 2. ZONA CENTRO: Tarjeta redondeada con formulario
        // ==========================================
        JPanel panelContenedorTarjeta = new JPanel(new BorderLayout());
        panelContenedorTarjeta.setOpaque(false);
        panelContenedorTarjeta.setBorder(new EmptyBorder(0, 15, 0, 15));
        
        JPanel panelTarjeta = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradiente = new GradientPaint(
                    0, 0, new Color(50, 50, 55),
                    0, getHeight(), new Color(40, 40, 45)
                );
                g2.setPaint(gradiente);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                
                g2.setColor(new Color(70, 70, 75));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panelTarjeta.setOpaque(false);
        panelTarjeta.setBorder(new EmptyBorder(30, 30, 30, 30));
        panelTarjeta.setLayout(new BoxLayout(panelTarjeta, BoxLayout.Y_AXIS));
        
        // --- Campo Nombre (SIN borde doble) ---
        JPanel panelNombre = new JPanel(new BorderLayout(0, 10));
        panelNombre.setOpaque(false);
        panelNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNombre.setMaximumSize(new Dimension(400, 90));
        
        JLabel lblNombre = new JLabel("Nombre del jugador");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNombre.setForeground(new Color(200, 200, 200));
        panelNombre.add(lblNombre, BorderLayout.NORTH);
        
        txtNombre = new JTextField();
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        txtNombre.setPreferredSize(new Dimension(0, 48));
        // 🟢 BORRE SIMPLE: Solo un borde gris, sin compound
        txtNombre.setBorder(BorderFactory.createLineBorder(COLOR_GRIS_MEDIO, 1));
        txtNombre.setBackground(new Color(35, 35, 40));
        txtNombre.setForeground(Color.WHITE);
        txtNombre.setCaretColor(Color.WHITE);
        panelNombre.add(txtNombre, BorderLayout.CENTER);
        panelTarjeta.add(panelNombre);
        
        panelTarjeta.add(Box.createVerticalStrut(35));
        
        // --- Panel del Avatar (sin maximumSize restrictivo) ---
        JPanel panelAvatar = new JPanel();
        panelAvatar.setLayout(new BoxLayout(panelAvatar, BoxLayout.Y_AXIS));
        panelAvatar.setOpaque(false);
        panelAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        // 🟢 Quitamos el maximumSize que aplastaba todo
        panelAvatar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        
        JLabel lblAvatarTitulo = new JLabel("Foto de perfil", SwingConstants.CENTER);
        lblAvatarTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblAvatarTitulo.setForeground(new Color(200, 200, 200));
        lblAvatarTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelAvatar.add(lblAvatarTitulo);
        
        panelAvatar.add(Box.createVerticalStrut(15));
        
        // 🟢 Contenedor para centrar el avatar sin deformarlo
        JPanel panelImagenContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panelImagenContainer.setOpaque(false);
        panelImagenContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblPreviewAvatar = new JLabel();
        lblPreviewAvatar.setPreferredSize(new Dimension(130, 130));
        lblPreviewAvatar.setOpaque(false);
        lblPreviewAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblPreviewAvatar.setVerticalAlignment(SwingConstants.CENTER);
        lblPreviewAvatar.setFont(new Font("Segoe UI", Font.BOLD, 60));
        lblPreviewAvatar.setForeground(new Color(80, 80, 85));
        lblPreviewAvatar.setText("?");
        panelImagenContainer.add(lblPreviewAvatar);
        panelAvatar.add(panelImagenContainer);
        
        panelAvatar.add(Box.createVerticalStrut(20));
        
        // 🟢 Botón con ancho fijo razonable
        btnCambiarAvatar = new JButton(" Seleccionar Foto");
        btnCambiarAvatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCambiarAvatar.setPreferredSize(new Dimension(220, 45));
        btnCambiarAvatar.setMaximumSize(new Dimension(220, 45));
        btnCambiarAvatar.setBackground(COLOR_GRIS_MEDIO);
        btnCambiarAvatar.setForeground(Color.WHITE);
        btnCambiarAvatar.setFocusPainted(false);
        btnCambiarAvatar.setBorderPainted(false);
        btnCambiarAvatar.setOpaque(true);
        btnCambiarAvatar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCambiarAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelAvatar.add(btnCambiarAvatar);
        
        panelTarjeta.add(panelAvatar);
        panelContenedorTarjeta.add(panelTarjeta, BorderLayout.CENTER);
        
        contentPane.add(panelContenedorTarjeta, BorderLayout.CENTER);
        
        // ==========================================
        // 3. ZONA SUR: Botones de Acción
        // ==========================================
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        panelBotones.setOpaque(false);
        
        btnGuardar = new JButton(jugadorAEditar == null ? "Guardar" : "Actualizar");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 16)); // 🟢 Más grande
        btnGuardar.setPreferredSize(new Dimension(160, 48)); // 🟢 Más alto
        btnGuardar.setBackground(COLOR_ROJO_BRILLANTE);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setOpaque(true);
        btnGuardar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelBotones.add(btnGuardar);
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCancelar.setPreferredSize(new Dimension(160, 48));
        btnCancelar.setBackground(COLOR_GRIS_OSCURO);
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
    
    private ImageIcon recortarImagenRedondeada(Image imagenOriginal, int ancho, int alto, int radio) {
        BufferedImage imagenRecortada = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagenRecortada.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        // 🟢 Calcular dimensiones manteniendo el aspect ratio
        int imgAncho = imagenOriginal.getWidth(null);
        int imgAlto = imagenOriginal.getHeight(null);
        double ratio = Math.min((double) ancho / imgAncho, (double) alto / imgAlto);
        int nuevoAncho = (int) (imgAncho * ratio);
        int nuevoAlto = (int) (imgAlto * ratio);
        
        // 🟢 Centrar la imagen dentro del área
        int x = (ancho - nuevoAncho) / 2;
        int y = (alto - nuevoAlto) / 2;
        
        //  Crear forma redondeada
        Shape formaRedondeada = new RoundRectangle2D.Double(0, 0, ancho, alto, radio, radio);
        g2.setClip(formaRedondeada);
        
        // 🟢 Dibujar la imagen centrada y escalada correctamente
        g2.drawImage(imagenOriginal, x, y, nuevoAncho, nuevoAlto, null);
        g2.dispose();
        
        return new ImageIcon(imagenRecortada);
    }
    
    private void cargarMiniaturaAvatar(String ruta) {
        try {
            File archivo = new File(ruta);
            if (archivo.exists()) {
                Image img = javax.imageio.ImageIO.read(archivo);
                ImageIcon icon = recortarImagenRedondeada(img, 130, 130, 20);
                lblPreviewAvatar.setIcon(icon);
                lblPreviewAvatar.setText(""); // Quitar el ?
                lblPreviewAvatar.setBorder(BorderFactory.createLineBorder(COLOR_ROJO_BRILLANTE, 2));
            }
        } catch (Exception e) {
            lblPreviewAvatar.setText("?");
            lblPreviewAvatar.setIcon(null);
        }
    }
}