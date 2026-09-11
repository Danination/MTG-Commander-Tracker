package modelo;

public class Comandante {
	
	private String nombre;
	private String colores;
	private String costeMana;
	private int cmc;			//cmc es coste de mana convertido
	private String rutaImagen;
	
	public Comandante(String nombre, String colores, String costeMana, int cmc) {
		this.nombre = nombre;
		this.colores = colores;
		this.costeMana = costeMana;
		this.cmc = cmc;	
		this.rutaImagen = null;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getColores() {
		return colores;
	}

	public void setColores(String colores) {
		this.colores = colores;
	}

	public String getCosteMana() {
		return costeMana;
	}

	public void setCosteMana(String costeMana) {
		this.costeMana = costeMana;
	}

	public int getCmc() {
		return cmc;
	}

	public void setCmc(int cmc) {
		this.cmc = cmc;
	}

	public String getRutaImagen() {
		return rutaImagen;
	}

	public void setRutaImagen(String rutaImagen) {
		this.rutaImagen = rutaImagen;
	}
	
	public boolean tieneColor(String colorBuscado) {
		if (this.colores == null) {
			return false;
	}
		return this.colores.contains(colorBuscado);
		
		}

	@Override
	public String toString() {
	    return nombre + " (" + colores + ") - CMC: " + cmc;
		}
	}

//Dani