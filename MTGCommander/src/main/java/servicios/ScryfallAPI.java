package servicios;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ScryfallAPI {

    /**
     * Busca una carta por nombre en la API de Scryfall
     * @param nombreBuscado Nombre de la carta (ej: "Atraxa")
     * @return Un objeto JsonObject con todos los datos de la carta, o null si no se encuentra
     */
	public static JsonObject buscarComandante(String nombreBuscado) {
	    try {
	        // 1. Creamos el cliente HTTP
	        HttpClient client = HttpClient.newHttpClient();
	        
	        // 2. Codificamos el nombre correctamente para URLs
	        String nombreCodificado = java.net.URLEncoder.encode(nombreBuscado, "UTF-8");
	        
	        // 3. Construimos la URL de Scryfall
	        String url = "https://api.scryfall.com/cards/named?fuzzy=" + nombreCodificado;
	        
	        // 4. Creamos la petición con las DOS cabeceras requeridas
	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(url))
	                .header("User-Agent", "MTG-Commander-Tracker/1.0")
	                .header("Accept", "application/json")  // ¡Esta es la que faltaba!
	                .build();
	        
	        // 5. Enviamos la petición y recibimos la respuesta
	        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	        
	        // 6. Si todo fue bien (código 200), convertimos el JSON a objeto Java
	        if (response.statusCode() == 200) {
	            return JsonParser.parseString(response.body()).getAsJsonObject();
	        } else {
	            System.out.println("❌ Error al buscar: " + response.statusCode());
	            System.out.println("Respuesta del servidor: " + response.body());
	            return null;
	        }
	    } catch (Exception e) {
	        System.out.println("❌ Error de conexión: " + e.getMessage());
	        e.printStackTrace();
	        return null;
	    }
	}

    /**
     * Extrae la URL de la imagen de alta calidad de la carta
     */
	public static String obtenerUrlImagen(JsonObject carta) {
	    if (carta == null) return null;
	    try {
	        // Usamos "art_crop" para obtener SOLO la ilustración, sin marco ni texto
	        return carta.getAsJsonObject("image_uris").get("art_crop").getAsString();
	    } catch (Exception e) {
	        // Si no tiene art_crop (cartas muy antiguas), usamos "normal" como fallback
	        try {
	            return carta.getAsJsonObject("image_uris").get("normal").getAsString();
	        } catch (Exception e2) {
	            return null;
	        }
	    }
	}
    
    /**
     * Extrae los colores de identidad de la carta
     */
    public static String obtenerColores(JsonObject carta) {
        if (carta == null) return "";
        try {
            // Scryfall devuelve un array de colores ["W", "B", "R"]
            StringBuilder colores = new StringBuilder();
            for (var color : carta.getAsJsonArray("color_identity")) {
                colores.append(color.getAsString());
            }
            return colores.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Método de prueba - LO EJECUTAREMOS PARA VERIFICAR QUE FUNCIONA
     */
    public static void main(String[] args) {
        System.out.println("🔍 Buscando a Atraxa en Scryfall...");
        
        JsonObject carta = buscarComandante("Aesi, Tyrant of Gyre Strait");	
        
        if (carta != null) {
            System.out.println("✅ ¡Encontrada!");
            System.out.println(" Nombre real: " + carta.get("name").getAsString());
            System.out.println(" Colores: " + obtenerColores(carta));
            System.out.println("🖼️ URL Imagen: " + obtenerUrlImagen(carta));
        } else {
            System.out.println("❌ No se encontró la carta.");
        }
    }
}