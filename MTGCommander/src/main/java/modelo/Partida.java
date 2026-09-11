package modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Partida {
	
	private int id;
	private LocalDateTime fecha;
	private int duracionMinutos;
	private List<ResultadoJugador> resultados;
	private String notas;
	
	public Partida(int id, LocalDateTime fecha, int duracionMinutos, String notas) {
		this.id = id;
		this.fecha = fecha;
		this.duracionMinutos = duracionMinutos;
		this.resultados = new ArrayList<>();
		this.notas = notas != null ? notas : "";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	public int getDuracionMinutos() {
		return duracionMinutos;
	}

	public void setDuracionMinutos(int duracionMinutos) {
		this.duracionMinutos = duracionMinutos;
	}

	public List<ResultadoJugador> getResultados() {
		return resultados;
	}

	public void setResultados(List<ResultadoJugador> resultados) {
		this.resultados = resultados;
	}

	public String getNotas() {
		return notas;
	}

	public void setNotas(String notas) {
		this.notas = notas;
	}
	
	public void añadirResultado(ResultadoJugador resultado) {
		this.resultados.add(resultado);
	}
	
	public ResultadoJugador obtenerGanador() {
		for (ResultadoJugador r : this.resultados) {
			if (r.getPosicionFinal() == 1) {
				return r;
			}
		}
		return null;
	}	
}
