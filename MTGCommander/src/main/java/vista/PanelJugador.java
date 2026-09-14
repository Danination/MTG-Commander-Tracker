package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.imageio.ImageIO;
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
    private boolean eliminado = false;
    private Image imagenFondo;

    // Componentes Visuales
    private JLabel lblVidas;
    private JLabel lblNombre;
    private JLabel lblCmdte;
    private JLabel lblVeneno;
    
    private JButton btnMasVida;
    private JButton btnMenosVida;
    private JButton btnConceder;
    private int energia;
    

    public PanelJugador(Jugador jugador, int vidasIniciales) {
        this.jugador = jugador;
        this.vidas = vidasIniciales;
        this.dañoComandante = 0;
        this.veneno = 0;

        // Configuración base
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 400));
        
        // 1. Cargar imagen de fondo (ilustración limpia)
        cargarImagenFondo(jugador.getComandanteImagenUrl());

        // 2. ZONA NORTE: Botón Gigante MENOS
        btnMenosVida = new JButton("−");
        btnMenosVida.setFont(new Font("Tahoma", Font.BOLD, 60));
        btnMenosVida.setFocusPainted(false);
        btnMenosVida.setOpaque(false);
        btnMenosVida.setContentAreaFilled(false);
        btnMenosVida.setBorderPainted(false);
        btnMenosVida.setForeground(new Color(255, 255, 255, 220));
        btnMenosVida.setPreferredSize(new Dimension(300, 80));
        btnMenosVida.addActionListener(e -> cambiarVidas(-1));
        add(btnMenosVida, BorderLayout.NORTH);

        // 3. ZONA CENTRO: Nombre + Vida gigante
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setOpaque(false);
        
        lblNombre = new JLabel(jugador.getNombre(), SwingConstants.CENTER);
        lblNombre.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setOpaque(true);
        lblNombre.setBackground(new Color(0, 0, 0, 180));
        panelCentro.add(lblNombre, BorderLayout.NORTH);

        lblVidas = new JLabel(String.valueOf(vidas), SwingConstants.CENTER);
        lblVidas.setFont(new Font("Tahoma", Font.BOLD, 100));
        lblVidas.setForeground(Color.WHITE);
        panelCentro.add(lblVidas, BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        // 4. ZONA SUR: Botón Gigante MÁS
        btnMasVida = new JButton("+");
        btnMasVida.setFont(new Font("Tahoma", Font.BOLD, 60));
        btnMasVida.setFocusPainted(false);
        btnMasVida.setOpaque(false);
        btnMasVida.setContentAreaFilled(false);
        btnMasVida.setBorderPainted(false);
        btnMasVida.setForeground(new Color(255, 255, 255, 220));
        btnMasVida.setPreferredSize(new Dimension(300, 80));
        btnMasVida.addActionListener(e -> cambiarVidas(1));
        add(btnMasVida, BorderLayout.SOUTH);

     // 5. PANEL FLOTANTE: Contadores en cuadrícula compacta (estilo app móvil)
        JPanel panelFlotante = new JPanel();
        panelFlotante.setLayout(new GridLayout(3, 2, 5, 5)); // 3 filas x 2 columnas
        panelFlotante.setOpaque(false);
        panelFlotante.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Font fontContador = new Font("Tahoma", Font.BOLD, 11);
        Color fondoContador = new Color(0, 0, 0, 200);

        // --- Fila 1: Daño de Comandante y Veneno ---
        lblCmdte = new JLabel("⚔️ Cmdte: 0", SwingConstants.CENTER);
        lblCmdte.setFont(fontContador);
        lblCmdte.setForeground(Color.WHITE);
        lblCmdte.setOpaque(true);
        lblCmdte.setBackground(fondoContador);
        lblCmdte.setBorder(javax.swing.BorderFactory.createLineBorder(Color.GRAY, 1));
        lblCmdte.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lblVeneno = new JLabel("️ Veneno: 0", SwingConstants.CENTER);
        lblVeneno.setFont(fontContador);
        lblVeneno.setForeground(Color.WHITE);
        lblVeneno.setOpaque(true);
        lblVeneno.setBackground(fondoContador);
        lblVeneno.setBorder(javax.swing.BorderFactory.createLineBorder(Color.GRAY, 1));
        lblVeneno.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        // --- Fila 2: Energía y Tesoro ---
        JLabel lblEnergia = new JLabel("⚡ Energía: 0", SwingConstants.CENTER);
        lblEnergia.setFont(fontContador);
        lblEnergia.setForeground(new Color(0, 255, 255)); // Cian
        lblEnergia.setOpaque(true);
        lblEnergia.setBackground(fondoContador);
        lblEnergia.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 255, 255), 1));
        lblEnergia.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        JLabel lblTesoro = new JLabel("💰 Tesoro: 0", SwingConstants.CENTER);
        lblTesoro.setFont(fontContador);
        lblTesoro.setForeground(new Color(255, 215, 0)); // Dorado
        lblTesoro.setOpaque(true);
        lblTesoro.setBackground(fondoContador);
        lblTesoro.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(255, 215, 0), 1));
        lblTesoro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        // --- Fila 3: Monarca y Conceder ---
        JLabel lblMonarca = new JLabel("👑 Monarca", SwingConstants.CENTER);
        lblMonarca.setFont(fontContador);
        lblMonarca.setForeground(Color.GRAY);
        lblMonarca.setOpaque(true);
        lblMonarca.setBackground(fondoContador);
        lblMonarca.setBorder(javax.swing.BorderFactory.createLineBorder(Color.GRAY, 1));
        lblMonarca.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnConceder = new JButton("Conceder");
        btnConceder.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnConceder.setBackground(new Color(200, 0, 0));
        btnConceder.setForeground(Color.WHITE);
        btnConceder.setFocusPainted(false);

        // Añadir todo al panel en orden (izquierda a derecha, arriba a abajo)
        panelFlotante.add(lblCmdte);
        panelFlotante.add(lblVeneno);
        panelFlotante.add(lblEnergia);
        panelFlotante.add(lblTesoro);
        panelFlotante.add(lblMonarca);
        panelFlotante.add(btnConceder);

        add(panelFlotante, BorderLayout.EAST);

        // 6. LISTENERS para los contadores (clic izq = sumar, clic der = restar)
        lblCmdte.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (eliminado) return;
                if (e.getButton() == MouseEvent.BUTTON1) {
                    dañoComandante++;
                } else if (e.getButton() == MouseEvent.BUTTON3 && dañoComandante > 0) {
                    dañoComandante--;
                }
                lblCmdte.setText("Cmdte: " + dañoComandante);
            }
        });
        
        lblVeneno.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (eliminado) return;
                if (e.getButton() == MouseEvent.BUTTON1) {
                    veneno++;
                } else if (e.getButton() == MouseEvent.BUTTON3 && veneno > 0) {
                    veneno--;
                }
                lblVeneno.setText("Veneno: " + veneno);
            }
        });

        btnConceder.addActionListener(e -> confirmarConcesion());
    }

    // Cargar imagen desde URL
    private void cargarImagenFondo(String urlImagen) {
        if (urlImagen != null && !urlImagen.isEmpty()) {
            try {
                URL url = new URL(urlImagen);
                imagenFondo = ImageIO.read(url);
            } catch (Exception e) {
                System.out.println("No se pudo cargar la imagen de fondo para " + jugador.getNombre());
                imagenFondo = null;
            }
        }
    }

    // Dibujar el fondo con filtro oscuro para mejorar legibilidad
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        if (imagenFondo != null) {
            g2d.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            // Filtro oscuro semitransparente para que el texto resalte
            g2d.setColor(new Color(0, 0, 0, 90));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        } else {
            // Fondo por defecto elegante
            g2d.setColor(new Color(50, 50, 60));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        
        // Si está eliminado, capa negra más opaca
        if (eliminado) {
            g2d.setColor(new Color(0, 0, 0, 200));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void cambiarVidas(int cantidad) {
        if (eliminado) return;
        this.vidas += cantidad;
        lblVidas.setText(String.valueOf(this.vidas));
        
        // Efecto visual: rojo si baja de 20, verde si sube de 40
        if (this.vidas < 20) lblVidas.setForeground(new Color(255, 80, 80));
        else if (this.vidas > 40) lblVidas.setForeground(new Color(100, 255, 100));
        else lblVidas.setForeground(Color.WHITE);
    }

    private void confirmarConcesion() {
        if (eliminado) return;
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Seguro que " + jugador.getNombre() + " concede?", 
                "Confirmar Concesión", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) eliminarJugador();
    }

    private void eliminarJugador() {
        eliminado = true;
        btnMasVida.setEnabled(false);
        btnMenosVida.setEnabled(false);
        btnConceder.setText("ELIMINADO");
        btnConceder.setBackground(Color.DARK_GRAY);
        lblCmdte.setEnabled(false);
        lblVeneno.setEnabled(false);
        repaint();
    }

    public void reiniciarPanel(int vidasIniciales) {
        this.vidas = vidasIniciales;
        this.dañoComandante = 0;
        this.veneno = 0;
        this.eliminado = false;
        
        lblVidas.setText(String.valueOf(vidas));
        lblVidas.setForeground(Color.WHITE);
        lblCmdte.setText("Cmdte: 0");
        lblCmdte.setEnabled(true);
        lblVeneno.setText("Veneno: 0");
        lblVeneno.setEnabled(true);
        
        btnMasVida.setEnabled(true);
        btnMenosVida.setEnabled(true);
        btnConceder.setEnabled(true);
        btnConceder.setText("Conceder");
        btnConceder.setBackground(new Color(200, 0, 0));
        
        repaint();
    }

    public Jugador getJugador() { return jugador; }
    public int getVidas() { return vidas; }
}