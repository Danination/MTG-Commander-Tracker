package vista;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.formdev.flatlaf.FlatDarkLaf;

import dao.GestorBD;

public class Ranking extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable tablaRanking;
    private DefaultTableModel modeloTabla;

    // Colores rojo sangre
    private static final Color COLOR_SANGRE = new Color(220, 60, 60);
    private static final Color COLOR_SANGRE_CLARO = new Color(165, 42, 42);

    // 🟢 Índice de la columna Racha, para la fuente con emoji y el ancho
    private static final int COL_RACHA = 7;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    FlatDarkLaf.setup();
                    Ranking frame = new Ranking();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Ranking() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Ranking de Jugadores");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // 🟢 Un poco más ancha: hay una columna nueva (Derrotas)
        setSize(1050, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(40, 40, 40, 40));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 25));

        // ==========================================
        // 1. ZONA NORTE: Título en Rojo Sangre
        // ==========================================
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BorderLayout(0, 10));
        panelNorte.setOpaque(false);
        panelNorte.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel panelTituloContainer = new JPanel();
        panelTituloContainer.setLayout(new BorderLayout());
        panelTituloContainer.setOpaque(false);

        JLabel lblTitulo = new JLabel("RANKING DE JUGADORES", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitulo.setForeground(COLOR_SANGRE);
        panelTituloContainer.add(lblTitulo, BorderLayout.CENTER);

        JPanel lineaDecorativa = new JPanel();
        lineaDecorativa.setPreferredSize(new Dimension(0, 3));
        lineaDecorativa.setBackground(new Color(150, 35, 35));
        panelTituloContainer.add(lineaDecorativa, BorderLayout.SOUTH);

        panelNorte.add(panelTituloContainer, BorderLayout.CENTER);

        JLabel lblSubtitulo = new JLabel("Estadísticas y salón de la fama", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitulo.setForeground(new Color(180, 180, 180));
        panelNorte.add(lblSubtitulo, BorderLayout.SOUTH);

        contentPane.add(panelNorte, BorderLayout.NORTH);

        // ==========================================
        // 2. ZONA CENTRO: Tarjeta redondeada con Tabla
        // ==========================================
        JPanel panelTablaContainer = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradiente = new GradientPaint(
                    0, 0, new Color(50, 50, 55),
                    0, getHeight(), new Color(40, 40, 45)
                );
                g2.setPaint(gradiente);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

                g2.setColor(new Color(70, 70, 75));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        panelTablaContainer.setOpaque(false);
        panelTablaContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 🟢 NUEVO: columna "Derrotas" (se calcula sola a partir de
        // Partidas y Victorias, no hace falta tocar GestorBD).
        String[] columnas = {"Pos.", "Jugador", "Partidas", "Victorias", "Derrotas", "% Victoria", "Pos. Prom.", "Racha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaRanking = new JTable(modeloTabla);
        tablaRanking.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tablaRanking.setRowHeight(45);
        tablaRanking.setBackground(new Color(45, 45, 48));
        tablaRanking.setForeground(Color.WHITE);
        tablaRanking.setGridColor(new Color(60, 60, 65));
        tablaRanking.setSelectionBackground(COLOR_SANGRE_CLARO);
        tablaRanking.setSelectionForeground(Color.WHITE);

        // Estilo de la cabecera
        tablaRanking.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tablaRanking.getTableHeader().setBackground(new Color(40, 40, 45));
        tablaRanking.getTableHeader().setForeground(new Color(200, 200, 200));
        tablaRanking.getTableHeader().setPreferredSize(new Dimension(0, 40));
        tablaRanking.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(70, 70, 75)));

        // Ajuste manual de anchos de columna
        TableColumn colPos = tablaRanking.getColumnModel().getColumn(0);
        colPos.setPreferredWidth(60);
        colPos.setMaxWidth(60);

        TableColumn colJugador = tablaRanking.getColumnModel().getColumn(1);
        colJugador.setPreferredWidth(230);

        TableColumn colRacha = tablaRanking.getColumnModel().getColumn(COL_RACHA);
        colRacha.setPreferredWidth(100);
        colRacha.setMaxWidth(120);

        // 🟢 UN SOLO renderer para TODA la tabla: antes había un renderer
        // "genérico" con fondo fijo para las columnas 1-6 y, por separado,
        // un renderer de "Object.class" que solo llegaba a la columna 0
        // (por eso el color del podio solo se veía en la columna "Pos.",
        // nunca en el resto de la fila). Ahora el mismo renderer decide
        // el fondo según el puesto para TODAS las columnas, y dentro de
        // eso aplica la alineación/fuente que toque según la columna.
        DefaultTableCellRenderer rendererUnificado = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    Color fondoFila;
                    if (row == 0) fondoFila = new Color(90, 25, 25);
                    else if (row == 1) fondoFila = new Color(75, 20, 20);
                    else if (row == 2) fondoFila = new Color(60, 15, 15);
                    // 🟢 Zebra striping sutil para el resto de filas, más
                    // fácil de seguir con la vista.
                    else fondoFila = (row % 2 == 0) ? new Color(45, 45, 48) : new Color(51, 51, 55);
                    setBackground(fondoFila);
                    setForeground(Color.WHITE);
                }

                setBorder(new EmptyBorder(0, 0, 0, 0));

                if (column == 0) {
                    setHorizontalAlignment(JLabel.CENTER);
                    if (row == 0) {
                        setText("🥇");
                        setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
                    } else if (row == 1) {
                        setText("🥈");
                        setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
                    } else if (row == 2) {
                        setText("🥉");
                        setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
                    } else {
                        setText(String.valueOf(row + 1));
                        setFont(new Font("Segoe UI", Font.BOLD, 16));
                    }
                } else if (column == 1) {
                    setHorizontalAlignment(JLabel.LEFT);
                    setBorder(new EmptyBorder(0, 15, 0, 0));
                    setFont(new Font("Segoe UI", Font.BOLD, 16));
                } else if (column == COL_RACHA) {
                    // 🟢 La racha puede traer un emoji desde la base de
                    // datos (p. ej. una llamita en racha ganadora); esta
                    // es la única fuente que lo dibuja bien en este
                    // equipo en vez de un cuadro vacío.
                    setHorizontalAlignment(JLabel.CENTER);
                    setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
                } else {
                    setHorizontalAlignment(JLabel.CENTER);
                    setFont(new Font("Segoe UI", Font.PLAIN, 16));
                }

                return c;
            }
        };

        for (int i = 0; i < columnas.length; i++) {
            tablaRanking.getColumnModel().getColumn(i).setCellRenderer(rendererUnificado);
        }

        JScrollPane scrollPane = new JScrollPane(tablaRanking);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        panelTablaContainer.add(scrollPane, BorderLayout.CENTER);

        contentPane.add(panelTablaContainer, BorderLayout.CENTER);

        cargarRanking();

        // ==========================================
        // 3. ZONA SUR: Botón Volver unificado
        // ==========================================
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotones.setOpaque(false);

        // 🟢 Mismo botón redondeado que en el resto de la app (antes era
        // un JButton plano, desentonaba).
        JButton btnVolver = new BotonRedondeado("Volver", new Color(50, 50, 55), new Color(65, 65, 70));
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnVolver.setPreferredSize(new Dimension(160, 46));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelBotones.add(btnVolver);

        contentPane.add(panelBotones, BorderLayout.SOUTH);

        btnVolver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                MenuPrincipal menu = new MenuPrincipal();
                menu.setVisible(true);
            }
        });
    }

    private void cargarRanking() {
        modeloTabla.setRowCount(0);

        List<Object[]> datosRanking = GestorBD.obtenerRanking();

        if (datosRanking.isEmpty()) {
            modeloTabla.addRow(new Object[]{"-", "Aún no hay datos", "-", "-", "-", "-", "-", "-"});
        } else {
            for (int i = 0; i < datosRanking.size(); i++) {
                // 🟢 Orden esperado desde GestorBD: Jugador, Partidas,
                // Victorias, % Victoria, Pos. Prom., Racha (6 campos).
                Object[] fila = datosRanking.get(i);

                Object jugador = fila[0];
                Object partidasObj = fila.length > 1 ? fila[1] : "-";
                Object victoriasObj = fila.length > 2 ? fila[2] : "-";
                Object porcentajeObj = fila.length > 3 ? fila[3] : "-";
                Object posPromObj = fila.length > 4 ? fila[4] : "-";
                Object rachaObj = fila.length > 5 ? fila[5] : "-";

                // 🟢 NUEVO: Derrotas = Partidas - Victorias
                String derrotas = "-";
                try {
                    int partidas = Integer.parseInt(String.valueOf(partidasObj).trim());
                    int victorias = Integer.parseInt(String.valueOf(victoriasObj).trim());
                    derrotas = String.valueOf(partidas - victorias);
                } catch (NumberFormatException ex) {
                    // Si el dato no es numérico (p. ej. "-"), se deja así.
                }

                Object[] filaCompleta = new Object[8];
                filaCompleta[0] = String.valueOf(i + 1);
                filaCompleta[1] = jugador;
                filaCompleta[2] = partidasObj;
                filaCompleta[3] = victoriasObj;
                filaCompleta[4] = derrotas;
                filaCompleta[5] = porcentajeObj;
                filaCompleta[6] = posPromObj;
                filaCompleta[7] = rachaObj;
                modeloTabla.addRow(filaCompleta);
            }
        }
    }

    /**
     * Botón con esquinas redondeadas, mismo componente que en el resto
     * de la app.
     */
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