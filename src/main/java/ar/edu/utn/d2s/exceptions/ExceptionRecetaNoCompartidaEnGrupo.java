package ar.edu.utn.d2s.exceptions;

public class ExceptionRecetaNoCompartidaEnGrupo extends Exception {

	private static final long serialVersionUID = 1L;

	public ExceptionRecetaNoCompartidaEnGrupo() {
		super("La receta no estaba compartida en el grupo");
	}
}