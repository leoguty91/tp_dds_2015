 package ar.edu.utn.d2s;

import javax.persistence.*;

@Entity
public class Ingrediente {
	 
	@Id
	private String nombre;

	@Enumerated(EnumType.STRING)
	private TipoAlimento tipoAlimento;
	
	public Ingrediente() {
		super();
	}

	public Ingrediente(String n, TipoAlimento ta) {
		setNombre(n);
		setTipoAlimento(ta);
	}

	public Ingrediente(String ing) {
		this.setNombre(ing);
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public TipoAlimento getTipoAlimento() {
		return tipoAlimento;
	}

	public void setTipoAlimento(TipoAlimento tipoAlimento) {
		this.tipoAlimento = tipoAlimento;
	}

	public String toString(){
		return nombre;
	}

	@Override
    public boolean equals (Object o) {
		if (o == this) return true;
		if (!(o instanceof Ingrediente)) return false;
		Ingrediente i = (Ingrediente) o;
		return (this.nombre.equals(i.getNombre()));
    }

	@Override
	public int hashCode() {
		return (int) nombre.hashCode();
	}
}