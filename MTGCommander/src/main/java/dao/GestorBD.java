package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import modelo.Jugador;

public class GestorBD {

    /**
     * Obtiene todos los jugadores guardados en la base de datos.
     */
    public static List<Jugador> obtenerTodosLosJugadores() {
        List<Jugador> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, color_favorito, comandante_nombre, comandante_imagen_url FROM jugadores";
        
        try (Connection conn = ConexionBD.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Jugador j = new Jugador(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("color_favorito")
                );
                j.setComandanteNombre(rs.getString("comandante_nombre"));
                j.setComandanteImagenUrl(rs.getString("comandante_imagen_url"));
                lista.add(j);
            }
        } catch (Exception e) {
            System.err.println("❌ Error al obtener jugadores: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Guarda un jugador nuevo o actualiza uno existente.
     * Si el ID es 0 o menor, hace un INSERT. Si es > 0, hace un UPDATE.
     */
    public static void guardarJugador(Jugador jugador) {
        Connection conn = ConexionBD.getConexion();
        if (conn == null) return;

        try {
            if (jugador.getId() <= 0) {
                // ES UN JUGADOR NUEVO (INSERT)
                String sql = "INSERT INTO jugadores (nombre, color_favorito, comandante_nombre, comandante_imagen_url) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, jugador.getNombre());
                    pstmt.setString(2, jugador.getColorFavorito());
                    pstmt.setString(3, jugador.getComandanteNombre());
                    pstmt.setString(4, jugador.getComandanteImagenUrl());
                    pstmt.executeUpdate();
                    
                    // Obtener el ID generado por la base de datos y asignarlo al objeto
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            // Necesitamos un setter para el ID en la clase Jugador. 
                            // Si no lo tienes, avísame, pero es buena práctica tenerlo.
                            jugador.setId(rs.getInt(1)); 
                        }
                    }
                }
            } else {
                // ES UNA ACTUALIZACIÓN (UPDATE)
                String sql = "UPDATE jugadores SET nombre = ?, color_favorito = ?, comandante_nombre = ?, comandante_imagen_url = ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, jugador.getNombre());
                    pstmt.setString(2, jugador.getColorFavorito());
                    pstmt.setString(3, jugador.getComandanteNombre());
                    pstmt.setString(4, jugador.getComandanteImagenUrl());
                    pstmt.setInt(5, jugador.getId());
                    pstmt.executeUpdate();
                }
            }
            System.out.println("✅ Jugador guardado/actualizado en la BD: " + jugador.getNombre());
        } catch (Exception e) {
            System.err.println("❌ Error al guardar jugador: " + e.getMessage());
        }
    }

    /**
     * Elimina un jugador de la base de datos por su ID.
     */
    public static void eliminarJugador(int id) {
        String sql = "DELETE FROM jugadores WHERE id = ?";
        
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✅ Jugador eliminado de la BD con ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar jugador: " + e.getMessage());
        }
    }
    
    /**
     * Guarda una nueva partida en la base de datos y devuelve su ID.
     */
    public static int guardarPartida(String fecha, int duracionMinutos, String notas) {
        int idPartida = -1;
        String sql = "INSERT INTO partidas (fecha, duracion_minutos, notas) VALUES (?, ?, ?)";
        
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, fecha);
            pstmt.setInt(2, duracionMinutos);
            pstmt.setString(3, notas);
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    idPartida = rs.getInt(1);
                }
            }
            System.out.println("✅ Partida guardada en BD con ID: " + idPartida);
        } catch (Exception e) {
            System.err.println("❌ Error al guardar partida: " + e.getMessage());
        }
        return idPartida;
    }

    /**
     * Guarda el resultado de un jugador en una partida específica.
     */
    public static void guardarResultado(int partidaId, int jugadorId, int posicion, String tipoResultado) {
        String sql = "INSERT INTO resultados (partida_id, jugador_id, posicion, tipo_resultado) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, partidaId);
            pstmt.setInt(2, jugadorId);
            pstmt.setInt(3, posicion);
            pstmt.setString(4, tipoResultado);
            pstmt.executeUpdate();
            
        } catch (Exception e) {
            System.err.println("❌ Error al guardar resultado: " + e.getMessage());
        }
    }
}