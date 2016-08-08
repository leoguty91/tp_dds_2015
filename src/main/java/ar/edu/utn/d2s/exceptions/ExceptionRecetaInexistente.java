package ar.edu.utn.d2s.exceptions;

public class ExceptionRecetaInexistente extends Exception {

	private static final long serialVersionUID = 1L;

	public ExceptionRecetaInexistente() {
		super("La receta no existe");
	}
}