package ar.edu.utn.d2s;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.*;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ar.edu.utn.d2s.exceptions.ExceptionRecetaNoCompartidaEnGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioNoPertenceAlGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

@Entity
public class Grupo {

	@Transient
	public static final String NOMBRE = "nombre";
	@Id
	private String nombre;
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable
	(
			name = "GRUPO_USUARIO",
			joinColumns = @JoinColumn(name = "GRUPO_NOMBRE"),
			inverseJoinColumns = @JoinColumn(name = "USUARIO_EMAIL")
	)
	private List<Usuario> usuarios = new ArrayList<Usuario>();

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(targetEntity = ar.edu.utn.d2s.Receta.class, cascade = CascadeType.ALL)
	@JoinTable
	(
			name = "GRUPO_RECETA",
			joinColumns = @JoinColumn(name = "GRUPO_NOMBRE"),
			inverseJoinColumns = @JoinColumn(name = "RECETA_ID")
	)
	private List<Receta> recetas = new ArrayList<Receta>();

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "grupo")
	private List<Calificacion> calificaciones = new ArrayList<Calificacion>();

	protected Grupo() {
		super();
	}

	public Grupo(String nombre) {
		setNombre(nombre);
	}

	public void agregarCalificacion(Usuario usuario, Receta receta, int calificacion) throws ExceptionValidacionDatos {
		if (!contieneReceta(receta)) {
			throw new ExceptionValidacionDatos("El grupo no contiene la receta");
		}
		if (yaCalifico(usuario, receta)) {
			throw new ExceptionValidacionDatos("El usuario ya la calificó");
		}
		calificaciones.add(new Calificacion(usuario, receta, calificacion, this));
	}

	public void agregarReceta(Receta receta) throws ExceptionYaExiste {
		if(contieneReceta(receta)) {
			throw new ExceptionYaExiste("Receta." + receta.getNombre());
		}
		recetas.add(receta);
	}

	public void agregarUsuario(Usuario usuario) throws ExceptionYaExiste {
		if(contieneUsuario(usuario)) {
			throw new ExceptionYaExiste("Usuario." + usuario.getMail());
		}
		usuarios.add(usuario);
	}

	public boolean contieneReceta(Receta receta) {
		return recetas.contains(receta);
	}
	
	public boolean contieneUsuario(Usuario usuario) {
		return usuarios.contains(usuario);
	}

	public void eliminarCalificacion(Integer id) {
		calificaciones.removeIf(c -> c.getId() == id);
	}

	public void eliminarUsuario(Usuario usuario) throws ExceptionUsuarioNoPertenceAlGrupo {
		if(!contieneUsuario(usuario)) {
			throw new ExceptionUsuarioNoPertenceAlGrupo();
		}
		usuarios.removeIf(u -> u.equals(usuario));
		recetas.removeIf(r -> usuario.getRecetas().contains(r));
		calificaciones.removeIf(c -> c.getUsuario().equals(usuario));
	}

	public void eliminarReceta(Receta receta) throws ExceptionRecetaNoCompartidaEnGrupo {
		if (!contieneReceta(receta)) {
			throw new ExceptionRecetaNoCompartidaEnGrupo();
		}
		recetas.removeIf(r -> r.equals(receta));
	}
 
	public int getCalificacion(Usuario usuario, Receta receta) {
		return getCalificaciones()
				.stream()
				.filter(c -> c.getUsuario().equals(usuario) &&
						c.getReceta().equals(receta))
				.map(c -> c.getCalificacion())
				.reduce(0, (sum, c) -> sum + c);
	}
	
	public Calificacion getCalificacionObj(Usuario usuario, Receta receta) {
		Calificacion calificacion = null;
		if (yaCalifico(usuario, receta)) {
			calificacion = getCalificaciones()
					.stream()
					.filter(c -> c.getUsuario().equals(usuario) &&
							c.getReceta().equals(receta))
					.findFirst().get();
		}
		
		return calificacion;
	}

	public List<Integer> getListaCalificaciones(Usuario usuario, Receta receta) {
		List<Integer> lista = new ArrayList<Integer>();
		if (contieneUsuario(usuario)) {
			lista = getCalificaciones()
					.stream()
					.filter(c -> c.getReceta().equals(receta))
					.map(c -> c.getCalificacion())
					.collect(Collectors.toList());
		}
		return lista;
	}

	public Double getPromedio(Receta receta) {
		Double promedio = (double) 0;
		if (contieneReceta(receta)) {
			promedio += getCalificaciones()
					.stream()
					.filter(c -> c.getReceta().equals(receta))
					.mapToInt(c -> c.getCalificacion())
					.average()
					.getAsDouble();
		}
		return promedio;
	}

	public Map<Receta, Double> getRankingReceta() {
		Map<Receta, Double> mapRanking = recetas
				.stream()
				.filter(r -> getPromedio(r) > 0)
				.collect(Collectors.toMap(r -> r, r -> getPromedio(r)));
		return ordenarRecetaMayorAMenorD(mapRanking);
	}

	public void modificarCalificacion(Usuario usuario, Receta receta, int calificacion) throws ExceptionValidacionDatos {
		if (yaCalifico(usuario, receta)) {
			Calificacion cal = getCalificaciones()
					.stream()
					.filter(c -> c.getUsuario().equals(usuario) && c.getReceta().equals(receta))
					.findFirst().get();
			cal.setCalificacion(calificacion);
		}
	}

	public void modificaReceta(Receta recetaOriginal, Receta recetaModificada) throws ExceptionYaExiste, ExceptionRecetaNoCompartidaEnGrupo {
		eliminarReceta(recetaOriginal);
		agregarReceta(recetaModificada);
	}

	public static Map<Receta, Double> ordenarRecetaMayorAMenorD(Map<Receta, Double> mapDesordenado) {
		List<Map.Entry<Receta, Double>> list = new LinkedList<Map.Entry<Receta,Double>>(mapDesordenado.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<Receta, Double>>() {
			public int compare(Map.Entry<Receta, Double> m1, Map.Entry<Receta, Double> m2) {
				return Double.compare(m2.getValue(), m1.getValue());
			}
		});

		Map<Receta, Double> mapOrdenado = new LinkedHashMap<Receta, Double>();
		for (Iterator<Map.Entry<Receta, Double>> it = list.iterator(); it.hasNext();) {
			Map.Entry<Receta, Double> entry = it.next();
			mapOrdenado.put(entry.getKey(), entry.getValue());
		}

		return mapOrdenado;
	}

	private boolean yaCalifico(Usuario usuario, Receta receta) {
		return getCalificaciones()
				.stream()
				.anyMatch(c -> c.getUsuario().equals(usuario) &&
						c.getReceta().equals(receta));
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public List<Receta> getRecetas() {
		return recetas;
	}

	public List<Calificacion> getCalificaciones() {
		return calificaciones;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Grupo)) return false;
		Grupo g = (Grupo) o;
		return this.nombre.equals(g.getNombre());
	}

	@Override
	public int hashCode() {
		return (int) nombre.hashCode();
	}
}