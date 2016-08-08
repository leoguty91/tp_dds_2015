package ar.edu.utn.d2s;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import ar.edu.utn.d2s.exceptions.ExceptionCalificacionInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionGrupoInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

public class RepositorioGrupos {

	private static RepositorioGrupos repositorioGrupos;
	private List<Grupo> grupos = new ArrayList<Grupo>();

	public RepositorioGrupos() {}

	public  static RepositorioGrupos getInstance() {
		if (repositorioGrupos == null) {
			repositorioGrupos = new RepositorioGrupos();
		}
		return repositorioGrupos;
	}

	public void resetearRepositorio(){
		grupos.clear();
	}

	public void agregarGrupo(Grupo grupo) throws ExceptionYaExiste {
		if (contieneGrupo(grupo.getNombre())) {
			throw new ExceptionYaExiste(grupo.getNombre());
		}
		grupos.add(grupo);
	}
	
	private boolean contieneGrupo(String grupo) {
		return grupos
				.stream()
				.anyMatch(g -> g.getNombre().equals(grupo));
	}

	public Grupo getGrupo(String grupo) throws ExceptionGrupoInexistente {
		if (!contieneGrupo(grupo)) {
			throw new ExceptionGrupoInexistente();
		}
		return grupos
				.stream()
				.filter(g -> g.getNombre().equals(grupo))
				.findFirst().get();
	}

	public List<Grupo> getGrupos() {
		return this.grupos;
	}

	public List<Receta> getRecetas(String grupo) throws ExceptionGrupoInexistente {
		List<Receta> recetas = new ArrayList<Receta>();
		if (contieneGrupo(grupo)) {
			recetas = getGrupo(grupo).getRecetas();
		}
		return recetas;
	}

	public Grupo getGrupo(Session session, String nombre) throws ExceptionGrupoInexistente {
		Criteria grupoCriteria = session.createCriteria(Grupo.class);
		grupoCriteria.add(Restrictions.eq("nombre", nombre));
		if (grupoCriteria.list().size() == 0) {
			throw new ExceptionGrupoInexistente();
		}
		return (Grupo) grupoCriteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Grupo> getGrupos(Session session) {
		Criteria grupoCriteria = session.createCriteria(Grupo.class);
		return (List<Grupo>) grupoCriteria.list();
	}

	public Calificacion getCalificacion(Session session, Integer idCalificacion) throws ExceptionCalificacionInexistente {
		Criteria calificacionCriteria = session.createCriteria(Calificacion.class);
		calificacionCriteria.add(Restrictions.eq("id", idCalificacion));
		if (calificacionCriteria.list().size() == 0) {
			throw new ExceptionCalificacionInexistente();
		}
		return (Calificacion) calificacionCriteria.uniqueResult();
	}
}
