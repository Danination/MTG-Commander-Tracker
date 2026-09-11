package modelo;

public class ResultadoJugador {
	
	private Jugador jugador;
	private Comandante comandanteUsado;
	private int posicionFinal;
	private String condicionEliminacion;
	
	public ResultadoJugador(Jugador jugador, Comandante comandanteUsado, int posicionFinal, String condicionEliminacion) {
		this.jugador = jugador;
		this.comandanteUsado = comandanteUsado;
		this.posicionFinal = posicionFinal;
		this.condicionEliminacion = condicionEliminacion;
	}

	public Jugador getJugador() {
		return jugador;
	}

	public void setJugador(Jugador jugador) {
		this.jugador = jugador;
	}

	public Comandante getComandanteUsado() {
		return comandanteUsado;
	}

	public void setComandanteUsado(Comandante comandanteUsado) {
		this.comandanteUsado = comandanteUsado;
	}

	public int getPosicionFinal() {
		return posicionFinal;
	}

	public void setPosicionFinal(int posicionFinal) {
		this.posicionFinal = posicionFinal;
	}

	public String getCondicionEliminacion() {
		return condicionEliminacion;
	}

	public void setCondicionEliminacion(String condicionEliminacion) {
		this.condicionEliminacion = condicionEliminacion;
	}
}
