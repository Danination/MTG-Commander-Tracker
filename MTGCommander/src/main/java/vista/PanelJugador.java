package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import modelo.Jugador;

public class PanelJugador extends JPanel {

    private static final long serialVersionUID = 1L;
    
    // Datos
    private Jugador jugador;
    private int vidas;
    private int veneno;
    private int energia;
    private boolean esMonarca;
    private boolean eliminado = false;
    private Image imagenFondo;
    private List<Jugador> todosLosJugadores;
    private List<PanelJugador> todosLosPaneles;
    
    // NUEVO: Mapa para rastrear el daño de comandante POR jugador enemigo
    private Map<Jugador, Integer> dañoPorComandante = new HashMap<>();

    // Componentes Visuales
    private JLabel lblVidas;
    private JLabel lblNombre;
    private JLabel lblFotoComandante;
    private JLabel lblIndicadorMonarca;
    
    private JButton btnMasVida;
    private JButton btnMenosVida;
    private JButton btnConceder;

    public PanelJugador(Jugador jugador, int vidasIniciales, 
                        List<Jugador> todosLosJugadores, 
                        List<PanelJugador> todosLosPaneles) {
        this.jugador = jugador;
        this.vidas = vidasIniciales;
        this.veneno = 0;
        this.energia = 0;
        this.esMonarca = false;
        this.todosLosJugadores = todosLosJugadores;
        this.todosLosPaneles = todosLosPaneles;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 500));
        setOpaque(false);
        
        cargarImagenFondo(jugador.getComandanteImagenUrl());

        // Botón MENOS (izquierda)
        btnMenosVida = new JButton("-");
        btnMenosVida.setFont(new Font("Arial", Font.BOLD, 80));
        btnMenosVida.setForeground(Color.WHITE);
        btnMenosVida.setBackground(new Color(0, 0, 0, 100));
        btnMenosVida.setOpaque(true);
        btnMenosVida.setFocusPainted(false);
        btnMenosVida.setBorderPainted(false);
        btnMenosVida.setPreferredSize(new Dimension(80, 500));
        btnMenosVida.addActionListener(e -> cambiarVidas(-1));
        add(btnMenosVida, BorderLayout.WEST);

        // Botón MÁS (derecha)
        btnMasVida = new JButton("+");
        btnMasVida.setFont(new Font("Arial", Font.BOLD, 80));
        btnMasVida.setForeground(Color.WHITE);
        btnMasVida.setBackground(new Color(0, 0, 0, 100));
        btnMasVida.setOpaque(true);
        btnMasVida.setFocusPainted(false);
        btnMasVida.setBorderPainted(false);
        btnMasVida.setPreferredSize(new Dimension(80, 500));
        btnMasVida.addActionListener(e -> cambiarVidas(1));
        add(btnMasVida, BorderLayout.EAST);

        // Vida gigante (centro)
        lblVidas = new JLabel(String.valueOf(vidas), SwingConstants.CENTER);
        lblVidas.setFont(new Font("Arial", Font.BOLD, 160));
        lblVidas.setForeground(Color.WHITE);
        lblVidas.setOpaque(false);
        add(lblVidas, BorderLayout.CENTER);

        // Panel inferior: Foto + Nombre + Indicador Monarca
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelInferior.setOpaque(false);
        
        lblFotoComandante = new JLabel();
        lblFotoComandante.setPreferredSize(new Dimension(80, 80));
        lblFotoComandante.setBorder(javax.swing.BorderFactory.createLineBorder(Color.WHITE, 2));
        lblFotoComandante.setOpaque(true);
        lblFotoComandante.setBackground(Color.BLACK);
        
        if (imagenFondo != null) {
            ImageIcon icon = new ImageIcon(imagenFondo.getScaledInstance(80, 80, Image.SCALE_SMOOTH));
            lblFotoComandante.setIcon(icon);
        } else {
            lblFotoComandante.setText("?");
            lblFotoComandante.setFont(new Font("Arial", Font.BOLD, 40));
            lblFotoComandante.setForeground(Color.WHITE);
            lblFotoComandante.setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        lblFotoComandante.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblFotoComandante.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                abrirPanelControl();
            }
        });
        
        panelInferior.add(lblFotoComandante);
        
        lblNombre = new JLabel(jugador.getNombre(), SwingConstants.CENTER);
        lblNombre.setFont(new Font("Arial", Font.BOLD, 20));
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setOpaque(true);
        lblNombre.setBackground(new Color(0, 0, 0, 180));
        lblNombre.setPreferredSize(new Dimension(150, 40));
        panelInferior.add(lblNombre);
        
        // Indicador de Monarca (corona)
        lblIndicadorMonarca = new JLabel("");
        lblIndicadorMonarca.setFont(new Font("Arial", Font.BOLD, 24));
        lblIndicadorMonarca.setForeground(new Color(255, 215, 0));
        panelInferior.add(lblIndicadorMonarca);
        
        add(panelInferior, BorderLayout.SOUTH);

        // Botón Conceder (esquina superior derecha)
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSuperior.setOpaque(false);
        btnConceder = new JButton("Conceder");
        btnConceder.setFont(new Font("Arial", Font.BOLD, 12));
        btnConceder.setBackground(new Color(200, 0, 0));
        btnConceder.setForeground(Color.WHITE);
        btnConceder.setFocusPainted(false);
        btnConceder.addActionListener(e -> confirmarConcesion());
        panelSuperior.add(btnConceder);
        add(panelSuperior, BorderLayout.NORTH);
    }

    private void abrirPanelControl() {
        PanelControlJugador popup = new PanelControlJugador(this, jugador, todosLosJugadores);
        popup.setVisible(true);
    }

    // ==========================================
    // MÉTODOS DE ESTADO (para el popup)
    // ==========================================
    
    public void sumarDanioComandante(int cantidad, Jugador quienHaceElDanio) {
        if (eliminado) return;
        
        // 1. Rastrear el daño por comandante (para la regla de los 21)
        int dañoActual = dañoPorComandante.getOrDefault(quienHaceElDanio, 0);
        dañoPorComandante.put(quienHaceElDanio, dañoActual + cantidad);
        
        // 2. ¡NUEVO! También restar las vidas totales
        cambiarVidas(-cantidad);
        
        System.out.println(jugador.getNombre() + " recibió " + cantidad + 
                          " de daño de comandante de " + quienHaceElDanio.getNombre() + 
                          ". Total de este comandante: " + (dañoActual + cantidad) +
                          ". Vidas restantes: " + vidas);
        
        // 3. Comprobar derrota por 21 de daño de un mismo comandante
        if ((dañoActual + cantidad) >= 21) {
            JOptionPane.showMessageDialog(this, 
                "¡" + jugador.getNombre() + " ha recibido 21 o más de daño de comandante de " + 
                quienHaceElDanio.getNombre() + "!\n\n¡HA PERDIDO LA PARTIDA!", 
                "¡DERROTA POR COMANDANTE!", 
                JOptionPane.WARNING_MESSAGE);
            eliminarJugador();
        }
    }
    
    public int getTotalDanioComandante() {
        int total = 0;
        for (int d : dañoPorComandante.values()) {
            total += d;
        }
        return total;
    }
    
    public Map<Jugador, Integer> getDesgloseDanioComandante() {
        return dañoPorComandante;
    }
    
    public void sumarVeneno() { 
        if (eliminado) return;
        veneno++; 
        if (veneno >= 10) {
            JOptionPane.showMessageDialog(this, 
                "¡" + jugador.getNombre() + " ha acumulado 10 contadores de veneno!\n\n¡HA PERDIDO LA PARTIDA!", 
                "¡DERROTA POR VENENO!", 
                JOptionPane.WARNING_MESSAGE);
            eliminarJugador();
        }
    }
    public void restarVeneno() { if (!eliminado && veneno > 0) veneno--; }
    public void sumarEnergia() { if (!eliminado) energia++; }
    public void restarEnergia() { if (!eliminado && energia > 0) energia--; }
    
    public void toggleMonarca() {
        if (eliminado) return;
        
        if (!this.esMonarca) {
            // Desactivar a todos los demás
            for (PanelJugador otroPanel : todosLosPaneles) {
                if (otroPanel != this) {
                    otroPanel.desactivarMonarca();
                }
            }
            this.esMonarca = true;
            this.lblIndicadorMonarca.setText("👑");
        } else {
            this.esMonarca = false;
            this.lblIndicadorMonarca.setText("");
        }
        repaint();
    }
    
    public void desactivarMonarca() {
        this.esMonarca = false;
        this.lblIndicadorMonarca.setText("");
        repaint();
    }
    
    // Getters
    public int getVidas() { return vidas; }
    public int getDanioComandante() { return getTotalDanioComandante(); }
    public int getVeneno() { return veneno; }
    public int getEnergia() { return energia; }
    public boolean esMonarca() { return esMonarca; }

    // ==========================================
    // MÉTODOS VISUALES
    // ==========================================
    
    private void cargarImagenFondo(String urlImagen) {
        if (urlImagen != null && !urlImagen.isEmpty()) {
            try {
                URL url = new URL(urlImagen);
                imagenFondo = ImageIO.read(url);
            } catch (Exception e) {
                imagenFondo = null;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        if (imagenFondo != null) {
            g2d.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2d.setColor(new Color(60, 60, 70));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        
        if (eliminado) {
            g2d.setColor(new Color(0, 0, 0, 200));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void cambiarVidas(int cantidad) {
        if (eliminado) return;
        this.vidas += cantidad;
        lblVidas.setText(String.valueOf(this.vidas));
        
        if (this.vidas < 20) lblVidas.setForeground(new Color(255, 80, 80));
        else if (this.vidas > 40) lblVidas.setForeground(new Color(100, 255, 100));
        else lblVidas.setForeground(Color.WHITE);
        
        // Comprobar derrota por vidas
        if (this.vidas <= 0) {
            JOptionPane.showMessageDialog(this, 
                "¡" + jugador.getNombre() + " ha llegado a 0 vidas!\n\n¡HA PERDIDO LA PARTIDA!", 
                "¡DERROTA!", 
                JOptionPane.WARNING_MESSAGE);
            eliminarJugador();
        }
    }

    private void confirmarConcesion() {
        if (eliminado) return;
        int opcion = JOptionPane.showConfirmDialog(this,
                "Seguro que " + jugador.getNombre() + " concede?", 
                "Confirmar Concesion", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) eliminarJugador();
    }

    private void eliminarJugador() {
        eliminado = true;
        btnMasVida.setEnabled(false);
        btnMenosVida.setEnabled(false);
        btnConceder.setText("ELIMINADO");
        btnConceder.setBackground(Color.DARK_GRAY);
        repaint();
    }

    public void reiniciarPanel(int vidasIniciales) {
        this.vidas = vidasIniciales;
        this.veneno = 0;
        this.energia = 0;
        this.esMonarca = false;
        this.eliminado = false;
        this.dañoPorComandante.clear(); // Resetear el mapa de daño
        
        lblVidas.setText(String.valueOf(vidas));
        lblVidas.setForeground(Color.WHITE);
        lblIndicadorMonarca.setText("");
        
        btnMasVida.setEnabled(true);
        btnMenosVida.setEnabled(true);
        btnConceder.setEnabled(true);
        btnConceder.setText("Conceder");
        btnConceder.setBackground(new Color(200, 0, 0));
        
        repaint();
    }

    public Jugador getJugador() { return jugador; }
}