package ar.edu.utn.d2s.exceptions;

public class ExceptionGrupoInexistente extends Exception {

	private static final long serialVersionUID = 1L;

	public ExceptionGrupoInexistente() {
		super("No existe el grupo");
	}
}