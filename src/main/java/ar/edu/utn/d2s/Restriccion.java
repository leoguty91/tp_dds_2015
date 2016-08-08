package ar.edu.utn.d2s;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class Restriccion implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "RESTRICCION_ID")
	private String nombre;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "INGREDIENTE_ID")
	private Ingrediente restriccion;
	
	protected Restriccion() {
		super();
	}
	
	public Restriccion(String n, Ingrediente i) {
		setNombre(n);
		setRestriccion(i);
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Ingrediente getRestriccion() {
		return restriccion;
	}

	public Ingrediente getIngrediente() {
		return restriccion;
	}

	public void setRestriccion(Ingrediente restriccion) {
		this.restriccion = restriccion;
	}

	public String getRestriccionIngrediente(){
		return restriccion.getNombre();
	}

	@Override
    public boolean equals (Object o) {
		if (o == this) return true;
		if (!(o instanceof Restriccion)) return false;
		Restriccion i = (Restriccion) o;
		return (this.nombre.equals(i.getNombre()));
	}
 
	@Override
	public int hashCode() {
		return (int) nombre.hashCode();
	}
}