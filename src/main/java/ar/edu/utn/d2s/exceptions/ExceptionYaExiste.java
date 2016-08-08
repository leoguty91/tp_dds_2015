package ar.edu.utn.d2s.exceptions;

public class ExceptionYaExiste extends Exception{

	private static final long serialVersionUID = 1L;

	public ExceptionYaExiste(String msg) {
		super("La clase: " + msg + " ya existe");
	}
}