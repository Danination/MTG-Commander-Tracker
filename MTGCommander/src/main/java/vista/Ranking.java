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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;

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
        setBounds(100, 100, 900, 500);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(15, 15));

        // ==========================================
        // 1. ZONA NORTE: Título decorativo
        // ==========================================
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTitulo.setOpaque(false);
        JLabel lblTitulo = new JLabel(" SALÓN DE LA FAMA 🏆");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(255, 215, 0)); // Dorado
        panelTitulo.add(lblTitulo);
        contentPane.add(panelTitulo, BorderLayout.NORTH);

        // ==========================================
        // 2. ZONA CENTRO: Tabla de Estadísticas
        // ==========================================
        String[] columnas = {"Posición", "Jugador", "Partidas", "Victorias", "% Victoria", "Pos. Promedio", "Racha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaRanking = new JTable(modeloTabla);
        tablaRanking.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tablaRanking.setRowHeight(40); // Filas más altas
        tablaRanking.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        tablaRanking.getTableHeader().setBackground(new Color(50, 50, 55));
        tablaRanking.getTableHeader().setForeground(Color.WHITE);
        
        // Centrar el texto de las columnas numéricas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 2; i < columnas.length; i++) {
            tablaRanking.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        //  RENDERIZADOR PERSONALIZADO para colorear el Top 3
        tablaRanking.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    // Colorear según la posición en el ranking
                    if (row == 0) {
                        // 🥇 ORO
                        c.setBackground(new Color(255, 215, 0, 40)); // Dorado suave
                        if (column == 0) {
                            setText("🥇");
                            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
                        }
                    } else if (row == 1) {
                        // 🥈 PLATA
                        c.setBackground(new Color(192, 192, 192, 40)); // Plateado suave
                        if (column == 0) {
                            setText("🥈");
                            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
                        }
                    } else if (row == 2) {
                        // 🥉 BRONCE
                        c.setBackground(new Color(205, 127, 50, 40)); // Bronce suave
                        if (column == 0) {
                            setText("🥉");
                            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
                        }
                    } else {
                        c.setBackground(new Color(45, 45, 48)); // Fondo normal oscuro
                        if (column == 0) {
                            setText(String.valueOf(row + 1));
                            setFont(new Font("Segoe UI", Font.BOLD, 16));
                        }
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaRanking);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Cargar datos
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
                // Añadimos un elemento extra al principio para la posición visual
                Object[] filaCompleta = new Object[7];
                filaCompleta[0] = String.valueOf(i + 1); // Posición numérica (luego se reemplaza por emoji)
                System.arraycopy(fila, 0, filaCompleta, 1, fila.length);
                modeloTabla.addRow(filaCompleta);
            }
        }
    }
}