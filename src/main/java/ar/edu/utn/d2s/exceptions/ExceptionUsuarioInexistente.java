package ar.edu.utn.d2s.exceptions;

public class ExceptionUsuarioInexistente extends Exception {

	private static final long serialVersionUID = 1L;

	public ExceptionUsuarioInexistente() {
		super("No existe el usuario");
	}
}