package vista;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;
import modelo.Jugador;

public class PanelControlJugador extends JDialog {

    private static final long serialVersionUID = 1L;

    private PanelJugador panelJugador;
    private Jugador jugador;
    private List<Jugador> todosLosJugadores;

    // Labels para actualizar dinámicamente
    private JLabel lblVidasGrande;
    private JLabel lblDesgloseCmdte;
    private JLabel lblFotoGrande;

    // 🟢 En vez de un campo por cada contador numérico, guardamos aquí
    // cada JLabel junto con la función que sabe leer su valor actual.
    // actualizarValores() recorre este mapa y refresca todos los textos
    // de una sola vez, sin importar cuántas tarjetas numéricas haya.
    private final Map<JLabel, Supplier<Integer>> etiquetasNumericas = new LinkedHashMap<>();
    private JPanel panelGridContadores;

    // Paleta de colores unificada
    private static final Color COLOR_ROJO_BRILLANTE = new Color(220, 60, 60);
    private static final Color COLOR_GRIS_OSCURO = new Color(50, 50, 55);
    private static final Color COLOR_GRIS_MEDIO = new Color(70, 70, 75);
    private static final Color COLOR_FONDO_TARJETA = new Color(45, 45, 50);

    public PanelControlJugador(PanelJugador panelJugador, Jugador jugador, List<Jugador> todosLosJugadores) {
        super((java.awt.Frame) null, "Control de " + jugador.getNombre(), true);

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.panelJugador = panelJugador;
        this.jugador = jugador;
        this.todosLosJugadores = todosLosJugadores;

        setTitle("Control de " + jugador.getNombre());
        setLocationRelativeTo(panelJugador);
        setResizable(false);

        JPanel contentPane = new JPanel(new BorderLayout(0, 15));
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);

        // ==========================================
        // 1. ZONA NORTE: Título + Línea decorativa
        // ==========================================
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setOpaque(false);
        panelNorte.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel lblTitulo = new JLabel("CONTROL DE JUGADOR", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(COLOR_ROJO_BRILLANTE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNorte.add(lblTitulo);

        panelNorte.add(Box.createVerticalStrut(8));

        JPanel lineaDecorativa = new JPanel();
        lineaDecorativa.setPreferredSize(new Dimension(1, 2));
        lineaDecorativa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        lineaDecorativa.setBackground(new Color(150, 35, 35));
        lineaDecorativa.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNorte.add(lineaDecorativa);

        contentPane.add(panelNorte, BorderLayout.NORTH);

        // ==========================================
        // 2. ZONA CENTRO: Pestañas
        // ==========================================
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel panelTabJugador = crearPestanaJugador();
        JPanel panelTabContadores = crearPestanaContadores();

        tabbedPane.addTab("Jugador", panelTabJugador);
        tabbedPane.addTab("Contadores", panelTabContadores);

        // 🟢 Forzamos que la pestaña reserve espacio para la más alta de
        // las dos. Sin esto, pack() puede dimensionar el diálogo según la
        // pestaña más pequeña y recortar la otra al cambiar de pestaña.
        int anchoTabs = Math.max(panelTabJugador.getPreferredSize().width, panelTabContadores.getPreferredSize().width);
        int altoTabs = Math.max(panelTabJugador.getPreferredSize().height, panelTabContadores.getPreferredSize().height);
        tabbedPane.setPreferredSize(new Dimension(anchoTabs, altoTabs));

        contentPane.add(tabbedPane, BorderLayout.CENTER);

        // ==========================================
        // 3. ZONA SUR: Botón de cierre
        // ==========================================
        JButton btnCerrar = new BotonRedondeado("Cerrar", COLOR_GRIS_OSCURO, new Color(65, 65, 70));
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setPreferredSize(new Dimension(0, 42));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> dispose());

        JPanel panelCerrar = new JPanel(new BorderLayout());
        panelCerrar.setOpaque(false);
        panelCerrar.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        panelCerrar.add(btnCerrar, BorderLayout.CENTER);
        contentPane.add(panelCerrar, BorderLayout.SOUTH);

        actualizarValores();

        // 🟢 pack() en vez de un setSize fijo: con la rejilla nueva de
        // contadores el contenido cambia de tamaño, y así la ventana
        // siempre se ajusta exactamente a lo que necesita.
        pack();
        setLocationRelativeTo(panelJugador);
    }

    // ==========================================
    // PESTAÑA 1: JUGADOR (Vida, Daño Cmdte, Monarca)
    // ==========================================
    private JPanel crearPestanaJugador() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        // 1. Zona Superior: Foto (AVATAR redondeado) + Vida + Nombre
        JPanel panelTop = new JPanel();
        panelTop.setLayout(new BorderLayout(15, 0));
        panelTop.setOpaque(false);

        lblFotoGrande = new JLabel();
        lblFotoGrande.setPreferredSize(new Dimension(110, 110));
        lblFotoGrande.setOpaque(false);

        String avatarPath = jugador.getAvatarPath();
        boolean avatarCargado = false;

        if (avatarPath != null && !avatarPath.isEmpty()) {
            File archivoAvatar = new File(avatarPath);
            if (archivoAvatar.exists()) {
                try {
                    Image imgAvatar = javax.imageio.ImageIO.read(archivoAvatar);
                    ImageIcon iconAvatar = recortarImagenRedondeada(imgAvatar, 110, 110, 15);
                    lblFotoGrande.setIcon(iconAvatar);
                    avatarCargado = true;
                } catch (Exception e) {
                    System.err.println("Error al cargar avatar: " + e.getMessage());
                }
            }
        }

        if (!avatarCargado) {
            String urlImagen = jugador.getComandanteImagenUrl();
            if (urlImagen != null && !urlImagen.isEmpty()) {
                try {
                    java.net.URL url = new java.net.URL(urlImagen);
                    Image img = javax.imageio.ImageIO.read(url);
                    ImageIcon icon = new ImageIcon(img.getScaledInstance(110, 110, Image.SCALE_SMOOTH));
                    lblFotoGrande.setIcon(icon);
                } catch (Exception e) {
                    lblFotoGrande.setText("?");
                    lblFotoGrande.setFont(new Font("Segoe UI", Font.BOLD, 40));
                    lblFotoGrande.setForeground(COLOR_GRIS_MEDIO);
                    lblFotoGrande.setHorizontalAlignment(SwingConstants.CENTER);
                }
            } else {
                lblFotoGrande.setText("?");
                lblFotoGrande.setFont(new Font("Segoe UI", Font.BOLD, 40));
                lblFotoGrande.setForeground(COLOR_GRIS_MEDIO);
                lblFotoGrande.setHorizontalAlignment(SwingConstants.CENTER);
            }
        }
        panelTop.add(lblFotoGrande, BorderLayout.WEST);

        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new GridLayout(2, 1, 0, 5));
        panelInfo.setOpaque(false);

        JLabel lblNombreGrande = new JLabel(jugador.getNombre(), SwingConstants.LEFT);
        lblNombreGrande.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblNombreGrande.setForeground(Color.WHITE);

        lblVidasGrande = new JLabel("40", SwingConstants.LEFT);
        lblVidasGrande.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblVidasGrande.setForeground(new Color(100, 255, 100));

        panelInfo.add(lblNombreGrande);
        panelInfo.add(lblVidasGrande);
        panelTop.add(panelInfo, BorderLayout.CENTER);

        panel.add(panelTop, BorderLayout.NORTH);

        // 2. Zona Centro: Daño de Comandante
        JPanel panelCmdte = new JPanel(new BorderLayout(0, 10));
        panelCmdte.setOpaque(false);

        JLabel lblTituloCmdte = new JLabel("Daño de Comandante Recibido", SwingConstants.CENTER);
        lblTituloCmdte.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTituloCmdte.setForeground(COLOR_ROJO_BRILLANTE);
        panelCmdte.add(lblTituloCmdte, BorderLayout.NORTH);

        lblDesgloseCmdte = new JLabel("Total: 0", SwingConstants.CENTER);
        lblDesgloseCmdte.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDesgloseCmdte.setForeground(Color.WHITE);
        panelCmdte.add(lblDesgloseCmdte, BorderLayout.CENTER);

        int numRivales = todosLosJugadores.size() - 1;
        // 🟢 Columnas adaptativas según cuántos rivales haya: con 1 rival
        // (1v1) sale un único botón grande y centrado; con 2-4, dos
        // columnas; con 5 o más, tres, para no alargar demasiado el panel.
        int columnas = numRivales <= 1 ? 1 : (numRivales <= 4 ? 2 : 3);
        int filas = (numRivales + columnas - 1) / columnas;

        JPanel gridBotonesCmdte = new JPanel(new GridLayout(filas, columnas, 10, 10));
        gridBotonesCmdte.setOpaque(false);

        for (Jugador otroJugador : todosLosJugadores) {
            if (!otroJugador.equals(jugador)) {
                JButton btn = new BotonRedondeado(otroJugador.getNombre(), COLOR_GRIS_OSCURO, new Color(65, 65, 70));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
                btn.setPreferredSize(new Dimension(0, 40));
                btn.setForeground(Color.WHITE);
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                // 🟢 Clic izquierdo: +1. Clic derecho: −1, para poder
                // corregir un clic de más sin quedar atrapado (sin esto,
                // pasarse de 21 dejaba el contador de ese rival
                // permanentemente por encima del umbral de derrota).
                btn.setToolTipText("Clic: +1 daño   ·   Clic derecho: −1 (corregir)");
                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            panelJugador.sumarDanioComandante(1, otroJugador);
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            panelJugador.restarDanioComandante(1, otroJugador);
                        }
                        actualizarValores();
                    }
                });
                gridBotonesCmdte.add(btn);
            }
        }

        // 🟢 Con muchos jugadores en la mesa esta rejilla puede crecer
        // bastante; va dentro de un scroll con techo de altura para que
        // el diálogo nunca se dispare de tamaño.
        JScrollPane scrollCmdte = new JScrollPane(gridBotonesCmdte);
        scrollCmdte.setOpaque(false);
        scrollCmdte.getViewport().setOpaque(false);
        scrollCmdte.setBorder(BorderFactory.createEmptyBorder());
        scrollCmdte.getVerticalScrollBar().setUnitIncrement(16);
        int altoDeseado = Math.min(filas * 50 + (filas - 1) * 10, 220);
        scrollCmdte.setPreferredSize(new Dimension(0, altoDeseado));

        panelCmdte.add(scrollCmdte, BorderLayout.SOUTH);
        panel.add(panelCmdte, BorderLayout.CENTER);

        // 🟢 El Monarca ya no vive aquí: ahora es una tarjeta más en la
        // pestaña "Contadores", junto al resto de interruptores.

        return panel;
    }

    // ==========================================
    // PESTAÑA 2: CONTADORES — rejilla de tarjetas
    // ==========================================
    private JPanel crearPestanaContadores() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);
        contenedor.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        panelGridContadores = new JPanel(new GridLayout(0, 2, 10, 10));
        panelGridContadores.setOpaque(false);

        // --- Interruptor YA EXISTENTE (toggleMonarca()/esMonarca() en PanelJugador) ---
        panelGridContadores.add(crearTarjetaToggle("👑", "Monarca", new Color(255, 215, 0),
                panelJugador::esMonarca, panelJugador::toggleMonarca));

        // --- Numéricos (ya existentes en PanelJugador) ---
        panelGridContadores.add(crearTarjetaNumerica("☠", "Veneno", new Color(138, 43, 226),
                panelJugador::getVeneno, panelJugador::sumarVeneno, panelJugador::restarVeneno));
        panelGridContadores.add(crearTarjetaNumerica("🔋", "Energía", new Color(0, 191, 255),
                panelJugador::getEnergia, panelJugador::sumarEnergia, panelJugador::restarEnergia));

        // --- Numéricos NUEVOS: requieren sumarX()/restarX()/getX() en PanelJugador ---
        panelGridContadores.add(crearTarjetaNumerica("💰", "Tesoro", new Color(218, 165, 32),
                panelJugador::getTesoro, panelJugador::sumarTesoro, panelJugador::restarTesoro));
        panelGridContadores.add(crearTarjetaNumerica("☢", "Rad", new Color(154, 205, 50),
                panelJugador::getRad, panelJugador::sumarRad, panelJugador::restarRad));
        panelGridContadores.add(crearTarjetaNumerica("💲", "C.Tax", new Color(255, 140, 0),
                panelJugador::getImpuestoComandante, panelJugador::sumarImpuestoComandante, panelJugador::restarImpuestoComandante));
        panelGridContadores.add(crearTarjetaNumerica("⭐", "Exp", new Color(255, 215, 0),
                panelJugador::getExperiencia, panelJugador::sumarExperiencia, panelJugador::restarExperiencia));

        // --- Interruptores NUEVOS: requieren toggleX()/esX() o tieneX() en PanelJugador ---
        panelGridContadores.add(crearTarjetaToggle("⚡", "Iniciativa", new Color(255, 193, 7),
                panelJugador::tieneIniciativa, panelJugador::toggleIniciativa));
        panelGridContadores.add(crearTarjetaToggle("✨", "Ascenso", new Color(186, 104, 200),
                panelJugador::tieneAscenso, panelJugador::toggleAscenso));
        panelGridContadores.add(crearTarjetaToggle("🌗", "Día/Noche", new Color(92, 107, 192),
                panelJugador::esNoche, panelJugador::toggleDiaNoche));
        panelGridContadores.add(crearTarjetaToggle("✖", "K.O.", new Color(229, 57, 53),
                panelJugador::estaKO, panelJugador::toggleKO));

        JScrollPane scroll = new JScrollPane(panelGridContadores);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setPreferredSize(new Dimension(420, 380));

        contenedor.add(scroll, BorderLayout.CENTER);
        return contenedor;
    }

    /**
     * Tarjeta compacta para un contador NUMÉRICO. Clic izquierdo: +1.
     * Clic derecho: −1. El valor se guarda en {@link #etiquetasNumericas}
     * para que actualizarValores() lo refresque automáticamente.
     */
    private JPanel crearTarjetaNumerica(String icono, String titulo, Color colorAcento,
            Supplier<Integer> obtenerValor, Runnable incrementar, Runnable decrementar) {

        JPanel tarjeta = new JPanel(new BorderLayout(0, 2)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_FONDO_TARJETA);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.setColor(colorAcento);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        tarjeta.setPreferredSize(new Dimension(0, 100));
        tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tarjeta.setToolTipText("Clic: +1   ·   Clic derecho: −1");

        // 🟢 Icono y título en etiquetas separadas: el icono necesita la
        // fuente "Segoe UI Emoji" para dibujarse (si no, sale un cuadro
        // vacío), y esa fuente no tiene por qué tener buen soporte para
        // el texto en negrita normal, así que separamos cada uno con su
        // propia fuente.
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panelTitulo.setOpaque(false);

        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        panelTitulo.add(lblIcono);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(colorAcento);
        panelTitulo.add(lblTitulo);

        tarjeta.add(panelTitulo, BorderLayout.NORTH);

        JLabel lblValor = new JLabel(String.valueOf(obtenerValor.get()), SwingConstants.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValor.setForeground(Color.WHITE);
        tarjeta.add(lblValor, BorderLayout.CENTER);

        etiquetasNumericas.put(lblValor, obtenerValor);

        tarjeta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    incrementar.run();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    decrementar.run();
                }
                actualizarValores();
            }
        });

        return tarjeta;
    }

    /**
     * Tarjeta compacta para un estado ON/OFF (Iniciativa, Ascenso,
     * Día/Noche, K.O.). Un clic alterna el estado; el color de la tarjeta
     * se recalcula en cada repintado leyendo directamente el estado
     * actual, así que no necesita mantenerse sincronizada a mano.
     */
    private JPanel crearTarjetaToggle(String icono, String titulo, Color colorAcento,
            Supplier<Boolean> obtenerEstado, Runnable alternar) {

        JPanel tarjeta = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                boolean activo = obtenerEstado.get();
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(activo ? colorAcento.darker().darker() : COLOR_FONDO_TARJETA);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.setColor(activo ? colorAcento : COLOR_GRIS_MEDIO);
                g2.setStroke(new BasicStroke(activo ? 2.5f : 1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        tarjeta.setPreferredSize(new Dimension(0, 100));
        tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tarjeta.setToolTipText("Clic para activar / desactivar");

        // 🟢 Misma corrección: el icono necesita la fuente con emoji.
        JLabel lblIcono = new JLabel(icono, SwingConstants.CENTER);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        tarjeta.add(lblIcono, BorderLayout.CENTER);

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(Color.WHITE);
        tarjeta.add(lblTitulo, BorderLayout.SOUTH);

        tarjeta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                alternar.run();
                actualizarValores();
            }
        });

        return tarjeta;
    }

    private void actualizarValores() {
        if (lblVidasGrande != null) {
            int vidas = panelJugador.getVidas();
            lblVidasGrande.setText(String.valueOf(vidas));
            if (vidas < 20) lblVidasGrande.setForeground(new Color(255, 80, 80));
            else lblVidasGrande.setForeground(new Color(100, 255, 100));
        }

        if (lblDesgloseCmdte != null) {
            Map<Jugador, Integer> desglose = panelJugador.getDesgloseDanioComandante();
            int total = panelJugador.getTotalDanioComandante();

            if (desglose.isEmpty()) {
                lblDesgloseCmdte.setText("Total: 0");
            } else {
                StringBuilder texto = new StringBuilder("Total: " + total + "  |  ");
                for (Map.Entry<Jugador, Integer> entry : desglose.entrySet()) {
                    texto.append(entry.getKey().getNombre()).append(": ").append(entry.getValue()).append("  ");
                }
                lblDesgloseCmdte.setText("<html><div style='text-align: center;'>" + texto.toString() + "</div></html>");
            }
        }

        // 🟢 Refresca TODAS las tarjetas numéricas de golpe, sin importar
        // cuántas haya ni cuáles sean.
        for (Map.Entry<JLabel, Supplier<Integer>> entry : etiquetasNumericas.entrySet()) {
            entry.getKey().setText(String.valueOf(entry.getValue().get()));
        }
        // Repinta la rejilla para que las tarjetas de interruptor recalculen
        // su color según el estado actual.
        if (panelGridContadores != null) {
            panelGridContadores.repaint();
        }
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================
    private ImageIcon recortarImagenRedondeada(Image imagenOriginal, int ancho, int alto, int radio) {
        BufferedImage imagenRecortada = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagenRecortada.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int imgAncho = imagenOriginal.getWidth(null);
        int imgAlto = imagenOriginal.getHeight(null);
        double ratio = Math.min((double) ancho / imgAncho, (double) alto / imgAlto);
        int nuevoAncho = (int) (imgAncho * ratio);
        int nuevoAlto = (int) (imgAlto * ratio);

        int x = (ancho - nuevoAncho) / 2;
        int y = (alto - nuevoAlto) / 2;

        Shape formaRedondeada = new RoundRectangle2D.Double(0, 0, ancho, alto, radio, radio);
        g2.setClip(formaRedondeada);
        g2.drawImage(imagenOriginal, x, y, nuevoAncho, nuevoAlto, null);
        g2.dispose();

        return new ImageIcon(imagenRecortada);
    }

    private static class BotonRedondeado extends JButton {
        private static final long serialVersionUID = 1L;
        private final Color colorBase;
        private final Color colorHover;
        private boolean hover = false;

        BotonRedondeado(String texto, Color colorBase, Color colorHover) {
            super(texto);
            this.colorBase = colorBase;
            this.colorHover = colorHover;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hover ? colorHover : colorBase);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}