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
import java.util.List;

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
    private int dañoComandante;
    private int veneno;
    private int energia;
    private boolean esMonarca;
    private boolean eliminado = false;
    private Image imagenFondo;
    private List<Jugador> todosLosJugadores; // Necesario para el popup
    private List<PanelJugador> todosLosPaneles;

    // Componentes Visuales
    private JLabel lblVidas;
    private JLabel lblNombre;
    private JLabel lblFotoComandante;
    private JLabel lblIndicadorMonarca; // Corona pequeña si es monarca
    
    private JButton btnMasVida;
    private JButton btnMenosVida;
    private JButton btnConceder;

    public PanelJugador(Jugador jugador, int vidasIniciales, List<Jugador> todosLosJugadores, List<PanelJugador> todosLosPaneles) {
        this.jugador = jugador;
        this.vidas = vidasIniciales;
        this.dañoComandante = 0;
        this.veneno = 0;
        this.energia = 0;
        this.esMonarca = false;
        this.todosLosJugadores = todosLosJugadores;
        this.todosLosPaneles = todosLosPaneles; // <-- NUEVO
        // ... (el resto del constructor se queda igual) ...

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
        
        // Al hacer clic en la foto, abrir el popup de control
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
        
        // Indicador de Monarca (corona pequeña)
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

    // Métodos públicos para que el popup pueda modificar los valores
    public void sumarDanioComandante(int cantidad, Jugador quienHaceElDanio) {
        if (eliminado) return;
        this.dañoComandante += cantidad;
        System.out.println(jugador.getNombre() + " recibió " + cantidad + " de daño de comandante de " + quienHaceElDanio.getNombre());
    }
    
    public void sumarVeneno() { if (!eliminado) veneno++; }
    public void restarVeneno() { if (!eliminado && veneno > 0) veneno--; }
    public void sumarEnergia() { if (!eliminado) energia++; }
    public void restarEnergia() { if (!eliminado && energia > 0) energia--; }
    
    public void toggleMonarca() {
        if (eliminado) return;
        
        if (!this.esMonarca) {
            // Si nos vamos a activar, primero desactivamos a TODOS los demás paneles
            for (PanelJugador otroPanel : todosLosPaneles) {
                if (otroPanel != this) {
                    otroPanel.desactivarMonarca();
                }
            }
            // Ahora nos activamos a nosotros mismos
            this.esMonarca = true;
            this.lblIndicadorMonarca.setText("👑");
        } else {
            // Si ya éramos monarca, simplemente nos desactivamos
            this.esMonarca = false;
            this.lblIndicadorMonarca.setText("");
        }
        repaint();
    }

    // Nuevo método auxiliar para ser desactivado por otros
    public void desactivarMonarca() {
        this.esMonarca = false;
        this.lblIndicadorMonarca.setText("");
        repaint();
    }
    
    // Getters para el popup
    public int getVidas() { return vidas; }
    public int getDanioComandante() { return dañoComandante; }
    public int getVeneno() { return veneno; }
    public int getEnergia() { return energia; }
    public boolean esMonarca() { return esMonarca; }

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
        this.dañoComandante = 0;
        this.veneno = 0;
        this.energia = 0;
        this.esMonarca = false;
        this.eliminado = false;
        
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