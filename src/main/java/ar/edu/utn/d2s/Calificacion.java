package ar.edu.utn.d2s;

import javax.persistence.*;

import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;

@Entity
public class Calificacion {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Transient
	private static final int CALIFICACIONMINIMA = 1, CALIFICACIONMAXIMA = 5;

	@OneToOne(targetEntity = ar.edu.utn.d2s.Receta.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "RECETA_ID")
	private Receta receta;

	@OneToOne(targetEntity = ar.edu.utn.d2s.Usuario.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "USUARIO_EMAIL")
	private Usuario usuario;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "GRUPO_NOMBRE")
	private Grupo grupo;

	public Grupo getGrupo() {
		return grupo;
	}

	public void setGrupo(Grupo g) {
		grupo = g;
	}

	@Column(name = "CALIFICACION")
	private int calificacion;

	protected Calificacion() {
		super();
	}

	public Calificacion(Usuario unUsuario, Receta receta, int unaCalificacion, Grupo grupo) throws ExceptionValidacionDatos {
		setUsuario(unUsuario);
		setReceta(receta);
		setCalificacion(unaCalificacion);
		setGrupo(grupo);
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public int getCalificacion() {
		return calificacion;
	}

	public void setCalificacion(int calificacion) throws ExceptionValidacionDatos {
		if (calificacion < CALIFICACIONMINIMA || calificacion > CALIFICACIONMAXIMA) {
			throw new ExceptionValidacionDatos("Ingrese una calificación entre 1 a 5");
		}
		this.calificacion = calificacion;
	}

	public Receta getReceta() {
		return receta;
	}

	public void setReceta(Receta receta) {
		this.receta = receta;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Calificacion)) return false;
		Calificacion g = (Calificacion) o;
		return (usuario.equals(g.getUsuario()) &&
				receta.equals(g.getReceta()));
	}

	@Override
	public int hashCode() {
		return (int) usuario.hashCode() * receta.hashCode();
	}

	public int getId() {
		return id;
	}
}
