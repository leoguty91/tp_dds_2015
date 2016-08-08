package ar.edu.utn.d2s.exceptions;

public class ExceptionPlanificacionSuperior7Dias extends Exception{

	private static final long serialVersionUID = 1L;

	public ExceptionPlanificacionSuperior7Dias() {
		super("Sólo puede agregar / modificar recetas en un rango menor a una semana");
	}
}