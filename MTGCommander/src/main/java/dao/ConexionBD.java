package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase encargada de gestionar la conexión con la base de datos SQLite.
 * La base de datos se guardará en un archivo local llamado "mtg_commander.db".
 */
public class ConexionBD {

    // Ruta del archivo de base de datos (en la raíz del proyecto)
    private static final String RUTA_BD = "mtg_commander.db";
    
    // URL de conexión a SQLite
    private static final String URL = "jdbc:sqlite:" + RUTA_BD;
    
    // Variable para mantener una única conexión (patrón Singleton simplificado)
    private static Connection conexion = null;

    /**
     * Obtiene la conexión con la base de datos.
     * Si no existe, la crea junto con las tablas necesarias.
     */
    public static Connection getConexion() {
        try {
            // Si ya tenemos una conexión abierta y válida, la reutilizamos
            if (conexion != null && !conexion.isClosed()) {
                return conexion;
            }
            
            // Si no, creamos una nueva
            conexion = DriverManager.getConnection(URL);
            System.out.println("✅ Conexión con la base de datos establecida: " + RUTA_BD);
            
            // Activar las claves foráneas (importante para integridad de datos)
            Statement stmt = conexion.createStatement();
            stmt.execute("PRAGMA foreign_keys = ON;");
            
            // Crear las tablas si no existen
            crearTablas();
            
            // 🟢 NUEVO: Migrar tablas existentes para añadir columnas nuevas
            migrarTablas();
            
            return conexion;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar con la base de datos: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Crea las tablas necesarias si no existen.
     */
    private static void crearTablas() {
        try {
            Statement stmt = conexion.createStatement();
            
            // Tabla de JUGADORES (sin avatar_path, lo añadimos en migrarTablas)
            String sqlJugadores = """
                CREATE TABLE IF NOT EXISTS jugadores (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL UNIQUE,
                    color_favorito TEXT,
                    comandante_nombre TEXT,
                    comandante_imagen_url TEXT
                )
            """;
            stmt.execute(sqlJugadores);
            
            // Tabla de PARTIDAS
            String sqlPartidas = """
                CREATE TABLE IF NOT EXISTS partidas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    fecha TEXT NOT NULL,
                    duracion_minutos INTEGER,
                    notas TEXT
                )
            """;
            stmt.execute(sqlPartidas);
            
            // Tabla de RESULTADOS (relaciona partidas con jugadores)
            String sqlResultados = """
                CREATE TABLE IF NOT EXISTS resultados (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    partida_id INTEGER NOT NULL,
                    jugador_id INTEGER NOT NULL,
                    posicion INTEGER NOT NULL,
                    tipo_resultado TEXT,
                    FOREIGN KEY (partida_id) REFERENCES partidas(id) ON DELETE CASCADE,
                    FOREIGN KEY (jugador_id) REFERENCES jugadores(id) ON DELETE CASCADE
                )
            """;
            stmt.execute(sqlResultados);
            
            System.out.println("✅ Tablas de la base de datos listas (jugadores, partidas, resultados)");
            
        } catch (SQLException e) {
            System.err.println("❌ Error al crear las tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 🟢 NUEVO: Método de migración para añadir columnas nuevas a tablas existentes.
     * Se ejecuta cada vez que se abre la app, pero solo hace cambios si faltan columnas.
     */
    private static void migrarTablas() {
        try {
            Statement stmt = conexion.createStatement();
            
            // Intentamos añadir la columna avatar_path a la tabla jugadores
            // Si ya existe, SQLite lanzará un error que capturamos e ignoramos
            try {
                stmt.execute("ALTER TABLE jugadores ADD COLUMN avatar_path TEXT");
                System.out.println("✅ Columna 'avatar_path' añadida a la tabla jugadores");
            } catch (SQLException e) {
                // La columna ya existe, no hacemos nada
                System.out.println("ℹ️ La columna 'avatar_path' ya existe en jugadores");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error en la migración de tablas: " + e.getMessage());
        }
    }

    /**
     * Cierra la conexión con la base de datos.
     * Se debe llamar al cerrar la aplicación.
     */
    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("🔒 Conexión con la base de datos cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al cerrar la conexión: " + e.getMessage());
        }
    }
    
    /**
     * Método de prueba para verificar que la conexión funciona.
     */
    public static void main(String[] args) {
        System.out.println("🧪 Probando conexión con SQLite...");
        
        Connection conn = getConexion();
        
        if (conn != null) {
            System.out.println("✅ ¡Conexión exitosa!");
            System.out.println("📁 El archivo de base de datos se ha creado en: " + 
                java.nio.file.Paths.get("").toAbsolutePath().toString() + "/" + RUTA_BD);
            cerrarConexion();
        } else {
            System.out.println("❌ No se pudo conectar.");
        }
    }
}