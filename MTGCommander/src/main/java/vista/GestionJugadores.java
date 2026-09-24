package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
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
import javax.swing.border.EmptyBorder;

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

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
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

    public GestionJugadores() {
        setTitle("Gestión de Jugadores");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(30, 30, 30, 30));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 25));
        
        // ==========================================
        // 1. ZONA NORTE: Título + Contador + Botón Volver (TODO JUNTO)
        // ==========================================
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BorderLayout());
        panelNorte.setOpaque(false);
        
        // Panel central con título y contador
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new GridLayout(2, 1, 0, 8));
        panelTitulo.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("Gestión de Jugadores", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(255, 215, 0)); // Dorado
        panelTitulo.add(lblTitulo);
        
        lblContador = new JLabel("0 jugadores registrados", SwingConstants.CENTER);
        lblContador.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblContador.setForeground(new Color(150, 150, 150));
        panelTitulo.add(lblContador);
        
        panelNorte.add(panelTitulo, BorderLayout.CENTER);
        
        // Botón Volver a la derecha
        JPanel panelVolver = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelVolver.setOpaque(false);
        btnVolver = new JButton("← Volver");
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVolver.setPreferredSize(new Dimension(110, 38));
        btnVolver.setBackground(new Color(50, 50, 55));
        btnVolver.setForeground(new Color(200, 200, 200));
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setOpaque(true);
        btnVolver.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelVolver.add(btnVolver);
        panelNorte.add(panelVolver, BorderLayout.EAST);
        
        contentPane.add(panelNorte, BorderLayout.NORTH);
        
        // ==========================================
        // 2. ZONA CENTRO: Lista con avatares
        // ==========================================
        modeloJugadores = new DefaultListModel<>();
        
        List<Jugador> jugadoresBD = GestorBD.obtenerTodosLosJugadores();
        for (Jugador j : jugadoresBD) {
            modeloJugadores.addElement(j);
        }
        actualizarContador();
        
        listaJugadores = new JList<>(modeloJugadores);
        listaJugadores.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        listaJugadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaJugadores.setFixedCellHeight(75);
        listaJugadores.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 55), 1));
        
        // Renderer personalizado
        listaJugadores.setCellRenderer(new JugadorCellRenderer());
        
        // Doble clic para editar
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
        contentPane.add(scrollPane, BorderLayout.CENTER);
        
        // ==========================================
        // 3. ZONA SUR: Botones (Colores sutiles, sin emojis)
        // ==========================================
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setOpaque(false);
        
        btnAñadir = new JButton("Añadir");
        btnAñadir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAñadir.setPreferredSize(new Dimension(140, 45));
        btnAñadir.setBackground(new Color(40, 120, 80)); // Verde oscuro elegante
        btnAñadir.setForeground(Color.WHITE);
        btnAñadir.setFocusPainted(false);
        btnAñadir.setBorderPainted(false);
        btnAñadir.setOpaque(true);
        btnAñadir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelBotones.add(btnAñadir);
        
        btnEditar = new JButton("Editar");
        btnEditar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEditar.setPreferredSize(new Dimension(140, 45));
        btnEditar.setBackground(new Color(40, 80, 140)); // Azul oscuro elegante
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setFocusPainted(false);
        btnEditar.setBorderPainted(false);
        btnEditar.setOpaque(true);
        btnEditar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelBotones.add(btnEditar);
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEliminar.setPreferredSize(new Dimension(140, 45));
        btnEliminar.setBackground(new Color(140, 40, 40)); // Rojo oscuro elegante
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setBorderPainted(false);
        btnEliminar.setOpaque(true);
        btnEliminar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelBotones.add(btnEliminar);
        
        contentPane.add(panelBotones, BorderLayout.SOUTH);
        
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
                // Pequeño delay para que al cerrar el formulario se actualice
                javax.swing.SwingUtilities.invokeLater(() -> actualizarContador());
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
    // RENDERER PERSONALIZADO MEJORADO
    // ==========================================
    private class JugadorCellRenderer extends JPanel implements javax.swing.ListCellRenderer<Jugador> {
        
        private static final long serialVersionUID = 1L;
        private JLabel lblAvatar;
        private JLabel lblNombre;
        
        public JugadorCellRenderer() {
            setLayout(new BorderLayout(15, 0));
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            
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
            
            // Colores de selección más elegantes
            if (isSelected) {
                setBackground(new Color(50, 90, 140)); // Azul oscuro elegante
                lblNombre.setForeground(Color.WHITE);
            } else {
                setBackground(new Color(45, 45, 48)); // Fondo oscuro normal
                lblNombre.setForeground(Color.WHITE);
            }
            
            return this;
        }
    }
}