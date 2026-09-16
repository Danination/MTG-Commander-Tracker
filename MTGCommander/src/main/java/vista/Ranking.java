package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
		setTitle("Ranking de Jugadores");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(10, 10));

		// ==========================================
		// 1. ZONA CENTRO: Tabla de Estadísticas
		// ==========================================
		String[] columnas = {"Jugador", "Partidas Jugadas", "Victorias", "% Victoria"};
		modeloTabla = new DefaultTableModel(columnas, 0) {
			// Hacer que la tabla no sea editable por el usuario
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		tablaRanking = new JTable(modeloTabla);
		tablaRanking.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tablaRanking.setRowHeight(30); // Filas más altas para que se lea bien
		tablaRanking.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
		tablaRanking.getTableHeader().setBackground(new Color(230, 230, 230));
		
		// Añadir scroll por si hay muchos jugadores
		JScrollPane scrollPane = new JScrollPane(tablaRanking);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		// Cargar datos
		cargarRanking();

		// ==========================================
		// 2. ZONA SUR: Botón Volver
		// ==========================================
		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
		contentPane.add(panelBotones, BorderLayout.SOUTH);

		JButton btnVolver = new JButton("Volver al Menú");
		btnVolver.setFont(new Font("Tahoma", Font.BOLD, 14));
		panelBotones.add(btnVolver);

		// ==========================================
		// 3. LÓGICA DEL BOTÓN
		// ==========================================
		btnVolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				MenuPrincipal menu = new MenuPrincipal();
				menu.setVisible(true);
			}
		});
	}

	private void cargarRanking() {
		// Limpiar tabla actual
		modeloTabla.setRowCount(0);
		
		List<Object[]> datosRanking = GestorBD.obtenerRanking();
		
		if (datosRanking.isEmpty()) {
			modeloTabla.addRow(new Object[]{"Aún no hay datos", "-", "-", "-"});
		} else {
			for (Object[] fila : datosRanking) {
				modeloTabla.addRow(fila);
			}
		}
	}
}