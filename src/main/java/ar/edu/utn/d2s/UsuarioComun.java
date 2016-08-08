package ar.edu.utn.d2s;

import javax.persistence.Entity;

import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;

@Entity
public class UsuarioComun extends Usuario {

	public UsuarioComun() {
		super();
	}

	public UsuarioComun(String nombre, String mail, int edad)
			throws ExceptionValidacionDatos {
		super(nombre, mail, edad);
	}
}