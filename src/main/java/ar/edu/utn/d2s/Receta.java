package ar.edu.utn.d2s;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ar.edu.utn.d2s.exceptions.ExceptionIngredienteRestringido;
import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

@Entity
public class Receta implements Cloneable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Transient
	private static final int INGREDIENTESMINIMOS = 2, CALORIASMINIMAS = 0;
	
	@Transient
	private static final String CADENAVACIA = "";

	@Column(name = "NOMBRE")
	private String nombre;

	@Column(name = "PROCEDIMIENTO")
	private String procedimiento;

	@Column(name = "CALORIAS")
	private int calorias;

	@Column(name = "COPIAS")
	private long copias = 0;

	@Column(name = "VISIBILIDAD")
	private boolean visibilidad = false;

	@Enumerated(EnumType.STRING)
	@Column(name = "DIFICULTAD")
	private Dificultad dificultad;

	@Enumerated(EnumType.STRING)
	@Column(name = "TEMPORADA")
	private Temporada temporada;

	@OneToOne(targetEntity = ar.edu.utn.d2s.Usuario.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "AUTOR")
	private Usuario autor;

	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "RECETA_INGREDIENTE",
			joinColumns = @JoinColumn(name = "RECETA_ID"),
			inverseJoinColumns = @JoinColumn(name = "INGREDIENTE_ID")
	)
	private List<Ingrediente> ingredientes = new ArrayList<Ingrediente>();

	@LazyCollection(LazyCollectionOption.FALSE)
	@Column(name = "TipoAlimentos")
	@Enumerated(EnumType.STRING)
	@ElementCollection(targetClass = TipoComida.class)
	private List<TipoComida> tipoComidas = new ArrayList<TipoComida>();
	
	public Receta() {
		super();
	}

	public Receta(String nombre, List<Ingrediente> ingredientes, String procedimiento, Dificultad dificultad,
			Temporada temporadaRecetario, int calorias) throws ExceptionValidacionDatos {
		setNombre(nombre);
		setIngredientes(ingredientes);
		setProcedimiento(procedimiento);
		setDificultad(dificultad);
		setTemporada(temporadaRecetario);
		setCalorias(calorias);
	}

	public void agregarIngrediente(Ingrediente ingrediente) throws ExceptionYaExiste {
		if (contieneIngrediente(ingrediente)) {
			throw new ExceptionYaExiste("Ingrediente." + ingrediente.getNombre());
		}
		ingredientes.add(ingrediente);
	}

	public void agregarTipoComida(TipoComida tipoComida) throws ExceptionYaExiste {
		if (contieneTipoComida(tipoComida)) {
			throw new ExceptionYaExiste("TipoComida." + tipoComida);
		}
		tipoComidas.add(tipoComida);
	}

	public boolean contieneIngrediente(Ingrediente ingrediente) {
		return ingredientes.contains(ingrediente);
	}

	public boolean contieneTipoComida(TipoComida tipoComida) {
		return tipoComidas.contains(tipoComida);
	}

	public boolean contieneTipoAlimento(TipoAlimento tipoAlimento) {
		return ingredientes.stream()
				.anyMatch(i -> i.getTipoAlimento().equals(tipoAlimento));
	}

	public void eliminaIngrediente(Ingrediente ingrediente) {
		ingredientes.removeIf(i -> i.equals(ingrediente));
	}

	public int getCantidadTipoAlimento(TipoAlimento tipoAlimento) {
		int cantidad = 0;
		cantidad += ingredientes.stream()
				.filter(i -> i.getTipoAlimento().equals(tipoAlimento))
				.count();
		return cantidad;
	}

	public void modificarReceta(String nombre, List<Ingrediente> ingredientes, String procedimiento, Dificultad dificultad, Temporada temporadaRecetario, int calorias) throws ExceptionValidacionDatos {
		setNombre(nombre);
		setIngredientes(ingredientes);
		setProcedimiento(procedimiento);
		setDificultad(dificultad);
		setTemporada(temporadaRecetario);
		setCalorias(calorias);
	}

	public void setNombre(String nombre) throws ExceptionValidacionDatos {
		if (nombre.equals(CADENAVACIA)) {
			throw new ExceptionValidacionDatos("Ingrese un nombre para la receta");
		}
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public void setProcedimiento(String procedimiento) throws ExceptionValidacionDatos {
		if (procedimiento.equals(CADENAVACIA)) {
			throw new ExceptionValidacionDatos("Ingrese un procedmiento válido");
		}
		this.procedimiento = procedimiento;
	}

	public String getProcedimiento() {
		return procedimiento;
	}

	public void setDificultad(Dificultad dificultad) throws ExceptionValidacionDatos {
		if (dificultad.toString().equals(CADENAVACIA)) {
			throw new ExceptionValidacionDatos("Ingrese una dificultad válida");
		}
		this.dificultad = dificultad;
	}

	public Dificultad getDificultad() {
		return dificultad;
	}

	public void setTemporada(Temporada temporadaRecetario) throws ExceptionValidacionDatos {
		if (temporadaRecetario.toString().equals(CADENAVACIA)) {
			throw new ExceptionValidacionDatos("Ingrese una temporada de recetario");
		}
		this.temporada = temporadaRecetario;
	}

	public Temporada getTemporada() {
		return temporada;
	}

	public void setCalorias(int calorias) throws ExceptionValidacionDatos {
		if (calorias == CALORIASMINIMAS) {
			throw new ExceptionValidacionDatos("Las calorias totales no pueden ser iguales a 0");
		}
		this.calorias = calorias;
	}

	public int getCalorias() {
		return calorias;
	}

	public List<Ingrediente> getIngredientes() {
		return ingredientes;
	}

	public void setIngredientes(List<Ingrediente> ingredientes) throws ExceptionValidacionDatos {
		if (ingredientes.size() < INGREDIENTESMINIMOS) {
			throw new ExceptionValidacionDatos("La lista de ingredientes tiene que ser mayor o igual a 2");
		}
		this.ingredientes = ingredientes;
	}

	public Usuario getAutor() {
		return autor;
	}

	public void setAutor(Usuario autor) throws ExceptionYaExiste, ExceptionIngredienteRestringido {
		this.autor = autor;
		autor.agregarReceta(this);
	}

	public void sumarRecetaCopiada() {
		copias ++;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setCopias(long copias) {
		this.copias = copias;
	}

	public long getCopias() {
		return copias;
	}

	public void setVisibilidad(boolean visibilidad) {
		this.visibilidad = visibilidad;
	}

	public boolean getVisibilidad() {
		return visibilidad;
	}

	@Override
	protected Receta clone() throws CloneNotSupportedException {
		Receta recetaClonada = (Receta) super.clone();
		recetaClonada.setId(0);
		recetaClonada.setCopias(0);
		recetaClonada.ingredientes = new ArrayList<Ingrediente>(ingredientes);
		recetaClonada.tipoComidas = new ArrayList<TipoComida>(tipoComidas);
		recetaClonada.setVisibilidad(false);
		return recetaClonada;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Receta)) return false;
		Receta r = (Receta) o;
		return (nombre.equals(r.getNombre()) && autor.equals(r.getAutor()));
	}

	@Override
	public int hashCode() {
		return (int) nombre.hashCode() * autor.hashCode();
	}
}