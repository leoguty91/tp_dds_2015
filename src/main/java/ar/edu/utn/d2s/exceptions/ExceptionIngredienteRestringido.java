package ar.edu.utn.d2s.exceptions;

public class ExceptionIngredienteRestringido extends Exception{

	private static final long serialVersionUID = 1L;

	public ExceptionIngredienteRestringido() {
		super("La receta contiene un elemento restringido");
	}
}