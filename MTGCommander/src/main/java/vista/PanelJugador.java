package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
    private int tesoro;
    private boolean esMonarca;
    private boolean eliminado = false;
    private Image imagenFondo;

    // Componentes Visuales
    private JLabel lblVidas;
    private JLabel lblNombre;
    private JLabel lblCmdte;
    private JLabel lblVeneno;
    private JLabel lblEnergia;
    private JLabel lblTesoro;
    private JLabel lblMonarca;
    private JLabel lblFotoComandante;
    
    private JButton btnMasVida;
    private JButton btnMenosVida;
    private JButton btnConceder;

    public PanelJugador(Jugador jugador, int vidasIniciales) {
        this.jugador = jugador;
        this.vidas = vidasIniciales;
        this.dañoComandante = 0;
        this.veneno = 0;
        this.energia = 0;
        this.tesoro = 0;
        this.esMonarca = false;

        // Configuración base
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 500));
        setOpaque(false); // Importante para que se vea el fondo pintado
        
        // 1. Cargar imagen de fondo
        cargarImagenFondo(jugador.getComandanteImagenUrl());

        // 2. ZONA OESTE: Botón Gigante MENOS
        btnMenosVida = new JButton("-");
        btnMenosVida.setFont(new Font("Arial", Font.BOLD, 80));
        btnMenosVida.setForeground(Color.WHITE);
        btnMenosVida.setBackground(new Color(0, 0, 0, 100)); // Negro semitransparente
        btnMenosVida.setOpaque(true);
        btnMenosVida.setFocusPainted(false);
        btnMenosVida.setBorderPainted(false);
        btnMenosVida.setPreferredSize(new Dimension(80, 500));
        btnMenosVida.addActionListener(e -> cambiarVidas(-1));
        add(btnMenosVida, BorderLayout.WEST);

        // 3. ZONA ESTE: Botón Gigante MÁS
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

        // 4. ZONA CENTRO: Número de vida GIGANTE
        lblVidas = new JLabel(String.valueOf(vidas), SwingConstants.CENTER);
        lblVidas.setFont(new Font("Arial", Font.BOLD, 160));
        lblVidas.setForeground(Color.WHITE);
        lblVidas.setOpaque(false);
        add(lblVidas, BorderLayout.CENTER);

        // 5. ZONA SUR: Foto del comandante + nombre
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
            // Imagen por defecto si no hay comandante
            lblFotoComandante.setText("?");
            lblFotoComandante.setFont(new Font("Arial", Font.BOLD, 40));
            lblFotoComandante.setForeground(Color.WHITE);
            lblFotoComandante.setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        lblFotoComandante.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblFotoComandante.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(PanelJugador.this, 
                    "Aqui se abrira el panel de control con todos los contadores.\n\n" +
                    "Jugador: " + jugador.getNombre() + "\n" +
                    "Comandante: " + (jugador.getComandanteNombre() != null ? jugador.getComandanteNombre() : "Sin asignar"),
                    "Panel de Control", 
                    JOptionPane.INFORMATION_MESSAGE);
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
        
        add(panelInferior, BorderLayout.SOUTH);

        // 6. PANEL DE CONTADORES (arriba, en cuadrícula 3x2)
        JPanel panelContadores = new JPanel();
        panelContadores.setLayout(new GridLayout(3, 2, 5, 5));
        panelContadores.setOpaque(false);
        panelContadores.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        Font fontContador = new Font("Arial", Font.BOLD, 12);
        Color fondoContador = new Color(0, 0, 0, 200);
        
        lblCmdte = new JLabel("Cmdte: 0", SwingConstants.CENTER);
        lblCmdte.setFont(fontContador);
        lblCmdte.setForeground(Color.WHITE);
        lblCmdte.setOpaque(true);
        lblCmdte.setBackground(fondoContador);
        lblCmdte.setBorder(javax.swing.BorderFactory.createLineBorder(Color.GRAY, 1));
        lblCmdte.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        lblVeneno = new JLabel("Veneno: 0", SwingConstants.CENTER);
        lblVeneno.setFont(fontContador);
        lblVeneno.setForeground(Color.WHITE);
        lblVeneno.setOpaque(true);
        lblVeneno.setBackground(fondoContador);
        lblVeneno.setBorder(javax.swing.BorderFactory.createLineBorder(Color.GRAY, 1));
        lblVeneno.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        lblEnergia = new JLabel("Energia: 0", SwingConstants.CENTER);
        lblEnergia.setFont(fontContador);
        lblEnergia.setForeground(new Color(0, 255, 255));
        lblEnergia.setOpaque(true);
        lblEnergia.setBackground(fondoContador);
        lblEnergia.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 255, 255), 1));
        lblEnergia.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        lblTesoro = new JLabel("Tesoro: 0", SwingConstants.CENTER);
        lblTesoro.setFont(fontContador);
        lblTesoro.setForeground(new Color(255, 215, 0));
        lblTesoro.setOpaque(true);
        lblTesoro.setBackground(fondoContador);
        lblTesoro.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(255, 215, 0), 1));
        lblTesoro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        lblMonarca = new JLabel("Monarca", SwingConstants.CENTER);
        lblMonarca.setFont(fontContador);
        lblMonarca.setForeground(Color.GRAY);
        lblMonarca.setOpaque(true);
        lblMonarca.setBackground(fondoContador);
        lblMonarca.setBorder(javax.swing.BorderFactory.createLineBorder(Color.GRAY, 1));
        lblMonarca.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        btnConceder = new JButton("Conceder");
        btnConceder.setFont(new Font("Arial", Font.BOLD, 12));
        btnConceder.setBackground(new Color(200, 0, 0));
        btnConceder.setForeground(Color.WHITE);
        btnConceder.setFocusPainted(false);
        
        panelContadores.add(lblCmdte);
        panelContadores.add(lblVeneno);
        panelContadores.add(lblEnergia);
        panelContadores.add(lblTesoro);
        panelContadores.add(lblMonarca);
        panelContadores.add(btnConceder);
        
        add(panelContadores, BorderLayout.NORTH);

        // 7. LISTENERS para los contadores
        lblCmdte.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (eliminado) return;
                if (e.getButton() == MouseEvent.BUTTON1) dañoComandante++;
                else if (e.getButton() == MouseEvent.BUTTON3 && dañoComandante > 0) dañoComandante--;
                lblCmdte.setText("Cmdte: " + dañoComandante);
            }
        });
        
        lblVeneno.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (eliminado) return;
                if (e.getButton() == MouseEvent.BUTTON1) veneno++;
                else if (e.getButton() == MouseEvent.BUTTON3 && veneno > 0) veneno--;
                lblVeneno.setText("Veneno: " + veneno);
            }
        });
        
        lblEnergia.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (eliminado) return;
                if (e.getButton() == MouseEvent.BUTTON1) energia++;
                else if (e.getButton() == MouseEvent.BUTTON3 && energia > 0) energia--;
                lblEnergia.setText("Energia: " + energia);
            }
        });
        
        lblTesoro.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (eliminado) return;
                if (e.getButton() == MouseEvent.BUTTON1) tesoro++;
                else if (e.getButton() == MouseEvent.BUTTON3 && tesoro > 0) tesoro--;
                lblTesoro.setText("Tesoro: " + tesoro);
            }
        });
        
        lblMonarca.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (eliminado) return;
                esMonarca = !esMonarca;
                if (esMonarca) {
                    lblMonarca.setText("Monarca: SI");
                    lblMonarca.setForeground(new Color(255, 215, 0));
                    lblMonarca.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(255, 215, 0), 2));
                } else {
                    lblMonarca.setText("Monarca");
                    lblMonarca.setForeground(Color.GRAY);
                    lblMonarca.setBorder(javax.swing.BorderFactory.createLineBorder(Color.GRAY, 1));
                }
            }
        });

        btnConceder.addActionListener(e -> confirmarConcesion());
    }

    private void cargarImagenFondo(String urlImagen) {
        if (urlImagen != null && !urlImagen.isEmpty()) {
            try {
                URL url = new URL(urlImagen);
                imagenFondo = ImageIO.read(url);
                System.out.println("Imagen cargada para " + jugador.getNombre() + ": " + urlImagen);
            } catch (Exception e) {
                System.out.println("No se pudo cargar la imagen de fondo para " + jugador.getNombre() + ": " + e.getMessage());
                imagenFondo = null;
            }
        } else {
            System.out.println("Sin imagen para " + jugador.getNombre());
            imagenFondo = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        if (imagenFondo != null) {
            g2d.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            // Filtro oscuro para que el texto resalte
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        } else {
            // Fondo por defecto
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
        lblCmdte.setEnabled(false);
        lblVeneno.setEnabled(false);
        lblEnergia.setEnabled(false);
        lblTesoro.setEnabled(false);
        lblMonarca.setEnabled(false);
        repaint();
    }

    public void reiniciarPanel(int vidasIniciales) {
        this.vidas = vidasIniciales;
        this.dañoComandante = 0;
        this.veneno = 0;
        this.energia = 0;
        this.tesoro = 0;
        this.esMonarca = false;
        this.eliminado = false;
        
        lblVidas.setText(String.valueOf(vidas));
        lblVidas.setForeground(Color.WHITE);
        lblCmdte.setText("Cmdte: 0");
        lblCmdte.setEnabled(true);
        lblVeneno.setText("Veneno: 0");
        lblVeneno.setEnabled(true);
        lblEnergia.setText("Energia: 0");
        lblEnergia.setEnabled(true);
        lblTesoro.setText("Tesoro: 0");
        lblTesoro.setEnabled(true);
        lblMonarca.setText("Monarca");
        lblMonarca.setForeground(Color.GRAY);
        lblMonarca.setBorder(javax.swing.BorderFactory.createLineBorder(Color.GRAY, 1));
        lblMonarca.setEnabled(true);
        
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