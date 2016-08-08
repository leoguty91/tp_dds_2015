package ar.edu.utn.d2s;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.*;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ar.edu.utn.d2s.exceptions.ExceptionGrupoInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionIngredienteRestringido;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaHorarioNoCorresponde;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaNoCompartidaEnGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioNoPertenceAlGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

@Entity
public abstract class Usuario {

	@Transient
	private static final int EDADMINIMA = 18;
	@Transient
	private static final String CADENAVACIA = "";

	@Column(name = "NOMBRE")
	private String nombre;

	@Id
	@Column(name = "EMAIL")
	private String mail;

	@Column(name = "PASSWORD")
	private String password;
	
	@Column(name = "EDAD")
	private int edad;

	@Column(name = "ALTURA")
	private String altura;

	@Enumerated(EnumType.STRING)
	@Column(name = "SEXO")
	private Sexo sexo;

	@Column(name = "RUTINA")
	private int rutina;

	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(targetEntity = ar.edu.utn.d2s.Restriccion.class, cascade = CascadeType.ALL)
	@JoinTable(
            name = "RESTRICCION_USUARIO",
            joinColumns = @JoinColumn(name = "EMAIL"),
            inverseJoinColumns = @JoinColumn(name = "NOMBRE")
    )
	private List<Restriccion> restricciones = new ArrayList<Restriccion>();

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "usuario")
	private List<Planificacion> planificaciones = new ArrayList<Planificacion>();

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "RECETA_USUARIO",
			joinColumns = @JoinColumn(name = "USUARIO_EMAIL"),
			inverseJoinColumns = @JoinColumn(name = "RECETA_ID")
	)
	private List<Receta> recetas = new ArrayList<Receta>();

	@ManyToMany(fetch = FetchType.EAGER, mappedBy = "usuarios")
	private List<Grupo> grupos = new ArrayList<Grupo>();

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(targetEntity = ar.edu.utn.d2s.Ingrediente.class, cascade = CascadeType.ALL)
	@JoinTable(
            name = "PREFERENCIAS_USUARIO",
            joinColumns = @JoinColumn(name = "EMAIL"),
            inverseJoinColumns = @JoinColumn(name = "INGREDIENTE_ID")
    )
	private List<Ingrediente> preferencias = new ArrayList<Ingrediente>();

	@Transient
	private Reportes reporte;

	@Transient
	private Estadisticas estadistica;

	@OneToOne(targetEntity = ar.edu.utn.d2s.PiramideAlimenticia.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "PiramideAlimenticia")
	private PiramideAlimenticia piramide;

	protected Usuario() {
		super();
	}

	public Usuario(String nombre, String mail, int edad) throws ExceptionValidacionDatos {
		setNombre(nombre);
		setMail(mail);
		setEdad(edad);
	}

	public void agregarFiltroReporte(FiltroStrategy filtro) throws ExceptionYaExiste {
		if (contieneFiltroReporte(filtro)) {
			throw new ExceptionYaExiste("Filtro." + filtro.getClass().getName());
		}
		reporte.agregarFiltro(filtro);
	}

	public void agregarPlanificacion(Planificacion planificacion) throws ExceptionRecetaHorarioNoCorresponde, ExceptionIngredienteRestringido {
		if (contieneIngredienteRestringido(planificacion.getReceta())) {
			throw new ExceptionIngredienteRestringido();
		}
		planificacion.setUsuario(this);
		planificaciones.removeIf(p -> p.getDia().equals(planificacion.getDia()) && p.getTipoComida().equals(planificacion.getTipoComida()));
		planificaciones.add(planificacion);
	}

	public void agregarPreferencia(Ingrediente preferencia) throws ExceptionYaExiste {
		if (contienePreferencia(preferencia)) {
			throw new ExceptionYaExiste("Preferencia." + preferencia.getNombre());
		}
		preferencias.add(preferencia);
	}

	public void agregarReceta(Receta receta) throws ExceptionYaExiste, ExceptionIngredienteRestringido {
		if (contieneReceta(receta)) {
			throw new ExceptionYaExiste("Receta." + receta.getNombre());
		}
		if (contieneIngredienteRestringido(receta)) {
			throw new ExceptionIngredienteRestringido();
		}
		recetas.add(receta);

	}

	public void agregarRestriccion(Restriccion restriccion) throws ExceptionYaExiste {
		if (contieneRestriccion(restriccion)) {
			throw new ExceptionYaExiste("Restriccion." + restriccion.getNombre());
		}
		restricciones.add(restriccion);
	}

	public void calificarReceta(Grupo grupo, Receta receta, int calificacion) throws ExceptionValidacionDatos, ExceptionUsuarioNoPertenceAlGrupo {
		if (!contieneAlGrupo(grupo)) {
			throw new ExceptionUsuarioNoPertenceAlGrupo();
		}
		grupo.agregarCalificacion(this, receta, calificacion);
	}

	public void configurarRepositorios(List<Grupo> grupos, List<Usuario> usuarios, List<Receta> recetas) {
		reporte = new Reportes();
		reporte.configurarRepositorios(grupos, usuarios, recetas);
		estadistica = new Estadisticas();
		estadistica.configurarRepositorios(usuarios, recetas);
	}

	public String consultarPlanificacion(Calendar dia, TipoComida tipoComida) throws ExceptionValidacionDatos {
		String planificacion = "Nada";
		if (contienePlanificacion(dia, tipoComida)) {
			planificacion = planificaciones
					.stream()
					.filter(p ->
					p.getDia().equals(dia) &&
					p.getTipoComida().equals(tipoComida))
					.map(p -> p.getReceta().getNombre())
					.findFirst().get();
		}
		return planificacion;
	}

	public boolean contieneAlGrupo(Grupo grupo) {
		return grupos.contains(grupo);
	}

	private boolean contieneFiltroReporte(FiltroStrategy filtro) {
		return reporte.contieneFiltro(filtro);
	}

	public boolean contieneIngredienteRestringido(Receta receta) {
		return restricciones
				.stream()
				.anyMatch(r -> receta.contieneIngrediente(r.getIngrediente()));
	}

	private boolean contienePlanificacion(Calendar dia, TipoComida tipoComida) {
		return planificaciones
				.stream()
				.anyMatch(p ->
				p.getDia().equals(dia) &&
				p.getTipoComida().equals(tipoComida));
	}

	public boolean contienePreferencia(Ingrediente preferencia) {
		return preferencias.contains(preferencia);
	}

	public boolean contieneReceta(Receta receta) {
		return recetas.contains(receta);
	}

	public boolean contieneRestriccion(Restriccion restriccion) {
		return restricciones.contains(restriccion);
	}

	public void compartirReceta(Grupo grupo, Receta receta) throws ExceptionYaExiste, ExceptionValidacionDatos, ExceptionUsuarioNoPertenceAlGrupo {
		if (!contieneReceta(receta)) {
			throw new ExceptionValidacionDatos("El usuario no contiene la receta");
		}
		if (!contieneAlGrupo(grupo)) {
			throw new ExceptionUsuarioNoPertenceAlGrupo();
		}
		grupo.agregarReceta(receta);
	}

	public void eliminarPlanificacion(Planificacion planificacion){
		planificaciones.removeIf(p -> p.equals(planificacion));
	}

	public void eliminarReceta(Receta receta) throws ExceptionRecetaNoCompartidaEnGrupo, ExceptionValidacionDatos  {
		if (!contieneReceta(receta)) {
			throw new ExceptionValidacionDatos("El usuario no contiene la receta");
		}
		for (Grupo grupo : grupos) {
			if (grupo.contieneReceta(receta)) {
				grupo.eliminarReceta(receta);
			}
		}
		recetas.removeIf(r -> r.equals(receta));
	}

	public Receta generarRecetaAPartirDeOtra(Receta receta) throws CloneNotSupportedException, ExceptionValidacionDatos, ExceptionYaExiste, ExceptionIngredienteRestringido {
		Receta recetaGenerada = receta.clone();
		receta.sumarRecetaCopiada();
		recetaGenerada.setAutor(this);
		return recetaGenerada;
	}

	public void generarRecetaNueva(String nombre, List<Ingrediente> ingredientes, String procedimiento, Dificultad dificultad, Temporada temporadaRecetario, int calorias) throws ExceptionValidacionDatos, ExceptionYaExiste, ExceptionIngredienteRestringido {
		Receta recetaNueva = new Receta(nombre, ingredientes, procedimiento, dificultad, temporadaRecetario, calorias);
		recetaNueva.setAutor(this);
	}

	protected Estadisticas getEstadistica() {
		return estadistica;
	}

	public Map<Receta, Long> getEstadisticasPorEstacionYMes(Mes mes, Temporada temporada) {
		return new HashMap<Receta, Long>();
	}

	public Map<Receta, Long> getEstadisticasPorEstacionYSemana(Mes mes, Semana semana, Temporada temporada) {
		return new HashMap<Receta, Long>();
	}

	public Map<Receta, Long> getEstadisticasPorRecetasMasCopiadas() {
		return new HashMap<Receta, Long>();
	}

	public List<Planificacion> getPlanificacionesTipoAlimento(TipoAlimento tipoAlimento) {
		return planificaciones
				.stream()
				.filter(p -> p.getReceta().contieneTipoAlimento(tipoAlimento))
				.collect(Collectors.toList());
	}

	private List<Receta> getRecetasDeGrupos() {
		return grupos
				.stream()
				.flatMap(grupo -> grupo.getRecetas().stream())
				.filter(receta -> !contieneIngredienteRestringido(receta))
				.collect(Collectors.toList());
	}

	private List<Receta> getRecetasDelRepositorio(List<Receta> recetas) {
		return recetas
				.stream()
				.filter(receta -> !contieneIngredienteRestringido(receta))
				.collect(Collectors.toList());
	}

	public List<Receta> getReporte() throws ExceptionGrupoInexistente {
		return reporte.getRecetasFiltradas();
	}

	public HashSet<Receta> listarTodasLasRecetas(List<Receta> recetas) {
		HashSet<Receta> todasRecetas = new HashSet<Receta>();

		todasRecetas.addAll(getRecetas());	
		todasRecetas.addAll(getRecetasDeGrupos());
		todasRecetas.addAll(getRecetasDelRepositorio(recetas));

		return todasRecetas;
	}

	public void modificarCalificacion(Grupo grupo, Receta receta, int calificacion) throws ExceptionValidacionDatos {
		grupo.modificarCalificacion(this, receta, calificacion);
	}

	public Receta modificarReceta(Receta receta, String nombre, List<Ingrediente> ingredientes, String procedimiento, Dificultad dificultad, Temporada temporadaRecetario, int calorias) throws CloneNotSupportedException, ExceptionValidacionDatos, ExceptionYaExiste, ExceptionIngredienteRestringido, ExceptionRecetaNoCompartidaEnGrupo {
		Receta recetaClon = (Receta) receta.clone();
		recetaClon.modificarReceta(nombre, ingredientes, procedimiento, dificultad, temporadaRecetario, calorias);
		eliminarReceta(receta);
		agregarReceta(recetaClon);
		for (Grupo grupo : getGrupos()) {
			if (grupo.contieneReceta(receta)) {
				grupo.modificaReceta(receta, recetaClon);
			}
		}
		return recetaClon;
	}

	public void unirAlGrupo(Grupo grupo) throws ExceptionYaExiste {
		if (contieneAlGrupo(grupo)) {
			throw new ExceptionYaExiste("Grupo." + grupo.getNombre());
		}
		grupo.agregarUsuario(this);
		grupos.add(grupo);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) throws ExceptionValidacionDatos {
		if (password.equals(null) || password.equals(CADENAVACIA)) {
			throw new ExceptionValidacionDatos("Ingrese un contrasena valida");
		}
		this.password = password;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) throws ExceptionValidacionDatos {
		if (nombre.equals(null) || nombre.equals(CADENAVACIA)) {
			throw new ExceptionValidacionDatos("El nombre no puede ser vacio");
		}
		this.nombre = nombre;
	}

	public int getEdad() {
		return edad;
	}

	public void setEdad(int edad) throws ExceptionValidacionDatos {
		if (edad < EDADMINIMA) {
			throw new ExceptionValidacionDatos("La edad tiene que ser mayor o igual a 18");
		}
		this.edad = edad;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) throws ExceptionValidacionDatos {
		if(!validateMail(mail)) {
			throw new ExceptionValidacionDatos("Ingrese un mail válido");
		}
		this.mail = mail;
	}

	public List<Grupo> getGrupos() {
		return grupos;
	}

	public List<Receta> getRecetas() {
		return recetas;
	}

	public List<Ingrediente> getPreferencias() {
		return preferencias;
	}

	public List<Restriccion> getRestricciones() {
		return restricciones;
	}

	public List<Planificacion> getPlanificaciones() {
		return planificaciones;
	}

	public PiramideAlimenticia getPiramide() {
		return piramide;
	}

	public void setPiramide(PiramideAlimenticia piramide) {
		this.piramide = piramide;
	}

	public String getAltura() {
		return altura;
	}

	public void setAltura(String altura) {
		this.altura = altura;
	}

	public Sexo getSexo() {
		return sexo;
	}

	public void setSexo(Sexo sexo) {
		this.sexo = sexo;
	}

	public int getRutina() {
		return rutina;
	}

	public void setRutina(Integer rutina) {
		this.rutina = rutina;
	}

	private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private static boolean validateMail(String email) {
		// Compiles the given regular expression into a pattern.
		Pattern pattern = Pattern.compile(PATTERN_EMAIL);

		// Match the given input against this pattern
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();

	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Usuario)) return false;
		Usuario u = (Usuario) o;
		return mail.equals(u.getMail());
	}

	@Override
	public int hashCode() {
		return (int) mail.hashCode();
	}

	@Override
	public String toString(){
		return this.getMail();
	}
}
