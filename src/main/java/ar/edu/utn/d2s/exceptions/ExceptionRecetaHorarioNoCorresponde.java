package ar.edu.utn.d2s.exceptions;

public class ExceptionRecetaHorarioNoCorresponde extends Exception{

	private static final long serialVersionUID = 1L;

	public ExceptionRecetaHorarioNoCorresponde() {
		super("La receta no se puede planificar en ese horario de comida");
	}
}