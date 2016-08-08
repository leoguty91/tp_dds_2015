package ar.edu.utn.d2s;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import ar.edu.utn.d2s.exceptions.ExceptionRecetaInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

public class RepositorioRecetas {

	private static RepositorioRecetas repositorioRecetas;
	private List<Receta> recetas = new ArrayList<Receta>();

	public RepositorioRecetas() {}

	public  static RepositorioRecetas getInstance() {
		if (repositorioRecetas == null) {
			repositorioRecetas = new RepositorioRecetas();
		}
		return repositorioRecetas;
	}

	public void resetearRepositorio(){
		recetas.clear();
	}

	public void agregarReceta(Receta receta) throws ExceptionYaExiste {
		if(contieneReceta(receta)) {
			throw new ExceptionYaExiste("Receta." + receta.getNombre());
		}
		recetas.add(receta);
	}

	public Receta getReceta(String id) throws ExceptionRecetaInexistente {
		if (!recetas.stream().anyMatch(r -> r.getId() == Integer.parseInt(id))) {
			throw new ExceptionRecetaInexistente();
		}
		return this.recetas
				.stream()
				.filter(r -> r.getId() == Integer.parseInt(id))
				.findFirst().get();
	}

	public boolean contieneReceta(Receta receta) {
		return recetas.contains(receta);
	}

	public void eliminaReceta(Receta receta) {
		if(contieneReceta(receta)) {
			recetas.remove(receta);
		}
	}

	public List<Receta> getRecetas() {
		return recetas;
	}

	public Receta getReceta(Session session, int idReceta) throws ExceptionRecetaInexistente {
		Criteria recetaCriteria = session.createCriteria(Receta.class);
		recetaCriteria.add(Restrictions.eq("id", idReceta));
		if (recetaCriteria.list().size() == 0) {
			throw new ExceptionRecetaInexistente();
		}
		return (Receta) recetaCriteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Receta> getRecetas(Session session) {
		Criteria recetaCriteria = session.createCriteria(Receta.class);
		recetaCriteria.addOrder(Order.desc("id"));
		return (List<Receta>) recetaCriteria.list();
	}

	public Ingrediente getIngrediente(Session session, String idIngrediente) {
		Criteria ingredienteCriteria = session.createCriteria(Ingrediente.class);
		ingredienteCriteria.add(Restrictions.eq("nombre", idIngrediente));
		Ingrediente ingrediente = null;
		if (ingredienteCriteria.list().size() == 1) {
			ingrediente = (Ingrediente) ingredienteCriteria.uniqueResult();
		} else {
			ingrediente = new Ingrediente();
			ingrediente.setNombre(idIngrediente);
		}
		return ingrediente;
	}
}