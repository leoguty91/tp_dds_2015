package ar.edu.utn.d2s;

public enum Semana {
	PRIMERA(1, 7), SEGUNDA(8, 14), TERCERA(15, 21), CUARTA(22, 28), QUINTA(29, 31);

	private int desde, hasta;

	private Semana(int desde, int hasta) {
		setDesde(desde);
		setHasta(hasta);
	}

	public int getDesde() {
		return desde;
	}

	private void setDesde(int desde) {
		this.desde = desde;
	}

	public int getHasta() {
		return hasta;
	}

	private void setHasta(int hasta) {
		this.hasta = hasta;
	}
}
