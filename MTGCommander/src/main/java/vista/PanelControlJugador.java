package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        setSize(550, 650); // Un poco más ancho y alto
        setLocationRelativeTo(panelJugador);
        
        // Usamos un JTabbedPane para organizar la información
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabbedPane.addTab("👤 Jugador", crearPestanaJugador());
        tabbedPane.addTab("🎲 Contadores", crearPestanaContadores());
        
        setLayout(new BorderLayout(10, 10));
        add(tabbedPane, BorderLayout.CENTER);
        
        // Botón de cerrar abajo del todo
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        panelSur.setOpaque(false);
        JButton btnCerrar = new JButton("Cerrar Panel");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCerrar.setPreferredSize(new Dimension(200, 45));
        btnCerrar.addActionListener(e -> dispose());
        panelSur.add(btnCerrar);
        
        add(panelSur, BorderLayout.SOUTH);
        
        actualizarValores();
    }

    // ==========================================
    // PESTAÑA 1: JUGADOR (Vida, Daño Cmdte, Monarca)
    // ==========================================
    private JPanel crearPestanaJugador() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false);

        // 1. Zona Superior: Foto + Vida
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelTop.setOpaque(false);
        
        lblFotoGrande = new JLabel();
        lblFotoGrande.setPreferredSize(new Dimension(150, 150));
        lblFotoGrande.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 3)); // Borde dorado
        lblFotoGrande.setOpaque(true);
        lblFotoGrande.setBackground(new Color(30, 30, 35));
        
        String urlImagen = jugador.getComandanteImagenUrl();
        if (urlImagen != null && !urlImagen.isEmpty()) {
            try {
                java.net.URL url = new java.net.URL(urlImagen);
                Image img = javax.imageio.ImageIO.read(url);
                ImageIcon icon = new ImageIcon(img.getScaledInstance(150, 150, Image.SCALE_SMOOTH));
                lblFotoGrande.setIcon(icon);
            } catch (Exception e) {
                lblFotoGrande.setText("?");
                lblFotoGrande.setFont(new Font("Segoe UI", Font.BOLD, 40));
                lblFotoGrande.setForeground(Color.WHITE);
                lblFotoGrande.setHorizontalAlignment(SwingConstants.CENTER);
            }
        }
        panelTop.add(lblFotoGrande);
        
        JPanel panelVida = new JPanel(new GridLayout(2, 1, 0, 5));
        panelVida.setOpaque(false);
        JLabel lblNombreGrande = new JLabel(jugador.getNombre(), SwingConstants.CENTER);
        lblNombreGrande.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblNombreGrande.setForeground(Color.WHITE);
        
        lblVidasGrande = new JLabel("40", SwingConstants.CENTER);
        lblVidasGrande.setFont(new Font("Segoe UI", Font.BOLD, 64));
        lblVidasGrande.setForeground(new Color(100, 255, 100)); // Verde neón
        
        panelVida.add(lblNombreGrande);
        panelVida.add(lblVidasGrande);
        panelTop.add(panelVida);
        panel.add(panelTop, BorderLayout.NORTH);

        // 2. Zona Centro: Daño de Comandante
        JPanel panelCmdte = new JPanel(new BorderLayout(0, 10));
        panelCmdte.setOpaque(false);
        
        lblDesgloseCmdte = new JLabel("Daño de Comandante: 0", SwingConstants.CENTER);
        lblDesgloseCmdte.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDesgloseCmdte.setForeground(new Color(200, 100, 100)); // Rojo suave
        panelCmdte.add(lblDesgloseCmdte, BorderLayout.NORTH);
        
        JPanel gridBotonesCmdte = new JPanel(new GridLayout(0, 2, 10, 10)); // 2 columnas
        gridBotonesCmdte.setOpaque(false);
        
        for (Jugador otroJugador : todosLosJugadores) {
            if (!otroJugador.equals(jugador)) {
                JButton btn = new JButton("⚔️ " + otroJugador.getNombre());
                btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                btn.setFocusPainted(false);
                btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                btn.addActionListener(e -> {
                    panelJugador.sumarDanioComandante(1, otroJugador);
                    actualizarValores();
                });
                gridBotonesCmdte.add(btn);
            }
        }
        panelCmdte.add(gridBotonesCmdte, BorderLayout.CENTER);
        panel.add(panelCmdte, BorderLayout.CENTER);

        // 3. Zona Sur: Monarca
        JPanel panelMonarca = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        panelMonarca.setOpaque(false);
        
        lblEstadoMonarca = new JLabel("NO ERES EL MONARCA");
        lblEstadoMonarca.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblEstadoMonarca.setForeground(Color.GRAY);
        panelMonarca.add(lblEstadoMonarca);
        
        JButton btnMonarca = new JButton("👑 Reclamar / Ceder Monarca");
        btnMonarca.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnMonarca.setPreferredSize(new Dimension(250, 40));
        btnMonarca.setFocusPainted(false);
        btnMonarca.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMonarca.addActionListener(e -> {
            panelJugador.toggleMonarca();
            actualizarValores();
        });
        panelMonarca.add(btnMonarca);
        
        panel.add(panelMonarca, BorderLayout.SOUTH);
        
        return panel;
    }

    // ==========================================
    // PESTAÑA 2: CONTADORES (Veneno, Energía)
    // ==========================================
    private JPanel crearPestanaContadores() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setOpaque(false);
        
        panel.add(crearTarjetaContador(
            "☠️ VENENO", 
            new Color(138, 43, 226), // Morado
            () -> panelJugador.getVeneno(),
            () -> panelJugador.restarVeneno(),
            () -> panelJugador.sumarVeneno(),
            valor -> lblValorVeneno.setText(String.valueOf(valor))
        ));
        
        panel.add(crearTarjetaContador(
            "⚡ ENERGÍA", 
            new Color(0, 191, 255), // Azul cian
            () -> panelJugador.getEnergia(),
            () -> panelJugador.restarEnergia(),
            () -> panelJugador.sumarEnergia(),
            valor -> lblValorEnergia.setText(String.valueOf(valor))
        ));
        
        return panel;
    }

    // Método auxiliar para crear tarjetas de contadores bonitas
    private JPanel crearTarjetaContador(String titulo, Color colorAcento, 
                                        java.util.function.IntSupplier getValor,
                                        Runnable restar, Runnable sumar, 
                                        java.util.function.IntConsumer actualizarLabel) {
        
        JPanel tarjeta = new JPanel(new BorderLayout(10, 10));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(colorAcento, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        tarjeta.setBackground(new Color(40, 40, 45));
        
        // Título
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(colorAcento);
        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        
        // Valor central
        JLabel lblValor = new JLabel(String.valueOf(getValor.getAsInt()), SwingConstants.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 56));
        lblValor.setForeground(Color.WHITE);
        tarjeta.add(lblValor, BorderLayout.CENTER);
        
        // Guardamos la referencia para actualizarla después
        if (titulo.contains("VENENO")) lblValorVeneno = lblValor;
        else if (titulo.contains("ENERGÍA")) lblValorEnergia = lblValor;
        
        // Botones + y -
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setOpaque(false);
        
        JButton btnMenos = new JButton("−");
        btnMenos.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btnMenos.setPreferredSize(new Dimension(60, 40));
        btnMenos.setFocusPainted(false);
        btnMenos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMenos.addActionListener(e -> {
            restar.run();
            actualizarLabel.accept(getValor.getAsInt());
        });
        
        JButton btnMas = new JButton("+");
        btnMas.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btnMas.setPreferredSize(new Dimension(60, 40));
        btnMas.setFocusPainted(false);
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

    // ==========================================
    // ACTUALIZACIÓN DE VALORES
    // ==========================================
    private void actualizarValores() {
        if (lblVidasGrande != null) lblVidasGrande.setText(String.valueOf(panelJugador.getVidas()));
        
        if (lblDesgloseCmdte != null) {
            Map<Jugador, Integer> desglose = panelJugador.getDesgloseDanioComandante();
            if (desglose.isEmpty()) {
                lblDesgloseCmdte.setText("Daño de Comandante: 0");
            } else {
                StringBuilder texto = new StringBuilder("Daño Cmdte: ");
                for (Map.Entry<Jugador, Integer> entry : desglose.entrySet()) {
                    texto.append(entry.getKey().getNombre()).append(": ")
                         .append(entry.getValue()).append(" | ");
                }
                String textoFinal = texto.toString();
                if (textoFinal.endsWith("| ")) {
                    textoFinal = textoFinal.substring(0, textoFinal.length() - 2);
                }
                lblDesgloseCmdte.setText(textoFinal);
            }
        }
        
        if (lblValorVeneno != null) lblValorVeneno.setText(String.valueOf(panelJugador.getVeneno()));
        if (lblValorEnergia != null) lblValorEnergia.setText(String.valueOf(panelJugador.getEnergia()));
        
        if (lblEstadoMonarca != null) {
            if (panelJugador.esMonarca()) {
                lblEstadoMonarca.setText("¡ERES EL MONARCA!");
                lblEstadoMonarca.setForeground(new Color(255, 215, 0)); // Dorado
            } else {
                lblEstadoMonarca.setText("NO ERES EL MONARCA");
                lblEstadoMonarca.setForeground(Color.GRAY);
            }
        }
    }
}