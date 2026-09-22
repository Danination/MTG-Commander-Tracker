package modelo;

import java.util.ArrayList;
import java.util.List;

public class Jugador {
	
	private int id;
	private String nombre;
	private String colorFavorito;
	private String fotoPerfil;
	private String fotoDaño;
	private String fotoGanarVida;
	private String fotoVictoria;
	private List<Comandante> comandantes;
	private List<Partida> partidasJugadas;
	private String comandanteNombre;
	private String comandanteImagenUrl;
	private String avatarPath;
	
	public Jugador(int id, String nombre, String colorFavorito) {
		this.id = id;
		this.nombre = nombre;
		this.colorFavorito = colorFavorito;
		this.comandantes = new ArrayList<>();
		this.partidasJugadas = new ArrayList<>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getColorFavorito() {
		return colorFavorito;
	}

	public void setColorFavorito(String colorFavorito) {
		this.colorFavorito = colorFavorito;
	}

	public String getFotoPerfil() {
		return fotoPerfil;
	}

	public void setFotoPerfil(String fotoPerfil) {
		this.fotoPerfil = fotoPerfil;
	}

	public String getFotoDaño() {
		return fotoDaño;
	}

	public void setFotoDaño(String fotoDaño) {
		this.fotoDaño = fotoDaño;
	}

	public String getFotoGanarVida() {
		return fotoGanarVida;
	}

	public void setFotoGanarVida(String fotoGanarVida) {
		this.fotoGanarVida = fotoGanarVida;
	}

	public String getFotoVictoria() {
		return fotoVictoria;
	}

	public void setFotoVictoria(String fotoVictoria) {
		this.fotoVictoria = fotoVictoria;
	}

	public List<Comandante> getComandantes() {
		return comandantes;
	}

	public void setComandantes(List<Comandante> comandantes) {
		this.comandantes = comandantes;
	}

	public List<Partida> getPartidasJugadas() {
		return partidasJugadas;
	}

	public void setPartidasJugadas(List<Partida> partidasJugadas) {
		this.partidasJugadas = partidasJugadas;
	}

	public String getComandanteNombre() {
		return comandanteNombre;
	}

	public void setComandanteNombre(String comandanteNombre) {
		this.comandanteNombre = comandanteNombre;
	}

	public String getComandanteImagenUrl() {
		return comandanteImagenUrl;
	}

	public void setComandanteImagenUrl(String comandanteImagenUrl) {
		this.comandanteImagenUrl = comandanteImagenUrl;
	}
	
	public String getAvatarPath() {
        return avatarPath;
    }
    
    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

	@Override
	public String toString() {
	    return nombre + " (" + colorFavorito + ")";	
	}		
}
