package vista;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatDarkLaf;

public class MenuPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    
    // Colores oficiales
    private static final Color COLOR_TITULO = new Color(255, 215, 0); 
    private static final Color COLOR_SUBTITULO = new Color(150, 150, 150); 
    
    // Colores específicos por botón (un poco más vibrantes para el degradado)
    private static final Color COLOR_NUEVA_PARTIDA = new Color(30, 140, 90); 
    private static final Color COLOR_JUGADORES = new Color(40, 90, 160); 
    private static final Color COLOR_RANKING = new Color(160, 110, 20); 
    private static final Color COLOR_HISTORIAL = new Color(110, 60, 140); 

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    FlatDarkLaf.setup();
                    MenuPrincipal frame = new MenuPrincipal();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public MenuPrincipal() {
        setTitle("MTG Commander");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 650); // 🟢 Ventana más grande
        setLocationRelativeTo(null);
        setResizable(false);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(40, 40, 40, 40)); // 🟢 Más margen
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 30));
        
        // ==========================================
        // 1. ZONA NORTE: Título + Subtítulo
        // ==========================================
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new GridLayout(2, 1, 0, 10));
        panelNorte.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("MTG COMMANDER", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 42)); // 🟢 Más grande
        lblTitulo.setForeground(COLOR_TITULO);
        panelNorte.add(lblTitulo);
        
        JLabel lblSubtitulo = new JLabel("Gestor de partidas y estadísticas", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblSubtitulo.setForeground(COLOR_SUBTITULO);
        panelNorte.add(lblSubtitulo);
        
        contentPane.add(panelNorte, BorderLayout.NORTH);
        
        // ==========================================
        // 2. ZONA CENTRO: Cuadrícula de botones 2x2
        // ==========================================
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(2, 2, 30, 30)); // 🟢 Más espacio entre botones
        panelBotones.setOpaque(false);
        
        JButton btnNuevaPartida = crearBotonPremium("Nueva Partida", COLOR_NUEVA_PARTIDA, "\u2694");
        JButton btnJugadores = crearBotonPremium("Jugadores", COLOR_JUGADORES, "\u263A");
        JButton btnRanking = crearBotonPremium("Ranking", COLOR_RANKING, "\u2605");
        JButton btnHistorial = crearBotonPremium("Historial", COLOR_HISTORIAL, "\u231B");
        
        panelBotones.add(btnNuevaPartida);
        panelBotones.add(btnJugadores);
        panelBotones.add(btnRanking);
        panelBotones.add(btnHistorial);
        
        contentPane.add(panelBotones, BorderLayout.CENTER);
        
        // ==========================================
        // 3. ZONA SUR: Pie de página sutil
        // ==========================================
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSur.setOpaque(false);
        JLabel lblVersion = new JLabel("v1.0 - Diseñado para Commander");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblVersion.setForeground(new Color(80, 80, 80));
        panelSur.add(lblVersion);
        contentPane.add(panelSur, BorderLayout.SOUTH);
        
        // ==========================================
        // 4. LÓGICA DE LOS BOTONES
        // ==========================================
        btnNuevaPartida.addActionListener(e -> { dispose(); new ConfigurarPartida().setVisible(true); });
        btnJugadores.addActionListener(e -> { dispose(); new GestionJugadores().setVisible(true); });
        btnRanking.addActionListener(e -> { dispose(); new Ranking().setVisible(true); });
        btnHistorial.addActionListener(e -> { dispose(); new Historial().setVisible(true); });
    }
    
    // ==========================================
    // 🟢 MÉTODO MÁGICO: Crear botón redondeado con profundidad
    // ==========================================
    private JButton crearBotonPremium(String texto, Color colorBase, String iconoUnicode) {
        
        // Creamos un JButton anónimo que redibuja su fondo
        JButton boton = new JButton() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Activar suavizado de bordes (antialiasing) para las curvas
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 1. Textura: Degradado vertical (de más claro arriba a más oscuro abajo)
                GradientPaint gradiente = new GradientPaint(
                    0, 0, getBackground().brighter(), 
                    0, getHeight(), getBackground().darker()
                );
                g2.setPaint(gradiente);
                
                // 2. Dibujar el rectángulo redondeado (30px de radio en las esquinas)
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 35, 35);
                
                // 3. Borde sutil superior para dar efecto de relieve/brillo
                g2.setColor(new Color(255, 255, 255, 50)); // Blanco semitransparente
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 35, 35);
                
                g2.dispose();
                super.paintComponent(g); // Dibuja el texto y el icono encima
            }
        };
        
        // Configuración del botón
        boton.setLayout(new BorderLayout());
        boton.setBackground(colorBase);
        boton.setForeground(Color.WHITE);
        
        // Clave para que el custom painting funcione
        boton.setContentAreaFilled(false); 
        boton.setOpaque(false);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        //  Tamaño mucho más grande
        boton.setPreferredSize(new Dimension(320, 220)); 
        
        // Icono
        JLabel lblIcono = new JLabel(iconoUnicode, SwingConstants.CENTER);
        lblIcono.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 60)); // Icono más grande
        lblIcono.setForeground(new Color(255, 255, 255, 220));
        lblIcono.setBorder(new EmptyBorder(10, 0, 0, 0));
        boton.add(lblIcono, BorderLayout.NORTH);
        
        // Texto
        JLabel lblTexto = new JLabel(texto, SwingConstants.CENTER);
        lblTexto.setFont(new Font("Segoe UI", Font.BOLD, 26)); // Texto más grande
        lblTexto.setForeground(Color.WHITE);
        lblTexto.setBorder(new EmptyBorder(0, 0, 25, 0));
        boton.add(lblTexto, BorderLayout.CENTER);
        
        // Efectos Hover y Click
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(colorBase.brighter().brighter()); // Se ilumina al pasar
                boton.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(colorBase); // Vuelve al color original
                boton.repaint();
            }
            @Override
            public void mousePressed(MouseEvent e) {
                boton.setBackground(colorBase.darker()); // Se oscurece al pulsar
                boton.repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                boton.setBackground(colorBase.brighter().brighter()); // Vuelve a iluminado
                boton.repaint();
            }
        });
        
        return boton;
    }
}