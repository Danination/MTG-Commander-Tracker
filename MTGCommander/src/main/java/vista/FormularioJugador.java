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
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

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
import javax.swing.border.AbstractBorder;
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
    private BordeRedondeado bordeAvatar;

    private Jugador jugadorAEditar;
    private DefaultListModel<Jugador> modeloRecibido;
    private String rutaAvatarTemporal;

    // Colores unificados
    private static final Color COLOR_ROJO_BRILLANTE = new Color(220, 60, 60);
    private static final Color COLOR_GRIS_OSCURO = new Color(50, 50, 55);
    private static final Color COLOR_GRIS_MEDIO = new Color(70, 70, 75);
    private static final Color COLOR_GRIS_CLARO_HOVER = new Color(90, 90, 95);
    private static final Color COLOR_ROJO_HOVER = new Color(235, 80, 80);
    private static final Color COLOR_GRIS_OSCURO_HOVER = new Color(65, 65, 70);
    private static final Color COLOR_FONDO_CAMPO = new Color(35, 35, 40);

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
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitulo.setForeground(COLOR_ROJO_BRILLANTE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNorte.add(lblTitulo);

        panelNorte.add(Box.createVerticalStrut(10));

        JPanel lineaDecorativa = new JPanel();
        lineaDecorativa.setPreferredSize(new Dimension(1, 2));
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

        // --- Campo Nombre ---
        JPanel panelNombre = new JPanel(new BorderLayout(0, 10));
        panelNombre.setOpaque(false);
        panelNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNombre.setMaximumSize(new Dimension(340, 90));

        JLabel lblNombre = new JLabel("Nombre del jugador");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNombre.setForeground(new Color(200, 200, 200));
        panelNombre.add(lblNombre, BorderLayout.NORTH);

        // 🟢 CampoRedondeado: pinta su propio fondo ya recortado en forma
        // redondeada (fillRoundRect) ANTES de que se dibuje el texto, así
        // no queda ningún resto cuadrado del fondo detrás del borde curvo
        // (eso era lo que se veía "mal" en las esquinas, sobre todo la
        // izquierda, en la captura anterior).
        txtNombre = new CampoRedondeado(COLOR_FONDO_CAMPO, 12);
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        txtNombre.setPreferredSize(new Dimension(0, 48));
        txtNombre.setHorizontalAlignment(JTextField.CENTER);
        txtNombre.setForeground(Color.WHITE);
        txtNombre.setCaretColor(Color.WHITE);
        final BordeRedondeado bordeCampo = new BordeRedondeado(COLOR_GRIS_MEDIO, 12);
        txtNombre.setBorder(bordeCampo);
        txtNombre.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                bordeCampo.setColor(COLOR_ROJO_BRILLANTE);
                txtNombre.repaint();
            }
            @Override
            public void focusLost(FocusEvent e) {
                bordeCampo.setColor(COLOR_GRIS_MEDIO);
                txtNombre.repaint();
            }
        });
        panelNombre.add(txtNombre, BorderLayout.CENTER);
        panelTarjeta.add(panelNombre);

        panelTarjeta.add(Box.createVerticalStrut(35));

        // --- Panel del Avatar ---
        JPanel panelAvatar = new JPanel();
        panelAvatar.setLayout(new BoxLayout(panelAvatar, BoxLayout.Y_AXIS));
        panelAvatar.setOpaque(false);
        panelAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblAvatarTitulo = new JLabel("Foto de perfil", SwingConstants.CENTER);
        lblAvatarTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblAvatarTitulo.setForeground(new Color(200, 200, 200));
        lblAvatarTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelAvatar.add(lblAvatarTitulo);

        panelAvatar.add(Box.createVerticalStrut(15));

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
        // 🟢 Marco redondeado SIEMPRE presente (antes solo aparecía una vez
        // cargada la foto). Empieza en gris y cambia a rojo cuando hay
        // avatar cargado, y vuelve a gris si falla la carga.
        bordeAvatar = new BordeRedondeado(COLOR_GRIS_MEDIO, 20, new Insets(0, 0, 0, 0));
        lblPreviewAvatar.setBorder(bordeAvatar);
        panelImagenContainer.add(lblPreviewAvatar);
        panelAvatar.add(panelImagenContainer);

        panelAvatar.add(Box.createVerticalStrut(20));

        btnCambiarAvatar = new BotonRedondeado(" Seleccionar Foto", COLOR_GRIS_MEDIO, COLOR_GRIS_CLARO_HOVER);
        btnCambiarAvatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCambiarAvatar.setPreferredSize(new Dimension(220, 45));
        btnCambiarAvatar.setMaximumSize(new Dimension(220, 45));
        btnCambiarAvatar.setForeground(Color.WHITE);
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
        panelBotones.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnGuardar = new BotonRedondeado(jugadorAEditar == null ? "Guardar" : "Actualizar", COLOR_ROJO_BRILLANTE, COLOR_ROJO_HOVER);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnGuardar.setPreferredSize(new Dimension(160, 48));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelBotones.add(btnGuardar);

        btnCancelar = new BotonRedondeado("Cancelar", COLOR_GRIS_OSCURO, COLOR_GRIS_OSCURO_HOVER);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCancelar.setPreferredSize(new Dimension(160, 48));
        btnCancelar.setForeground(Color.WHITE);
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

        // ==========================================
        // 6. AJUSTE FINAL DE TAMAÑO
        // ==========================================
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
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

        int imgAncho = imagenOriginal.getWidth(null);
        int imgAlto = imagenOriginal.getHeight(null);
        double ratio = Math.min((double) ancho / imgAncho, (double) alto / imgAlto);
        int nuevoAncho = (int) (imgAncho * ratio);
        int nuevoAlto = (int) (imgAlto * ratio);

        int x = (ancho - nuevoAncho) / 2;
        int y = (alto - nuevoAlto) / 2;

        Shape formaRedondeada = new RoundRectangle2D.Double(0, 0, ancho, alto, radio, radio);
        g2.setClip(formaRedondeada);

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
                lblPreviewAvatar.setText("");
                // 🟢 Ya no reemplazamos el borde: solo recoloreamos el
                // marco redondeado que siempre está ahí.
                bordeAvatar.setColor(COLOR_ROJO_BRILLANTE);
                lblPreviewAvatar.repaint();
            }
        } catch (Exception e) {
            lblPreviewAvatar.setText("?");
            lblPreviewAvatar.setIcon(null);
            bordeAvatar.setColor(COLOR_GRIS_MEDIO);
            lblPreviewAvatar.repaint();
        }
    }

    /**
     * Borde redondeado reutilizable. Se puede cambiar su color en caliente
     * (por ejemplo al ganar el foco, o al cargar/fallar un avatar) sin
     * recrear el borde ni el componente.
     */
    private static class BordeRedondeado extends AbstractBorder {
        private static final long serialVersionUID = 1L;
        private Color color;
        private final int radio;
        private final Insets insets;

        BordeRedondeado(Color color, int radio) {
            this(color, radio, new Insets(8, 14, 8, 14));
        }

        BordeRedondeado(Color color, int radio, Insets insets) {
            this.color = color;
            this.radio = radio;
            this.insets = insets;
        }

        void setColor(Color color) {
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radio, radio);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return (Insets) insets.clone();
        }

        @Override
        public Insets getBorderInsets(Component c, Insets destInsets) {
            destInsets.set(insets.top, insets.left, insets.bottom, insets.right);
            return destInsets;
        }
    }

    /**
     * Campo de texto que pinta su propio fondo ya recortado con esquinas
     * redondeadas (en vez de un rectángulo cuadrado), para que no quede
     * ningún resto del fondo asomando detrás del borde curvo.
     */
    private static class CampoRedondeado extends JTextField {
        private static final long serialVersionUID = 1L;
        private final Color colorFondo;
        private final int radio;

        CampoRedondeado(Color colorFondo, int radio) {
            this.colorFondo = colorFondo;
            this.radio = radio;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(colorFondo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Botón con esquinas redondeadas (para que combine con la tarjeta y el
     * avatar) y un efecto de resaltado sutil al pasar el mouse por encima.
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