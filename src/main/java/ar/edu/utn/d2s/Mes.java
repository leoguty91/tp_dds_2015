package ar.edu.utn.d2s;

public enum Mes {
	ENERO(0), FEBRERO(1), MARZO(2), ABRIL(3), MAYO(4), JUNIO(5), JULIO(6), AGOSTO(7), SEPTIEMBRE(8), OCTUBRE(9), NOVIEMBRE(10), DICIEMBRE(11);

	private int mes;

	private Mes(int mes) {
		setMes(mes);
	}

	public int getMes() {
		return mes;
	}

	private void setMes(int mes) {
		this.mes = mes;
	}
}