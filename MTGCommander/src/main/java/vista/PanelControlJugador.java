package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import modelo.Jugador;

public class PanelControlJugador extends JDialog {

    private static final long serialVersionUID = 1L;
    
    private PanelJugador panelJugador;
    private Jugador jugador;
    private List<Jugador> todosLosJugadores;
    
    // Labels para actualizar dinámicamente
    private JLabel lblVidasGrande;
    private JLabel lblDesgloseCmdte;
    private JLabel lblValorVeneno;
    private JLabel lblValorEnergia;
    private JLabel lblEstadoMonarca;
    private JLabel lblFotoGrande;

    public PanelControlJugador(PanelJugador panelJugador, Jugador jugador, List<Jugador> todosLosJugadores) {
        this.panelJugador = panelJugador;
        this.jugador = jugador;
        this.todosLosJugadores = todosLosJugadores;
        
        setTitle("Control de " + jugador.getNombre());
        setSize(500, 600);
        setLocationRelativeTo(panelJugador);
        setResizable(false);
        
        // Usamos un JTabbedPane para organizar la información
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tabbedPane.addTab("Jugador", crearPestanaJugador());
        tabbedPane.addTab("Contadores", crearPestanaContadores());
        
        setLayout(new BorderLayout(0, 0));
        add(tabbedPane, BorderLayout.CENTER);
        
        actualizarValores();
    }

    // ==========================================
    // PESTAÑA 1: JUGADOR (Vida, Daño Cmdte, Monarca)
    // ==========================================
    private JPanel crearPestanaJugador() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setOpaque(false);

        // 1. Zona Superior: Foto + Vida + Nombre
        JPanel panelTop = new JPanel();
        panelTop.setLayout(new BorderLayout(20, 0));
        panelTop.setOpaque(false);
        
        // Foto del jugador
        lblFotoGrande = new JLabel();
        lblFotoGrande.setPreferredSize(new Dimension(130, 130));
        lblFotoGrande.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 85), 2));
        lblFotoGrande.setOpaque(true);
        lblFotoGrande.setBackground(new Color(40, 40, 45));
        
        String urlImagen = jugador.getComandanteImagenUrl();
        if (urlImagen != null && !urlImagen.isEmpty()) {
            try {
                java.net.URL url = new java.net.URL(urlImagen);
                Image img = javax.imageio.ImageIO.read(url);
                ImageIcon icon = new ImageIcon(img.getScaledInstance(130, 130, Image.SCALE_SMOOTH));
                lblFotoGrande.setIcon(icon);
            } catch (Exception e) {
                lblFotoGrande.setText("?");
                lblFotoGrande.setFont(new Font("Segoe UI", Font.BOLD, 40));
                lblFotoGrande.setForeground(Color.WHITE);
                lblFotoGrande.setHorizontalAlignment(SwingConstants.CENTER);
            }
        }
        panelTop.add(lblFotoGrande, BorderLayout.WEST);
        
        // Panel de nombre y vida
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new GridLayout(2, 1, 0, 10));
        panelInfo.setOpaque(false);
        
        JLabel lblNombreGrande = new JLabel(jugador.getNombre(), SwingConstants.LEFT);
        lblNombreGrande.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblNombreGrande.setForeground(Color.WHITE);
        
        lblVidasGrande = new JLabel("40", SwingConstants.LEFT);
        lblVidasGrande.setFont(new Font("Segoe UI", Font.BOLD, 56));
        lblVidasGrande.setForeground(new Color(100, 255, 100));
        
        panelInfo.add(lblNombreGrande);
        panelInfo.add(lblVidasGrande);
        panelTop.add(panelInfo, BorderLayout.CENTER);
        
        panel.add(panelTop, BorderLayout.NORTH);

        // Separador sutil
        JPanel separador = new JPanel();
        separador.setPreferredSize(new Dimension(0, 1));
        separador.setBackground(new Color(60, 60, 65));
        separador.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(separador, BorderLayout.CENTER);

        // 2. Zona Centro: Daño de Comandante
        JPanel panelCmdte = new JPanel(new BorderLayout(0, 15));
        panelCmdte.setOpaque(false);
        
        JLabel lblTituloCmdte = new JLabel("Daño de Comandante", SwingConstants.CENTER);
        lblTituloCmdte.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTituloCmdte.setForeground(new Color(200, 100, 100));
        panelCmdte.add(lblTituloCmdte, BorderLayout.NORTH);
        
        lblDesgloseCmdte = new JLabel("0", SwingConstants.CENTER);
        lblDesgloseCmdte.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDesgloseCmdte.setForeground(new Color(180, 180, 180));
        panelCmdte.add(lblDesgloseCmdte, BorderLayout.CENTER);
        
        // Grid de botones de daño (más compacto)
        int numRivales = todosLosJugadores.size() - 1;
        int columnas = 2;
        int filas = (numRivales + columnas - 1) / columnas;
        
        JPanel gridBotonesCmdte = new JPanel(new GridLayout(filas, columnas, 10, 10));
        gridBotonesCmdte.setOpaque(false);
        
        for (Jugador otroJugador : todosLosJugadores) {
            if (!otroJugador.equals(jugador)) {
                JButton btn = new JButton(otroJugador.getNombre());
                btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
                btn.setBackground(new Color(50, 50, 55));
                btn.setForeground(Color.WHITE);
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setOpaque(true);
                btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                btn.setPreferredSize(new Dimension(0, 40));
                btn.addActionListener(e -> {
                    panelJugador.sumarDanioComandante(1, otroJugador);
                    actualizarValores();
                });
                gridBotonesCmdte.add(btn);
            }
        }
        panelCmdte.add(gridBotonesCmdte, BorderLayout.SOUTH);
        panel.add(panelCmdte, BorderLayout.CENTER);

        // 3. Zona Sur: Monarca
        JPanel panelMonarca = new JPanel();
        panelMonarca.setLayout(new BorderLayout(0, 10));
        panelMonarca.setOpaque(false);
        panelMonarca.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        lblEstadoMonarca = new JLabel("No eres el Monarca", SwingConstants.CENTER);
        lblEstadoMonarca.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblEstadoMonarca.setForeground(Color.GRAY);
        panelMonarca.add(lblEstadoMonarca, BorderLayout.NORTH);
        
        JButton btnMonarca = new JButton("Reclamar / Ceder Monarca");
        btnMonarca.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnMonarca.setBackground(new Color(60, 60, 65));
        btnMonarca.setForeground(Color.WHITE);
        btnMonarca.setFocusPainted(false);
        btnMonarca.setBorderPainted(false);
        btnMonarca.setOpaque(true);
        btnMonarca.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMonarca.setPreferredSize(new Dimension(0, 45));
        btnMonarca.addActionListener(e -> {
            panelJugador.toggleMonarca();
            actualizarValores();
        });
        panelMonarca.add(btnMonarca, BorderLayout.CENTER);
        
        panel.add(panelMonarca, BorderLayout.SOUTH);
        
        return panel;
    }

    // ==========================================
    // PESTAÑA 2: CONTADORES (Veneno, Energía)
    // ==========================================
    private JPanel crearPestanaContadores() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setOpaque(false);
        
        panel.add(crearTarjetaContador(
            "VENENO", 
            new Color(138, 43, 226),
            () -> panelJugador.getVeneno(),
            () -> panelJugador.restarVeneno(),
            () -> panelJugador.sumarVeneno(),
            valor -> lblValorVeneno.setText(String.valueOf(valor))
        ));
        
        panel.add(crearTarjetaContador(
            "ENERGÍA", 
            new Color(0, 191, 255),
            () -> panelJugador.getEnergia(),
            () -> panelJugador.restarEnergia(),
            () -> panelJugador.sumarEnergia(),
            valor -> lblValorEnergia.setText(String.valueOf(valor))
        ));
        
        return panel;
    }

    private JPanel crearTarjetaContador(String titulo, Color colorAcento, 
                                        java.util.function.IntSupplier getValor,
                                        Runnable restar, Runnable sumar, 
                                        java.util.function.IntConsumer actualizarLabel) {
        
        JPanel tarjeta = new JPanel(new BorderLayout(0, 15));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(colorAcento, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        tarjeta.setBackground(new Color(45, 45, 50));
        
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(colorAcento);
        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        
        JLabel lblValor = new JLabel(String.valueOf(getValor.getAsInt()), SwingConstants.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 56));
        lblValor.setForeground(Color.WHITE);
        tarjeta.add(lblValor, BorderLayout.CENTER);
        
        if (titulo.equals("VENENO")) lblValorVeneno = lblValor;
        else if (titulo.equals("ENERGÍA")) lblValorEnergia = lblValor;
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        panelBotones.setOpaque(false);
        
        JButton btnMenos = new JButton("-");
        btnMenos.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btnMenos.setPreferredSize(new Dimension(70, 45));
        btnMenos.setBackground(new Color(60, 60, 65));
        btnMenos.setForeground(Color.WHITE);
        btnMenos.setFocusPainted(false);
        btnMenos.setBorderPainted(false);
        btnMenos.setOpaque(true);
        btnMenos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMenos.addActionListener(e -> {
            restar.run();
            actualizarLabel.accept(getValor.getAsInt());
        });
        
        JButton btnMas = new JButton("+");
        btnMas.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btnMas.setPreferredSize(new Dimension(70, 45));
        btnMas.setBackground(new Color(60, 60, 65));
        btnMas.setForeground(Color.WHITE);
        btnMas.setFocusPainted(false);
        btnMas.setBorderPainted(false);
        btnMas.setOpaque(true);
        btnMas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMas.addActionListener(e -> {
            sumar.run();
            actualizarLabel.accept(getValor.getAsInt());
        });
        
        panelBotones.add(btnMenos);
        panelBotones.add(btnMas);
        tarjeta.add(panelBotones, BorderLayout.SOUTH);
        
        return tarjeta;
    }

    private void actualizarValores() {
        if (lblVidasGrande != null) lblVidasGrande.setText(String.valueOf(panelJugador.getVidas()));
        
        if (lblDesgloseCmdte != null) {
            Map<Jugador, Integer> desglose = panelJugador.getDesgloseDanioComandante();
            if (desglose.isEmpty()) {
                lblDesgloseCmdte.setText("0");
            } else {
                StringBuilder texto = new StringBuilder();
                for (Map.Entry<Jugador, Integer> entry : desglose.entrySet()) {
                    if (texto.length() > 0) texto.append(" | ");
                    texto.append(entry.getKey().getNombre()).append(": ").append(entry.getValue());
                }
                lblDesgloseCmdte.setText(texto.toString());
            }
        }
        
        if (lblValorVeneno != null) lblValorVeneno.setText(String.valueOf(panelJugador.getVeneno()));
        if (lblValorEnergia != null) lblValorEnergia.setText(String.valueOf(panelJugador.getEnergia()));
        
        if (lblEstadoMonarca != null) {
            if (panelJugador.esMonarca()) {
                lblEstadoMonarca.setText("¡Eres el Monarca!");
                lblEstadoMonarca.setForeground(new Color(255, 215, 0));
            } else {
                lblEstadoMonarca.setText("No eres el Monarca");
                lblEstadoMonarca.setForeground(Color.GRAY);
            }
        }
    }
}