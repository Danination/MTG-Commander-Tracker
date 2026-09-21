package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import dao.GestorBD;

public class Ranking extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable tablaRanking;
    private DefaultTableModel modeloTabla;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Ranking frame = new Ranking();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Ranking() {
        setTitle("🏆 Ranking de Jugadores");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 950, 550); // 🟢 MEJORA: Un poco más ancho para que quepa todo holgadamente
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(15, 15));

        // ==========================================
        // 1. ZONA NORTE: Título decorativo
        // ==========================================
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTitulo.setOpaque(false);
        JLabel lblTitulo = new JLabel(" --> SALÓN DE LA FAMA <--");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(255, 215, 0)); // Dorado
        panelTitulo.add(lblTitulo);
        contentPane.add(panelTitulo, BorderLayout.NORTH);

        // ==========================================
        // 2. ZONA CENTRO: Tabla de Estadísticas
        // ==========================================
        String[] columnas = {"Pos.", "Jugador", "Partidas", "Victorias", "% Victoria", "Pos. Prom.", "Racha (Últimas) 🔥"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaRanking = new JTable(modeloTabla);
        tablaRanking.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tablaRanking.setRowHeight(45); // 🟢 MEJORA: Filas un poco más altas para que respiren
        
        // Estilo de la cabecera
        tablaRanking.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tablaRanking.getTableHeader().setBackground(new Color(50, 50, 55));
        tablaRanking.getTableHeader().setForeground(Color.WHITE);
        tablaRanking.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 40));
        
        // 🟢 MEJORA: Ajuste manual de anchos de columna para una distribución perfecta
        TableColumn colPos = tablaRanking.getColumnModel().getColumn(0);
        colPos.setPreferredWidth(60);
        colPos.setMaxWidth(60);
        
        TableColumn colJugador = tablaRanking.getColumnModel().getColumn(1);
        colJugador.setPreferredWidth(250); // Más espacio para nombres largos
        
        // 🟢  TableColumn no tiene setToolTipText.
        // Lo solucionamos con un nombre de columna autoexplicativo.
        TableColumn colRacha = tablaRanking.getColumnModel().getColumn(6);
        colRacha.setPreferredWidth(140); // Un poco más de ancho para que quepa el texto nuevo
        colRacha.setMaxWidth(160);

        // Centrar el texto de las columnas numéricas (de la 2 a la 6)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 2; i < columnas.length; i++) {
            tablaRanking.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Alinear el nombre del jugador a la izquierda con un pequeño margen
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBorder(new EmptyBorder(0, 10, 0, 0)); // Margen izquierdo de 10px
        tablaRanking.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);

        // RENDERIZADOR PERSONALIZADO para colorear el Top 3
        tablaRanking.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row == 0) {
                        c.setBackground(new Color(255, 215, 0, 30)); // 🥇 ORO (más sutil)
                        if (column == 0) {
                            setText("🥇");
                            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
                            setHorizontalAlignment(JLabel.CENTER);
                        }
                    } else if (row == 1) {
                        c.setBackground(new Color(192, 192, 192, 30)); // 🥈 PLATA
                        if (column == 0) {
                            setText("🥈");
                            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
                            setHorizontalAlignment(JLabel.CENTER);
                        }
                    } else if (row == 2) {
                        c.setBackground(new Color(205, 127, 50, 30)); // 🥉 BRONCE
                        if (column == 0) {
                            setText("🥉");
                            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
                            setHorizontalAlignment(JLabel.CENTER);
                        }
                    } else {
                        c.setBackground(new Color(45, 45, 48)); // Fondo normal oscuro
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
        scrollPane.setBorder(null); // Quitamos el borde por defecto del scroll para que sea más limpio
        contentPane.add(scrollPane, BorderLayout.CENTER);

        cargarRanking();

        // ==========================================
        // 3. ZONA SUR: Botón Volver
        // ==========================================
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotones.setOpaque(false);
        contentPane.add(panelBotones, BorderLayout.SOUTH);

        JButton btnVolver = new JButton("Volver al Menú");
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnVolver.setBackground(new Color(70, 70, 75));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setOpaque(true);
        btnVolver.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        // Un poco más de tamaño al botón
        btnVolver.setPreferredSize(new java.awt.Dimension(200, 40)); 
        panelBotones.add(btnVolver);

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