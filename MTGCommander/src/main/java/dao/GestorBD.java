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
	    // 🟢 Añadimos avatar_path a la consulta
	    String sql = "SELECT id, nombre, color_favorito, comandante_nombre, comandante_imagen_url, avatar_path FROM jugadores";
	    
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
	            j.setAvatarPath(rs.getString("avatar_path")); // 🟢 NUEVO
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
	            // INSERT: Añadimos avatar_path
	            String sql = "INSERT INTO jugadores (nombre, color_favorito, comandante_nombre, comandante_imagen_url, avatar_path) VALUES (?, ?, ?, ?, ?)";
	            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	                pstmt.setString(1, jugador.getNombre());
	                pstmt.setString(2, jugador.getColorFavorito());
	                pstmt.setString(3, jugador.getComandanteNombre());
	                pstmt.setString(4, jugador.getComandanteImagenUrl());
	                pstmt.setString(5, jugador.getAvatarPath()); // 🟢 NUEVO
	                pstmt.executeUpdate();
	                
	                try (ResultSet rs = pstmt.getGeneratedKeys()) {
	                    if (rs.next()) {
	                        jugador.setId(rs.getInt(1)); 
	                    }
	                }
	            }
	        } else {
	            // UPDATE: Añadimos avatar_path
	            String sql = "UPDATE jugadores SET nombre = ?, color_favorito = ?, comandante_nombre = ?, comandante_imagen_url = ?, avatar_path = ? WHERE id = ?";
	            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	                pstmt.setString(1, jugador.getNombre());
	                pstmt.setString(2, jugador.getColorFavorito());
	                pstmt.setString(3, jugador.getComandanteNombre());
	                pstmt.setString(4, jugador.getComandanteImagenUrl());
	                pstmt.setString(5, jugador.getAvatarPath()); // 🟢 NUEVO
	                pstmt.setInt(6, jugador.getId());
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
    
    /**
     * Obtiene las estadísticas de todos los jugadores para el Ranking.
     * Devuelve una lista de arrays de Object: [Nombre, Partidas, Victorias, %Victoria]
     */
    /**
     * Obtiene las estadísticas completas de todos los jugadores para el Ranking.
     * Devuelve: [Nombre, Partidas, Victorias, %Victoria, PosiciónPromedio, Racha]
     */
    public static List<Object[]> obtenerRanking() {
        List<Object[]> ranking = new ArrayList<>();
        
        String sql = """
            SELECT 
                j.nombre,
                COUNT(r.id) as partidas,
                SUM(CASE WHEN r.posicion = 1 THEN 1 ELSE 0 END) as victorias,
                ROUND(100.0 * SUM(CASE WHEN r.posicion = 1 THEN 1 ELSE 0 END) / COUNT(r.id), 1) as porcentaje,
                ROUND(AVG(r.posicion), 1) as posicion_promedio,
                -- Calcular racha actual (victorias/derrotas consecutivas)
                (SELECT COUNT(*) FROM resultados r2 
                 WHERE r2.jugador_id = j.id 
                 AND r2.partida_id >= (
                     SELECT MAX(partida_id) FROM resultados r3 
                     WHERE r3.jugador_id = j.id 
                     AND (r3.posicion != 1 OR r3.posicion = 1)
                 ) - 10
                 AND r2.posicion = 1
                ) as racha_victorias
            FROM jugadores j
            JOIN resultados r ON j.id = r.jugador_id
            GROUP BY j.id, j.nombre
            ORDER BY victorias DESC, porcentaje DESC
        """;

        try (Connection conn = ConexionBD.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] fila = new Object[6];
                fila[0] = rs.getString("nombre");
                fila[1] = rs.getInt("partidas");
                fila[2] = rs.getInt("victorias");
                fila[3] = rs.getDouble("porcentaje") + "%";
                fila[4] = rs.getDouble("posicion_promedio");
                fila[5] = rs.getInt("racha_victorias") + "🔥"; // Emoji de fuego para la racha
                ranking.add(fila);
            }
        } catch (Exception e) {
            System.err.println("❌ Error al obtener ranking: " + e.getMessage());
        }
        return ranking;
    }
}