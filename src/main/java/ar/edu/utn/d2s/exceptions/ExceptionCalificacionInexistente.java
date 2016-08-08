package ar.edu.utn.d2s.exceptions;

public class ExceptionCalificacionInexistente extends Exception{

	private static final long serialVersionUID = 1L;

	public ExceptionCalificacionInexistente() {
		super("La calificacion no existe");
	}
}
