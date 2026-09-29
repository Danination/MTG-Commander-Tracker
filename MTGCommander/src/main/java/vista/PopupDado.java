package vista;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;

public class PopupDado extends JDialog {

    private static final long serialVersionUID = 1L;
    
    private JPanel panelDado;
    private JLabel lblResultado;
    private JButton btnTirarDeNuevo;
    private JButton btnCerrar;
    
    // Colores
    private static final Color COLOR_ROJO_SANGRE = new Color(220, 60, 60);
    private static final Color COLOR_ROJO_OSCURO = new Color(180, 40, 40);
    private static final Color COLOR_BLANCO = new Color(255, 255, 255);
    private static final Color COLOR_SOMBRA = new Color(0, 0, 0, 100);
    
    // Estado del dado
    private int caraActual = 1;
    private int resultadoFinal = 1;
    private boolean animando = false;
    
    // Para el gesto de lanzamiento
    private Point2D puntoInicio;
    private Point2D puntoFin;
    private long tiempoInicio;
    private long tiempoFin;
    
    // Tamaño del dado
    private static final int TAMANO_DADO = 180;
    private static final int RADIO_ESQUINA = 25;

    public PopupDado(java.awt.Frame padre) {
        super(padre, "Tirar Dado", false); // No modal
        
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("Tirar Dado");
        setSize(400, 500);
        setLocationRelativeTo(padre);
        setResizable(false);
        setAlwaysOnTop(true);
        
        JPanel contentPane = new JPanel(new BorderLayout(0, 20));
        contentPane.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPane.setBackground(new Color(30, 30, 35));
        setContentPane(contentPane);
        
        // ==========================================
        // 1. ZONA NORTE: Título
        // ==========================================
        JLabel lblTitulo = new JLabel("LANZAR DADO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_ROJO_SANGRE);
        contentPane.add(lblTitulo, BorderLayout.NORTH);
        
        // ==========================================
        // 2. ZONA CENTRO: El Dado (interactivo)
        // ==========================================
        panelDado = new JPanel() {
            private static final long serialVersionUID = 1L;
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int centroX = getWidth() / 2;
                int centroY = getHeight() / 2;
                int mitad = TAMANO_DADO / 2;
                
                // Sombra del dado (efecto 3D)
                g2.setColor(COLOR_SOMBRA);
                g2.fillRoundRect(centroX - mitad + 8, centroY - mitad + 8, 
                                 TAMANO_DADO, TAMANO_DADO, RADIO_ESQUINA, RADIO_ESQUINA);
                
                // Cuerpo del dado con degradado (efecto 3D)
                java.awt.GradientPaint degradado = new java.awt.GradientPaint(
                    centroX - mitad, centroY - mitad, COLOR_ROJO_SANGRE.brighter(),
                    centroX + mitad, centroY + mitad, COLOR_ROJO_OSCURO
                );
                g2.setPaint(degradado);
                g2.fillRoundRect(centroX - mitad, centroY - mitad, 
                                 TAMANO_DADO, TAMANO_DADO, RADIO_ESQUINA, RADIO_ESQUINA);
                
                // Borde brillante
                g2.setColor(new Color(255, 255, 255, 80));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(centroX - mitad, centroY - mitad, 
                                TAMANO_DADO - 1, TAMANO_DADO - 1, RADIO_ESQUINA, RADIO_ESQUINA);
                
                // Dibujar los puntos de la cara actual
                dibujarPuntos(g2, centroX, centroY, mitad);
                
                g2.dispose();
            }
            
            private void dibujarPuntos(Graphics2D g2, int centroX, int centroY, int mitad) {
                g2.setColor(COLOR_BLANCO);
                int radioPunto = 12;
                
                // Posiciones de los puntos según la cara
                // Usamos un sistema de coordenadas relativo al centro
                int[][] posiciones = getPuntosParaCara(caraActual);
                
                for (int[] pos : posiciones) {
                    int x = centroX + pos[0];
                    int y = centroY + pos[1];
                    g2.fillOval(x - radioPunto, y - radioPunto, radioPunto * 2, radioPunto * 2);
                    
                    // Brillo en el punto (efecto 3D)
                    g2.setColor(new Color(255, 255, 255, 150));
                    g2.fillOval(x - radioPunto + 3, y - radioPunto + 3, radioPunto, radioPunto);
                    g2.setColor(COLOR_BLANCO);
                }
            }
            
            private int[][] getPuntosParaCara(int cara) {
                int offset = 50; // Distancia del centro a los puntos laterales
                int cero = 0;
                
                switch (cara) {
                    case 1:
                        return new int[][]{{cero, cero}};
                    case 2:
                        return new int[][]{{-offset, -offset}, {offset, offset}};
                    case 3:
                        return new int[][]{{-offset, -offset}, {cero, cero}, {offset, offset}};
                    case 4:
                        return new int[][]{{-offset, -offset}, {offset, -offset}, {-offset, offset}, {offset, offset}};
                    case 5:
                        return new int[][]{{-offset, -offset}, {offset, -offset}, {cero, cero}, {-offset, offset}, {offset, offset}};
                    case 6:
                        return new int[][]{{-offset, -offset}, {offset, -offset}, {-offset, cero}, {offset, cero}, {-offset, offset}, {offset, offset}};
                    default:
                        return new int[][]{{cero, cero}};
                }
            }
        };
        
        panelDado.setPreferredSize(new Dimension(300, 300));
        panelDado.setOpaque(false);
        panelDado.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // ==========================================
        // GESTO DE LANZAMIENTO
        // ==========================================
        panelDado.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!animando) {
                    puntoInicio = e.getPoint();
                    tiempoInicio = System.currentTimeMillis();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!animando && puntoInicio != null) {
                    puntoFin = e.getPoint();
                    tiempoFin = System.currentTimeMillis();
                    lanzarDadoConGesto();
                }
            }
        });
        
        contentPane.add(panelDado, BorderLayout.CENTER);
        
        // ==========================================
        // 3. ZONA SUR: Resultado y Botones
        // ==========================================
        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BoxLayout(panelInferior, BoxLayout.Y_AXIS));
        panelInferior.setOpaque(false);
        
        lblResultado = new JLabel("Arrastra y suelta el dado para lanzar", SwingConstants.CENTER);
        lblResultado.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblResultado.setForeground(new Color(180, 180, 180));
        lblResultado.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        panelInferior.add(lblResultado);
        
        panelInferior.add(Box.createVerticalStrut(20));
        
        // Botón Tirar de Nuevo
        btnTirarDeNuevo = new JButton("🎲 Tirar de Nuevo");
        btnTirarDeNuevo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnTirarDeNuevo.setPreferredSize(new Dimension(200, 45));
        btnTirarDeNuevo.setMaximumSize(new Dimension(200, 45));
        btnTirarDeNuevo.setBackground(COLOR_ROJO_SANGRE);
        btnTirarDeNuevo.setForeground(Color.WHITE);
        btnTirarDeNuevo.setFocusPainted(false);
        btnTirarDeNuevo.setBorderPainted(false);
        btnTirarDeNuevo.setOpaque(true);
        btnTirarDeNuevo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTirarDeNuevo.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        btnTirarDeNuevo.addActionListener(e -> lanzarDadoAleatorio());
        panelInferior.add(btnTirarDeNuevo);
        
        panelInferior.add(Box.createVerticalStrut(10));
        
        // Botón Cerrar
        btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCerrar.setPreferredSize(new Dimension(200, 45));
        btnCerrar.setMaximumSize(new Dimension(200, 45));
        btnCerrar.setBackground(new Color(50, 50, 55));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setOpaque(true);
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrar.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        btnCerrar.addActionListener(e -> dispose());
        panelInferior.add(btnCerrar);
        
        contentPane.add(panelInferior, BorderLayout.SOUTH);
    }
    
    private void lanzarDadoConGesto() {
        // Calcular velocidad del gesto
        double distancia = puntoInicio.distance(puntoFin);
        long duracion = tiempoFin - tiempoInicio;
        
        // Velocidad = distancia / tiempo
        double velocidad = distancia / Math.max(duracion, 1);
        
        // Determinar duración de la animación basada en velocidad
        // Velocidad alta = más tiempo de animación
        int duracionAnimacion;
        if (velocidad > 2.0) {
            duracionAnimacion = 1500; // Lanzamiento rápido
            lblResultado.setText("¡Lanzamiento rápido! 🚀");
        } else if (velocidad > 1.0) {
            duracionAnimacion = 1000; // Lanzamiento normal
            lblResultado.setText("Lanzando...");
        } else {
            duracionAnimacion = 600; // Lanzamiento suave
            lblResultado.setText("Lanzamiento suave...");
        }
        
        animarDado(duracionAnimacion);
    }
    
    private void lanzarDadoAleatorio() {
        lblResultado.setText("Lanzando...");
        animarDado(1000);
    }
    
    private void animarDado(int duracionTotal) {
        animando = true;
        btnTirarDeNuevo.setEnabled(false);
        panelDado.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        // Generar resultado final aleatorio
        resultadoFinal = (int) (Math.random() * 6) + 1;
        
        // Timer para la animación
        final long inicio = System.currentTimeMillis();
        final int duracion = duracionTotal;
        final int intervalo = 50; // Cambiar cara cada 50ms
        
        Timer timer = new Timer(intervalo, new ActionListener() {
            int frame = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                long transcurrido = System.currentTimeMillis() - inicio;
                
                if (transcurrido >= duracion) {
                    // Animación terminada - mostrar resultado final
                    ((Timer) e.getSource()).stop();
                    caraActual = resultadoFinal;
                    panelDado.repaint();
                    
                    // Efecto de "pop" final
                    efectoPop();
                    
                    lblResultado.setText("<html><div style='text-align: center;'>¡Resultado: <b style='color: #DC3C3C; font-size: 20px;'>" + 
                                       resultadoFinal + "</b></div></html>");
                    
                    animando = false;
                    btnTirarDeNuevo.setEnabled(true);
                    panelDado.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    // Mostrar caras aleatorias durante la animación
                    caraActual = (int) (Math.random() * 6) + 1;
                    panelDado.repaint();
                    frame++;
                }
            }
        });
        
        timer.start();
    }
    
    private void efectoPop() {
        // Animación de "pop" - agrandar y volver al tamaño normal
        Timer popTimer = new Timer(30, new ActionListener() {
            int etapa = 0;
            double escala = 1.0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (etapa == 0) {
                    // Crecer
                    escala += 0.05;
                    if (escala >= 1.2) {
                        etapa = 1;
                    }
                } else {
                    // Encoger
                    escala -= 0.05;
                    if (escala <= 1.0) {
                        escala = 1.0;
                        ((Timer) e.getSource()).stop();
                    }
                }
                
                // Aplicar escala al panel (simulado cambiando el tamaño)
                int nuevoTamano = (int) (TAMANO_DADO * escala);
                int offset = (TAMANO_DADO - nuevoTamano) / 2;
                panelDado.setBorder(BorderFactory.createEmptyBorder(offset, offset, offset, offset));
                panelDado.repaint();
            }
        });
        popTimer.start();
    }
    
    public int getResultado() {
        return resultadoFinal;
    }
}