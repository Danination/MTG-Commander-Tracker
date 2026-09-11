package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import modelo.Jugador;

public class PanelJugador extends JPanel {

	private static final long serialVersionUID = 1L;
	
	// Datos del jugador
	private Jugador jugador;
	private int vidas;
	private int dañoComandante;
	private int veneno;
	private boolean eliminado = false; // <--- NUEVO: Estado del jugador

	// Componentes visuales (Ahora son atributos para poder desactivarlos luego)
	private JLabel lblNombre;
	private JLabel lblVidas;
	private JLabel lblDañoCmdte;
	private JLabel lblVeneno;
	
	private JButton btnMasVida;
	private JButton btnMenosVida;
	private JButton btnMasCmdte;
	private JButton btnMenosCmdte;
	private JButton btnMasVeneno;
	private JButton btnMenosVeneno;
	private JButton btnConceder;

	public PanelJugador(Jugador jugador, int vidasIniciales) {
		this.jugador = jugador;
		this.vidas = vidasIniciales;
		this.dañoComandante = 0;
		this.veneno = 0;

		setBorder(new LineBorder(Color.GRAY, 2, true));
		setLayout(new BorderLayout(5, 5));

		// ==========================================
		// 1. ZONA NORTE: Nombre
		// ==========================================
		lblNombre = new JLabel(jugador.getNombre() + " (" + jugador.getColorFavorito() + ")", JLabel.CENTER);
		lblNombre.setFont(new Font("Tahoma", Font.BOLD, 16));
		add(lblNombre, BorderLayout.NORTH);

		// ==========================================
		// 2. ZONA CENTRO: Vidas
		// ==========================================
		JPanel panelCentro = new JPanel(new BorderLayout());
		
		lblVidas = new JLabel(String.valueOf(vidas), JLabel.CENTER);
		lblVidas.setFont(new Font("Tahoma", Font.BOLD, 48));
		panelCentro.add(lblVidas, BorderLayout.CENTER);

		JPanel panelBotonesVida = new JPanel(new GridLayout(1, 2, 5, 5));
		btnMasVida = new JButton("+1");
		btnMenosVida = new JButton("-1");
		
		btnMasVida.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnMenosVida.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		panelBotonesVida.add(btnMasVida);
		panelBotonesVida.add(btnMenosVida);
		panelCentro.add(panelBotonesVida, BorderLayout.SOUTH);
		
		add(panelCentro, BorderLayout.CENTER);

		// ==========================================
		// 3. ZONA SUR: Comandante, Veneno y Conceder
		// ==========================================
		JPanel panelSur = new JPanel();
		panelSur.setLayout(new GridLayout(3, 1, 5, 5)); // 3 filas: Cmdte, Veneno, Conceder

		// Fila 1: Daño de Comandante
		JPanel filaCmdte = new JPanel(new FlowLayout(FlowLayout.CENTER));
		lblDañoCmdte = new JLabel("Daño Cmdte: 0");
		lblDañoCmdte.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnMasCmdte = new JButton("+1");
		btnMenosCmdte = new JButton("-1");
		filaCmdte.add(lblDañoCmdte);
		filaCmdte.add(btnMasCmdte);
		filaCmdte.add(btnMenosCmdte);
		panelSur.add(filaCmdte);

		// Fila 2: Veneno
		JPanel filaVeneno = new JPanel(new FlowLayout(FlowLayout.CENTER));
		lblVeneno = new JLabel("Veneno: 0");
		lblVeneno.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnMasVeneno = new JButton("+1");
		btnMenosVeneno = new JButton("-1");
		filaVeneno.add(lblVeneno);
		filaVeneno.add(btnMasVeneno);
		filaVeneno.add(btnMenosVeneno);
		panelSur.add(filaVeneno);

		// Fila 3: Conceder
		JPanel filaConceder = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btnConceder = new JButton("Conceder");
		btnConceder.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnConceder.setForeground(Color.RED);
		filaConceder.add(btnConceder);
		panelSur.add(filaConceder);

		add(panelSur, BorderLayout.SOUTH);

		// ==========================================
		// 4. LÓGICA DE LOS BOTONES
		// ==========================================
		
		// Botones de Vida
		btnMasVida.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { cambiarVidas(1); }
		});
		btnMenosVida.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { cambiarVidas(-1); }
		});

		// Botones de Daño de Comandante
		btnMasCmdte.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dañoComandante++;
				lblDañoCmdte.setText("Daño Cmdte: " + dañoComandante);
			}
		});
		btnMenosCmdte.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (dañoComandante > 0) {
					dañoComandante--;
					lblDañoCmdte.setText("Daño Cmdte: " + dañoComandante);
				}
			}
		});

		// Botones de Veneno
		btnMasVeneno.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				veneno++;
				lblVeneno.setText("Veneno: " + veneno);
			}
		});
		btnMenosVeneno.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (veneno > 0) {
					veneno--;
					lblVeneno.setText("Veneno: " + veneno);
				}
			}
		});

		// Botón CONCEDER (La lógica estrella)
		btnConceder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (eliminado) return; // Si ya está eliminado, no hacemos nada

				int opcion = JOptionPane.showConfirmDialog(PanelJugador.this,
						"¿Seguro que " + jugador.getNombre() + " concede la partida?",
						"Confirmar Concesión", JOptionPane.YES_NO_OPTION);

				if (opcion == JOptionPane.YES_OPTION) {
					eliminarJugador();
				}
			}
		});
	}

	// ==========================================
	// MÉTODOS AUXILIARES
	// ==========================================
	
	private void cambiarVidas(int cantidad) {
		if (eliminado) return; // <--- BLOQUEO: Si está eliminado, no puede cambiar vidas

		this.vidas += cantidad;
		lblVidas.setText(String.valueOf(this.vidas));
		
		if (this.vidas < 20) {
			lblVidas.setForeground(Color.RED);
		} else {
			lblVidas.setForeground(Color.BLACK);
		}
	}

	// Método para eliminar al jugador visual y lógicamente
	private void eliminarJugador() {
		eliminado = true;
		
		// 1. Deshabilitar todos los botones
		btnMasVida.setEnabled(false);
		btnMenosVida.setEnabled(false);
		btnMasCmdte.setEnabled(false);
		btnMenosCmdte.setEnabled(false);
		btnMasVeneno.setEnabled(false);
		btnMenosVeneno.setEnabled(false);
		btnConceder.setEnabled(false);
		
		// 2. Cambios visuales para indicar que está fuera
		btnConceder.setText("ELIMINADO");
		lblVidas.setForeground(Color.GRAY);
		lblNombre.setForeground(Color.GRAY);
		this.setBackground(Color.LIGHT_GRAY); // El fondo del panel se pone gris
	}
	
	// Método público para reiniciar el panel a su estado original
	public void reiniciarPanel(int vidasIniciales) {
		this.vidas = vidasIniciales;
		this.dañoComandante = 0;
		this.veneno = 0;
		this.eliminado = false;

		// Actualizar textos
		lblVidas.setText(String.valueOf(vidas));
		lblVidas.setForeground(Color.BLACK);
		lblDañoCmdte.setText("Daño Cmdte: 0");
		lblVeneno.setText("Veneno: 0");

		// Reactivar todos los botones
		btnMasVida.setEnabled(true); btnMenosVida.setEnabled(true);
		btnMasCmdte.setEnabled(true); btnMenosCmdte.setEnabled(true);
		btnMasVeneno.setEnabled(true); btnMenosVeneno.setEnabled(true);
		btnConceder.setEnabled(true); btnConceder.setText("Conceder");

		// Restaurar colores y fondo
		this.setBackground(null); 
		lblNombre.setForeground(Color.BLACK);
	}

	public Jugador getJugador() { return jugador; }
	public int getVidas() { return vidas; }
}