package ar.edu.utn.d2s.exceptions;

public class ExceptionPlanificacionInexistente extends Exception{

	private static final long serialVersionUID = 1L;

	public ExceptionPlanificacionInexistente() {
		super("La planificacion no existe");
	}
}