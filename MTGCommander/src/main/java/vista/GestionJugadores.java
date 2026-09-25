package vista;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatDarkLaf;

import dao.GestorBD;
import modelo.Jugador;

public class GestionJugadores extends JFrame {

    private static final long serialVersionUID = 1L;
    
    private JPanel contentPane;
    private DefaultListModel<Jugador> modeloJugadores;
    private JList<Jugador> listaJugadores;
    private JLabel lblContador;
    
    private JButton btnVolver;
    private JButton btnAñadir;
    private JButton btnEditar;
    private JButton btnEliminar;
    
    // Colores rojo brillante
    private static final Color COLOR_ROJO_BRILLANTE = new Color(220, 60, 60);
    private static final Color COLOR_ROJO_OSCURO = new Color(139, 26, 26);
    private static final Color COLOR_ROJO_MEDIO = new Color(165, 42, 42);

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    FlatDarkLaf.setup();
                    GestionJugadores frame = new GestionJugadores();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public GestionJugadores() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("Gestión de Jugadores");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(30, 30, 30, 30));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 10)); // De 20 a 10 píxeles
        
        // ==========================================
        // 1. ZONA NORTE: Título + Línea + Contador
        // ==========================================
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS)); // 🟢 Cambio clave: BoxLayout vertical
        panelNorte.setOpaque(false);
        panelNorte.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Título
        JLabel lblTitulo = new JLabel("GESTIÓN DE JUGADORES", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitulo.setForeground(COLOR_ROJO_BRILLANTE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNorte.add(lblTitulo);
        
        // Espacio pequeño
        panelNorte.add(Box.createVerticalStrut(8));
        
        // Línea decorativa FINA (solo 2px de alto)
        JPanel lineaDecorativa = new JPanel();
        lineaDecorativa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2)); // 🟢 Altura máxima de 2px
        lineaDecorativa.setBackground(new Color(150, 35, 35));
        lineaDecorativa.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNorte.add(lineaDecorativa);
        
        // Espacio pequeño
        panelNorte.add(Box.createVerticalStrut(8));
        
        // Contador
        lblContador = new JLabel("0 jugadores registrados", SwingConstants.CENTER);
        lblContador.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblContador.setForeground(new Color(180, 180, 180));
        lblContador.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNorte.add(lblContador);
        
        contentPane.add(panelNorte, BorderLayout.NORTH);
        
        // ==========================================
        // 2. ZONA CENTRO: Tarjeta redondeada con lista
        // ==========================================
        
        // Panel contenedor con padding para evitar que la tarjeta se salga
        JPanel panelContenedorTarjeta = new JPanel(new BorderLayout());
        panelContenedorTarjeta.setOpaque(false);
        panelContenedorTarjeta.setBorder(new EmptyBorder(0, 20, 0, 20)); // 🟢 Padding lateral de 20px
        
        // La tarjeta redondeada
        JPanel panelListaContainer = new JPanel(new BorderLayout()) {
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
        panelListaContainer.setOpaque(false);
        panelListaContainer.setBorder(new EmptyBorder(15, 15, 15, 15)); // Padding interno
        
        modeloJugadores = new DefaultListModel<>();
        
        List<Jugador> jugadoresBD = GestorBD.obtenerTodosLosJugadores();
        for (Jugador j : jugadoresBD) {
            modeloJugadores.addElement(j);
        }
        actualizarContador();
        
        listaJugadores = new JList<>(modeloJugadores);
        listaJugadores.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        listaJugadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaJugadores.setFixedCellHeight(65);
        listaJugadores.setBackground(new Color(45, 45, 48));
        listaJugadores.setForeground(Color.WHITE);
        listaJugadores.setSelectionBackground(COLOR_ROJO_MEDIO);
        listaJugadores.setSelectionForeground(Color.WHITE);
        listaJugadores.setBorder(null);
        
        listaJugadores.setCellRenderer(new JugadorCellRenderer());
        
        listaJugadores.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarJugadorSeleccionado();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(listaJugadores);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        panelListaContainer.add(scrollPane, BorderLayout.CENTER);
        
        // Añadir la tarjeta al contenedor con padding
        panelContenedorTarjeta.add(panelListaContainer, BorderLayout.CENTER);
        
        contentPane.add(panelContenedorTarjeta, BorderLayout.CENTER);
        
        // ==========================================
        // 3. ZONA SUR: Botones de acción + Volver
        // ==========================================
        JPanel panelSur = new JPanel();
        panelSur.setLayout(new GridLayout(2, 1, 0, 15));
        panelSur.setOpaque(false);
        
        // Fila 1: Botones de acción (Añadir, Editar, Eliminar)
        JPanel panelBotonesAccion = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotonesAccion.setOpaque(false);
        
        btnAñadir = new JButton("Añadir");
        btnAñadir.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnAñadir.setPreferredSize(new Dimension(140, 45));
        btnAñadir.setBackground(new Color(40, 120, 80));
        btnAñadir.setForeground(Color.WHITE);
        btnAñadir.setFocusPainted(false);
        btnAñadir.setBorderPainted(false);
        btnAñadir.setOpaque(true);
        btnAñadir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelBotonesAccion.add(btnAñadir);
        
        btnEditar = new JButton("Editar");
        btnEditar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnEditar.setPreferredSize(new Dimension(140, 45));
        btnEditar.setBackground(new Color(40, 80, 140));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setFocusPainted(false);
        btnEditar.setBorderPainted(false);
        btnEditar.setOpaque(true);
        btnEditar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelBotonesAccion.add(btnEditar);
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnEliminar.setPreferredSize(new Dimension(140, 45));
        btnEliminar.setBackground(COLOR_ROJO_OSCURO);
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setBorderPainted(false);
        btnEliminar.setOpaque(true);
        btnEliminar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelBotonesAccion.add(btnEliminar);
        
        panelSur.add(panelBotonesAccion);
        
        // Fila 2: Botón Volver alargado
        JPanel panelVolver = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panelVolver.setOpaque(false);
        
        btnVolver = new JButton("← Volver al Menú Principal");
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnVolver.setPreferredSize(new Dimension(300, 45)); // Más alargado
        btnVolver.setBackground(new Color(50, 50, 55));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setOpaque(true);
        btnVolver.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelVolver.add(btnVolver);
        
        panelSur.add(panelVolver);
        
        contentPane.add(panelSur, BorderLayout.SOUTH);
        
        // ==========================================
        // 4. LÓGICA DE LOS BOTONES
        // ==========================================
        btnVolver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                MenuPrincipal menu = new MenuPrincipal();
                menu.setVisible(true);
            }
        });
        
        btnAñadir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FormularioJugador formulario = new FormularioJugador(modeloJugadores, null);
                formulario.setVisible(true);
                javax.swing.SwingUtilities.invokeLater(() -> {
                    listaJugadores.repaint();
                    actualizarContador();
                });
            }
        });
        
        btnEditar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editarJugadorSeleccionado();
            }
        });
        
        btnEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Jugador seleccionado = listaJugadores.getSelectedValue();
                
                if (seleccionado == null) {
                    JOptionPane.showMessageDialog(GestionJugadores.this, 
                        "Por favor, selecciona un jugador de la lista primero.", 
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                int opcion = JOptionPane.showConfirmDialog(GestionJugadores.this,
                    "¿Estás seguro de que quieres eliminar a " + seleccionado.getNombre() + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
                if (opcion == JOptionPane.YES_OPTION) {
                    GestorBD.eliminarJugador(seleccionado.getId());
                    modeloJugadores.removeElement(seleccionado);
                    listaJugadores.repaint();
                    actualizarContador();
                }
            }
        });
    }
    
    private void editarJugadorSeleccionado() {
        Jugador seleccionado = listaJugadores.getSelectedValue();
        
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, selecciona un jugador de la lista para editar.", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        FormularioJugador formulario = new FormularioJugador(modeloJugadores, seleccionado);
        formulario.setVisible(true);
        javax.swing.SwingUtilities.invokeLater(() -> {
            listaJugadores.repaint();
            actualizarContador();
        });
    }
    
    private void actualizarContador() {
        int total = modeloJugadores.size();
        String texto = total == 1 ? "1 jugador registrado" : total + " jugadores registrados";
        lblContador.setText(texto);
    }
    
    // ==========================================
    // RENDERER PERSONALIZADO
    // ==========================================
    private class JugadorCellRenderer extends JPanel implements javax.swing.ListCellRenderer<Jugador> {
        
        private static final long serialVersionUID = 1L;
        private JLabel lblAvatar;
        private JLabel lblNombre;
        
        public JugadorCellRenderer() {
            setLayout(new BorderLayout(15, 0));
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            
            lblAvatar = new JLabel();
            lblAvatar.setPreferredSize(new Dimension(55, 55));
            lblAvatar.setOpaque(false);
            lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
            add(lblAvatar, BorderLayout.WEST);
            
            lblNombre = new JLabel();
            lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblNombre.setForeground(Color.WHITE);
            add(lblNombre, BorderLayout.CENTER);
        }
        
        @Override
        public Component getListCellRendererComponent(JList<? extends Jugador> list, Jugador value,
                int index, boolean isSelected, boolean cellHasFocus) {
            
            if (value != null) {
                lblNombre.setText(value.getNombre());
                
                String avatarPath = value.getAvatarPath();
                if (avatarPath != null && !avatarPath.isEmpty()) {
                    File archivoAvatar = new File(avatarPath);
                    if (archivoAvatar.exists()) {
                        try {
                            Image img = javax.imageio.ImageIO.read(archivoAvatar);
                            ImageIcon icon = new ImageIcon(img.getScaledInstance(55, 55, Image.SCALE_SMOOTH));
                            lblAvatar.setIcon(icon);
                            lblAvatar.setText("");
                        } catch (Exception e) {
                            lblAvatar.setIcon(null);
                            lblAvatar.setText("?");
                            lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 24));
                            lblAvatar.setForeground(Color.GRAY);
                        }
                    } else {
                        lblAvatar.setIcon(null);
                        lblAvatar.setText("?");
                        lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 24));
                        lblAvatar.setForeground(Color.GRAY);
                    }
                } else {
                    lblAvatar.setIcon(null);
                    lblAvatar.setText("?");
                    lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 24));
                    lblAvatar.setForeground(Color.GRAY);
                }
            }
            
            if (isSelected) {
                setBackground(COLOR_ROJO_MEDIO);
                lblNombre.setForeground(Color.WHITE);
            } else {
                setBackground(new Color(45, 45, 48));
                lblNombre.setForeground(Color.WHITE);
            }
            
            return this;
        }
    }
}