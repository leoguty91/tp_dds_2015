package ar.edu.utn.d2s.exceptions;

public class ExceptionUsuarioNoPertenceAlGrupo extends Exception {

	private static final long serialVersionUID = 1L;
	
	public ExceptionUsuarioNoPertenceAlGrupo() {
		super("El usuario no pertenece al grupo");
	}
}