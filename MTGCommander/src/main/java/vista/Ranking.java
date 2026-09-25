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
    private static final Color COLOR_SANGRE_OSCURO = new Color(74, 14, 14);

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
        setSize(950, 600);
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
        
        String[] columnas = {"Pos.", "Jugador", "Partidas", "Victorias", "% Victoria", "Pos. Prom.", "Racha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
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
        colJugador.setPreferredWidth(250);
        
        TableColumn colRacha = tablaRanking.getColumnModel().getColumn(6);
        colRacha.setPreferredWidth(100);
        colRacha.setMaxWidth(120);

        // Centrar el texto de las columnas numéricas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(new Color(45, 45, 48));
        centerRenderer.setForeground(Color.WHITE);
        for (int i = 2; i < columnas.length; i++) {
            tablaRanking.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Alinear el nombre del jugador a la izquierda con margen
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBorder(new EmptyBorder(0, 15, 0, 0));
        leftRenderer.setBackground(new Color(45, 45, 48));
        leftRenderer.setForeground(Color.WHITE);
        tablaRanking.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);

        // 🟢 RENDERIZADOR PERSONALIZADO para el Top 3 en tonos ROJOS
        tablaRanking.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row == 0) {
                        c.setBackground(new Color(90, 25, 25)); // 🥇 1º Rojo brillante
                        if (column == 0) {
                            setText("🥇");
                            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
                            setHorizontalAlignment(JLabel.CENTER);
                        }
                    } else if (row == 1) {
                        c.setBackground(new Color(75, 20, 20)); // 🥈 2º Rojo medio
                        if (column == 0) {
                            setText("🥈");
                            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
                            setHorizontalAlignment(JLabel.CENTER);
                        }
                    } else if (row == 2) {
                        c.setBackground(new Color(60, 15, 15)); // 🥉 3º Rojo oscuro
                        if (column == 0) {
                            setText("🥉");
                            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
                            setHorizontalAlignment(JLabel.CENTER);
                        }
                    } else {
                        c.setBackground(new Color(45, 45, 48)); // Fondo normal
                        if (column == 0) {
                            setText(String.valueOf(row + 1));
                            setFont(new Font("Segoe UI", Font.BOLD, 16));
                            setHorizontalAlignment(JLabel.CENTER);
                        }
                    }
                }
                return c;
            }
        });
        
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
        
        JButton btnVolver = new JButton("← Volver");
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnVolver.setPreferredSize(new Dimension(140, 45));
        btnVolver.setBackground(new Color(50, 50, 55));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setOpaque(true);
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
            modeloTabla.addRow(new Object[]{"-", "Aún no hay datos", "-", "-", "-", "-", "-"});
        } else {
            for (int i = 0; i < datosRanking.size(); i++) {
                Object[] fila = datosRanking.get(i);
                Object[] filaCompleta = new Object[7];
                filaCompleta[0] = String.valueOf(i + 1);
                System.arraycopy(fila, 0, filaCompleta, 1, fila.length);
                modeloTabla.addRow(filaCompleta);
            }
        }
    }
}