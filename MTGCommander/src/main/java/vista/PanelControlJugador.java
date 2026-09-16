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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import modelo.Jugador;

public class PanelControlJugador extends JDialog {

    private static final long serialVersionUID = 1L;
    
    private PanelJugador panelJugador;
    private Jugador jugador;
    private List<Jugador> todosLosJugadores;
    
    // Labels para mostrar los valores actuales
    private JLabel lblVidas;
    private JLabel lblCmdte;
    private JLabel lblVeneno;
    private JLabel lblEnergia;
    private JLabel lblMonarca;
    private JLabel lblFotoGrande;

    public PanelControlJugador(PanelJugador panelJugador, Jugador jugador, List<Jugador> todosLosJugadores) {
        // 1. Constructor vacío: CERO errores de compilación garantizados
        super(); 
        
        // 2. Configuramos las propiedades manualmente
        setModal(true); // Esto hace que bloquee la ventana de atrás (como un popup debe ser)
        setTitle("Control de " + jugador.getNombre());
        setSize(500, 600);
        
        // 3. Esto la centra perfectamente respecto al panel que la llamó
        setLocationRelativeTo(panelJugador); 
        setLayout(new BorderLayout(10, 10));
        
        this.panelJugador = panelJugador;
        this.jugador = jugador;
        this.todosLosJugadores = todosLosJugadores;
        
        // ==========================================
        // 1. ZONA NORTE: Foto grande del comandante
        // ==========================================
        JPanel panelFoto = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelFoto.setOpaque(false);
        
        lblFotoGrande = new JLabel();
        lblFotoGrande.setPreferredSize(new Dimension(200, 200));
        lblFotoGrande.setBorder(javax.swing.BorderFactory.createLineBorder(Color.WHITE, 3));
        lblFotoGrande.setOpaque(true);
        lblFotoGrande.setBackground(Color.BLACK);
        
        String urlImagen = jugador.getComandanteImagenUrl();
        if (urlImagen != null && !urlImagen.isEmpty()) {
            try {
                java.net.URL url = new java.net.URL(urlImagen);
                Image img = javax.imageio.ImageIO.read(url);
                ImageIcon icon = new ImageIcon(img.getScaledInstance(200, 200, Image.SCALE_SMOOTH));
                lblFotoGrande.setIcon(icon);
            } catch (Exception e) {
                lblFotoGrande.setText("Sin imagen");
                lblFotoGrande.setFont(new Font("Arial", Font.BOLD, 20));
                lblFotoGrande.setForeground(Color.WHITE);
                lblFotoGrande.setHorizontalAlignment(SwingConstants.CENTER);
            }
        } else {
            lblFotoGrande.setText("?");
            lblFotoGrande.setFont(new Font("Arial", Font.BOLD, 80));
            lblFotoGrande.setForeground(Color.WHITE);
            lblFotoGrande.setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        panelFoto.add(lblFotoGrande);
        add(panelFoto, BorderLayout.NORTH);
        
        // ==========================================
        // 2. ZONA CENTRO: Contadores organizados
        // ==========================================
        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new GridLayout(4, 1, 10, 10));
        panelCentro.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Fila 1: Vidas
        JPanel panelVidas = new JPanel(new FlowLayout(FlowLayout.CENTER));
        lblVidas = new JLabel("Vidas: " + panelJugador.getVidas());
        lblVidas.setFont(new Font("Arial", Font.BOLD, 24));
        panelVidas.add(lblVidas);
        panelCentro.add(panelVidas);
        
        // Fila 2: Daño de Comandante
        JPanel panelCmdte = new JPanel();
        panelCmdte.setLayout(new BorderLayout());
        lblCmdte = new JLabel("Daño de Comandante: " + panelJugador.getDanioComandante(), SwingConstants.CENTER);
        lblCmdte.setFont(new Font("Arial", Font.BOLD, 16));
        panelCmdte.add(lblCmdte, BorderLayout.NORTH);
        
        JPanel panelBotonesCmdte = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        for (Jugador otroJugador : todosLosJugadores) {
            if (!otroJugador.equals(jugador)) {
                JButton btn = new JButton(otroJugador.getNombre());
                btn.setFont(new Font("Arial", Font.PLAIN, 12));
                btn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        panelJugador.sumarDanioComandante(1, otroJugador);
                        actualizarValores();
                    }
                });
                panelBotonesCmdte.add(btn);
            }
        }
        panelCmdte.add(panelBotonesCmdte, BorderLayout.CENTER);
        panelCentro.add(panelCmdte);
        
        // Fila 3: Veneno
        JPanel panelVeneno = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        lblVeneno = new JLabel("Veneno: " + panelJugador.getVeneno());
        lblVeneno.setFont(new Font("Arial", Font.BOLD, 16));
        JButton btnMenosVeneno = new JButton("-");
        JButton btnMasVeneno = new JButton("+");
        btnMenosVeneno.addActionListener(e -> { panelJugador.restarVeneno(); actualizarValores(); });
        btnMasVeneno.addActionListener(e -> { panelJugador.sumarVeneno(); actualizarValores(); });
        panelVeneno.add(btnMenosVeneno);
        panelVeneno.add(lblVeneno);
        panelVeneno.add(btnMasVeneno);
        panelCentro.add(panelVeneno);
        
        // Fila 4: Energía
        JPanel panelEnergia = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        lblEnergia = new JLabel("Energía: " + panelJugador.getEnergia());
        lblEnergia.setFont(new Font("Arial", Font.BOLD, 16));
        JButton btnMenosEnergia = new JButton("-");
        JButton btnMasEnergia = new JButton("+");
        btnMenosEnergia.addActionListener(e -> { panelJugador.restarEnergia(); actualizarValores(); });
        btnMasEnergia.addActionListener(e -> { panelJugador.sumarEnergia(); actualizarValores(); });
        panelEnergia.add(btnMenosEnergia);
        panelEnergia.add(lblEnergia);
        panelEnergia.add(btnMasEnergia);
        panelCentro.add(panelEnergia);
        
        add(panelCentro, BorderLayout.CENTER);
        
        // ==========================================
        // 3. ZONA SUR: Monarca y Cerrar
        // ==========================================
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        lblMonarca = new JLabel("Monarca: NO");
        lblMonarca.setFont(new Font("Arial", Font.BOLD, 16));
        lblMonarca.setForeground(Color.GRAY);
        panelSur.add(lblMonarca);
        
        JButton btnMonarca = new JButton("Activar/Desactivar Monarca");
        btnMonarca.addActionListener(e -> {
            panelJugador.toggleMonarca();
            actualizarValores();
        });
        panelSur.add(btnMonarca);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        panelSur.add(btnCerrar);
        
        add(panelSur, BorderLayout.SOUTH);
        
        // Inicializar valores
        actualizarValores();
    }
    
    private void actualizarValores() {
        lblVidas.setText("Vidas: " + panelJugador.getVidas());
        lblCmdte.setText("Daño de Comandante: " + panelJugador.getDanioComandante());
        lblVeneno.setText("Veneno: " + panelJugador.getVeneno());
        lblEnergia.setText("Energía: " + panelJugador.getEnergia());
        lblMonarca.setText("Monarca: " + (panelJugador.esMonarca() ? "SÍ" : "NO"));
        
        if (panelJugador.esMonarca()) {
            lblMonarca.setForeground(new Color(255, 215, 0)); // Dorado
        } else {
            lblMonarca.setForeground(Color.GRAY);
        }
    }
}